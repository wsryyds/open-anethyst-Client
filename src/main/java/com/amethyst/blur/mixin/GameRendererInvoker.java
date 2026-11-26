package com.amethyst.blur.mixin;

import net.minecraft.class_1653;
import net.minecraft.class_524;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_524.class)
public interface GameRendererInvoker {
   @Invoker("method_9778")
   void blur$loadShader(class_1653 var1);
}
