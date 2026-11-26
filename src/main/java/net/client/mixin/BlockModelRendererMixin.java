package net.client.mixin;

import net.client.ClientConfig;
import net.minecraft.class_1158;
import net.minecraft.class_1752;
import net.minecraft.class_197;
import net.minecraft.class_2232;
import net.minecraft.class_2432;
import net.minecraft.class_2528;
import net.minecraft.class_2552;
import net.minecraft.class_520;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_2432.class)
public class BlockModelRendererMixin {
   @Inject(method = "method_9963", at = @At("HEAD"), cancellable = true)
   private void client$hideGlass(
      class_1158 world, class_2528 model, class_2232 state, class_2552 pos, class_520 buffer, boolean cull, CallbackInfoReturnable<Boolean> cir
   ) {
      int mode = ClientConfig.glassMode;
      if (mode > 0) {
         class_197 block = state.method_9028();
         boolean clear = block == class_1752.field_7441 || block == class_1752.field_7326;
         boolean stained = block == class_1752.field_7420 || block == class_1752.field_7421;
         if (clear || mode == 1 && stained) {
            cir.setReturnValue(false);
         }
      }
   }
}
