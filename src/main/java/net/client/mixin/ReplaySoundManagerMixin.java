package net.client.mixin;

import net.client.replay.ReplayClient;
import net.minecraft.class_1900;
import net.minecraft.class_1909;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1909.class)
public abstract class ReplaySoundManagerMixin {
   @Inject(method = "method_7121", at = @At("HEAD"), cancellable = true)
   private void client$recordSound(class_1900 sound, CallbackInfo ci) {
      if (ReplayClient.isReplayCaptureActive()) {
         ci.cancel();
      } else {
         if (Boolean.getBoolean("client.replay.soundDebug")) {
            System.out.println("[SoundHook] play " + sound.method_7073());
         }

         try {
            ReplayClient.noteSoundSeen();
            if (ReplayClient.getRecordingManager() != null) {
               ReplayClient.getRecordingManager().onSound(sound);
            }
         } catch (Throwable var4) {
         }
      }
   }
}
