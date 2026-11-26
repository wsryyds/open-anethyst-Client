package com.amethyst.blur.mixin;

import com.amethyst.blur.Blur;
import net.minecraft.class_1871;
import net.minecraft.class_1872;
import net.minecraft.class_1873;
import net.minecraft.class_1877;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1872.class)
public class ShaderEffectMixin {
   @Inject(method = "method_6945", at = @At("HEAD"))
   private void blur$updateUniforms(float tickDelta, CallbackInfo ci) {
      Blur blur = Blur.getInstance();
      if (blur != null) {
         for (class_1873 pass : ((ShaderEffectAccessor)this).blur$getPasses()) {
            class_1871 program = ((PostProcessShaderAccessor)pass).blur$getProgram();
            class_1877 radius = program.method_6933("Radius");
            if (radius != null) {
               radius.method_6976(blur.getRadius());
            }

            class_1877 progress = program.method_6933("Progress");
            if (progress != null) {
               progress.method_6976(Blur.getProgress());
            }
         }
      }
   }
}
