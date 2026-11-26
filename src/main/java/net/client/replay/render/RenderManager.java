package net.client.replay.render;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import net.client.replay.ReplayClient;
import net.client.replay.gui.ReplayProgressScreen;
import net.client.replay.session.CameraController;
import net.client.replay.util.ReplayPaths;
import net.client.util.AfterimageHandler;
import net.minecraft.class_1600;

public final class RenderManager {
   private final class_1600 client;
   private RenderJob job;
   private PreviewJob preview;

   public RenderManager(class_1600 client) {
      this.client = client;
   }

   public boolean isActive() {
      return this.job != null || this.preview != null;
   }

   public boolean isRendering() {
      return this.job != null;
   }

   public boolean isBackgroundRendering() {
      return this.job != null && this.job.isBackground();
   }

   public boolean start(File replay, double fps, int width, int height, String encoder) {
      return this.start(replay, fps, width, height, encoder, -1L, -1L);
   }

   public boolean start(File replay, double fps, int width, int height, String encoder, long trimStartNanos, long trimEndNanos) {
      return this.start(replay, fps, width, height, encoder, trimStartNanos, trimEndNanos, null);
   }

   public boolean start(File replay, double fps, int width, int height, String encoder, long trimStartNanos, long trimEndNanos, CameraController sourceCamera) {
      if (this.isActive()) {
         ReplayClient.chat("已有回放任务在运行，请先执行 .replay stop。");
         return false;
      } else if (this.client.field_3803 != null && this.preview != null) {
         ReplayClient.chat("A replay preview is already active. Use .replay stop first.");
         return false;
      } else {
         boolean background = this.client.field_3803 != null;
         if (this.client.field_3803 == null && this.preview != null) {
            ReplayClient.chat("A replay preview is already active. Use .replay stop first.");
            return false;
         } else {
            try {
               AfterimageHandler.clearAll();
               this.job = new RenderJob(this.client, replay, fps, width, height, encoder, trimStartNanos, trimEndNanos, sourceCamera, background);
               ReplayClient.chat(
                  background
                     ? "后台渲染已开始：继续游玩/录制，输入 .replay stop 可停止。"
                     : "Render queued: " + replay.getName() + ", " + width + "x" + height + ", " + fps + " fps, encoder " + encoder
               );
               if (!background) {
                  this.client.method_2928(new ReplayProgressScreen(this.job));
               }

               return true;
            } catch (Throwable var14) {
               ReplayClient.LOGGER.error("Could not start render", var14);
               ReplayClient.chat("Could not start render: " + var14.getMessage());
               this.job = null;
               return false;
            }
         }
      }
   }

   public boolean startLatest() {
      File file = latestReplay();
      if (file == null) {
         ReplayClient.chat("No .lcr replay found in " + ReplayPaths.replaysDir().getAbsolutePath());
         return false;
      } else {
         return this.start(
            file,
            ReplayClient.getConfig().defaultFps,
            ReplayClient.getConfig().defaultWidth,
            ReplayClient.getConfig().defaultHeight,
            ReplayClient.getConfig().encoder
         );
      }
   }

   public RenderJob getJob() {
      return this.job;
   }

   public PreviewJob getPreview() {
      return this.preview;
   }

   public boolean startPreview(File replay) {
      if (this.isActive()) {
         ReplayClient.chat("已有回放任务在运行，请先执行 .replay stop。");
         return false;
      } else if (this.client.field_3803 != null) {
         ReplayClient.chat("编辑/渲染只能在主菜单进行；游戏中只能录制。");
         return false;
      } else {
         try {
            AfterimageHandler.clearAll();
            this.preview = new PreviewJob(this.client, replay);
            return true;
         } catch (Throwable var3) {
            ReplayClient.LOGGER.error("Could not start preview", var3);
            ReplayClient.chat("Could not start preview: " + var3.getMessage());
            this.preview = null;
            return false;
         }
      }
   }

   public void renderTick() {
      if (this.job != null) {
         this.job.tick(ReplayClient.getConfig().renderBudgetMs);
      }
   }

   public void clientTick() {
      if (this.preview != null) {
         this.preview.tick();
      }
   }

   public void stop() {
      if (this.job != null) {
         this.job.cancel();
         this.job = null;
      } else if (this.preview != null) {
         this.preview.stop();
         this.preview = null;
      } else {
         ReplayClient.chat("No replay job is active.");
      }
   }

   public void onJobFinished(RenderJob finishedJob) {
      if (this.job == finishedJob) {
         this.job = null;
      }
   }

   public void onPreviewFinished(PreviewJob finishedPreview) {
      if (this.preview == finishedPreview) {
         this.preview = null;
      }
   }

   public static File latestReplay() {
      File[] files = ReplayPaths.replaysDir().listFiles(new FileFilter() {
         @Override
         public boolean accept(File file) {
            return file.isFile() && file.getName().endsWith(".lcr");
         }
      });
      if (files != null && files.length != 0) {
         Arrays.sort(files, new Comparator<File>() {
            public int compare(File a, File b) {
               return Long.compare(b.lastModified(), a.lastModified());
            }
         });
         return files[0];
      } else {
         return null;
      }
   }
}
