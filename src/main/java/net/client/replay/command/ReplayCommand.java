package net.client.replay.command;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import net.client.replay.ReplayClient;
import net.client.replay.config.ReplayConfig;
import net.client.replay.gui.ReplayEditorScreen;
import net.client.replay.render.PreviewJob;
import net.client.replay.render.RenderManager;
import net.client.replay.util.ReplayPaths;
import net.minecraft.class_1600;

public final class ReplayCommand {
   private ReplayCommand() {
   }

   public static boolean handle(String message) {
      if (message == null) {
         return false;
      } else {
         String lower = message.trim();
         String prefix = null;
         if (lower.startsWith(".replay")) {
            prefix = ".replay";
         } else if (lower.startsWith("!replay")) {
            prefix = "!replay";
         }

         if (prefix == null) {
            return false;
         } else {
            String rest = lower.substring(prefix.length()).trim();
            if (rest.isEmpty()) {
               help();
               return true;
            } else {
               String[] args = rest.split("\\s+");
               String sub = args[0].toLowerCase();

               try {
                  if ("record".equals(sub)) {
                     recording(args);
                  } else if ("render".equals(sub)) {
                     render(args);
                  } else if ("play".equals(sub) || "preview".equals(sub)) {
                     play(args);
                  } else if ("stop".equals(sub) || "cancel".equals(sub)) {
                     ReplayClient.getRenderManager().stop();
                     ReplayClient.getRecordingManager().stop();
                  } else if ("status".equals(sub)) {
                     ReplayClient.getRecordingManager().status();
                     ReplayClient.chat("渲染任务：" + (ReplayClient.getRenderManager().isActive() ? "运行中" : "空闲"));
                  } else if ("list".equals(sub)) {
                     list();
                  } else if ("config".equals(sub)) {
                     config(args);
                  } else if ("gui".equals(sub)) {
                     ReplayClient.openConsole();
                  } else if ("help".equals(sub)) {
                     help();
                  } else {
                     ReplayClient.chat("未知子命令：" + sub);
                     help();
                  }
               } catch (Throwable var7) {
                  ReplayClient.LOGGER.error("Command failed", var7);
                  ReplayClient.chat("命令执行失败：" + var7.getMessage());
               }

               return true;
            }
         }
      }
   }

   private static void recording(String[] args) {
      String action = args.length > 1 ? args[1].toLowerCase() : "status";
      if ("start".equals(action)) {
         String name = args.length > 2 ? args[2] : "";
         ReplayClient.getRecordingManager().start(name);
      } else if ("stop".equals(action)) {
         ReplayClient.getRecordingManager().stop();
      } else if ("status".equals(action)) {
         ReplayClient.getRecordingManager().status();
      } else if (!"gameaudio".equals(action) && !"sound".equals(action)) {
         ReplayClient.chat("用法：.replay record start [名称] | stop | status | gameaudio on/off");
      } else {
         boolean on = args.length < 3 || !"off".equalsIgnoreCase(args[2]);
         ReplayClient.getConfig().recordGameAudio = on;
         ReplayClient.getConfig().save();
         ReplayClient.chat("游戏内录音：" + (on ? "开（从声音事件重建）" : "关"));
      }
   }

   private static void render(String[] args) {
      if (ReplayClient.getRenderManager().isActive()) {
         ReplayClient.chat("已有回放任务在运行，请先执行 .replay stop。");
      } else {
         double fps = ReplayClient.getConfig().defaultFps;
         int width = ReplayClient.getConfig().defaultWidth;
         int height = ReplayClient.getConfig().defaultHeight;
         String encoder = ReplayClient.getConfig().encoder;
         int index = 1;
         File replay;
         if (args.length > 1 && !isNumber(args[1]) && !"latest".equalsIgnoreCase(args[1])) {
            replay = resolveReplay(args[1]);
            index = 2;
         } else {
            if (args.length > 1 && "latest".equalsIgnoreCase(args[1])) {
               index = 2;
            }

            replay = RenderManager.latestReplay();
         }

         if (replay == null) {
            ReplayClient.chat("没有找到回放，先录制一个或用 .replay list 查看。");
         } else {
            if (index < args.length && isNumber(args[index])) {
               fps = Double.parseDouble(args[index++]);
            }

            if (index < args.length && isNumber(args[index])) {
               width = (int)Double.parseDouble(args[index++]);
            }

            if (index < args.length && isNumber(args[index])) {
               height = (int)Double.parseDouble(args[index++]);
            }

            if (index < args.length) {
               encoder = args[index++].toLowerCase();
            }

            if (width < 16 || height < 16) {
               ReplayClient.chat("分辨率太小。");
            } else if (!(fps <= 0.0) && !Double.isNaN(fps) && !Double.isInfinite(fps)) {
               if (width > 7680) {
                  width = 7680;
               }

               if (height > 4320) {
                  height = 4320;
               }

               if ((width & 1) != 0) {
                  width--;
               }

               if ((height & 1) != 0) {
                  height--;
               }

               ReplayClient.chat(
                  class_1600.method_2965().field_3803 == null
                     ? "Starting render in the main-menu progress screen."
                     : "Starting background render; you can keep playing and recording."
               );
               ReplayClient.getRenderManager().start(replay, fps, width, height, encoder);
            } else {
               ReplayClient.chat("帧率必须是大于 0 的有限数字（不设上限）。");
            }
         }
      }
   }

   private static void play(String[] args) {
      if (class_1600.method_2965().field_3803 != null) {
         ReplayClient.chat("编辑/预览只能在主菜单进行；游戏中只能录制。");
      } else if (ReplayClient.getRenderManager().isActive()) {
         ReplayClient.chat("已有回放任务在运行，请先执行 .replay stop。");
      } else {
         File replay = args.length > 1 ? resolveReplay(args[1]) : RenderManager.latestReplay();
         if (replay == null) {
            ReplayClient.chat("没有找到回放。");
         } else {
            ReplayClient.chat("正在启动回放预览，当前世界会被卸载。");
            if (ReplayClient.getRenderManager().startPreview(replay)) {
               PreviewJob preview = ReplayClient.getRenderManager().getPreview();
               if (preview != null) {
                  class_1600.method_2965().method_2928(new ReplayEditorScreen(preview));
               }
            }
         }
      }
   }

   private static File resolveReplay(String name) {
      if (name != null && !name.isEmpty()) {
         File direct = new File(name);
         if (direct.isFile()) {
            return direct;
         } else {
            if (!name.endsWith(".lcr")) {
               name = name + ".lcr";
            }

            File file = new File(ReplayPaths.replaysDir(), name);
            return file.isFile() ? file : null;
         }
      } else {
         return RenderManager.latestReplay();
      }
   }

   private static boolean isNumber(String value) {
      try {
         Double.parseDouble(value);
         return true;
      } catch (Throwable var2) {
         return false;
      }
   }

   private static void list() {
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
         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
         ReplayClient.chat("共 " + files.length + " 个回放：");

         for (File file : files) {
            ReplayClient.chat("  " + file.getName() + "  " + file.length() / 1024L + " KiB  " + format.format(new Date(file.lastModified())));
         }
      } else {
         ReplayClient.chat("回放目录为空：" + ReplayPaths.replaysDir().getAbsolutePath());
      }
   }

   private static void config(String[] args) {
      ReplayConfig config = ReplayClient.getConfig();
      if (args.length < 2) {
         ReplayClient.chat(
            "sampleHz="
               + config.sampleHz
               + " defaultFps="
               + config.defaultFps
               + " size="
               + config.defaultWidth
               + "x"
               + config.defaultHeight
               + " encoder="
               + config.encoder
               + " autoRecord="
               + config.autoRecord
               + " ffmpegPath="
               + (config.ffmpegPath.isEmpty() ? "(auto)" : config.ffmpegPath)
         );
      } else {
         String key = args[1];
         String value = args.length > 2 ? args[2] : "";
         if ("sampleHz".equals(key)) {
            config.sampleHz = Integer.parseInt(value);
         } else if ("defaultFps".equals(key)) {
            config.defaultFps = Double.parseDouble(value);
         } else if ("defaultWidth".equals(key)) {
            config.defaultWidth = Integer.parseInt(value);
         } else if ("defaultHeight".equals(key)) {
            config.defaultHeight = Integer.parseInt(value);
         } else if ("encoder".equals(key)) {
            config.encoder = value;
         } else if ("ffmpegPath".equals(key)) {
            config.ffmpegPath = value;
         } else if ("ffmpegArgs".equals(key)) {
            config.ffmpegArgs = value;
         } else if ("autoRecord".equals(key)) {
            config.autoRecord = Boolean.parseBoolean(value);
         } else if ("showHand".equals(key)) {
            config.showHand = Boolean.parseBoolean(value);
         } else if ("recordGameAudio".equals(key)) {
            config.recordGameAudio = Boolean.parseBoolean(value);
         } else if ("keepFrames".equals(key)) {
            config.keepFrames = Boolean.parseBoolean(value);
         } else if ("renderBudgetMs".equals(key)) {
            config.renderBudgetMs = Integer.parseInt(value);
         } else if ("skipStartSeconds".equals(key)) {
            config.skipStartSeconds = Double.parseDouble(value);
         } else if ("chunkBuildBudgetMs".equals(key)) {
            config.chunkBuildBudgetMs = Integer.parseInt(value);
         } else {
            if (!"cameraFile".equals(key)) {
               ReplayClient.chat("未知配置项：" + key);
               return;
            }

            config.cameraFile = value;
         }

         config.sanitize();
         config.save();
         ReplayClient.chat("已设置 " + key + " = " + value);
      }
   }

   private static void help() {
      ReplayClient.chat(".replay record start [name] | record stop | record status");
      ReplayClient.chat(".replay render [name] [fps] [width] [height] [ffmpeg|png|mjpeg]");
      ReplayClient.chat(".replay play [name]  |  .replay stop  |  .replay list");
      ReplayClient.chat(".replay gui  |  .replay config [key] [value]");
      ReplayClient.chat(
         "Hotkeys: "
            + ReplayClient.recordKeyName()
            + " start recording, "
            + ReplayClient.stopKeyName()
            + " stop recording, "
            + ReplayClient.renderKeyName()
            + " render latest."
      );
   }
}
