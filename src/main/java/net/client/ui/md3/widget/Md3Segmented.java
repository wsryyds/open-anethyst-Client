package net.client.ui.md3.widget;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;

public class Md3Segmented extends Md3Widget {
   public String[] labels = new String[]{"Day", "Week", "Month"};
   public int selected;
   public boolean showCheck = true;
   public int surface;

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      float seg = this.w / this.labels.length;
      if (this.selected >= 0 && this.selected < this.labels.length) {
         float sx = this.x + seg * this.selected;
         int color = t.colors.secondaryContainer;
         if (this.labels.length == 1) {
            c.roundRect(sx, this.y, seg, this.h, this.h / 2.0F, color);
         } else if (this.selected == 0) {
            c.roundRect(sx, this.y, seg, this.h, this.h / 2.0F, color);
            c.fill(sx + seg - this.h / 2.0F, this.y, this.h / 2.0F, this.h, color);
         } else if (this.selected == this.labels.length - 1) {
            c.roundRect(sx, this.y, seg, this.h, this.h / 2.0F, color);
            c.fill(sx, this.y, this.h / 2.0F, this.h, color);
         } else {
            c.fill(sx, this.y, seg, this.h, color);
         }
      }

      for (int i = 1; i < this.labels.length; i++) {
         c.fill(this.x + seg * i, this.y + 1.0F, 1.0F, this.h - 2.0F, t.colors.outlineVariant);
      }

      c.strokeRoundRect(this.x, this.y, this.w, this.h, this.h / 2.0F, 1.0F, t.colors.outline);

      for (int i = 0; i < this.labels.length; i++) {
         float sx = this.x + seg * i;
         String label = this.labels[i];
         float tw = c.textWidth(label, Md3Type.LABEL_LARGE);
         String icon = i == this.selected && this.showCheck ? "check" : null;
         float iconSize = 16.0F;
         float gap = 6.0F;
         float group = (icon != null ? iconSize + gap : 0.0F) + tw;
         float left = sx + (seg - group) / 2.0F;
         int color = i == this.selected ? t.colors.onSecondaryContainer : t.colors.onSurfaceVariant;
         if (icon != null) {
            c.icon(icon, left, this.y + this.h / 2.0F - iconSize / 2.0F, iconSize, color);
            left += iconSize + gap;
         }

         Md3Text.leftMiddle(c, label, left, this.y + this.h / 2.0F, Md3Type.LABEL_LARGE, color);
      }

      if (this.consumeClick() && this.enabled) {
         int i = (int)((mx - this.x) / (this.w / this.labels.length));
         if (i >= 0 && i < this.labels.length) {
            this.selected = i;
         }
      }
   }
}
