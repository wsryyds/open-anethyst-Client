package net.client.util;

import net.client.ClientConfig;
import net.client.mixin.LivingEntitySwingAccessor;
import net.minecraft.class_1600;
import net.minecraft.class_988;
import net.minecraft.class_234.class_235;

public final class OldBlockHitSwing {
   private OldBlockHitSwing() {
   }

   public static void keepAlive(class_1600 mc) {
      if (mc != null && ClientConfig.blockHitAnim) {
         class_988 player = mc.field_10310;
         if (player != null
            && mc.field_3823 != null
            && player.method_3193()
            && mc.field_3823.field_941.method_6619()
            && mc.field_3822 != null
            && mc.field_3822.field_595 == class_235.field_7609) {
            int fullTicks = ((LivingEntitySwingAccessor)player).client$swingDuration();
            int halfTicks = Math.max(1, fullTicks / 2);
            boolean notStarted = !player.field_6769 || player.field_6770 < 0;
            boolean pastHalf = player.field_6770 >= halfTicks;
            if (notStarted || pastHalf) {
               player.field_6770 = -1;
               player.field_6769 = true;
            }
         }
      }
   }
}
