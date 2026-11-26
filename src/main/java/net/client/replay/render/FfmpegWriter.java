package net.client.replay.render;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.client.replay.ReplayClient;

public final class FfmpegWriter implements VideoWriter {
   private final Process process;
   private final OutputStream stdin;
   private final String description;
   private final Thread stderrThread;
   private final StringBuilder stderr = new StringBuilder();
   private final byte[] frameBuffer;
   private boolean closed;

   public FfmpegWriter(File output, int width, int height, double fps, String ffmpegBinary, String extraArgs) throws IOException {
      this(output, width, height, fps, ffmpegBinary, extraArgs, null, 0.0);
   }

   public FfmpegWriter(File output, int width, int height, double fps, String ffmpegBinary, String extraArgs, File audioFile) throws IOException {
      this(output, width, height, fps, ffmpegBinary, extraArgs, audioFile, 0.0);
   }

   public FfmpegWriter(File output, int width, int height, double fps, String ffmpegBinary, String extraArgs, File audioFile, double audioOffsetSeconds) throws IOException {
      List<String> command = new ArrayList<>();
      command.add(ffmpegBinary);
      command.add("-y");
      command.add("-hide_banner");
      command.add("-loglevel");
      command.add("warning");
      command.add("-f");
      command.add("rawvideo");
      command.add("-pix_fmt");
      command.add("rgba");
      command.add("-s");
      command.add(width + "x" + height);
      command.add("-r");
      command.add(String.format(Locale.US, "%.6f", fps));
      command.add("-i");
      command.add("-");
      boolean withAudio = audioFile != null && audioFile.isFile() && audioFile.length() > 44L;
      if (withAudio) {
         if (audioOffsetSeconds > 0.0) {
            command.add("-ss");
            command.add(String.format(Locale.US, "%.6f", audioOffsetSeconds));
         }

         command.add("-i");
         command.add(audioFile.getAbsolutePath());
      }

      command.add("-vf");
      command.add("vflip");
      command.add("-c:v");
      command.add("libx264");
      command.add("-preset");
      command.add("veryfast");
      command.add("-crf");
      command.add("18");
      command.add("-pix_fmt");
      command.add("yuv420p");
      if (withAudio) {
         command.add("-map");
         command.add("0:v:0");
         command.add("-map");
         command.add("1:a:0");
         command.add("-c:a");
         command.add("aac");
         command.add("-b:a");
         command.add("128k");
      } else {
         command.add("-an");
      }

      command.add("-movflags");
      command.add("+faststart");
      if (extraArgs != null && !extraArgs.trim().isEmpty()) {
         for (String arg : extraArgs.trim().split("\\s+")) {
            command.add(arg);
         }
      }

      command.add(output.getAbsolutePath());
      ProcessBuilder builder = new ProcessBuilder(command);
      builder.redirectErrorStream(false);
      File binaryFile = new File(ffmpegBinary);
      String parent = binaryFile.getParentFile() == null ? null : binaryFile.getParentFile().getAbsolutePath();
      if (parent != null) {
         String oldLibraryPath = builder.environment().get("LD_LIBRARY_PATH");
         if (oldLibraryPath == null || !oldLibraryPath.contains(parent)) {
            builder.environment().put("LD_LIBRARY_PATH", oldLibraryPath != null && !oldLibraryPath.isEmpty() ? parent + ":" + oldLibraryPath : parent);
         }

         String oldPath = builder.environment().get("PATH");
         if (oldPath == null || !oldPath.contains(parent)) {
            builder.environment().put("PATH", oldPath != null && !oldPath.isEmpty() ? parent + ":" + oldPath : parent);
         }
      }

      this.process = builder.start();
      this.stdin = this.process.getOutputStream();
      this.frameBuffer = new byte[width * height * 4];
      this.description = "ffmpeg " + width + "x" + height + " @ " + fps + " fps -> " + output.getAbsolutePath();
      this.stderrThread = new Thread(new Runnable() {
         @Override
         public void run() {
            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(FfmpegWriter.this.process.getErrorStream()))) {
               while ((line = reader.readLine()) != null) {
                  synchronized (FfmpegWriter.this.stderr) {
                     if (FfmpegWriter.this.stderr.length() > 8192) {
                        FfmpegWriter.this.stderr.setLength(0);
                     }

                     FfmpegWriter.this.stderr.append(line).append('\n');
                  }

                  if (ReplayClient.getConfig() != null && ReplayClient.getConfig().debug) {
                     ReplayClient.LOGGER.info("[ffmpeg] {}", line);
                  }
               }
            } catch (Throwable var8) {
            }
         }
      }, "ReplayClient-ffmpeg-stderr");
      this.stderrThread.setDaemon(true);
      this.stderrThread.start();

      try {
         Thread.sleep(220L);
      } catch (InterruptedException var19) {
         Thread.currentThread().interrupt();
      }

      if (!this.process.isAlive()) {
         try {
            Thread.sleep(80L);
         } catch (InterruptedException var18) {
            Thread.currentThread().interrupt();
         }

         throw new IOException("ffmpeg 启动后立即退出: " + this.stderrText() + "（检查 ffmpegPath 和编码器参数）");
      }
   }

   private String stderrText() {
      synchronized (this.stderr) {
         String value = this.stderr.toString().trim();
         return value.isEmpty() ? "没有 stderr 输出" : value;
      }
   }

   @Override
   public void writeFrame(ByteBuffer rgba, int width, int height) throws IOException {
      if (this.closed) {
         throw new IOException("Writer already closed");
      } else if (width * height * 4 != this.frameBuffer.length) {
         throw new IOException("Frame size changed");
      } else {
         rgba.rewind();
         rgba.get(this.frameBuffer);
         this.stdin.write(this.frameBuffer);
      }
   }

   @Override
   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;

         try {
            this.stdin.close();
         } catch (Throwable var7) {
         }

         try {
            int exit = this.process.waitFor();
            if (exit != 0) {
               String error;
               synchronized (this.stderr) {
                  error = this.stderr.toString();
               }

               throw new IOException("ffmpeg exited with " + exit + ": " + error);
            }
         } catch (InterruptedException var6) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for ffmpeg", var6);
         }
      }
   }

   @Override
   public String describe() {
      return this.description;
   }
}
