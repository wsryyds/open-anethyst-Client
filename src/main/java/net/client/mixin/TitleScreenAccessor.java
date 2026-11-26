package net.client.mixin;

import net.minecraft.class_356;
import net.minecraft.class_624;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_624.class)
public interface TitleScreenAccessor {
   @Accessor("field_10468")
   class_356 client$realmsButton();
}
