package net.client.ui.md3.widget;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;

public class Md3NavigationRail extends Md3Widget {
   public String[] labels = new String[0];
   public String[] icons = new String[0];
   public int selected;
   public int activatedIndex = -1;
   public float itemHeight = 48.0F;
   public float scroll;
   public float topPadding;
   public float bottomPadding = 8.0F;
   public int surface;

   public int visibleCount() {
      float inner = this.h - this.topPadding - this.bottomPadding;
      return Math.max(1, (int)Math.floor(inner / this.itemHeight));
   }

   public float maxScroll() {
      float content = this.labels.length * this.itemHeight + this.topPadding + this.bottomPadding;
      return Math.max(0.0F, content - this.h);
   }

   public void clampScroll() {
      if (this.scroll < 0.0F) {
         this.scroll = 0.0F;
      }

      float max = this.maxScroll();
      if (this.scroll > max) {
         this.scroll = max;
      }
   }

   public int indexAt(float mx, float my) {
      if (!(mx < this.x) && !(mx >= this.x + this.w) && !(my < this.y) && !(my >= this.y + this.h)) {
         float local = my - this.y + this.scroll - this.topPadding;
         if (local < 0.0F) {
            return -1;
         } else {
            int index = (int)Math.floor(local / this.itemHeight);
            return index >= 0 && index < this.labels.length ? index : -1;
         }
      } else {
         return -1;
      }
   }

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      this.clampScroll();
      if (this.surface != 0) {
         c.roundRect(this.x, this.y, this.w, this.h, 20.0F, this.surface);
      }

      c.pushClip(this.x, this.y + 4.0F, this.w, this.h - 8.0F);
      int hoverIndex = this.indexAt(mx, my);

      for (int i = 0; i < this.labels.length; i++) {
         float iy = this.y + this.topPadding + i * this.itemHeight - this.scroll;
         if (!(iy + this.itemHeight < this.y - 4.0F) && !(iy > this.y + this.h + 4.0F)) {
            boolean active = i == this.selected;
            boolean hot = hoverIndex == i && this.enabled;
            float pillH = this.itemHeight - 8.0F;
            if (active) {
               c.roundRect(this.x + 8.0F, iy + 4.0F, this.w - 16.0F, pillH, pillH / 2.0F, Md3Colors.alpha(t.colors.secondaryContainer, 0.92F));
            } else if (hot) {
               c.roundRect(this.x + 8.0F, iy + 4.0F, this.w - 16.0F, pillH, pillH / 2.0F, Md3Colors.alpha(t.colors.onSurface, 0.06F));
            }

            int color = active ? t.colors.onSecondaryContainer : (hot ? t.colors.onSurface : t.colors.onSurfaceVariant);
            float iconSize = 19.0F;
            String icon = i < this.icons.length ? this.icons[i] : null;
            float textX;
            if (icon != null) {
               c.icon(icon, this.x + 20.0F, iy + this.itemHeight / 2.0F - iconSize / 2.0F, iconSize, color);
               textX = this.x + 44.0F;
            } else {
               textX = this.x + 20.0F;
            }

            String label = this.labels[i];
            float avail = this.x + this.w - 12.0F - textX;
            label = fit(c, label, Md3Type.LABEL_LARGE, avail);
            Md3Text.leftMiddle(c, label, textX, iy + this.itemHeight / 2.0F, Md3Type.LABEL_LARGE, color);
         }
      }

      c.popClip();
      if (this.consumeClick() && this.enabled) {
         int index = this.indexAt(mx, my);
         if (index >= 0) {
            this.selected = index;
            this.activatedIndex = index;
         }
      }
   }

   private static String fit(Md3Canvas c, String text, Md3Type type, float maxWidth) {
      if (maxWidth <= 0.0F) {
         return "";
      } else if (c.textWidth(text, type) <= maxWidth) {
         return text;
      } else {
         String ellipsis = "...";
         float ew = c.textWidth(ellipsis, type);
         int end = text.length();

         while (end > 0 && c.textWidth(text.substring(0, end), type) + ew > maxWidth) {
            end--;
         }

         return text.substring(0, Math.max(0, end)) + ellipsis;
      }
   }
}
