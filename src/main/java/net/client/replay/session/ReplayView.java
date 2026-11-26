package net.client.replay.session;

import net.client.util.ClientLog;
import net.minecraft.class_1600;
import net.minecraft.class_864;

public final class ReplayView {
   private static long lastTraceMs;

   private ReplayView() {
   }

   public static boolean shouldHideLocalPlayer(class_864 player) {
      if (!isLocalPlayer(player)) {
         return false;
      } else {
         class_1600 client = class_1600.method_2965();
         if (client != null && client.field_3823 != null) {
            class_864 cam = client.method_9388();
            boolean hide;
            if (cam == null) {
               hide = false;
            } else if (cam == player) {
               hide = client.field_3823.field_949 == 0;
            } else {
               double eyeY = player.field_3253 + player.method_2544();
               double dx = cam.field_3252 - player.field_3252;
               double dy = cam.field_3253 - eyeY;
               double dz = cam.field_3254 - player.field_3254;
               hide = dx * dx + dy * dy + dz * dz < 0.2025;
            }

            trace(player, client, hide);
            return hide;
         } else {
            return false;
         }
      }
   }

   private static void trace(class_864 player, class_1600 client, boolean hide) {
      long now = System.currentTimeMillis();
      if (now - lastTraceMs >= 2000L) {
         lastTraceMs = now;
         class_864 cam = client.method_9388();
         ClientLog.info(
            "[replay] view hide="
               + hide
               + " persp="
               + client.field_3823.field_949
               + " cam="
               + (cam == null ? "null" : cam.getClass().getSimpleName())
               + " camIsPlayer="
               + (cam == player)
         );
      }
   }

   public static boolean isLocalPlayer(class_864 entity) {
      class_1600 client = class_1600.method_2965();
      return client != null && client.field_10310 != null && entity == client.field_10310;
   }
}
