package net.client.mixin;

import net.client.ClientConfig;
import net.minecraft.class_1600;
import net.minecraft.class_345;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_345.class)
public class MouseInputMixin {
   @Shadow
   public int field_931;
   @Shadow
   public int field_932;

   @Inject(method = "method_863", at = @At("RETURN"))
   private void client$rawMouse(CallbackInfo ci) {
      if (ClientConfig.rawInput) {
         class_1600 mc = class_1600.method_2965();
         if (mc != null && mc.field_3823 != null) {
            float f = mc.field_3823.field_972 * 0.6F + 0.2F;
            if (!(f <= 0.0F)) {
               float ratio = 1.0F / (4.0F * f * f);
               this.field_931 = Math.round(this.field_931 * ratio);
               this.field_932 = Math.round(this.field_932 * ratio);
            }
         }
      }
   }
}
