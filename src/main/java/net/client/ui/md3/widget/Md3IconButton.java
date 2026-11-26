package net.client.ui.md3.widget;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Theme;

public class Md3IconButton extends Md3Widget {
   public Md3IconButton.Variant variant = Md3IconButton.Variant.STANDARD;
   public String icon = "settings";
   public boolean selected;
   public int surface;

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      int base;
      int content;
      switch (this.variant) {
         case STANDARD:
         default:
            base = this.surface != 0 ? this.surface : t.colors.surface;
            content = t.colors.onSurfaceVariant;
            break;
         case FILLED:
            base = t.colors.primary;
            content = t.colors.onPrimary;
            break;
         case TONAL:
            base = t.colors.secondaryContainer;
            content = t.colors.onSecondaryContainer;
            break;
         case OUTLINED:
            base = this.surface != 0 ? this.surface : t.colors.surface;
            content = t.colors.onSurfaceVariant;
      }

      if (this.selected) {
         base = t.colors.primaryContainer;
         content = t.colors.onPrimaryContainer;
      }

      if (!this.enabled) {
         int disabledBase = Md3Paint.disabledContainer(this.surface != 0 ? this.surface : t.colors.surface, t.colors.onSurface);
         c.roundRect(this.x, this.y, this.w, this.h, this.w / 2.0F, disabledBase);
         content = Md3Paint.disabledContent(t.colors.onSurface);
      } else {
         int painted = Md3Paint.container(base, content, this.hoverAnim.get(), this.pressAnim.get());
         c.roundRect(this.x, this.y, this.w, this.h, this.w / 2.0F, painted);
         if (this.variant == Md3IconButton.Variant.OUTLINED && !this.selected) {
            c.strokeRoundRect(this.x, this.y, this.w, this.h, this.w / 2.0F, 1.0F, t.colors.outline);
         }
      }

      float iconSize = Math.min(24.0F, this.w * 0.5F);
      c.icon(this.icon, this.x + (this.w - iconSize) / 2.0F, this.y + (this.h - iconSize) / 2.0F, iconSize, content);
   }

   public static enum Variant {
      STANDARD,
      FILLED,
      TONAL,
      OUTLINED;
   }
}
