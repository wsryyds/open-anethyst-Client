package net.client.mixin;

import net.client.ClientConfig;
import net.client.ClientScreen;
import net.minecraft.class_1600;
import net.minecraft.class_356;
import net.minecraft.class_385;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_385.class)
public abstract class GameMenuScreenMixin {
   @Inject(method = "method_1044", at = @At("TAIL"))
   private void client$addSettingsButton(CallbackInfo ci) {
      ScreenAccessor screen = (ScreenAccessor)this;
      int x = screen.client$width() / 2 - 100;
      int y = screen.client$height() / 4 + 56;
      screen.client$buttons().add(new class_356(8, x, y, 200, 20, ClientConfig.t("客户端设置", "Client settings")));
   }

   @Inject(method = "method_1027", at = @At("HEAD"), cancellable = true)
   private void client$openSettings(class_356 button, CallbackInfo ci) {
      class_1600 mc = class_1600.method_2965();
      if (button.field_1054 == 8 && mc != null) {
         mc.method_2928(new ClientScreen());
         ci.cancel();
      }
   }
}
