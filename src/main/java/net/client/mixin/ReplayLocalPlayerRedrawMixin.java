package net.client.mixin;

import net.client.replay.ReplayClient;
import net.client.replay.session.ReplayView;
import net.minecraft.class_1600;
import net.minecraft.class_530;
import net.minecraft.class_536;
import net.minecraft.class_864;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_530.class)
public class ReplayLocalPlayerRedrawMixin {
   private static boolean announced;

   @Inject(method = "method_9908", at = @At("RETURN"))
   private void client$redrawLocalPlayer(class_864 cameraEntity, class_536 cameraView, float tickDelta, CallbackInfo ci) {
      class_1600 client = class_1600.method_2965();
      if (client != null && client.field_10310 != null && client.method_9390() != null) {
         if (ReplayClient.isReplayActive() || ReplayClient.isReplayCaptureActive()) {
            class_864 camera = client.method_9388();
            if (!ReplayView.shouldHideLocalPlayer(client.field_10310)) {
               try {
                  client.method_9390().method_6914(client.field_10310, tickDelta);
                  if (!announced) {
                     announced = true;
                     System.out.println("[replay] local player redrawn by replay pass");
                  }
               } catch (Throwable var8) {
               }
            }
         }
      }
   }
}
