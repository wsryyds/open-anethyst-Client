package net.client.replay.record;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_1967;
import net.minecraft.class_1997;
import net.minecraft.class_1999;
import net.minecraft.class_2025;
import net.minecraft.class_700;
import net.minecraft.class_878;
import net.minecraft.class_878.class_879;

public final class PacketCodec {
   private PacketCodec() {
   }

   public static byte[] encode(class_700<?> packet) throws IOException {
      class_1967 buffer = new class_1967(Unpooled.buffer());

      byte[] var3;
      try {
         if (!writeSpawnPacket(packet, buffer)) {
            packet.method_7539(buffer);
         }

         byte[] data = new byte[buffer.readableBytes()];
         buffer.readBytes(data);
         var3 = data;
      } finally {
         buffer.release();
      }

      return var3;
   }

   private static boolean writeSpawnPacket(class_700<?> packet, class_1967 buffer) throws IOException {
      if (packet instanceof class_1999 spawn) {
         List<class_879> entries;
         try {
            entries = spawn.method_7661();
         } catch (Throwable var5) {
            entries = Collections.emptyList();
         }

         if (entries == null) {
            entries = Collections.emptyList();
         }

         buffer.method_7425(spawn.method_7662());
         buffer.method_10606(spawn.method_7663());
         buffer.writeInt(spawn.method_7664());
         buffer.writeInt(spawn.method_7665());
         buffer.writeInt(spawn.method_7666());
         buffer.writeByte(spawn.method_7667());
         buffer.writeByte(spawn.method_7668());
         buffer.writeShort(spawn.method_7669());
         class_878.method_8378(entries, buffer);
         return true;
      } else if (packet instanceof class_1997 spawn) {
         List<class_879> entriesx;
         try {
            entriesx = spawn.method_7641();
         } catch (Throwable var6) {
            entriesx = Collections.emptyList();
         }

         if (entriesx == null) {
            entriesx = Collections.emptyList();
         }

         buffer.method_7425(spawn.method_7642());
         buffer.writeByte(spawn.method_7643() & 0xFF);
         buffer.writeInt(spawn.method_7644());
         buffer.writeInt(spawn.method_7645());
         buffer.writeInt(spawn.method_7646());
         buffer.writeByte(spawn.method_7650());
         buffer.writeByte(spawn.method_7651());
         buffer.writeByte(spawn.method_7652());
         buffer.writeShort(spawn.method_7647());
         buffer.writeShort(spawn.method_7648());
         buffer.writeShort(spawn.method_7649());
         class_878.method_8378(entriesx, buffer);
         return true;
      } else {
         return false;
      }
   }

   private static Class<?> loadPacketClass(String className) throws ClassNotFoundException {
      try {
         return Class.forName(className);
      } catch (ClassNotFoundException var8) {
         for (ClassLoader loader : new ClassLoader[]{
            Thread.currentThread().getContextClassLoader(), class_700.class.getClassLoader(), class_2025.class.getClassLoader()
         }) {
            if (loader != null) {
               try {
                  return Class.forName(className, false, loader);
               } catch (ClassNotFoundException var7) {
               }
            }
         }

         if (className.endsWith("GameJoinS2CPacket")) {
            return class_2025.class;
         } else {
            throw var8;
         }
      }
   }

   public static class_700<?> decode(String className, byte[] data) throws Exception {
      Class<?> raw = loadPacketClass(className);
      if (!class_700.class.isAssignableFrom(raw)) {
         throw new IllegalArgumentException(className + " is not a Packet");
      } else {
         Constructor<?> constructor = raw.getDeclaredConstructor();
         if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
         }

         class_700 packet = (class_700)constructor.newInstance();
         class_1967 buffer = new class_1967(Unpooled.wrappedBuffer(data));

         try {
            packet.method_7535(buffer);
         } finally {
            buffer.release();
         }

         return packet;
      }
   }
}
