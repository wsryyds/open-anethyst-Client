package com.mcpacks.mixin;

import com.mcpacks.gui.GuiFastResourcePacks;
import java.lang.reflect.Field;
import net.minecraft.class_1600;
import net.minecraft.class_388;
import net.minecraft.class_617;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1600.class)
public class MinecraftClientMixin {
   @Unique
   private static Field mcpacks$parentScreenField;
   @Unique
   private static boolean mcpacks$parentFieldSearched;

   @Inject(method = "method_2928", at = @At("HEAD"), cancellable = true)
   private void mcpacks$replaceResourcePackScreen(class_388 screen, CallbackInfo ci) {
      if (screen != null && screen instanceof class_617 && !(screen instanceof GuiFastResourcePacks)) {
         class_388 parent = mcpacks$findParentScreen((class_617)screen);
         if (parent == null) {
            parent = class_1600.method_2965().field_3816;
         }

         ci.cancel();
         class_1600.method_2965().method_2928(new GuiFastResourcePacks(parent));
      }
   }

   @Unique
   private static class_388 mcpacks$findParentScreen(class_617 vanillaScreen) {
      if (!mcpacks$parentFieldSearched) {
         mcpacks$parentFieldSearched = true;

         try {
            for (Field field : class_617.class.getDeclaredFields()) {
               if (class_388.class.isAssignableFrom(field.getType())) {
                  field.setAccessible(true);
                  mcpacks$parentScreenField = field;
                  break;
               }
            }
         } catch (Exception var6) {
            mcpacks$parentScreenField = null;
         }
      }

      if (mcpacks$parentScreenField == null) {
         return null;
      } else {
         try {
            return (class_388)mcpacks$parentScreenField.get(vanillaScreen);
         } catch (Exception var5) {
            return null;
         }
      }
   }
}
