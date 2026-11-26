package net.client.mixin;

import net.client.util.CustomParticles;
import net.minecraft.class_1600;
import net.minecraft.class_864;
import net.minecraft.class_988;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_988.class)
public class PlayerAttackParticleMixin {
   @Inject(method = "method_3216", at = @At("RETURN"))
   private void client$attackParticles(class_864 target, CallbackInfo ci) {
      class_1600 mc = class_1600.method_2965();
      if (mc != null && mc.field_10310 != null && (Object)this == mc.field_10310) {
         CustomParticles.onLocalAttack(target);
      }
   }
}
