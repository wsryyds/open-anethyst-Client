package net.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CurseForgeUpdater {
   private static final CurseForgeUpdater.Info UNKNOWN = new CurseForgeUpdater.Info(CurseForgeUpdater.State.UNKNOWN, null, null);
   private static final CurseForgeUpdater.Info CHECKING = new CurseForgeUpdater.Info(CurseForgeUpdater.State.CHECKING, null, null);
   private static final Map<String, CurseForgeUpdater.Info> CACHE = new ConcurrentHashMap<>();
   private static volatile boolean started;
   private static final Pattern VERSIONS = Pattern.compile("\"versions\"\\s*:\\s*\\[([^\\]]*)\\]");
   private static final Pattern VERSION = Pattern.compile("\"version\"\\s*:\\s*\"([^\"]*)\"");
   private static final Pattern DISPLAY = Pattern.compile("\"display\"\\s*:\\s*\"([^\"]*)\"");

   private CurseForgeUpdater() {
   }

   public static CurseForgeUpdater.Info get(String modId) {
      if (modId == null) {
         return UNKNOWN;
      } else {
         CurseForgeUpdater.Info info = CACHE.get(modId);
         return info == null ? UNKNOWN : info;
      }
   }

   public static void checkAll(List<String> modIds, Map<String, String> names, Map<String, String> versions) {
      if (!started) {
         started = true;
         final ArrayList<String> ids = new ArrayList<>(modIds);
         final HashMap<String, String> nameCopy = new HashMap<>(names);
         final HashMap<String, String> verCopy = new HashMap<>(versions);
         Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
               for (String id : ids) {
                  if (!CurseForgeUpdater.isSelf(id)) {
                     CurseForgeUpdater.CACHE.put(id, CurseForgeUpdater.CHECKING);

                     try {
                        CurseForgeUpdater.CACHE.put(id, CurseForgeUpdater.query(id, nameCopy.get(id), verCopy.get(id)));
                     } catch (Exception var4) {
                        CurseForgeUpdater.CACHE.put(id, new CurseForgeUpdater.Info(CurseForgeUpdater.State.ERROR, null, null));
                     }
                  }
               }
            }
         }, "Client-CurseForgeCheck");
         t.setDaemon(true);
         t.start();
      }
   }

   private static boolean isSelf(String id) {
      return "client".equals(id) || "minecraft".equals(id) || "java".equals(id) || "fabricloader".equals(id);
   }

   private static CurseForgeUpdater.Info query(String id, String name, String installed) {
      String page = null;
      String display = null;

      for (String slug : slugCandidates(id, name)) {
         String json = EnvCheck.fetch("https://api.cfwidget.com/minecraft/mc-mods/" + slug, 7000);
         if (json != null) {
            page = "https://www.curseforge.com/minecraft/mc-mods/" + slug;
            display = pick189File(json);
            if (display != null) {
               break;
            }
         }
      }

      if (display == null) {
         return new CurseForgeUpdater.Info(CurseForgeUpdater.State.NO_PROJECT, null, page);
      } else {
         return installed != null && !installed.trim().isEmpty() && containsVersion(display, installed)
            ? new CurseForgeUpdater.Info(CurseForgeUpdater.State.UP_TO_DATE, display, page)
            : new CurseForgeUpdater.Info(CurseForgeUpdater.State.OUTDATED, display, page);
      }
   }

   private static String pick189File(String json) {
      int filesAt = json.indexOf("\"files\"");
      String body = filesAt >= 0 ? json.substring(filesAt) : json;
      Matcher m = VERSIONS.matcher(body);
      String best = null;

      while (m.find()) {
         String group = m.group(1);
         if (group.contains("1.8.9")) {
            int from = Math.max(0, m.start() - 900);
            Matcher d = DISPLAY.matcher(body.substring(from, m.start()));
            String found = null;

            while (d.find()) {
               found = d.group(1);
            }

            if (found == null) {
               Matcher v = VERSION.matcher(body.substring(from, m.start()));

               while (v.find()) {
                  found = v.group(1);
               }
            }

            best = found;
            break;
         }
      }

      return best;
   }

   private static boolean containsVersion(String display, String installed) {
      if (display == null || installed == null || installed.trim().isEmpty()) {
         return false;
      } else if (VersionUtil.isUpToDate(installed, display.trim())) {
         return true;
      } else {
         String normDisplay = VersionUtil.normalize(display);
         String normInstalled = VersionUtil.normalize(installed);
         return !normInstalled.isEmpty() && normDisplay.contains(normInstalled) ? true : compareLike(display, installed);
      }
   }

   private static boolean compareLike(String haystack, String needle) {
      String a = haystack == null ? "" : haystack;
      String b = needle.trim();
      if (b.isEmpty()) {
         return false;
      } else if (a.contains(b)) {
         return true;
      } else {
         String normA = a.replace("-", "").replace("_", "");
         String normB = b.replace("-", "").replace("_", "");
         return !normB.isEmpty() && normA.contains(normB);
      }
   }

   private static List<String> slugCandidates(String id, String name) {
      List<String> out = new ArrayList<>();
      add(out, id);
      if (name != null) {
         add(out, name.toLowerCase().replace(' ', '-'));
         String base = name.replaceAll("(?i)\\s*v?\\d+(\\.\\d+)*.*$", "");
         add(out, base.toLowerCase().trim().replace(' ', '-'));
      }

      return out;
   }

   private static void add(List<String> out, String s) {
      if (s != null) {
         String slug = s.trim().toLowerCase().replaceAll("[^a-z0-9\\-]", "");

         while (slug.startsWith("-")) {
            slug = slug.substring(1);
         }

         while (slug.endsWith("-")) {
            slug = slug.substring(0, slug.length() - 1);
         }

         if (!slug.isEmpty() && !out.contains(slug)) {
            out.add(slug);
         }
      }
   }

   public static final class Info {
      public final CurseForgeUpdater.State state;
      public final String latest;
      public final String page;

      Info(CurseForgeUpdater.State state, String latest, String page) {
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
