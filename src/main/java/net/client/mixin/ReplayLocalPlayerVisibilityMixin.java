package net.client.mixin;

import java.util.Locale;
import net.client.replay.ReplayClient;
import net.client.replay.session.ReplayView;
import net.minecraft.class_1600;
import net.minecraft.class_1635;
import net.minecraft.class_1699;
import net.minecraft.class_864;
import net.minecraft.class_988;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_1635.class)
public class ReplayLocalPlayerVisibilityMixin {
   private static boolean announced;
   private static long lastTraceMs;

   @Redirect(method = "method_5778", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1699;method_4446()Z"))
   private boolean client$forceVisible(class_1699 entity) {
      if (client$hideLocalPlayer(entity)) {
         return true;
      } else if (client$showLocalPlayer(entity)) {
         client$trace(entity);
         return false;
      } else {
         return entity.method_4446();
      }
   }

   @Redirect(method = "method_5778", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1699;method_5383(Lnet/minecraft/class_988;)Z"))
   private boolean client$forceInvisibleTo(class_1699 entity, class_988 viewer) {
      if (client$hideLocalPlayer(entity)) {
         return true;
      } else {
         return client$showLocalPlayer(entity) ? false : entity.method_5383(viewer);
      }
   }

   private static void client$trace(class_1699 entity) {
      long now = System.currentTimeMillis();
      if (now - lastTraceMs >= 2000L) {
         lastTraceMs = now;
         class_1600 client = class_1600.method_2965();
         if (client != null && client.field_10310 != null) {
            class_864 camera = client.method_9388();
            double dx = entity.field_3252 - (camera == null ? 0.0 : camera.field_3252);
            double dy = entity.field_3253 - (camera == null ? 0.0 : camera.field_3253);
            double dz = entity.field_3254 - (camera == null ? 0.0 : camera.field_3254);
            System.out
               .println(
                  String.format(
                     Locale.ROOT,
                     "[replay] local player pos=(%.1f, %.1f, %.1f) invisible=%s cam=%s camPos=(%.1f, %.1f, %.1f) dist=%.1f persp=%d",
                     entity.field_3252,
                     entity.field_3253,
                     entity.field_3254,
                     entity.method_4446(),
                     camera == null ? "null" : camera.getClass().getSimpleName(),
                     camera == null ? 0.0 : camera.field_3252,
                     camera == null ? 0.0 : camera.field_3253,
                     camera == null ? 0.0 : camera.field_3254,
                     Math.sqrt(dx * dx + dy * dy + dz * dz),
                     client.field_3823 == null ? -1 : client.field_3823.field_949
                  )
               );
         }
      }
   }

   private static boolean client$isLocalPlayer(class_1699 entity) {
      class_1600 client = class_1600.method_2965();
      return client != null && client.field_10310 != null && entity == client.field_10310;
   }

   private static boolean client$hideLocalPlayer(class_1699 entity) {
      if (!client$isLocalPlayer(entity)) {
         return false;
      } else {
         class_1600 client = class_1600.method_2965();
         return !ReplayClient.isReplayActive() && !ReplayClient.isReplayCaptureActive() ? false : ReplayView.shouldHideLocalPlayer(entity);
      }
   }

   private static boolean client$showLocalPlayer(class_1699 entity) {
      if (!client$isLocalPlayer(entity)) {
         return false;
      } else {
         class_1600 client = class_1600.method_2965();
         return !ReplayClient.isReplayActive() && !ReplayClient.isReplayCaptureActive() ? false : !ReplayView.shouldHideLocalPlayer(entity);
      }
   }
}
