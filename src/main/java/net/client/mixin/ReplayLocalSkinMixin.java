package net.client.mixin;

import net.client.replay.ReplayClient;
import net.client.replay.session.ReplayLocalSkin;
import net.minecraft.class_1631;
import net.minecraft.class_1653;
import net.minecraft.class_570;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_570.class)
public class ReplayLocalSkinMixin {
   @Inject(method = "method_5797", at = @At("HEAD"), cancellable = true)
   private void client$replayLocalSkin(class_1631 player, CallbackInfoReturnable<class_1653> cir) {
      if (ReplayClient.isReplayActive() || ReplayClient.isReplayCaptureActive()) {
         if (ReplayLocalSkin.hasSkin()) {
            if (ReplayLocalSkin.isLocal(player)) {
               cir.setReturnValue(ReplayLocalSkin.skin());
            }
         }
      }
   }
}
