package net.client.mixin;

import net.client.util.CustomParticles;
import net.minecraft.class_1600;
import net.minecraft.class_477;
import net.minecraft.class_864;
import net.minecraft.class_988;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_477.class)
public class AttackEntityParticleMixin {
   @Inject(method = "method_1227", at = @At("RETURN"))
   private void client$attackEntityParticles(class_988 player, class_864 target, CallbackInfo ci) {
      class_1600 mc = class_1600.method_2965();
      if (mc != null && player != null && player == mc.field_10310) {
         CustomParticles.onLocalAttack(target);
      }
   }
}
