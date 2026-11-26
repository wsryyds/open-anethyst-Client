package net.client.replay.render;

import java.nio.ByteBuffer;
import net.client.ClientConfig;
import net.client.mixin.ReplayGameRendererAccessor;
import net.client.replay.ReplayClient;
import net.minecraft.class_1600;
import net.minecraft.class_1862;
import net.minecraft.class_2403;
import net.minecraft.class_389;
import net.minecraft.class_629;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public final class FrameCapture implements AutoCloseable {
   private static boolean windowModeAnnounced;
   private final class_1600 client;
   private final int width;
   private final int height;
   private final class_1862 framebuffer;
   private final ByteBuffer buffer;

   public FrameCapture(class_1600 client, int width, int height) {
      this.client = client;
      this.width = width;
      this.height = height;
      if (class_629.method_7315()) {
         this.framebuffer = new class_1862(width, height, true);
         this.framebuffer.field_7989[0] = 0.0F;
         this.framebuffer.field_7989[1] = 0.0F;
         this.framebuffer.field_7989[2] = 0.0F;
         this.framebuffer.field_7989[3] = 1.0F;
      } else {
         this.framebuffer = null;
         if (!windowModeAnnounced) {
            windowModeAnnounced = true;
            ReplayClient.LOGGER.info("[replay] 环境未提供 FBO，改用窗口捕获 {}x{}", width, height);
         }
      }

      this.buffer = BufferUtils.createByteBuffer(width * height * 4);
   }

   public void capture(float partialTicks) {
      this.capture(partialTicks, System.nanoTime());
   }

   public void capture(float partialTicks, long chunkDeadline) {
      int oldWidth = this.client.field_3801;
      int oldHeight = this.client.field_3802;
      float oldFov = this.client.field_3823.field_958;
      ReplayGameRendererAccessor renderer = (ReplayGameRendererAccessor)this.client.field_3818;
      boolean oldRenderHand = renderer.client$getRenderHand();
      boolean oldHudHidden = this.client.field_3823 != null && this.client.field_3823.field_948;
      boolean oldScissor = GL11.glIsEnabled(3089);

      try {
         this.client.field_3801 = this.width;
         this.client.field_3802 = this.height;
         renderer.client$setRenderHand(true);
         if (this.client.field_3823 != null) {
            this.client.field_3823.field_948 = false;
         }

         if (oldScissor) {
            GL11.glDisable(3089);
         }

         if (this.framebuffer != null) {
            this.framebuffer.method_6894(true);
         }

         class_2403.method_9801(0.0F, 0.0F, 0.0F, 1.0F);
         class_2403.method_9845(16640);
         class_2403.method_9820(0, 0, this.width, this.height);
         this.client.field_3818.method_1326(partialTicks, chunkDeadline);
         if (this.client.field_3820 != null && (ReplayClient.getConfig() == null || ReplayClient.getConfig().showHud)) {
            class_389 hudWindow = new class_389(this.client);
            double guiW = hudWindow.method_1047();
            double guiH = hudWindow.method_1048();
            boolean oldChatVisible = ClientConfig.chatVisible;
            ClientConfig.chatVisible = false;
            class_2403.method_9847(5889);
            class_2403.method_9791();
            class_2403.method_9790();
            GL11.glOrtho(0.0, guiW, guiH, 0.0, 1000.0, 3000.0);
            class_2403.method_9847(5888);
            class_2403.method_9791();
            class_2403.method_9790();

            try {
               this.client.field_3820.method_9420(partialTicks);
            } finally {
               ClientConfig.chatVisible = oldChatVisible;
               class_2403.method_9847(5889);
               class_2403.method_9792();
               class_2403.method_9847(5888);
               class_2403.method_9792();
               class_2403.method_9820(0, 0, this.width, this.height);
            }
         }

         if (this.framebuffer != null) {
            this.framebuffer.method_6894(false);
         }

         GL11.glPixelStorei(3333, 1);
         this.buffer.clear();
         GL11.glReadPixels(0, 0, this.width, this.height, 6408, 5121, this.buffer);
         this.buffer.rewind();
      } finally {
         renderer.client$setRenderHand(oldRenderHand);
         this.client.field_3823.field_958 = oldFov;
         if (this.client.field_3823 != null) {
            this.client.field_3823.field_948 = oldHudHidden;
         }

         this.client.field_3801 = oldWidth;
         this.client.field_3802 = oldHeight;
         this.client.method_6633().method_6894(true);
         class_2403.method_9820(0, 0, oldWidth, oldHeight);
         if (oldScissor) {
            GL11.glEnable(3089);
         }
      }
   }

   public ByteBuffer getBuffer() {
      return this.buffer;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public boolean isWindowMode() {
      return this.framebuffer == null;
   }

   @Override
   public void close() {
      if (this.framebuffer != null) {
         this.framebuffer.method_6890();
      }
   }
}
