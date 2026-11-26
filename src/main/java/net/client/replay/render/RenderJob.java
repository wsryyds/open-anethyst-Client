package net.client.replay.render;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Locale;
import net.client.replay.ReplayClient;
import net.client.replay.record.GameAudioMixer;
import net.client.replay.session.CameraController;
import net.client.replay.session.CameraPath;
import net.client.replay.session.ReplayReader;
import net.client.replay.session.ReplaySession;
import net.client.replay.util.ReplayPaths;
import net.minecraft.class_1600;
import net.minecraft.class_1862;
import net.minecraft.class_629;

public final class RenderJob {
   private final class_1600 client;
   private final File replayFile;
   private final double fps;
   private final int width;
   private final int height;
   private final String encoder;
   private final CameraController sourceCamera;
   private final boolean background;
   private final int renderPerspective;
   private final long startTimeNanos;
   private final long endTimeNanos;
   private final long totalFrames;
   private final double durationSeconds;
   private ReplayReader reader;
   private ReplaySession session;
   private FrameCapture capture;
   private VideoWriter writer;
   private long frameIndex;
   private boolean finished;
   private boolean cancelled;
   private long lastProgressNanos;
   private static final int FRAME_WINDOW = 40;
   private final long[] frameTimes = new long[40];
   private int frameTimeIndex;
   private int frameTimeCount;
   private long lastFrameNanos;
   private long startedNanos;
   private long finishedNanos;
   private String outputDescription = "";
   private String lastError = "";
   private boolean disposed;

   public RenderJob(class_1600 client, File replayFile, double fps, int width, int height, String encoder) throws IOException {
      this(client, replayFile, fps, width, height, encoder, -1L, -1L, null);
   }

   public RenderJob(class_1600 client, File replayFile, double fps, int width, int height, String encoder, long trimStartNanos, long trimEndNanos) throws IOException {
      this(client, replayFile, fps, width, height, encoder, trimStartNanos, trimEndNanos, null);
   }

   public RenderJob(
      class_1600 client,
      File replayFile,
      double fps,
      int width,
      int height,
      String encoder,
      long trimStartNanos,
      long trimEndNanos,
      CameraController sourceCamera
   ) throws IOException {
      this(client, replayFile, fps, width, height, encoder, trimStartNanos, trimEndNanos, sourceCamera, false);
   }

   public RenderJob(
      class_1600 client,
      File replayFile,
      double fps,
      int width,
      int height,
      String encoder,
      long trimStartNanos,
      long trimEndNanos,
      CameraController sourceCamera,
      boolean background
   ) throws IOException {
      this.client = client;
      this.replayFile = replayFile;
      this.fps = fps;
      int capWidth = width;
      int capHeight = height;
      if (!class_629.method_7315()) {
         try {
            class_1862 main = client.method_6633();
            int ww = main == null ? 0 : main.field_7981;
            int wh = main == null ? 0 : main.field_7982;
            if (ww > 1 && wh > 1) {
               capWidth = Math.min(width, ww);
               capHeight = Math.min(height, wh);
            }
         } catch (Throwable var31) {
         }
      }

      this.width = Math.max(2, capWidth - (capWidth & 1));
      this.height = Math.max(2, capHeight - (capHeight & 1));
      this.encoder = encoder;
      this.sourceCamera = sourceCamera;
      this.background = background;
      this.renderPerspective = sourceCamera != null && sourceCamera.getMode() == 0 ? sourceCamera.getPerspective() : 0;
      this.reader = ReplayReader.open(replayFile);
      if (!this.reader.hasGameJoin()) {
         throw new IOException("这个 .lcr 没有 GameJoin 记录，无法载入回放世界；请用新版重新录制。");
      } else {
         long contentStart = this.reader.contentStartNanos();
         double skipSeconds = ReplayClient.getConfig() == null ? 1.0 : ReplayClient.getConfig().skipStartSeconds;
         if (skipSeconds < 0.0 || Double.isNaN(skipSeconds) || Double.isInfinite(skipSeconds)) {
            skipSeconds = 0.0;
         }

         long skipNanos = (long)(skipSeconds * 1.0E9);
         long endLimit = Math.max(this.reader.meta.durationNanos, this.reader.lastPacketTime());
         if (trimStartNanos < 0L || trimEndNanos <= 0L) {
            try {
               CameraPath saved = CameraPath.load(ReplayPaths.cameraFileFor(replayFile));
               if (saved != null) {
                  if (trimStartNanos < 0L && saved.trimStartSeconds > 0.0) {
                     trimStartNanos = (long)(saved.trimStartSeconds * 1.0E9);
                  }

                  if (trimEndNanos <= 0L && saved.trimEndSeconds > 0.0) {
                     trimEndNanos = (long)(saved.trimEndSeconds * 1.0E9);
                  }
               }
            } catch (Throwable var30) {
            }
         }

         long proposedStart = contentStart + skipNanos;
         if (proposedStart > endLimit - 50000000L) {
            proposedStart = Math.max(contentStart, endLimit - 50000000L);
         }

         if (trimStartNanos >= 0L && trimStartNanos < endLimit) {
            proposedStart = trimStartNanos;
         }

         long proposedEnd = endLimit;
         if (trimEndNanos > proposedStart && trimEndNanos <= endLimit) {
            proposedEnd = trimEndNanos;
         }

         this.startTimeNanos = Math.max(0L, proposedStart);
         this.endTimeNanos = Math.max(this.startTimeNanos + 50000000L, proposedEnd);
         long duration = Math.max(100000000L, this.endTimeNanos - this.startTimeNanos + 100000000L);
         this.durationSeconds = duration / 1.0E9;
         this.totalFrames = Math.max(1L, (long)Math.ceil(this.durationSeconds * fps) + 1L);
         ReplayClient.LOGGER
            .info(
               "[replay] range {}s -> {}s ({}, {} frames @ {} fps)",
               String.format(Locale.ROOT, "%.2f", this.startTimeNanos / 1.0E9),
               String.format(Locale.ROOT, "%.2f", this.endTimeNanos / 1.0E9),
               String.format(Locale.ROOT, "%.2f s", this.durationSeconds),
               this.totalFrames,
               String.format(Locale.ROOT, "%.1f", fps)
            );
      }
   }

   public boolean isFinished() {
      return this.finished || this.cancelled;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public long getFrameIndex() {
      return this.frameIndex;
   }

   public long getTotalFrames() {
      return this.totalFrames;
   }

   public int getPercent() {
      return this.finished && !this.cancelled && this.lastError.isEmpty() && this.frameIndex >= this.totalFrames
         ? 100
         : (int)Math.max(0L, Math.min(100L, this.frameIndex * 100L / this.totalFrames));
   }

   public long getElapsedMillis() {
      return this.startedNanos <= 0L ? 0L : (System.nanoTime() - this.startedNanos) / 1000000L;
   }

   public double getActualFps() {
      long elapsed = System.nanoTime() - this.startedNanos;
      return elapsed > 0L && this.startedNanos > 0L ? this.frameIndex / (elapsed / 1.0E9) : 0.0;
   }

   public double getRecentFps() {
      int n = Math.min(this.frameTimeCount, 40);
      if (n < 4) {
         return -1.0;
      } else {
         long sum = 0L;

         for (int i = 0; i < n; i++) {
            sum += this.frameTimes[(this.frameTimeIndex - 1 - i + 80) % 40];
         }

         return sum <= 0L ? -1.0 : n / (sum / 1.0E9);
      }
   }

   public double getEtaSeconds() {
      if (this.frameIndex >= 8L && this.getElapsedMillis() >= 3000L) {
         double overall = this.getActualFps();
         double recent = this.getRecentFps();
         double rate = overall;
         if (recent > 0.01) {
            rate = overall > 0.01 ? Math.min(overall, recent) : recent;
         }

         return rate <= 0.01 ? -1.0 : (this.totalFrames - this.frameIndex) / rate;
      } else {
         return -1.0;
      }
   }

   public String getOutputDescription() {
      return this.outputDescription;
   }

   public String getLastError() {
      return this.lastError;
   }

   public String getEncoder() {
      return this.encoder;
   }

   public boolean isBackground() {
      return this.background;
   }

   public long getStartTimeNanos() {
      return this.startTimeNanos;
   }

   public long getEndTimeNanos() {
      return this.endTimeNanos;
   }

   public double getFps() {
      return this.fps;
   }

   public File getReplayFile() {
      return this.replayFile;
   }

   public void tick(int budgetMillis) {
      if (!this.finished && !this.cancelled) {
         try {
            if (this.session == null) {
               this.initialize();
            }

            if (this.background && !this.session.isLiveStateCurrent()) {
               throw new IOException("后台渲染期间玩家已切换世界；任务已停止，避免破坏当前游戏。");
            }

            long budget = Math.max(1L, (long)budgetMillis) * 1000000L;
            long begin = System.nanoTime();
            if (this.background) {
               this.renderOneFrame();
               if (this.finished || this.cancelled) {
                  return;
               }
            } else {
               do {
                  this.renderOneFrame();
                  if (this.finished || this.cancelled) {
                     return;
                  }
               } while (System.nanoTime() - begin < budget);
            }

            this.reportProgress(false);
         } catch (Throwable var6) {
            this.fail(var6);
         }
      }
   }

   private void initialize() throws IOException {
      this.startedNanos = System.nanoTime();
      this.session = new ReplaySession(this.client, this.reader);
      if (this.background) {
         this.session.startBackground(this.startTimeNanos);
      } else {
         this.session.start(this.startTimeNanos, true);
      }

      if (this.sourceCamera != null && this.session.getCameraController() != null) {
         this.session.getCameraController().copyStateFrom(this.sourceCamera);
      }

      this.capture = new FrameCapture(this.client, this.width, this.height);
      if (this.capture.isWindowMode()) {
         ReplayClient.chat("当前环境没有 FBO，导出改为窗口捕获：" + this.width + "x" + this.height);
      }

      String base = stripExtension(this.replayFile.getName()) + "-" + formatFps(this.fps) + "fps-" + ReplayPaths.timestampName();
      File audio = this.reader.getAudioFile();
      if (audio == null && this.reader.hasSoundEvents()) {
         try {
            File mixed = new File(ReplayPaths.cacheDir(), stripExtension(this.replayFile.getName()) + "-gameaudio.wav");
            GameAudioMixer mixer = new GameAudioMixer(this.client);
            if (mixer.mix(this.reader.readSoundEvents(), this.reader.meta.durationNanos, mixed)) {
               audio = mixed;
               ReplayClient.chat("已从回放中的声音事件重建游戏内音轨。");
            }
         } catch (Throwable var8) {
            ReplayClient.LOGGER.warn("Could not rebuild game audio for export", var8);
         }
      }

      List<String> binaries = FfmpegLocator.findAllCached();
      ReplayClient.LOGGER.info("[replay] ffmpeg candidates: {}", binaries);
      if ("mjpeg".equals(this.encoder) || "avi".equals(this.encoder)) {
         File output = new File(ReplayPaths.exportsDir(), base + ".avi");
         this.writer = new MjpegAviWriter(output, this.width, this.height, this.fps, (int)this.totalFrames, audio, this.startTimeNanos / 1.0E9);
         this.outputDescription = output.getAbsolutePath();
         ReplayClient.chat("MJPEG AVI -> " + output.getName() + "（纯 Java，无需 ffmpeg）");
      } else if ("ffmpeg".equals(this.encoder)) {
         File output = new File(ReplayPaths.exportsDir(), base + ".mp4");

         for (String binary : binaries) {
            try {
               double var10010 = this.startTimeNanos;
               this.writer = new FfmpegWriter(output, this.width, this.height, this.fps, binary, ReplayClient.getConfig().ffmpegArgs, audio, var10010 / 1.0E9);
               this.outputDescription = output.getAbsolutePath();
               ReplayClient.chat("Rendering " + this.totalFrames + " frames at " + formatFps(this.fps) + " fps -> " + output.getName());
               break;
            } catch (Throwable var9) {
               ReplayClient.LOGGER.warn("ffmpeg candidate failed: {}", binary, var9);
               this.writer = null;
            }
         }

         if (this.writer == null) {
            File aviFallback = new File(ReplayPaths.exportsDir(), base + ".avi");
            this.writer = new MjpegAviWriter(aviFallback, this.width, this.height, this.fps, (int)this.totalFrames, audio, this.startTimeNanos / 1.0E9);
            this.outputDescription = aviFallback.getAbsolutePath();
            ReplayClient.chat("未找到可用的 ffmpeg，改为纯 Java MJPEG AVI 输出：" + aviFallback.getName());
            ReplayClient.LOGGER.info("[replay] ffmpeg unavailable, falling back to MJPEG AVI");
         }
      } else {
         File dir = new File(ReplayPaths.exportsDir(), base + "-frames");
         this.writer = new PngSequenceWriter(
            dir,
            this.fps,
            audio,
            new File(ReplayPaths.exportsDir(), base + ".mp4"),
            binaries.isEmpty() ? null : binaries.get(0),
            ReplayClient.getConfig().keepFrames
         );
         this.outputDescription = dir.getAbsolutePath();
         ReplayClient.chat("Rendering " + this.totalFrames + " frames at " + formatFps(this.fps) + " fps -> " + base + ".mp4 (PNG frames are temporary)");
      }

      ReplayClient.chat(
         "Camera: "
            + (this.session.getCameraController() != null && this.session.getCameraController().hasPath() ? "keyframe track" : "player view")
            + " | "
            + this.width
            + "x"
            + this.height
      );
   }

   private void renderOneFrame() throws IOException {
      long frameTime = this.startTimeNanos + (long)(this.frameIndex * 1.0E9 / this.fps);
      boolean reachedEnd = false;
      boolean bg = this.background && this.session.isBackground();
      if (bg) {
         this.session.beginBackgroundFrame();
      }

      int oldPerspective = this.client.field_3823.field_949;

      try {
         int framePerspective = this.renderPerspective;
         CameraController camera = this.session.getCameraController();
         if (camera != null) {
            framePerspective = camera.getPerspectiveAt(frameTime / 1.0E9);
         }

         this.client.field_3823.field_949 = framePerspective;
         this.session.advanceTo(frameTime);
         long chunkBudget = ReplayClient.getConfig() == null ? 2000000L : ReplayClient.getConfig().chunkBuildBudgetMs * 1000000L;
         long chunkDeadline = System.nanoTime() + (this.frameIndex < 30L ? Math.max(chunkBudget, 30000000L) : chunkBudget);
         this.capture.capture(1.0F, chunkDeadline);
         ByteBuffer buffer = this.capture.getBuffer();
         this.writer.writeFrame(buffer, this.width, this.height);
         this.frameIndex++;
         long frameNow = System.nanoTime();
         if (this.lastFrameNanos > 0L) {
            this.frameTimes[this.frameTimeIndex] = frameNow - this.lastFrameNanos;
            this.frameTimeIndex = (this.frameTimeIndex + 1) % 40;
            if (this.frameTimeCount < 40) {
               this.frameTimeCount++;
            }
         }

         this.lastFrameNanos = frameNow;
         reachedEnd = this.frameIndex >= this.totalFrames;
      } finally {
         this.client.field_3823.field_949 = oldPerspective;
         if (bg) {
            this.session.endBackgroundFrame();
         }
      }

      if (reachedEnd) {
         this.finish();
      }
   }

   private void reportProgress(boolean force) {
      long now = System.nanoTime();
      if (force || now - this.lastProgressNanos >= 5000000000L) {
         this.lastProgressNanos = now;
         double percent = this.frameIndex * 100.0 / this.totalFrames;
         long elapsed = now - this.startedNanos;
         double fpsActual = elapsed <= 0L ? 0.0 : this.frameIndex / (elapsed / 1.0E9);
         ReplayClient.chat(String.format(Locale.US, "Render %.1f%% (%d/%d), %.1f frames/s", percent, this.frameIndex, this.totalFrames, fpsActual));
      }
   }

   private void finish() throws IOException {
      this.finished = true;
      this.finishedNanos = System.nanoTime();
      if (this.writer != null) {
         if (this.writer instanceof PngSequenceWriter) {
            ReplayClient.chat("PNG 帧已写完，正在调用 ffmpeg 合成 MP4（可能需要几十秒）...");
         }

         this.writer.close();
         String description = this.writer.describe();
         if (description != null && !description.isEmpty()) {
            this.outputDescription = description;
         }

         ReplayClient.chat("Render finished: " + this.outputDescription);
      }

      this.closeCapture();
      if (ReplayClient.getRenderManager() != null) {
         ReplayClient.getRenderManager().onJobFinished(this);
      }

      if (this.background) {
         this.dispose();
      }
   }

   public void cancel() {
      if (!this.finished && !this.cancelled) {
         this.cancelled = true;
         this.finishedNanos = System.nanoTime();

         try {
            if (this.writer != null) {
               this.writer.close();
            }

            if (this.writer != null) {
               this.outputDescription = this.writer.describe();
            }
         } catch (Throwable var2) {
            ReplayClient.LOGGER.warn("Could not finalize partial export", var2);
         }

         this.closeCapture();
         String kept = this.outputDescription != null && !this.outputDescription.isEmpty() ? this.outputDescription : "（没有开始写文件）";
         ReplayClient.chat("Render cancelled at frame " + this.frameIndex + "/" + this.totalFrames + "; kept: " + kept);
         if (ReplayClient.getRenderManager() != null) {
            ReplayClient.getRenderManager().onJobFinished(this);
         }

         if (this.background) {
            this.dispose();
         }
      }
   }

   private void fail(Throwable t) {
      this.finished = true;
      this.finishedNanos = System.nanoTime();
      this.lastError = t.getClass().getSimpleName() + ": " + t.getMessage();
      ReplayClient.LOGGER.error("Render failed", t);
      ReplayClient.chat("Render failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());

      try {
         if (this.writer != null) {
            this.writer.close();
         }
      } catch (Throwable var3) {
      }

      this.closeCapture();
      if (ReplayClient.getRenderManager() != null) {
         ReplayClient.getRenderManager().onJobFinished(this);
      }

      if (this.background) {
         this.dispose();
      }
   }

   public void dispose() {
      if (!this.disposed) {
         this.disposed = true;
         this.closeCapture();

         try {
            if (this.session != null) {
               this.session.close();
            }
         } catch (Throwable var3) {
         }

         this.session = null;

         try {
            if (this.reader != null) {
               this.reader.close();
            }
         } catch (Throwable var2) {
         }

         this.reader = null;
         this.writer = null;
      }
   }

   private void closeCapture() {
      try {
         if (this.capture != null) {
            this.capture.close();
         }
      } catch (Throwable var2) {
      }

      this.capture = null;
   }

   public long getFinishedNanos() {
      return this.finishedNanos;
   }

   private static String stripExtension(String name) {
      int dot = name.lastIndexOf(46);
      return dot > 0 ? name.substring(0, dot) : name;
   }

   private static String formatFps(double fps) {
      return Math.abs(fps - Math.round(fps)) < 1.0E-4 ? Long.toString(Math.round(fps)) : String.format(Locale.US, "%.2f", fps);
   }
}
