package net.client.mixin;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import net.client.ClientConfig;
import net.client.replay.ReplayClient;
import net.client.util.ClientLog;
import net.minecraft.class_1150;
import net.minecraft.class_1600;
import net.minecraft.class_1862;
import net.minecraft.class_2403;
import net.minecraft.class_2520;
import net.minecraft.class_520;
import net.minecraft.class_524;
import net.minecraft.class_533;
import net.minecraft.class_864;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_524.class, priority = 1100)
public class MotionBlurMixin {
   private static final int STYLE_TEMPORAL = 0;
   private static final int STYLE_LINEAR = 1;
   private static final int STYLE_VELOCITY = 2;
   private static final int STYLE_SOFT = 3;
   private static final int GL_QUADS = 7;
   private static final int GL_TEXTURE_2D = 3553;
   private static final float MAX_FRAME_RETAIN = 0.995F;
   private static final int VELOCITY_TAPS = 5;
   private static final float REF_DISTANCE = 4.0F;
   private static final long LOG_INTERVAL_NANO = 10000000000L;
   private static final float ACC_STEP = 0.016666668F;
   private static int lc$tex = -1;
   private static int lc$texPrev = -1;
   private static boolean lc$prevValid = false;
   private static int lc$w = 0;
   private static int lc$h = 0;
   private static int lc$style = -1;
   private static WeakReference<class_1150> lc$world = new WeakReference<>(null);
   private static boolean lc$hasCamera = false;
   private static float lc$lastYaw = 0.0F;
   private static float lc$lastPitch = 0.0F;
   private static double lc$lastCamX = 0.0;
   private static double lc$lastCamY = 0.0;
   private static double lc$lastCamZ = 0.0;
   private static float lc$velX = 0.0F;
   private static float lc$velY = 0.0F;
   private static long lc$lastFrameNano = -1L;
   private static long lc$lastLogNano = -1L;
   private static float lc$accTime = 0.0F;
   private static boolean lc$savedScissor = false;
   private static boolean lc$savedAlphaTest = false;
   private static boolean lc$savedLighting = false;

   @Inject(method = "method_1326", at = @At("TAIL"))
   private void client$motionBlur(float tickDelta, long limitTime, CallbackInfo ci) {
      class_1600 client = class_1600.method_2965();
      boolean capturing = ReplayClient.isReplayCaptureActive() || ReplayClient.getRenderManager() != null && ReplayClient.getRenderManager().isRendering();
      if (ClientConfig.motionBlur
         && client.field_3803 != null
         && client.field_10310 != null
         && !capturing
         && (client.field_3816 == null || !(ClientConfig.backdropBlur > 0.001F))) {
         class_1862 framebuffer = client.method_6633();
         if (framebuffer != null) {
            int w = framebuffer.field_7983;
            int h = framebuffer.field_7984;
            if (w > 0 && h > 0) {
               int style = ClientConfig.blurStyle;
               if (style < 0 || style > 3) {
                  style = 0;
               }

               float strength = ClientConfig.blurAmount;
               if (!(strength <= 0.0F)) {
                  if (strength > 1.0F) {
                     strength = 1.0F;
                  }

                  class_1150 world = client.field_3803;
                  class_1150 tracked = lc$world.get();
                  if (tracked != world) {
                     lc$world = new WeakReference<>(world);
                     lc$hasCamera = false;
                     lc$lastFrameNano = -1L;
                     lc$velX = 0.0F;
                     lc$velY = 0.0F;
                     lc$accTime = 0.0F;
                  }

                  boolean recreate = lc$tex == -1 || lc$w != w || lc$h != h || lc$style != style;
                  if (recreate) {
                     lc$releaseTexture();
                     lc$tex = lc$createTexture(w, h);
                     lc$prevValid = false;
                     if (style == 2) {
                        lc$texPrev = lc$createTexture(w, h);
                     }

                     lc$w = w;
                     lc$h = h;
                     lc$style = style;
                     lc$lastFrameNano = -1L;
                     lc$velX = 0.0F;
                     lc$velY = 0.0F;
                     lc$hasCamera = false;
                     lc$accTime = 0.0F;
                  }

                  long now = System.nanoTime();
                  float dt = 0.016666668F;
                  if (lc$lastFrameNano > 0L) {
                     dt = (float)((now - lc$lastFrameNano) / 1.0E9);
                  }

                  lc$lastFrameNano = now;
                  if (dt < 0.004F) {
                     dt = 0.004F;
                  }

                  if (dt > 0.05F) {
                     dt = 0.05F;
                  }

                  lc$updateVelocity(client, w, h, dt);
                  if (recreate) {
                     lc$copyScreenToTexture(lc$tex, w, h);
                  } else {
                     lc$accTime += dt;
                     boolean accFrame = lc$accTime >= 0.016666668F;
                     float blendDt = accFrame ? lc$accTime : 0.016666668F;
                     lc$begin(w, h);

                     try {
                        switch (style) {
                           case 0:
                           default:
                              lc$drawTemporal(strength, blendDt, w, h);
                              if (accFrame) {
                                 lc$copyScreenToTexture(lc$tex, w, h);
                                 lc$accTime = 0.0F;
                              }
                              break;
                           case 1:
                              lc$drawLinear(strength, blendDt, w, h);
                              if (accFrame) {
                                 lc$copyScreenToTexture(lc$tex, w, h);
                                 lc$accTime = 0.0F;
                              }
                              break;
                           case 2:
                              lc$copyScreenToTexture(lc$tex, w, h);
                              lc$drawVelocity(strength, w, h);
                              if (lc$prevValid && lc$texPrev != -1) {
                                 lc$quadTex(lc$texPrev, 0.0F, 0.0F, w, h, lc$retain(lc$halfLife(2, strength), blendDt));
                              }

                              if (accFrame) {
                                 lc$copyScreenToTexture(lc$texPrev, w, h);
                                 lc$prevValid = true;
                                 lc$accTime = 0.0F;
                              }
                              break;
                           case 3:
                              lc$drawSoft(strength, blendDt, w, h);
                              if (accFrame) {
                                 lc$copyScreenToTexture(lc$tex, w, h);
                                 lc$accTime = 0.0F;
                              }
                        }
                     } finally {
                        lc$end();
                     }

                     lc$logOnce(style, strength, w, h, dt, now);
                  }
               }
            }
         }
      } else {
         lc$hasCamera = false;
         lc$lastFrameNano = -1L;
      }
   }

   private static float lc$retain(float halfLife, float dt) {
      if (halfLife < 0.005F) {
         halfLife = 0.005F;
      }

      double retain60 = Math.pow(0.5, 1.0 / (halfLife * 60.0));
      float r = (float)Math.pow(retain60, dt * 60.0F);
      return r > 0.995F ? 0.995F : r;
   }

   private static float lc$halfLife(int style, float strength) {
      switch (style) {
         case 0:
         default:
            return 0.03F + 0.19F * strength;
         case 1:
            return 0.03F + 0.27F * strength;
         case 2:
            return 0.02F + 0.1F * strength;
         case 3:
            return 0.03F + 0.23F * strength;
      }
   }

   private static void lc$drawTemporal(float strength, float dt, int w, int h) {
      float retain = lc$retain(lc$halfLife(0, strength), dt);
      float shift = 0.6F;
      lc$quad(-shift, 0.0F, w - shift, h, retain * 0.14F);
      lc$quad(shift, 0.0F, w + shift, h, retain * 0.14F);
      lc$quad(0.0F, 0.0F, w, h, retain * 0.72F);
   }

   private static void lc$drawLinear(float strength, float dt, int w, int h) {
      float retain = lc$retain(lc$halfLife(1, strength), dt);
      lc$quad(0.0F, 0.0F, w, h, retain);
   }

   private static void lc$drawVelocity(float strength, int w, int h) {
      float vx = lc$velX * strength;
      float vy = lc$velY * strength;
      float len = (float)Math.sqrt(vx * vx + vy * vy);
      if (!(len < 0.5F)) {
         float maxLen = 0.1F * w;
         if (len > maxLen) {
            float k = maxLen / len;
            vx *= k;
            vy *= k;
         }

         for (int i = 1; i <= 5; i++) {
            float k = i / 5.0F;
            float alpha = 1.0F / (i + 1);
            float ox = vx * k;
            float oy = vy * k;
            lc$quad(ox, oy, w + ox, h + oy, alpha);
         }
      }
   }

   private static void lc$drawSoft(float strength, float dt, int w, int h) {
      float retain = lc$retain(lc$halfLife(3, strength), dt);
      float radius = 0.5F + 1.5F * strength;
      lc$quad(0.0F, 0.0F, w, h, retain * 0.72F);
      lc$quad(-radius, -radius, w - radius, h - radius, retain * 0.14F);
      lc$quad(radius, radius, w + radius, h + radius, retain * 0.14F);
   }

   private static void lc$updateVelocity(class_1600 client, int w, int h, float dt) {
      class_864 camera = (class_864)(client.method_9388() != null ? client.method_9388() : client.field_10310);
      if (camera != null) {
         float yaw = camera.field_3258;
         float pitch = camera.field_3193;
         double px = camera.field_3252;
         double py = camera.field_3253;
         double pz = camera.field_3254;
         if (!lc$hasCamera) {
            lc$lastYaw = yaw;
            lc$lastPitch = pitch;
            lc$lastCamX = px;
            lc$lastCamY = py;
            lc$lastCamZ = pz;
            lc$hasCamera = true;
            lc$velX = 0.0F;
            lc$velY = 0.0F;
         } else {
            float dyaw = yaw - lc$lastYaw;

            while (dyaw > 180.0F) {
               dyaw -= 360.0F;
            }

            while (dyaw < -180.0F) {
               dyaw += 360.0F;
            }

            float dpitch = pitch - lc$lastPitch;
            double dx = px - lc$lastCamX;
            double dy = py - lc$lastCamY;
            double dz = pz - lc$lastCamZ;
            lc$lastYaw = yaw;
            lc$lastPitch = pitch;
            lc$lastCamX = px;
            lc$lastCamY = py;
            lc$lastCamZ = pz;
            double moveLen = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (moveLen > 12.0) {
               dx = 0.0;
               dy = 0.0;
               dz = 0.0;
            }

            float fovV = client.field_3823.field_958;
            if (fovV < 10.0F) {
               fovV = 10.0F;
            }

            float aspect = (float)w / h;
            float fovH = (float)Math.toDegrees(2.0 * Math.atan(Math.tan(Math.toRadians(fovV) * 0.5) * aspect));
            float pxPerDegX = w / fovH;
            float pxPerDegY = h / fovV;
            float yawRad = (float)Math.toRadians(yaw);
            float rightX = -((float)Math.cos(yawRad));
            float rightZ = -((float)Math.sin(yawRad));
            float lateral = (float)(dx * rightX + dz * rightZ);
            float vertical = (float)dy;
            float shutter = 1.0F / (60.0F * dt);
            if (shutter < 0.25F) {
               shutter = 0.25F;
            }

            if (shutter > 4.0F) {
               shutter = 4.0F;
            }

            float targetX = (-dyaw * pxPerDegX - lateral * pxPerDegX / 4.0F) * shutter;
            float targetY = (-dpitch * pxPerDegY + vertical * pxPerDegY / 4.0F) * shutter;
            float ease = 0.35F;
            lc$velX = lc$velX + (targetX - lc$velX) * ease;
            lc$velY = lc$velY + (targetY - lc$velY) * ease;
         }
      }
   }

   private static String lc$styleName(int style) {
      switch (style) {
         case 1:
            return "linear";
         case 2:
            return "velocity";
         case 3:
            return "soft";
         default:
            return "temporal";
      }
   }

   private static void lc$logOnce(int style, float strength, int w, int h, float dt, long now) {
      if (lc$lastLogNano <= 0L || now - lc$lastLogNano >= 10000000000L) {
         lc$lastLogNano = now;
         int fps = Math.round(1.0F / dt);
         float halfLife = lc$halfLife(style, strength);
         float stepAlpha = 1.0F - lc$retain(halfLife, 0.016666668F);
         ClientLog.info(
            String.format(
               "[MotionBlur] style=%s strength=%.2f size=%dx%d fps=%d halfLife=%.0fms stepAlpha=%.3f vel=%.1f,%.1f",
               lc$styleName(style),
               strength,
               w,
               h,
               fps,
               halfLife * 1000.0F,
               stepAlpha,
               lc$velX,
               lc$velY
            )
         );
      }
   }

   private static void lc$begin(int w, int h) {
      lc$savedScissor = GL11.glIsEnabled(3089);
      lc$savedAlphaTest = GL11.glIsEnabled(3008);
      lc$savedLighting = GL11.glIsEnabled(2896);
      if (lc$savedScissor) {
         GL11.glDisable(3089);
      }

      if (lc$savedAlphaTest) {
         class_2403.method_9822();
      }

      if (lc$savedLighting) {
         class_2403.method_9832();
      }

      class_2403.method_9847(5889);
      class_2403.method_9791();
      class_2403.method_9790();
      GL11.glOrtho(0.0, w, h, 0.0, -1.0, 1.0);
      class_2403.method_9847(5888);
      class_2403.method_9791();
      class_2403.method_9790();
      class_2403.method_9838();
      class_2403.method_9850();
      class_2403.method_9843();
      class_2403.method_9819(770, 771);
      class_2403.method_9855();
      class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static void lc$end() {
      class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
      class_2403.method_9842();
      class_2403.method_9840();
      class_2403.method_9848();
      class_2403.method_9847(5889);
      class_2403.method_9792();
      class_2403.method_9847(5888);
      class_2403.method_9792();
      class_2403.method_9839(0);
      if (lc$savedLighting) {
         class_2403.method_9830();
      }

      if (lc$savedAlphaTest) {
         class_2403.method_9828();
      }

      if (lc$savedScissor) {
         GL11.glEnable(3089);
      }
   }

   private static void lc$quad(float x0, float y0, float x1, float y1, float alpha) {
      lc$quadTex(lc$tex, x0, y0, x1, y1, alpha);
   }

   private static void lc$quadTex(int texture, float x0, float y0, float x1, float y1, float alpha) {
      if (texture != -1 && !(alpha <= 0.0F)) {
         class_2403.method_9839(texture);
         class_533 tessellator = class_533.method_9926();
         class_520 buffer = tessellator.method_9928();
         buffer.method_9737(7, class_2520.field_11210);
         buffer.method_9742(x0, y1, 0.0).method_9728(0.0, 0.0).method_9731(1.0F, 1.0F, 1.0F, alpha).method_9750();
         buffer.method_9742(x1, y1, 0.0).method_9728(1.0, 0.0).method_9731(1.0F, 1.0F, 1.0F, alpha).method_9750();
         buffer.method_9742(x1, y0, 0.0).method_9728(1.0, 1.0).method_9731(1.0F, 1.0F, 1.0F, alpha).method_9750();
         buffer.method_9742(x0, y0, 0.0).method_9728(0.0, 1.0).method_9731(1.0F, 1.0F, 1.0F, alpha).method_9750();
         tessellator.method_9927();
      }
   }

   private static void lc$copyScreenToTexture(int texture, int w, int h) {
      if (texture != -1) {
         class_2403.method_9839(texture);
         GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, w, h);
         class_2403.method_9839(0);
      }
   }

   private static int lc$createTexture(int w, int h) {
      int id = GL11.glGenTextures();
      class_2403.method_9839(id);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 33071);
      GL11.glTexParameteri(3553, 10243, 33071);
      ByteBuffer empty = ByteBuffer.allocateDirect(w * h * 4);
      GL11.glTexImage2D(3553, 0, 6408, w, h, 0, 6408, 5121, empty);
      return id;
   }

   private static void lc$releaseTexture() {
      if (lc$tex != -1) {
         GL11.glDeleteTextures(lc$tex);
         lc$tex = -1;
      }

      if (lc$texPrev != -1) {
         GL11.glDeleteTextures(lc$texPrev);
         lc$texPrev = -1;
      }

      lc$prevValid = false;
      lc$w = 0;
      lc$h = 0;
   }
}
