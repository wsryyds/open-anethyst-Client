package net.client.ui.md3.widget;

import net.client.ui.md3.anim.Md3Anim;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;

public class Md3Radio extends Md3Widget {
   public boolean selected;
   public int surface;
   public int group;
   private final Md3Anim dot = new Md3Anim(0.0F);

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      float d = 20.0F;
      float bx = this.x + (this.w - d) / 2.0F;
      float by = this.y + (this.h - d) / 2.0F;
      if (this.hoverAnim.get() > 0.01F && this.enabled) {
         c.roundRect(
            this.x + this.w / 2.0F - 20.0F,
            this.y + this.h / 2.0F - 20.0F,
            40.0F,
            40.0F,
            20.0F,
            Md3Colors.alpha(t.colors.onSurface, 0.08F * this.hoverAnim.get())
         );
      }

      int ring = this.enabled ? (this.selected ? t.colors.primary : t.colors.onSurfaceVariant) : Md3Colors.alpha(t.colors.onSurface, 0.38F);
      c.strokeRoundRect(bx, by, d, d, d / 2.0F, 2.0F, ring);
      float a = this.dot.get();
      if (a > 0.01F) {
         float inner = 10.0F * a;
         c.roundRect(
            this.x + this.w / 2.0F - inner / 2.0F, this.y + this.h / 2.0F - inner / 2.0F, inner, inner, inner / 2.0F, this.enabled ? t.colors.primary : ring
         );
      }

      this.dot.target(this.selected ? 1.0F : 0.0F);
      this.dot.update(dt, 20.0F);
      if (this.consumeClick() && this.enabled) {
         this.selected = true;
      }
   }
}
