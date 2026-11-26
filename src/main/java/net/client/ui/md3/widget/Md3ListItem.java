package net.client.ui.md3.widget;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;

public class Md3ListItem extends Md3Widget {
   public String headline = "";
   public String supporting;
   public String leadingIcon;
   public String leadingText;
   public String trailingIcon;
   public String trailingText;
   public boolean selected;
   public boolean interactive = true;
   public boolean showDivider;
   public int surface;
   public float headlineTop = 0.5F;

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      if (this.visible) {
         int container = this.selected ? t.colors.secondaryContainer : (this.surface != 0 ? this.surface : t.colors.surface);
         if (this.selected) {
            c.roundRect(this.x, this.y, this.w, this.h, 14.0F, container);
         } else if (this.interactive && this.hoverAnim.get() > 0.01F) {
            c.roundRect(this.x, this.y, this.w, this.h, 14.0F, Md3Colors.alpha(t.colors.onSurface, 0.055F * this.hoverAnim.get()));
         }

         int content = this.selected ? t.colors.onSecondaryContainer : t.colors.onSurface;
         int support = this.selected ? Md3Colors.alpha(t.colors.onSecondaryContainer, 0.78F) : t.colors.onSurfaceVariant;
         float left = this.x + 16.0F;
         float cy = this.y + this.h / 2.0F;
         if (this.leadingIcon != null) {
            float d = 32.0F;
            c.roundRect(
               left, cy - d / 2.0F, d, d, d / 2.0F, this.selected ? Md3Colors.alpha(t.colors.onSecondaryContainer, 0.1F) : t.colors.surfaceContainerHighest
            );
            c.icon(this.leadingIcon, left + 6.0F, cy - 10.0F, 20.0F, content);
            left += d + 14.0F;
         } else if (this.leadingText != null && !this.leadingText.isEmpty()) {
            float d = 32.0F;
            c.roundRect(
               left, cy - d / 2.0F, d, d, d / 2.0F, this.selected ? Md3Colors.alpha(t.colors.onSecondaryContainer, 0.1F) : t.colors.surfaceContainerHighest
            );
            Md3Text.centered(c, this.leadingText.substring(0, 1).toUpperCase(), left + d / 2.0F, cy, Md3Type.TITLE_SMALL, content);
            left += d + 14.0F;
         }

         float right = this.x + this.w - 14.0F;
         if (this.trailingIcon != null) {
            float d = 36.0F;
            c.roundRect(
               right - d, cy - d / 2.0F, d, d, d / 2.0F, this.hoverAnim.get() > 0.01F && this.interactive ? Md3Colors.alpha(t.colors.onSurface, 0.06F) : 0
            );
            c.icon(this.trailingIcon, right - d + 8.0F, cy - 10.0F, 20.0F, content);
            right -= d + 6.0F;
         } else if (this.trailingText != null && !this.trailingText.isEmpty()) {
            float tw = c.textWidth(this.trailingText, Md3Type.LABEL_MEDIUM);
            right -= tw;
            Md3Text.rightMiddle(
               c, this.trailingText, right + tw, cy, Md3Type.LABEL_MEDIUM, this.selected ? t.colors.onSecondaryContainer : t.colors.onSurfaceVariant
            );
            right -= 10.0F;
         }

         float textRight = right - 6.0F;
         float textWidth = Math.max(20.0F, textRight - left);
         if (this.supporting != null && !this.supporting.isEmpty()) {
            float top = this.y + (this.h - 34.0F) / 2.0F;
            String head = fit(c, this.headline, Md3Type.BODY_LARGE, textWidth);
            c.pushClip(left, top, textWidth, 34.0F);
            c.text(head, left, top, Md3Type.BODY_LARGE, content);
            String sub = fit(c, this.supporting, Md3Type.BODY_MEDIUM, textWidth);
            c.text(sub, left, top + 18.0F, Md3Type.BODY_MEDIUM, support);
            c.popClip();
         } else {
            float top = this.y + this.h / 2.0F;
            c.pushClip(left, this.y, textWidth, this.h);
            Md3Text.leftMiddle(c, fit(c, this.headline, Md3Type.BODY_LARGE, textWidth), left, top, Md3Type.BODY_LARGE, content);
            c.popClip();
         }

         if (this.showDivider) {
            c.fill(this.x + 16.0F, this.y + this.h - 1.0F, this.w - 32.0F, 1.0F, t.colors.outlineVariant);
         }
      }
   }

   public boolean consumeActivation() {
      return this.consumeClick();
   }

   private static String fit(Md3Canvas c, String text, Md3Type type, float maxWidth) {
      if (text == null) {
         return "";
      } else if (maxWidth <= 0.0F) {
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
