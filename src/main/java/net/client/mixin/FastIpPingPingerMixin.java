package net.client.mixin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.client.util.FastIpPing;
import net.minecraft.class_1848;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_1848.class)
public class FastIpPingPingerMixin {
   @Redirect(method = "method_6855", at = @At(value = "INVOKE", target = "Ljava/net/InetAddress;getByName(Ljava/lang/String;)Ljava/net/InetAddress;"))
   private static InetAddress client$fastPing(String address) throws UnknownHostException {
      return FastIpPing.getByName(address);
   }
}
