package net.client.replay.record;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.class_1069;
import net.minecraft.class_1071;
import net.minecraft.class_1600;
import net.minecraft.class_1699;
import net.minecraft.class_518;
import net.minecraft.class_864;
import net.minecraft.class_987;
import net.minecraft.class_988;

public final class EntitySnapshot {
   public static final int BYTES = 92;
   public static final int EQUIP_BYTES = 50;
   public static final int BYTES_V2 = 142;
   public static int FORMAT_VERSION = 1;
   public static final int FLAG_SNEAKING = 1;
   public static final int FLAG_SPRINTING = 2;
   public static final int FLAG_INVISIBLE = 4;
   public static final int FLAG_USING_ITEM = 8;
   public static final int FLAG_SWINGING = 16;
   public static final int FLAG_DEAD = 32;
   public int entityId;
   public double x;
   public double y;
   public double z;
   public float yaw;
   public float pitch;
   public float headYaw;
   public float bodyYaw;
   public float velocityX;
   public float velocityY;
   public float velocityZ;
   public float limbA;
   public float limbB;
   public float limbC;
   public float limbD;
   public float swingProgress;
   public int flags;
   public int hurtTime;
   public float width;
   public float height;
   public short[] hotbarId = new short[9];
   public short[] hotbarDamage = new short[9];
   public byte[] hotbarCount = new byte[9];
   public byte selectedSlot;

   public static EntitySnapshot capture(class_864 entity, float tickDelta) {
      EntitySnapshot s = new EntitySnapshot();
      s.entityId = entity.method_8365();
      s.x = entity.field_3249 + (entity.field_3252 - entity.field_3249) * tickDelta;
      s.y = entity.field_3250 + (entity.field_3253 - entity.field_3250) * tickDelta;
      s.z = entity.field_3251 + (entity.field_3254 - entity.field_3251) * tickDelta;
      s.yaw = interpolateAngle(entity.field_3194, entity.field_3258, tickDelta);
      s.pitch = entity.field_3195 + (entity.field_3193 - entity.field_3195) * tickDelta;
      s.velocityX = (float)entity.field_3255;
      s.velocityY = (float)entity.field_3256;
      s.velocityZ = (float)entity.field_3257;
      s.width = entity.field_3206;
      s.height = entity.field_3207;
      if (entity instanceof class_1699 living) {
         s.headYaw = interpolateAngle(living.field_6759, living.field_6758, tickDelta);
         s.bodyYaw = interpolateAngle(living.field_6757, living.field_6756, tickDelta);
         s.limbA = living.field_6748;
         s.limbB = living.field_6749;
         s.limbC = living.field_6750;
         s.limbD = living.field_6752;
         s.swingProgress = living.method_6148(tickDelta);
         s.hurtTime = living.field_6773;
         if (living instanceof class_988 player) {
            if (player.method_3193()) {
               s.flags |= 8;
            }

            s.captureInventory(player);
         }

         if (living.field_6769) {
            s.flags |= 16;
         }

         if (living.method_6122() <= 0.0F) {
            s.flags |= 32;
         }
      }

      if (entity instanceof class_518 && Boolean.getBoolean("client.replay.soundDebug")) {
         System.out
            .println(
               "[SneakCap] live=" + entity.method_2513() + " id=" + entity.method_8365() + " samePlayer=" + (entity == class_1600.method_2965().field_10310)
            );
      }

      if (entity.method_2513()) {
         s.flags |= 1;
      }

      if (entity.method_2514()) {
         s.flags |= 2;
      }

      if (entity.method_4446()) {
         s.flags |= 4;
      }

      return s;
   }

   private void captureInventory(class_988 player) {
      class_987 inv = player.field_3999;
      if (inv != null) {
         for (int i = 0; i < 9; i++) {
            class_1071 stack = inv.field_3964[i];
            if (stack != null && stack.method_3421() != null) {
               this.hotbarId[i] = (short)class_1069.method_6364(stack.method_3421());
               this.hotbarDamage[i] = (short)stack.method_3439();
               this.hotbarCount[i] = (byte)Math.min(127, stack.field_4376);
            }
         }

         this.selectedSlot = (byte)inv.field_3966;
      }
   }

   private void applyInventory(class_988 player) {
      class_987 inv = player.field_3999;
      if (inv != null) {
         for (int i = 0; i < 9; i++) {
            int id = this.hotbarId[i];
            if (id == 0) {
               inv.field_3964[i] = null;
            } else {
               class_1069 item = class_1069.method_6366(id);
               if (item == null) {
                  inv.field_3964[i] = null;
               } else {
                  int count = this.hotbarCount[i] <= 0 ? 1 : this.hotbarCount[i];
                  inv.field_3964[i] = new class_1071(item, count, this.hotbarDamage[i]);
               }
            }
         }

         if (this.selectedSlot >= 0 && this.selectedSlot < 9) {
            inv.field_3966 = this.selectedSlot;
         }
      }
   }

   public void applyTo(class_864 entity) {
      entity.field_3249 = entity.field_3252 = this.x;
      entity.field_3250 = entity.field_3253 = this.y;
      entity.field_3251 = entity.field_3254 = this.z;
      entity.field_3211 = entity.field_3252;
      entity.field_3212 = entity.field_3253;
      entity.field_3213 = entity.field_3254;
      entity.field_3194 = entity.field_3258 = this.yaw;
      entity.field_3195 = entity.field_3193 = this.pitch;
      entity.field_3255 = this.velocityX;
      entity.field_3256 = this.velocityY;
      entity.field_3257 = this.velocityZ;
      entity.field_3206 = this.width;
      entity.field_3207 = this.height;
      entity.method_2505((this.flags & 1) != 0);
      if (entity instanceof class_518 clientPlayer && clientPlayer.field_1758 != null) {
         clientPlayer.field_1758.field_1756 = (this.flags & 1) != 0;
      }

      entity.method_2529((this.flags & 2) != 0);
      entity.method_4452((this.flags & 4) != 0);
      if (entity instanceof class_1699 living) {
         living.field_6759 = living.field_6758 = this.headYaw;
         living.field_6757 = living.field_6756 = this.bodyYaw;
         living.field_6748 = this.limbA;
         living.field_6749 = this.limbB;
         living.field_6750 = this.limbC;
         living.field_6752 = this.limbD;
         living.field_6746 = living.field_6747 = this.swingProgress;
         living.field_6773 = this.hurtTime;
         living.field_6769 = (this.flags & 16) != 0;
         if ((this.flags & 32) != 0) {
         }

         if (entity instanceof class_988) {
            this.applyInventory((class_988)living);
         }
      }
   }

   public void write(DataOutput out) throws IOException {
      out.writeInt(this.entityId);
      out.writeDouble(this.x);
      out.writeDouble(this.y);
      out.writeDouble(this.z);
      out.writeFloat(this.yaw);
      out.writeFloat(this.pitch);
      out.writeFloat(this.headYaw);
      out.writeFloat(this.bodyYaw);
      out.writeFloat(this.velocityX);
      out.writeFloat(this.velocityY);
      out.writeFloat(this.velocityZ);
      out.writeFloat(this.limbA);
      out.writeFloat(this.limbB);
      out.writeFloat(this.limbC);
      out.writeFloat(this.limbD);
      out.writeFloat(this.swingProgress);
      out.writeInt(this.flags);
      out.writeInt(this.hurtTime);
      out.writeFloat(this.width);
      out.writeFloat(this.height);

      for (int i = 0; i < 9; i++) {
         out.writeShort(this.hotbarId[i]);
         out.writeShort(this.hotbarDamage[i]);
         out.writeByte(this.hotbarCount[i]);
      }

      out.writeByte(this.selectedSlot);
      out.writeInt(0);
   }

   public static EntitySnapshot read(DataInput in) throws IOException {
      EntitySnapshot s = new EntitySnapshot();
      s.entityId = in.readInt();
      s.x = in.readDouble();
      s.y = in.readDouble();
      s.z = in.readDouble();
      s.yaw = in.readFloat();
      s.pitch = in.readFloat();
      s.headYaw = in.readFloat();
      s.bodyYaw = in.readFloat();
      s.velocityX = in.readFloat();
      s.velocityY = in.readFloat();
      s.velocityZ = in.readFloat();
      s.limbA = in.readFloat();
      s.limbB = in.readFloat();
      s.limbC = in.readFloat();
      s.limbD = in.readFloat();
      s.swingProgress = in.readFloat();
      s.flags = in.readInt();
      s.hurtTime = in.readInt();
      s.width = in.readFloat();
      s.height = in.readFloat();
      if (FORMAT_VERSION >= 2) {
         for (int i = 0; i < 9; i++) {
            s.hotbarId[i] = in.readShort();
            s.hotbarDamage[i] = in.readShort();
            s.hotbarCount[i] = in.readByte();
         }

         s.selectedSlot = in.readByte();
         in.readInt();
      }

      return s;
   }

   public static float interpolateAngle(float previous, float current, float delta) {
      float difference = current - previous;

      while (difference < -180.0F) {
         difference += 360.0F;
      }

      while (difference >= 180.0F) {
         difference -= 360.0F;
      }

      return previous + difference * delta;
   }

   public static float lerpAngle(float from, float to, float progress) {
      float difference = to - from;

      while (difference < -180.0F) {
         difference += 360.0F;
      }

      while (difference >= 180.0F) {
         difference -= 360.0F;
      }

      return from + difference * progress;
   }
}
