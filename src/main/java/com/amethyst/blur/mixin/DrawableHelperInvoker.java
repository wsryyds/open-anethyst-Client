package com.amethyst.blur.mixin;

import net.minecraft.class_372;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_372.class)
public interface DrawableHelperInvoker {
   @Invoker("method_989")
   void blur$fillGradient(int var1, int var2, int var3, int var4, int var5, int var6);
}
