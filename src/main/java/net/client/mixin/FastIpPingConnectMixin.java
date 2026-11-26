package net.client.mixin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.client.util.FastIpPing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/class_472$1")
public class FastIpPingConnectMixin {
   @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetAddress;getByName(Ljava/lang/String;)Ljava/net/InetAddress;"))
   private static InetAddress client$fastConnect(String address) throws UnknownHostException {
      return FastIpPing.getByName(address);
   }
}
