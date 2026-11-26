package net.client.ui.md3.anim;

public final class Md3Anim {
   private float value;
   private float target;

   public Md3Anim(float initial) {
      this.value = initial;
      this.target = initial;
   }

   public void target(float target) {
      this.target = target;
   }

   public void snap(float value) {
      this.value = value;
      this.target = value;
   }

   public float get() {
      return this.value;
   }

   public float update(float dt, float speed) {
      if (dt <= 0.0F) {
         return this.value;
      } else {
         float factor = 1.0F - (float)Math.exp(-speed * dt);
         this.value = this.value + (this.target - this.value) * factor;
         if (Math.abs(this.target - this.value) < 0.001F) {
            this.value = this.target;
         }

         return this.value;
      }
   }

   public float update(float dt) {
      return this.update(dt, 14.0F);
   }

   public boolean settled() {
      return Math.abs(this.target - this.value) < 0.01F;
   }

   public static float easeOut(float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      return 1.0F - (1.0F - t) * (1.0F - t);
   }

   public static float emphasized(float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      return t < 0.5F ? 4.0F * t * t * t : 1.0F - (float)Math.pow(-2.0F * t + 2.0F, 3.0) / 2.0F;
   }

   public static float standard(float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      return t * t * (3.0F - 2.0F * t);
   }
}
