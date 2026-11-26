package net.client.replay.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_1150;
import net.minecraft.class_1699;
import net.minecraft.class_864;

public final class SnapshotSampler {
   private long lastSampleNanos;
   private int sampleHz;

   public SnapshotSampler(int sampleHz) {
      this.sampleHz = sampleHz;
   }

   public boolean shouldSample(long nowNanos) {
      if (this.sampleHz <= 0) {
         return false;
      } else {
         long interval = 1000000000L / this.sampleHz;
         if (this.lastSampleNanos != 0L && nowNanos - this.lastSampleNanos < interval) {
            return false;
         } else {
            this.lastSampleNanos = nowNanos;
            return true;
         }
      }
   }

   public SnapshotSample sample(long timeNanos, class_1150 world, float tickDelta) {
      SnapshotSample sample = new SnapshotSample();
      sample.timeNanos = timeNanos;
      if (world == null) {
         return sample;
      } else {
         List<class_864> loaded = world.method_3737();
         if (loaded != null && !loaded.isEmpty()) {
            List<EntitySnapshot> entities = new ArrayList<>(loaded.size());

            for (class_864 entity : loaded) {
               if (entity != null && entity.method_8365() >= 0) {
                  if (entity instanceof class_1699 && ((class_1699)entity).method_6122() <= 0.0F) {
                  }

                  entities.add(EntitySnapshot.capture(entity, tickDelta));
               }
            }

            Collections.sort(entities, new Comparator<EntitySnapshot>() {
               public int compare(EntitySnapshot a, EntitySnapshot b) {
                  return Integer.compare(a.entityId, b.entityId);
               }
            });
            sample.entities = entities;
            return sample;
         } else {
            return sample;
         }
      }
   }
}
