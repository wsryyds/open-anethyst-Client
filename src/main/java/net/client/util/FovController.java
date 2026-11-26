package net.client.util;

import net.client.ClientConfig;
import net.minecraft.class_1600;
import net.minecraft.class_1631;
import net.minecraft.class_1734;
import net.minecraft.class_321;
import net.minecraft.class_63;
import net.minecraft.class_860;
import net.minecraft.class_861;
import net.minecraft.class_864;

public final class FovController {
   private static final float SMOOTH_HALF_LIFE = 0.12F;
   private static float current = Float.NaN;
   private static long lastNanos;

   private FovController() {
   }

   public static void reset() {
      current = Float.NaN;
      lastNanos = 0L;
   }

   public static float apply(class_1600 client, float tickDelta) {
      float target = target(client);
      long now = System.nanoTime();
      float dt = lastNanos == 0L ? 0.0F : (float)(now - lastNanos) / 1.0E9F;
      lastNanos = now;
      if (dt < 0.0F) {
         dt = 0.0F;
      }

      if (!Float.isNaN(current) && ClientConfig.fovSmooth && !(dt > 0.5F)) {
         float alpha = 1.0F - (float)Math.pow(0.5, dt / 0.12F);
         current = current + (target - current) * alpha;
         if (Math.abs(target - current) < 0.01F) {
            current = target;
         }
      } else {
         current = target;
      }

      float fov = current;
      if (fov < ClientConfig.fovMin) {
         fov = ClientConfig.fovMin;
      } else if (fov > ClientConfig.fovMax) {
         fov = ClientConfig.fovMax;
      }

      return fov;
   }

   private static float target(class_1600 client) {
      float base = ClientConfig.fovBase * ClientConfig.fovGlobal;
      float best = base;
      float bestDelta = 0.0F;
      class_864 camera = client.method_9388();
      class_1631 player = (class_1631)(camera instanceof class_1631 ? (class_1631)camera : client.field_10310);
      if (player != null) {
         if (ClientConfig.fovSprintOn && player.method_2514()) {
            best = pick(base, base, bestDelta, ClientConfig.fovSprint);
            bestDelta = Math.abs(best - base);
         }

         if (ClientConfig.fovSpeedOn) {
            class_861 speed = player.method_6131(class_860.field_3166);
            if (speed != null) {
               best = pick(base, best, bestDelta, ClientConfig.fovSpeed);
               bestDelta = Math.abs(best - base);
            }
         }

         if (ClientConfig.fovSlowOn) {
            class_861 slow = player.method_6131(class_860.field_3167);
            if (slow != null) {
               best = pick(base, best, bestDelta, ClientConfig.fovSlow);
               bestDelta = Math.abs(best - base);
            }
         }

         if (ClientConfig.fovFlyOn && player.field_3998.field_3958) {
            best = pick(base, best, bestDelta, ClientConfig.fovFly);
            bestDelta = Math.abs(best - base);
         }

         if (ClientConfig.fovAimOn && player.method_3193() && player.method_3191().method_3421() == class_1734.field_7125) {
            best = pick(base, best, bestDelta, ClientConfig.fovAim);
            bestDelta = Math.abs(best - base);
         }
      }

      if (ClientConfig.fovWaterOn
         && client.field_3803 != null
         && camera != null
         && class_321.method_9371(client.field_3803, camera, 0.0F).method_6456() == class_63.field_129) {
         best = pick(base, best, bestDelta, ClientConfig.fovWater);
      }

      return best;
   }

   private static float pick(float base, float best, float bestDelta, float candidate) {
      return Math.abs(candidate - base) > bestDelta ? candidate : best;
   }
}
