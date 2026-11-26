package net.client.ui;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3Text;
import net.minecraft.class_1600;
import net.minecraft.class_1653;
import net.minecraft.class_1895;

public final class ClientUi {
   private static boolean soundBroken;

   private ClientUi() {
   }

   public static void backdrop(Md3Canvas c, Md3Theme t, int w, int h, boolean inWorld) {
      if (w > 0 && h > 0) {
         if (!inWorld) {
            c.fill(0.0F, 0.0F, w, h, t.colors.surface);
            c.glow(w * 0.15F - 160.0F, -150.0F, 480.0F, 360.0F, Md3Colors.alpha(t.colors.primary, ClientTheme.dark() ? 0.16F : 0.09F));
            c.glow(w * 0.82F - 160.0F, h - 220.0F, 420.0F, 340.0F, Md3Colors.alpha(t.colors.secondary, ClientTheme.dark() ? 0.12F : 0.07F));
         }
      }
   }

   public static void click() {
      if (!soundBroken) {
         try {
            class_1600 mc = class_1600.method_2965();
            if (mc == null || mc.method_6630() == null) {
               return;
            }

            mc.method_6630().method_7121(class_1895.method_7052(new class_1653("gui.button.press"), 1.0F));
         } catch (Throwable var1) {
            soundBroken = true;
         }
      }
   }

   public static boolean hit(float mx, float my, float x, float y, float w, float h) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }

   public static String fit(Md3Canvas c, String text, Md3Type type, float maxWidth) {
      if (text != null && !text.isEmpty() && !(maxWidth <= 0.0F)) {
         if (c.textWidth(text, type) <= maxWidth) {
            return text;
         } else {
            String tail = "...";
            float tailW = c.textWidth(tail, type);
            int end = text.length();

            while (end > 0 && c.textWidth(text.substring(0, end), type) + tailW > maxWidth) {
               end--;
            }

            return text.substring(0, Math.max(0, end)) + tail;
         }
      } else {
         return "";
      }
   }

   public static void textFit(Md3Canvas c, String text, float x, float cy, Md3Type type, float maxWidth, int color) {
      Md3Text.leftMiddle(c, fit(c, text, type, maxWidth), x, cy, type, color);
   }

   public static void tag(Md3Canvas c, Md3Theme t, String label, float x, float cy) {
      float tw = c.textWidth(label, Md3Type.LABEL_MEDIUM);
      float w = tw + 16.0F;
      float h = 22.0F;
      c.roundRect(x, cy - h / 2.0F, w, h, 11.0F, t.colors.surfaceContainerHighest);
      Md3Text.centered(c, label, x + w / 2.0F, cy, Md3Type.LABEL_MEDIUM, t.colors.onSurfaceVariant);
   }

   public static float tagWidth(Md3Canvas c, String label) {
      return c.textWidth(label, Md3Type.LABEL_MEDIUM) + 16.0F;
   }

   public static void divider(Md3Canvas c, Md3Theme t, float x, float y, float w) {
      c.fill(x, y, w, 1.0F, t.colors.outlineVariant);
   }

   public static void sectionHeader(Md3Canvas c, Md3Theme t, String title, String trailing, float x, float y, float w) {
      c.text(title, x, y, Md3Type.TITLE_SMALL, t.colors.onSurfaceVariant);
      if (trailing != null && !trailing.isEmpty()) {
         float tw = c.textWidth(trailing, Md3Type.LABEL_MEDIUM);
         c.text(trailing, x + w - tw, y + 1.0F, Md3Type.LABEL_MEDIUM, t.colors.primary);
      }
   }
}
