package net.client.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;

public final class OptiFineUpdater {
   private static volatile OptiFineUpdater.Info info;
   private static volatile boolean started;
   private static final Pattern REMOTE = Pattern.compile("1\\.8\\.9_(HD_U_[A-Za-z0-9_]+)\\.jar");
   private static final Pattern LOCAL = Pattern.compile("OptiFine[_-]([0-9.]+)_(HD_U_[A-Za-z0-9_]+)");

   private OptiFineUpdater() {
   }

   public static OptiFineUpdater.Info get() {
      ensureStarted();
      return info;
   }

   public static synchronized void ensureStarted() {
      if (!started) {
         started = true;
         Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
               String local = OptiFineUpdater.localVersion();
               String latest = null;

               try {
                  String html = EnvCheck.fetch("https://optifine.net/downloads", 9000);
                  if (html != null) {
                     Matcher m = OptiFineUpdater.REMOTE.matcher(html);
                     if (m.find()) {
                        latest = m.group(1);
                     }
                  }
               } catch (Exception var5) {
                  ClientLog.warn("OptiFine 官网检查失败", var5);
               }

               boolean outdated = local != null && latest != null && !VersionUtil.isUpToDate(local, latest.replace("HD_U_", ""));
               OptiFineUpdater.info = new OptiFineUpdater.Info(local, latest, outdated, "https://optifine.net/downloads");
            }
         }, "Client-OptiFineCheck");
         t.setDaemon(true);
         t.start();
      }
   }

   private static String norm(String v) {
      return v == null ? "" : v.replace("HD_U_", "").replace("_", "").toLowerCase();
   }

   private static String localVersion() {
      try {
         File mods = FabricLoader.getInstance().getGameDir().resolve("mods").toFile();
         if (mods == null || !mods.isDirectory()) {
            return null;
         }

         File[] files = mods.listFiles();
         if (files == null) {
            return null;
         }

         for (File f : files) {
            String name = f.getName();
            if (name.toLowerCase().endsWith(".jar") && name.toLowerCase().contains("optifine") && name.contains("1.8.9")) {
               Matcher m = LOCAL.matcher(name);
               if (m.find()) {
                  return m.group(2).replace("HD_U_", "");
               }
            }
         }
      } catch (Exception var81) {
         ClientLog.warn("读取 OptiFine 本地版本失败", var81);
      }

      return null;
   }

   public static final class Info {
      public final String local;
      public final String latest;
      public final boolean outdated;
      public final String page;

      Info(String local, String latest, boolean outdated, String page) {
         this.local = local;
         this.latest = latest;
         this.outdated = outdated;
         this.page = page;
      }
   }
}
