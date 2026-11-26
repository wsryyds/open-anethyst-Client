package net.client.util;

import net.client.ClientConfig;
import net.minecraft.class_1354;
import net.minecraft.class_1600;
import net.minecraft.class_197;
import net.minecraft.class_2232;
import net.minecraft.class_231;
import net.minecraft.class_234;
import net.minecraft.class_2520;
import net.minecraft.class_2552;
import net.minecraft.class_520;
import net.minecraft.class_533;
import net.minecraft.class_864;
import org.lwjgl.opengl.GL11;

public final class BlockHighlightRenderer {
   private BlockHighlightRenderer() {
   }

   public static void render(class_234 hit, float tickDelta) {
      class_1600 mc = class_1600.method_2965();
      if (mc.field_3803 != null && mc.field_10310 != null && hit != null && hit.method_9344() != null) {
         class_2552 pos = hit.method_9344();
         class_2232 state = mc.field_3803.method_8580(pos);
         class_197 block = state.method_9028();
         String name = block.method_392();
         if (name == null || !name.endsWith("air") && !name.endsWith("air.void")) {
            class_864 cam = mc.method_9388();
            if (cam == null) {
               cam = mc.field_10310;
            }

            double cx = cam.field_3249 + (cam.field_3252 - cam.field_3249) * tickDelta;
            double cy = cam.field_3250 + (cam.field_3253 - cam.field_3250) * tickDelta;
            double cz = cam.field_3251 + (cam.field_3254 - cam.field_3251) * tickDelta;

            class_231 sel;
            try {
               sel = block.method_8657(mc.field_3803, pos);
            } catch (Exception var38) {
               sel = new class_231(
                  pos.method_10572(), pos.method_10573(), pos.method_10574(), pos.method_10572() + 1, pos.method_10573() + 1, pos.method_10574() + 1
               );
            }

            if (sel != null) {
               double minX = sel.field_582 - cx - 1.0E-4;
               double minY = sel.field_583 - cy - 1.0E-4;
               double minZ = sel.field_584 - cz - 1.0E-4;
               double maxX = sel.field_585 - cx + 1.0E-4;
               double maxY = sel.field_586 - cy + 1.0E-4;
               double maxZ = sel.field_587 - cz + 1.0E-4;
               int fill = ClientConfig.highlightFill;
               int line = ClientConfig.highlightOutline;
               float fr = red(fill);
               float fg = green(fill);
               float fb = blue(fill);
               float fa = alpha(fill);
               float lr = red(line);
               float lg = green(line);
               float lb = blue(line);
               float la = alpha(line);
               class_1354 side = hit.field_10268;
               GL11.glPushAttrib(1048575);
               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               GL11.glDisable(3553);
               GL11.glDisable(2884);
               GL11.glEnable(2929);
               GL11.glDepthMask(false);
               GL11.glDisable(2896);
               if (fa > 0.0F) {
                  renderVolumeFill(minX, minY, minZ, maxX, maxY, maxZ, fr, fg, fb, fa);
               }

               if (la > 0.0F) {
                  renderOutline(minX, minY, minZ, maxX, maxY, maxZ, lr, lg, lb, la);
               }

               GL11.glDepthMask(true);
               GL11.glPopAttrib();
               if (side == null) {
                  side = class_1354.field_5200;
               }
            }
         }
      }
   }

   private static void renderVolumeFill(double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
      class_533 tess = class_533.method_9926();
      class_520 buf = tess.method_9928();
      int ri = (int)(r * 255.0F);
      int gi = (int)(g * 255.0F);
      int bi = (int)(b * 255.0F);
      int ai = (int)(a * 255.0F);
      buf.method_9737(7, class_2520.field_11207);
      quad(buf, ri, gi, bi, ai, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2);
      quad(buf, ri, gi, bi, ai, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1);
      quad(buf, ri, gi, bi, ai, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1);
      quad(buf, ri, gi, bi, ai, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
      quad(buf, ri, gi, bi, ai, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1);
      quad(buf, ri, gi, bi, ai, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2);
      tess.method_9927();
   }

   private static void quad(
      class_520 buf,
      int r,
      int g,
      int b,
      int a,
      double x1,
      double y1,
      double z1,
      double x2,
      double y2,
      double z2,
      double x3,
      double y3,
      double z3,
      double x4,
      double y4,
      double z4
   ) {
      buf.method_9742(x1, y1, z1).method_9747(r, g, b, a).method_9750();
      buf.method_9742(x2, y2, z2).method_9747(r, g, b, a).method_9750();
      buf.method_9742(x3, y3, z3).method_9747(r, g, b, a).method_9750();
      buf.method_9742(x4, y4, z4).method_9747(r, g, b, a).method_9750();
   }

   private static void renderOutline(double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glLineWidth(2.0F);
      class_533 tess = class_533.method_9926();
      class_520 buf = tess.method_9928();
      int ri = (int)(r * 255.0F);
      int gi = (int)(g * 255.0F);
      int bi = (int)(b * 255.0F);
      int ai = (int)(a * 255.0F);
      buf.method_9737(1, class_2520.field_11207);
      edge(buf, ri, gi, bi, ai, x1, y1, z1, x2, y1, z1);
      edge(buf, ri, gi, bi, ai, x2, y1, z1, x2, y1, z2);
      edge(buf, ri, gi, bi, ai, x2, y1, z2, x1, y1, z2);
      edge(buf, ri, gi, bi, ai, x1, y1, z2, x1, y1, z1);
      edge(buf, ri, gi, bi, ai, x1, y2, z1, x2, y2, z1);
      edge(buf, ri, gi, bi, ai, x2, y2, z1, x2, y2, z2);
      edge(buf, ri, gi, bi, ai, x2, y2, z2, x1, y2, z2);
      edge(buf, ri, gi, bi, ai, x1, y2, z2, x1, y2, z1);
      edge(buf, ri, gi, bi, ai, x1, y1, z1, x1, y2, z1);
      edge(buf, ri, gi, bi, ai, x2, y1, z1, x2, y2, z1);
      edge(buf, ri, gi, bi, ai, x2, y1, z2, x2, y2, z2);
      edge(buf, ri, gi, bi, ai, x1, y1, z2, x1, y2, z2);
      tess.method_9927();
      GL11.glLineWidth(1.0F);
      GL11.glDisable(2848);
   }

   private static void edge(class_520 buf, int r, int g, int b, int a, double x1, double y1, double z1, double x2, double y2, double z2) {
      buf.method_9742(x1, y1, z1).method_9747(r, g, b, a).method_9750();
      buf.method_9742(x2, y2, z2).method_9747(r, g, b, a).method_9750();
   }

   private static float red(int argb) {
      return (argb >> 16 & 0xFF) / 255.0F;
   }

   private static float green(int argb) {
      return (argb >> 8 & 0xFF) / 255.0F;
   }

   private static float blue(int argb) {
      return (argb & 0xFF) / 255.0F;
   }

   private static float alpha(int argb) {
      return (argb >> 24 & 0xFF) / 255.0F;
   }
}
