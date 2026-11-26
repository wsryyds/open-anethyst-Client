package net.client.ui.md3.theme;

public enum Md3Type {
   DISPLAY_LARGE(34, Md3Type.Weight.REGULAR, -0.6F, 42.0F),
   DISPLAY_MEDIUM(29, Md3Type.Weight.REGULAR, -0.4F, 36.0F),
   DISPLAY_SMALL(25, Md3Type.Weight.REGULAR, -0.2F, 32.0F),
   HEADLINE_LARGE(24, Md3Type.Weight.REGULAR, 0.0F, 32.0F),
   HEADLINE_MEDIUM(21, Md3Type.Weight.REGULAR, 0.0F, 28.0F),
   HEADLINE_SMALL(18, Md3Type.Weight.REGULAR, 0.0F, 25.0F),
   TITLE_LARGE(18, Md3Type.Weight.MEDIUM, 0.0F, 24.0F),
   TITLE_MEDIUM(15, Md3Type.Weight.MEDIUM, 0.0F, 21.0F),
   TITLE_SMALL(13, Md3Type.Weight.MEDIUM, 0.0F, 19.0F),
   BODY_LARGE(14, Md3Type.Weight.REGULAR, 0.1F, 20.0F),
   BODY_MEDIUM(12, Md3Type.Weight.REGULAR, 0.1F, 18.0F),
   BODY_SMALL(11, Md3Type.Weight.REGULAR, 0.1F, 16.0F),
   VANILLA_TEXT(9, Md3Type.Weight.REGULAR, 0.0F, 12.0F),
   WORLD_TEXT(9, Md3Type.Weight.REGULAR, 0.0F, 12.0F, "regular_14"),
   LABEL_LARGE(13, Md3Type.Weight.MEDIUM, 0.3F, 19.0F),
   LABEL_MEDIUM(12, Md3Type.Weight.MEDIUM, 0.4F, 17.0F),
   LABEL_SMALL(10, Md3Type.Weight.MEDIUM, 0.5F, 15.0F);

   public final int size;
   public final Md3Type.Weight weight;
   public final float tracking;
   public final float lineHeight;
   public final String atlasOverride;

   private Md3Type(int size, Md3Type.Weight weight, float tracking, float lineHeight) {
      this(size, weight, tracking, lineHeight, null);
   }

   private Md3Type(int size, Md3Type.Weight weight, float tracking, float lineHeight, String atlasOverride) {
      this.size = size;
      this.weight = weight;
      this.tracking = tracking;
      this.lineHeight = lineHeight;
      this.atlasOverride = atlasOverride;
   }

   public String atlasKey() {
      return this.atlasOverride != null ? this.atlasOverride : this.weight.key + "_" + this.size;
   }

   public static enum Weight {
      REGULAR("regular"),
      MEDIUM("medium");

      public final String key;

      private Weight(String key) {
         this.key = key;
      }
   }
}
