package net.client.mixin;

import java.util.List;
import net.client.AccountScreen;
import net.client.ClientConfig;
import net.client.ClientScreen;
import net.client.Lang;
import net.client.replay.ReplayClient;
import net.client.ui.ClientTheme;
import net.client.ui.ClientUi;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3Button;
import net.client.util.Accounts;
import net.minecraft.class_1600;
import net.minecraft.class_356;
import net.minecraft.class_388;
import net.minecraft.class_624;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = class_624.class, priority = 1200)
public class TitleScreenMixin {
   private static final float ACCOUNT_W = 118.0F;
   private static final float ACCOUNT_H = 22.0F;
   private static final float SETTINGS_W = 200.0F;
   private static final float SETTINGS_H = 20.0F;
   private final Md3Canvas lc$canvas = new Md3Canvas();
   private final Md3Button lc$account = new Md3Button(Md3Button.Variant.TONAL, "");
   private final Md3Button lc$settings = new Md3Button(Md3Button.Variant.FILLED, "");
   private final Md3Button lc$replay = new Md3Button(Md3Button.Variant.TONAL, "");
   private float lc$settingsX;
   private float lc$settingsY;
   private float lc$replayY;

   private class_388 self() {
      return (class_388)(Object)this;
   }

   @Inject(method = "method_1044", at = @At("TAIL"))
   private void client$replaceRealms(CallbackInfo ci) {
      Accounts.bootstrap();
      class_624 self = (class_624)(Object)this;
      class_356 realms = ((TitleScreenAccessor)self).client$realmsButton();
      this.lc$settingsX = realms != null ? realms.field_1051 : self.field_1230 / 2.0F - 100.0F;
      this.lc$settingsY = realms != null ? realms.field_1052 : self.field_1231 / 4.0F + 48.0F + 48.0F;
      this.lc$replayY = 34.0F;
      if (realms != null) {
         realms.field_1056 = false;
      }

      List<class_356> buttons = ((ScreenAccessor)self).client$buttons();
      if (!ClientConfig.uiOverride && buttons != null) {
         buttons.add(new class_356(9001, (int)this.lc$settingsX, (int)this.lc$settingsY, 200, 20, Lang.tr("menu.open")));
      }

      this.lc$account.variant = Md3Button.Variant.TONAL;
      this.lc$account.icon = "account_circle";
      this.lc$settings.variant = Md3Button.Variant.TONAL;
      this.lc$settings.icon = "tune";
      this.lc$settings.label = Lang.tr("menu.open");
      this.lc$replay.variant = Md3Button.Variant.TONAL;
      this.lc$replay.icon = "animation";
      this.lc$replay.label = Lang.tr("menu.replay");
      this.lc$replay.labelType = Md3Type.LABEL_SMALL;
      this.lc$account.labelType = Md3Type.LABEL_SMALL;
      this.lc$settings.labelType = Md3Type.LABEL_SMALL;
   }

   @Inject(method = "method_9593", at = @At("HEAD"), cancellable = true)
   private void client$noRealms(CallbackInfoReturnable<Boolean> cir) {
      cir.setReturnValue(false);
   }

   @Inject(method = "method_1025", at = @At("TAIL"))
   private void client$renderButtons(int mouseX, int mouseY, float delta, CallbackInfo ci) {
      class_1600 client = class_1600.method_2965();
      if (client != null) {
         class_388 screen = this.self();
         String name = client.method_5568() == null ? "" : client.method_5568().method_5583();
         String accountLabel = Lang.tr("menu.account") + "  " + name;
         if (this.lc$canvas.textWidth(accountLabel, Md3Type.LABEL_SMALL) + 48.0F > 118.0F) {
            accountLabel = Lang.tr("menu.account");
         }

         Md3Theme t = ClientTheme.get();
         this.lc$canvas.begin(screen.field_1230, screen.field_1231);
         float ax = 10.0F;
         float ay = 8.0F;
         this.lc$account.label = accountLabel;
         this.lc$account.surface = t.colors.surfaceContainerLow;
         this.lc$account.setBounds(ax, ay, 118.0F, 22.0F);
         this.lc$account.render(this.lc$canvas, t, mouseX, mouseY, Mouse.isButtonDown(0), delta);
         if (ClientConfig.uiOverride) {
            float sx = this.lc$settingsX;
            float sy = this.lc$settingsY;
            this.lc$settings.setBounds(sx, sy, 200.0F, 20.0F);
            this.lc$settings.render(this.lc$canvas, t, mouseX, mouseY, Mouse.isButtonDown(0), delta);
         }

         if (ReplayClient.isReady()) {
            this.lc$replay.setBounds(10.0F, this.lc$replayY, 118.0F, 22.0F);
            this.lc$replay.render(this.lc$canvas, t, mouseX, mouseY, Mouse.isButtonDown(0), delta);
         }

         this.lc$canvas.end();
      }
   }

   @Inject(method = "method_1026", at = @At("HEAD"), cancellable = true)
   private void client$clickButtons(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
      class_1600 client = class_1600.method_2965();
      if (client != null && mouseButton == 0) {
         class_388 screen = this.self();
         float sx = this.lc$settingsX;
         float sy = this.lc$settingsY;
         if (ClientUi.hit(mouseX, mouseY, 10.0F, 8.0F, 118.0F, 22.0F)) {
            ClientUi.click();
            client.method_2928(new AccountScreen(screen));
            ci.cancel();
         } else if (ClientUi.hit(mouseX, mouseY, sx, sy, 200.0F, 20.0F)) {
            ClientUi.click();
            client.method_2928(new ClientScreen());
            ci.cancel();
         } else if (ReplayClient.isReady() && ClientUi.hit(mouseX, mouseY, 10.0F, this.lc$replayY, 118.0F, 22.0F)) {
            ClientUi.click();
            ReplayClient.openConsole();
            ci.cancel();
         }
      }
   }
}
