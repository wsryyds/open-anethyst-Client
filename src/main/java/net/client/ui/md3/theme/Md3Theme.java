package net.client.ui.md3.theme;

public final class Md3Theme {
   public final Md3ColorScheme colors;
   public final boolean dark;
   public static final int SHAPE_EXTRA_SMALL = 4;
   public static final int SHAPE_SMALL = 8;
   public static final int SHAPE_MEDIUM = 12;
   public static final int SHAPE_LARGE = 16;
   public static final int SHAPE_EXTRA_LARGE = 28;
   public static final int SHAPE_FULL = 999;
   public static final int DURATION_SHORT1 = 100;
   public static final int DURATION_SHORT2 = 150;
   public static final int DURATION_MEDIUM1 = 250;
   public static final int DURATION_MEDIUM2 = 350;
   public static final int DURATION_LONG1 = 450;
   public static final int DURATION_LONG2 = 650;

   public Md3Theme(Md3ColorScheme colors, boolean dark) {
      this.colors = colors;
      this.dark = dark;
   }

   public static Md3Theme light() {
      return new Md3Theme(Md3ColorScheme.light(), false);
   }

   public static Md3Theme dark() {
      return new Md3Theme(Md3ColorScheme.dark(), true);
   }

   public Md3Theme withScheme(Md3ColorScheme scheme) {
      return new Md3Theme(scheme, this.dark);
   }

   public static float radius(float token, float height) {
      return token >= 999.0F ? height / 2.0F : token;
   }
}
