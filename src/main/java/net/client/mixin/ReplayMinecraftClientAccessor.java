package net.client.mixin;

import net.minecraft.class_1600;
import net.minecraft.class_354;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1600.class)
public interface ReplayMinecraftClientAccessor {
   @Accessor("field_3778")
   class_354 client$getTicker();
}
