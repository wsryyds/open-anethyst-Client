package net.client.mixin;

import net.client.replay.ReplayClient;
import net.client.replay.session.ReplayView;
import net.minecraft.class_1600;
import net.minecraft.class_1631;
import net.minecraft.class_2344;
import net.minecraft.class_570;
import net.minecraft.class_864;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_570.class)
public class ReplayPlayerEntityRendererMixin {
   @Redirect(method = "method_1581", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1631;method_11243()Z"))
   private boolean client$keepReplayPlayerVisible(class_1631 player) {
      boolean mainPlayer = player.method_11243();
      if (!mainPlayer) {
         return false;
      } else if (!ReplayClient.isReplayActive() && !ReplayClient.isReplayCaptureActive()) {
         return true;
      } else {
         class_1600 client = class_1600.method_2965();
         if (client != null && client.field_10310 == player && client.field_3823 != null) {
            class_864 camera = client.method_9388();
            return ReplayView.shouldHideLocalPlayer(player);
         } else {
            return true;
         }
      }
   }

   @Inject(method = "method_10307", at = @At("TAIL"))
   private void client$forceSkinLayers(class_1631 player, CallbackInfo ci) {
      if (ReplayClient.isReplayActive() || ReplayClient.isReplayCaptureActive()) {
         class_1600 client = class_1600.method_2965();
         if (client != null && client.field_10310 == player) {
            class_2344 model = ((class_570)(Object)this).method_10308();
            model.field_1474.field_1604 = true;
            model.field_10564.field_1604 = true;
            model.field_10560.field_1604 = true;
            model.field_10561.field_1604 = true;
            model.field_10562.field_1604 = true;
            model.field_10563.field_1604 = true;
         }
      }
   }
}
