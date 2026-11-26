package net.client.replay.util;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import net.client.replay.ReplayClient;

public final class ReplayPaths {
   private ReplayPaths() {
   }

   public static File replaysDir() {
      return ensure(new File(ReplayClient.gameDir(), "replays"));
   }

   public static File exportsDir() {
      return ensure(new File(ReplayClient.gameDir(), "exports"));
   }

   public static File cacheDir() {
      return ensure(new File(replaysDir(), ".cache"));
   }

   public static File ensure(File dir) {
      if (!dir.isDirectory() && !dir.mkdirs()) {
         ReplayClient.LOGGER.warn("无法创建目录 {}", dir);
      }

      return dir;
   }

   public static String sanitizeName(String name) {
      if (name == null || name.trim().isEmpty()) {
         name = "replay";
      }

      return name.replaceAll("[^a-zA-Z0-9._-]", "_");
   }

   public static String timestampName() {
      return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.ROOT).format(new Date());
   }

   public static File[] listReplays() {
      File[] files = replaysDir().listFiles(new FileFilter() {
         @Override
         public boolean accept(File file) {
            return file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".lcr");
         }
      });
      if (files == null) {
         return new File[0];
      } else {
         Arrays.sort(files, new Comparator<File>() {
            public int compare(File a, File b) {
               return Long.compare(b.lastModified(), a.lastModified());
            }
         });
         return files;
      }
   }

   public static File latestReplay() {
      File[] files = listReplays();
      return files.length == 0 ? null : files[0];
   }

   public static File cameraFileFor(File replay) {
      if (replay == null) {
         return null;
      } else {
         String name = replay.getName();
         String base = name.toLowerCase(Locale.ROOT).endsWith(".lcr") ? name.substring(0, name.length() - 4) : name;
         return new File(replay.getParentFile(), base + ".camera.json");
      }
   }

   public static String formatSize(long bytes) {
      if (bytes < 1024L) {
         return bytes + " B";
      } else if (bytes < 1048576L) {
         return String.format(Locale.ROOT, "%.0f KB", bytes / 1024.0);
      } else {
         return bytes < 1073741824L ? String.format(Locale.ROOT, "%.1f MB", bytes / 1048576.0) : String.format(Locale.ROOT, "%.2f GB", bytes / 1.0737418E9F);
      }
   }

   public static String formatTime(long millis) {
      return new SimpleDateFormat("MM-dd HH:mm", Locale.ROOT).format(new Date(millis));
   }

   public static String formatDuration(double seconds) {
      if (seconds < 0.0 || Double.isNaN(seconds)) {
         seconds = 0.0;
      }

      int total = (int)Math.round(seconds);
      int m = total / 60;
      int s = total % 60;
      return String.format(Locale.ROOT, "%d:%02d", m, s);
   }
}
