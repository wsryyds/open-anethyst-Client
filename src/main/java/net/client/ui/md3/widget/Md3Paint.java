package net.client.ui.md3.widget;

import net.client.ui.md3.theme.Md3Colors;

public final class Md3Paint {
   private Md3Paint() {
   }

   public static int container(int base, int layer, float hover, float press) {
      float opacity = Math.max(hover * 0.08F, press * 0.12F);
      return opacity <= 0.0F ? base : Md3Colors.composite(Md3Colors.alpha(layer, opacity), base);
   }

   public static int disabledContainer(int surface, int onSurface) {
      return Md3Colors.composite(Md3Colors.alpha(onSurface, 0.12F), surface);
   }

   public static int disabledContent(int onSurface) {
      return Md3Colors.alpha(onSurface, 0.38F);
   }
}
