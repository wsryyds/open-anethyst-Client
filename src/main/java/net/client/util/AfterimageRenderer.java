package net.client.util;

import java.nio.ByteBuffer;
import java.util.List;
import net.client.ClientConfig;
import net.client.mixin.LivingEntityRendererScaleAccessor;
import net.minecraft.class_1600;
import net.minecraft.class_1635;
import net.minecraft.class_1699;
import net.minecraft.class_2403;
import net.minecraft.class_440;
import net.minecraft.class_444;
import net.minecraft.class_550;
import net.minecraft.class_837;
import net.minecraft.class_864;
import org.lwjgl.opengl.GL11;

public final class AfterimageRenderer {
   private AfterimageRenderer() {
   }

   public static void renderAll(List<Afterimage> list, float partialTicks) {
      if (list != null && !list.isEmpty()) {
         class_1600 mc = class_1600.method_2965();
         if (mc.field_3803 != null && mc.method_9388() != null) {
            class_550 dispatcher = mc.method_9390();
            if (dispatcher != null) {
               double[] cam = cameraPos(mc, partialTicks);
               float strength = class_837.method_2336(ClientConfig.afterimageStrength, 0.2F, 2.0F);
               int shells = Math.min(Math.max(ClientConfig.afterimageLayers, 1), 12);
               int rgb = ClientConfig.afterimageColor;
               float red = (rgb >> 16 & 0xFF) / 255.0F;
               float green = (rgb >> 8 & 0xFF) / 255.0F;
               float blue = (rgb & 0xFF) / 255.0F;
               float core = coreOpacity(strength);

               for (Afterimage ghost : list) {
                  float alpha = ghost.getAlpha() * strength;
                  if (!(alpha <= 0.01F)) {
                     class_1699 entity = ghost.entity;
                     if (entity != null && !entity.field_3204) {
                        if (!ghost.captured) {
                           ghost.captureFrom(entity);
                        }

                        if (dispatcher.method_1519(entity) instanceof class_1635 livingRenderer) {
                           class_444 model = livingRenderer.method_10254();
                           if (model != null) {
                              try {
                                 drawGhost(ghost, entity, model, livingRenderer, cam, partialTicks, alpha, core, shells, red, green, blue);
                              } catch (Exception var20) {
                                 ClientLog.error("单张残影渲染异常", var20);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static float coreOpacity(float strength) {
      return class_837.method_2336(0.14F + 0.09F * strength, 0.1F, 0.4F);
   }

   private static double[] cameraPos(class_1600 mc, float partialTicks) {
      class_864 camera = mc.method_9388();
      return camera == null
         ? new double[]{0.0, 0.0, 0.0}
         : new double[]{
            camera.field_3249 + (camera.field_3252 - camera.field_3249) * partialTicks,
            camera.field_3250 + (camera.field_3253 - camera.field_3250) * partialTicks,
            camera.field_3251 + (camera.field_3254 - camera.field_3251) * partialTicks
         };
   }

   private static void placeModel(Afterimage ghost, double[] cam, float scale, class_1635 renderer, class_1699 entity, float partialTicks) {
      class_2403.method_9816((float)(-cam[0]), (float)(-cam[1]), (float)(-cam[2]));
      class_2403.method_9814(ghost.x, ghost.y, ghost.z);
      class_2403.method_9817(180.0F - ghost.bodyYaw, 0.0F, 1.0F, 0.0F);
      class_2403.method_9800(-1.0F, -1.0F, 1.0F);
      ((LivingEntityRendererScaleAccessor)renderer).client$scale(entity, partialTicks);
      class_2403.method_9816(0.0F, -1.5078125F, 0.0F);
      if (scale != 1.0F) {
         class_2403.method_9800(scale, scale, scale);
      }
   }

   private static void drawGhost(
      Afterimage ghost,
      class_1699 entity,
      class_444 model,
      class_1635 renderer,
      double[] cam,
      float partialTicks,
      float alpha,
      float core,
      int shells,
      float red,
      float green,
      float blue
   ) {
      float headYaw = ghost.headYaw - ghost.bodyYaw;
      float pitch = ghost.pitch;
      float savedSwing = model.field_1490;
      boolean savedRiding = model.field_1491;
      boolean savedChild = model.field_1493;
      boolean isBiped = model instanceof class_440;
      boolean savedSneaking = false;
      boolean savedAiming = false;
      model.field_1490 = ghost.swingProgress;
      model.field_1491 = ghost.riding;
      model.field_1493 = ghost.child;
      if (isBiped) {
         class_440 biped = (class_440)model;
         savedSneaking = biped.field_1484;
         savedAiming = biped.field_1485;
         biped.field_1484 = ghost.sneaking;
         biped.field_1485 = false;
      }

      AfterimageRenderer.GlStateSnapshot state = AfterimageRenderer.GlStateSnapshot.capture();
      class_2403.method_9791();

      try {
         setupGhostState();
         class_2403.method_9791();
         placeModel(ghost, cam, 1.0F, renderer, entity, partialTicks);
         class_2403.method_9811(true);
         class_2403.method_9848();
         class_2403.method_9831(1029);
         class_2403.method_9825(red, green, blue, alpha * core);
         drawModel(model, entity, ghost, headYaw, pitch);
         class_2403.method_9792();

         for (int i = 0; i < shells; i++) {
            class_2403.method_9791();
            placeModel(ghost, cam, 1.0F + 0.055F * (i + 1), renderer, entity, partialTicks);
            class_2403.method_9811(false);
            class_2403.method_9848();
            class_2403.method_9831(1028);
            float layerAlpha = alpha * core * (float)Math.pow(0.72F, i + 1);
            class_2403.method_9825(red, green, blue, layerAlpha);
            drawModel(model, entity, ghost, headYaw, pitch);
            class_2403.method_9792();
         }
      } finally {
         class_2403.method_9792();
         state.restore();
         model.field_1490 = savedSwing;
         model.field_1491 = savedRiding;
         model.field_1493 = savedChild;
         if (isBiped) {
            class_440 biped = (class_440)model;
            biped.field_1484 = savedSneaking;
            biped.field_1485 = savedAiming;
         }
      }
   }

   private static void drawModel(class_444 model, class_1699 entity, Afterimage ghost, float headYaw, float pitch) {
      model.method_1176(entity, ghost.limbSwing, ghost.limbSwingAmount, ghost.ticksAlive, headYaw, pitch, 0.0625F);
   }

   private static void setupGhostState() {
      class_2403.method_9856();
      class_2403.method_9832();
      class_2403.method_9822();
      class_2403.method_9836();
      class_2403.method_9854();
      class_2403.method_9843();
      class_2403.method_9819(770, 771);
      class_2403.method_9840();
      class_2403.method_9826(515);
      class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static final class GlStateSnapshot {
      private final boolean texture;
      private final boolean lighting;
      private final boolean alphaTest;
      private final boolean colorMaterial;
      private final boolean blend;
      private final boolean cull;
      private final boolean depthTest;
      private final int depthFunc;
      private final int blendSrc;
      private final int blendDst;
      private final int cullFace;
      private final boolean depthMask;
      private final boolean[] colorMask;

      private GlStateSnapshot(
         boolean texture,
         boolean lighting,
         boolean alphaTest,
         boolean colorMaterial,
         boolean blend,
         boolean cull,
         boolean depthTest,
         int depthFunc,
         int blendSrc,
         int blendDst,
         int cullFace,
         boolean depthMask,
         boolean[] colorMask
      ) {
         this.texture = texture;
         this.lighting = lighting;
         this.alphaTest = alphaTest;
         this.colorMaterial = colorMaterial;
         this.blend = blend;
         this.cull = cull;
         this.depthTest = depthTest;
         this.depthFunc = depthFunc;
         this.blendSrc = blendSrc;
         this.blendDst = blendDst;
         this.cullFace = cullFace;
         this.depthMask = depthMask;
         this.colorMask = colorMask;
      }

      static AfterimageRenderer.GlStateSnapshot capture() {
         ByteBuffer maskBuffer = ByteBuffer.allocateDirect(16);
         GL11.glGetBoolean(3107, maskBuffer);
         boolean[] mask = new boolean[4];

         for (int i = 0; i < 4; i++) {
            mask[i] = maskBuffer.get(i) != 0;
         }

         return new AfterimageRenderer.GlStateSnapshot(
            GL11.glIsEnabled(3553),
            GL11.glIsEnabled(2896),
            GL11.glIsEnabled(3008),
            GL11.glIsEnabled(2903),
            GL11.glIsEnabled(3042),
            GL11.glIsEnabled(2884),
            GL11.glIsEnabled(2929),
            GL11.glGetInteger(2932),
            GL11.glGetInteger(3041),
            GL11.glGetInteger(3040),
            GL11.glGetInteger(2885),
            GL11.glGetBoolean(2930),
            mask
         );
      }

      void restore() {
         class_2403.method_9812(this.colorMask[0], this.colorMask[1], this.colorMask[2], this.colorMask[3]);
         class_2403.method_9811(this.depthMask);
         class_2403.method_9826(this.depthFunc);
         class_2403.method_9831(this.cullFace);
         class_2403.method_9819(this.blendSrc, this.blendDst);
         if (this.texture) {
            class_2403.method_9855();
         } else {
            class_2403.method_9856();
         }

         if (this.lighting) {
            class_2403.method_9830();
         } else {
            class_2403.method_9832();
         }

         if (this.alphaTest) {
            class_2403.method_9828();
         } else {
            class_2403.method_9822();
         }

         if (this.colorMaterial) {
            class_2403.method_9834();
         } else {
            class_2403.method_9836();
         }

         if (this.blend) {
            class_2403.method_9843();
         } else {
            class_2403.method_9842();
         }

         if (this.cull) {
            class_2403.method_9848();
         } else {
            class_2403.method_9850();
         }

         if (this.depthTest) {
            class_2403.method_9840();
         } else {
            class_2403.method_9838();
         }
      }
   }
}
