package net.client.mixin;

import net.client.ClientConfig;
import net.minecraft.class_1071;
import net.minecraft.class_1600;
import net.minecraft.class_1631;
import net.minecraft.class_1699;
import net.minecraft.class_2403;
import net.minecraft.class_518;
import net.minecraft.class_529;
import net.minecraft.class_837;
import net.minecraft.class_988;
import net.minecraft.class_2462.class_2464;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_529.class, priority = 1200)
public abstract class HeldItemRendererMixin {
   @Shadow
   @Final
   private class_1600 field_1876;
   @Shadow
   private class_1071 field_1877;
   @Shadow
   private float field_1878;
   @Shadow
   private float field_1879;
   @Unique
   private boolean client$bobPushed;
   @Unique
   private boolean client$fireScaled;
   @Unique
   private boolean client$handOffsetPushed;
   @Unique
   private static float client$frameDelta = 0.0F;

   @Shadow
   private void method_9873(float equipProgress, float swingProgress) {
   }

   @Shadow
   private void method_9878(float swingProgress) {
   }

   @Shadow
   private void method_9862(float pitch, float yaw) {
   }

   @Shadow
   private void method_9866(class_1631 player) {
   }

   @Shadow
   private void method_9870(class_518 player, float tickDelta) {
   }

   @Shadow
   private void method_9869(class_1631 player, float pitch, float equipProgress, float swingProgress) {
   }

   @Shadow
   private void method_9867(class_1631 player, float tickDelta) {
   }

   @Shadow
   private void method_9877() {
   }

   @Shadow
   private void method_9863(float tickDelta, class_1631 player) {
   }

   @Shadow
   public abstract void method_9872(class_1699 var1, class_1071 var2, class_2464 var3);

   @Shadow
   private void method_9868(class_1631 player, float equipProgress, float swingProgress) {
   }

   @Inject(method = "method_1362", at = @At("HEAD"), cancellable = true)
   private void client$fireHeightIn(float tickDelta, CallbackInfo ci) {
      int pct = ClientConfig.fireHeight;
      if (pct < 100) {
         if (pct <= 0) {
            ci.cancel();
         } else {
            float s = Math.max(0.05F, pct / 100.0F);
            class_2403.method_9791();
            class_2403.method_9816(0.0F, -0.5F * (1.0F - s), 0.0F);
            class_2403.method_9800(1.0F, s, 1.0F);
            this.client$fireScaled = true;
         }
      }
   }

   @Inject(method = "method_1362", at = @At("RETURN"))
   private void client$fireHeightOut(float tickDelta, CallbackInfo ci) {
      if (this.client$fireScaled) {
         this.client$fireScaled = false;
         class_2403.method_9792();
      }
   }

   @Inject(method = "method_1354", at = @At("HEAD"))
   private void client$handOffsetPush(float tickDelta, CallbackInfo ci) {
      class_2403.method_9791();
      float forward = ClientConfig.handForward;
      float right = ClientConfig.handRight;
      float up = ClientConfig.handUp;
      if (forward != 0.0F || right != 0.0F || up != 0.0F) {
         class_2403.method_9816(right, up, -forward);
      }

      this.client$handOffsetPushed = true;
   }

   @Inject(method = "method_1354", at = @At("RETURN"))
   private void client$handOffsetPop(float tickDelta, CallbackInfo ci) {
      if (this.client$handOffsetPushed) {
         class_2403.method_9792();
         this.client$handOffsetPushed = false;
      }
   }

   @Inject(method = "method_1354", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2403;method_9791()V", shift = Shift.AFTER))
   private void client$leftHandMirror(float tickDelta, CallbackInfo ci) {
      if (ClientConfig.leftHanded) {
         class_2403.method_9800(-1.0F, 1.0F, 1.0F);
         class_2403.method_9816(-0.1F, 0.0F, 0.0F);
         GL11.glFrontFace(2304);
      }
   }

   @Inject(method = "method_1354", at = @At("RETURN"))
   private void client$leftHandRestoreCull(float tickDelta, CallbackInfo ci) {
      if (ClientConfig.leftHanded) {
         GL11.glFrontFace(2305);
      }
   }

   @Inject(method = "method_1354", at = @At("HEAD"))
   private void client$bobPush(float tickDelta, CallbackInfo ci) {
      if (ClientConfig.minimalBobbing && this.field_1876.field_3823.field_975) {
         class_2403.method_9791();
         this.client$applyHandBob(tickDelta);
         this.client$bobPushed = true;
      }
   }

   @Inject(method = "method_1354", at = @At("RETURN"))
   private void client$bobPop(float tickDelta, CallbackInfo ci) {
      if (this.client$bobPushed) {
         class_2403.method_9792();
         this.client$bobPushed = false;
      }
   }

   @Inject(method = "method_1354", at = @At("HEAD"))
   private void client$captureFrameDelta(float tickDelta, CallbackInfo ci) {
      client$frameDelta = tickDelta;
   }

   @ModifyArg(
      method = "method_1354",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_529;method_9873(FF)V"),
      slice = @Slice(
         from = @At(value = "INVOKE", target = "Lnet/minecraft/class_529;method_9867(Lnet/minecraft/class_1631;F)V"),
         to = @At(value = "INVOKE", target = "Lnet/minecraft/class_529;method_9863(FLnet/minecraft/class_1631;)V")
      ),
      index = 1
   )
   private float client$oldBlockHitSwing(float swingProgress) {
      return ClientConfig.blockHitAnim && this.field_1876 != null && this.field_1876.field_10310 != null
         ? this.field_1876.field_10310.method_6148(client$frameDelta)
         : swingProgress;
   }

   private void client$applyHandBob(float tickDelta) {
      class_988 player = this.field_1876.field_10310;
      float delta = player.field_3209 - player.field_3208;
      float walked = -(player.field_3209 + delta * tickDelta);
      float stride = player.field_3977 + (player.field_3978 - player.field_3977) * tickDelta;
      float tilt = player.field_6752 + (player.field_6753 - player.field_6752) * tickDelta;
      class_2403.method_9816(
         class_837.method_2335(walked * (float) Math.PI) * stride * 0.5F, -Math.abs(class_837.method_2344(walked * (float) Math.PI) * stride), 0.0F
      );
      class_2403.method_9817(class_837.method_2335(walked * (float) Math.PI) * stride * 3.0F, 0.0F, 0.0F, 1.0F);
      class_2403.method_9817(Math.abs(class_837.method_2344(walked * (float) Math.PI - 0.2F) * stride) * 5.0F, 1.0F, 0.0F, 0.0F);
      class_2403.method_9817(tilt, 1.0F, 0.0F, 0.0F);
   }
}
