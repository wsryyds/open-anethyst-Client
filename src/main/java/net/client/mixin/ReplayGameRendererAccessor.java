package net.client.mixin;

import net.minecraft.class_524;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_524.class)
public interface ReplayGameRendererAccessor {
   @Accessor("field_10695")
   boolean client$getRenderHand();

   @Accessor("field_10695")
   void client$setRenderHand(boolean var1);
}
