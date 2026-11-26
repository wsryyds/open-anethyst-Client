package net.client.mixin;

import net.minecraft.class_1600;
import net.minecraft.class_355;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1600.class)
public interface SessionAccessor {
   @Mutable
   @Accessor("field_3808")
   void client$setSession(class_355 var1);
}
