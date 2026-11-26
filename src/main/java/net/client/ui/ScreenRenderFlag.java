package net.client.ui;

public final class ScreenRenderFlag {
   private static int depth;

   private ScreenRenderFlag() {
   }

   public static void begin() {
      depth++;
   }

   public static void end() {
      if (depth > 0) {
         depth--;
      }
   }

   public static boolean active() {
      return depth > 0;
   }
}
