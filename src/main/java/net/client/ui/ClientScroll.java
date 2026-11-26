package net.client.ui;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;

public final class ClientScroll {
   public float value;
   public float target;
   public boolean dragging;
   private float grabOffset;
   private float content;
   private float viewport;

   public void measure(float content, float viewport) {
      this.content = Math.max(0.0F, content);
      this.viewport = Math.max(1.0F, viewport);
      this.clamp();
   }

   public float max() {
      return Math.max(0.0F, this.content - this.viewport);
   }

   private void clamp() {
      float max = this.max();
      if (this.value < 0.0F) {
         this.value = 0.0F;
      }

      if (this.value > max) {
         this.value = max;
      }

      if (this.target < 0.0F) {
         this.target = 0.0F;
      }

      if (this.target > max) {
         this.target = max;
      }
   }

   public void jump(float v) {
      this.value = v;
      this.target = v;
      this.clamp();
   }

   public void scrollBy(float delta) {
      this.target += delta;
      this.clamp();
   }

   public void update(float dt) {
      float speed = Math.min(1.0F, dt * 15.0F);
      this.value = this.value + (this.target - this.value) * speed;
      if (Math.abs(this.target - this.value) < 0.6F) {
         this.value = this.target;
      }

      this.clamp();
   }

   public float thumbHeight() {
      return this.content <= 0.0F ? this.viewport : Math.max(24.0F, this.viewport * this.viewport / this.content);
   }

   public float thumbY(float trackY) {
      float max = this.max();
      float track = this.viewport - this.thumbHeight();
      return trackY + (max <= 0.0F ? 0.0F : this.value / max * Math.max(0.0F, track));
   }

   public boolean scrollable() {
      return this.content > this.viewport + 0.5F;
   }

   public void renderBar(Md3Canvas c, Md3Theme t, float x, float trackY, float width) {
      if (this.scrollable()) {
         float h = this.thumbHeight();
         float y = this.thumbY(trackY);
         c.roundRect(x, trackY, width, this.viewport, width / 2.0F, Md3Colors.alpha(t.colors.onSurface, 0.06F));
         c.roundRect(x, y, width, h, width / 2.0F, this.dragging ? t.colors.primary : Md3Colors.alpha(t.colors.onSurfaceVariant, 0.62F));
      }
   }

   public boolean press(float mx, float my, float x, float trackY, float width, float slop) {
      if (!this.scrollable()) {
         return false;
      } else if (mx < x - slop || mx > x + width + slop) {
         return false;
      } else if (!(my < trackY - slop) && !(my > trackY + this.viewport + slop)) {
         float thumbY = this.thumbY(trackY);
         float thumbH = this.thumbHeight();
         if (my >= thumbY && my <= thumbY + thumbH) {
            this.grabOffset = my - thumbY;
         } else {
            this.grabOffset = thumbH / 2.0F;
            this.dragTo(my - this.grabOffset, trackY);
         }

         this.dragging = true;
         return true;
      } else {
         return false;
      }
   }

   public void drag(float my, float trackY) {
      if (this.dragging) {
         this.dragTo(my - this.grabOffset, trackY);
      }
   }

   private void dragTo(float thumbTop, float trackY) {
      float track = this.viewport - this.thumbHeight();
      if (track <= 0.0F) {
         this.jump(0.0F);
      } else {
         float ratio = (thumbTop - trackY) / track;
         if (ratio < 0.0F) {
            ratio = 0.0F;
         }

         if (ratio > 1.0F) {
            ratio = 1.0F;
         }

         this.jump(ratio * this.max());
      }
   }

   public void release() {
      this.dragging = false;
   }

   public int rowAt(float my, float listTop, float rowHeight, int count) {
      if (rowHeight <= 0.0F) {
         return -1;
      } else {
         int index = (int)Math.floor((my - listTop + this.value) / rowHeight);
         return index >= 0 && index < count ? index : -1;
      }
   }
}
