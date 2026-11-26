package net.client.replay.gui;

import java.io.File;
import java.util.Locale;
import net.client.replay.ReplayClient;
import net.client.replay.config.ReplayConfig;
import net.client.replay.render.PreviewJob;
import net.client.replay.render.RenderJob;
import net.client.replay.render.RenderManager;
import net.client.replay.session.CameraPath;
import net.client.replay.util.ReplayPaths;
import net.client.ui.ClientScroll;
import net.client.ui.ClientTheme;
import net.client.ui.ClientUi;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3Text;
import net.client.ui.md3.widget.Md3TextField;
import net.minecraft.class_1600;
import net.minecraft.class_388;
import org.lwjgl.input.Mouse;

public class ReplayScreen extends class_388 {
   private static final float ROW_H = 36.0F;
   private final Md3Canvas canvas = new Md3Canvas();
   private final ClientScroll listScroll = new ClientScroll();
   private final ClientScroll sideScroll = new ClientScroll();
   private File[] replays = new File[0];
   private int selected = -1;
   private String message = "";
   private long messageUntil;
   private File pendingDelete;
   private long pendingDeleteUntil;
   private long lastRowClickAt;
   private int lastRowClickIndex = -1;
   private long lastNanos;
   private float dt;
   private int openFrames;
   private float mouseX;
   private float mouseY;
   private boolean mouseDown;
   private boolean listBarDrag;
   private boolean sideBarDrag;
   private float panelX;
   private float panelY;
   private float panelW;
   private float panelH;
   private float headerH = 38.0F;
   private float listX;
   private float listY;
   private float listW;
   private float listH;
   private float rowsY;
   private float rowsH;
   private float sideX;
   private float sideY;
   private float sideW;
   private float sideH;
   private float sideContentH;
   private Md3TextField sampleField;
   private Md3TextField fpsField;
   private Md3TextField widthField;
   private Md3TextField heightField;
   private final Md3TextField[] fields = new Md3TextField[4];
   private int focusField = -1;
   private float rowSampleY;
   private float rowFpsY;
   private float rowWidthY;
   private float rowHeightY;
   private float rowEncoderY;
   private float rowAudioY;
   private float rowAutoY;
   private float actionY;
   private float btnPlayX;
   private float btnRenderX;
   private float btnDeleteX;
   private float actionW;

   public ReplayScreen() {
      this.replays = ReplayPaths.listReplays();
      this.selected = this.replays.length == 0 ? -1 : 0;

      for (int i = 0; i < this.fields.length; i++) {
         Md3TextField field = new Md3TextField();
         field.variant = Md3TextField.Variant.OUTLINED;
         field.label = "";
         field.supporting = "";
         field.maxLength = 12;
         this.fields[i] = field;
      }

      this.sampleField = this.fields[0];
      this.fpsField = this.fields[1];
      this.widthField = this.fields[2];
      this.heightField = this.fields[3];
      this.syncFieldsFromConfig();
   }

   private void syncFieldsFromConfig() {
      ReplayConfig config = ReplayClient.getConfig();
      if (config != null) {
         this.sampleField.text = Integer.toString(config.sampleHz);
         this.fpsField.text = trim(config.defaultFps);
         this.widthField.text = Integer.toString(config.defaultWidth);
         this.heightField.text = Integer.toString(config.defaultHeight);
      }
   }

   private static String trim(double value) {
      return Math.abs(value - Math.round(value)) < 1.0E-4 ? Long.toString(Math.round(value)) : String.format(Locale.ROOT, "%.2f", value);
   }

   @Override
   public void method_1044() {
      this.replays = ReplayPaths.listReplays();
      if (this.selected >= this.replays.length) {
         this.selected = this.replays.length - 1;
      }

      float margin = 6.0F;
      this.panelW = Math.min(980.0F, this.field_1230 - margin * 2.0F);
      this.panelH = Math.min(640.0F, this.field_1231 - margin * 2.0F);
      this.panelX = (this.field_1230 - this.panelW) / 2.0F;
      this.panelY = (this.field_1231 - this.panelH) / 2.0F;
      this.headerH = 34.0F;
      float inner = 8.0F;
      float contentY = this.panelY + this.headerH;
      float contentH = this.panelH - this.headerH - 26.0F;
      float gap = 8.0F;
      boolean two = this.panelW >= 430.0F;
      if (two) {
         this.listW = Math.round((this.panelW - inner * 3.0F - gap) * 0.54F);
      } else {
         this.listW = this.panelW - inner * 2.0F;
      }

      this.listX = this.panelX + inner;
      this.listY = contentY;
      this.sideX = this.listX;
      this.sideW = this.listW;
      if (two) {
         this.listH = contentH;
         this.sideW = this.panelW - inner * 3.0F - gap - this.listW;
         this.sideX = this.listX + this.listW + inner + gap;
         this.sideY = this.listY;
         this.sideH = this.listH;
      } else {
         this.listH = Math.max(76.0F, (float)Math.round(contentH * 0.46F));
         this.sideY = this.listY + this.listH + gap;
         this.sideH = Math.max(60.0F, contentH - this.listH - gap);
      }

      this.actionW = (this.listW - 16.0F) / 3.0F - 4.0F;
      this.actionY = this.listY + this.listH - 28.0F;
      this.btnPlayX = this.listX + 8.0F;
      this.btnRenderX = this.btnPlayX + this.actionW + 6.0F;
      this.btnDeleteX = this.btnRenderX + this.actionW + 6.0F;
      this.rowsY = this.listY + 24.0F;
      this.rowsH = Math.max(20.0F, this.actionY - 6.0F - this.rowsY);
      this.listScroll.measure(this.replays.length * 36.0F, this.rowsH);
      float row = Math.min(30.0F, Math.max(24.0F, this.sideH / 11.0F));
      this.rowSampleY = this.sideY + 20.0F;
      this.rowFpsY = this.rowSampleY + row;
      this.rowWidthY = this.rowFpsY + row;
      this.rowHeightY = this.rowWidthY + row;
      this.rowEncoderY = this.rowHeightY + row + 2.0F;
      this.rowAudioY = this.rowEncoderY + row;
      this.rowAutoY = this.rowAudioY + row;
      this.sideContentH = this.rowAutoY + row + 28.0F - this.sideY;
      this.sideScroll.measure(this.sideContentH, this.sideH - 6.0F);
      float fieldW = Math.min(86.0F, Math.max(48.0F, this.sideW * 0.4F));

      for (int i = 0; i < this.fields.length; i++) {
         this.fields[i].setBounds(0.0F, 0.0F, fieldW, 22.0F);
      }

      this.lastNanos = 0L;
      this.openFrames = 0;
      this.mouseDown = false;
      this.listBarDrag = false;
      this.sideBarDrag = false;
   }

   private float[] rowYs() {
      return new float[]{this.rowSampleY, this.rowFpsY, this.rowWidthY, this.rowHeightY};
   }

   @Override
   public void method_1025(int mx, int my, float tickDelta) {
      this.mouseX = mx;
      this.mouseY = my;
      long now = System.nanoTime();
      this.dt = this.lastNanos == 0L ? 0.05F : Math.min(0.05F, (float)(now - this.lastNanos) / 1.0E9F);
      this.lastNanos = now;
      this.openFrames++;
      this.listScroll.update(this.dt);
      this.sideScroll.update(this.dt);
      Md3Theme t = ClientTheme.get();
      this.canvas.begin(this.field_1230, this.field_1231);
      ClientUi.backdrop(this.canvas, t, this.field_1230, this.field_1231, false);
      this.drawPanel(t);
      this.drawHeader(t);
      this.drawList(t);
      this.drawSide(t);
      this.drawFooter(t);
      this.canvas.end();
   }

   private void drawPanel(Md3Theme t) {
      this.canvas.shadow(this.panelX, this.panelY, this.panelW, this.panelH, 20.0F, 2);
      this.canvas.roundRect(this.panelX, this.panelY, this.panelW, this.panelH, 20.0F, t.colors.surfaceContainerLow);
      this.canvas.strokeRoundRect(this.panelX, this.panelY, this.panelW, this.panelH, 20.0F, 1.0F, Md3Colors.alpha(t.colors.outlineVariant, 0.4F));
   }

   private void drawHeader(Md3Theme t) {
      float iconX = this.panelX + 10.0F;
      float iconY = this.panelY + 8.0F;
      this.canvas.roundRect(iconX, iconY, 20.0F, 20.0F, 7.0F, t.colors.primaryContainer);
      this.canvas.icon("animation", iconX + 3.0F, iconY + 3.0F, 14.0F, t.colors.onPrimaryContainer);
      Md3Text.leftMiddle(this.canvas, "回放与录制", iconX + 28.0F, this.panelY + this.headerH / 2.0F, Md3Type.TITLE_SMALL, t.colors.onSurface);
      String sub = this.replays.length + " 个回放 · " + ReplayPaths.formatSize(this.totalBytes());
      float subX = iconX + 28.0F + this.canvas.textWidth("回放与录制", Md3Type.TITLE_SMALL) + 10.0F;
      if (subX + 60.0F < this.panelX + this.panelW - 80.0F) {
         Md3Text.leftMiddle(
            this.canvas,
            ReplayUi.fit(this.canvas, sub, Md3Type.BODY_SMALL, this.panelX + this.panelW - 70.0F - subX),
            subX,
            this.panelY + this.headerH / 2.0F,
            Md3Type.BODY_SMALL,
            t.colors.onSurfaceVariant
         );
      }

      float closeS = 26.0F;
      float cx = this.panelX + this.panelW - 10.0F - closeS;
      float cy = this.panelY + (this.headerH - closeS) / 2.0F;
      ReplayUi.button(
         this.canvas,
         t,
         cx,
         cy,
         closeS,
         closeS,
         "",
         "close",
         ReplayUi.Style.ICON,
         true,
         ReplayUi.hit(this.mouseX, this.mouseY, cx, cy, closeS, closeS),
         this.mouseDown
      );
      float rx = cx - 4.0F - closeS;
      ReplayUi.button(
         this.canvas,
         t,
         rx,
         cy,
         closeS,
         closeS,
         "",
         "refresh",
         ReplayUi.Style.ICON,
         true,
         ReplayUi.hit(this.mouseX, this.mouseY, rx, cy, closeS, closeS),
         this.mouseDown
      );
   }

   private long totalBytes() {
      long total = 0L;

      for (File file : this.replays) {
         total += file.length();
      }

      return total;
   }

   private void drawList(Md3Theme t) {
      ReplayUi.card(this.canvas, t, this.listX, this.listY, this.listW, this.listH);
      Md3Text.leftMiddle(this.canvas, "回放文件", this.listX + 10.0F, this.listY + 12.0F, Md3Type.TITLE_SMALL, t.colors.onSurface);
      Md3Text.rightMiddle(this.canvas, "双击行可直接剪辑", this.listX + this.listW - 26.0F, this.listY + 12.0F, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
      float startY = this.rowsY;
      float endY = this.rowsY + this.rowsH;
      this.canvas.pushClip(this.listX + 4.0F, startY, this.listW - 12.0F, this.rowsH);
      if (this.replays.length == 0) {
         float emptyY = startY + Math.max(10.0F, this.rowsH * 0.5F);
         Md3Text.leftMiddle(
            this.canvas, "还没有回放，按 " + ReplayClient.recordKeyName() + " 开始录制", this.listX + 10.0F, emptyY, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant
         );
      } else {
         float y = startY - this.listScroll.value;

         for (int i = 0; i < this.replays.length; i++) {
            File file = this.replays[i];
            boolean sel = i == this.selected;
            boolean hover = ReplayUi.hit(this.mouseX, this.mouseY, this.listX + 4.0F, y + 1.0F, this.listW - 16.0F, 34.0F);
            if (sel) {
               this.canvas.roundRect(this.listX + 4.0F, y + 1.0F, this.listW - 16.0F, 34.0F, 8.0F, t.colors.primaryContainer);
            } else if (hover) {
               this.canvas.roundRect(this.listX + 4.0F, y + 1.0F, this.listW - 16.0F, 34.0F, 8.0F, t.colors.surfaceContainerHighest);
            }

            int titleColor = sel ? t.colors.onPrimaryContainer : t.colors.onSurface;
            int metaColor = sel ? t.colors.onPrimaryContainer : t.colors.onSurfaceVariant;
            String name = file.getName().substring(0, Math.max(0, file.getName().length() - 4));
            Md3Text.leftMiddle(
               this.canvas,
               ReplayUi.fit(this.canvas, name, Md3Type.BODY_MEDIUM, this.listW - 52.0F),
               this.listX + 10.0F,
               y + 11.0F,
               Md3Type.BODY_MEDIUM,
               titleColor
            );
            Md3Text.leftMiddle(
               this.canvas,
               ReplayPaths.formatTime(file.lastModified()) + " · " + ReplayPaths.formatSize(file.length()),
               this.listX + 10.0F,
               y + 25.0F,
               Md3Type.BODY_SMALL,
               Md3Colors.alpha(metaColor, 0.8F)
            );
            y += 36.0F;
         }
      }

      this.canvas.popClip();
      this.listScroll.renderBar(this.canvas, t, this.listX + this.listW - 9.0F, startY, 4.0F);
      String playLabel = "播放 / 剪辑";
      ReplayUi.button(
         this.canvas,
         t,
         this.btnPlayX,
         this.actionY,
         this.actionW,
         22.0F,
         playLabel,
         "play_arrow",
         ReplayUi.Style.FILLED,
         this.selected >= 0,
         ReplayUi.hit(this.mouseX, this.mouseY, this.btnPlayX, this.actionY, this.actionW, 22.0F),
         this.mouseDown
      );
      boolean renderBusy = ReplayClient.getRenderManager() != null && ReplayClient.getRenderManager().isActive();
      ReplayUi.button(
         this.canvas,
         t,
         this.btnRenderX,
         this.actionY,
         this.actionW,
         22.0F,
         "渲染",
         "download",
         ReplayUi.Style.TONAL,
         this.selected >= 0 && !renderBusy,
         ReplayUi.hit(this.mouseX, this.mouseY, this.btnRenderX, this.actionY, this.actionW, 22.0F),
         this.mouseDown
      );
      boolean armed = this.pendingDelete != null
         && System.currentTimeMillis() < this.pendingDeleteUntil
         && this.selected >= 0
         && this.replays[this.selected].equals(this.pendingDelete);
      ReplayUi.button(
         this.canvas,
         t,
         this.btnDeleteX,
         this.actionY,
         this.actionW,
         22.0F,
         armed ? "确认删除" : "删除",
         "delete",
         ReplayUi.Style.DANGER,
         this.selected >= 0,
         ReplayUi.hit(this.mouseX, this.mouseY, this.btnDeleteX, this.actionY, this.actionW, 22.0F),
         this.mouseDown
      );
   }

   private void drawSide(Md3Theme t) {
      ReplayUi.card(this.canvas, t, this.sideX, this.sideY, this.sideW, this.sideH);
      this.canvas.pushClip(this.sideX + 4.0F, this.sideY + 4.0F, this.sideW - 8.0F, this.sideH - 8.0F);
      float off = -this.sideScroll.value;
      float x = this.sideX + 10.0F;
      float w = this.sideW - 24.0F;
      Md3Text.leftMiddle(this.canvas, "录制与导出", x, this.sideY + 12.0F + off, Md3Type.TITLE_SMALL, t.colors.onSurface);
      float[] ys = this.rowYs();
      String[] labels = new String[]{"采样率 Hz", "帧率", "宽度", "高度"};

      for (int i = 0; i < 4; i++) {
         float y = ys[i] + off;
         Md3Text.leftMiddle(this.canvas, labels[i], x, y + 11.0F, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
         float fw = Math.min(86.0F, Math.max(48.0F, this.sideW * 0.4F));
         Md3TextField field = this.fields[i];
         field.x = x + w - fw;
         field.y = y;
         field.w = fw;
         field.h = 22.0F;
         field.focused = this.focusField == i;
         field.render(this.canvas, t, this.mouseX, this.mouseY, false, this.dt);
      }

      float encY = this.rowEncoderY + off;
      Md3Text.leftMiddle(this.canvas, "编码器", x, encY + 11.0F, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
      String enc = ReplayClient.getConfig() == null ? "ffmpeg" : ReplayClient.getConfig().encoder;
      float chipW = Math.min(92.0F, Math.max(64.0F, this.sideW * 0.42F));
      ReplayUi.button(
         this.canvas,
         t,
         x + w - chipW,
         encY,
         chipW,
         22.0F,
         enc.toUpperCase(Locale.ROOT),
         "animation",
         ReplayUi.Style.OUTLINED,
         true,
         ReplayUi.hit(this.mouseX, this.mouseY, x + w - chipW, encY, chipW, 22.0F),
         this.mouseDown
      );
      float audioY = this.rowAudioY + off;
      ReplayUi.switchRow(this.canvas, t, x, audioY, w, 24.0F, "录制游戏内声音", ReplayClient.getConfig() != null && ReplayClient.getConfig().recordGameAudio, true);
      float autoY = this.rowAutoY + off;
      ReplayUi.switchRow(this.canvas, t, x, autoY, w, 24.0F, "进世界自动录制", ReplayClient.getConfig() != null && ReplayClient.getConfig().autoRecord, true);
      this.canvas.popClip();
      this.sideScroll.renderBar(this.canvas, t, this.sideX + this.sideW - 9.0F, this.sideY + 4.0F, 4.0F);
   }

   private void drawFooter(Md3Theme t) {
      float y = this.panelY + this.panelH - 20.0F;
      String status = this.statusText();
      Md3Text.leftMiddle(
         this.canvas,
         ReplayUi.fit(this.canvas, status, Md3Type.BODY_SMALL, this.panelW - 24.0F),
         this.panelX + 12.0F,
         y,
         Md3Type.BODY_SMALL,
         this.message.isEmpty() ? t.colors.onSurfaceVariant : t.colors.primary
      );
      RenderJob job = ReplayClient.getRenderManager() == null ? null : ReplayClient.getRenderManager().getJob();
      if (job != null) {
         float barW = Math.min(160.0F, this.panelW * 0.4F);
         float bx = this.panelX + this.panelW - 12.0F - barW;
         this.canvas.roundRect(bx, y - 2.0F, barW, 4.0F, 2.0F, t.colors.surfaceContainerHighest);
         this.canvas.roundRect(bx, y - 2.0F, barW * Math.max(0.0F, Math.min(1.0F, job.getPercent() / 100.0F)), 4.0F, 2.0F, t.colors.primary);
      }
   }

   private String statusText() {
      long now = System.currentTimeMillis();
      if (!this.message.isEmpty() && now < this.messageUntil) {
         return this.message;
      } else if (ReplayClient.getRecordingManager() != null && ReplayClient.getRecordingManager().isActive()) {
         return "录制中：" + ReplayClient.getRecordingManager().statusLine();
      } else {
         RenderManager manager = ReplayClient.getRenderManager();
         if (manager != null && manager.isActive()) {
            RenderJob job = manager.getJob();
            return job != null ? "导出 " + job.getPercent() + "% · " + job.getOutputDescription() : "回放预览进行中";
         } else {
            return this.replays.length != 0 && this.selected >= 0
               ? "选中 " + this.replays[this.selected].getName() + " · 回放目录 " + ReplayPaths.replaysDir().getAbsolutePath()
               : "回放目录：" + ReplayPaths.replaysDir().getAbsolutePath();
         }
      }
   }

   private void say(String text) {
      this.message = text;
      this.messageUntil = System.currentTimeMillis() + 2500L;
   }

   @Override
   public void method_1035() {
      int wheel = Mouse.getEventDWheel();
      if (wheel != 0) {
         float delta = -Integer.signum(wheel) * 40.0F;
         if (ReplayUi.hit(this.mouseX, this.mouseY, this.listX, this.rowsY, this.listW, this.rowsH)) {
            this.listScroll.scrollBy(delta);
         } else if (ReplayUi.hit(this.mouseX, this.mouseY, this.sideX, this.sideY, this.sideW, this.sideH)) {
            this.sideScroll.scrollBy(delta);
         }
      }

      super.method_1035();
   }

   @Override
   protected void method_1026(int mx, int my, int button) {
      if (this.openFrames >= 3 && button == 0) {
         this.mouseX = mx;
         this.mouseY = my;
         this.mouseDown = true;
         float closeS = 26.0F;
         float cx = this.panelX + this.panelW - 10.0F - closeS;
         float cy = this.panelY + (this.headerH - closeS) / 2.0F;
         if (ReplayUi.hit(mx, my, cx, cy, closeS, closeS)) {
            this.close();
         } else {
            float rx = cx - 4.0F - closeS;
            if (ReplayUi.hit(mx, my, rx, cy, closeS, closeS)) {
               this.refresh();
               this.say("已刷新回放列表。");
               ClientUi.click();
            } else if (this.listScroll.press(mx, my, this.listX + this.listW - 9.0F, this.rowsY, 4.0F, 8.0F)) {
               this.listBarDrag = true;
            } else if (this.sideScroll.press(mx, my, this.sideX + this.sideW - 9.0F, this.sideY + 4.0F, 4.0F, 8.0F)) {
               this.sideBarDrag = true;
            } else if (!ReplayUi.hit(mx, my, this.listX + 4.0F, this.rowsY, this.listW - 14.0F, this.rowsH)) {
               if (ReplayUi.hit(mx, my, this.btnPlayX, this.actionY, this.actionW, 22.0F)) {
                  this.playSelected();
               } else if (ReplayUi.hit(mx, my, this.btnRenderX, this.actionY, this.actionW, 22.0F)) {
                  this.renderSelected();
               } else if (ReplayUi.hit(mx, my, this.btnDeleteX, this.actionY, this.actionW, 22.0F)) {
                  this.deleteSelected();
               } else {
                  float off = -this.sideScroll.value;
                  float x = this.sideX + 10.0F;
                  float w = this.sideW - 24.0F;
                  float[] ys = this.rowYs();

                  for (int i = 0; i < 4; i++) {
                     float fw = Math.min(86.0F, Math.max(48.0F, this.sideW * 0.4F));
                     if (ReplayUi.hit(mx, my, x + w - fw, ys[i] + off, fw, 22.0F)) {
                        this.setFocus(i);
                        return;
                     }
                  }

                  this.setFocus(-1);
                  float chipW = Math.min(92.0F, Math.max(64.0F, this.sideW * 0.42F));
                  if (ReplayUi.hit(mx, my, x + w - chipW, this.rowEncoderY + off, chipW, 22.0F)) {
                     ReplayConfig config = ReplayClient.getConfig();
                     config.encoder = ReplayConfig.nextEncoder(config.encoder);
                     config.save();
                     this.say("编码器已切换为 " + config.encoder.toUpperCase(Locale.ROOT));
                     ClientUi.click();
                  } else if (ReplayUi.hit(mx, my, x, this.rowAudioY + off, w, 24.0F)) {
                     ReplayConfig config = ReplayClient.getConfig();
                     config.recordGameAudio = !config.recordGameAudio;
                     config.save();
                     this.say(config.recordGameAudio ? "下次录制会重建游戏内声音。" : "已关闭游戏内声音录制。");
                     ClientUi.click();
                  } else if (ReplayUi.hit(mx, my, x, this.rowAutoY + off, w, 24.0F)) {
                     ReplayConfig config = ReplayClient.getConfig();
                     config.autoRecord = !config.autoRecord;
                     config.save();
                     this.say(config.autoRecord ? "进入世界后会自动开始录制。" : "已关闭自动录制。");
                     ClientUi.click();
                  }
               }
            } else {
               int index = (int)Math.floor((my - this.rowsY + this.listScroll.value) / 36.0F);
               if (index < 0 || index >= this.replays.length) {
                  index = -1;
               }

               if (index >= 0) {
                  long now = System.currentTimeMillis();
                  if (index == this.lastRowClickIndex && now - this.lastRowClickAt < 450L) {
                     this.selected = index;
                     this.playSelected();
                  } else {
                     this.selected = index;
                     this.say("已选择：" + this.replays[index].getName());
                  }

                  this.lastRowClickIndex = index;
                  this.lastRowClickAt = now;
                  ClientUi.click();
               }
            }
         }
      } else {
         this.mouseX = mx;
         this.mouseY = my;
      }
   }

   private void setFocus(int index) {
      if (this.focusField >= 0 && index != this.focusField) {
         this.applySettings(true);
      }

      this.focusField = index;

      for (int i = 0; i < this.fields.length; i++) {
         this.fields[i].focused = i == index;
      }
   }

   @Override
   protected void method_1032(int mx, int my, int button) {
      this.mouseDown = false;
      this.listBarDrag = false;
      this.sideBarDrag = false;
      this.listScroll.release();
      this.sideScroll.release();
      super.method_1032(mx, my, button);
   }

   @Override
   protected void method_4259(int mx, int my, int button, long clickedAt) {
      if (this.listBarDrag) {
         this.listScroll.drag(my, this.rowsY);
      } else if (this.sideBarDrag) {
         this.sideScroll.drag(my, this.sideY + 4.0F);
      } else {
         super.method_4259(mx, my, button, clickedAt);
      }
   }

   @Override
   protected void method_1024(char ch, int code) {
      if (this.openFrames >= 3) {
         if (code == 1) {
            if (this.focusField >= 0) {
               this.setFocus(-1);
            } else {
               this.close();
            }
         } else if (this.focusField < 0) {
            if (code == 28 && this.selected >= 0) {
               this.playSelected();
            }
         } else {
            Md3TextField field = this.fields[this.focusField];
            if (code == 28 || code == 156) {
               this.setFocus(-1);
            } else if (code == 14) {
               field.backspace();
            } else if (ch >= '0' && ch <= '9' || ch == '.' || ch == '-') {
               field.typeChar(ch);
            }
         }
      }
   }

   @Override
   public void method_1030() {
      if (this.focusField >= 0) {
         this.applySettings(false);
      }

      ReplayConfig config = ReplayClient.getConfig();
      if (config != null) {
         config.save();
      }

      super.method_1030();
   }

   private void refresh() {
      this.replays = ReplayPaths.listReplays();
      if (this.selected >= this.replays.length) {
         this.selected = this.replays.length - 1;
      }

      if (this.selected < 0 && this.replays.length > 0) {
         this.selected = 0;
      }

      this.listScroll.measure(this.replays.length * 36.0F, this.rowsH);
   }

   private void close() {
      class_1600 client = class_1600.method_2965();
      if (client != null) {
         client.method_2928(null);
      }
   }

   private File selectedFile() {
      return this.selected >= 0 && this.selected < this.replays.length ? this.replays[this.selected] : null;
   }

   private void playSelected() {
      File file = this.selectedFile();
      if (file == null) {
         this.say("请先选择一个回放。");
      } else {
         RenderManager manager = ReplayClient.getRenderManager();
         if (manager != null && manager.isActive()) {
            this.say("已有回放 / 渲染任务在运行，请先停止。");
         } else {
            PreviewJob preview = ReplayClient.openPreview(file);
            if (preview == null) {
               this.say("无法启动回放预览，请查看日志。");
            }
         }
      }
   }

   private void renderSelected() {
      final File file = this.selectedFile();
      if (file == null) {
         this.say("请先选择一个回放。");
      } else {
         RenderManager manager = ReplayClient.getRenderManager();
         if (manager != null && manager.isActive()) {
            this.say("已有回放 / 渲染任务在运行，请先停止。");
         } else if (this.applySettings(false)) {
            ReplayConfig config = ReplayClient.getConfig();
            if (manager != null) {
               if (ReplayFfmpegMissingScreen.needsPrompt()) {
                  class_1600 mc = class_1600.method_2965();
                  if (mc != null) {
                     mc.method_2928(new ReplayFfmpegMissingScreen(new Runnable() {
                        @Override
                        public void run() {
                           RenderManager m = ReplayClient.getRenderManager();
                           ReplayConfig c = ReplayClient.getConfig();
                           if (m != null && c != null) {
                              m.start(file, c.defaultFps, c.defaultWidth, c.defaultHeight, c.encoder);
                           }
                        }
                     }));
                  }
               } else {
                  long trimStart = -1L;
                  long trimEnd = -1L;

                  try {
                     File cameraFile = ReplayPaths.cameraFileFor(file);
                     CameraPath path = CameraPath.load(cameraFile);
                     if (path != null) {
                        if (path.trimStartSeconds > 0.0) {
                           trimStart = (long)(path.trimStartSeconds * 1.0E9);
                        }

                        if (path.trimEndSeconds > 0.0) {
                           trimEnd = (long)(path.trimEndSeconds * 1.0E9);
                        }
                     }
                  } catch (Throwable var10) {
                  }

                  manager.start(file, config.defaultFps, config.defaultWidth, config.defaultHeight, config.encoder, trimStart, trimEnd);
               }
            }
         }
      }
   }

   private void deleteSelected() {
      File file = this.selectedFile();
      if (file == null) {
         this.say("请先选择一个回放。");
      } else {
         RenderManager manager = ReplayClient.getRenderManager();
         if ((manager == null || !manager.isActive()) && (ReplayClient.getRecordingManager() == null || !ReplayClient.getRecordingManager().isActive())) {
            long now = System.currentTimeMillis();
            if (this.pendingDelete != null && this.pendingDelete.equals(file) && now <= this.pendingDeleteUntil) {
               boolean ok = file.delete();
               File camera = ReplayPaths.cameraFileFor(file);
               if (ok && camera != null && camera.isFile()) {
                  camera.delete();
               }

               this.pendingDelete = null;
               this.pendingDeleteUntil = 0L;
               this.refresh();
               this.say(ok ? "已删除回放：" + file.getName() : "删除失败，文件可能被占用。");
            } else {
               this.pendingDelete = file;
               this.pendingDeleteUntil = now + 5000L;
               this.say("再点一次“确认删除”才会永久删除：" + file.getName());
            }
         } else {
            this.say("请先停止录制 / 回放 / 导出任务，再删除回放。");
         }
      }
   }

   private void applySettings() {
      this.applySettings(true);
   }

   private boolean applySettings(boolean notify) {
      ReplayConfig config = ReplayClient.getConfig();
      if (config == null) {
         return false;
      } else {
         try {
            int sample = Integer.parseInt(this.sampleField.text.trim());
            double fps = Double.parseDouble(this.fpsField.text.trim());
            int width = Integer.parseInt(this.widthField.text.trim());
            int height = Integer.parseInt(this.heightField.text.trim());
            if (!(fps <= 0.0) && !Double.isNaN(fps) && !Double.isInfinite(fps)) {
               config.sampleHz = sample;
               config.defaultFps = fps;
               config.defaultWidth = width;
               config.defaultHeight = height;
               config.sanitize();
               config.save();
               this.syncFieldsFromConfig();
               if (notify) {
                  this.say(
                     "设置已保存：" + config.defaultWidth + "x" + config.defaultHeight + " · " + trim(config.defaultFps) + " fps · 采样 " + config.sampleHz + " Hz"
                  );
                  ClientUi.click();
               }

               return true;
            } else {
               this.say("帧率必须是大于 0 的有限数字。");
               return false;
            }
         } catch (Throwable var8) {
            this.say("设置无效：" + (var8.getMessage() == null ? "请检查数字格式" : var8.getMessage()));
            return false;
         }
      }
   }
}
