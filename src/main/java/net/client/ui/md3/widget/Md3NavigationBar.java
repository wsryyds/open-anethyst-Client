package net.client.ui.md3.widget;

import net.client.ui.md3.anim.Md3Anim;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;

public class Md3NavigationBar extends Md3Widget {
   public String[] labels = new String[]{"Home", "Search", "Saved", "Profile"};
   public String[] icons = new String[]{"home", "search", "favorite", "person"};
   public int selected;
   public float barHeight = 64.0F;
   public Md3Anim indicator = new Md3Anim(0.0F);

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      c.fill(this.x, this.y, this.w, this.barHeight, t.colors.surfaceContainer);
      c.fill(this.x, this.y, this.w, 1.0F, t.colors.outlineVariant);
      float seg = this.w / this.labels.length;
      this.indicator.target(this.selected);
      this.indicator.update(dt, 14.0F);
      float pillW = 64.0F;
      float pillX = this.x + seg * this.indicator.get() + (seg - pillW) / 2.0F;
      c.roundRect(pillX, this.y + 8.0F, pillW, 32.0F, 16.0F, t.colors.primaryContainer);

      for (int i = 0; i < this.labels.length; i++) {
         float sx = this.x + seg * i;
         boolean active = i == this.selected;
         int color = active ? t.colors.onPrimaryContainer : t.colors.onSurfaceVariant;
         c.icon(this.icons[i], sx + (seg - 20.0F) / 2.0F, this.y + 14.0F, 20.0F, color);
         Md3Text.centered(c, this.labels[i], sx + seg / 2.0F, this.y + 48.0F, Md3Type.LABEL_SMALL, color);
      }

      if (this.consumeClick() && this.enabled) {
         int i = (int)((mx - this.x) / seg);
         if (i >= 0 && i < this.labels.length) {
            this.selected = i;
         }
      }
   }
}
