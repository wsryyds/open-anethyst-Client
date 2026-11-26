package net.client.mixin;

import net.client.util.ServerListDrag;
import net.client.util.ServerListPingFix;
import net.minecraft.class_1827;
import net.minecraft.class_376;
import net.minecraft.class_486;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_376.class)
public class MultiplayerScreenMixin {
   @Shadow
   private class_1827 field_7824;
   @Shadow
   private class_486 field_1186;

   @Inject(method = "method_1044", at = @At("TAIL"))
   private void client$initServerFixes(CallbackInfo ci) {
      ServerListDrag.reset();
      if (this.field_1186 != null) {
         ServerListPingFix.configure(this.field_1186.method_1281());
      }
   }

   @Inject(method = "method_6785", at = @At("TAIL"))
   private void client$refreshServerFixes(CallbackInfo ci) {
      if (this.field_1186 != null) {
         ServerListPingFix.configure(this.field_1186.method_1281());
      }
   }

   @Inject(method = "method_1025", at = @At("TAIL"))
   private void client$dragGhost(int mouseX, int mouseY, float delta, CallbackInfo ci) {
      ServerListDrag.renderGhost(mouseX, mouseY);
   }

   @Inject(method = "method_1026", at = @At("HEAD"))
   private void client$serverDragPress(int mouseX, int mouseY, int button, CallbackInfo ci) {
      ServerListDrag.onPress((class_376)(Object)this, this.field_7824, this.field_1186, mouseX, mouseY, button);
   }

   @Inject(method = "method_1032", at = @At("HEAD"), cancellable = true)
   private void client$serverDragRelease(int mouseX, int mouseY, int button, CallbackInfo ci) {
      if (ServerListDrag.onRelease((class_376)(Object)this, mouseX, mouseY, button)) {
         ci.cancel();
      }
   }
}
