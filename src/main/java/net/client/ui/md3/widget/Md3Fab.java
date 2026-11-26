package net.client.ui.md3.widget;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;

public class Md3Fab extends Md3Widget {
   public Md3Fab.Size size = Md3Fab.Size.REGULAR;
   public String icon = "add";
   public String label = "Compose";
   public int surface;

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      int base = t.colors.primaryContainer;
      int content = t.colors.onPrimaryContainer;
      if (!this.enabled) {
         base = Md3Paint.disabledContainer(this.surface != 0 ? this.surface : t.colors.surface, t.colors.onSurface);
         content = Md3Paint.disabledContent(t.colors.onSurface);
      }

      base = Md3Paint.container(base, content, this.hoverAnim.get(), this.pressAnim.get());
      c.shadow(this.x, this.y, this.w, this.h, Math.min(this.w, this.h) / 2.0F, this.size == Md3Fab.Size.SMALL ? 2 : 4);
      c.roundRect(this.x, this.y, this.w, this.h, Math.min(this.w, this.h) / 2.0F, base);
      float iconSize = this.size == Md3Fab.Size.LARGE ? 36.0F : (this.size == Md3Fab.Size.SMALL ? 20.0F : 24.0F);
      if (this.size == Md3Fab.Size.EXTENDED) {
         float tw = c.textWidth(this.label, Md3Type.LABEL_LARGE);
         float gap = 12.0F;
         float group = iconSize + gap + tw;
         float left = this.x + (this.w - group) / 2.0F;
         c.icon(this.icon, left, this.y + (this.h - iconSize) / 2.0F, iconSize, content);
         Md3Text.leftMiddle(c, this.label, left + iconSize + gap, this.y + this.h / 2.0F, Md3Type.LABEL_LARGE, content);
      } else {
         c.icon(this.icon, this.x + (this.w - iconSize) / 2.0F, this.y + (this.h - iconSize) / 2.0F, iconSize, content);
      }
   }

   public static enum Size {
      SMALL,
      REGULAR,
      LARGE,
      EXTENDED;
   }
}
