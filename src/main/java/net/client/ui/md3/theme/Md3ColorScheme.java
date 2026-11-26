package net.client.ui.md3.theme;

public final class Md3ColorScheme {
   public int primary;
   public int onPrimary;
   public int primaryContainer;
   public int onPrimaryContainer;
   public int secondary;
   public int onSecondary;
   public int secondaryContainer;
   public int onSecondaryContainer;
   public int tertiary;
   public int onTertiary;
   public int tertiaryContainer;
   public int onTertiaryContainer;
   public int error;
   public int onError;
   public int errorContainer;
   public int onErrorContainer;
   public int background;
   public int onBackground;
   public int surface;
   public int onSurface;
   public int surfaceVariant;
   public int onSurfaceVariant;
   public int surfaceDim;
   public int surfaceBright;
   public int surfaceContainerLowest;
   public int surfaceContainerLow;
   public int surfaceContainer;
   public int surfaceContainerHigh;
   public int surfaceContainerHighest;
   public int outline;
   public int outlineVariant;
   public int inverseSurface;
   public int inverseOnSurface;
   public int inversePrimary;
   public int shadow;
   public int scrim;

   private Md3ColorScheme() {
   }

   public static Md3ColorScheme light() {
      Md3ColorScheme s = new Md3ColorScheme();
      s.primary = Md3Colors.rgb(6770852);
      s.onPrimary = Md3Colors.rgb(16777215);
      s.primaryContainer = Md3Colors.rgb(15392255);
      s.onPrimaryContainer = Md3Colors.rgb(2162781);
      s.secondary = Md3Colors.rgb(6445937);
      s.onSecondary = Md3Colors.rgb(16777215);
      s.secondaryContainer = Md3Colors.rgb(15261432);
      s.onSecondaryContainer = Md3Colors.rgb(1906987);
      s.tertiary = Md3Colors.rgb(8213088);
      s.onTertiary = Md3Colors.rgb(16777215);
      s.tertiaryContainer = Md3Colors.rgb(16767204);
      s.onTertiaryContainer = Md3Colors.rgb(3215645);
      s.error = Md3Colors.rgb(11740702);
      s.onError = Md3Colors.rgb(16777215);
      s.errorContainer = Md3Colors.rgb(16375516);
      s.onErrorContainer = Md3Colors.rgb(4263435);
      s.background = Md3Colors.rgb(16709631);
      s.onBackground = Md3Colors.rgb(1907488);
      s.surface = Md3Colors.rgb(16709631);
      s.onSurface = Md3Colors.rgb(1907488);
      s.surfaceVariant = Md3Colors.rgb(15196396);
      s.onSurfaceVariant = Md3Colors.rgb(4801871);
      s.surfaceDim = Md3Colors.rgb(14604513);
      s.surfaceBright = Md3Colors.rgb(16709631);
      s.surfaceContainerLowest = Md3Colors.rgb(16777215);
      s.surfaceContainerLow = Md3Colors.rgb(16249594);
      s.surfaceContainer = Md3Colors.rgb(15986167);
      s.surfaceContainerHigh = Md3Colors.rgb(15525616);
      s.surfaceContainerHighest = Md3Colors.rgb(15130857);
      s.outline = Md3Colors.rgb(7959678);
      s.outlineVariant = Md3Colors.rgb(13288656);
      s.inverseSurface = Md3Colors.rgb(3288885);
      s.inverseOnSurface = Md3Colors.rgb(16117751);
      s.inversePrimary = Md3Colors.rgb(13679871);
      s.shadow = Md3Colors.rgb(0);
      s.scrim = Md3Colors.rgb(0);
      return s;
   }

   public static Md3ColorScheme dark() {
      Md3ColorScheme s = new Md3ColorScheme();
      s.primary = Md3Colors.rgb(13679871);
      s.onPrimary = Md3Colors.rgb(3677810);
      s.primaryContainer = Md3Colors.rgb(5191563);
      s.onPrimaryContainer = Md3Colors.rgb(15392255);
      s.secondary = Md3Colors.rgb(13419228);
      s.onSecondary = Md3Colors.rgb(3353921);
      s.secondaryContainer = Md3Colors.rgb(4867160);
      s.onSecondaryContainer = Md3Colors.rgb(15261432);
      s.tertiary = Md3Colors.rgb(15710408);
      s.onTertiary = Md3Colors.rgb(4793650);
      s.tertiaryContainer = Md3Colors.rgb(6503240);
      s.onTertiaryContainer = Md3Colors.rgb(16767204);
      s.error = Md3Colors.rgb(15906997);
      s.onError = Md3Colors.rgb(6296592);
      s.errorContainer = Md3Colors.rgb(9182488);
      s.onErrorContainer = Md3Colors.rgb(16375516);
      s.background = Md3Colors.rgb(1315352);
      s.onBackground = Md3Colors.rgb(15130857);
      s.surface = Md3Colors.rgb(1315352);
      s.onSurface = Md3Colors.rgb(15130857);
      s.surfaceVariant = Md3Colors.rgb(4801871);
      s.onSurfaceVariant = Md3Colors.rgb(13288656);
      s.surfaceDim = Md3Colors.rgb(1315352);
      s.surfaceBright = Md3Colors.rgb(3881022);
      s.surfaceContainerLowest = Md3Colors.rgb(986387);
      s.surfaceContainerLow = Md3Colors.rgb(1907488);
      s.surfaceContainer = Md3Colors.rgb(2170662);
      s.surfaceContainerHigh = Md3Colors.rgb(2828592);
      s.surfaceContainerHighest = Md3Colors.rgb(3552315);
      s.outline = Md3Colors.rgb(9670553);
      s.outlineVariant = Md3Colors.rgb(4801871);
      s.inverseSurface = Md3Colors.rgb(15130857);
      s.inverseOnSurface = Md3Colors.rgb(3223603);
      s.inversePrimary = Md3Colors.rgb(6770852);
      s.shadow = Md3Colors.rgb(0);
      s.scrim = Md3Colors.rgb(0);
      return s;
   }

   public static Md3ColorScheme lerp(Md3ColorScheme a, Md3ColorScheme b, float t) {
      Md3ColorScheme s = new Md3ColorScheme();
      s.primary = Md3Colors.lerp(a.primary, b.primary, t);
      s.onPrimary = Md3Colors.lerp(a.onPrimary, b.onPrimary, t);
      s.primaryContainer = Md3Colors.lerp(a.primaryContainer, b.primaryContainer, t);
      s.onPrimaryContainer = Md3Colors.lerp(a.onPrimaryContainer, b.onPrimaryContainer, t);
      s.secondary = Md3Colors.lerp(a.secondary, b.secondary, t);
      s.onSecondary = Md3Colors.lerp(a.onSecondary, b.onSecondary, t);
      s.secondaryContainer = Md3Colors.lerp(a.secondaryContainer, b.secondaryContainer, t);
      s.onSecondaryContainer = Md3Colors.lerp(a.onSecondaryContainer, b.onSecondaryContainer, t);
      s.tertiary = Md3Colors.lerp(a.tertiary, b.tertiary, t);
      s.onTertiary = Md3Colors.lerp(a.onTertiary, b.onTertiary, t);
      s.tertiaryContainer = Md3Colors.lerp(a.tertiaryContainer, b.tertiaryContainer, t);
      s.onTertiaryContainer = Md3Colors.lerp(a.onTertiaryContainer, b.onTertiaryContainer, t);
      s.error = Md3Colors.lerp(a.error, b.error, t);
      s.onError = Md3Colors.lerp(a.onError, b.onError, t);
      s.errorContainer = Md3Colors.lerp(a.errorContainer, b.errorContainer, t);
      s.onErrorContainer = Md3Colors.lerp(a.onErrorContainer, b.onErrorContainer, t);
      s.background = Md3Colors.lerp(a.background, b.background, t);
      s.onBackground = Md3Colors.lerp(a.onBackground, b.onBackground, t);
      s.surface = Md3Colors.lerp(a.surface, b.surface, t);
      s.onSurface = Md3Colors.lerp(a.onSurface, b.onSurface, t);
      s.surfaceVariant = Md3Colors.lerp(a.surfaceVariant, b.surfaceVariant, t);
      s.onSurfaceVariant = Md3Colors.lerp(a.onSurfaceVariant, b.onSurfaceVariant, t);
      s.surfaceDim = Md3Colors.lerp(a.surfaceDim, b.surfaceDim, t);
      s.surfaceBright = Md3Colors.lerp(a.surfaceBright, b.surfaceBright, t);
      s.surfaceContainerLowest = Md3Colors.lerp(a.surfaceContainerLowest, b.surfaceContainerLowest, t);
      s.surfaceContainerLow = Md3Colors.lerp(a.surfaceContainerLow, b.surfaceContainerLow, t);
      s.surfaceContainer = Md3Colors.lerp(a.surfaceContainer, b.surfaceContainer, t);
      s.surfaceContainerHigh = Md3Colors.lerp(a.surfaceContainerHigh, b.surfaceContainerHigh, t);
      s.surfaceContainerHighest = Md3Colors.lerp(a.surfaceContainerHighest, b.surfaceContainerHighest, t);
      s.outline = Md3Colors.lerp(a.outline, b.outline, t);
      s.outlineVariant = Md3Colors.lerp(a.outlineVariant, b.outlineVariant, t);
      s.inverseSurface = Md3Colors.lerp(a.inverseSurface, b.inverseSurface, t);
      s.inverseOnSurface = Md3Colors.lerp(a.inverseOnSurface, b.inverseOnSurface, t);
      s.inversePrimary = Md3Colors.lerp(a.inversePrimary, b.inversePrimary, t);
      s.shadow = Md3Colors.lerp(a.shadow, b.shadow, t);
      s.scrim = Md3Colors.lerp(a.scrim, b.scrim, t);
      return s;
   }

   public static int[] tonalPalette(String name) {
      if ("secondary".equals(name)) {
         return new int[]{-16777216, -14870229, -13423295, -11910056, -10331279, -8752503, -6976091, -5199936, -3357988, -1515784, -594433, -1025, -1};
      } else if ("tertiary".equals(name)) {
         return new int[]{-16777216, -13561571, -11983566, -10273976, -8564128, -6788745, -5012590, -2974292, -1066808, -10012, -4879, -1025, -1};
      } else if ("neutral".equals(name)) {
         return new int[]{-16777216, -14869728, -13488331, -12040628, -10461852, -8817027, -7106666, -5330511, -3488307, -1646359, -659465, -67585, -1};
      } else {
         return "neutral-variant".equals(name)
            ? new int[]{-16777216, -14869982, -13488329, -11975345, -10461850, -8817538, -7106663, -5330508, -3488560, -1580820, -659718, -67585, -1}
            : new int[]{-16777216, -14614435, -13099406, -11585653, -10006364, -8427586, -6651173, -4809224, -3097345, -1384961, -594433, -1026, -1};
      }
   }
}
