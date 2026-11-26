package net.client.mixin;

import net.minecraft.class_864;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_864.class)
public interface EntityFlagInvoker {
   @Invoker("method_2551")
   boolean client$getFlag(int var1);
}
