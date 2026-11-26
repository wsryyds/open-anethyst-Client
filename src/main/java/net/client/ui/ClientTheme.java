package net.client.ui;

import net.client.ClientConfig;
import net.client.ui.md3.theme.Md3ColorScheme;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;

public final class ClientTheme {
   private static Md3Theme cached;
   private static int cachedColor = Integer.MIN_VALUE;
   private static boolean cachedLight;

   private ClientTheme() {
   }

   public static Md3Theme get() {
      boolean light = ClientConfig.lightTheme;
      int color = ClientConfig.themeColor;
      if (cached != null && cachedColor == color && cachedLight == light) {
         return cached;
      } else {
         cached = build(light, color);
         cachedColor = color;
         cachedLight = light;
         return cached;
      }
   }

   public static boolean dark() {
      return !ClientConfig.lightTheme;
   }

   private static Md3Theme build(boolean light, int color) {
      Md3ColorScheme scheme = light ? Md3ColorScheme.light() : Md3ColorScheme.dark();
      if ((color & 16777215) != 6770852) {
         scheme.primary = color;
         scheme.onPrimary = contrast(color);
         scheme.primaryContainer = Md3Colors.composite(Md3Colors.alpha(color, light ? 0.18F : 0.3F), scheme.surfaceContainerHigh);
         scheme.onPrimaryContainer = contrast(scheme.primaryContainer);
         scheme.secondary = Md3Colors.composite(Md3Colors.alpha(color, light ? 0.38F : 0.32F), scheme.surface);
         scheme.onSecondary = contrast(scheme.secondary);
         scheme.secondaryContainer = Md3Colors.composite(Md3Colors.alpha(color, light ? 0.14F : 0.2F), scheme.surfaceContainerHighest);
         scheme.onSecondaryContainer = contrast(scheme.secondaryContainer);
         scheme.inversePrimary = Md3Colors.composite(Md3Colors.alpha(color, 0.45F), scheme.inverseSurface);
      }

      return new Md3Theme(scheme, !light);
   }

   private static int contrast(int background) {
      int c = background | 0xFF000000;
      double r = (c >> 16 & 0xFF) / 255.0;
      double g = (c >> 8 & 0xFF) / 255.0;
      double b = (c & 0xFF) / 255.0;
      double lum = 0.2126 * linear(r) + 0.7152 * linear(g) + 0.0722 * linear(b);
      return lum > 0.55 ? -15658735 : -1;
   }

   private static double linear(double v) {
      return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
   }
}
