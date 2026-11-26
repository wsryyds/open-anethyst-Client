package net.client.mixin;

import net.minecraft.class_1635;
import net.minecraft.class_1699;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_1635.class)
public interface LivingEntityRendererScaleAccessor {
   @Invoker("method_5775")
   void client$scale(class_1699 var1, float var2);
}
