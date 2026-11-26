package net.client.mixin;

import net.client.ClientConfig;
import net.client.util.ClientLog;
import net.minecraft.class_2403;
import net.minecraft.class_2489;
import net.minecraft.class_837;
import net.minecraft.class_964;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_2489.class)
public abstract class ItemEntityRendererMixin {
   private static class_964 client$current;
   private static int client$renders;
   private static int client$spinCalls;
   private static int client$posCalls;
   private static boolean client$loggedHead;
   private static boolean client$loggedSpin;
   private static boolean client$loggedMiss;

   @Inject(method = "method_10220", at = @At("HEAD"))
   private void client$remember(class_964 entity, double x, double y, double z, float yaw, float tickDelta, CallbackInfo ci) {
      client$current = entity;
      client$renders++;
      if (!client$loggedHead) {
         client$loggedHead = true;
         ClientLog.info("[物理掉落物] render 钩子已挂上，realDrops=" + ClientConfig.realDrops + " dropNoSpin=" + ClientConfig.dropRest);
      }

      if (!client$loggedMiss && client$renders > 40 && client$spinCalls == 0) {
         client$loggedMiss = true;
         ClientLog.warn("[物理掉落物] 自转钩子未命中（渲染 " + client$renders + " 次仍为 0），原版自转没有被拦截");
      }
   }

   @Redirect(method = "method_10221", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2403;method_9817(FFFF)V"), require = 1)
   private static void client$cancelVanillaSpin(float angle, float x, float y, float z) {
      class_964 entity = client$current;
      client$spinCalls++;
      if (!client$loggedSpin) {
         client$loggedSpin = true;
         ClientLog.info("[物理掉落物] 自转钩子命中，onGround=" + (entity != null && entity.field_3197));
      }

      if (client$spinCalls % 200 == 0) {
         ClientLog.info("[物理掉落物] 自转钩子累计命中 " + client$spinCalls + " 次，当前 onGround=" + (entity != null && entity.field_3197));
      }

      if (!ClientConfig.realDrops || !ClientConfig.dropRest || entity == null || !entity.field_3197) {
         class_2403.method_9817(angle, x, y, z);
      }
   }

   @Redirect(method = "method_10221", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2403;method_9816(FFF)V", ordinal = 0), require = 1)
   private static void client$cancelHover(float x, float y, float z) {
      class_964 entity = client$current;
      if (ClientConfig.realDrops && ClientConfig.dropRest && entity != null && entity.field_3197) {
         float hover = class_837.method_2335(entity.method_11157() / 10.0F + entity.field_3895) * 0.1F + 0.1F;
         class_2403.method_9816(x, y - hover, z);
      } else {
         class_2403.method_9816(x, y, z);
      }
   }

   @Inject(
      method = "method_10220",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_560;method_10243(Lnet/minecraft/class_1071;Lnet/minecraft/class_2528;)V", ordinal = 0)
   )
   private void client$poseBlockModel(class_964 entity, double x, double y, double z, float yaw, float tickDelta, CallbackInfo ci) {
      this.client$applyPose(entity, tickDelta);
   }

   @Inject(
      method = "method_10220",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_560;method_10243(Lnet/minecraft/class_1071;Lnet/minecraft/class_2528;)V", ordinal = 1)
   )
   private void client$poseItemModel(class_964 entity, double x, double y, double z, float yaw, float tickDelta, CallbackInfo ci) {
      this.client$applyPose(entity, tickDelta);
   }

   private void client$applyPose(class_964 entity, float tickDelta) {
      client$posCalls++;
      if (ClientConfig.realDrops) {
         double vx = entity.field_3255;
         double vz = entity.field_3257;
         float seconds = (entity.method_11157() + tickDelta) / 20.0F;
         if (entity.field_3197) {
            int id = entity.method_8365();
            float restYaw = id * 47 % 24 * 15.0F;
            class_2403.method_9816(0.0F, -0.12F, 0.0F);
            class_2403.method_9817(restYaw, 0.0F, 1.0F, 0.0F);
            class_2403.method_9817(90.0F, 1.0F, 0.0F, 0.0F);
         } else {
            float travelYaw;
            if (vx * vx + vz * vz > 1.0E-4) {
               travelYaw = (float)Math.toDegrees(Math.atan2(-vx, vz));
            } else {
               travelYaw = entity.method_8365() * 37 % 360;
            }

            float speed = (float)Math.sqrt(vx * vx + vz * vz);
            float tumbleSpeed = 230.0F * (0.6F + Math.min(speed, 0.9F));
            class_2403.method_9817(travelYaw, 0.0F, 1.0F, 0.0F);
            class_2403.method_9817(-tumbleSpeed * seconds, 1.0F, 0.0F, 0.0F);
            class_2403.method_9817(40.0F * seconds, 0.0F, 0.0F, 1.0F);
         }
      }
   }
}
