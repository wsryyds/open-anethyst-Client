package net.client.replay.session;

import net.minecraft.class_1150;
import net.minecraft.class_322;
import net.minecraft.class_864;

public class ReplayCameraEntity extends class_864 {
   public ReplayCameraEntity(class_1150 world) {
      super(world);
      this.field_3216 = true;
      this.field_3206 = 0.1F;
      this.field_3207 = 0.1F;
   }

   public void setTransform(CameraPath.Transform transform) {
      this.field_3249 = this.field_3252 = this.field_3211 = transform.x;
      this.field_3250 = this.field_3253 = this.field_3212 = transform.y;
      this.field_3251 = this.field_3254 = this.field_3213 = transform.z;
      this.field_3194 = this.field_3258 = transform.yaw;
      this.field_3195 = this.field_3193 = transform.pitch;
   }

   public void setTransform(double x, double y, double z, float yaw, float pitch) {
      this.field_3249 = this.field_3252 = this.field_3211 = x;
      this.field_3250 = this.field_3253 = this.field_3212 = y;
      this.field_3251 = this.field_3254 = this.field_3213 = z;
      this.field_3194 = this.field_3258 = yaw;
      this.field_3195 = this.field_3193 = pitch;
   }

   @Override
   protected void method_2484() {
   }

   @Override
   protected void method_2498(class_322 tag) {
   }

   @Override
   protected void method_2528(class_322 tag) {
   }
}
