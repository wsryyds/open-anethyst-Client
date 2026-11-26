package net.client.mixin;

import java.nio.FloatBuffer;
import net.client.ClientConfig;
import net.client.util.AfterimageHandler;
import net.client.util.FovController;
import net.client.util.FreelookHandler;
import net.client.util.OverlayRender;
import net.minecraft.class_1150;
import net.minecraft.class_1175;
import net.minecraft.class_1600;
import net.minecraft.class_1699;
import net.minecraft.class_2403;
import net.minecraft.class_2552;
import net.minecraft.class_347;
import net.minecraft.class_524;
import net.minecraft.class_864;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = class_524.class, priority = 1200)
public class GameRendererMixin {
   @Inject(method = "method_1325", at = @At("HEAD"))
   private void client$freelookApply(float tickDelta, int anaglyphFilter, CallbackInfo ci) {
      FreelookHandler.applyCamera();
   }

   @Inject(method = "method_1325", at = @At("RETURN"))
   private void client$freelookRestore(float tickDelta, int anaglyphFilter, CallbackInfo ci) {
      FreelookHandler.restoreCamera();
   }

   @Shadow
   private FloatBuffer method_1324(float red, float green, float blue, float alpha) {
      return null;
   }

   @Inject(method = "method_9776", at = @At("HEAD"))
   private void client$worldDepth(int anaglyphFilter, float tickDelta, long limitTime, CallbackInfo ci) {
      class_2403.method_9840();
      class_2403.method_9811(true);
   }

   @Inject(method = "method_9776", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_524;method_1332(FI)V", shift = Shift.BEFORE))
   private void client$afterimage(int anaglyphFilter, float tickDelta, long limitTime, CallbackInfo ci) {
      AfterimageHandler.onRenderWorld(tickDelta);
   }

   @Inject(
      method = "method_9776",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_530;method_9894(Lnet/minecraft/class_2165;DILnet/minecraft/class_864;)I",
         ordinal = 0,
         shift = Shift.BEFORE
      )
   )
   private void client$skyFill(int anaglyphFilter, float tickDelta, long limitTime, CallbackInfo ci) {
      if (ClientConfig.fogModify) {
         int c = ClientConfig.fogColor;
         float r = (c >> 16 & 0xFF) / 255.0F;
         float g = (c >> 8 & 0xFF) / 255.0F;
         float b = (c & 0xFF) / 255.0F;
         GL11.glDisable(3089);
         GL11.glColorMask(true, true, true, true);
         class_2403.method_9801(r, g, b, 1.0F);
         GL11.glClear(16384);
      }
   }

   @Inject(method = "method_1336", at = @At("HEAD"), cancellable = true)
   private void client$noHurtCam(float tickDelta, CallbackInfo ci) {
      if (ClientConfig.noHurtCam) {
         class_864 camera = class_1600.method_2965().method_9388();
         if (!(camera instanceof class_1699) || !(((class_1699)camera).method_6122() <= 0.0F)) {
            ci.cancel();
         }
      }
   }

   @Redirect(method = "method_4302", at = @At(value = "FIELD", target = "Lnet/minecraft/class_347;field_959:F", opcode = 180))
   private float client$gamma(class_347 options) {
      return ClientConfig.fullbright ? 100.0F : options.field_959;
   }

   @Inject(method = "method_1338", at = @At("HEAD"), cancellable = true)
   private void client$minimalBob(float tickDelta, CallbackInfo ci) {
      if (ClientConfig.minimalBobbing && class_1600.method_2965().field_3823.field_975) {
         ci.cancel();
      }
   }

   @Redirect(method = "method_1327", at = @At(value = "FIELD", target = "Lnet/minecraft/class_347;field_958:F", opcode = 180))
   private float client$fovBase(class_347 options) {
      return ClientConfig.fovClient ? ClientConfig.fovBase * ClientConfig.fovGlobal : options.field_958;
   }

   @Inject(method = "method_1327", at = @At("RETURN"), cancellable = true)
   private void client$fovReturn(float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
      if (!ClientConfig.fovClient) {
         FovController.reset();
      } else if (changingFov) {
         class_1600 client = class_1600.method_2965();
         if (client.field_3803 != null) {
            cir.setReturnValue(FovController.apply(client, tickDelta));
         }
      }
   }

   @Redirect(method = "method_1329", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glFog(ILjava/nio/FloatBuffer;)V"))
   private void client$fogColor(int pname, FloatBuffer buffer) {
      if (ClientConfig.fogModify) {
         int color = ClientConfig.fogColor;
         buffer = this.method_1324((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
      }

      GL11.glFog(pname, buffer);
   }

   @Redirect(method = "method_1329", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2403;method_9815(F)V"))
   private void client$fogStart(float value) {
      class_2403.method_9815(value / clampDensity());
   }

   @Redirect(method = "method_1329", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2403;method_9823(F)V"))
   private void client$fogEnd(float value) {
      class_2403.method_9823(value / clampDensity());
   }

   private static float clampDensity() {
      float d = ClientConfig.fogDensity;
      if (d < 0.1F) {
         d = 0.1F;
      }

      if (d > 30.0F) {
         d = 30.0F;
      }

      return d;
   }

   @Redirect(
      method = "method_1334",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1150;method_8562(Lnet/minecraft/class_2552;)Lnet/minecraft/class_2552;"),
      require = 0
   )
   private class_2552 client$precipHeight(class_1150 world, class_2552 pos) {
      class_2552 height = world.method_8562(pos);
      return ClientConfig.weatherMode > 0 && ClientConfig.weatherThroughBlocks ? new class_2552(height.method_10572(), 0, height.method_10574()) : height;
   }

   @Redirect(method = "method_1334", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1175;method_3852(FI)F"), require = 0)
   private float client$weatherTemperature(class_1175 source, float temperature, int seaLevel) {
      int mode = ClientConfig.weatherMode;
      if (mode == 4) {
         return 0.0F;
      } else {
         return mode != 2 && mode != 3 ? source.method_3852(temperature, seaLevel) : 1.0F;
      }
   }

   @Inject(method = "method_1341", at = @At("HEAD"), cancellable = true)
   private void client$noRainSplashWhileSnow(CallbackInfo ci) {
      if (ClientConfig.weatherMode == 4) {
         ci.cancel();
      }
   }

   @Inject(method = "method_1326", at = @At("TAIL"))
   private void client$worldTail(float tickDelta, long nanoTime, CallbackInfo ci) {
      OverlayRender.render(tickDelta);
   }
}
