package net.client;

import net.client.ime.ImeBridge;
import net.client.ui.ClientTheme;
import net.client.ui.ClientUi;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3Surfaces;
import net.client.ui.md3.widget.Md3Text;
import net.client.ui.md3.widget.Md3TextField;
import net.minecraft.class_388;
import org.lwjgl.input.Keyboard;

public class ScoreboardSettingsScreen extends class_388 {
   private final class_388 parent;
   private final Md3Canvas canvas = new Md3Canvas();
   private final Md3TextField titleField = new Md3TextField();
   private final Md3TextField contentField = new Md3TextField();
   private float cardX;
   private float cardY;
   private float cardW;
   private float cardH;
   private boolean compact;
   private float titleY;
   private float contentY;
   private boolean mouseDown;
   private long lastNanos;
   private float dt;

   public ScoreboardSettingsScreen(class_388 parent) {
      this.parent = parent;
   }

   @Override
   public void method_1044() {
      Keyboard.enableRepeatEvents(true);
      this.field_1232.clear();
      this.compact = this.field_1231 < 400.0F;
      this.cardW = Math.min(600.0F, this.field_1230 - 24.0F);
      this.cardH = Math.min(this.compact ? 210.0F : 420.0F, this.field_1231 - 16.0F);
      this.cardX = (this.field_1230 - this.cardW) / 2.0F;
      this.cardY = (this.field_1231 - this.cardH) / 2.0F;
      this.titleY = this.cardY + (this.compact ? 46.0F : 70.0F);
      float titleH = this.compact ? 50.0F : 56.0F;
      this.contentY = this.titleY + titleH + (this.compact ? 2.0F : 16.0F);
      float contentH = this.compact ? 50.0F : 64.0F;
      this.titleField.variant = Md3TextField.Variant.FILLED;
      this.titleField.label = ClientConfig.t("计分板标题", "Scoreboard title");
      this.titleField.supporting = this.compact ? "" : ClientConfig.t("留空 = 沿用服务器标题", "Empty = use the server title");
      this.titleField.maxLength = 48;
      this.titleField.surface = ClientTheme.get().colors.surfaceContainerHigh;
      this.titleField.text = ClientConfig.scoreboardTitle == null ? "" : ClientConfig.scoreboardTitle;
      this.titleField.setBounds(this.cardX + 24.0F, this.titleY, this.cardW - 48.0F, titleH);
      this.contentField.variant = Md3TextField.Variant.FILLED;
      this.contentField.label = ClientConfig.t("计分板内容", "Scoreboard content");
      this.contentField.supporting = this.compact
         ? ClientConfig.t("多行用 | 分隔，名字:分数", "Lines: | ; name:score")
         : ClientConfig.t("多行用 | 分隔，名字和分数用 : 分隔", "Separate lines with |; name and score with :");
      this.contentField.maxLength = 960;
      this.contentField.surface = ClientTheme.get().colors.surfaceContainerHigh;
      this.contentField.text = ClientConfig.scoreboardText == null ? "" : ClientConfig.scoreboardText.replace('\n', '|');
      this.contentField.setBounds(this.cardX + 24.0F, this.contentY, this.cardW - 48.0F, contentH);
   }

   @Override
   public void method_1025(int mouseX, int mouseY, float tickDelta) {
      long now = System.nanoTime();
      this.dt = this.lastNanos == 0L ? 0.016F : Math.min(0.05F, (float)(now - this.lastNanos) / 1.0E9F);
      this.lastNanos = now;
      super.method_1025(mouseX, mouseY, tickDelta);
      Md3Theme t = ClientTheme.get();
      this.canvas.begin(this.field_1230, this.field_1231);
      ClientUi.backdrop(this.canvas, t, this.field_1230, this.field_1231, this.field_1229 != null && this.field_1229.field_3803 != null);
      this.canvas.fill(0.0F, 0.0F, this.field_1230, this.field_1231, Md3Colors.alpha(t.colors.scrim, 0.58F));
      Md3Surfaces.dialog(this.canvas, t, this.cardX, this.cardY, this.cardW, this.cardH);
      this.canvas
         .text(
            ClientConfig.t("自定义计分板内容", "Scoreboard content"),
            this.cardX + 24.0F,
            this.cardY + (this.compact ? 14.0F : 22.0F),
            Md3Type.TITLE_MEDIUM,
            t.colors.onSurface
         );
      if (!this.compact) {
         this.canvas
            .text(
               ClientConfig.t("会覆盖服务器侧边栏；单人模式同样生效。", "Overrides the server sidebar, including in single player."),
               this.cardX + 24.0F,
               this.cardY + 48.0F,
               Md3Type.BODY_SMALL,
               t.colors.onSurfaceVariant
            );
      }

      this.titleField.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      this.contentField.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      if (this.titleField.focused) {
         ImeBridge.onFieldRender(
            (int)this.titleField.x + 16, (int)this.titleField.y + 28, (int)this.titleField.w - 32, this.titleField.text, this.titleField.text.length(), true
         );
      } else if (this.contentField.focused) {
         ImeBridge.onFieldRender(
            (int)this.contentField.x + 16,
            (int)this.contentField.y + 28,
            (int)this.contentField.w - 32,
            this.contentField.text,
            this.contentField.text.length(),
            true
         );
      }

      float bw = this.compact ? 88.0F : 104.0F;
      float bh = this.compact ? 28.0F : 40.0F;
      float by = this.buttonY();
      float saveX = this.cardX + this.cardW - 24.0F - bw;
      float cancelX = saveX - 8.0F - bw;
      boolean saveHot = ClientUi.hit(mouseX, mouseY, saveX, by, bw, bh);
      this.canvas
         .roundRect(
            saveX, by, bw, bh, bh / 2.0F, saveHot ? Md3Colors.composite(Md3Colors.alpha(t.colors.onPrimary, 0.12F), t.colors.primary) : t.colors.primary
         );
      Md3Text.centered(this.canvas, ClientConfig.t("保存", "Save"), saveX + bw / 2.0F, by + bh / 2.0F, Md3Type.LABEL_LARGE, t.colors.onPrimary);
      boolean cancelHot = ClientUi.hit(mouseX, mouseY, cancelX, by, bw, bh);
      this.canvas.roundRect(cancelX, by, bw, bh, bh / 2.0F, cancelHot ? t.colors.surfaceContainerHighest : t.colors.surfaceContainerLow);
      Md3Text.centered(this.canvas, ClientConfig.t("取消", "Cancel"), cancelX + bw / 2.0F, by + bh / 2.0F, Md3Type.LABEL_LARGE, t.colors.onSurface);
      this.canvas.end();
   }

   private float buttonY() {
      return this.compact ? this.cardY + 162.0F : this.cardY + this.cardH - 60.0F;
   }

   @Override
   protected void method_1026(int mouseX, int mouseY, int button) {
      if (button == 0) {
         this.mouseDown = true;
      }

      if (button == 0) {
         if (ClientUi.hit(mouseX, mouseY, this.titleField.x, this.titleField.y, this.titleField.w, this.titleField.h)) {
            this.titleField.focused = true;
            this.contentField.focused = false;
         } else if (ClientUi.hit(mouseX, mouseY, this.contentField.x, this.contentField.y, this.contentField.w, this.contentField.h)) {
            this.contentField.focused = true;
            this.titleField.focused = false;
         } else {
            float bw = this.compact ? 88.0F : 104.0F;
            float bh = this.compact ? 28.0F : 40.0F;
            float by = this.buttonY();
            float saveX = this.cardX + this.cardW - 24.0F - bw;
            float cancelX = saveX - 8.0F - bw;
            if (ClientUi.hit(mouseX, mouseY, saveX, by, bw, bh)) {
               this.saveAndClose();
            } else if (ClientUi.hit(mouseX, mouseY, cancelX, by, bw, bh)) {
               this.close();
            }
         }
      }
   }

   @Override
   protected void method_1032(int mouseX, int mouseY, int button) {
      this.mouseDown = false;
   }

   @Override
   protected void method_1024(char ch, int code) {
      if (code == 28 || code == 156) {
         this.saveAndClose();
      } else if (code == 1) {
         this.close();
      } else if (!ImeBridge.handleKey(ch, code)) {
         if (code == 14) {
            if (this.contentField.focused) {
               this.contentField.backspace();
            } else {
               this.titleField.backspace();
            }
         } else if (ch >= ' ' && ch != 127) {
            if (this.contentField.focused) {
               this.contentField.typeChar(ch);
            } else {
               this.titleField.typeChar(ch);
            }
         }
      }
   }

   private void saveAndClose() {
      ClientConfig.scoreboardCustom = true;
      ClientConfig.scoreboardShowTitle = true;
      ClientConfig.scoreboardTitle = this.titleField.text == null ? "" : this.titleField.text.trim();
      String raw = this.contentField.text == null ? "" : this.contentField.text.trim();
      StringBuilder custom = new StringBuilder();
      int lines = 0;

      for (String part : raw.split("\\|")) {
         String line = part == null ? "" : part.trim();
         if (!line.isEmpty()) {
            if (line.length() > 64) {
               line = line.substring(0, 64);
            }

            if (lines > 0) {
               custom.append('\n');
            }

            custom.append(line);
            if (++lines >= 15) {
               break;
            }
         }
      }

      ClientConfig.scoreboardText = custom.toString();
      ClientConfig.validate();
      ClientConfig.save();
      this.close();
   }

   private void close() {
      Keyboard.enableRepeatEvents(false);
      if (this.field_1229 != null) {
         this.field_1229.method_2928(this.parent);
      }
   }
}
