package net.client.mixin;

import net.minecraft.class_1699;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_1699.class)
public interface LivingEntitySwingAccessor {
   @Invoker("method_6143")
   int client$swingDuration();
}
