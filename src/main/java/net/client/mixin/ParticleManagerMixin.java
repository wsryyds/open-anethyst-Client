package net.client.mixin;

import java.util.Random;
import net.client.ClientConfig;
import net.client.util.CustomParticles;
import net.minecraft.class_1354;
import net.minecraft.class_1600;
import net.minecraft.class_2232;
import net.minecraft.class_2552;
import net.minecraft.class_2562;
import net.minecraft.class_502;
import net.minecraft.class_503;
import net.minecraft.class_864;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_503.class)
public class ParticleManagerMixin {
   @Unique
   private static boolean client$spawningExtra = false;
   @Unique
   private static float client$critCarry = 0.0F;
   @Unique
   private static float client$sharpCarry = 0.0F;
   @Unique
   private static final Random CLIENT_RANDOM = new Random();

   @Inject(method = "method_9704", at = @At("HEAD"), cancellable = true)
   private void client$skipBlockBreak(class_2552 pos, class_2232 state, CallbackInfo ci) {
      if (ClientConfig.hideMiningParticles) {
         ci.cancel();
      }
   }

   @Inject(method = "method_9705", at = @At("HEAD"), cancellable = true)
   private void client$skipBlockBreaking(class_2552 pos, class_1354 direction, CallbackInfo ci) {
      if (ClientConfig.hideMiningParticles) {
         ci.cancel();
      }
   }

   @Inject(method = "method_9707", at = @At("HEAD"), cancellable = true)
   private void client$skipOwnEmitter(class_864 entity, class_2562 type, CallbackInfo ci) {
      if (ClientConfig.hideOwnParticles && (type == class_2562.field_11512 || type == class_2562.field_11513)) {
         class_1600 client = class_1600.method_2965();
         if (client != null && client.field_10310 != null && entity == client.field_10310) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "method_9701", at = @At("HEAD"), cancellable = true)
   private void client$skipOwnPotion(int id, double x, double y, double z, double vx, double vy, double vz, int[] args, CallbackInfoReturnable<class_502> cir) {
      if (ClientConfig.hideOwnParticles && (id == class_2562.field_11518.method_10559() || id == class_2562.field_11519.method_10559())) {
         class_1600 client = class_1600.method_2965();
         if (client != null && client.field_10310 != null) {
            double dx = x - client.field_10310.field_3252;
            double dy = y - client.field_10310.field_3253;
            double dz = z - client.field_10310.field_3254;
            double reach = client.field_10310.field_3206 / 2.0 + 0.6;
            if (dx * dx + dz * dz <= reach * reach && dy >= -0.6 && dy <= client.field_10310.field_3207 + 0.6) {
               cir.setReturnValue(null);
            }
         }
      }
   }

   @Inject(method = "method_9701", at = @At("HEAD"), cancellable = true)
   private void client$multiplyParticles(
      int id, double x, double y, double z, double vx, double vy, double vz, int[] args, CallbackInfoReturnable<class_502> cir
   ) {
      if (!client$spawningExtra && !CustomParticles.bypass && ClientConfig.customParticles) {
         boolean crit = id == class_2562.field_11512.method_10559();
         boolean sharp = id == class_2562.field_11513.method_10559();
         if (crit || sharp) {
            float mult = crit ? ClientConfig.critParticleMultiplier : ClientConfig.sharpParticleMultiplier;
            if (mult != 1.0F) {
               if (mult < 1.0F) {
                  if (CLIENT_RANDOM.nextFloat() > mult) {
                     cir.setReturnValue(null);
                  }
               } else {
                  float carry = crit ? client$critCarry : client$sharpCarry;
                  carry += mult - 1.0F;
                  int extra = (int)carry;
                  carry -= extra;
                  if (crit) {
                     client$critCarry = carry;
                  } else {
                     client$sharpCarry = carry;
                  }

                  if (extra > 6) {
                     extra = 6;
                  }

                  if (extra > 0) {
                     client$spawningExtra = true;

                     try {
                        class_503 self = (class_503)(Object)this;

                        for (int i = 0; i < extra; i++) {
                           double jx = x + (CLIENT_RANDOM.nextDouble() - 0.5) * 0.6;
                           double jy = y + (CLIENT_RANDOM.nextDouble() - 0.5) * 0.4;
                           double jz = z + (CLIENT_RANDOM.nextDouble() - 0.5) * 0.6;
                           self.method_9701(id, jx, jy, jz, vx, vy, vz, args);
                        }
                     } finally {
                        client$spawningExtra = false;
                     }
                  }
               }
            }
         }
      }
   }
}
