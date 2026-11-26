package net.client.ime;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Arrays;
import java.util.List;
import net.client.util.ClientLog;

final class Imm32Backend implements ImeBackend {
   private static final int GCS_COMPSTR = 8;
   private static final int CFS_POINT = 2;
   private static final int NI_COMPOSITIONSTR = 21;
   private static final int CPS_CANCEL = 4;
   private static final int VK_ESCAPE = 27;
   private static final int VK_RETURN = 13;
   private static final int VK_BACK = 8;
   private static boolean loaded;
   private static boolean loadFailed;
   private static Pointer hwnd;
   private static Pointer himc;
   private static String lastWarn = "";

   private static void warnOnce(String msg) {
      if (!lastWarn.equals(msg)) {
         lastWarn = msg;
         ClientLog.warn(msg);
      }
   }

   @Override
   public boolean available() {
      return this.init();
   }

   private boolean init() {
      if (loadFailed) {
         return false;
      } else {
         if (!loaded) {
            loaded = true;

            try {
               Class.forName("com.sun.jna.Native");
            } catch (Throwable var3) {
               warnOnce("未找到 JNA，输入法桥不可用: " + var3);
               loadFailed = true;
               return false;
            }
         }

         if (hwnd == null) {
            hwnd = findWindow();
            if (hwnd == null) {
               return false;
            }
         }

         if (himc == null) {
            try {
               Pointer ctx = Imm32Backend.Imm.INSTANCE.ImmGetContext(hwnd);
               if (ctx == null) {
                  ctx = Imm32Backend.Imm.INSTANCE.ImmCreateContext();
                  if (ctx == null) {
                     warnOnce("ImmCreateContext 返回空，跳过输入法桥");
                     return false;
                  }

                  Imm32Backend.Imm.INSTANCE.ImmAssociateContext(hwnd, ctx);
                  ClientLog.info("输入法：已重新关联 IMM32 上下文");
               }

               himc = ctx;
            } catch (Throwable var2) {
               warnOnce("获取输入法上下文失败: " + var2);
               loadFailed = true;
               return false;
            }
         }

         return himc != null;
      }
   }

   private static Pointer findWindow() {
      try {
         Pointer h = Imm32Backend.User32.INSTANCE.FindWindow("LWJGL", null);
         return h != null ? h : Imm32Backend.User32.INSTANCE.GetForegroundWindow();
      } catch (Throwable var1) {
         warnOnce("查找游戏窗口失败: " + var1);
         return null;
      }
   }

   @Override
   public String composition() {
      if (!this.init()) {
         return "";
      } else {
         try {
            int bytes = Imm32Backend.Imm.INSTANCE.ImmGetCompositionStringW(himc, 8, null, 0);
            if (bytes <= 0) {
               return "";
            } else {
               Memory mem = new Memory(bytes);
               int got = Imm32Backend.Imm.INSTANCE.ImmGetCompositionStringW(himc, 8, mem, bytes);
               if (got <= 0) {
                  return "";
               } else {
                  int chars = got / 2;
                  StringBuilder sb = new StringBuilder(chars);

                  for (int i = 0; i < chars; i++) {
                     sb.append(mem.getChar(i * 2L));
                  }

                  return sb.toString();
               }
            }
         } catch (Throwable var7) {
            warnOnce("读取组字串失败: " + var7);
            return "";
         }
      }
   }

   @Override
   public void setCaret(int pixelX, int pixelY) {
      if (this.init()) {
         try {
            Imm32Backend.COMPOSITIONFORM form = new Imm32Backend.COMPOSITIONFORM();
            form.dwStyle = 2;
            form.ptCurrentPos.x = pixelX;
            form.ptCurrentPos.y = pixelY;
            form.write();
            Imm32Backend.Imm.INSTANCE.ImmSetCompositionWindow(himc, form);
         } catch (Throwable var4) {
            warnOnce("候选窗定位失败: " + var4);
         }
      }
   }

   @Override
   public void cancel() {
      if (this.init()) {
         try {
            Imm32Backend.Imm.INSTANCE.ImmNotifyIME(himc, 21, 4, 0);
         } catch (Throwable var2) {
            warnOnce("取消组字失败: " + var2);
         }
      }
   }

   @Override
   public void reset() {
      himc = null;
   }

   @Override
   public boolean swallowVirtualKey(int vk) {
      return vk != 27 && vk != 13 && vk != 8 ? vk >= 32 && vk <= 90 || vk >= 96 && vk <= 111 || vk >= 186 && vk <= 223 : true;
   }

   public static class COMPOSITIONFORM extends Structure {
      public int dwStyle;
      public Imm32Backend.POINT ptCurrentPos = new Imm32Backend.POINT();
      public Imm32Backend.RECT rcArea = new Imm32Backend.RECT();

      @Override
      protected List<String> getFieldOrder() {
         return Arrays.asList("dwStyle", "ptCurrentPos", "rcArea");
      }
   }

   interface Imm extends StdCallLibrary {
      Imm32Backend.Imm INSTANCE = Native.load("imm32", Imm32Backend.Imm.class, W32APIOptions.DEFAULT_OPTIONS);

      Pointer ImmGetContext(Pointer var1);

      boolean ImmReleaseContext(Pointer var1, Pointer var2);

      Pointer ImmCreateContext();

      boolean ImmDestroyContext(Pointer var1);

      Pointer ImmAssociateContext(Pointer var1, Pointer var2);

      int ImmGetCompositionStringW(Pointer var1, int var2, Pointer var3, int var4);

      boolean ImmSetCompositionWindow(Pointer var1, Imm32Backend.COMPOSITIONFORM var2);

      boolean ImmNotifyIME(Pointer var1, int var2, int var3, int var4);
   }

   public static class POINT extends Structure {
      public int x;
      public int y;

      @Override
      protected List<String> getFieldOrder() {
         return Arrays.asList("x", "y");
      }
   }

   public static class RECT extends Structure {
      public int left;
      public int top;
      public int right;
      public int bottom;

      @Override
      protected List<String> getFieldOrder() {
         return Arrays.asList("left", "top", "right", "bottom");
      }
   }

   interface User32 extends StdCallLibrary {
      Imm32Backend.User32 INSTANCE = Native.load("user32", Imm32Backend.User32.class, W32APIOptions.DEFAULT_OPTIONS);

      Pointer FindWindow(String var1, String var2);

      Pointer GetForegroundWindow();
   }
}
