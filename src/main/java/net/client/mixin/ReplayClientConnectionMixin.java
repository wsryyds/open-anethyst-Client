package net.client.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.client.replay.ReplayClient;
import net.minecraft.class_1957;
import net.minecraft.class_700;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1957.class)
public class ReplayClientConnectionMixin {
   @Inject(method = "method_7396", at = @At("HEAD"))
   private void client$onPacket(ChannelHandlerContext context, class_700<?> packet, CallbackInfo ci) {
      if (ReplayClient.getRecordingManager() != null) {
         ReplayClient.getRecordingManager().onPacket((class_1957)(Object)this, packet);
      }
   }
}
