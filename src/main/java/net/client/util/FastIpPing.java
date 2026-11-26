package net.client.util;

import com.google.common.net.InetAddresses;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class FastIpPing {
   private FastIpPing() {
   }

   public static InetAddress getByName(String address) throws UnknownHostException {
      InetAddress resolved = InetAddress.getByName(address);
      return address != null && InetAddresses.isInetAddress(address) ? InetAddress.getByAddress(resolved.getHostAddress(), resolved.getAddress()) : resolved;
   }
}
