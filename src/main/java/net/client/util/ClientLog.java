package net.client.util;

import net.client.Client;

public final class ClientLog {
   private static long lastWarn = 0L;
   private static int suppressed = 0;

   private ClientLog() {
   }

   public static void info(String msg) {
      Client.LOGGER.info(msg);
   }

   public static void warn(String msg) {
      long now = System.currentTimeMillis();
      if (now - lastWarn < 2000L) {
         suppressed++;
      } else {
         lastWarn = now;
         if (suppressed > 0) {
            Client.LOGGER.warn(msg + " (另有 " + suppressed + " 次同类抑制)");
            suppressed = 0;
         } else {
            Client.LOGGER.warn(msg);
         }
      }
   }

   public static void warn(String msg, Throwable t) {
      Client.LOGGER.warn(msg, t);
   }

   public static void error(String msg, Throwable t) {
      Client.LOGGER.error(msg, t);
   }
}
