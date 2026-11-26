package net.client;

import java.util.List;
import net.client.ui.ClientScroll;
import net.client.ui.ClientTheme;
import net.client.ui.ClientUi;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3Button;
import net.client.ui.md3.widget.Md3IconButton;
import net.client.ui.md3.widget.Md3ListItem;
import net.client.ui.md3.widget.Md3Surfaces;
import net.client.ui.md3.widget.Md3Text;
import net.client.util.Accounts;
import net.minecraft.class_388;
import net.minecraft.class_624;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class AccountScreen extends class_388 {
   private final class_388 parent;
   private final Md3Canvas canvas = new Md3Canvas();
   private final ClientScroll scroll = new ClientScroll();
   private final Md3IconButton closeButton = new Md3IconButton();
   private final Md3Button[] addButtons = new Md3Button[4];
   private final String[] addTypes = new String[]{"offline", "microsoft", "skin", "token"};
   private float cardX;
   private float cardY;
   private float cardW;
   private float cardH;
   private float listX;
   private float listY;
   private float listW;
   private float listH;
   private float[] rowYs = new float[0];
   private String toast;
   private long toastUntil;
   private int pendingDelete = -1;
   private long lastNanos;
   private float dt;
   private boolean mouseDown;
   private float mouseNowX;
   private float mouseNowY;
   private boolean stackedAddButtons;
   private boolean compactVertical;
   private float rowH = 56.0F;

   public AccountScreen(class_388 parent) {
      this.parent = parent;
   }

   @Override
   public void method_1044() {
      Keyboard.enableRepeatEvents(true);
      this.field_1232.clear();
      this.cardH = Math.min(430.0F, this.field_1231 - 24.0F);
      this.compactVertical = this.cardH < 300.0F;
      this.cardW = Math.min(440.0F, this.field_1230 - 24.0F);
      this.cardW = Math.min(this.cardW, Math.max(340.0F, this.cardH * 1.55F));
      this.cardX = (this.field_1230 - this.cardW) / 2.0F;
      this.cardY = (this.field_1231 - this.cardH) / 2.0F;
      this.listX = this.cardX + 18.0F;
      this.listY = this.cardY + (this.compactVertical ? 56.0F : 64.0F);
      this.listW = this.cardW - 36.0F;
      this.rowH = this.compactVertical ? 48.0F : 56.0F;
      this.stackedAddButtons = this.cardW < 400.0F;
      String[] labels = new String[]{Lang.tr("acc.offline"), Lang.tr("acc.ms"), Lang.tr("acc.skin"), Lang.tr("acc.token")};
      String[] icons = new String[]{"person", "account_circle", "face", "vpn_key"};
      if (this.stackedAddButtons) {
         float bh = this.compactVertical ? 32.0F : 36.0F;
         float gap = this.compactVertical ? 6.0F : 8.0F;
         float bottom = this.compactVertical ? 6.0F : 12.0F;
         float bw = (this.listW - gap) / 2.0F;
         float by = this.cardY + this.cardH - bottom - bh * 2.0F - gap;
         this.listH = Math.max(54.0F, by - (this.compactVertical ? 6.0F : 10.0F) - this.listY);

         for (int i = 0; i < 4; i++) {
            this.addButtons[i] = new Md3Button(Md3Button.Variant.OUTLINED, labels[i]);
            this.addButtons[i].icon = icons[i];
            this.addButtons[i].setBounds(this.listX + i % 2 * (bw + gap), by + i / 2 * (bh + gap), bw, bh);
         }
      } else {
         this.listH = this.cardH - 142.0F;
         float bw = (this.listW - 24.0F) / 4.0F;
         float by = this.cardY + this.cardH - 60.0F;

         for (int i = 0; i < 4; i++) {
            this.addButtons[i] = new Md3Button(Md3Button.Variant.OUTLINED, labels[i]);
            this.addButtons[i].icon = icons[i];
            this.addButtons[i].setBounds(this.listX + i * (bw + 8.0F), by, bw, 40.0F);
         }
      }

      this.closeButton.variant = Md3IconButton.Variant.STANDARD;
      this.closeButton.icon = "close";
      this.closeButton.setBounds(this.cardX + this.cardW - 50.0F, this.cardY + 12.0F, 38.0F, 38.0F);
      this.scroll.jump(0.0F);
   }

   @Override
   public void method_1025(int mouseX, int mouseY, float tickDelta) {
      this.mouseNowX = mouseX;
      this.mouseNowY = mouseY;
      long now = System.nanoTime();
      this.dt = this.lastNanos == 0L ? 0.016F : Math.min(0.05F, (float)(now - this.lastNanos) / 1.0E9F);
      this.lastNanos = now;
      super.method_1025(mouseX, mouseY, tickDelta);
      Md3Theme t = ClientTheme.get();
      this.canvas.begin(this.field_1230, this.field_1231);
      ClientUi.backdrop(this.canvas, t, this.field_1230, this.field_1231, this.field_1229 != null && this.field_1229.field_3803 != null);
      if (this.pendingDelete >= 0) {
         this.canvas.fill(0.0F, 0.0F, this.field_1230, this.field_1231, Md3Colors.alpha(t.colors.scrim, 0.58F));
      }

      this.canvas.shadow(this.cardX, this.cardY, this.cardW, this.cardH, 28.0F, 4);
      this.canvas.roundRect(this.cardX, this.cardY, this.cardW, this.cardH, 28.0F, t.colors.surfaceContainerHigh);
      this.canvas.text(Lang.tr("acc.title"), this.cardX + 24.0F, this.cardY + 18.0F, Md3Type.TITLE_LARGE, t.colors.onSurface);
      List<Accounts.Account> list = Accounts.list();
      int totalRows = list.size() + (Accounts.launcher() != null ? 1 : 0);
      String count = Integer.toString(totalRows);
      float tw = this.canvas.textWidth(Lang.tr("acc.title"), Md3Type.TITLE_LARGE);
      this.canvas.roundRect(this.cardX + 24.0F + tw + 10.0F, this.cardY + 18.0F, 26.0F, 22.0F, 11.0F, t.colors.primaryContainer);
      Md3Text.centered(this.canvas, count, this.cardX + 24.0F + tw + 23.0F, this.cardY + 29.0F, Md3Type.LABEL_MEDIUM, t.colors.onPrimaryContainer);
      this.canvas.text(Lang.tr("acc.hint"), this.cardX + 24.0F, this.cardY + 42.0F, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
      this.closeButton.surface = t.colors.surfaceContainerHigh;
      this.closeButton.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      if (this.closeButton.consumeClick()) {
         this.back();
      }

      this.canvas.roundRect(this.listX, this.listY, this.listW, this.listH, 16.0F, t.colors.surfaceContainerLow);
      this.renderList(t, list, mouseX, mouseY);

      for (Md3Button button : this.addButtons) {
         button.surface = t.colors.surfaceContainerHigh;
         button.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      }

      this.renderToast(t);
      if (this.pendingDelete >= 0 && this.pendingDelete < list.size()) {
         this.renderConfirm(t, list.get(this.pendingDelete), mouseX, mouseY);
      }

      this.canvas.end();
   }

   private void renderList(Md3Theme t, List<Accounts.Account> list, int mouseX, int mouseY) {
      float innerH = this.listH - 12.0F;
      Accounts.Account launcher = Accounts.launcher();
      int offset = launcher != null ? 1 : 0;
      int total = list.size() + offset;
      this.scroll.measure(total * this.rowH, innerH);
      this.scroll.update(this.dt);
      this.canvas.pushClip(this.listX + 4.0F, this.listY + 6.0F, this.listW - 8.0F, innerH);
      this.rowYs = new float[total];
      if (total == 0) {
         this.canvas
            .icon(
               "person_add",
               this.listX + this.listW / 2.0F - 16.0F,
               this.listY + this.listH / 2.0F - 34.0F,
               32.0F,
               Md3Colors.alpha(t.colors.onSurfaceVariant, 0.55F)
            );
         Md3Text.centered(
            this.canvas,
            this.tr("还没有账号", "No accounts yet"),
            this.listX + this.listW / 2.0F,
            this.listY + this.listH / 2.0F + 2.0F,
            Md3Type.TITLE_SMALL,
            t.colors.onSurfaceVariant
         );
         Md3Text.centered(
            this.canvas,
            this.tr("从下面添加一个", "Add one below"),
            this.listX + this.listW / 2.0F,
            this.listY + this.listH / 2.0F + 24.0F,
            Md3Type.BODY_MEDIUM,
            Md3Colors.alpha(t.colors.onSurfaceVariant, 0.7F)
         );
         this.canvas.popClip();
      } else {
         int current = Accounts.currentIndex();
         float base = this.listY + 6.0F - this.scroll.value;

         for (int row = 0; row < total; row++) {
            float y = base + row * this.rowH;
            this.rowYs[row] = y;
            if (!(y + this.rowH < this.listY) && !(y > this.listY + this.listH)) {
               Md3ListItem item = new Md3ListItem();
               if (launcher != null && row == 0) {
                  item.headline = launcher.name;
                  item.supporting = this.typeLabel(launcher.type) + (Accounts.usingLauncher() ? " · " + Lang.tr("acc.current") : "");
                  item.leadingText = launcher.name;
                  item.trailingIcon = null;
                  item.selected = Accounts.usingLauncher();
               } else {
                  int index = row - offset;
                  Accounts.Account account = list.get(index);
                  item.headline = account.name;
                  item.supporting = this.typeLabel(account.type) + (!Accounts.usingLauncher() && index == current ? " · " + Lang.tr("acc.current") : "");
                  item.leadingText = account.name;
                  item.trailingIcon = "delete";
                  item.selected = !Accounts.usingLauncher() && index == current;
               }

               item.setBounds(this.listX + 6.0F, y, this.listW - 12.0F, this.rowH);
               item.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
               if (row < total - 1) {
                  this.canvas.fill(this.listX + 30.0F, y + this.rowH - 1.0F, this.listW - 60.0F, 1.0F, t.colors.outlineVariant);
               }
            }
         }

         this.canvas.popClip();
         this.scroll.renderBar(this.canvas, t, this.listX + this.listW - 8.0F, this.listY + 8.0F, 4.0F);
      }
   }

   private void renderConfirm(Md3Theme t, Accounts.Account account, int mouseX, int mouseY) {
      float w = Math.min(340.0F, this.field_1230 - 24.0F);
      float h = 180.0F;
      float x = (this.field_1230 - w) / 2.0F;
      float y = (this.field_1231 - h) / 2.0F;
      Md3Surfaces.dialog(this.canvas, t, x, y, w, h);
      this.canvas.text(Lang.tr("acc.del.title"), x + 24.0F, y + 20.0F, Md3Type.TITLE_MEDIUM, t.colors.onSurface);
      this.canvas.text(account.name, x + 24.0F, y + 46.0F, Md3Type.BODY_LARGE, t.colors.primary);
      this.canvas.text(this.tr("此操作不可撤销", "This cannot be undone"), x + 24.0F, y + 68.0F, Md3Type.BODY_MEDIUM, t.colors.onSurfaceVariant);
      float bw = 86.0F;
      float bh = 38.0F;
      float by = y + h - 20.0F - bh;
      float delX = x + w - 24.0F - bw;
      float cancelX = delX - 8.0F - bw;
      boolean delHot = ClientUi.hit(mouseX, mouseY, delX, by, bw, bh);
      this.canvas
         .roundRect(delX, by, bw, bh, bh / 2.0F, delHot ? Md3Colors.composite(Md3Colors.alpha(t.colors.onError, 0.12F), t.colors.error) : t.colors.error);
      Md3Text.centered(this.canvas, Lang.tr("acc.del.ok"), delX + bw / 2.0F, by + bh / 2.0F, Md3Type.LABEL_LARGE, t.colors.onError);
      boolean cancelHot = ClientUi.hit(mouseX, mouseY, cancelX, by, bw, bh);
      this.canvas.roundRect(cancelX, by, bw, bh, bh / 2.0F, cancelHot ? t.colors.surfaceContainerHighest : t.colors.surfaceContainerLow);
      Md3Text.centered(this.canvas, Lang.tr("acc.cancel"), cancelX + bw / 2.0F, by + bh / 2.0F, Md3Type.LABEL_LARGE, t.colors.onSurface);
   }

   private void renderToast(Md3Theme t) {
      if (this.toast != null && System.currentTimeMillis() < this.toastUntil) {
         float w = Math.min(this.cardW - 40.0F, this.canvas.textWidth(this.toast, Md3Type.BODY_MEDIUM) + 44.0F);
         float x = this.cardX + (this.cardW - w) / 2.0F;
         float y = this.cardY + this.cardH - (this.stackedAddButtons ? 112.0F : 48.0F);
         Md3Surfaces.snackbar(this.canvas, t, x, y, w, 38.0F);
         Md3Text.centered(
            this.canvas,
            ClientUi.fit(this.canvas, this.toast, Md3Type.BODY_MEDIUM, w - 28.0F),
            x + w / 2.0F,
            y + 19.0F,
            Md3Type.BODY_MEDIUM,
            t.colors.inverseOnSurface
         );
      }
   }

   private String typeLabel(String type) {
      if ("offline".equals(type)) {
         return Lang.tr("acc.offline");
      } else if ("token".equals(type)) {
         return Lang.tr("acc.token");
      } else if ("launcher".equals(type)) {
         return this.tr("启动器账号", "Launcher account");
      } else {
         return "skin".equals(type) ? Lang.tr("acc.skin") : Lang.tr("acc.ms");
      }
   }

   @Override
   public void method_1035() {
      int wheel = Mouse.getEventDWheel();
      if (wheel != 0 && this.pendingDelete < 0) {
         this.scroll.scrollBy(-Integer.signum(wheel) * 48.0F);
      }

      super.method_1035();
   }

   @Override
   protected void method_1026(int mouseX, int mouseY, int button) {
      this.mouseNowX = mouseX;
      this.mouseNowY = mouseY;
      if (button == 0) {
         this.mouseDown = true;
      }

      List<Accounts.Account> list = Accounts.list();
      if (this.pendingDelete >= 0) {
         if (this.pendingDelete < list.size()) {
            float w = Math.min(340.0F, this.field_1230 - 24.0F);
            float h = 180.0F;
            float x = (this.field_1230 - w) / 2.0F;
            float y = (this.field_1231 - h) / 2.0F;
            float bw = 86.0F;
            float bh = 38.0F;
            float by = y + h - 20.0F - bh;
            float delX = x + w - 24.0F - bw;
            float cancelX = delX - 8.0F - bw;
            if (button == 0 && ClientUi.hit(mouseX, mouseY, delX, by, bw, bh)) {
               Accounts.remove(this.pendingDelete);
               this.pendingDelete = -1;
               this.scroll.jump(0.0F);
               this.toast = this.tr("账号已删除", "Account removed");
               this.toastUntil = System.currentTimeMillis() + 2200L;
               ClientUi.click();
               return;
            }

            if (button == 0 && ClientUi.hit(mouseX, mouseY, cancelX, by, bw, bh)) {
               this.pendingDelete = -1;
               ClientUi.click();
               return;
            }
         } else {
            this.pendingDelete = -1;
         }
      } else if (button != 0 || !ClientUi.hit(mouseX, mouseY, this.closeButton.x, this.closeButton.y, this.closeButton.w, this.closeButton.h)) {
         if (button == 0) {
            for (int i = 0; i < this.addButtons.length; i++) {
               Md3Button b = this.addButtons[i];
               if (ClientUi.hit(mouseX, mouseY, b.x, b.y, b.w, b.h)) {
                  ClientUi.click();
                  this.field_1229.method_2928(new AccountLoginScreen(this, this.addTypes[i]));
                  return;
               }
            }
         }

         Accounts.Account launcher = Accounts.launcher();
         int offset = launcher != null ? 1 : 0;
         int total = list.size() + offset;

         for (int row = 0; row < this.rowYs.length && row < total; row++) {
            float yx = this.rowYs[row];
            if (!(yx < this.listY)
               && !(yx + this.rowH > this.listY + this.listH)
               && ClientUi.hit(mouseX, mouseY, this.listX + 6.0F, yx, this.listW - 12.0F, this.rowH)) {
               if (launcher != null && row == 0) {
                  if (button == 0) {
                     Accounts.selectLauncher();
                     this.toast = this.tr("已切换到启动器账号", "Switched to launcher account");
                     this.toastUntil = System.currentTimeMillis() + 2200L;
                     ClientUi.click();
                     return;
                  }
               } else {
                  int index = row - offset;
                  if (ClientUi.hit(mouseX, mouseY, this.listX + this.listW - 56.0F, yx + 10.0F, 36.0F, 36.0F)) {
                     this.pendingDelete = index;
                     ClientUi.click();
                     return;
                  }

                  if (button == 1) {
                     this.pendingDelete = index;
                     ClientUi.click();
                     return;
                  }

                  if (button == 0) {
                     Accounts.select(index);
                     this.toast = Lang.tr("acc.applied");
                     this.toastUntil = System.currentTimeMillis() + 2200L;
                     ClientUi.click();
                     return;
                  }
               }
            }
         }
      }
   }

   @Override
   protected void method_1032(int mouseX, int mouseY, int button) {
      this.mouseDown = false;
      this.scroll.release();
   }

   @Override
   protected void method_4259(int mouseX, int mouseY, int button, long lastClick) {
      if (this.scroll.dragging) {
         this.scroll.drag(mouseY, this.listY + 8.0F);
      }
   }

   @Override
   protected void method_1024(char id, int code) {
      if (code == 1) {
         if (this.pendingDelete >= 0) {
            this.pendingDelete = -1;
         } else {
            this.back();
         }
      }
   }

   private void back() {
      this.field_1229.method_2928((class_388)(this.parent != null ? this.parent : new class_624()));
   }

   private String tr(String zh, String en) {
      return ClientConfig.t(zh, en);
   }

   @Override
   public boolean method_1037() {
      return false;
   }
}
