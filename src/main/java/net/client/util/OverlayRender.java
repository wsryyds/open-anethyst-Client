package net.client.util;

import net.client.ClientConfig;
import net.minecraft.class_1600;
import net.minecraft.class_2403;
import net.minecraft.class_370;
import net.minecraft.class_518;
import org.lwjgl.opengl.GL11;

public final class OverlayRender {
   private OverlayRender() {
   }

   public static void render(float tickDelta) {
      class_1600 mc = class_1600.method_2965();
      if (mc.field_10310 != null && mc.field_3803 != null && ClientConfig.selfNameTag && mc.field_3823.field_949 != 0) {
      }
   }

   private static void renderSelfName(class_1600 mc, float tickDelta) {
      class_518 player = mc.field_10310;
      class_370 textRenderer = mc.field_3814;
      String text = player.method_6344().method_7472();
      if (text != null && !text.isEmpty()) {
         double x = player.field_3249 + (player.field_3252 - player.field_3249) * tickDelta;
         double y = player.field_3250 + (player.field_3253 - player.field_3250) * tickDelta + player.field_3207 + 0.5;
         double z = player.field_3251 + (player.field_3254 - player.field_3251) * tickDelta;
         int bg = ClientConfig.nameBgColor;
         int fg = ClientConfig.nameTextColor;
         class_2403.method_9791();
         class_2403.method_9816((float)x, (float)y, (float)z);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         class_2403.method_9817(-player.field_3258, 0.0F, 1.0F, 0.0F);
         class_2403.method_9817(player.field_3193, 1.0F, 0.0F, 0.0F);
         class_2403.method_9800(-0.02666667F, -0.02666667F, 0.02666667F);
         class_2403.method_9832();
         class_2403.method_9811(false);
         class_2403.method_9843();
         class_2403.method_9805(770, 771, 1, 0);
         int halfWidth = textRenderer.method_954(text) / 2;
         class_2403.method_9856();
         class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glColor4f(red(bg), green(bg), blue(bg), alpha(bg));
         GL11.glBegin(7);
         GL11.glVertex3d(-halfWidth - 1.0, -1.0, 0.0);
         GL11.glVertex3d(-halfWidth - 1.0, 8.0, 0.0);
         GL11.glVertex3d(halfWidth + 1.0, 8.0, 0.0);
         GL11.glVertex3d(halfWidth + 1.0, -1.0, 0.0);
         GL11.glEnd();
         class_2403.method_9855();
         textRenderer.method_4247(text, -halfWidth, 0, fg);
         class_2403.method_9842();
         class_2403.method_9830();
         class_2403.method_9811(true);
         class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
         class_2403.method_9792();
      }
   }

   private static void boxEdges(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      line(minX, minY, minZ, maxX, minY, minZ);
      line(maxX, minY, minZ, maxX, minY, maxZ);
      line(maxX, minY, maxZ, minX, minY, maxZ);
      line(minX, minY, maxZ, minX, minY, minZ);
      line(minX, maxY, minZ, maxX, maxY, minZ);
      line(maxX, maxY, minZ, maxX, maxY, maxZ);
      line(maxX, maxY, maxZ, minX, maxY, maxZ);
      line(minX, maxY, maxZ, minX, maxY, minZ);
      line(minX, minY, minZ, minX, maxY, minZ);
      line(maxX, minY, minZ, maxX, maxY, minZ);
      line(maxX, minY, maxZ, maxX, maxY, maxZ);
      line(minX, minY, maxZ, minX, maxY, maxZ);
   }

   private static void line(double x1, double y1, double z1, double x2, double y2, double z2) {
      GL11.glVertex3d(x1, y1, z1);
      GL11.glVertex3d(x2, y2, z2);
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
