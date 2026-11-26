package net.client.mixin;

import net.minecraft.class_2294;
import net.minecraft.class_371;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_371.class)
public interface InGameHudAccessor {
   @Accessor("field_10344")
   class_2294 client$debugHud();
}
