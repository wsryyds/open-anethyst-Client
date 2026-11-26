package net.client.mixin;

import net.client.replay.session.CameraController;
import net.minecraft.class_1071;
import net.minecraft.class_1099;
import net.minecraft.class_1631;
import net.minecraft.class_1699;
import net.minecraft.class_1734;
import net.minecraft.class_2403;
import net.minecraft.class_328;
import net.minecraft.class_529;
import net.minecraft.class_550;
import net.minecraft.class_551;
import net.minecraft.class_570;
import net.minecraft.class_864;
import net.minecraft.class_988;
import net.minecraft.class_2462.class_2464;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_529.class)
public class ReplayHeldItemRendererMixin {
   @Shadow
   private float field_1878;
   @Shadow
   private float field_1879;
   @Shadow
   private class_1071 field_1877;

   @Shadow
   private void method_9866(class_1631 player) {
   }

   @Shadow
   private void method_9868(class_1631 player, float equipProgress, float swingProgress) {
   }

   @Shadow
   private void method_9869(class_1631 player, float pitch, float equipProgress, float swingProgress) {
   }

   @Shadow
   private void method_9878(float swingProgress) {
   }

   @Shadow
   private void method_9873(float equipProgress, float swingProgress) {
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
   private void method_9872(class_1699 entity, class_1071 stack, class_2464 mode) {
   }

   @Inject(method = "method_1354", at = @At("HEAD"), cancellable = true)
   private void client$renderTargetArm(float tickDelta, CallbackInfo ci) {
      class_1631 target = CameraController.getFirstPersonHandPlayer();
      if (target != null) {
         float f = 1.0F - (this.field_1879 + (this.field_1878 - this.field_1879) * tickDelta);
         float pitch = target.field_3195 + (target.field_3193 - target.field_3195) * tickDelta;
         float swing = target.method_6148(tickDelta);
         this.method_9866(target);
         class_2403.method_9788();
         class_2403.method_9791();
         class_1071 stack = this.field_1877;
         if (stack != null) {
            if (stack.method_3421() == class_1734.field_7026) {
               this.method_9869(target, pitch, f, swing);
            } else if (target.method_3192() > 0) {
               class_1099 action = stack.method_3444();
               if (action == class_1099.field_9157 || action == class_1099.field_9158) {
                  this.method_9867(target, tickDelta);
                  this.method_9873(f, 0.0F);
               } else if (action == class_1099.field_9159) {
                  this.method_9873(f, 0.0F);
                  this.method_9877();
               } else if (action == class_1099.field_9160) {
                  this.method_9873(f, 0.0F);
                  this.method_9863(tickDelta, target);
               } else {
                  this.method_9873(f, 0.0F);
               }

               this.method_9872(target, stack, class_2464.field_10994);
            } else {
               this.method_9878(swing);
               this.method_9873(f, swing);
               this.method_9872(target, stack, class_2464.field_10994);
            }
         } else {
            this.method_9868(target, f, swing);
         }

         class_2403.method_9792();
         class_2403.method_9789();
         class_328.method_843();
         ci.cancel();
      }
   }

   @ModifyVariable(method = "method_1353", at = @At("STORE"), index = 1)
   private class_988 client$targetHeldItemPlayer(class_988 original) {
      class_1631 target = CameraController.getFirstPersonHandPlayer();
      return (class_988)(target != null ? target : original);
   }

   @ModifyVariable(method = "method_1354", at = @At("STORE"), index = 3)
   private class_1631 client$targetHandPlayer(class_1631 original) {
      class_1631 target = CameraController.getFirstPersonHandPlayer();
      return target != null ? target : original;
   }

   @ModifyVariable(method = "method_9868", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private class_1631 client$targetArmSkin(class_1631 original) {
      class_1631 target = CameraController.getFirstPersonHandPlayer();
      return target != null ? target : original;
   }

   @Redirect(method = "method_1354", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1631;method_4446()Z"))
   private boolean client$armIsVisible(class_1631 player) {
      return CameraController.getFirstPersonHandPlayer() != null ? false : player.method_4446();
   }

   @Redirect(
      method = "method_9868",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_550;method_1519(Lnet/minecraft/class_864;)Lnet/minecraft/class_551;")
   )
   private class_551 client$targetArmRenderer(class_550 dispatcher, class_864 entity) {
      class_1631 target = CameraController.getFirstPersonHandPlayer();
      return dispatcher.method_1519((class_864)(target != null ? target : entity));
   }

   @Redirect(method = "method_9868", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_570;method_10305(Lnet/minecraft/class_1631;)V"))
   private void client$renderTargetRightArm(class_570 renderer, class_1631 player) {
      class_1631 target = CameraController.getFirstPersonHandPlayer();
      renderer.method_10305(target != null ? target : player);
   }
}
