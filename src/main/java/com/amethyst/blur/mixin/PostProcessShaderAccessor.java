package com.amethyst.blur.mixin;

import net.minecraft.class_1871;
import net.minecraft.class_1873;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1873.class)
public interface PostProcessShaderAccessor {
   @Accessor("field_8051")
   class_1871 blur$getProgram();
}
