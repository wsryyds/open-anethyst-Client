package net.client.mixin;

import net.client.util.ServerListDrag;
import net.minecraft.class_390;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_390.class)
public class ListWidgetMouseMixin {
   @Inject(method = "method_9529", at = @At("HEAD"), cancellable = true)
   private void client$serverDragWheelOnly(CallbackInfo ci) {
      if (ServerListDrag.isActive()) {
         if (Mouse.getEventDWheel() == 0) {
            ci.cancel();
         } else {
            ((class_390)(Object)this).method_6705(false);
         }
      }
   }
}
