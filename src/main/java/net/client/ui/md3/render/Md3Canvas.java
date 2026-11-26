package net.client.ui.md3.render;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Type;
import net.minecraft.class_2403;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class Md3Canvas {
   private final Deque<int[]> clips = new ArrayDeque<>();
   public int width;
   public int height;
   public int scale = 1;
   public float pixelScale = 1.0F;
   public boolean pixelSnap = true;
   private int framebufferHeight;
   private boolean textureOn = true;
   private int boundTexture = -1;
   private int currentColor = -1;
   private boolean active;
   private boolean prevDepth;
   private boolean prevCull;
   private boolean prevAlpha;
   private boolean prevLighting;
   private boolean prevBlend;
   private boolean prevTexture;
   private boolean prevScissor;
   private int prevTextureId;

   public void begin(int width, int height) {
      this.beginState(width, height, true);
   }

   public void beginPreserve(int width, int height) {
      this.beginState(width, height, false);
   }

   private void beginState(int width, int height, boolean guiMatrix) {
      this.width = width;
      this.height = height;
      this.pixelScale = width > 0 ? (float)Display.getWidth() / width : 1.0F;
      this.scale = Math.max(1, Math.round(this.pixelScale));
      this.pixelSnap = true;
      this.framebufferHeight = Display.getHeight();
      this.active = true;
      this.clips.clear();
      this.prevDepth = GL11.glIsEnabled(2929);
      this.prevCull = GL11.glIsEnabled(2884);
      this.prevAlpha = GL11.glIsEnabled(3008);
      this.prevLighting = GL11.glIsEnabled(2896);
      this.prevBlend = GL11.glIsEnabled(3042);
      this.prevTexture = GL11.glIsEnabled(3553);
      this.prevScissor = GL11.glIsEnabled(3089);
      this.prevTextureId = GL11.glGetInteger(32873);
      if (guiMatrix) {
         class_2403.method_9838();
      } else if (this.isWorldProjection()) {
         class_2403.method_9840();
      }

      class_2403.method_9850();
      class_2403.method_9843();
      class_2403.method_9805(770, 771, 1, 771);
      class_2403.method_9822();
      class_2403.method_9832();
      class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
      class_2403.method_9855();
      GL11.glDisable(3089);
      this.textureOn = true;
      this.boundTexture = -1;
      this.currentColor = -1;
      if (guiMatrix) {
         class_2403.method_9847(5889);
         class_2403.method_9791();
         class_2403.method_9790();
         class_2403.method_9797(0.0, width, height, 0.0, 1000.0, 3000.0);
         class_2403.method_9847(5888);
         class_2403.method_9791();
         class_2403.method_9790();
         class_2403.method_9816(0.0F, 0.0F, -2000.0F);
      }
   }

   public void end() {
      this.endState(true);
   }

   public void endPreserve() {
      this.endState(false);
   }

   private boolean isWorldProjection() {
      FloatBuffer projection = BufferUtils.createFloatBuffer(16);
      GL11.glGetFloat(2983, projection);
      return Math.abs(projection.get(0)) > 0.02F;
   }

   private void endState(boolean guiMatrix) {
      if (this.active) {
         this.active = false;

         while (!this.clips.isEmpty()) {
            this.popClip();
         }

         GL11.glDisable(3089);
         class_2403.method_9839(0);
         class_2403.method_9855();
         class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
         this.textureOn = true;
         this.boundTexture = -1;
         if (guiMatrix) {
            class_2403.method_9847(5888);
            class_2403.method_9792();
            class_2403.method_9847(5889);
            class_2403.method_9792();
            class_2403.method_9847(5888);
         }

         if (guiMatrix) {
            class_2403.method_9838();
         } else if (this.isWorldProjection()) {
            class_2403.method_9840();
         } else if (this.prevDepth) {
            class_2403.method_9840();
         } else {
            class_2403.method_9838();
         }

         if (this.prevCull) {
            class_2403.method_9848();
         } else {
            class_2403.method_9850();
         }

         if (this.prevAlpha) {
            class_2403.method_9828();
         } else {
            class_2403.method_9822();
         }

         if (this.prevLighting) {
            class_2403.method_9830();
         } else {
            class_2403.method_9832();
         }

         if (this.prevBlend) {
            class_2403.method_9843();
         } else {
            class_2403.method_9842();
         }

         if (this.prevTexture) {
            class_2403.method_9855();
         } else {
            class_2403.method_9856();
         }

         if (this.prevTextureId > 0) {
            class_2403.method_9839(this.prevTextureId);
         }

         if (this.prevScissor) {
            GL11.glEnable(3089);
         } else {
            GL11.glDisable(3089);
         }

         this.textureOn = this.prevTexture;
         this.boundTexture = this.prevTextureId;
      }
   }

   public void invalidateState() {
      this.boundTexture = -1;
      this.currentColor = -1;
      this.textureOn = GL11.glIsEnabled(3553);
      class_2403.method_9825(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void color(int argb) {
      if (argb != this.currentColor) {
         this.currentColor = argb;
         class_2403.method_9825(Md3Colors.r(argb) / 255.0F, Md3Colors.g(argb) / 255.0F, Md3Colors.b(argb) / 255.0F, Md3Colors.a(argb) / 255.0F);
      }
   }

   private void texture(int id) {
      if (id <= 0) {
         if (this.textureOn) {
            class_2403.method_9856();
            this.textureOn = false;
            this.boundTexture = -1;
         }
      } else {
         if (!this.textureOn) {
            class_2403.method_9855();
            this.textureOn = true;
            this.boundTexture = -1;
         }

         if (id != this.boundTexture) {
            class_2403.method_9839(id);
            this.boundTexture = id;
         }
      }
   }

   private void quad(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1) {
      GL11.glBegin(7);
      GL11.glTexCoord2f(u0, v0);
      GL11.glVertex2f(x0, y0);
      GL11.glTexCoord2f(u1, v0);
      GL11.glVertex2f(x1, y0);
      GL11.glTexCoord2f(u1, v1);
      GL11.glVertex2f(x1, y1);
      GL11.glTexCoord2f(u0, v1);
      GL11.glVertex2f(x0, y1);
      GL11.glEnd();
   }

   public void fill(float x, float y, float w, float h, int argb) {
      if (!(w <= 0.0F) && !(h <= 0.0F) && Md3Colors.a(argb) != 0) {
         this.texture(0);
         this.color(argb);
         GL11.glBegin(7);
         GL11.glVertex2f(x, y);
         GL11.glVertex2f(x + w, y);
         GL11.glVertex2f(x + w, y + h);
         GL11.glVertex2f(x, y + h);
         GL11.glEnd();
      }
   }

   public void image(int textureId, float x, float y, float w, float h, float u0, float v0, float u1, float v1, int argb) {
      if (!(w <= 0.0F) && !(h <= 0.0F) && Md3Colors.a(argb) != 0) {
         this.texture(textureId);
         this.color(argb);
         this.quad(x, y, x + w, y + h, u0, v0, u1, v1);
      }
   }

   public void roundRect(float x, float y, float w, float h, float radius, int argb) {
      if (!(w <= 0.0F) && !(h <= 0.0F) && Md3Colors.a(argb) != 0) {
         float r = Math.min(radius, Math.min(w, h) / 2.0F);
         if (r < 0.75F) {
            this.fill(x, y, w, h, argb);
         } else {
            int rpx = Math.max(1, Math.round(r * this.scale));
            int tex = Md3Shapes.fillCorner(rpx);
            this.fill(x + r, y, w - 2.0F * r, h, argb);
            this.fill(x, y + r, r, h - 2.0F * r, argb);
            this.fill(x + w - r, y + r, r, h - 2.0F * r, argb);
            this.image(tex, x, y, r, r, 0.0F, 0.0F, 1.0F, 1.0F, argb);
            this.image(tex, x + w - r, y, r, r, 1.0F, 0.0F, 0.0F, 1.0F, argb);
            this.image(tex, x, y + h - r, r, r, 0.0F, 1.0F, 1.0F, 0.0F, argb);
            this.image(tex, x + w - r, y + h - r, r, r, 1.0F, 1.0F, 0.0F, 0.0F, argb);
         }
      }
   }

   public void roundRectTop(float x, float y, float w, float h, float radius, int argb) {
      if (!(w <= 0.0F) && !(h <= 0.0F) && Md3Colors.a(argb) != 0) {
         float r = Math.min(radius, Math.min(w / 2.0F, h));
         if (r < 0.75F) {
            this.fill(x, y, w, h, argb);
         } else {
            int rpx = Math.max(1, Math.round(r * this.scale));
            int tex = Md3Shapes.fillCorner(rpx);
            this.fill(x, y + r, w, h - r, argb);
            this.fill(x + r, y, w - 2.0F * r, r, argb);
            this.image(tex, x, y, r, r, 0.0F, 0.0F, 1.0F, 1.0F, argb);
            this.image(tex, x + w - r, y, r, r, 1.0F, 0.0F, 0.0F, 1.0F, argb);
         }
      }
   }

   public void strokeRoundRect(float x, float y, float w, float h, float radius, float stroke, int argb) {
      if (!(w <= 0.0F) && !(h <= 0.0F) && Md3Colors.a(argb) != 0) {
         float r = Math.min(radius, Math.min(w, h) / 2.0F);
         float s = Math.max(stroke, 1.0F / this.scale);
         int spx = Math.max(1, Math.round(s * this.scale));
         if (r < 0.75F) {
            this.fill(x, y, w, s, argb);
            this.fill(x, y + h - s, w, s, argb);
            this.fill(x, y + s, s, h - 2.0F * s, argb);
            this.fill(x + w - s, y + s, s, h - 2.0F * s, argb);
         } else {
            int rpx = Math.max(1, Math.round(r * this.scale));
            int tex = Md3Shapes.strokeCorner(rpx, spx);
            this.fill(x + r, y, w - 2.0F * r, s, argb);
            this.fill(x + r, y + h - s, w - 2.0F * r, s, argb);
            this.fill(x, y + r, s, h - 2.0F * r, argb);
            this.fill(x + w - s, y + r, s, h - 2.0F * r, argb);
            this.image(tex, x, y, r, r, 0.0F, 0.0F, 1.0F, 1.0F, argb);
            this.image(tex, x + w - r, y, r, r, 1.0F, 0.0F, 0.0F, 1.0F, argb);
            this.image(tex, x, y + h - r, r, r, 0.0F, 1.0F, 1.0F, 0.0F, argb);
            this.image(tex, x + w - r, y + h - r, r, r, 1.0F, 1.0F, 0.0F, 0.0F, argb);
         }
      }
   }

   public void shadow(float x, float y, float w, float h, float radius, int level) {
      if (level > 0 && !(w <= 0.0F) && !(h <= 0.0F)) {
         float blur = 2.0F + level * 1.5F;
         int layers = 4 + level;
         float total = 0.1F + level * 0.022F;
         float per = total / layers;
         int alpha = Math.max(1, (int)(per * 255.0F));
         int color = Md3Colors.argb(alpha, 0, 0, 0);
         float offsetY = level * 0.7F;

         for (int i = layers; i >= 1; i--) {
            float grow = blur * i / layers;
            this.roundRect(x - grow, y - grow + offsetY, w + 2.0F * grow, h + 2.0F * grow, radius + grow, color);
         }
      }
   }

   public void glow(float x, float y, float w, float h, int argb) {
      this.image(Md3Shapes.glow(), x, y, w, h, 0.0F, 0.0F, 1.0F, 1.0F, argb);
   }

   public void verticalGradient(float x, float y, float w, float h, int top, int bottom) {
      int bands = 24;

      for (int i = 0; i < bands; i++) {
         float t0 = (float)i / bands;
         float t1 = (float)(i + 1) / bands;
         int c = Md3Colors.lerp(top, bottom, (t0 + t1) * 0.5F);
         this.fill(x, y + h * t0, w, h * (t1 - t0) + 0.5F, c);
      }
   }

   public void pushMatrix() {
      class_2403.method_9791();
   }

   public void popMatrix() {
      class_2403.method_9792();
   }

   public void scale(float factor) {
      class_2403.method_9800(factor, factor, 1.0F);
   }

   public void translate(float x, float y) {
      class_2403.method_9816(x, y, 0.0F);
   }

   public void rotate(float degrees) {
      class_2403.method_9817(degrees, 0.0F, 0.0F, 1.0F);
   }

   public float text(String text, float x, float yTop, Md3Type type, int color) {
      return Md3Fonts.draw(this, text, x, yTop, type, color);
   }

   public float textWidth(String text, Md3Type type) {
      return Md3Fonts.width(text, type);
   }

   public void textCentered(String text, float centerX, float yTop, Md3Type type, int color) {
      this.text(text, centerX - this.textWidth(text, type) / 2.0F, yTop, type, color);
   }

   public void icon(String name, float x, float y, float size, int color) {
      Md3Icons.draw(this, name, x, y, size, color);
   }

   public void pushClip(float x, float y, float w, float h) {
      int nx = (int)Math.floor(x);
      int ny = (int)Math.floor(y);
      int nr = (int)Math.ceil(x + w);
      int nb = (int)Math.ceil(y + h);
      if (!this.clips.isEmpty()) {
         int[] p = this.clips.peek();
         nx = Math.max(nx, p[0]);
         ny = Math.max(ny, p[1]);
         nr = Math.min(nr, p[0] + p[2]);
         nb = Math.min(nb, p[1] + p[3]);
      }

      int nw = Math.max(0, nr - nx);
      int nh = Math.max(0, nb - ny);
      this.clips.push(new int[]{nx, ny, nw, nh});
      this.applyScissor(nx, ny, nw, nh);
   }

   public void popClip() {
      if (!this.clips.isEmpty()) {
         this.clips.pop();
         if (this.clips.isEmpty()) {
            GL11.glDisable(3089);
         } else {
            int[] p = this.clips.peek();
            this.applyScissor(p[0], p[1], p[2], p[3]);
         }
      }
   }

   private void applyScissor(int x, int y, int w, int h) {
      if (w > 0 && h > 0) {
         GL11.glEnable(3089);
         int px = (int)Math.floor(x * this.pixelScale);
         int py = (int)Math.floor(this.framebufferHeight - (y + h) * this.pixelScale);
         int pw = (int)Math.ceil(w * this.pixelScale);
         int ph = (int)Math.ceil(h * this.pixelScale);
         GL11.glScissor(px, py, pw, ph);
      } else {
         GL11.glEnable(3089);
         GL11.glScissor(0, 0, 0, 0);
      }
   }
}
