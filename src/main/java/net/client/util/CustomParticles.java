package net.client.util;

import java.util.Random;
import net.client.ClientConfig;
import net.minecraft.class_1600;
import net.minecraft.class_2562;
import net.minecraft.class_864;

public final class CustomParticles {
   public static boolean bypass = false;
   private static final Random RANDOM = new Random();
   private static class_864 lastTarget;
   private static long lastAttackNanos;

   private CustomParticles() {
   }

   public static void onLocalAttack(class_864 target) {
      if (target != null && ClientConfig.customParticles) {
         long now = System.nanoTime();
         if (target != lastTarget || now - lastAttackNanos >= 2000000L) {
            lastTarget = target;
            lastAttackNanos = now;
            class_1600 mc = class_1600.method_2965();
            if (mc != null && mc.field_3807 != null) {
               int crit = Math.round(10.0F * Math.max(0.0F, ClientConfig.critParticleMultiplier));
               int sharp = Math.round(8.0F * Math.max(0.0F, ClientConfig.sharpParticleMultiplier));
               if (crit > 0 || sharp > 0) {
                  bypass = true;

                  try {
                     emit(mc, class_2562.field_11512, target, crit);
                     emit(mc, class_2562.field_11513, target, sharp);
                  } finally {
                     bypass = false;
                  }
               }
            }
         }
      }
   }

   private static void emit(class_1600 mc, class_2562 type, class_864 target, int count) {
      if (count > 0) {
         if (count > 60) {
            count = 60;
         }

         for (int i = 0; i < count; i++) {
            double px = target.field_3252 + (RANDOM.nextDouble() - 0.5) * (target.field_3206 + 0.4);
            double py = target.field_3253 + RANDOM.nextDouble() * target.field_3207;
            double pz = target.field_3254 + (RANDOM.nextDouble() - 0.5) * (target.field_3206 + 0.4);
            double vx = (RANDOM.nextDouble() - 0.5) * 0.8;
            double vy = RANDOM.nextDouble() * 0.6 + 0.2;
            double vz = (RANDOM.nextDouble() - 0.5) * 0.8;
            mc.field_3807.method_9701(type.method_10559(), px, py, pz, vx, vy, vz);
         }
      }
   }
}
