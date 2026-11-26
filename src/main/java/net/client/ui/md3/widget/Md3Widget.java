package net.client.ui.md3.widget;

import net.client.ui.md3.anim.Md3Anim;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Theme;

public abstract class Md3Widget {
   public float x;
   public float y;
   public float w;
   public float h;
   public boolean enabled = true;
   public boolean visible = true;
   protected boolean hovered;
   protected boolean pressed;
   protected boolean pressedInside;
   private boolean wasDown;
   public boolean clicked;
   protected final Md3Anim hoverAnim = new Md3Anim(0.0F);
   protected final Md3Anim pressAnim = new Md3Anim(0.0F);

   public void setBounds(float x, float y, float w, float h) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
   }

   protected boolean contains(float mx, float my) {
      return mx >= this.x && mx < this.x + this.w && my >= this.y && my < this.y + this.h;
   }

   protected void pointer(float mx, float my, boolean down, float dt) {
      this.hovered = this.enabled && this.contains(mx, my);
      if (down && !this.wasDown) {
         this.pressedInside = this.hovered;
      }

      this.pressed = down && this.pressedInside;
      if (!down) {
         this.clicked = this.wasDown && this.pressedInside && this.hovered;
         this.pressedInside = false;
         this.pressed = false;
      } else {
         this.clicked = false;
      }

      this.wasDown = down;
      this.hoverAnim.target(this.hovered ? 1.0F : 0.0F);
      this.pressAnim.target(this.pressed ? 1.0F : 0.0F);
      this.hoverAnim.update(dt, 12.0F);
      this.pressAnim.update(dt, 18.0F);
   }

   public abstract void render(Md3Canvas var1, Md3Theme var2, float var3, float var4, boolean var5, float var6);

   public boolean consumeClick() {
      boolean c = this.clicked;
      this.clicked = false;
      return c;
   }
}
