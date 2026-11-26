package net.client.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import net.client.ClientConfig;
import net.minecraft.class_1600;
import net.minecraft.class_1699;

public final class AfterimageHandler {
   private static final List<Afterimage> LIST = new CopyOnWriteArrayList<>();
   private static final Map<Integer, Long> LAST_ADD = new HashMap<>();

   private AfterimageHandler() {
   }

   public static int size() {
      return LIST.size();
   }

   public static void clearAll() {
      LIST.clear();
      LAST_ADD.clear();
   }

   public static void addAfterimage(class_1699 entity) {
      if (entity != null && !entity.field_3204) {
         int id = entity.method_8365();
         long now = System.currentTimeMillis();
         Long previous = LAST_ADD.get(id);
         if (previous == null || now - previous >= 50L) {
            LAST_ADD.put(id, now);

            while (LIST.size() >= 24) {
               LIST.remove(0);
            }

            LIST.add(new Afterimage(entity, Math.max(200, ClientConfig.afterimageLife)));
         }
      }
   }

   public static void tick() {
      if (!ClientConfig.afterimage) {
         if (!LIST.isEmpty()) {
            LIST.clear();
         }

         if (!LAST_ADD.isEmpty()) {
            LAST_ADD.clear();
         }
      } else {
         pruneExpired();
         if (LAST_ADD.size() > 128) {
            LAST_ADD.clear();
         }
      }
   }

   public static void onRenderWorld(float partialTicks) {
      if (ClientConfig.afterimage && !LIST.isEmpty()) {
         class_1600 mc = class_1600.method_2965();
         if (mc != null && mc.field_3803 != null && mc.method_9388() != null) {
            try {
               AfterimageRenderer.renderAll(LIST, partialTicks);
            } catch (Exception var3) {
               ClientLog.error("残影渲染异常", var3);
            }

            pruneExpired();
         }
      }
   }

   private static void pruneExpired() {
      boolean changed = false;

      for (Afterimage afterimage : LIST) {
         if (afterimage.isExpired() || afterimage.entity == null || afterimage.entity.field_3204) {
            LIST.remove(afterimage);
            changed = true;
         }
      }

      if (changed && LIST.isEmpty()) {
         LAST_ADD.clear();
      }
   }
}
