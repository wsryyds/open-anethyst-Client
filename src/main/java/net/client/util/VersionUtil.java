package net.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class VersionUtil {
   private VersionUtil() {
   }

   public static String normalize(String raw) {
      if (raw == null) {
         return "";
      } else {
         String v = raw.trim().toLowerCase(Locale.ROOT);
         if (v.isEmpty()) {
            return "";
         } else {
            while (v.startsWith("v") || v.startsWith("build-") || v.startsWith("build")) {
               if (v.startsWith("v")) {
                  v = v.substring(1);
               } else if (v.startsWith("build-")) {
                  v = v.substring(6);
               } else if (v.startsWith("build")) {
                  v = v.substring(5);
               }
            }

            int plus = v.indexOf(43);
            if (plus >= 0) {
               v = v.substring(0, plus);
            }

            if (v.endsWith("_1.8.9")) {
               v = v.substring(0, v.length() - 6);
            }

            if (v.endsWith("-mc1.8.9")) {
               v = v.substring(0, v.length() - 8);
            } else if (v.endsWith("-1.8.9")) {
               v = v.substring(0, v.length() - 6);
            }

            return v.trim();
         }
      }
   }

   private static List<String> tokens(String raw) {
      String v = normalize(raw);
      List<String> out = new ArrayList<>();
      if (v.isEmpty()) {
         return out;
      } else {
         StringBuilder sb = new StringBuilder();
         char prev = 0;

         for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            boolean digit = c >= '0' && c <= '9';
            if (i > 0 && digit != (prev >= '0' && prev <= '9') && !isSeparator(c) && !isSeparator(prev)) {
               out.add(sb.toString());
               sb.setLength(0);
            }

            if (!isSeparator(c)) {
               sb.append(c);
            }

            prev = c;
         }

         if (sb.length() > 0) {
            out.add(sb.toString());
         }

         return out;
      }
   }

   private static boolean isSeparator(char c) {
      return c == '.' || c == '-' || c == '_' || c == ' ' || c == '+';
   }

   private static boolean isNumeric(String s) {
      if (s.isEmpty()) {
         return false;
      } else {
         for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
               return false;
            }
         }

         return true;
      }
   }

   public static int compare(String a, String b) {
      String na = normalize(a);
      String nb = normalize(b);
      int da = indexOfPreRelease(na);
      int db = indexOfPreRelease(nb);
      String baseA = da >= 0 ? na.substring(0, da) : na;
      String baseB = db >= 0 ? nb.substring(0, db) : nb;
      int baseCmp = compareTokens(baseA, baseB);
      if (baseCmp != 0) {
         return baseCmp;
      } else {
         boolean preA = da >= 0;
         boolean preB = db >= 0;
         if (preA != preB) {
            return preA ? -1 : 1;
         } else {
            return !preA ? 0 : compareTokens(na.substring(da), nb.substring(db));
         }
      }
   }

   private static int indexOfPreRelease(String v) {
      String[] marks = new String[]{"-alpha", "-beta", "-rc", "-snapshot", "-pre", "-dev", "-preview", "_alpha", "_beta", "_rc", "_pre"};
      int best = -1;

      for (String m : marks) {
         int i = v.indexOf(m);
         if (i >= 0 && (best < 0 || i < best)) {
            best = i;
         }
      }

      return best;
   }

   private static int compareTokens(String a, String b) {
      List<String> ta = tokens(a);
      List<String> tb = tokens(b);
      if (ta.isEmpty() && tb.isEmpty()) {
         return 0;
      } else {
         int n = Math.max(ta.size(), tb.size());

         for (int i = 0; i < n; i++) {
            String x = i < ta.size() ? ta.get(i) : "0";
            String y = i < tb.size() ? tb.get(i) : "0";
            if (isNumeric(x) && isNumeric(y)) {
               int vx = safeInt(x);
               int vy = safeInt(y);
               if (vx != vy) {
                  return vx < vy ? -1 : 1;
               }
            } else {
               int c = x.compareTo(y);
               if (c != 0) {
                  return c < 0 ? -1 : 1;
               }
            }
         }

         return 0;
      }
   }

   private static int safeInt(String s) {
      try {
         return Integer.parseInt(s);
      } catch (Exception var2) {
         return 0;
      }
   }

   public static boolean isUpToDate(String installed, String latest) {
      if (installed != null && latest != null) {
         String a = normalize(installed);
         String b = normalize(latest);
         if (a.isEmpty() || b.isEmpty()) {
            return false;
         } else if (a.equals(b)) {
            return true;
         } else {
            return a.replace("-", "").replace("_", "").equals(b.replace("-", "").replace("_", "")) ? true : compare(a, b) >= 0;
         }
      } else {
         return false;
      }
   }
}
