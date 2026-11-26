package net.client.util;

import net.minecraft.class_1600;
import net.minecraft.class_1825;
import net.minecraft.class_1827;
import net.minecraft.class_2403;
import net.minecraft.class_372;
import net.minecraft.class_376;
import net.minecraft.class_485;
import net.minecraft.class_486;

public final class ServerListDrag {
   private static boolean active;
   private static class_376 screen;
   private static class_1827 widget;
   private static class_486 list;
   private static int source = -1;
   private static int target = -1;
   private static int startX;
   private static int startY;
   private static boolean moved;
   private static long pickupNanos;
   private static long ghostFrameNanos;
   private static float ghostScale;

   private ServerListDrag() {
   }

   public static void reset() {
      active = false;
      screen = null;
      widget = null;
      list = null;
      source = -1;
      target = -1;
      moved = false;
      pickupNanos = 0L;
      ghostFrameNanos = 0L;
      ghostScale = 0.9F;
      startX = 0;
      startY = 0;
   }

   public static boolean isActive() {
      return active;
   }

   public static void onPress(class_376 owner, class_1827 view, class_486 serverList, int mouseX, int mouseY, int button) {
      reset();
      if (button == 0 && owner != null && view != null && serverList != null) {
         int index = serverEntryAt(owner, view, serverList, mouseX, mouseY);
         if (index >= 0) {
            int rowWidth = view.method_6706();
            int rowLeft = owner.field_1230 / 2 - rowWidth / 2;
            if (mouseX >= rowLeft + 36) {
               active = true;
               screen = owner;
               widget = view;
               list = serverList;
               source = index;
               target = index;
               startX = mouseX;
               startY = mouseY;
               moved = false;
               pickupNanos = System.nanoTime();
               ghostFrameNanos = 0L;
               ghostScale = 0.9F;
            }
         }
      }
   }

   public static boolean onDrag(class_376 owner, int mouseX, int mouseY, int button) {
      if (active && owner == screen && button == 0) {
         if (!moved && (Math.abs(mouseX - startX) >= 5 || Math.abs(mouseY - startY) >= 5)) {
            moved = true;
            pickupNanos = System.nanoTime();
            ghostFrameNanos = 0L;
            ghostScale = 0.9F;
         }

         if (moved) {
            int index = serverEntryAt(owner, widget, list, mouseX, mouseY);
            if (index >= 0) {
               target = index;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean onRelease(class_376 owner, int mouseX, int mouseY, int button) {
      if (active && owner == screen) {
         boolean apply = button == 0 && moved && target >= 0 && target != source;
         ServerListDrag.LocalState state = new ServerListDrag.LocalState(source, target);
         class_486 serverList = list;
         class_1827 view = widget;
         reset();
         if (apply && serverList != null && view != null) {
            int from = state.from;
            int to = state.to;
            if (to >= 0 && to < serverList.method_1281()) {
               while (from < to) {
                  serverList.method_1275(from, from + 1);
                  from++;
               }

               while (from > to) {
                  serverList.method_1275(from, from - 1);
                  from--;
               }

               view.method_6791(serverList);
               owner.method_6781(to);
               serverList.method_1278();
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void renderGhost(int mouseX, int mouseY) {
      if (active && moved && list != null && source >= 0 && source < list.method_1281()) {
         class_485 info = list.method_1274(source);
         if (info != null) {
            if (screen != null && widget != null && list != null) {
               int index = serverEntryAt(screen, widget, list, mouseX, mouseY);
               if (index >= 0) {
                  target = index;
               }
            }

            class_1600 mc = class_1600.method_2965();
            if (mc != null) {
               long now = System.nanoTime();
               if (ghostFrameNanos != 0L) {
                  float dt = Math.min(0.05F, (float)(now - ghostFrameNanos) / 1.0E9F);
                  ghostScale = ghostScale + (1.0F - ghostScale) * Math.min(1.0F, dt * 16.0F);
               }

               ghostFrameNanos = now;
               float age = pickupNanos == 0L ? 1.0F : Math.min(1.0F, (float)(now - pickupNanos) / 1.6E8F);
               float ease = 1.0F - (1.0F - age) * (1.0F - age) * (1.0F - age);
               float scale = 0.9F + 0.1F * Math.max(ease, ghostScale);
               float lift = (1.0F - ease) * 10.0F;
               String name = info.field_1686 == null ? "" : info.field_1686;
               int textW = Math.max(44, mc.field_3814.method_954(name));
               int boxW = textW + 24;
               int boxH = 22;
               class_2403.method_9791();
               class_2403.method_9816(mouseX + 10.0F + boxW / 2.0F, mouseY - 8.0F + lift + boxH / 2.0F, 0.0F);
               class_2403.method_9800(scale, scale, 1.0F);
               class_372.method_988(-boxW / 2 - 3, -boxH / 2 + 3, boxW / 2 + 3, boxH / 2 + 3, 1426063360);
               class_372.method_988(-boxW / 2, -boxH / 2, boxW / 2, boxH / 2, -299163336);
               mc.field_3814.method_4247(name, -textW / 2, -4, -791041);
               class_2403.method_9792();
            }
         }
      }
   }

   private static int serverEntryAt(class_376 owner, class_1827 view, class_486 serverList, int mouseX, int mouseY) {
      if (view != null && serverList != null && view.method_6707(mouseY)) {
         int index = view.method_1067(mouseX, mouseY);
         if (index >= 0 && index < serverList.method_1281()) {
            Object entry = view.method_6697(index);
            if (!(entry instanceof class_1825)) {
               return -1;
            } else {
               class_485 info = ((class_1825)entry).method_6787();
               return info == null ? -1 : index;
            }
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private static final class LocalState {
      final int from;
      final int to;

      LocalState(int from, int to) {
         this.from = from;
         this.to = to;
      }
   }
}
