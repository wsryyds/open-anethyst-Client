package com.amethyst.blur.mixin;

import java.util.List;
import net.minecraft.class_1600;
import net.minecraft.class_1655;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1600.class)
public interface MinecraftClientAccessor {
   @Accessor("field_6275")
   List<class_1655> blur$getResourcePacks();
}
