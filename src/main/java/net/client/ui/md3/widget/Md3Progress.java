package net.client.ui.md3.widget;

import net.client.ui.md3.anim.Md3Anim;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.render.Md3Shapes;
import net.client.ui.md3.theme.Md3Type;

public final class Md3Progress {
   private Md3Progress() {
   }

   public static void linear(Md3Canvas c, float x, float y, float w, int color, int trackColor, float value, boolean indeterminate, float time) {
      float h = 4.0F;
      c.roundRect(x, y, w, h, 2.0F, trackColor);
      if (indeterminate) {
         float phase = time * 0.8F % 1.6F;
         float p = Math.min(1.0F, Math.max(0.0F, phase / 1.2F));
         float eased = Md3Anim.emphasized(p);
         float barW = w * (0.3F + 0.18F * (float)Math.sin(p * Math.PI));
         float bx = x + (w - barW) * eased;
         c.roundRect(bx, y, barW, h, 2.0F, color);
      } else {
         c.roundRect(x, y, w * Math.max(0.0F, Math.min(1.0F, value)), h, 2.0F, color);
      }
   }

   public static void circular(
      Md3Canvas c, float cx, float cy, float diameter, float thickness, int color, int trackColor, float value, boolean indeterminate, float time
   ) {
      float r = diameter / 2.0F;
      c.strokeRoundRect(cx - r, cy - r, diameter, diameter, r, thickness, trackColor);
      if (indeterminate || !(value <= 0.001F)) {
         int rpx = Math.max(2, Math.round(r * c.scale));
         int tpx = Math.max(1, Math.round(thickness * c.scale));
         float angle;
         int sweep;
         if (indeterminate) {
            sweep = 260;
            angle = time * 300.0F % 360.0F;
         } else {
            sweep = Math.min(359, Math.round(value * 360.0F));
            if (value >= 0.999F) {
               c.strokeRoundRect(cx - r, cy - r, diameter, diameter, r, thickness, color);
               return;
            }

            angle = -90.0F;
         }

         int tex = Md3Shapes.arc(rpx, tpx, sweep);
         float drawSize = (float)(rpx * 2 + 2) / c.scale;
         float left = cx - drawSize / 2.0F;
         float top = cy - drawSize / 2.0F;
         c.pushMatrix();
         c.translate(cx, cy);
         c.rotate(angle);
         c.translate(-cx, -cy);
         c.image(tex, left, top, drawSize, drawSize, 0.0F, 0.0F, 1.0F, 1.0F, color);
         c.popMatrix();
      }
   }

   public static void linearGradientText(Md3Canvas c, String value, float x, float right, float cy, int color) {
      Md3Type type = Md3Type.LABEL_MEDIUM;
      Md3Text.rightMiddle(c, value, right, cy, type, color);
   }
}
