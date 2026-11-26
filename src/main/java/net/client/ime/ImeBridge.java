package net.client.ime;

import net.client.ClientConfig;
import net.client.ui.ClientTheme;
import net.client.util.ClientLog;
import net.client.util.HudFill;
import net.minecraft.class_1600;
import net.minecraft.class_367;
import net.minecraft.class_370;
import org.lwjgl.opengl.Display;

public final class ImeBridge {
   private static final ImeBackend BACKEND = (ImeBackend)(isWindows() ? new Imm32Backend() : new ImeBridge.UnsupportedBackend());
   private static final int KEY_ESCAPE = 1;
   private static final int KEY_BACKSPACE = 14;
   private static final int KEY_ENTER = 28;
   private static final int KEY_KP_ENTER = 156;
   private static volatile String composition = "";
   private static boolean warnedUnsupported;

   private ImeBridge() {
   }

   public static boolean isWindows() {
      String os = System.getProperty("os.name", "");
      return os != null && os.toLowerCase().contains("win");
   }

   public static boolean supported() {
      return !(BACKEND instanceof ImeBridge.UnsupportedBackend);
   }

   public static void onConfigChanged() {
      composition = "";
      BACKEND.reset();
   }

   public static String composition() {
      return composition;
   }

   public static boolean composing() {
      return !composition.isEmpty();
   }

   public static void onFieldRender(class_367 field) {
      if (field != null) {
         onFieldRender(field.field_1117, field.field_1118, field.method_946(), field.method_924(), field.method_941(), field.method_944());
      }
   }

   public static void onFieldRender(int fieldX, int fieldY, int fieldWidth, String text, int cursor, boolean focused) {
      if (!ClientConfig.imeFix) {
         composition = "";
      } else if (!BACKEND.available()) {
         if (!warnedUnsupported) {
            warnedUnsupported = true;
            ClientLog.warn("输入法桥当前平台不可用（仅 Windows 桌面端支持）");
         }

         composition = "";
      } else if (!focused) {
         composition = "";
      } else {
         String comp = BACKEND.composition();
         composition = comp == null ? "" : comp;
         if (!composition.isEmpty()) {
            class_1600 mc = class_1600.method_2965();
            if (mc != null) {
               int caret = caretX(fieldX, fieldWidth, text, cursor, mc);
               int lineH = mc.field_3814 == null ? 9 : mc.field_3814.field_1141;
               BACKEND.setCaret(toPixels(caret), toPixels(fieldY + lineH + 2));
               drawComposition(mc, comp, caret, fieldY);
            }
         }
      }
   }

   public static boolean handleKey(char typedChar, int keyCode) {
      if (!ClientConfig.imeFix || !composing() || !BACKEND.available()) {
         return false;
      } else if (keyCode == 1) {
         BACKEND.cancel();
         composition = "";
         return true;
      } else {
         return keyCode != 14 && keyCode != 28 && keyCode != 156 ? typedChar >= ' ' || typedChar > 0 : true;
      }
   }

   private static void drawComposition(class_1600 mc, String comp, int caret, int fieldY) {
      class_370 tr = mc.field_3814;
      if (tr != null && !comp.isEmpty()) {
         int y = fieldY + 6;
         int accent = ClientTheme.get().colors.primary;
         tr.method_4247(comp, caret, y, accent);
         int w = tr.method_954(comp);
         HudFill.fill(caret, y + tr.field_1141, caret + w, y + tr.field_1141 + 1, accent);
      }
   }

   private static int caretX(int fieldX, int fieldWidth, String text, int cursor, class_1600 mc) {
      if (text == null) {
         return fieldX + 4;
      } else {
         int end = cursor < 0 ? 0 : (cursor > text.length() ? text.length() : cursor);
         class_370 tr = mc.field_3814;
         int width = tr == null ? 0 : tr.method_954(text.substring(0, end));
         int x = fieldX + 4 + width;
         int maxX = fieldX + fieldWidth - 4;
         return x > maxX ? maxX : x;
      }
   }

   private static int toPixels(int gui) {
      int guiW = class_1600.method_2965() == null ? 0 : class_1600.method_2965().field_3801;
      int pxW = Display.getWidth();
      if (guiW > 0 && pxW > 0) {
         double scale = (double)pxW / guiW;
         return (int)Math.round(gui * scale);
      } else {
         return gui;
      }
   }

   static final class UnsupportedBackend implements ImeBackend {
      @Override
      public boolean available() {
         return false;
      }

      @Override
      public String composition() {
         return "";
      }

      @Override
      public void setCaret(int pixelX, int pixelY) {
      }

      @Override
      public void cancel() {
      }

      @Override
      public void reset() {
      }

      @Override
      public boolean swallowVirtualKey(int vk) {
         return false;
      }
   }
}
