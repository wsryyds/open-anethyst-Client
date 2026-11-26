package net.client.mixin;

import java.util.Iterator;
import java.util.List;
import net.minecraft.class_356;
import net.minecraft.class_383;
import net.minecraft.class_347.class_350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_383.class)
public class SettingsScreenMixin {
   private static final int[] KEEP_ORDER = new int[]{110, 8675309, 106, 101, 100, 102, 103, 105};

   @Inject(method = "method_1044", at = @At("TAIL"))
   private void client$trim(CallbackInfo ci) {
      class_383 self = (class_383)(Object)this;
      List<class_356> buttons = ((ScreenAccessor)self).client$buttons();
      if (buttons != null) {
         int realmsId = class_350.field_10336.method_882();
         Iterator<class_356> it = buttons.iterator();

         while (it.hasNext()) {
            class_356 button = it.next();
            String label = button.field_1053 == null ? "" : button.field_1053;
            if (button.field_1054 == 107 || button.field_1054 == 104 || button.field_1054 == realmsId || label.contains("Realms") || label.contains("领域")) {
               it.remove();
            }
         }

         int startX = self.field_1230 / 2 - 155;
         int startY = self.field_1231 / 6 + 48 - 6;

         for (int i = 0; i < KEEP_ORDER.length; i++) {
            class_356 found = null;

            for (class_356 button : buttons) {
               if (button.field_1054 == KEEP_ORDER[i]) {
                  found = button;
                  break;
               }
            }

            if (found != null) {
               found.field_1051 = startX + i % 2 * 160;
               found.field_1052 = startY + 24 * (i / 2);
            }
         }

         for (class_356 buttonx : buttons) {
            if (buttonx.field_1054 == 200) {
               buttonx.field_1052 = startY + 24 * ((KEEP_ORDER.length + 1) / 2) + 6;
            }
         }
      }
   }
}
