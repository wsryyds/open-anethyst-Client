package net.client.util;

import net.client.ClientConfig;
import net.minecraft.class_1600;
import net.minecraft.class_1631;
import net.minecraft.class_837;
import net.minecraft.class_864;
import org.lwjgl.input.Keyboard;

public final class FreelookHandler {
   private static float yawOffset;
   private static float pitchOffset;
   private static float bodyYaw;
   private static float bodyPitch;
   private static float seenYaw;
   private static float seenPitch;
   private static boolean holding;

   private FreelookHandler() {
   }

   public static boolean isHolding() {
      return holding;
   }

   public static void tick() {
      class_1600 mc = class_1600.method_2965();
      boolean wanted = mc != null
         && mc.field_10310 != null
         && mc.field_3803 != null
         && mc.field_3816 == null
         && ClientConfig.freelook
         && Keyboard.isKeyDown(ClientConfig.freelookKey);
      if (!wanted) {
         release();
      } else if (!holding) {
         holding = true;
         class_1631 player = mc.field_10310;
         bodyYaw = player.field_3258;
         bodyPitch = player.field_3193;
         yawOffset = 0.0F;
         pitchOffset = 0.0F;
         seenYaw = bodyYaw;
         seenPitch = bodyPitch;
      }
   }

   private static void release() {
      if (holding) {
         holding = false;
         class_1600 mc = class_1600.method_2965();
         if (mc != null && mc.field_10310 != null) {
            mc.field_10310.field_3258 = bodyYaw;
            mc.field_10310.field_3194 = bodyYaw;
            mc.field_10310.field_3193 = bodyPitch;
            mc.field_10310.field_3195 = bodyPitch;
         }

         yawOffset = 0.0F;
         pitchOffset = 0.0F;
      }
   }

   public static void applyCamera() {
      if (holding) {
         class_1600 mc = class_1600.method_2965();
         class_864 camera = mc == null ? null : mc.method_9388();
         if (camera != null) {
            if (mc.field_10310 != null) {
               class_1631 player = mc.field_10310;
               float gainedYaw = class_837.method_2353(player.field_3258 - seenYaw);
               float gainedPitch = player.field_3193 - seenPitch;
               yawOffset = class_837.method_2353(yawOffset + gainedYaw);
               pitchOffset = class_837.method_2336(pitchOffset + gainedPitch, -90.0F, 90.0F);
               player.field_3258 = bodyYaw;
               player.field_3193 = bodyPitch;
               player.field_3194 = bodyYaw;
               player.field_3195 = bodyPitch;
               seenYaw = bodyYaw;
               seenPitch = bodyPitch;
            }

            float viewYaw = class_837.method_2353(bodyYaw + yawOffset);
            float viewPitch = class_837.method_2336(bodyPitch + pitchOffset, -90.0F, 90.0F);
            camera.field_3258 = viewYaw;
            camera.field_3194 = viewYaw;
            camera.field_3193 = viewPitch;
            camera.field_3195 = viewPitch;
         }
      }
   }

   public static void restoreCamera() {
      if (holding) {
         class_1600 mc = class_1600.method_2965();
         class_864 camera = mc == null ? null : mc.method_9388();
         if (camera != null) {
            camera.field_3258 = bodyYaw;
            camera.field_3194 = bodyYaw;
            camera.field_3193 = bodyPitch;
            camera.field_3195 = bodyPitch;
         }
      }
   }
}
