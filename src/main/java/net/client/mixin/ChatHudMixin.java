package net.client.mixin;

import net.client.ClientConfig;
import net.client.util.HudFill;
import net.minecraft.class_357;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_357.class, priority = 1200)
public class ChatHudMixin {
   @Redirect(method = "method_896", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_357;method_988(IIIII)V", ordinal = 0))
   private static void client$chatBackground(int x1, int y1, int x2, int y2, int color) {
      if (ClientConfig.chatBackground) {
         int fade = color >>> 24 & 0xFF;
         int picked = ClientConfig.chatBgColor >>> 24 & 0xFF;
         int alpha = (int)Math.min(255.0F, picked * fade / 127.0F);
         color = alpha << 24 | ClientConfig.chatBgColor & 16777215;
      }

      HudFill.fill(x1, y1, x2, y2, color);
   }

   @Inject(method = "method_896", at = @At("HEAD"), cancellable = true, require = 0)
   private void client$hideChat(int ticks, CallbackInfo ci) {
      if (!ClientConfig.chatVisible) {
         ci.cancel();
      }
   }
}
