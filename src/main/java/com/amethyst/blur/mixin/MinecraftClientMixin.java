package com.amethyst.blur.mixin;

import com.amethyst.blur.Blur;
import net.minecraft.class_1600;
import net.minecraft.class_388;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1600.class)
public class MinecraftClientMixin {
   @Inject(method = "method_2928", at = @At("HEAD"))
   private void blur$onScreenChange(class_388 screen, CallbackInfo ci) {
      Blur.onScreenChange(screen);
   }

   @Inject(method = "method_5576", at = @At("HEAD"))
   private void blur$ensureResourcePack(CallbackInfo ci) {
      Blur.ensureResourcePack((class_1600)(Object)this);
   }
}
