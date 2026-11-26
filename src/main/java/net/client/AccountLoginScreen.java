package net.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import net.client.ime.ImeBridge;
import net.client.ui.ClientTheme;
import net.client.ui.ClientUi;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3IconButton;
import net.client.ui.md3.widget.Md3Progress;
import net.client.ui.md3.widget.Md3Text;
import net.client.ui.md3.widget.Md3TextField;
import net.client.util.Accounts;
import net.client.util.Auth;
import net.minecraft.class_1600;
import net.minecraft.class_388;
import org.lwjgl.input.Keyboard;

public class AccountLoginScreen extends class_388 {
   private final class_388 parent;
   private final String type;
   private final List<AccountLoginScreen.Field> fields = new ArrayList<>();
   private final Md3Canvas canvas = new Md3Canvas();
   private final Md3IconButton closeButton = new Md3IconButton();
   private int focus;
   private float cardX;
   private float cardY;
   private float cardW;
   private float cardH;
   private float fieldH = 52.0F;
   private float fieldGap = 8.0F;
   private String status = "";
   private boolean busy;
   private volatile String deviceUserCode;
   private volatile String deviceUri;
   private volatile Accounts.Account deviceAccount;
   private volatile String deviceError;
   private long lastNanos;
   private float dt;
   private boolean mouseDown;
   private float time;

   public AccountLoginScreen(class_388 parent, String type) {
      this.parent = parent;
      this.type = type;
   }

   private String title() {
      if ("microsoft".equals(this.type)) {
         return Lang.tr("acc.ms");
      } else if ("skin".equals(this.type)) {
         return Lang.tr("acc.skin");
      } else {
         return "token".equals(this.type) ? Lang.tr("acc.token") : Lang.tr("acc.offline");
      }
   }

   private String icon() {
      if ("microsoft".equals(this.type)) {
         return "account_circle";
      } else if ("skin".equals(this.type)) {
         return "language";
      } else {
         return "token".equals(this.type) ? "vpn_key" : "person_add";
      }
   }

   @Override
   public void method_1044() {
      this.field_1232.clear();
      this.fields.clear();
      if ("offline".equals(this.type)) {
         this.addField(Lang.tr("acc.f.name"), false, "");
      } else if ("token".equals(this.type)) {
         this.addField(Lang.tr("acc.f.token"), true, "");
      } else if ("microsoft".equals(this.type)) {
         this.addField(Lang.tr("acc.f.client"), false, ClientConfig.msClientId == null ? "" : ClientConfig.msClientId.trim());
      } else if ("skin".equals(this.type)) {
         this.addField(Lang.tr("acc.f.site"), false, "");
         this.addField(Lang.tr("acc.f.email"), false, "");
         this.addField(Lang.tr("acc.f.password"), true, "");
      }

      this.cardW = Math.min(440.0F, this.field_1230 - 24.0F);
      float infoH = "microsoft".equals(this.type) ? 52.0F : 34.0F;
      float desired = 64.0F + this.fields.size() * (this.fieldH + this.fieldGap) + infoH + 76.0F;
      this.cardH = Math.min(desired, this.field_1231 - 16.0F);
      if (this.cardH < desired && !this.fields.isEmpty()) {
         float usable = this.cardH - 64.0F - infoH - 76.0F - this.fieldGap * this.fields.size();
         this.fieldH = Math.max(44.0F, usable / this.fields.size());
      }

      this.cardX = (this.field_1230 - this.cardW) / 2.0F;
      this.cardY = (this.field_1231 - this.cardH) / 2.0F;
      float fx = this.cardX + 24.0F;
      float fw = this.cardW - 48.0F;
      float fy = this.cardY + 64.0F;

      for (AccountLoginScreen.Field field : this.fields) {
         field.widget.setBounds(fx, fy, fw, this.fieldH);
         field.widget.surface = ClientTheme.get().colors.surfaceContainerHigh;
         fy += this.fieldH + this.fieldGap;
      }

      this.closeButton.variant = Md3IconButton.Variant.STANDARD;
      this.closeButton.icon = "close";
      this.closeButton.setBounds(this.cardX + this.cardW - 50.0F, this.cardY + 12.0F, 38.0F, 38.0F);
      this.focus = 0;
      if ("microsoft".equals(this.type) && this.deviceUserCode == null && this.deviceError == null) {
         this.startDeviceFlow();
      }
   }

   private void addField(String label, boolean secret, String value) {
      Md3TextField widget = new Md3TextField();
      widget.variant = Md3TextField.Variant.FILLED;
      widget.label = label;
      widget.supporting = "";
      widget.masked = secret;
      widget.maxLength = 128;
      widget.text = value;
      this.fields.add(new AccountLoginScreen.Field(widget));
   }

   private String typedClientId() {
      return "microsoft".equals(this.type) ? this.value(0) : "";
   }

   private void startDeviceFlow() {
      this.busy = true;
      this.status = Lang.tr("acc.ms.waiting");
      Thread thread = new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               Auth.DeviceCode code = Auth.requestDeviceCode(AccountLoginScreen.this.typedClientId());
               AccountLoginScreen.this.deviceUri = code.verificationUri;
               AccountLoginScreen.this.deviceUserCode = code.userCode;
               AccountLoginScreen.this.deviceAccount = Auth.awaitMicrosoft(code, AccountLoginScreen.this.typedClientId());
            } catch (Exception var2) {
               AccountLoginScreen.this.deviceError = var2.getMessage() == null ? var2.getClass().getSimpleName() : var2.getMessage();
            }
         }
      }, "Client-AccountLogin");
      thread.setDaemon(true);
      thread.start();
   }

   @Override
   public void method_1025(int mouseX, int mouseY, float tickDelta) {
      long now = System.nanoTime();
      this.dt = this.lastNanos == 0L ? 0.016F : Math.min(0.05F, (float)(now - this.lastNanos) / 1.0E9F);
      this.lastNanos = now;
      this.time = this.time + this.dt;
      super.method_1025(mouseX, mouseY, tickDelta);
      Md3Theme t = ClientTheme.get();
      this.canvas.begin(this.field_1230, this.field_1231);
      ClientUi.backdrop(this.canvas, t, this.field_1230, this.field_1231, this.field_1229 != null && this.field_1229.field_3803 != null);
      this.canvas.shadow(this.cardX, this.cardY, this.cardW, this.cardH, 28.0F, 4);
      this.canvas.roundRect(this.cardX, this.cardY, this.cardW, this.cardH, 28.0F, t.colors.surfaceContainerHigh);
      float iconX = this.cardX + 24.0F;
      float iconY = this.cardY + 18.0F;
      this.canvas.roundRect(iconX, iconY, 36.0F, 36.0F, 12.0F, t.colors.primaryContainer);
      this.canvas.icon(this.icon(), iconX + 8.0F, iconY + 8.0F, 20.0F, t.colors.onPrimaryContainer);
      this.canvas.text(this.title(), iconX + 46.0F, iconY + 9.0F, Md3Type.TITLE_LARGE, t.colors.onSurface);
      this.closeButton.surface = t.colors.surfaceContainerHigh;
      this.closeButton.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      if (this.closeButton.consumeClick()) {
         this.back();
      }

      for (int i = 0; i < this.fields.size(); i++) {
         AccountLoginScreen.Field field = this.fields.get(i);
         field.widget.focused = i == this.focus;
         field.widget.surface = t.colors.surfaceContainerHigh;
         field.widget.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
         if (i == this.focus) {
            ImeBridge.onFieldRender(
               (int)field.widget.x + 16, (int)field.widget.y + 28, (int)field.widget.w - 32, field.widget.text, field.widget.text.length(), true
            );
         }
      }

      float infoY = this.cardY + 64.0F + this.fields.size() * (this.fieldH + this.fieldGap) + 2.0F;
      this.renderStatus(t, infoY);
      float by = this.cardY + this.cardH - 20.0F - 40.0F;
      float bw = 100.0F;
      float okX = this.cardX + this.cardW - 24.0F - bw;
      float cancelX = okX - 8.0F - bw;
      boolean okHot = ClientUi.hit(mouseX, mouseY, okX, by, bw, 40.0F);
      int okBase = this.busy ? Md3Colors.composite(Md3Colors.alpha(t.colors.onSurface, 0.12F), t.colors.primary) : t.colors.primary;
      if (okHot && !this.busy) {
         okBase = Md3Colors.composite(Md3Colors.alpha(t.colors.onPrimary, 0.12F), t.colors.primary);
      }

      this.canvas.roundRect(okX, by, bw, 40.0F, 20.0F, okBase);
      String okLabel = this.busy ? Lang.tr("acc.waiting") : Lang.tr("acc.ok");
      Md3Text.centered(
         this.canvas,
         ClientUi.fit(this.canvas, okLabel, Md3Type.LABEL_LARGE, bw - 16.0F),
         okX + bw / 2.0F,
         by + 20.0F,
         Md3Type.LABEL_LARGE,
         this.busy ? Md3Colors.alpha(t.colors.onPrimary, 0.7F) : t.colors.onPrimary
      );
      boolean cancelHot = ClientUi.hit(mouseX, mouseY, cancelX, by, bw, 40.0F);
      this.canvas.roundRect(cancelX, by, bw, 40.0F, 20.0F, cancelHot ? t.colors.surfaceContainerHighest : t.colors.surfaceContainerLow);
      Md3Text.centered(this.canvas, Lang.tr("acc.cancel"), cancelX + bw / 2.0F, by + 20.0F, Md3Type.LABEL_LARGE, t.colors.onSurface);
      this.canvas.end();
   }

   private void renderStatus(Md3Theme t, float y) {
      if ("microsoft".equals(this.type)) {
         if (this.deviceError != null) {
            this.canvas
               .text(
                  ClientUi.fit(this.canvas, Lang.tr("acc.ms.error") + ": " + this.deviceError, Md3Type.BODY_MEDIUM, this.cardW - 48.0F),
                  this.cardX + 24.0F,
                  y,
                  Md3Type.BODY_MEDIUM,
                  t.colors.error
               );
         } else if (this.deviceAccount != null) {
            this.canvas.icon("check_circle", this.cardX + 24.0F, y - 2.0F, 18.0F, t.colors.primary);
            this.canvas.text(Lang.tr("acc.ms.ok"), this.cardX + 48.0F, y, Md3Type.BODY_MEDIUM, t.colors.primary);
         } else if (this.deviceUserCode != null) {
            this.canvas
               .text(
                  ClientUi.fit(this.canvas, Lang.tr("acc.ms.go") + "  " + this.deviceUri, Md3Type.BODY_SMALL, this.cardW - 48.0F),
                  this.cardX + 24.0F,
                  y,
                  Md3Type.BODY_SMALL,
                  t.colors.onSurfaceVariant
               );
            String code = this.deviceUserCode;
            float cw = Math.max(120.0F, this.canvas.textWidth(code, Md3Type.TITLE_SMALL) + 28.0F);
            this.canvas.roundRect(this.cardX + 24.0F, y + 18.0F, cw, 26.0F, 8.0F, t.colors.surfaceContainerHighest);
            Md3Text.centered(this.canvas, code, this.cardX + 24.0F + cw / 2.0F, y + 31.0F, Md3Type.TITLE_SMALL, t.colors.primary);
         } else {
            this.renderBusyLine(t, y);
         }
      } else {
         if (!this.status.isEmpty()) {
            boolean error = this.status.startsWith(Lang.tr("acc.err")) || this.status.startsWith("失败");
            this.canvas
               .text(
                  ClientUi.fit(this.canvas, this.status, Md3Type.BODY_MEDIUM, this.cardW - 48.0F),
                  this.cardX + 24.0F,
                  y,
                  Md3Type.BODY_MEDIUM,
                  error ? t.colors.error : t.colors.onSurfaceVariant
               );
         } else if (this.busy) {
            this.renderBusyLine(t, y);
         }
      }
   }

   private void renderBusyLine(Md3Theme t, float y) {
      Md3Progress.circular(this.canvas, this.cardX + 32.0F, y + 8.0F, 16.0F, 3.0F, t.colors.primary, t.colors.surfaceContainerHighest, 0.0F, true, this.time);
      this.canvas
         .text(
            ClientUi.fit(this.canvas, this.status, Md3Type.BODY_MEDIUM, this.cardW - 64.0F),
            this.cardX + 48.0F,
            y,
            Md3Type.BODY_MEDIUM,
            t.colors.onSurfaceVariant
         );
   }

   @Override
   protected void method_1026(int mouseX, int mouseY, int button) {
      if (button == 0) {
         this.mouseDown = true;
      }

      if (button == 0) {
         if (!ClientUi.hit(mouseX, mouseY, this.closeButton.x, this.closeButton.y, this.closeButton.w, this.closeButton.h)) {
            for (int i = 0; i < this.fields.size(); i++) {
               Md3TextField field = this.fields.get(i).widget;
               if (ClientUi.hit(mouseX, mouseY, field.x, field.y, field.w, field.h)) {
                  this.focus = i;
                  return;
               }
            }

            float by = this.cardY + this.cardH - 20.0F - 40.0F;
            float bw = 100.0F;
            float okX = this.cardX + this.cardW - 24.0F - bw;
            float cancelX = okX - 8.0F - bw;
            if (ClientUi.hit(mouseX, mouseY, okX, by, bw, 40.0F)) {
               if (!this.busy) {
                  ClientUi.click();
                  this.submit();
               }
            } else {
               if (ClientUi.hit(mouseX, mouseY, cancelX, by, bw, 40.0F)) {
                  ClientUi.click();
                  this.back();
               }
            }
         }
      }
   }

   @Override
   protected void method_1032(int mouseX, int mouseY, int button) {
      this.mouseDown = false;
   }

   @Override
   protected void method_1024(char id, int code) {
      if (code == 1) {
         this.back();
      } else if (code != 15) {
         if (code != 28 && code != 156) {
            if (!this.fields.isEmpty() && this.focus < this.fields.size()) {
               Md3TextField field = this.fields.get(this.focus).widget;
               if (!ImeBridge.handleKey(id, code)) {
                  if (code == 14) {
                     field.backspace();
                  } else if (id >= ' ' && id != 127) {
                     field.typeChar(id);
                  }
               }
            }
         } else {
            this.submit();
         }
      } else {
         if (!this.fields.isEmpty()) {
            boolean shift = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
            this.focus = (this.focus + (shift ? this.fields.size() - 1 : 1)) % this.fields.size();
         }
      }
   }

   private void back() {
      if (this.parent != null) {
         this.field_1229.method_2928(this.parent);
      } else {
         this.field_1229.method_2928(new AccountScreen(null));
      }
   }

   private void submit() {
      if (!this.busy) {
         try {
            if ("offline".equals(this.type)) {
               String name = this.value(0);
               if (name.isEmpty()) {
                  this.status = Lang.tr("acc.err.name");
                  return;
               }

               this.finish(Auth.offline(name));
            } else if ("token".equals(this.type)) {
               final String token = this.value(0);
               if (token.isEmpty()) {
                  this.status = Lang.tr("acc.err.token");
                  return;
               }

               this.busy = true;
               this.status = Lang.tr("acc.waiting");
               Thread thread = new Thread(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        final Accounts.Account account = Auth.fromToken(token);
                        class_1600.method_2965().method_6637(new Callable<Void>() {
                           public Void call() {
                              AccountLoginScreen.this.finish(account);
                              return null;
                           }
                        });
                     } catch (Exception var3) {
                        final String message = var3.getMessage() == null ? var3.getClass().getSimpleName() : var3.getMessage();
                        class_1600.method_2965().method_6637(new Callable<Void>() {
                           public Void call() {
                              AccountLoginScreen.this.busy = false;
                              AccountLoginScreen.this.status = Lang.tr("acc.err") + ": " + message;
                              return null;
                           }
                        });
                     }
                  }
               }, "Client-TokenLogin");
               thread.setDaemon(true);
               thread.start();
            } else if ("skin".equals(this.type)) {
               final String site = this.value(0);
               final String email = this.value(1);
               final String password = this.value(2);
               if (site.isEmpty() || email.isEmpty() || password.isEmpty()) {
                  this.status = Lang.tr("acc.err.empty");
                  return;
               }

               this.busy = true;
               this.status = Lang.tr("acc.waiting");
               Thread thread = new Thread(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        final Accounts.Account account = Auth.skinSite(site, email, password);
                        class_1600.method_2965().method_6637(new Callable<Void>() {
                           public Void call() {
                              AccountLoginScreen.this.finish(account);
                              return null;
                           }
                        });
                     } catch (Exception var3) {
                        final String message = var3.getMessage() == null ? var3.getClass().getSimpleName() : var3.getMessage();
                        class_1600.method_2965().method_6637(new Callable<Void>() {
                           public Void call() {
                              AccountLoginScreen.this.busy = false;
                              AccountLoginScreen.this.status = Lang.tr("acc.err") + ": " + message;
                              return null;
                           }
                        });
                     }
                  }
               }, "Client-SkinLogin");
               thread.setDaemon(true);
               thread.start();
            } else if ("microsoft".equals(this.type)) {
               if (this.deviceAccount != null) {
                  this.finish(this.deviceAccount);
               } else if (this.deviceError != null) {
                  this.status = this.deviceError;
               } else {
                  this.status = Lang.tr("acc.ms.waiting");
               }
            }
         } catch (Exception var5) {
            this.busy = false;
            this.status = Lang.tr("acc.err") + ": " + var5.getMessage();
         }
      }
   }

   private void finish(Accounts.Account account) {
      this.busy = false;
      if ("microsoft".equals(this.type)) {
         String typed = this.typedClientId();
         if (typed != null && !typed.trim().isEmpty()) {
            ClientConfig.msClientId = typed.trim();
            ClientConfig.saveSoon();
         }
      }

      Accounts.add(account);
      this.back();
   }

   private String value(int index) {
      return index < this.fields.size() ? this.fields.get(index).widget.text.trim() : "";
   }

   @Override
   public boolean method_1037() {
      return false;
   }

   private static final class Field {
      final Md3TextField widget;

      Field(Md3TextField widget) {
         this.widget = widget;
      }
   }
}
