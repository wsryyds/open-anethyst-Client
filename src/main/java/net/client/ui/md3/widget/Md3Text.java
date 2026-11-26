package net.client.ui.md3.widget;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Type;

public final class Md3Text {
   private Md3Text() {
   }

   public static float topForCenter(float centerY, Md3Type type) {
      return centerY - type.size * 0.56F;
   }

   public static float topForLineCenter(float centerY, Md3Type type) {
      return centerY - type.lineHeight * 0.5F;
   }

   public static void centered(Md3Canvas canvas, String text, float cx, float cy, Md3Type type, int color) {
      canvas.textCentered(text, cx, topForCenter(cy, type), type, color);
   }

   public static void leftMiddle(Md3Canvas canvas, String text, float x, float cy, Md3Type type, int color) {
      canvas.text(text, x, topForCenter(cy, type), type, color);
   }

   public static void rightMiddle(Md3Canvas canvas, String text, float rightX, float cy, Md3Type type, int color) {
      canvas.text(text, rightX - canvas.textWidth(text, type), topForCenter(cy, type), type, color);
   }
}
