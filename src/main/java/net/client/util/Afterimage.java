package net.client.util;

import net.minecraft.class_1699;

public final class Afterimage {
   public final class_1699 entity;
   public final int entityId;
   public double x;
   public double y;
   public double z;
   public float yaw;
   public float pitch;
   public float headYaw;
   public float bodyYaw;
   public float limbSwing;
   public float limbSwingAmount;
   public float ticksAlive;
   public float swingProgress;
   public boolean sneaking;
   public boolean riding;
   public boolean child;
   public final long createTime;
   public final long durationMillis;
   public boolean captured;

   public Afterimage(class_1699 entity, long durationMillis) {
      this.entity = entity;
      this.entityId = entity.method_8365();
      this.createTime = System.currentTimeMillis();
      this.durationMillis = Math.max(100L, durationMillis);
   }

   public void captureFrom(class_1699 entity) {
      if (entity != null) {
         this.x = entity.field_3252;
         this.y = entity.field_3253;
         this.z = entity.field_3254;
         this.yaw = entity.field_3258;
         this.pitch = entity.field_3193;
         this.headYaw = entity.field_6758;
         this.bodyYaw = entity.field_6756;
         this.limbSwing = entity.field_6750;
         this.limbSwingAmount = entity.field_6749;
         this.ticksAlive = entity.field_3220;
         this.swingProgress = entity.method_6148(1.0F);
         this.sneaking = entity.method_2513();
         this.riding = entity.method_2512();
         this.child = entity.method_2662();
         this.captured = true;
      }
   }

   public float getAlpha() {
      long age = System.currentTimeMillis() - this.createTime;
      float a = 1.0F - (float)age / (float)this.durationMillis;
      if (a < 0.0F) {
         a = 0.0F;
      }

      if (a > 1.0F) {
         a = 1.0F;
      }

      return a;
   }

   public boolean isExpired() {
      return System.currentTimeMillis() - this.createTime > this.durationMillis;
   }
}
