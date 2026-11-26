package net.client.ui.md3.widget;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;

public final class Md3Surfaces {
   private Md3Surfaces() {
   }

   public static void card(Md3Canvas c, Md3Theme t, float x, float y, float w, float h, Md3Surfaces.CardVariant variant, float radius) {
      if (variant == Md3Surfaces.CardVariant.ELEVATED) {
         c.shadow(x, y, w, h, radius, 1);
         c.roundRect(x, y, w, h, radius, t.colors.surfaceContainerLow);
      } else if (variant == Md3Surfaces.CardVariant.FILLED) {
         c.roundRect(x, y, w, h, radius, t.colors.surfaceContainerHighest);
      } else {
         c.roundRect(x, y, w, h, radius, t.colors.surfaceContainerLowest);
         c.strokeRoundRect(x, y, w, h, radius, 1.0F, t.colors.outlineVariant);
      }
   }

   public static void dialog(Md3Canvas c, Md3Theme t, float x, float y, float w, float h) {
      c.shadow(x, y, w, h, 28.0F, 5);
      c.roundRect(x, y, w, h, 28.0F, t.colors.surfaceContainerHigh);
   }

   public static void snackbar(Md3Canvas c, Md3Theme t, float x, float y, float w, float h) {
      c.shadow(x, y, w, h, 4.0F, 3);
      c.roundRect(x, y, w, h, 4.0F, t.colors.inverseSurface);
   }

   public static void menu(Md3Canvas c, Md3Theme t, float x, float y, float w, float h) {
      c.shadow(x, y, w, h, 12.0F, 3);
      c.roundRect(x, y, w, h, 12.0F, t.colors.surfaceContainer);
   }

   public static void menuItem(Md3Canvas c, Md3Theme t, float x, float y, float w, float h, String icon, String label, boolean selected) {
      if (selected) {
         c.roundRect(x + 4.0F, y, w - 8.0F, h, 8.0F, t.colors.secondaryContainer);
      }

      int color = selected ? t.colors.onSecondaryContainer : t.colors.onSurfaceVariant;
      c.icon(icon, x + 12.0F, y + h / 2.0F - 10.0F, 20.0F, color);
      Md3Text.leftMiddle(c, label, x + 44.0F, y + h / 2.0F, Md3Type.BODY_MEDIUM, color);
   }

   public static void tooltip(Md3Canvas c, Md3Theme t, float x, float y, float w, float h, String label) {
      c.shadow(x, y, w, h, 4.0F, 2);
      c.roundRect(x, y, w, h, 4.0F, t.colors.surfaceContainerHighest);
      Md3Text.centered(c, label, x + w / 2.0F, y + h / 2.0F, Md3Type.LABEL_MEDIUM, t.colors.onSurface);
   }

   public static void badge(Md3Canvas c, float x, float y, float d, int color, String label) {
      c.roundRect(x, y, d, d, d / 2.0F, color);
      if (label != null) {
         Md3Text.centered(c, label, x + d / 2.0F, y + d / 2.0F, Md3Type.LABEL_SMALL, Md3Colors.rgb(16777215));
      }
   }

   public static enum CardVariant {
      ELEVATED,
      FILLED,
      OUTLINED;
   }
}
