package net.client.mixin;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import net.client.util.ServerListPingFix;
import net.minecraft.class_1825;
import net.minecraft.class_485;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_1825.class)
public class ServerEntryPingMixin {
   @Shadow
   private class_485 field_7834;

   @Redirect(
      method = "method_6700",
      at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ThreadPoolExecutor;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;")
   )
   private Future<?> client$submitPing(ThreadPoolExecutor vanillaPool, Runnable task) {
      return ServerListPingFix.submit(vanillaPool, task, this.field_7834);
   }
}
