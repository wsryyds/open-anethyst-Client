package net.client.replay.render;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.client.replay.ReplayClient;

public final class FfmpegLocator {
   private static final String[] NAMES = new String[]{"ffmpeg", "ffmpeg.bin", "libffmpeg.so", "ffmpeg-arm64"};
   private static final int MAX_VISITS = 6000;
   private static List<String> cached;
   private static long cachedNanos;

   private FfmpegLocator() {
   }

   public static String find() {
      List<String> candidates = findAllCached();
      return candidates.isEmpty() ? null : candidates.get(0);
   }

   public static synchronized List<String> findAllCached() {
      long now = System.nanoTime();
      if (cached != null && now - cachedNanos < 30000000000L) {
         return cached;
      } else {
         cached = findAll();
         cachedNanos = now;
         return cached;
      }
   }

   public static List<String> findAll() {
      List<String> candidates = new ArrayList<>();
      String configured = ReplayClient.getConfig() == null ? "" : ReplayClient.getConfig().ffmpegPath;
      addCandidate(candidates, configured == null ? "" : configured.trim());
      addEnv(candidates, "POJAV_FFMPEG_PATH");
      addEnv(candidates, "FFMPEG_PATH");
      addEnv(candidates, "FFMPEG_BIN");
      String nativeDir = System.getenv("POJAV_NATIVEDIR");
      addCandidate(candidates, nativeDir == null ? null : nativeDir + "/ffmpeg");
      addCandidate(candidates, nativeDir == null ? null : nativeDir + "/ffmpeg.bin");
      addCandidate(candidates, nativeDir == null ? null : nativeDir + "/libffmpeg.so");
      String ldPath = System.getenv("LD_LIBRARY_PATH");
      if (ldPath != null) {
         for (String dir : ldPath.split(File.pathSeparator)) {
            addCandidate(candidates, dir + "/libffmpeg.so");
            addCandidate(candidates, dir + "/ffmpeg");
         }
      }

      addCandidate(candidates, "/data/data/net.kdt.pojavlaunch.ffmpeg/files/ffmpeg");
      addCandidate(candidates, "/data/user/0/net.kdt.pojavlaunch.ffmpeg/files/ffmpeg");
      addCandidate(candidates, "/data/data/net.kdt.pojavlaunch/files/ffmpeg");
      addCandidate(candidates, "/data/user/0/net.kdt.pojavlaunch/files/ffmpeg");
      addCandidate(candidates, "/system/bin/ffmpeg");
      addCandidate(candidates, "/sbin/ffmpeg");
      addCandidate(candidates, "/system/xbin/ffmpeg");
      scan(candidates, new File("/data/app"), 4);
      String launcher = System.getenv("HOME");
      if (launcher != null) {
         scan(candidates, new File(launcher, "files"), 4);
         scan(candidates, new File(launcher, "components"), 4);
      }

      scan(candidates, new File("/storage/emulated/0/Android/data"), 4);
      scan(candidates, new File("/data/local/tmp"), 3);
      if (isOnPath("ffmpeg")) {
         addCandidate(candidates, "ffmpeg");
      }

      if (isOnPath("ffmpeg.exe")) {
         addCandidate(candidates, "ffmpeg.exe");
      }

      return candidates;
   }

   private static void scan(List<String> out, File root, int maxDepth) {
      if (root != null && root.isDirectory()) {
         ArrayDeque<Object[]> queue = new ArrayDeque<>();
         queue.add(new Object[]{root, 0});
         int visited = 0;

         while (!queue.isEmpty() && visited < 6000) {
            Object[] item = queue.poll();
            File dir = (File)item[0];
            int depth = (Integer)item[1];
            visited++;
            String lower = dir.getName().toLowerCase(Locale.ROOT);
            boolean promising = depth <= 1
               || lower.contains("ffmpeg")
               || lower.contains("mio")
               || lower.contains("pojav")
               || lower.contains("zalith")
               || lower.contains("lib");

            for (String name : NAMES) {
               addCandidate(out, new File(dir, name).getAbsolutePath());
            }

            if (depth < maxDepth && promising) {
               File[] children = dir.listFiles();
               if (children != null) {
                  for (File child : children) {
                     if (child.isDirectory()) {
                        queue.add(new Object[]{child, depth + 1});
                     }
                  }
               }
            }
         }
      }
   }

   private static void addCandidate(List<String> list, String candidate) {
      if (candidate != null && !candidate.isEmpty()) {
         if (!list.contains(candidate)) {
            File file = new File(candidate);
            if (file.isFile() && file.canExecute()) {
               for (String existing : list) {
                  if (existing.equals(file.getAbsolutePath())) {
                     return;
                  }
               }

               list.add(file.getAbsolutePath());
            } else if (isOnPath(candidate)) {
               for (String existingx : list) {
                  if (existingx.equals(candidate)) {
                     return;
                  }
               }

               list.add(candidate);
            }
         }
      }
   }

   private static void addEnv(List<String> list, String name) {
      String value = System.getenv(name);
      if (value != null && !value.trim().isEmpty()) {
         addCandidate(list, value.trim());
      }
   }

   private static boolean isOnPath(String command) {
      String path = System.getenv("PATH");
      if (path == null) {
         return false;
      } else {
         String[] parts = path.split(File.pathSeparator);

         for (String part : parts) {
            File file = new File(part, command);
            if (file.isFile() && file.canExecute()) {
               return true;
            }
         }

         return false;
      }
   }
}
