package net.client.mixin;

import net.client.ClientConfig;
import net.client.ui.ClientTheme;
import net.client.ui.ScreenRenderFlag;
import net.client.util.ServerListDrag;
import net.minecraft.class_1600;
import net.minecraft.class_2403;
import net.minecraft.class_372;
import net.minecraft.class_376;
import net.minecraft.class_388;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_388.class)
public class ScreenMixin {
   @Inject(method = "method_1043", at = @At("HEAD"), cancellable = true)
   private void client$overlayBackdrop(CallbackInfo ci) {
      this.client$paintOverlay(ci);
   }

   @Inject(method = "method_1031", at = @At("HEAD"), cancellable = true)
   private void client$overlayBackdropInt(int tint, CallbackInfo ci) {
      this.client$paintOverlay(ci);
   }

   @Inject(method = "method_1034", at = @At("HEAD"), cancellable = true)
   private void client$overlayDirt(int tint, CallbackInfo ci) {
      this.client$paintOverlay(ci);
   }

   @Inject(method = "method_1025", at = @At("HEAD"))
   private void client$enterScreenRender(int mouseX, int mouseY, float delta, CallbackInfo ci) {
      ScreenRenderFlag.begin();
   }

   @Inject(method = "method_1025", at = @At("RETURN"))
   private void client$exitScreenRender(int mouseX, int mouseY, float delta, CallbackInfo ci) {
      ScreenRenderFlag.end();
   }

   @Inject(method = "method_4259", at = @At("HEAD"), cancellable = true)
   private void client$serverListDrag(int mouseX, int mouseY, int button, long time, CallbackInfo ci) {
      if ((Object)this instanceof class_376 && ServerListDrag.onDrag((class_376)(Object)this, mouseX, mouseY, button)) {
         ci.cancel();
      }
   }

   private void client$paintOverlay(CallbackInfo ci) {
      class_1600 mc = class_1600.method_2965();
      if (mc != null) {
         int w = ((ScreenAccessor)this).client$width();
         int h = ((ScreenAccessor)this).client$height();
         if (w > 0 && h > 0) {
            if (mc.field_3803 == null && ClientConfig.uiOverride) {
               int surface = ClientTheme.get().colors.surface;
               class_2403.method_9856();
               class_2403.method_9843();
               class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
               class_372.method_988(0, 0, w, h, surface);
               class_2403.method_9855();
               class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
               ci.cancel();
            }
         }
      }
   }
}
