package net.client.mixin;

import net.client.replay.ReplayClient;
import net.minecraft.class_1845;
import net.minecraft.class_1982;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1845.class)
public class ReplayClientPlayNetworkHandlerMixin {
   @Inject(method = "method_7456", at = @At("TAIL"))
   private void client$onDisconnected(class_1982 reason, CallbackInfo ci) {
      if (ReplayClient.getRecordingManager() != null && ReplayClient.getRecordingManager().isActive()) {
         ReplayClient.getRecordingManager().stop();
      }
   }
}
