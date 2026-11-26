package net.client.mixin;

import net.client.replay.ReplayClient;
import net.minecraft.class_1600;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1600.class)
public class ReplayMinecraftClientMixin {
   @Inject(method = "method_2954", at = @At("TAIL"))
   private void client$tick(CallbackInfo ci) {
      ReplayClient.onClientTick();
   }

   @Inject(method = "method_2916", at = @At("TAIL"))
   private void client$frame(CallbackInfo ci) {
      class_1600 client = (class_1600)(Object)this;
      float tickDelta = ((ReplayMinecraftClientAccessor)client).client$getTicker().field_1038;
      ReplayClient.onFrameEnd(tickDelta);
   }
}
