package net.client.ui.md3.widget;

import net.client.ui.md3.anim.Md3Anim;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;

public class Md3Checkbox extends Md3Widget {
   public boolean checked;
   public boolean indeterminate;
   public int surface;
   private final Md3Anim check = new Md3Anim(0.0F);

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      float box = 18.0F;
      float bx = this.x + (this.w - box) / 2.0F;
      float by = this.y + (this.h - box) / 2.0F;
      float a = this.check.get();
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

      if (!this.enabled) {
         c.strokeRoundRect(bx, by, box, box, 4.0F, 2.0F, Md3Colors.alpha(t.colors.onSurface, 0.38F));
      } else if (a < 0.5F) {
         c.strokeRoundRect(bx, by, box, box, 4.0F, 2.0F, t.colors.onSurfaceVariant);
      } else {
         c.roundRect(bx, by, box, box, 4.0F, t.colors.primary);
      }

      if (this.indeterminate) {
         c.roundRect(bx + 4.0F, by + box / 2.0F - 1.5F, box - 8.0F, 3.0F, 1.5F, t.colors.onPrimary);
      } else if (a > 0.5F) {
         float iconSize = 14.0F;
         c.icon("check", bx + (box - iconSize) / 2.0F, by + (box - iconSize) / 2.0F, iconSize, t.colors.onPrimary);
      }

      float target = !this.checked && !this.indeterminate ? 0.0F : 1.0F;
      this.check.target(target);
      this.check.update(dt, 18.0F);
      if (this.consumeClick() && this.enabled) {
         this.checked = !this.checked;
         this.indeterminate = false;
      }
   }
}
