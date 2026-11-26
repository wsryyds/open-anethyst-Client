package net.client.ui.md3.widget;

import net.client.ui.md3.anim.Md3Anim;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;

public class Md3Tabs extends Md3Widget {
   public String[] labels = new String[]{"Overview", "Details", "Reviews"};
   public int selected;
   public boolean primary = true;
   public Md3Anim indicator = new Md3Anim(0.0F);

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      float seg = this.w / this.labels.length;
      this.indicator.target(this.selected);
      this.indicator.update(dt, 14.0F);
      float ix = this.x + seg * this.indicator.get();
      float indicatorW = seg - 32.0F;
      c.roundRect(ix + 16.0F, this.y + this.h - 3.0F, indicatorW, 3.0F, 1.5F, this.primary ? t.colors.primary : t.colors.primary);
      if (!this.primary) {
         c.fill(this.x, this.y + this.h - 1.0F, this.w, 1.0F, t.colors.outlineVariant);
      }

      for (int i = 0; i < this.labels.length; i++) {
         float sx = this.x + seg * i;
         if (this.hoverAnim.get() > 0.01F && this.hovered && mx >= sx && mx < sx + seg) {
            c.roundRect(sx + 4.0F, this.y + 4.0F, seg - 8.0F, this.h - 8.0F, 8.0F, Md3Colors.alpha(t.colors.onSurface, 0.05F));
         }

         boolean active = i == this.selected;
         int color = active ? (this.primary ? t.colors.primary : t.colors.onSurface) : t.colors.onSurfaceVariant;
         Md3Text.centered(c, this.labels[i], sx + seg / 2.0F, this.y + this.h / 2.0F, Md3Type.LABEL_LARGE, color);
      }

      if (this.consumeClick() && this.enabled) {
         int i = (int)((mx - this.x) / seg);
         if (i >= 0 && i < this.labels.length) {
            this.selected = i;
         }
      }
   }
}
