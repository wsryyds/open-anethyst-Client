package net.client.ui.md3.widget;

import net.client.ui.md3.anim.Md3Anim;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;

public class Md3Menu {
   public String[] items = new String[0];
   public int selected;
   public float x;
   public float y;
   public float w;
   public float itemHeight = 40.0F;
   public float padding = 4.0F;
   public int surface;
   private final Md3Anim openAnim = new Md3Anim(0.0F);

   public float height() {
      return this.padding * 2.0F + this.items.length * this.itemHeight;
   }

   public void open() {
      this.openAnim.snap(0.0F);
      this.openAnim.target(1.0F);
   }

   public void update(float dt) {
      this.openAnim.update(dt, 16.0F);
   }

   public float anim() {
      return this.openAnim.get();
   }

   public int indexAt(float mx, float my) {
      if (!(mx < this.x) && !(mx >= this.x + this.w) && !(my < this.y) && !(my >= this.y + this.height())) {
         float local = my - this.y - this.padding;
         if (local < 0.0F) {
            return -1;
         } else {
            int index = (int)Math.floor(local / this.itemHeight);
            return index >= 0 && index < this.items.length ? index : -1;
         }
      } else {
         return -1;
      }
   }

   public void render(Md3Canvas c, Md3Theme t, float mx, float my) {
      float h = this.height();
      float a = this.anim();
      if (!(a <= 0.01F)) {
         float grow = 4.0F * (1.0F - a);
         Md3Surfaces.menu(c, t, this.x, this.y + grow, this.w, h - grow * 2.0F);
         c.pushClip(this.x + 2.0F, this.y + 2.0F, this.w - 4.0F, h - 4.0F);
         int hover = this.indexAt(mx, my);

         for (int i = 0; i < this.items.length; i++) {
            float iy = this.y + this.padding + i * this.itemHeight;
            boolean current = i == this.selected;
            if (i == hover) {
               c.roundRect(this.x + 4.0F, iy, this.w - 8.0F, this.itemHeight, 8.0F, Md3Colors.alpha(t.colors.onSurface, 0.06F));
            }

            if (current) {
               c.roundRect(this.x + 4.0F, iy, this.w - 8.0F, this.itemHeight, 8.0F, t.colors.secondaryContainer);
            }

            int color = current ? t.colors.onSecondaryContainer : t.colors.onSurface;
            String label = this.items[i];
            float max = this.w - 56.0F;
            if (c.textWidth(label, Md3Type.BODY_MEDIUM) > max) {
               int end = label.length();

               while (end > 0 && c.textWidth(label.substring(0, end) + "...", Md3Type.BODY_MEDIUM) > max) {
                  end--;
               }

               label = label.substring(0, end) + "...";
            }

            Md3Text.leftMiddle(c, label, this.x + 16.0F, iy + this.itemHeight / 2.0F, Md3Type.BODY_MEDIUM, color);
            if (current) {
               c.icon("check", this.x + this.w - 32.0F, iy + this.itemHeight / 2.0F - 9.0F, 18.0F, color);
            }
         }

         c.popClip();
      }
   }
}
