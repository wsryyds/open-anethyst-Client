package net.client.mixin;

import net.client.ime.ImeBridge;
import net.minecraft.class_367;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_367.class)
public class TextFieldWidgetMixin {
   @Inject(method = "method_937", at = @At("TAIL"))
   private void client$imeRender(CallbackInfo ci) {
      try {
         ImeBridge.onFieldRender((class_367)(Object)this);
      } catch (Throwable var3) {
      }
   }

   @Inject(method = "method_917", at = @At("HEAD"), cancellable = true)
   private void client$imeKey(char typedChar, int keyCode, CallbackInfoReturnable<Boolean> cir) {
      try {
         if (ImeBridge.handleKey(typedChar, keyCode)) {
            cir.setReturnValue(Boolean.TRUE);
         }
      } catch (Throwable var5) {
      }
   }
}
