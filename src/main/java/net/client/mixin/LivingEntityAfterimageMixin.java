package net.client.mixin;

import net.client.ClientConfig;
import net.client.util.AfterimageHandler;
import net.minecraft.class_1600;
import net.minecraft.class_1699;
import net.minecraft.class_864;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1699.class)
public class LivingEntityAfterimageMixin {
   @Inject(method = "method_2485", at = @At("HEAD"))
   private void client$onStatus(byte status, CallbackInfo ci) {
      if (status == 2 && ClientConfig.afterimage) {
         class_1699 self = (class_1699)(Object)this;
         class_1600 mc = class_1600.method_2965();
         if (mc != null) {
            class_864 camera = mc.method_9388();
            if (self != camera || ClientConfig.afterimageSelf) {
               AfterimageHandler.addAfterimage(self);
            }
         }
      }
   }
}
