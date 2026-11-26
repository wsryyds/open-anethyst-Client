package net.client.replay.session;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.Map;
import java.util.UUID;
import net.client.mixin.ReplayClientPlayNetworkHandlerAccessor;
import net.client.util.ClientLog;
import net.minecraft.class_1600;
import net.minecraft.class_1631;
import net.minecraft.class_1653;
import net.minecraft.class_1845;
import net.minecraft.class_2346;
import net.minecraft.class_864;

public final class ReplayLocalSkin {
   private static class_1653 skin;
   private static String model;

   private ReplayLocalSkin() {
   }

   public static boolean capture(class_864 live) {
      if (skin != null) {
         return true;
      } else if (!(live instanceof class_1631 player)) {
         return false;
      } else {
         try {
            if (!player.method_6868()) {
               return false;
            } else {
               class_1653 id = player.method_5725();
               if (id == null) {
                  return false;
               } else {
                  skin = id;
                  model = player.method_9716();
                  return true;
               }
            }
         } catch (Throwable var3) {
            return false;
         }
      }
   }

   public static void clear() {
      skin = null;
      model = null;
   }

   public static class_1653 skin() {
      return skin;
   }

   public static boolean hasSkin() {
      return skin != null;
   }

   public static void installEntry(class_1845 handler, String uuid, String name, String textures) {
      if (handler != null) {
         try {
            GameProfile base = handler.method_9657();
            UUID id = base == null ? null : base.getId();
            if (uuid != null && !uuid.isEmpty()) {
               try {
                  id = UUID.fromString(uuid);
               } catch (Throwable var10) {
               }
            }

            if (id == null) {
               return;
            }

            String playerName = name != null && !name.isEmpty() ? name : (base == null ? "Player" : base.getName());
            GameProfile profile = new GameProfile(id, playerName);
            if (textures != null && !textures.isEmpty()) {
               profile.getProperties().put("textures", new Property("textures", textures));
            }

            class_2346 entry = new class_2346(profile);
            Map<UUID, class_2346> list = ((ReplayClientPlayNetworkHandlerAccessor)handler).client$getPlayerListEntries();
            list.put(id, entry);
            if (base != null && base.getId() != null) {
               list.put(base.getId(), entry);
            }

            ClientLog.info("[replay] skin entry installed uuid=" + id + " textures=" + (textures != null && !textures.isEmpty()));
         } catch (Throwable var11) {
            ClientLog.warn("[replay] skin entry failed: " + var11);
         }
      }
   }

   public static void applyModelParts(class_864 player) {
      if (player != null) {
         try {
            player.method_2561().method_2702(10, (byte)127);
         } catch (Throwable var2) {
            ClientLog.warn("[replay] model parts failed: " + var2);
         }
      }
   }

   public static boolean isLocal(class_864 entity) {
      class_1600 client = class_1600.method_2965();
      return client != null && client.field_10310 != null && entity == client.field_10310;
   }
}
