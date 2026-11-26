package net.client.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_1600;
import net.minecraft.class_1811;
import net.minecraft.class_1989;
import net.minecraft.class_471;

public final class ModrinthUpdater {
   private static final ModrinthUpdater.Info UNKNOWN = new ModrinthUpdater.Info(ModrinthUpdater.State.UNKNOWN, null, null);
   private static final ModrinthUpdater.Info CHECKING = new ModrinthUpdater.Info(ModrinthUpdater.State.CHECKING, null, null);
   private static final Map<String, ModrinthUpdater.Info> CACHE = new ConcurrentHashMap<>();
   private static volatile boolean started;
   private static final Pattern SLUG = Pattern.compile("\"slug\"\\s*:\\s*\"([^\"]+)\"");
   private static final Pattern VERSION = Pattern.compile("\"version_number\"\\s*:\\s*\"([^\"]+)\"");

   private ModrinthUpdater() {
   }

   public static ModrinthUpdater.Info get(String modId) {
      if (modId == null) {
         return UNKNOWN;
      } else {
         ModrinthUpdater.Info info = CACHE.get(modId);
         return info == null ? UNKNOWN : info;
      }
   }

   public static void checkAll(List<String> modIds, Map<String, String> installedVersions) {
      if (!started && modIds != null && !modIds.isEmpty() || !started) {
         started = true;
         final ArrayList<String> ids = new ArrayList<>(modIds);
         final HashMap<String, String> versions = new HashMap<>(installedVersions);
         Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
               for (String id : ids) {
                  if (!ModrinthUpdater.isSelf(id)) {
                     String installed = versions.get(id);

                     try {
                        ModrinthUpdater.CACHE.put(id, ModrinthUpdater.query(id, installed));
                     } catch (Exception var5) {
                        ModrinthUpdater.CACHE.put(id, new ModrinthUpdater.Info(ModrinthUpdater.State.ERROR, null, null));
                     }
                  }
               }
            }
         }, "Client-ModrinthCheck");
         thread.setDaemon(true);
         thread.start();
      }
   }

   private static boolean isSelf(String id) {
      return "client".equals(id) || "minecraft".equals(id) || "java".equals(id);
   }

   private static ModrinthUpdater.Info query(String modId, String installed) throws Exception {
      CACHE.put(modId, CHECKING);
      String projectJson = fetch("https://api.modrinth.com/v2/project/" + modId, 6000);
      if (projectJson == null) {
         return new ModrinthUpdater.Info(ModrinthUpdater.State.NO_PROJECT, null, null);
      } else {
         Matcher slugMatcher = SLUG.matcher(projectJson);
         if (!slugMatcher.find()) {
            return new ModrinthUpdater.Info(ModrinthUpdater.State.NO_PROJECT, null, null);
         } else {
            String slug = slugMatcher.group(1);
            String page = "https://modrinth.com/mod/" + slug;
            String filter = "?game_versions="
               + URLEncoder.encode("[\"1.8.9\"]", "UTF-8")
               + "&loaders="
               + URLEncoder.encode("[\"fabric\",\"legacy-fabric\"]", "UTF-8");
            String versionsJson = fetch("https://api.modrinth.com/v2/project/" + slug + "/version" + filter, 8000);
            if (versionsJson != null && versionsJson.trim().startsWith("[")) {
               Matcher versionMatcher = VERSION.matcher(versionsJson);
               if (!versionMatcher.find()) {
                  return new ModrinthUpdater.Info(ModrinthUpdater.State.NO_PROJECT, null, page);
               } else {
                  String latest = versionMatcher.group(1);

                  while (versionMatcher.find()) {
                     String candidate = versionMatcher.group(1);
                     if (VersionUtil.compare(candidate, latest) > 0) {
                        latest = candidate;
                     }
                  }

                  boolean current = installed != null && !installed.trim().isEmpty() && VersionUtil.isUpToDate(installed, latest);
                  return current
                     ? new ModrinthUpdater.Info(ModrinthUpdater.State.UP_TO_DATE, latest, page)
                     : new ModrinthUpdater.Info(ModrinthUpdater.State.OUTDATED, latest, page);
               }
            } else {
               return new ModrinthUpdater.Info(ModrinthUpdater.State.NO_PROJECT, null, page);
            }
         }
      }
   }

   private static String fetch(String url, int timeoutMs) {
      HttpURLConnection conn = null;

      String var8;
      try {
         conn = (HttpURLConnection)new URL(url).openConnection();
         conn.setConnectTimeout(timeoutMs);
         conn.setReadTimeout(timeoutMs);
         conn.setRequestProperty("User-Agent", "Client/1.0.1 (Minecraft 1.8.9 mod)");
         conn.setRequestProperty("Accept", "application/json");
         int code = conn.getResponseCode();
         if (code != 200) {
            Object var20 = null;
            return (String)var20;
         }

         BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
         StringBuilder sb = new StringBuilder();

         String line;
         while ((line = reader.readLine()) != null) {
            sb.append(line);
         }

         reader.close();
         var8 = sb.toString();
      } catch (Exception var19) {
         return null;
      } finally {
         if (conn != null) {
            try {
               conn.disconnect();
            } catch (Exception var181) {
               ClientLog.warn("断开 HTTP 连接失败", var181);
            }
         }
      }

      return var8;
   }

   public static void openPage(String page) {
      if (page != null) {
         class_1600 mc = class_1600.method_2965();
         if (mc != null) {
            mc.method_2928(new class_471(new class_1811() {
               @Override
               public void method_1029(boolean confirmed, int id) {
                  class_1600.method_2965().method_2928(null);
                  if (confirmed) {
                     ModrinthUpdater.browse(page);
                  }
               }
            }, page, -1, true));
         } else {
            browse(page);
         }
      }
   }

   private static void browse(String page) {
      try {
         Class<?> desktop = Class.forName("java.awt.Desktop");
         Object instance = desktop.getMethod("getDesktop").invoke(null);
         desktop.getMethod("browse", URI.class).invoke(instance, URI.create(page));
      } catch (Exception var31) {
         ClientLog.warn("调用系统浏览器失败，退化为聊天框地址", var31);
         class_1600 client = class_1600.method_2965();
         if (client != null && client.field_10310 != null) {
            client.field_10310.method_5505(new class_1989("§7Modrinth: §f" + page));
         }
      }
   }

   public static final class Info {
      public final ModrinthUpdater.State state;
      public final String latest;
      public final String page;

      Info(ModrinthUpdater.State state, String latest, String page) {
         this.state = state;
         this.latest = latest;
         this.page = page;
      }
   }

   public static enum State {
      UNKNOWN,
      CHECKING,
      UP_TO_DATE,
      OUTDATED,
      NO_PROJECT,
      ERROR;
   }
}
