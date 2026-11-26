package net.client.mixin;

import net.client.ClientConfig;
import net.client.ClientScreen;
import net.client.util.AfterimageHandler;
import net.client.util.FreelookHandler;
import net.client.util.OldBlockHitSwing;
import net.minecraft.class_1600;
import net.minecraft.class_234;
import net.minecraft.class_347;
import net.minecraft.class_388;
import net.minecraft.class_518;
import net.minecraft.class_624;
import net.minecraft.class_234.class_235;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1600.class)
public class MinecraftClientMixin {
   @Shadow
   private class_388 field_3816;
   @Shadow
   private class_518 field_10310;
   @Shadow
   private class_347 field_3823;
   @Shadow
   private int field_3781;
   @Shadow
   public class_234 field_3822;
   @Unique
   private boolean client$shiftWasDown;

   @Shadow
   public void method_2928(class_388 screen) {
   }

   @Inject(method = "method_4498", at = @At("HEAD"), cancellable = true)
   private void client$unlockMenuFps(CallbackInfoReturnable<Integer> cir) {
      if (this.field_3823 != null) {
         cir.setReturnValue(this.field_3823.field_978);
      }
   }

   @Inject(method = "method_6644", at = @At("HEAD"))
   private void client$attackGuard(CallbackInfo ci) {
      this.field_3781 = 0;
   }

   @Inject(method = "method_6644", at = @At("RETURN"))
   private void client$hitDelayFix(CallbackInfo ci) {
      if (this.field_3822 != null && this.field_3822.field_595 == class_235.field_603 && this.field_3822.field_601 != null) {
         this.field_3781 = 0;
      }
   }

   @Inject(method = "method_6638", at = @At("HEAD"))
   private void client$oldBlockHitSwing(boolean breaking, CallbackInfo ci) {
      if (breaking) {
         OldBlockHitSwing.keepAlive((class_1600)(Object)this);
      }
   }

   @Inject(method = "method_2954", at = @At("TAIL"))
   private void client$onTick(CallbackInfo ci) {
      ClientConfig.ensureLoaded();
      ClientConfig.flushIfDirty();
      boolean shiftDown = Keyboard.isKeyDown(54);
      if (shiftDown && !this.client$shiftWasDown) {
         if (this.field_3816 instanceof ClientScreen) {
            this.method_2928(null);
         } else if (this.field_3816 == null || this.field_3816 instanceof class_624) {
            this.method_2928(new ClientScreen());
         }
      }

      this.client$shiftWasDown = shiftDown;
      AfterimageHandler.tick();
      FreelookHandler.tick();
      if (ClientConfig.autoSprint
         && this.field_3816 == null
         && this.field_10310 != null
         && this.field_3823 != null
         && this.field_3823.field_993.method_6619()
         && !this.field_10310.method_2513()
         && this.field_10310.method_3188().method_3238() > 6) {
         this.field_10310.method_2529(true);
      }
   }
}
