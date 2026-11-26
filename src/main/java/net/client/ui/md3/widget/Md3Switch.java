package net.client.ui.md3.widget;

import net.client.ui.md3.anim.Md3Anim;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;

public class Md3Switch extends Md3Widget {
   public boolean checked;
   public int surface;
   private final Md3Anim knob = new Md3Anim(0.0F);

   public void setChecked(boolean checked) {
      this.checked = checked;
      this.knob.snap(checked ? 1.0F : 0.0F);
   }

   public void setCheckedAnimated(boolean checked) {
      this.checked = checked;
   }

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      float a = this.knob.get();
      float trackW = 52.0F;
      float trackH = 32.0F;
      float tx = this.x + (this.w - trackW) / 2.0F;
      float ty = this.y + (this.h - trackH) / 2.0F;
      int track = Md3Colors.lerp(t.colors.surfaceContainerHighest, t.colors.primary, a);
      if (!this.enabled) {
         track = Md3Paint.disabledContainer(this.surface != 0 ? this.surface : t.colors.surface, t.colors.onSurface);
      }

      c.roundRect(tx, ty, trackW, trackH, trackH / 2.0F, track);
      if (a < 0.5F && this.enabled) {
         c.strokeRoundRect(tx, ty, trackW, trackH, trackH / 2.0F, 2.0F, t.colors.outline);
      }

      if (this.hoverAnim.get() > 0.01F && this.enabled) {
         float halo = 40.0F;
         c.roundRect(
            tx + trackW / 2.0F - halo / 2.0F,
            ty + trackH / 2.0F - halo / 2.0F,
            halo,
            halo,
            halo / 2.0F,
            Md3Colors.alpha(t.colors.onSurface, 0.08F * this.hoverAnim.get())
         );
      }

      float thumbR = 10.0F + a * 3.0F;
      float thumbX = tx + 16.0F + (trackW - 32.0F) * a;
      int thumb = Md3Colors.lerp(t.colors.outline, t.colors.onPrimary, a);
      if (!this.enabled) {
         thumb = Md3Paint.disabledContent(t.colors.onSurface);
      }

      c.roundRect(thumbX - thumbR, ty + trackH / 2.0F - thumbR, thumbR * 2.0F, thumbR * 2.0F, thumbR, thumb);
      if (a < 0.5F && this.enabled) {
         c.roundRect(
            thumbX - thumbR + 2.0F,
            ty + trackH / 2.0F - thumbR + 2.0F,
            (thumbR - 2.0F) * 2.0F,
            (thumbR - 2.0F) * 2.0F,
            thumbR - 2.0F,
            t.colors.surfaceContainerHighest
         );
      }

      this.knob.target(this.checked ? 1.0F : 0.0F);
      this.knob.update(dt, 10.0F);
   }
}
