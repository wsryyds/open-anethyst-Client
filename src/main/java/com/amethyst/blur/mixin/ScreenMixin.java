package com.amethyst.blur.mixin;

import com.amethyst.blur.Blur;
import net.minecraft.class_1600;
import net.minecraft.class_388;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_388.class)
public class ScreenMixin {
   @Inject(method = "method_1031", at = @At("HEAD"), cancellable = true)
   private void blur$renderBackground(int alpha, CallbackInfo ci) {
      Blur.bootstrap();
      class_1600 client = class_1600.method_2965();
      if (client != null && client.field_3803 != null) {
         class_388 self = (class_388)(Object)this;
         ((DrawableHelperInvoker)this).blur$fillGradient(0, 0, self.field_1230, self.field_1231, Blur.getBackgroundColor(true), Blur.getBackgroundColor(false));
         ci.cancel();
      }
   }
}
