package net.client.replay.gui;

import net.client.ui.ClientUi;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3Paint;
import net.client.ui.md3.widget.Md3Surfaces;
import net.client.ui.md3.widget.Md3Text;

public final class ReplayUi {
   private ReplayUi() {
   }

   public static boolean hit(float mx, float my, float x, float y, float w, float h) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }

   public static void card(Md3Canvas c, Md3Theme t, float x, float y, float w, float h) {
      Md3Surfaces.card(c, t, x, y, w, h, Md3Surfaces.CardVariant.FILLED, Math.min(16.0F, h / 2.0F));
   }

   public static void button(
      Md3Canvas c,
      Md3Theme t,
      float x,
      float y,
      float w,
      float h,
      String label,
      String icon,
      ReplayUi.Style style,
      boolean enabled,
      boolean hovered,
      boolean pressed
   ) {
      int base;
      int content;
      switch (style) {
         case FILLED:
         default:
            base = t.colors.primary;
            content = t.colors.onPrimary;
            break;
         case TONAL:
         case ICON_TONAL:
            base = t.colors.secondaryContainer;
            content = t.colors.onSecondaryContainer;
            break;
         case OUTLINED:
            base = t.colors.surfaceContainerLow;
            content = t.colors.primary;
            break;
         case TEXT:
            base = t.colors.surfaceContainerLow;
            content = t.colors.primary;
            break;
         case ICON:
            base = t.colors.surfaceContainerHigh;
            content = t.colors.onSurfaceVariant;
            break;
         case DANGER:
            base = t.colors.error;
            content = t.colors.onError;
      }

      if (!enabled) {
         int disabled = Md3Paint.disabledContainer(base, t.colors.onSurface);
         c.roundRect(x, y, w, h, h / 2.0F, disabled);
         content = Md3Paint.disabledContent(t.colors.onSurface);
      } else {
         int painted = Md3Paint.container(base, content, hovered ? 0.1F : 0.0F, pressed ? 0.16F : 0.0F);
         if (style == ReplayUi.Style.TEXT) {
            c.roundRect(x, y, w, h, 6.0F, painted);
         } else {
            c.roundRect(x, y, w, h, h / 2.0F, painted);
         }

         if (style == ReplayUi.Style.OUTLINED) {
            c.strokeRoundRect(x, y, w, h, h / 2.0F, 1.0F, t.colors.outline);
         }
      }

      float iconSize = Math.min(18.0F, h - 4.0F);
      boolean hasLabel = label != null && !label.isEmpty();
      float gap = icon != null && hasLabel ? 6.0F : 0.0F;
      float tw = hasLabel ? c.textWidth(label, Md3Type.LABEL_MEDIUM) : 0.0F;
      float group = (icon != null ? iconSize + gap : 0.0F) + tw;
      float left = x + (w - group) / 2.0F;
      float cy = y + h / 2.0F;
      if (icon != null) {
         c.icon(icon, left, cy - iconSize / 2.0F, iconSize, !enabled ? Md3Paint.disabledContent(t.colors.onSurface) : content);
         left += iconSize + gap;
      }

      if (hasLabel) {
         Md3Text.leftMiddle(c, label, left, cy, Md3Type.LABEL_MEDIUM, !enabled ? Md3Paint.disabledContent(t.colors.onSurface) : content);
      }
   }

   public static void switchRow(Md3Canvas c, Md3Theme t, float x, float y, float w, float h, String label, boolean on, boolean enabled) {
      int color = enabled ? t.colors.onSurface : Md3Colors.alpha(t.colors.onSurface, 0.38F);
      Md3Text.leftMiddle(c, label, x, y + h / 2.0F, Md3Type.BODY_MEDIUM, color);
      float trackW = 36.0F;
      float trackH = 22.0F;
      float tx = x + w - trackW;
      float ty = y + (h - trackH) / 2.0F;
      int track = on ? t.colors.primary : Md3Colors.alpha(t.colors.onSurfaceVariant, 0.35F);
      if (!enabled) {
         track = Md3Colors.alpha(t.colors.onSurface, 0.12F);
      }

      c.roundRect(tx, ty, trackW, trackH, trackH / 2.0F, track);
      float thumb = 9.0F;
      float thumbX = tx + (on ? trackW - thumb * 2.0F - 2.0F : 2.0F);
      int thumbColor = enabled ? (on ? t.colors.onPrimary : t.colors.outline) : t.colors.onSurface;
      c.roundRect(thumbX, ty + (trackH - thumb * 2.0F) / 2.0F, thumb * 2.0F, thumb * 2.0F, thumb, thumbColor);
   }

   public static void switchOnly(Md3Canvas c, Md3Theme t, float x, float y, float w, float h, boolean on, boolean enabled) {
      float trackW = Math.min(w, 36.0F);
      float trackH = Math.min(h, 22.0F);
      float tx = x + (w - trackW) / 2.0F;
      float ty = y + (h - trackH) / 2.0F;
      int track = on ? t.colors.primary : Md3Colors.alpha(t.colors.onSurfaceVariant, 0.35F);
      if (!enabled) {
         track = Md3Colors.alpha(t.colors.onSurface, 0.12F);
      }

      c.roundRect(tx, ty, trackW, trackH, trackH / 2.0F, track);
      float thumb = 9.0F;
      float thumbX = tx + (on ? trackW - thumb * 2.0F - 2.0F : 2.0F);
      int thumbColor = enabled ? (on ? t.colors.onPrimary : t.colors.outline) : t.colors.onSurface;
      c.roundRect(thumbX, ty + (trackH - thumb * 2.0F) / 2.0F, thumb * 2.0F, thumb * 2.0F, thumb, thumbColor);
   }

   public static void scrollbar(Md3Canvas c, Md3Theme t, float x, float y, float h, float value, float content, float viewport, boolean dragging) {
      if (!(content <= viewport + 0.5F)) {
         float thumbH = Math.max(20.0F, h * viewport / content);
         float max = content - viewport;
         float off = max <= 0.0F ? 0.0F : value / max * (h - thumbH);
         c.roundRect(x, y, 4.0F, h, 2.0F, Md3Colors.alpha(t.colors.onSurface, 0.06F));
         c.roundRect(x, y + off, 4.0F, thumbH, 2.0F, dragging ? t.colors.primary : Md3Colors.alpha(t.colors.onSurfaceVariant, 0.55F));
      }
   }

   public static String fit(Md3Canvas c, String text, Md3Type type, float maxWidth) {
      return ClientUi.fit(c, text, type, maxWidth);
   }

   public static void section(Md3Canvas c, Md3Theme t, String title, float x, float cy) {
      Md3Text.leftMiddle(c, title, x, cy, Md3Type.TITLE_SMALL, t.colors.onSurfaceVariant);
   }

   public static void value(Md3Canvas c, Md3Theme t, String label, String value, float x, float cy, float w) {
      Md3Text.leftMiddle(c, label, x, cy, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
      Md3Text.rightMiddle(c, value, x + w, cy, Md3Type.BODY_SMALL, t.colors.onSurface);
   }

   public static enum Style {
      FILLED,
      TONAL,
      OUTLINED,
      TEXT,
      ICON,
      ICON_TONAL,
      DANGER;
   }
}
