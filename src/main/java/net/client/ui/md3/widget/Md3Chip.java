package net.client.ui.md3.widget;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;

public class Md3Chip extends Md3Widget {
   public Md3Chip.Variant variant = Md3Chip.Variant.ASSIST;
   public String label = "Chip";
   public String icon;
   public boolean selected;
   public int surface;

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      int base;
      int content;
      boolean outline;
      if (this.selected) {
         base = t.colors.secondaryContainer;
         content = t.colors.onSecondaryContainer;
         outline = false;
      } else if (this.variant == Md3Chip.Variant.SUGGESTION) {
         base = t.colors.surfaceContainerLow;
         content = t.colors.onSurfaceVariant;
         outline = false;
      } else {
         base = this.surface != 0 ? this.surface : t.colors.surface;
         content = t.colors.onSurfaceVariant;
         outline = true;
      }

      if (!this.enabled) {
         base = Md3Paint.disabledContainer(this.surface != 0 ? this.surface : t.colors.surface, t.colors.onSurface);
         content = Md3Paint.disabledContent(t.colors.onSurface);
         outline = false;
      }

      int painted = Md3Paint.container(base, content, this.hoverAnim.get(), this.pressAnim.get());
      c.roundRect(this.x, this.y, this.w, this.h, this.h / 2.0F, painted);
      if (outline) {
         c.strokeRoundRect(this.x, this.y, this.w, this.h, this.h / 2.0F, 1.0F, t.colors.outlineVariant);
      }

      float cy = this.y + this.h / 2.0F;
      float iconSize = 18.0F;
      float gap = 6.0F;
      String lead = this.icon;
      if (this.selected && this.variant == Md3Chip.Variant.FILTER) {
         lead = "check";
      }

      float tw = c.textWidth(this.label, Md3Type.LABEL_MEDIUM);
      float group = (lead != null ? iconSize + gap : 0.0F) + tw;
      float left = this.x + (this.w - group) / 2.0F;
      if (lead != null) {
         c.icon(lead, left, cy - iconSize / 2.0F, iconSize, content);
         left += iconSize + gap;
      }

      Md3Text.leftMiddle(c, this.label, left, cy, Md3Type.LABEL_MEDIUM, content);
   }

   public static enum Variant {
      ASSIST,
      FILTER,
      INPUT,
      SUGGESTION;
   }
}
