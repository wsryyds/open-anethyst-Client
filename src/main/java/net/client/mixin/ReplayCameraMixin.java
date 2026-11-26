package net.client.mixin;

import net.client.replay.session.CameraController;
import net.minecraft.class_321;
import net.minecraft.class_988;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(class_321.class)
public class ReplayCameraMixin {
   @ModifyVariable(method = "method_804", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private static class_988 client$overrideCameraPlayer(class_988 original) {
      class_988 target = CameraController.getCameraOverridePlayer();
      return target != null ? target : original;
   }
}
