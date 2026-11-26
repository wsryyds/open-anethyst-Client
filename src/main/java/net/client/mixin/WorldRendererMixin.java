package net.client.mixin;

import net.client.ClientConfig;
import net.client.util.BlockHighlightRenderer;
import net.minecraft.class_1600;
import net.minecraft.class_234;
import net.minecraft.class_236;
import net.minecraft.class_2403;
import net.minecraft.class_478;
import net.minecraft.class_530;
import net.minecraft.class_864;
import net.minecraft.class_988;
import net.minecraft.class_234.class_235;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_530.class, priority = 1200)
public class WorldRendererMixin {
   @Inject(method = "method_1380", at = @At("HEAD"), cancellable = true)
   private void client$blockHighlight(class_988 player, class_234 hit, int mode, float tickDelta, CallbackInfo ci) {
      if (ClientConfig.highlightBlock) {
         if (class_1600.method_2965().field_3816 != null) {
            ci.cancel();
            return;
         }

         if (hit != null && hit.field_595 == class_235.field_7609) {
            ci.cancel();
            BlockHighlightRenderer.render(hit, tickDelta);
         }
      }
   }

   @Redirect(
      method = "method_9891",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_478;method_3631(Lnet/minecraft/class_864;F)Lnet/minecraft/class_236;")
   )
   private class_236 client$skyFollowFog(class_478 world, class_864 entity, float delta) {
      class_236 vanilla = world.method_3631(entity, delta);
      if (!ClientConfig.fogModify) {
         return vanilla;
      } else {
         int c = ClientConfig.fogColor;
         return new class_236((c >> 16 & 0xFF) / 255.0, (c >> 8 & 0xFF) / 255.0, (c & 0xFF) / 255.0);
      }
   }

   @Inject(method = "method_9891", at = @At("HEAD"), cancellable = true)
   private void client$skyOverlay(float tickDelta, int anaglyphFilter, CallbackInfo ci) {
      if (ClientConfig.fogModify) {
         int c = ClientConfig.fogColor;
         float r = (c >> 16 & 0xFF) / 255.0F;
         float g = (c >> 8 & 0xFF) / 255.0F;
         float b = (c & 0xFF) / 255.0F;
         GL11.glDisable(3089);
         GL11.glColorMask(true, true, true, true);
         class_2403.method_9801(r, g, b, 1.0F);
         GL11.glClear(16384);
         ci.cancel();
      }
   }
}
