package net.client.replay;

import java.io.File;
import java.util.concurrent.Callable;
import net.client.replay.config.ReplayConfig;
import net.client.replay.gui.ReplayEditorScreen;
import net.client.replay.gui.ReplayScreen;
import net.client.replay.record.RecordingManager;
import net.client.replay.render.PreviewJob;
import net.client.replay.render.RenderJob;
import net.client.replay.render.RenderManager;
import net.client.replay.session.CameraController;
import net.client.replay.util.ReplayPaths;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_1600;
import net.minecraft.class_1982;
import net.minecraft.class_1989;
import net.minecraft.class_388;
import net.minecraft.class_624;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class ReplayClient implements ClientModInitializer {
   public static final String MOD_ID = "client";
   public static final Logger LOGGER = LogManager.getLogger("Client/Replay");
   private static ReplayConfig config;
   private static RecordingManager recordingManager;
   private static RenderManager renderManager;
   private static ReplayClient instance;
   private boolean lastRecordKey;
   private boolean lastRenderKey;
   private boolean lastStopKey;
   private boolean lastGuiKey;
   private static boolean hotkeysEnabled = true;
   private static volatile boolean replayCaptureActive;
   public static volatile long soundSeen;
   public static volatile long soundRecorded;
   private static boolean autoPlayTried;

   @Override
   public void onInitializeClient() {
      instance = this;
      config = ReplayConfig.load();
      class_1600 client = class_1600.method_2965();
      recordingManager = new RecordingManager(client);
      renderManager = new RenderManager(client);
      LOGGER.info("回放子系统已加载：replays={} exports={}", ReplayPaths.replaysDir().getAbsolutePath(), ReplayPaths.exportsDir().getAbsolutePath());
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               if (ReplayClient.recordingManager != null) {
                  ReplayClient.recordingManager.shutdown();
               }
            } catch (Throwable var2) {
            }
         }
      }, "Client-replay-save"));
   }

   public static ReplayConfig getConfig() {
      return config;
   }

   public static RecordingManager getRecordingManager() {
      return recordingManager;
   }

   public static RenderManager getRenderManager() {
      return renderManager;
   }

   public static boolean isReady() {
      return config != null && recordingManager != null && renderManager != null;
   }

   public static File gameDir() {
      class_1600 client = class_1600.method_2965();
      return client == null ? new File(".") : client.field_3762;
   }

   public static String keyName(int code) {
      if (code <= 0) {
         return "未绑定";
      } else {
         try {
            String name = Keyboard.getKeyName(code);
            return name != null && !name.isEmpty() ? name : "键码 " + code;
         } catch (Throwable var2) {
            return "键码 " + code;
         }
      }
   }

   public static String recordKeyName() {
      return keyName(getConfig().hotkeyRecord);
   }

   public static String stopKeyName() {
      return keyName(getConfig().hotkeyStop);
   }

   public static String renderKeyName() {
      return keyName(getConfig().hotkeyRender);
   }

   public static boolean isReplayActive() {
      return renderManager != null && renderManager.isActive() ? renderManager.getPreview() != null || !renderManager.isBackgroundRendering() : false;
   }

   public static void noteSoundSeen() {
      soundSeen++;
   }

   public static void noteSoundRecorded() {
      soundRecorded++;
   }

   public static void setReplayCaptureActive(boolean active) {
      replayCaptureActive = active;
   }

   public static boolean isReplayCaptureActive() {
      return replayCaptureActive;
   }

   public static boolean canEditReplay() {
      class_1600 client = class_1600.method_2965();
      return client == null ? false : client.field_3803 == null || renderManager != null && renderManager.getPreview() != null;
   }

   public static void openConsole() {
      class_1600 client = class_1600.method_2965();
      if (client != null) {
         if (!canEditReplay()) {
            chat("只能在主菜单编辑/渲染回放；游戏中只能录制。");
         } else {
            try {
               if (renderManager != null && renderManager.getPreview() != null) {
                  client.method_2928(new ReplayEditorScreen(renderManager.getPreview()));
               } else {
                  client.method_2928(new ReplayScreen());
               }
            } catch (Throwable var2) {
               LOGGER.warn("无法打开回放界面", var2);
               chat("打开回放界面失败：" + var2.getMessage());
            }
         }
      }
   }

   public static void openGui() {
      openConsole();
   }

   public static void chat(String message) {
      if (message != null && !message.isEmpty()) {
         class_1600 client = class_1600.method_2965();
         if (client != null) {
            if (!client.method_6640()) {
               client.method_6637(new Callable<Void>() {
                  public Void call() {
                     ReplayClient.chatNow(message);
                     return null;
                  }
               });
            } else {
               chatNow(message);
            }
         }
      }
   }

   private static void chatNow(String message) {
      LOGGER.info("[replay] {}", message);
      class_1600 client = class_1600.method_2965();
      if (client != null) {
         class_1982 text = new class_1989("[回放] " + message);

         try {
            if (client.field_10310 != null) {
               client.field_10310.method_8428(text);
            } else if (client.field_3820 != null) {
               client.field_3820.method_983().method_6690(text);
            }
         } catch (Throwable var4) {
         }
      }
   }

   private static void maybeAutoPlay() {
      if (!autoPlayTried) {
         String path = System.getProperty("client.replay.autoPlay");
         if (path != null && !path.isEmpty()) {
            class_1600 client = class_1600.method_2965();
            if (client != null && client.field_3803 == null) {
               if (client.field_3816 == null || client.field_3816 instanceof class_624) {
                  autoPlayTried = true;
                  File file = new File(path);
                  System.out.println("[AutoPlay] opening " + file.getAbsolutePath() + " exists=" + file.exists());
                  openPreview(file);
               }
            }
         }
      }
   }

   public static void onClientTick() {
      maybeAutoPlay();
      if (recordingManager != null) {
         recordingManager.clientTick();
      }

      if (renderManager != null) {
         renderManager.clientTick();
      }

      if (instance != null && hotkeysEnabled) {
         instance.pollHotkeys();
      }
   }

   public static void onFrameEnd(float tickDelta) {
      if (recordingManager != null) {
         recordingManager.sample(System.nanoTime(), tickDelta);
      }

      if (renderManager != null) {
         renderManager.renderTick();
      }
   }

   private void pollHotkeys() {
      if (config != null) {
         class_1600 client = class_1600.method_2965();
         if (client != null) {
            boolean record = isDown(config.hotkeyRecord);
            boolean render = isDown(config.hotkeyRender);
            boolean stop = isDown(config.hotkeyStop);
            boolean gui = isDown(config.hotkeyGui);
            class_388 screen = client.field_3816;
            if (screen != null) {
               if (gui && !this.lastGuiKey && client.field_3803 == null) {
                  openConsole();
               }

               this.lastGuiKey = gui;
               this.lastRecordKey = record;
               this.lastRenderKey = render;
               this.lastStopKey = stop;
            } else {
               if (gui && !this.lastGuiKey && canEditReplay()) {
                  openConsole();
               }

               this.lastGuiKey = gui;
               if (record && !this.lastRecordKey) {
                  if (recordingManager.isActive()) {
                     chat("已经在录制，按 " + stopKeyName() + " 停止。");
                  } else if (client.field_3803 != null && client.field_10310 != null) {
                     recordingManager.start("");
                  } else {
                     chat("进入世界后才能开始录制；主菜单只用于编辑/渲染回放。");
                  }
               }

               if (stop && !this.lastStopKey) {
                  if (recordingManager.isActive()) {
                     recordingManager.stop();
                  } else if (renderManager.isActive()) {
                     renderManager.stop();
                     chat("已停止回放 / 渲染任务。");
                  } else {
                     chat("当前没有录制或渲染任务。");
                  }
               }

               if (render && !this.lastRenderKey) {
                  if (renderManager.isActive()) {
                     chat("已有回放 / 渲染任务在运行，按 " + stopKeyName() + " 停止。");
                  } else {
                     renderManager.startLatest();
                  }
               }

               this.lastRecordKey = record;
               this.lastRenderKey = render;
               this.lastStopKey = stop;
            }
         }
      }
   }

   private static boolean isDown(int key) {
      if (key < 0) {
         return false;
      } else {
         try {
            return Keyboard.isKeyDown(key);
         } catch (Throwable var2) {
            hotkeysEnabled = false;
            return false;
         }
      }
   }

   public static RenderJob startRender(File replay, double fps, int width, int height, String encoder, long startNanos, long endNanos, CameraController camera) {
      if (renderManager == null || replay == null) {
         return null;
      } else {
         return !renderManager.start(replay, fps, width, height, encoder, startNanos, endNanos, camera) ? null : renderManager.getJob();
      }
   }

   public static PreviewJob openPreview(File replay) {
      if (renderManager == null || replay == null) {
         return null;
      } else if (!renderManager.startPreview(replay)) {
         return null;
      } else {
         class_1600 client = class_1600.method_2965();
         PreviewJob preview = renderManager.getPreview();
         if (client != null && preview != null) {
            client.method_2928(new ReplayEditorScreen(preview));
         }

         return preview;
      }
   }
}
