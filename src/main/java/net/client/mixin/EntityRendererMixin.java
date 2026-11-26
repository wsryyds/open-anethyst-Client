package net.client.mixin;

import net.client.ClientConfig;
import net.minecraft.class_1600;
import net.minecraft.class_2403;
import net.minecraft.class_2520;
import net.minecraft.class_370;
import net.minecraft.class_520;
import net.minecraft.class_533;
import net.minecraft.class_550;
import net.minecraft.class_551;
import net.minecraft.class_864;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_551.class, priority = 1100)
public abstract class EntityRendererMixin<T extends class_864> {
   @Shadow
   protected class_550 field_2111;

   @Inject(method = "method_6917", at = @At("HEAD"), cancellable = true)
   private void client$nameTag(T entity, String text, double x, double y, double z, int maxDistance, CallbackInfo ci) {
      if (text != null && !text.isEmpty()) {
         double dist = entity.method_2548(this.field_2111.field_11098);
         if (!(dist > maxDistance * maxDistance)) {
            class_370 tr = class_1600.method_2965().field_3814;
            if (tr != null) {
               int bg = ClientConfig.nameBgColor;
               int fg = ClientConfig.nameTextColor;
               float scale = 0.026666673F;
               class_2403.method_9791();
               class_2403.method_9816((float)x, (float)(y + entity.field_3207 + 0.5), (float)z);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               class_2403.method_9817(-this.field_2111.field_2102, 0.0F, 1.0F, 0.0F);
               class_2403.method_9817(this.field_2111.field_2103, 1.0F, 0.0F, 0.0F);
               class_2403.method_9800(-scale, -scale, scale);
               class_2403.method_9832();
               class_2403.method_9811(false);
               class_2403.method_9838();
               class_2403.method_9843();
               class_2403.method_9805(770, 771, 1, 0);
               class_2403.method_9856();
               int half = tr.method_954(text) / 2;
               class_533 tess = class_533.method_9926();
               class_520 buf = tess.method_9928();
               buf.method_9737(7, class_2520.field_11207);
               float a = (bg >>> 24 & 0xFF) / 255.0F;
               float r = (bg >> 16 & 0xFF) / 255.0F;
               float g = (bg >> 8 & 0xFF) / 255.0F;
               float b = (bg & 0xFF) / 255.0F;
               buf.method_9742(-half - 2.0, 9.0, 0.0).method_9731(r, g, b, a).method_9750();
               buf.method_9742(half + 2.0, 9.0, 0.0).method_9731(r, g, b, a).method_9750();
               buf.method_9742(half + 2.0, -2.0, 0.0).method_9731(r, g, b, a).method_9750();
               buf.method_9742(-half - 2.0, -2.0, 0.0).method_9731(r, g, b, a).method_9750();
               tess.method_9927();
               class_2403.method_9855();
               class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
               tr.method_4247(text, -half, 0, fg);
               class_2403.method_9840();
               class_2403.method_9811(true);
               class_2403.method_9830();
               class_2403.method_9842();
               class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
               class_2403.method_9792();
               ci.cancel();
            }
         }
      }
   }
}
