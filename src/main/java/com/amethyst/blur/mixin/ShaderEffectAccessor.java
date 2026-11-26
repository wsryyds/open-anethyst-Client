package com.amethyst.blur.mixin;

import java.util.List;
import net.minecraft.class_1872;
import net.minecraft.class_1873;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1872.class)
public interface ShaderEffectAccessor {
   @Accessor("field_8041")
   List<class_1873> blur$getPasses();
}
