package net.client.mixin;

import net.client.replay.ReplayClient;
import net.client.replay.command.ReplayCommand;
import net.minecraft.class_518;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_518.class)
public class ReplayClientPlayerEntityMixin {
   @Inject(method = "method_2513", at = @At("HEAD"), cancellable = true)
   private void client$isSneaking(CallbackInfoReturnable<Boolean> cir) {
      if (ReplayClient.isReplayActive() || ReplayClient.isReplayCaptureActive()) {
         boolean sneaking = ((EntityFlagInvoker)this).client$getFlag(1);
         if (Boolean.getBoolean("client.replay.soundDebug")) {
            System.out.println("[Sneak] replay isSneaking=" + sneaking);
         }

         cir.setReturnValue(sneaking);
      }
   }

   @Inject(method = "method_9720", at = @At("HEAD"), cancellable = true)
   private void client$sendChatMessage(String message, CallbackInfo ci) {
      if (ReplayCommand.handle(message)) {
         ci.cancel();
      }
   }
}
