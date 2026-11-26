package net.client.mixin;

import net.client.ClientConfig;
import net.client.ui.ClientTheme;
import net.client.ui.ScreenRenderFlag;
import net.minecraft.class_1600;
import net.minecraft.class_370;
import net.minecraft.class_385;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(class_370.class)
public class TextColorMixin {
   @ModifyVariable(method = "method_4247", at = @At("HEAD"), argsOnly = true, index = 4, require = 0)
   private int client$recolor(int color) {
      return client$map(color);
   }

   @ModifyVariable(method = "method_9417", at = @At("HEAD"), argsOnly = true, index = 4, require = 0)
   private int client$recolorFloat(int color) {
      return client$map(color);
   }

   private static int client$map(int color) {
      if (!ClientConfig.lightTheme || !ClientConfig.uiOverride) {
         return color;
      } else if (!ScreenRenderFlag.active()) {
         return color;
      } else {
         class_1600 mc = class_1600.method_2965();
         if (mc != null && mc.field_3816 != null) {
            String screenName = mc.field_3816.getClass().getName();
            if (!screenName.startsWith("net.client.") && !screenName.startsWith("com.mcpacks.")) {
               return color;
            } else if (mc.field_3823 != null && mc.field_3823.field_950 && mc.field_3816 instanceof class_385) {
               return color;
            } else {
               int rgb = color & 16777215;
               return rgb != 16777215 && rgb != 14737632 ? color : ClientTheme.get().colors.onSurface;
            }
         } else {
            return color;
         }
      }
   }
}
