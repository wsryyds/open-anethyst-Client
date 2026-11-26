package net.client.ui.md3.widget;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;

public class Md3Slider extends Md3Widget {
   public float value;
   public boolean enabled = true;
   public int surface;
   private boolean dragging;
   private float halo;

   public Md3Slider(float value) {
      this.value = value;
   }

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      float trackY = this.y + this.h / 2.0F - 2.0F;
      float thumbR = 10.0F;
      float travel = this.w - thumbR * 2.0F;
      boolean over = this.enabled && mx >= this.x && mx <= this.x + this.w && my >= this.y && my <= this.y + this.h;
      if (down && !this.dragging && over) {
         this.dragging = true;
      }

      if (this.dragging) {
         this.value = Math.max(0.0F, Math.min(1.0F, (mx - this.x - thumbR) / travel));
      }

      if (!down) {
         this.dragging = false;
      }

      this.halo = this.halo + ((!this.dragging && !over ? 0.0F : 1.0F) - this.halo) * Math.min(1.0F, dt * 14.0F);
      int active = this.enabled ? t.colors.primary : Md3Colors.alpha(t.colors.onSurface, 0.38F);
      int inactive = this.enabled ? t.colors.surfaceContainerHighest : Md3Colors.alpha(t.colors.onSurface, 0.12F);
      c.roundRect(this.x, trackY, this.w, 4.0F, 2.0F, inactive);
      c.roundRect(this.x, trackY, this.value * this.w, 4.0F, 2.0F, active);
      float thumbX = this.x + thumbR + this.value * travel;
      if (this.halo > 0.02F) {
         float r = 20.0F;
         c.roundRect(thumbX - r, this.y + this.h / 2.0F - r, r * 2.0F, r * 2.0F, r, Md3Colors.alpha(t.colors.primary, 0.1F * this.halo));
      }

      c.roundRect(thumbX - thumbR, this.y + this.h / 2.0F - thumbR, thumbR * 2.0F, thumbR * 2.0F, thumbR, active);
   }
}
