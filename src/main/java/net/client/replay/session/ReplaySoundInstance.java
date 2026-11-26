package net.client.replay.session;

import net.minecraft.class_1653;
import net.minecraft.class_1900;
import net.minecraft.class_1900.class_1901;

public final class ReplaySoundInstance implements class_1900 {
   private final class_1653 identifier;
   private final float volume;
   private final float pitch;
   private final float x;
   private final float y;
   private final float z;

   public ReplaySoundInstance(class_1653 identifier, float volume, float pitch, float x, float y, float z) {
      this.identifier = identifier;
      this.volume = volume;
      this.pitch = pitch;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   @Override
   public class_1653 method_7073() {
      return this.identifier;
   }

   @Override
   public boolean method_7074() {
      return false;
   }

   @Override
   public int method_7075() {
      return 0;
   }

   @Override
   public float method_7076() {
      return this.volume;
   }

   @Override
   public float method_7077() {
      return this.pitch;
   }

   @Override
   public float method_7078() {
      return this.x;
   }

   @Override
   public float method_7079() {
      return this.y;
   }

   @Override
   public float method_7080() {
      return this.z;
   }

   @Override
   public class_1901 method_7081() {
      return class_1901.field_8162;
   }
}
