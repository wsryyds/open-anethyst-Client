package net.client.mixin;

import net.minecraft.class_1825;
import net.minecraft.class_372;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_1825.class)
public class ServerEntryOverlayMixin {
   @Redirect(method = "method_6700", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_372;method_988(IIIII)V"))
   private void client$skipHoverBackdrop(int x1, int y1, int x2, int y2, int color) {
   }

   @Redirect(method = "method_6700", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_372;method_6674(IIFFIIFF)V"))
   private static void client$skipSortArrows(int x, int y, float u, float v, int width, int height, float texWidth, float texHeight) {
      if (width != 32 || height != 32) {
         class_372.method_6674(x, y, u, v, width, height, texWidth, texHeight);
      }
   }
}
