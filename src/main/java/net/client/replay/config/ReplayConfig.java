package net.client.replay.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import net.client.replay.ReplayClient;

public class ReplayConfig {
   public int sampleHz = 60;
   public double defaultFps = 60.0;
   public int defaultWidth = 1280;
   public int defaultHeight = 720;
   public String encoder = "ffmpeg";
   public String ffmpegPath = "";
   public String ffmpegArgs = "";
   public int renderBudgetMs = 25;
   public double skipStartSeconds = 1.0;
   public int chunkBuildBudgetMs = 2;
   public boolean showHand = true;
   public boolean showHud = true;
   public boolean recordGameAudio = true;
   public boolean keepFrames = false;
   public boolean autoRecord = false;
   public int hotkeyRecord = 65;
   public int hotkeyRender = 67;
   public int hotkeyStop = 66;
   public int hotkeyGui = -1;
   public String cameraFile = "";
   public boolean debug = false;
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static File file;

   public static synchronized ReplayConfig load() {
      File dir = new File(ReplayClient.gameDir(), "config");
      file = new File(dir, "client-replay.json");
      ReplayConfig config = new ReplayConfig();
      if (file.isFile()) {
         try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            ReplayConfig loaded = GSON.fromJson(reader, ReplayConfig.class);
            if (loaded != null) {
               config = loaded;
            }
         } catch (Throwable var7) {
            ReplayClient.LOGGER.warn("无法读取回放配置，使用默认值", var7);
            config = new ReplayConfig();
         }
      }

      config.sanitize();
      config.save();
      return config;
   }

   public void sanitize() {
      this.sampleHz = clamp(this.sampleHz, 0, 240);
      if (!finite(this.defaultFps) || this.defaultFps <= 0.0) {
         this.defaultFps = 60.0;
      }

      this.defaultFps = Math.min(1000.0, this.defaultFps);
      this.defaultWidth = even(clamp(this.defaultWidth, 64, 7680));
      this.defaultHeight = even(clamp(this.defaultHeight, 64, 4320));
      this.encoder = normalizeEncoder(this.encoder);
      if (this.ffmpegPath == null) {
         this.ffmpegPath = "";
      }

      if (this.ffmpegArgs == null) {
         this.ffmpegArgs = "";
      }

      this.renderBudgetMs = clamp(this.renderBudgetMs, 1, 200);
      if (!finite(this.skipStartSeconds) || this.skipStartSeconds < 0.0) {
         this.skipStartSeconds = 0.0;
      }

      this.skipStartSeconds = Math.min(30.0, this.skipStartSeconds);
      this.chunkBuildBudgetMs = clamp(this.chunkBuildBudgetMs, 0, 100);
      if (this.hotkeyGui < -1) {
         this.hotkeyGui = -1;
      }

      this.hotkeyRecord = validKey(this.hotkeyRecord, 65);
      this.hotkeyRender = validKey(this.hotkeyRender, 67);
      this.hotkeyStop = validKey(this.hotkeyStop, 66);
      if (this.cameraFile == null) {
         this.cameraFile = "";
      }
   }

   public synchronized void save() {
      if (file != null) {
         try {
            File parent = file.getParentFile();
            if (parent != null && !parent.isDirectory()) {
               parent.mkdirs();
            }

            File tmp = new File(file.getPath() + ".tmp");

            try (Writer writer = new OutputStreamWriter(Files.newOutputStream(tmp.toPath()), StandardCharsets.UTF_8)) {
               GSON.toJson(this, writer);
            }

            try {
               Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException var7) {
               Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
         } catch (Throwable var9) {
            ReplayClient.LOGGER.warn("无法写入回放配置", var9);
         }
      }
   }

   public static String normalizeEncoder(String value) {
      String v = value == null ? "ffmpeg" : value.trim().toLowerCase(Locale.ROOT);
      if ("avi".equals(v) || "mjpeg-avi".equals(v)) {
         v = "mjpeg";
      }

      if (!"ffmpeg".equals(v) && !"png".equals(v) && !"mjpeg".equals(v)) {
         v = "ffmpeg";
      }

      return v;
   }

   public static String nextEncoder(String current) {
      if ("ffmpeg".equals(current)) {
         return "png";
      } else {
         return "png".equals(current) ? "mjpeg" : "ffmpeg";
      }
   }

   private static boolean finite(double v) {
      return !Double.isNaN(v) && !Double.isInfinite(v);
   }

   private static int clamp(int v, int lo, int hi) {
      return v < lo ? lo : (v > hi ? hi : v);
   }

   private static int even(int v) {
      return (v & 1) != 0 ? v - 1 : v;
   }

   private static int validKey(int key, int fallback) {
      return key >= 0 && key <= 255 ? key : fallback;
   }
}
