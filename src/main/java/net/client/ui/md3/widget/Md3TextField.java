package net.client.ui.md3.widget;

import net.client.ui.md3.anim.Md3Anim;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;

public class Md3TextField extends Md3Widget {
   public Md3TextField.Variant variant = Md3TextField.Variant.FILLED;
   public String label = "Label";
   public String text = "";
   public String supporting = "Supporting text";
   public String leadingIcon;
   public String trailingIcon;
   public boolean error;
   public boolean focused;
   public int surface;
   public boolean focusRequested;
   public boolean masked;
   public int maxLength = 256;
   private final Md3Anim floatAnim = new Md3Anim(0.0F);

   private String display() {
      if (!this.masked) {
         return this.text;
      } else {
         StringBuilder sb = new StringBuilder(this.text.length());

         for (int i = 0; i < this.text.length(); i++) {
            sb.append('*');
         }

         return sb.toString();
      }
   }

   public void typeChar(char ch) {
      if (ch >= ' ' && ch != 127 && this.text.length() < this.maxLength) {
         this.text = this.text + ch;
      }
   }

   public void backspace() {
      if (!this.text.isEmpty()) {
         this.text = this.text.substring(0, this.text.length() - 1);
      }
   }

   public void clear() {
      this.text = "";
   }

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      if (this.consumeClick() && this.enabled) {
         this.focusRequested = true;
      }

      boolean floating = this.focused || !this.text.isEmpty();
      this.floatAnim.target(floating ? 1.0F : 0.0F);
      this.floatAnim.update(dt, 14.0F);
      float a = this.floatAnim.get();
      int accent = this.error ? t.colors.error : (this.focused ? t.colors.primary : t.colors.outline);
      int labelColor = this.error ? t.colors.error : (this.focused ? t.colors.primary : t.colors.onSurfaceVariant);
      int textColor = this.enabled ? t.colors.onSurface : Md3Paint.disabledContent(t.colors.onSurface);
      if (this.variant == Md3TextField.Variant.FILLED) {
         c.roundRectTop(this.x, this.y, this.w, this.h, 4.0F, t.colors.surfaceContainerHighest);
         c.fill(this.x, this.y + this.h - (this.focused ? 2.0F : 1.0F), this.w, this.focused ? 2.0F : 1.0F, accent);
      } else {
         c.roundRect(this.x, this.y, this.w, this.h, 4.0F, this.surface != 0 ? this.surface : t.colors.surface);
         c.strokeRoundRect(this.x, this.y, this.w, this.h, 4.0F, this.focused ? 2.0F : 1.0F, accent);
      }

      float textX = this.x + 16.0F;
      if (this.leadingIcon != null) {
         c.icon(this.leadingIcon, this.x + 12.0F, this.y + this.h / 2.0F - 10.0F, 20.0F, labelColor);
         textX = this.x + 44.0F;
      }

      if (this.trailingIcon != null) {
         c.icon(this.trailingIcon, this.x + this.w - 32.0F, this.y + this.h / 2.0F - 10.0F, 20.0F, labelColor);
      }

      if (a > 0.02F) {
         float ly = this.variant == Md3TextField.Variant.OUTLINED ? this.y - 6.0F : this.y + 8.0F;
         if (this.variant == Md3TextField.Variant.OUTLINED && !this.label.isEmpty()) {
            float tw = c.textWidth(this.label, Md3Type.BODY_SMALL);
            c.fill(textX - 3.0F, ly - 1.0F, tw + 6.0F, 14.0F, this.surface != 0 ? this.surface : t.colors.surface);
         }

         c.text(this.label, textX, ly, Md3Type.BODY_SMALL, Md3Colors.alpha(labelColor, a));
      }

      if (a < 0.98F) {
         float ly = Md3Text.topForCenter(this.y + this.h / 2.0F, Md3Type.BODY_LARGE);
         c.text(
            this.label,
            textX,
            ly,
            Md3Type.BODY_LARGE,
            Md3Colors.alpha(this.enabled ? t.colors.onSurfaceVariant : Md3Paint.disabledContent(t.colors.onSurface), (1.0F - a) * (this.enabled ? 1.0F : 0.38F))
         );
      }

      boolean hasLabel = this.label != null && !this.label.isEmpty();
      float clipTop;
      float inputY;
      if (this.variant == Md3TextField.Variant.OUTLINED) {
         inputY = hasLabel ? this.y + 27.0F : Md3Text.topForLineCenter(this.y + this.h / 2.0F, Md3Type.BODY_LARGE);
         clipTop = hasLabel ? this.y + 18.0F : this.y + 6.0F;
      } else {
         inputY = this.y + 30.0F;
         clipTop = this.y + 18.0F;
      }

      c.pushClip(this.x + 8.0F, clipTop, this.w - 16.0F, this.h - (clipTop - this.y) - 4.0F);
      float tw = c.text(this.display(), textX, inputY, Md3Type.BODY_LARGE, textColor);
      if (this.focused && this.enabled && System.currentTimeMillis() / 500L % 2L == 0L) {
         c.fill(textX + tw + 1.0F, inputY - 1.0F, 1.0F, 15.0F, t.colors.primary);
      }

      c.popClip();
      if (!this.supporting.isEmpty()) {
         c.text(this.supporting, this.x + 16.0F, this.y + this.h + 6.0F, Md3Type.BODY_SMALL, labelColor);
      }
   }

   public static enum Variant {
      FILLED,
      OUTLINED;
   }
}
