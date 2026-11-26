package net.client.mixin;

import net.client.ClientConfig;
import net.minecraft.class_864;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_864.class)
public class EntitySprintParticleMixin {
   @Inject(method = "method_10929", at = @At("HEAD"), cancellable = true)
   private void client$skipSprintParticles(CallbackInfo ci) {
      if (ClientConfig.hideSprintParticles) {
         ci.cancel();
      }
   }
}
