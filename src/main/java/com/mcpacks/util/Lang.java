package com.mcpacks.util;

import java.util.Locale;
import java.util.regex.Matcher;
import net.minecraft.class_1600;

public final class Lang {
   private Lang() {
   }

   public static boolean isChinese() {
      class_1600 mc = class_1600.method_2965();
      if (mc != null && mc.field_3823 != null) {
         try {
            String language = mc.field_3823.field_963;
            return language != null && language.toLowerCase(Locale.ROOT).startsWith("zh");
         } catch (Exception var21) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static String s(String en, String zh) {
      return isChinese() ? zh : en;
   }

   public static String s(String en, String zh, Object... args) {
      String pattern = isChinese() ? zh : en;

      for (Object arg : args) {
         pattern = pattern.replaceFirst("%s", Matcher.quoteReplacement(String.valueOf(arg)));
      }

      return pattern;
   }
}
