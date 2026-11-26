package net.client.mixin;

import net.client.ClientConfig;
import net.minecraft.class_1150;
import net.minecraft.class_236;
import net.minecraft.class_864;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = class_1150.class, priority = 1200)
public class WorldMixin {
   @Inject(method = "method_3715", at = @At("HEAD"), cancellable = true)
   private void client$rainGradient(float delta, CallbackInfoReturnable<Float> cir) {
      class_1150 self = (class_1150)(Object)this;
      if (self.field_4532) {
         int mode = ClientConfig.weatherMode;
         if (mode > 0) {
            cir.setReturnValue(mode >= 2 ? 1.0F : 0.0F);
         }
      }
   }

   @Inject(method = "method_3713", at = @At("HEAD"), cancellable = true)
   private void client$thunderGradient(float delta, CallbackInfoReturnable<Float> cir) {
      class_1150 self = (class_1150)(Object)this;
      if (self.field_4532) {
         int mode = ClientConfig.weatherMode;
         if (mode > 0) {
            cir.setReturnValue(mode == 3 ? 1.0F : 0.0F);
         }
      }
   }

   @Inject(method = "method_3583", at = @At("HEAD"), cancellable = true)
   private void client$timeOfDay(CallbackInfoReturnable<Long> cir) {
      class_1150 self = (class_1150)(Object)this;
      if (self.field_4532 && ClientConfig.timeOverride) {
         cir.setReturnValue((long)ClientConfig.timeTicks);
      }
   }

   @Inject(method = "method_3670", at = @At("HEAD"), cancellable = true)
   private void client$skyAngle(float tickDelta, CallbackInfoReturnable<Float> cir) {
      class_1150 self = (class_1150)(Object)this;
      if (self.field_4532 && ClientConfig.timeOverride) {
         cir.setReturnValue(self.field_4558.method_3980(ClientConfig.timeTicks, 0.0F));
      }
   }

   @Inject(method = "method_3631", at = @At("HEAD"), cancellable = true)
   private void client$skyColor(class_864 entity, float delta, CallbackInfoReturnable<class_236> cir) {
      class_1150 self = (class_1150)(Object)this;
      if (self.field_4532 && ClientConfig.fogModify) {
         int c = ClientConfig.fogColor;
         cir.setReturnValue(new class_236((c >> 16 & 0xFF) / 255.0, (c >> 8 & 0xFF) / 255.0, (c & 0xFF) / 255.0));
      }
   }

   @Inject(method = "method_3702", at = @At("HEAD"), cancellable = true)
   private void client$fogColor(float delta, CallbackInfoReturnable<class_236> cir) {
      class_1150 self = (class_1150)(Object)this;
      if (self.field_4532 && ClientConfig.fogModify) {
         int c = ClientConfig.fogColor;
         cir.setReturnValue(new class_236((c >> 16 & 0xFF) / 255.0, (c >> 8 & 0xFF) / 255.0, (c & 0xFF) / 255.0));
      }
   }
}
