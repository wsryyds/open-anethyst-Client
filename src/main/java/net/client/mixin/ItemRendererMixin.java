package net.client.mixin;

import net.client.ClientConfig;
import net.minecraft.class_2528;
import net.minecraft.class_560;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = class_560.class, priority = 1200)
public abstract class ItemRendererMixin {
   @Shadow
   private void method_10235(class_2528 model, int color) {
      throw new AssertionError("shadow");
   }

   @Redirect(method = "method_10234", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_560;method_10235(Lnet/minecraft/class_2528;I)V"))
   private void client$glintColor(class_560 instance, class_2528 model, int color) {
      this.method_10235(model, ClientConfig.customGlint ? ClientConfig.glintColor : color);
   }
}
