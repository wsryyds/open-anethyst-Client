package com.mcpacks.gui;

import com.mcpacks.core.PackManager;
import com.mcpacks.util.FolderUtils;
import com.mcpacks.util.Lang;
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

public class GuiCreateFolderDialog extends class_388 {
   private final class_388 parentScreen;
   private final Md3Canvas canvas = new Md3Canvas();
   private final Md3TextField nameField = new Md3TextField();
   private String errorMessage = "";
   private float cardX;
   private float cardY;
   private float cardW;
   private float cardH;
   private boolean mouseDown;
   private long lastNanos;
   private float dt;

   public GuiCreateFolderDialog(class_388 parentScreen) {
      this.parentScreen = parentScreen;
   }

   @Override
   public void method_1044() {
      Keyboard.enableRepeatEvents(true);
      this.field_1232.clear();
      this.cardW = Math.min(400.0F, this.field_1230 - 24.0F);
      this.cardH = 236.0F;
      this.cardX = (this.field_1230 - this.cardW) / 2.0F;
      this.cardY = (this.field_1231 - this.cardH) / 2.0F;
      this.nameField.variant = Md3TextField.Variant.FILLED;
      this.nameField.label = Lang.s("Folder name", "文件夹名");
      this.nameField.supporting = "";
      this.nameField.maxLength = 32;
      this.nameField.focused = true;
      this.nameField.setBounds(this.cardX + 24.0F, this.cardY + 82.0F, this.cardW - 48.0F, 52.0F);
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
      this.canvas.text(Lang.s("Create New Resource Pack Folder", "新建资源包文件夹"), this.cardX + 24.0F, this.cardY + 20.0F, Md3Type.TITLE_MEDIUM, t.colors.onSurface);
      PackManager pm = PackManager.getInstance();
      int currentDepth = pm.getCurrentDepth();
      String locInfo = Lang.isChinese()
         ? "创建位置：" + (pm.isAtRoot() ? "根目录" : pm.getCurrentDir().getName()) + " (第 " + (currentDepth + 1) + "/3 层)"
         : "Creating inside: " + (pm.isAtRoot() ? "Root" : pm.getCurrentDir().getName()) + " (Layer " + (currentDepth + 1) + " of 3)";
      this.canvas
         .text(
            ClientUi.fit(this.canvas, locInfo, Md3Type.BODY_MEDIUM, this.cardW - 48.0F),
            this.cardX + 24.0F,
            this.cardY + 46.0F,
            Md3Type.BODY_MEDIUM,
            t.colors.onSurfaceVariant
         );
      this.nameField.surface = t.colors.surfaceContainerHigh;
      this.nameField.render(this.canvas, t, mouseX, mouseY, this.mouseDown, this.dt);
      ImeBridge.onFieldRender(
         (int)this.nameField.x + 16, (int)this.nameField.y + 28, (int)this.nameField.w - 32, this.nameField.text, this.nameField.text.length(), true
      );
      if (!this.errorMessage.isEmpty()) {
         this.canvas.icon("warning", this.cardX + 24.0F, this.cardY + 146.0F, 16.0F, t.colors.error);
         this.canvas
            .text(
               ClientUi.fit(this.canvas, this.errorMessage, Md3Type.BODY_MEDIUM, this.cardW - 68.0F),
               this.cardX + 46.0F,
               this.cardY + 145.0F,
               Md3Type.BODY_MEDIUM,
               t.colors.error
            );
      }

      float bw = 92.0F;
      float bh = 40.0F;
      float by = this.cardY + this.cardH - 20.0F - bh;
      float createX = this.cardX + this.cardW - 24.0F - bw;
      float cancelX = createX - 8.0F - bw;
      boolean createHot = ClientUi.hit(mouseX, mouseY, createX, by, bw, bh);
      this.canvas
         .roundRect(
            createX, by, bw, bh, bh / 2.0F, createHot ? Md3Colors.composite(Md3Colors.alpha(t.colors.onPrimary, 0.12F), t.colors.primary) : t.colors.primary
         );
      Md3Text.centered(this.canvas, Lang.s("Create", "创建"), createX + bw / 2.0F, by + bh / 2.0F, Md3Type.LABEL_LARGE, t.colors.onPrimary);
      boolean cancelHot = ClientUi.hit(mouseX, mouseY, cancelX, by, bw, bh);
      this.canvas.roundRect(cancelX, by, bw, bh, bh / 2.0F, cancelHot ? t.colors.surfaceContainerHighest : t.colors.surfaceContainerLow);
      Md3Text.centered(this.canvas, Lang.s("Cancel", "取消"), cancelX + bw / 2.0F, by + bh / 2.0F, Md3Type.LABEL_LARGE, t.colors.onSurface);
      this.canvas.end();
   }

   @Override
   protected void method_1026(int mouseX, int mouseY, int button) {
      if (button == 0) {
         this.mouseDown = true;
      }

      if (button == 0) {
         float bw = 92.0F;
         float bh = 40.0F;
         float by = this.cardY + this.cardH - 20.0F - bh;
         float createX = this.cardX + this.cardW - 24.0F - bw;
         float cancelX = createX - 8.0F - bw;
         if (ClientUi.hit(mouseX, mouseY, createX, by, bw, bh)) {
            this.handleCreate();
         } else {
            if (ClientUi.hit(mouseX, mouseY, cancelX, by, bw, bh)) {
               this.field_1229.method_2928(this.parentScreen);
            }
         }
      }
   }

   @Override
   protected void method_1032(int mouseX, int mouseY, int button) {
      this.mouseDown = false;
   }

   private void handleCreate() {
      String text = this.nameField.text.trim();
      if (text.isEmpty()) {
         this.errorMessage = Lang.s("Folder name cannot be empty!", "文件夹名不能为空！");
      } else {
         PackManager pm = PackManager.getInstance();
         if (!FolderUtils.canCreateSubfolder(pm.getRootDir(), pm.getCurrentDir())) {
            this.errorMessage = Lang.s("Maximum folder depth (3 layers) reached!", "已达到最大嵌套层数 (3 层)！");
         } else {
            if (pm.createNewFolder(text)) {
               this.field_1229.method_2928(this.parentScreen);
            } else {
               this.errorMessage = Lang.s("Failed to create folder (invalid name or already exists).", "创建失败：名称非法或已存在。");
            }
         }
      }
   }

   @Override
   protected void method_1024(char id, int code) {
      if (code == 28 || code == 156) {
         this.handleCreate();
      } else if (code == 1) {
         this.field_1229.method_2928(this.parentScreen);
      } else if (!ImeBridge.handleKey(id, code)) {
         if (code == 14) {
            this.nameField.backspace();
         } else if (id >= ' ' && id != 127) {
            this.nameField.typeChar(id);
         }
      }
   }

   @Override
   public void method_1030() {
      Keyboard.enableRepeatEvents(false);
      super.method_1030();
   }

   @Override
   public boolean method_1037() {
      return false;
   }
}
