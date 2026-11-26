package net.client.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;

public final class EnvCheck {
   public static final int MIN_MAJOR = 21;
   public static final int RECOMMENDED_MAJOR = 25;
   public static final String RECOMMENDED_LOADER = "0.20.1";
   public static final String MIN_LOADER = "0.12.0";
   private static volatile EnvCheck.Status javaStatus = new EnvCheck.Status("Java 检查中…", "checking Java…", 0);
   private static volatile EnvCheck.Status loaderStatus = new EnvCheck.Status("Legacy Fabric 检查中…", "checking Legacy Fabric…", 0);
   private static volatile boolean started;
   private static final Pattern SEMVER = Pattern.compile("\"semver\"\\s*:\\s*\"([^\"]+)\"");
   private static final Pattern LDR_VER = Pattern.compile("\"version\"\\s*:\\s*\"([^\"]+)\"");

   private EnvCheck() {
   }

   public static EnvCheck.Status java() {
      ensureStarted();
      return javaStatus;
   }

   public static EnvCheck.Status loader() {
      ensureStarted();
      return loaderStatus;
   }

   public static synchronized void ensureStarted() {
      if (!started) {
         started = true;
         Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
               try {
                  EnvCheck.checkJava();
               } catch (Exception var3) {
                  EnvCheck.javaStatus = new EnvCheck.Status("Java 信息读取失败", "failed to read Java info", 2);
               }

               try {
                  EnvCheck.checkLoader();
               } catch (Exception var2) {
                  EnvCheck.loaderStatus = new EnvCheck.Status("Legacy Fabric 检查失败", "Legacy Fabric check failed", 0);
               }
            }
         }, "Client-EnvCheck");
         t.setDaemon(true);
         t.start();
      }
   }

   private static void checkJava() {
      String raw = System.getProperty("java.version", "?");
      String vendor = resolveVendor(raw);
      boolean internalRuntime = raw.toLowerCase().contains("internal");
      String vendorZh = "内置运行时".equals(vendor) ? "内置运行时" : vendor;
      String vendorEn = "内置运行时".equals(vendor) ? "bundled runtime" : vendor;
      int major = parseMajor(raw);
      int patch = parsePatch(raw, major);
      if (major <= 0) {
         javaStatus = new EnvCheck.Status("Java 版本无法解析（" + raw + "）", "unparseable Java version (" + raw + ")", 2);
      } else {
         String localTag = prettyVersion(raw);
         String vendorTagZh = vendorZh.isEmpty() ? "" : " · " + vendorZh;
         String vendorTagEn = vendorEn.isEmpty() ? "" : " · " + vendorEn;
         String range = "Java 21 及以上（推荐 25 LTS）";
         String rangeEn = "Java 21+ (25 LTS recommended)";
         if (major < 21) {
            javaStatus = new EnvCheck.Status(
               "Java " + localTag + vendorTagZh + " · 无法运行（本模组需要 " + range + "）",
               "Java " + localTag + vendorTagEn + " · unsupported (this mod requires " + rangeEn + ")",
               2
            );
         } else if (major < 25) {
            javaStatus = new EnvCheck.Status(
               "Java " + localTag + vendorTagZh + " · 可运行（推荐 25 LTS）", "Java " + localTag + vendorTagEn + " · runs (Java 25 LTS recommended)", 1
            );
         } else if (major > 25) {
            javaStatus = new EnvCheck.Status(
               "Java " + localTag + vendorTagZh + " · 可运行（较新，推荐 25 LTS）",
               "Java " + localTag + vendorTagEn + " · usable (newer than 25 LTS, may need extra launch flags)",
               1
            );
         } else {
            int latest = fetchLatestPatch(major);
            if (latest <= 0 || patch < 0) {
               javaStatus = new EnvCheck.Status(
                  "Java " + localTag + vendorTagZh + " · 推荐版本（25 LTS）", "Java " + localTag + vendorTagEn + " · recommended (25 LTS)", 1
               );
            } else if (patch < latest) {
               if (internalRuntime) {
                  javaStatus = new EnvCheck.Status(
                     "Java " + localTag + vendorTagZh + " · 推荐版本，随启动器更新（官方最新 25.0." + latest + "）",
                     "Java " + localTag + vendorTagEn + " · recommended, bundled runtime, update with your launcher (official 25.0." + latest + ")",
                     1
                  );
               } else {
                  javaStatus = new EnvCheck.Status(
                     "Java " + localTag + vendorTagZh + " · 推荐版本，该分支最新为 25.0." + latest + "，可升级补丁",
                     "Java " + localTag + vendorTagEn + " · recommended, latest on this branch is 25.0." + latest + ", patch update available",
                     1
                  );
               }
            } else {
               javaStatus = new EnvCheck.Status(
                  "Java " + localTag + vendorTagZh + " · 推荐版本，已是 25 分支最新补丁", "Java " + localTag + vendorTagEn + " · recommended, latest 25 patch", 1
               );
            }
         }
      }
   }

   private static String prettyVersion(String raw) {
      if (raw == null) {
         return "?";
      } else {
         String v = raw.trim();
         int dash = v.indexOf(45);
         if (dash > 0) {
            v = v.substring(0, dash);
         }

         int plus = v.indexOf(43);
         if (plus > 0) {
            v = v.substring(0, plus);
         }

         return v;
      }
   }

   private static String resolveVendor(String raw) {
      String vendor = shortVendor(System.getProperty("java.vendor", ""));
      if (vendor.isEmpty() || "N/A".equalsIgnoreCase(vendor) || "unknown".equalsIgnoreCase(vendor) || "null".equalsIgnoreCase(vendor)) {
         vendor = "";
      }

      if (vendor.isEmpty()) {
         String vm = shortVendor(System.getProperty("java.vm.vendor", ""));
         if (!vm.isEmpty() && !"N/A".equalsIgnoreCase(vm)) {
            vendor = vm;
         }
      }

      if (raw != null && raw.toLowerCase().contains("internal")) {
         vendor = "内置运行时";
      }

      return vendor;
   }

   private static String shortVendor(String vendor) {
      if (vendor == null) {
         return "";
      } else {
         String v = vendor.trim();
         if (v.isEmpty()) {
            return "";
         } else {
            String low = v.toLowerCase();
            if (low.contains("adoptium") || low.contains("temurin")) {
               return "Adoptium";
            } else if (low.contains("oracle")) {
               return "Oracle";
            } else if (low.contains("microsoft")) {
               return "Microsoft";
            } else if (low.contains("azul")) {
               return "Azul";
            } else if (low.contains("bellsoft") || low.contains("liberica")) {
               return "Liberica";
            } else if (low.contains("amazon")) {
               return "Corretto";
            } else if (low.contains("graalvm")) {
               return "GraalVM";
            } else if (low.contains("android")) {
               return "Android";
            } else if (low.contains("openjdk")) {
               return "OpenJDK";
            } else {
               return v.length() > 14 ? v.substring(0, 14) : v;
            }
         }
      }
   }

   private static int parseMajor(String raw) {
      if (raw != null && !raw.isEmpty()) {
         try {
            String v = raw.trim();
            if (v.startsWith("1.")) {
               String rest = v.substring(2);
               int dot = rest.indexOf(46);
               return Integer.parseInt(dot > 0 ? rest.substring(0, dot) : rest);
            } else {
               int dot = v.indexOf(46);
               String head = dot > 0 ? v.substring(0, dot) : v;
               int dash = head.indexOf(45);
               if (dash > 0) {
                  head = head.substring(0, dash);
               }

               return Integer.parseInt(head);
            }
         } catch (Exception var51) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private static int parsePatch(String raw, int major) {
      if (raw == null) {
         return -1;
      } else {
         try {
            if (major != 8 && !raw.startsWith("1.")) {
               String v = raw.trim();
               int dash = v.indexOf(45);
               if (dash > 0) {
                  v = v.substring(0, dash);
               }

               String[] parts = v.split("\\.");
               return parts.length >= 3 ? Integer.parseInt(parts[2]) : 0;
            } else {
               int us = raw.indexOf(95);
               return us > 0 ? Integer.parseInt(raw.substring(us + 1)) : -1;
            }
         } catch (Exception var51) {
            return -1;
         }
      }
   }

   private static int fetchLatestPatch(int major) {
      String os = apiOs();
      String arch = apiArch();
      String json = fetch("https://api.adoptium.net/v3/assets/latest/" + major + "/hotspot?os=" + os + "&architecture=" + arch + "&image_type=jdk", 8000);
      if (json == null) {
         return -1;
      } else {
         Matcher m = SEMVER.matcher(json);
         return !m.find() ? -1 : parsePatch(m.group(1).replace("+", "-"), major);
      }
   }

   private static String apiOs() {
      String os = System.getProperty("os.name", "").toLowerCase();
      if (os.contains("win")) {
         return "windows";
      } else {
         return os.contains("mac") ? "mac" : "linux";
      }
   }

   private static String apiArch() {
      String arch = System.getProperty("os.arch", "").toLowerCase();
      if (!arch.contains("aarch64") && !arch.contains("arm64")) {
         return arch.contains("arm") ? "arm" : "x64";
      } else {
         return "aarch64";
      }
   }

   private static void checkLoader() {
      String local = localLoaderVersion();
      String json = fetch("https://meta.legacyfabric.net/v2/versions/loader/1.8.9", 8000);
      String latest = null;
      if (json != null) {
         Matcher m = LDR_VER.matcher(json);
         if (m.find()) {
            latest = m.group(1);
         }
      }

      if (latest == null) {
         loaderStatus = new EnvCheck.Status("Legacy Fabric 加载器 " + local + " · 无法获取最新版本", "Legacy Fabric loader " + local + " · latest unknown", 0);
      } else {
         int cmp = compareVersion(local, latest);
         int minCmp = compareVersion(local, "0.12.0");
         if (minCmp < 0) {
            loaderStatus = new EnvCheck.Status(
               "Legacy Fabric 加载器 " + local + " · 过旧（最低 0.12.0，推荐 0.20.1）", "Legacy Fabric loader " + local + " · too old (min 0.12.0, 0.20.1 recommended)", 2
            );
         } else if (cmp < 0) {
            loaderStatus = new EnvCheck.Status(
               "Legacy Fabric 加载器 " + local + " · 有新版 " + latest + "（推荐 0.20.1）",
               "Legacy Fabric loader " + local + " · newer " + latest + " available (0.20.1 recommended)",
               0
            );
         } else {
            loaderStatus = new EnvCheck.Status("Legacy Fabric 加载器 " + local + " · 已是 1.8.9 可用的最新版本", "Legacy Fabric loader " + local + " · latest for 1.8.9", 1);
         }
      }
   }

   private static String localLoaderVersion() {
      try {
         FabricLoader loader = FabricLoader.getInstance();
         if (loader.getModContainer("fabricloader").isPresent()) {
            return loader.getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString();
         }
      } catch (Exception var11) {
         ClientLog.warn("读取 Fabric Loader 版本失败", var11);
      }

      return "?";
   }

   static int compareVersion(String a, String b) {
      if (a != null && b != null) {
         String[] pa = a.split("\\.");
         String[] pb = b.split("\\.");
         int n = Math.max(pa.length, pb.length);

         for (int i = 0; i < n; i++) {
            int va = i < pa.length ? num(pa[i]) : 0;
            int vb = i < pb.length ? num(pb[i]) : 0;
            if (va != vb) {
               return va < vb ? -1 : 1;
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   private static int num(String s) {
      try {
         StringBuilder sb = new StringBuilder();

         for (char c : s.toCharArray()) {
            if (c < '0' || c > '9') {
               break;
            }

            sb.append(c);
         }

         return sb.length() == 0 ? 0 : Integer.parseInt(sb.toString());
      } catch (Exception var61) {
         return 0;
      }
   }

   static String fetch(String url, int timeoutMs) {
      HttpURLConnection conn = null;

      String var7;
      try {
         conn = (HttpURLConnection)new URL(url).openConnection();
         conn.setConnectTimeout(timeoutMs);
         conn.setReadTimeout(timeoutMs);
         conn.setRequestProperty("User-Agent", "Client/1.0.1 (Minecraft 1.8.9 mod)");
         conn.setRequestProperty("Accept", "application/json");
         if (conn.getResponseCode() != 200) {
            Object e = null;
            return (String)e;
         }

         BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
         StringBuilder sb = new StringBuilder();

         String line;
         while ((line = reader.readLine()) != null) {
            sb.append(line);
         }

         reader.close();
         var7 = sb.toString();
      } catch (Exception var18) {
         return null;
      } finally {
         if (conn != null) {
            try {
               conn.disconnect();
            } catch (Exception var171) {
            }
         }
      }

      return var7;
   }

   public static final class Status {
      public final String zh;
      public final String en;
      public final int tone;

      Status(String zh, String en, int tone) {
         this.zh = zh;
         this.en = en;
         this.tone = tone;
      }
   }
}
