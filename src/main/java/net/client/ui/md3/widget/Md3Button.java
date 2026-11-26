package net.client.ui.md3.widget;

import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;

public class Md3Button extends Md3Widget {
   public Md3Button.Variant variant = Md3Button.Variant.FILLED;
   public String label = "Button";
   public String icon;
   public int surface;
   public Md3Type labelType = Md3Type.LABEL_LARGE;

   public Md3Button() {
   }

   public Md3Button(Md3Button.Variant variant, String label) {
      this.variant = variant;
      this.label = label;
   }

   @Override
   public void render(Md3Canvas c, Md3Theme t, float mx, float my, boolean down, float dt) {
      this.pointer(mx, my, down, dt);
      if (this.visible) {
         int content;
         int base;
         switch (this.variant) {
            case FILLED:
            default:
               base = t.colors.primary;
               content = t.colors.onPrimary;
               break;
            case TONAL:
               base = t.colors.secondaryContainer;
               content = t.colors.onSecondaryContainer;
               break;
            case ELEVATED:
               base = t.colors.surfaceContainerLow;
               content = t.colors.primary;
               c.shadow(this.x, this.y, this.w, this.h, this.h / 2.0F, 1);
               break;
            case OUTLINED:
               base = this.surface != 0 ? this.surface : t.colors.surface;
               content = t.colors.primary;
               break;
            case TEXT:
               base = this.surface != 0 ? this.surface : t.colors.surface;
               content = t.colors.primary;
         }

         if (!this.enabled) {
            int disabledBase = Md3Paint.disabledContainer(this.surface != 0 ? this.surface : t.colors.surface, t.colors.onSurface);
            c.roundRect(this.x, this.y, this.w, this.h, this.h / 2.0F, disabledBase);
            if (this.variant == Md3Button.Variant.OUTLINED) {
               c.strokeRoundRect(this.x, this.y, this.w, this.h, this.h / 2.0F, 1.0F, Md3Colors.alpha(t.colors.onSurface, 0.12F));
            }

            content = Md3Paint.disabledContent(t.colors.onSurface);
         } else {
            int layerColor = this.variant == Md3Button.Variant.FILLED ? t.colors.onPrimary : content;
            int painted = Md3Paint.container(base, layerColor, this.hoverAnim.get(), this.pressAnim.get());
            c.roundRect(this.x, this.y, this.w, this.h, this.h / 2.0F, painted);
            if (this.variant == Md3Button.Variant.OUTLINED) {
               c.strokeRoundRect(this.x, this.y, this.w, this.h, this.h / 2.0F, 1.0F, t.colors.outline);
            }
         }

         float iconSize = 18.0F;
         boolean hasLabel = this.label != null && !this.label.isEmpty();
         float gap = this.icon != null && hasLabel ? 8.0F : 0.0F;
         float tw = hasLabel ? c.textWidth(this.label, this.labelType) : 0.0F;
         float group = (this.icon != null ? iconSize + gap : 0.0F) + tw;
         float left = this.x + (this.w - group) / 2.0F;
         float cy = this.y + this.h / 2.0F;
         if (this.icon != null) {
            c.icon(this.icon, left, cy - iconSize / 2.0F, iconSize, content);
            left += iconSize + gap;
         }

         if (hasLabel) {
            Md3Text.leftMiddle(c, this.label, left, cy, this.labelType, content);
         }
      }
   }

   public static enum Variant {
      FILLED,
      TONAL,
      ELEVATED,
      OUTLINED,
      TEXT;
   }
}
