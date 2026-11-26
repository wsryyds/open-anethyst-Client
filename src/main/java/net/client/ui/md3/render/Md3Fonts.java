package net.client.ui.md3.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.client.ui.md3.theme.Md3Type;

public final class Md3Fonts {
   private static final Map<String, Md3Font> CACHE = new HashMap<>();

   private Md3Fonts() {
   }

   public static Md3Font get(Md3Type type) {
      String key = type.atlasKey() + "@" + type.size + "@" + type.tracking;
      Md3Font font = CACHE.get(key);
      if (font == null) {
         font = Md3Font.load(type);
         if (font != null) {
            font.fallback = fallbackFor(type);
            CACHE.put(key, font);
         }
      }

      return font;
   }

   private static Md3Font fallbackFor(Md3Type type) {
      Md3Type fbType;
      if (type.weight == Md3Type.Weight.MEDIUM) {
         fbType = Md3Type.BODY_MEDIUM;
      } else {
         fbType = Md3Type.BODY_LARGE;
      }

      if (fbType.size == type.size && fbType.weight == type.weight) {
         return null;
      } else {
         Md3Font fb = CACHE.get(fbType.atlasKey() + "@" + fbType.size + "@" + fbType.tracking);
         if (fb == null) {
            fb = Md3Font.load(fbType);
            if (fb == null) {
               return null;
            }

            fb.fallback = null;
            CACHE.put(fbType.atlasKey() + "@" + fbType.size + "@" + fbType.tracking, fb);
         }

         return fb;
      }
   }

   public static float width(String text, Md3Type type) {
      Md3Font font = get(type);
      return font == null ? text.length() * type.size * 0.55F : font.width(text, type.tracking);
   }

   public static float draw(Md3Canvas canvas, String text, float x, float yTop, Md3Type type, int color) {
      Md3Font font = get(type);
      if (font == null) {
         return 0.0F;
      } else {
         font.draw(canvas, text, x, yTop, color, type.tracking);
         return font.width(text, type.tracking);
      }
   }

   public static List<String> wrap(String text, Md3Type type, float maxWidth) {
      List<String> lines = new ArrayList<>();
      if (text != null && !text.isEmpty()) {
         StringBuilder line = new StringBuilder();
         int lastBreak = -1;

         for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
               lines.add(line.toString());
               line.setLength(0);
               lastBreak = -1;
            } else {
               line.append(ch);
               if (ch == ' ' || isCjk(ch)) {
                  lastBreak = line.length();
               }

               if (width(line.toString(), type) > maxWidth && line.length() > 1) {
                  int cut = lastBreak > 0 ? lastBreak : line.length() - 1;
                  String head = line.substring(0, cut).trim();
                  if (!head.isEmpty()) {
                     lines.add(head);
                  }

                  String tail = line.substring(cut).trim();
                  line.setLength(0);
                  line.append(tail);
                  lastBreak = -1;
               }
            }
         }

         if (line.length() > 0) {
            lines.add(line.toString().trim());
         }

         return lines;
      } else {
         return lines;
      }
   }

   private static boolean isCjk(char c) {
      return c >= 11904 && c <= '鿿' || c >= '豈' && c <= '\ufaff';
   }
}
