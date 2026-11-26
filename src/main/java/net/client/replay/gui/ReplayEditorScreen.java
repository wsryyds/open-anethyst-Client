package net.client.replay.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.client.replay.ReplayClient;
import net.client.replay.config.ReplayConfig;
import net.client.replay.render.PreviewJob;
import net.client.replay.session.CameraController;
import net.client.replay.session.CameraPath;
import net.client.replay.util.ReplayPaths;
import net.client.ui.ClientTheme;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3Text;
import net.client.ui.md3.widget.Md3TextField;
import net.minecraft.class_1600;
import net.minecraft.class_388;
import net.minecraft.class_864;
import net.minecraft.class_988;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ReplayEditorScreen extends class_388 {
   private static final int PAUSE = 1;
   private static final int BACK = 2;
   private static final int FORWARD = 3;
   private static final int RENDER = 4;
   private static final int EXIT = 5;
   private static final int HIDE = 6;
   private static final int MODE_PLAYER = 10;
   private static final int MODE_FREE = 11;
   private static final int MODE_TARGET = 12;
   private static final int ADD_KEY = 20;
   private static final int CLEAR_KEY = 21;
   private static final int SAVE_KEY = 22;
   private static final int JUMP_START = 32;
   private static final int JUMP_END = 33;
   private static final int LOOK_LEFT = 40;
   private static final int APPLY_CAM = 50;
   private static final int F_TRIM_START = 0;
   private static final int F_TRIM_END = 1;
   private static final int F_CAM_X = 2;
   private final Md3Canvas canvas = new Md3Canvas();
   private final PreviewJob preview;
   private final List<ReplayEditorScreen.Btn> buttons = new ArrayList<>();
   private final Md3TextField[] fields = new Md3TextField[8];
   private final float[] fieldX = new float[8];
   private final float[] fieldY = new float[8];
   private final float[] fieldW = new float[8];
   private final float[] fieldH = new float[8];
   private boolean[] fieldVisible = new boolean[8];
   private boolean panelVisible;
   private boolean compact;
   private float mouseX;
   private float mouseY;
   private boolean mouseDown;
   private long lastNanos;
   private float dt;
   private int openFrames;
   private boolean trimRestored;
   private String message = "";
   private long messageUntil;
   private float headerH;
   private float timelineX;
   private float timelineY;
   private float timelineW;
   private float timelineH;
   private float leftX;
   private float leftY;
   private float leftW;
   private float leftH;
   private float rightX;
   private float rightY;
   private float rightW;
   private float rightH;
   private int focusField = -1;
   private int timelineDrag;
   private boolean draggingCamera;
   private boolean blankClick;
   private float dragX;
   private float dragY;
   private int targetIndex;
   private boolean lastF5;
   private long lastPerspectiveMs;
   private CameraPath.Keyframe pendingDelete;
   private boolean pathDirty;
   private int pendingLeave;
   private float dlgX;
   private float dlgY;
   private float dlgW;
   private float dlgH;

   public ReplayEditorScreen(PreviewJob preview) {
      this.preview = preview;

      for (int i = 0; i < this.fields.length; i++) {
         Md3TextField field = new Md3TextField();
         field.variant = Md3TextField.Variant.OUTLINED;
         field.label = "";
         field.supporting = "";
         field.maxLength = 10;
         this.fields[i] = field;
      }

      this.panelVisible = false;
      if (preview != null) {
         preview.setLoop(true);
      }
   }

   private CameraController camera() {
      return this.preview != null && this.preview.getSession() != null ? this.preview.getSession().getCameraController() : null;
   }

   @Override
   public void method_1044() {
      this.compact = this.field_1231 < 310.0F || this.field_1230 < 420.0F;
      this.headerH = this.compact ? 22.0F : 26.0F;
      this.timelineX = 8.0F;
      this.timelineY = this.compact ? 26.0F : 30.0F;
      this.timelineW = Math.max(40.0F, this.field_1230 - 16.0F);
      this.timelineH = 12.0F;
      float actionsY = this.timelineY + this.timelineH + 4.0F;
      float contentY = actionsY + 24.0F;
      float contentH = Math.max(80.0F, this.field_1231 - contentY - 18.0F);
      if (this.compact) {
         this.leftX = 6.0F;
         this.leftY = contentY;
         this.leftW = this.field_1230 - 12.0F;
         this.leftH = contentH;
         this.rightX = this.leftX;
         this.rightY = this.leftY;
         this.rightW = this.leftW;
         this.rightH = this.leftH;
         this.layoutCompact(actionsY);
      } else {
         this.leftX = 8.0F;
         this.rightX = this.field_1230 / 2.0F + 2.0F;
         this.leftY = contentY;
         this.rightY = contentY;
         this.leftW = this.field_1230 / 2.0F - 10.0F;
         this.rightW = this.field_1230 / 2.0F - 10.0F;
         this.leftH = contentH;
         this.rightH = contentH;
         this.layoutFull(actionsY);
      }

      this.lastNanos = 0L;
      this.openFrames = 0;
      this.focusField = -1;
      this.timelineDrag = 0;
      this.draggingCamera = false;
      this.setGrabbed(!this.panelVisible);
   }

   private void btn(int id, float x, float y, float w, float h, String label, String icon, ReplayUi.Style style) {
      ReplayEditorScreen.Btn b = new ReplayEditorScreen.Btn();
      b.id = id;
      b.x = x;
      b.y = y;
      b.w = w;
      b.h = h;
      b.label = label;
      b.icon = icon;
      b.style = style;
      this.buttons.add(b);
   }

   private void fieldBox(int index, int kind, float x, float y, float w, float h) {
      this.fieldX[index] = x;
      this.fieldY[index] = y;
      this.fieldW[index] = w;
      this.fieldH[index] = h;
      this.fieldVisible[index] = true;
      this.fields[index].setBounds(x, y, w, h);
   }

   private void layoutFull(float actionsY) {
      float gap = 4.0F;
      float quarter = (this.field_1230 - 16.0F - gap * 4.0F) / 5.0F;
      this.btn(1, 8.0F, actionsY, quarter, 22.0F, "暂停", "pause", ReplayUi.Style.TONAL);
      this.btn(2, 8.0F + quarter + gap, actionsY, quarter, 22.0F, "-5 秒", "fast_rewind", ReplayUi.Style.TEXT);
      this.btn(3, 8.0F + (quarter + gap) * 2.0F, actionsY, quarter, 22.0F, "+5 秒", "fast_forward", ReplayUi.Style.TEXT);
      this.btn(4, 8.0F + (quarter + gap) * 3.0F, actionsY, quarter, 22.0F, "渲染片段", "download", ReplayUi.Style.FILLED);
      this.btn(5, 8.0F + (quarter + gap) * 4.0F, actionsY, quarter, 22.0F, "退出", "close", ReplayUi.Style.TONAL);
      float rowH = 22.0F;
      float rowGap = 10.0F;
      float margin = 8.0F;
      float w = this.field_1230 - margin * 2.0F;
      float keyY = this.field_1231 - margin - rowH;
      float modeY = keyY - rowH - rowGap;
      float trimY = modeY - rowH - rowGap;
      float fieldW = 74.0F;
      this.fieldBox(0, 0, margin, trimY, fieldW, rowH);
      this.fieldBox(1, 1, margin + fieldW + gap, trimY, fieldW, rowH);
      float bx = margin + fieldW * 2.0F + gap * 2.0F;
      float bw = (w - (bx - margin) - gap) / 2.0F;
      this.btn(32, bx, trimY, bw, rowH, "跳到起点", null, ReplayUi.Style.TONAL);
      this.btn(33, bx + bw + gap, trimY, bw, rowH, "跳到终点", null, ReplayUi.Style.TONAL);
      float m3 = (w - gap * 2.0F) / 3.0F;
      this.btn(10, margin, modeY, m3, rowH, "玩家视角", null, ReplayUi.Style.TONAL);
      this.btn(11, margin + (m3 + gap), modeY, m3, rowH, "自由相机", null, ReplayUi.Style.TONAL);
      this.btn(12, margin + (m3 + gap) * 2.0F, modeY, m3, rowH, "目标玩家", null, ReplayUi.Style.TONAL);
      float k3 = (w - gap * 2.0F) / 3.0F;
      this.btn(20, margin, keyY, k3, rowH, "添加关键帧", null, ReplayUi.Style.TONAL);
      this.btn(21, margin + (k3 + gap), keyY, k3, rowH, "清空轨道", null, ReplayUi.Style.TONAL);
      this.btn(22, margin + (k3 + gap) * 2.0F, keyY, k3, rowH, "保存轨道", null, ReplayUi.Style.TONAL);
   }

   private void layoutCompact(float actionsY) {
      float gap = 3.0F;
      float quarter = (this.field_1230 - 12.0F - gap * 4.0F) / 5.0F;
      this.btn(1, 6.0F, actionsY, quarter, 20.0F, "暂停", "pause", ReplayUi.Style.TONAL);
      this.btn(2, 6.0F + quarter + gap, actionsY, quarter, 20.0F, "◀5s", "fast_rewind", ReplayUi.Style.TEXT);
      this.btn(3, 6.0F + (quarter + gap) * 2.0F, actionsY, quarter, 20.0F, "5s▶", "fast_forward", ReplayUi.Style.TEXT);
      this.btn(4, 6.0F + (quarter + gap) * 3.0F, actionsY, quarter, 20.0F, "渲染", "download", ReplayUi.Style.FILLED);
      this.btn(5, 6.0F + (quarter + gap) * 4.0F, actionsY, quarter, 20.0F, "退出", "close", ReplayUi.Style.TONAL);
      float rowH = 20.0F;
      float rowGap = 7.0F;
      float margin = 6.0F;
      float w = this.field_1230 - margin * 2.0F;
      float keyY = this.field_1231 - margin - rowH;
      float modeY = keyY - rowH - rowGap;
      float trimY = modeY - rowH - rowGap;
      float fieldW = Math.min(60.0F, w / 4.0F);
      this.fieldBox(0, 0, margin, trimY, fieldW, rowH);
      this.fieldBox(1, 1, margin + fieldW + gap, trimY, fieldW, rowH);
      float bx = margin + fieldW * 2.0F + gap * 2.0F;
      float bw = (w - (bx - margin) - gap) / 2.0F;
      this.btn(32, bx, trimY, bw, rowH, "起点", null, ReplayUi.Style.TONAL);
      this.btn(33, bx + bw + gap, trimY, bw, rowH, "终点", null, ReplayUi.Style.TONAL);
      float m3 = (w - gap * 2.0F) / 3.0F;
      this.btn(10, margin, modeY, m3, rowH, "玩家", null, ReplayUi.Style.TONAL);
      this.btn(11, margin + (m3 + gap), modeY, m3, rowH, "自由", null, ReplayUi.Style.TONAL);
      this.btn(12, margin + (m3 + gap) * 2.0F, modeY, m3, rowH, "目标", null, ReplayUi.Style.TONAL);
      float k3 = (w - gap * 2.0F) / 3.0F;
      this.btn(20, margin, keyY, k3, rowH, "添加关键帧", null, ReplayUi.Style.TONAL);
      this.btn(21, margin + (k3 + gap), keyY, k3, rowH, "清空", null, ReplayUi.Style.TONAL);
      this.btn(22, margin + (k3 + gap) * 2.0F, keyY, k3, rowH, "保存", null, ReplayUi.Style.TONAL);
   }

   private int keyCount() {
      CameraController controller = this.camera();
      return controller != null && controller.getPath() != null ? controller.getPath().keyframes.size() : 0;
   }

   @Override
   public void method_1025(int mx, int my, float tickDelta) {
      this.mouseX = mx;
      this.mouseY = my;
      long now = System.nanoTime();
      this.dt = this.lastNanos == 0L ? 0.05F : Math.min(0.05F, (float)(now - this.lastNanos) / 1.0E9F);
      this.lastNanos = now;
      this.openFrames++;
      if (!this.trimRestored && this.preview != null) {
         CameraController controller = this.camera();
         if (controller != null) {
            this.trimRestored = true;
            double total = Math.max(0.01, this.preview.getTotalSeconds());
            double savedStart = controller.getSavedTrimStart();
            double savedEnd = controller.getSavedTrimEnd();
            if (savedStart >= 0.0 && savedStart < total - 0.05 && (this.fields[0].text == null || this.fields[0].text.isEmpty())) {
               this.fields[0].text = fmt(savedStart);
            }

            if (savedEnd > savedStart + 0.05 && savedEnd <= total && (this.fields[1].text == null || this.fields[1].text.isEmpty())) {
               this.fields[1].text = fmt(savedEnd);
            }

            double saved = controller.getSavedPlayhead();
            if (saved > 0.05) {
               this.preview.seekToSeconds(saved);
            }
         }
      }

      this.tickEditor(now);
      Md3Theme t = ClientTheme.get();
      this.canvas.begin(this.field_1230, this.field_1231);
      if (!this.panelVisible) {
         this.canvas.fill(0.0F, 0.0F, this.field_1230, 22.0F, 1711276032);
         Md3Text.centered(this.canvas, "T 呼出剪辑面板 · 拖动屏幕转视角 · F5 切视角 · F1 切 HUD", this.field_1230 / 2.0F, 11.0F, Md3Type.BODY_SMALL, -1);
         String time = this.timeText();
         Md3Text.centered(this.canvas, time, this.field_1230 / 2.0F, 30.0F, Md3Type.LABEL_MEDIUM, -3936);
         this.canvas.end();
      } else {
         this.drawCards(t);
         this.drawHeader(t);
         this.drawTimeline(t);
         this.drawButtons(t);
         this.drawFields(t);
         this.drawCardLabels(t);
         this.drawFooter(t);
         if (this.pendingDelete != null) {
            this.drawDeleteDialog(t);
         } else if (this.pendingLeave != 0) {
            this.drawLeaveDialog(t);
         }

         this.canvas.end();
      }
   }

   private void setGrabbed(boolean grabbed) {
      try {
         Mouse.setGrabbed(grabbed);
      } catch (Throwable var3) {
      }
   }

   private void tickEditor(long now) {
      CameraController controller = this.camera();

      boolean f5;
      try {
         f5 = Keyboard.isKeyDown(63);
      } catch (Throwable var11) {
         f5 = false;
      }

      if (f5 && !this.lastF5) {
         this.cyclePerspective();
      }

      this.lastF5 = f5;
      if (!this.panelVisible && controller != null && controller.getMode() == 1) {
         int dx = Mouse.getDX();
         int dy = Mouse.getDY();
         if (dx != 0 || dy != 0) {
            controller.rotateFree(dx * 0.45F, -dy * 0.45F);
         }
      }

      if (this.focusField < 0 && controller != null && controller.getMode() == 1) {
         double forward = (Keyboard.isKeyDown(17) ? 1.0 : 0.0) - (Keyboard.isKeyDown(31) ? 1.0 : 0.0);
         double strafe = (Keyboard.isKeyDown(32) ? 1.0 : 0.0) - (Keyboard.isKeyDown(30) ? 1.0 : 0.0);
         double vertical = (Keyboard.isKeyDown(57) ? 1.0 : 0.0) - (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54) ? 0.0 : 1.0);
         if (forward != 0.0 || strafe != 0.0 || vertical != 0.0) {
            controller.moveFree(forward, strafe, vertical, this.dt * 20.0);
         }
      }

      this.syncCameraFields();
      if (this.preview != null) {
         this.preview.setClipRange((long)(this.trimStart() * 1.0E9), (long)(this.trimEnd() * 1.0E9));
      }

      if (this.preview == null || this.preview.isFinished()) {
         this.message = "回放预览已结束。";
      }

      if (this.messageUntil > 0L && now / 1000000L > this.messageUntil) {
         this.message = "";
         this.messageUntil = 0L;
      }
   }

   private void drawHeader(Md3Theme t) {
      this.canvas.fill(0.0F, 0.0F, this.field_1230, this.headerH, -435154920);
      String file = this.preview == null ? "-" : this.preview.getFile().getName();
      float x = 8.0F;
      this.canvas.icon("animation", x, this.headerH / 2.0F - 7.0F, 14.0F, -1);
      Md3Text.leftMiddle(
         this.canvas,
         ReplayUi.fit(this.canvas, "剪辑台 · " + file, Md3Type.TITLE_SMALL, this.field_1230 * 0.5F),
         x + 20.0F,
         this.headerH / 2.0F,
         Md3Type.TITLE_SMALL,
         -1
      );
      String time = this.timeText();
      Md3Text.rightMiddle(this.canvas, time, this.field_1230 - 8.0F, this.headerH / 2.0F, Md3Type.LABEL_MEDIUM, -8062);
   }

   private String timeText() {
      if (this.preview == null) {
         return "0:00 / 0:00";
      } else {
         double total = Math.max(0.01, this.preview.getTotalSeconds());
         return ReplayPaths.formatDuration(this.preview.getCurrentSeconds()) + " / " + ReplayPaths.formatDuration(total);
      }
   }

   private void drawTimeline(Md3Theme t) {
      this.canvas.roundRect(this.timelineX, this.timelineY, this.timelineW, this.timelineH, this.timelineH / 2.0F, -870309848);
      double total = this.preview == null ? 1.0 : Math.max(0.01, this.preview.getTotalSeconds());
      float sx = this.timeX(this.trimStart());
      float ex = this.timeX(this.trimEnd());
      this.canvas.roundRect(sx, this.timelineY, Math.max(2.0F, ex - sx), this.timelineH, this.timelineH / 2.0F, -867223669);
      this.canvas.roundRect(sx - 2.0F, this.timelineY - 2.0F, 4.0F, this.timelineH + 4.0F, 2.0F, -3722);
      this.canvas.roundRect(ex - 2.0F, this.timelineY - 2.0F, 4.0F, this.timelineH + 4.0F, 2.0F, -30080);
      float px = this.timeX(this.preview == null ? 0.0 : this.preview.getCurrentSeconds());
      this.canvas.fill(px, this.timelineY - 3.0F, 1.5F, this.timelineH + 6.0F, -1);
      CameraController controller = this.camera();
      if (controller != null && controller.getPath() != null) {
         for (CameraPath.Keyframe frame : controller.getPath().keyframes) {
            float kx = this.timeX(frame.time);
            int color = frame.mode == 0 ? -9834322 : (frame.mode == 2 ? -10929 : -12532481);
            this.canvas.roundRect(kx - 1.5F, this.timelineY + 2.0F, 3.0F, 3.0F, 1.5F, color);
            this.canvas.roundRect(kx - 1.5F, this.timelineY + 7.0F, 3.0F, 3.0F, 1.5F, color);
         }
      }

      if (Mouse.isButtonDown(0) && this.timelineDrag != 0 && this.preview != null) {
         double value = this.secondsAt(this.mouseX);
         if (this.timelineDrag == 1) {
            double end = this.trimEnd();
            this.fields[0].text = fmt(Math.max(0.0, Math.min(value, end - 0.05)));
            this.pathDirty = true;
         } else if (this.timelineDrag == 2) {
            double start = this.trimStart();
            this.fields[1].text = fmt(Math.min(total, Math.max(value, start + 0.05)));
            this.pathDirty = true;
         } else {
            this.preview.seekToSeconds(value);
         }
      }

      String totalText = ReplayPaths.formatDuration(total);
      Md3Text.rightMiddle(this.canvas, totalText, this.timelineX + this.timelineW, this.timelineY - 5.0F, Md3Type.LABEL_SMALL, -855638017);
   }

   private float timeX(double seconds) {
      double total = this.preview == null ? 1.0 : Math.max(0.01, this.preview.getTotalSeconds());
      float ratio = (float)Math.max(0.0, Math.min(1.0, seconds / total));
      return this.timelineX + ratio * this.timelineW;
   }

   private double secondsAt(float mx) {
      double total = this.preview == null ? 0.0 : Math.max(0.01, this.preview.getTotalSeconds());
      float ratio = Math.max(0.0F, Math.min(1.0F, (mx - this.timelineX) / this.timelineW));
      return ratio * total;
   }

   private void drawButtons(Md3Theme t) {
      CameraController controller = this.camera();
      int mode = controller == null ? -1 : controller.getMode();

      for (ReplayEditorScreen.Btn b : this.buttons) {
         boolean hover = ReplayUi.hit(this.mouseX, this.mouseY, b.x, b.y, b.w, b.h);
         String label = b.label;
         String icon = b.icon;
         ReplayUi.Style style = b.style;
         if (b.id == 1) {
            label = this.preview != null && this.preview.isPaused() ? "继续" : "暂停";
            icon = this.preview != null && this.preview.isPaused() ? "play_arrow" : "pause";
         } else if (b.id == 10 && mode == 0) {
            style = ReplayUi.Style.FILLED;
         } else if (b.id == 11 && mode == 1) {
            style = ReplayUi.Style.FILLED;
         } else if (b.id == 12 && mode == 2) {
            style = ReplayUi.Style.FILLED;
            label = "目标:" + this.targetLabel();
         } else if (b.id == 21) {
            label = "清空轨道 " + this.keyCount();
         }

         boolean enabled = this.buttonEnabled(b.id);
         ReplayUi.button(this.canvas, t, b.x, b.y, b.w, b.h, label, icon, style, enabled, hover, hover && this.mouseDown);
      }
   }

   private boolean buttonEnabled(int id) {
      if (this.preview == null) {
         return false;
      } else if (id == 5 || id == 6) {
         return true;
      } else if (id == 4) {
         return !this.preview.isFinished() && !this.busy();
      } else if (id == 21) {
         return this.keyCount() > 0;
      } else if (id != 22) {
         if (id >= 10 && id <= 12) {
            return this.camera() != null;
         } else if (id >= 40 && id < 50) {
            return this.camera() != null;
         } else {
            return id == 50 ? this.camera() != null : !this.preview.isFinished();
         }
      } else if (this.keyCount() <= 0 && !this.pathDirty) {
         double total = this.preview.getTotalSeconds();
         double start = this.trimStart();
         double end = this.trimEnd();
         return start > 0.05 || total > 0.0 && end < total - 0.05;
      } else {
         return true;
      }
   }

   private boolean busy() {
      return ReplayClient.getRenderManager() != null && ReplayClient.getRenderManager().isActive() && ReplayClient.getRenderManager().getPreview() == null;
   }

   private void drawFields(Md3Theme t) {
      String[] labels = new String[]{"起点", "终点", "X", "Y", "Z", "Yaw", "Pitch", "FOV"};

      for (int i = 0; i < this.fields.length; i++) {
         if (this.fieldVisible[i]) {
            if (i < 2) {
               Md3Text.leftMiddle(this.canvas, labels[i] + " (秒)", this.fieldX[i], this.fieldY[i] - 8.0F, Md3Type.LABEL_SMALL, -855638017);
            } else {
               Md3Text.rightMiddle(
                  this.canvas, labels[i], this.fieldX[i] - 2.0F, this.fieldY[i] + this.fieldH[i] / 2.0F, Md3Type.LABEL_SMALL, t.colors.onSurfaceVariant
               );
            }

            Md3TextField field = this.fields[i];
            field.focused = this.focusField == i;
            field.render(this.canvas, t, this.mouseX, this.mouseY, false, this.dt);
         }
      }
   }

   private void drawCards(Md3Theme t) {
   }

   private void drawCardLabels(Md3Theme t) {
   }

   private void layoutDialog() {
      this.dlgW = Math.min(280.0F, this.field_1230 - 32.0F);
      this.dlgH = 118.0F;
      this.dlgX = (this.field_1230 - this.dlgW) / 2.0F;
      this.dlgY = (this.field_1231 - this.dlgH) / 2.0F;
   }

   private void drawDeleteDialog(Md3Theme t) {
      this.layoutDialog();
      this.canvas.fill(0.0F, 0.0F, this.field_1230, this.field_1231, -2013265920);
      this.canvas.shadow(this.dlgX, this.dlgY, this.dlgW, this.dlgH, 16.0F, 2);
      this.canvas.roundRect(this.dlgX, this.dlgY, this.dlgW, this.dlgH, 16.0F, t.colors.surfaceContainerHigh);
      Md3Text.leftMiddle(this.canvas, "删除这个关键帧？", this.dlgX + 16.0F, this.dlgY + 18.0F, Md3Type.TITLE_SMALL, t.colors.onSurface);
      String info = String.format(Locale.ROOT, "%.2f 秒 · %s", this.pendingDelete.time, modeLabel(this.pendingDelete.mode));
      Md3Text.leftMiddle(this.canvas, info, this.dlgX + 16.0F, this.dlgY + 42.0F, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
      float gap = 8.0F;
      float bw = (this.dlgW - 32.0F - gap) / 2.0F;
      float by = this.dlgY + this.dlgH - 34.0F;
      ReplayUi.button(
         this.canvas,
         t,
         this.dlgX + 16.0F,
         by,
         bw,
         24.0F,
         "删除",
         "delete",
         ReplayUi.Style.DANGER,
         true,
         ReplayUi.hit(this.mouseX, this.mouseY, this.dlgX + 16.0F, by, bw, 24.0F),
         this.mouseDown
      );
      ReplayUi.button(
         this.canvas,
         t,
         this.dlgX + 16.0F + bw + gap,
         by,
         bw,
         24.0F,
         "取消",
         "close",
         ReplayUi.Style.OUTLINED,
         true,
         ReplayUi.hit(this.mouseX, this.mouseY, this.dlgX + 16.0F + bw + gap, by, bw, 24.0F),
         this.mouseDown
      );
   }

   private void drawLeaveDialog(Md3Theme t) {
      this.layoutDialog();
      this.canvas.fill(0.0F, 0.0F, this.field_1230, this.field_1231, -2013265920);
      this.canvas.shadow(this.dlgX, this.dlgY, this.dlgW, this.dlgH, 16.0F, 2);
      this.canvas.roundRect(this.dlgX, this.dlgY, this.dlgW, this.dlgH, 16.0F, t.colors.surfaceContainerHigh);
      String title = this.pendingLeave == 1 ? "退出剪辑台？" : "渲染片段？";
      Md3Text.leftMiddle(this.canvas, title, this.dlgX + 16.0F, this.dlgY + 18.0F, Md3Type.TITLE_SMALL, t.colors.onSurface);
      Md3Text.leftMiddle(this.canvas, "轨道有未保存的关键帧改动，要先保存吗？", this.dlgX + 16.0F, this.dlgY + 42.0F, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
      float gap = 8.0F;
      float bw = (this.dlgW - 32.0F - gap * 2.0F) / 3.0F;
      float by = this.dlgY + this.dlgH - 34.0F;
      float bx = this.dlgX + 16.0F;
      ReplayUi.button(
         this.canvas,
         t,
         bx,
         by,
         bw,
         24.0F,
         "保存",
         "save",
         ReplayUi.Style.FILLED,
         true,
         ReplayUi.hit(this.mouseX, this.mouseY, bx, by, bw, 24.0F),
         this.mouseDown
      );
      ReplayUi.button(
         this.canvas,
         t,
         bx + bw + gap,
         by,
         bw,
         24.0F,
         "不保存",
         "delete",
         ReplayUi.Style.DANGER,
         true,
         ReplayUi.hit(this.mouseX, this.mouseY, bx + bw + gap, by, bw, 24.0F),
         this.mouseDown
      );
      ReplayUi.button(
         this.canvas,
         t,
         bx + (bw + gap) * 2.0F,
         by,
         bw,
         24.0F,
         "取消",
         "close",
         ReplayUi.Style.OUTLINED,
         true,
         ReplayUi.hit(this.mouseX, this.mouseY, bx + (bw + gap) * 2.0F, by, bw, 24.0F),
         this.mouseDown
      );
   }

   private static String modeLabel(int mode) {
      if (mode == 0) {
         return "玩家视角";
      } else {
         return mode == 2 ? "目标玩家" : "自由相机";
      }
   }

   private void drawFooter(Md3Theme t) {
      if (!this.message.isEmpty()) {
         Md3Text.centered(
            this.canvas,
            ReplayUi.fit(this.canvas, this.message, Md3Type.BODY_SMALL, this.field_1230 - 20.0F),
            this.field_1230 / 2.0F,
            this.field_1231 - 14.0F,
            Md3Type.BODY_SMALL,
            t.colors.primary
         );
      }
   }

   private String modeName() {
      CameraController controller = this.camera();
      if (controller == null) {
         return "无";
      } else if (controller.isPathActive()) {
         return "关键帧轨道";
      } else if (controller.getMode() == 1) {
         return "自由相机";
      } else {
         return controller.getMode() == 2 ? "目标玩家" : "玩家视角";
      }
   }

   private String targetLabel() {
      CameraController controller = this.camera();
      if (controller != null && this.preview != null && this.preview.getSession() != null && this.preview.getSession().getWorld() != null) {
         int id = controller.getTargetEntityId();
         if (id < 0) {
            return "无";
         } else {
            for (class_864 entity : this.preview.getSession().getWorld().method_3737()) {
               if (entity != null && entity.method_8365() == id && entity instanceof class_988 player) {
                  return player.method_8429() == null ? "#" + id : player.method_8429().getName();
               }
            }

            return "#" + id;
         }
      } else {
         return "无";
      }
   }

   private boolean timelineContains(float mx, float my) {
      return ReplayUi.hit(mx, my, this.timelineX - 6.0F, this.timelineY - 6.0F, this.timelineW + 12.0F, this.timelineH + 12.0F);
   }

   private CameraPath.Keyframe keyframeAt(float mx, float my) {
      CameraController controller = this.camera();
      if (controller != null && controller.getPath() != null) {
         CameraPath.Keyframe best = null;
         float bestDistance = 6.0F;

         for (CameraPath.Keyframe frame : controller.getPath().keyframes) {
            float x = this.timeX(frame.time);
            float d = Math.abs(mx - x);
            if (d <= bestDistance) {
               bestDistance = d;
               best = frame;
            }
         }

         return best;
      } else {
         return null;
      }
   }

   private ReplayEditorScreen.Btn hitButton(float mx, float my) {
      for (ReplayEditorScreen.Btn b : this.buttons) {
         if (ReplayUi.hit(mx, my, b.x, b.y, b.w, b.h)) {
            return b;
         }
      }

      return null;
   }

   @Override
   protected void method_1026(int mx, int my, int button) {
      this.mouseX = mx;
      this.mouseY = my;
      this.mouseDown = true;
      if (this.openFrames >= 2 && this.preview != null) {
         if (this.pendingDelete != null) {
            if (button == 0) {
               this.layoutDialog();
               float gap = 8.0F;
               float bw = (this.dlgW - 32.0F - gap) / 2.0F;
               float by = this.dlgY + this.dlgH - 34.0F;
               if (ReplayUi.hit(mx, my, this.dlgX + 16.0F, by, bw, 24.0F)) {
                  CameraController controller = this.camera();
                  CameraPath.Keyframe frame = this.pendingDelete;
                  this.pendingDelete = null;
                  if (controller != null && controller.deleteKeyframe(frame)) {
                     this.pathDirty = true;
                     this.message = "已删除关键帧。";
                  }
               } else if (ReplayUi.hit(mx, my, this.dlgX + 16.0F + bw + gap, by, bw, 24.0F)) {
                  this.pendingDelete = null;
               }
            }
         } else if (this.pendingLeave != 0) {
            if (button == 0) {
               this.layoutDialog();
               float gap = 8.0F;
               float bw = (this.dlgW - 32.0F - gap * 2.0F) / 3.0F;
               float by = this.dlgY + this.dlgH - 34.0F;
               float bx = this.dlgX + 16.0F;
               if (ReplayUi.hit(mx, my, bx, by, bw, 24.0F)) {
                  this.saveThenContinue();
               } else if (ReplayUi.hit(mx, my, bx + bw + gap, by, bw, 24.0F)) {
                  this.discardThenContinue();
               } else {
                  this.pendingLeave = 0;
               }
            }
         } else if (button == 1) {
            if (this.panelVisible && this.timelineContains(mx, my)) {
               CameraPath.Keyframe hit = this.keyframeAt(mx, my);
               if (hit != null) {
                  this.pendingDelete = hit;
                  this.focusField = -1;
               }
            }
         } else if (button == 0) {
            if (!this.panelVisible) {
               CameraController controller = this.camera();
               if (controller != null && controller.getMode() == 1) {
                  this.draggingCamera = true;
                  this.dragX = mx;
                  this.dragY = my;
               }
            } else if (ReplayUi.hit(mx, my, this.timelineX - 4.0F, this.timelineY - 4.0F, this.timelineW + 8.0F, this.timelineH + 8.0F)) {
               float sx = this.timeX(this.trimStart());
               float ex = this.timeX(this.trimEnd());
               if (Math.abs(mx - sx) <= 6.0F) {
                  this.timelineDrag = 1;
               } else if (Math.abs(mx - ex) <= 6.0F) {
                  this.timelineDrag = 2;
               } else {
                  this.timelineDrag = 3;
                  this.preview.seekToSeconds(this.secondsAt(mx));
               }

               this.focusField = -1;
            } else {
               for (int i = 0; i < this.fields.length; i++) {
                  if (this.fieldVisible[i] && ReplayUi.hit(mx, my, this.fieldX[i], this.fieldY[i], this.fieldW[i], this.fieldH[i])) {
                     this.focusField = i;
                     return;
                  }
               }

               this.focusField = -1;
               ReplayEditorScreen.Btn b = this.hitButton(mx, my);
               if (b == null) {
                  CameraController controller = this.camera();
                  if (controller != null && controller.getMode() == 1) {
                     this.draggingCamera = true;
                     this.blankClick = true;
                     this.dragX = mx;
                     this.dragY = my;
                  }
               } else if (b.id == 6) {
                  this.panelVisible = false;
                  this.setGrabbed(true);
               } else {
                  if (this.buttonEnabled(b.id)) {
                     this.action(b.id);
                  }
               }
            }
         }
      }
   }

   @Override
   protected void method_1032(int mx, int my, int button) {
      this.mouseDown = false;
      this.timelineDrag = 0;
      boolean wasBlank = this.blankClick;
      this.draggingCamera = false;
      this.blankClick = false;
      if (wasBlank && button == 0 && this.panelVisible) {
         float dx = mx - this.dragX;
         float dy = my - this.dragY;
         if (dx * dx + dy * dy < 144.0F) {
            this.pickTargetPlayer(mx, my);
         }
      }

      super.method_1032(mx, my, button);
   }

   private void pickTargetPlayer(int mx, int my) {
      CameraController controller = this.camera();
      if (controller != null && this.preview != null && this.preview.getSession() != null && this.preview.getSession().getWorld() != null) {
         if (controller.getMode() == 1) {
            class_1600 client = class_1600.method_2965();
            double ox = controller.getFreeX();
            double oy = controller.getFreeY();
            double oz = controller.getFreeZ();
            double yawRad = Math.toRadians(controller.getFreeYaw());
            double pitchRad = Math.toRadians(controller.getFreePitch());
            double fx = -Math.sin(yawRad) * Math.cos(pitchRad);
            double fy = -Math.sin(pitchRad);
            double fz = Math.cos(yawRad) * Math.cos(pitchRad);
            double rx = -Math.cos(yawRad);
            double ry = 0.0;
            double rz = -Math.sin(yawRad);
            double ux = ry * fz - rz * fy;
            double uy = rz * fx - rx * fz;
            double uz = rx * fy - ry * fx;
            float fov = controller.getFreeFov();
            double tanV = Math.tan(Math.toRadians(Math.max(10.0F, Math.min(160.0F, fov))) * 0.5);
            double tanH = tanV * ((double)this.field_1230 / Math.max(1, this.field_1231));
            class_988 best = null;
            double bestPixels = 64.0;
            double bestDepth = Double.MAX_VALUE;

            for (class_864 entity : this.preview.getSession().getWorld().method_3737()) {
               if (entity instanceof class_988 && entity != client.field_10310) {
                  class_988 player = (class_988)entity;
                  double dx = player.field_3252 - ox;
                  double dy = player.field_3253 + player.method_2544() - oy;
                  double dz = player.field_3254 - oz;
                  double depth = dx * fx + dy * fy + dz * fz;
                  if (!(depth <= 0.08)) {
                     double ndcX = (dx * rx + dy * ry + dz * rz) / depth / tanH;
                     double ndcY = (dx * ux + dy * uy + dz * uz) / depth / tanV;
                     double px = this.field_1230 * 0.5 * (1.0 + ndcX);
                     double py = this.field_1231 * 0.5 * (1.0 - ndcY);
                     double off = Math.sqrt((px - mx) * (px - mx) + (py - my) * (py - my));
                     if (off <= bestPixels && depth < bestDepth) {
                        best = player;
                        bestPixels = off;
                        bestDepth = depth;
                     }
                  }
               }
            }

            if (best == null) {
               double bestDist = 32.0;

               for (class_864 entityx : this.preview.getSession().getWorld().method_3737()) {
                  if (entityx instanceof class_988 && entityx != client.field_10310) {
                     class_988 player = (class_988)entityx;
                     double dx = player.field_3252 - ox;
                     double dy = player.field_3253 + player.method_2544() - oy;
                     double dz = player.field_3254 - oz;
                     double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                     if (!(dist < 0.1) && !(dist > bestDist)) {
                        double dot = (dx * fx + dy * fy + dz * fz) / dist;
                        if (!(dot < 0.966)) {
                           best = player;
                           bestDist = dist;
                        }
                     }
                  }
               }
            }

            if (best != null) {
               controller.setMode(2);
               controller.setTargetEntity(best.method_8365());
               this.targetIndex = best.method_8365();
               this.message = "目标玩家：" + (best.method_8429() == null ? "#" + best.method_8365() : best.method_8429().getName());
            } else {
               this.message = "点到的位置没有玩家（对准人身上再点，32 格内）。";
            }
         }
      }
   }

   @Override
   protected void method_4259(int mx, int my, int button, long clickedAt) {
      if (this.draggingCamera) {
         CameraController controller = this.camera();
         if (controller != null) {
            controller.rotateFree((mx - this.dragX) * 0.35F, (my - this.dragY) * 0.35F);
            this.dragX = mx;
            this.dragY = my;
            this.syncCameraFields();
         }
      }

      super.method_4259(mx, my, button, clickedAt);
   }

   @Override
   protected void method_1024(char ch, int code) {
      if (this.openFrames >= 2) {
         if (this.pendingLeave != 0) {
            if (code == 1) {
               this.pendingLeave = 0;
            }
         } else if (this.focusField < 0) {
            if (code == 1) {
               this.panelVisible = false;
               this.setGrabbed(true);
               this.message = "面板已收起，按 T 重新呼出。";
            } else if (code == 20) {
               this.panelVisible = !this.panelVisible;
               this.setGrabbed(!this.panelVisible);
               this.message = this.panelVisible ? "已呼出剪辑面板和鼠标。" : "面板已收起。";
            } else if (code == 63) {
               this.cyclePerspective();
            } else {
               if (code == 59) {
                  class_1600 client = class_1600.method_2965();
                  if (client != null && client.field_3823 != null) {
                     client.field_3823.field_948 = !client.field_3823.field_948;
                     this.message = client.field_3823.field_948 ? "HUD 已隐藏。" : "HUD 已显示。";
                  }
               }
            }
         } else {
            Md3TextField field = this.fields[this.focusField];
            if (code == 1 || code == 28) {
               this.focusField = -1;
            } else if (code == 14) {
               field.backspace();
            } else if (ch >= '0' && ch <= '9' || ch == '.' || ch == '-') {
               field.typeChar(ch);
            }
         }
      }
   }

   private void cyclePerspective() {
      class_1600 client = class_1600.method_2965();
      if (client != null && client.field_3823 != null) {
         long now = System.currentTimeMillis();
         if (now - this.lastPerspectiveMs >= 250L) {
            this.lastPerspectiveMs = now;
            client.field_3823.field_949 = (client.field_3823.field_949 + 1) % 3;
            CameraController controller = this.camera();
            if (controller != null) {
               controller.setPerspective(client.field_3823.field_949);
            }

            this.message = client.field_3823.field_949 == 0 ? "第一人称" : (client.field_3823.field_949 == 1 ? "第三人称（背后）" : "第三人称（正面）");
         }
      }
   }

   private void action(int id) {
      CameraController controller = this.camera();
      if (id == 1) {
         this.preview.setPaused(!this.preview.isPaused());
         this.message = this.preview.isPaused() ? "已暂停，可以调整相机。" : "继续播放。";
      } else if (id == 2) {
         this.preview.seekToSeconds(this.preview.getCurrentSeconds() - 5.0);
      } else if (id == 3) {
         this.preview.seekToSeconds(this.preview.getCurrentSeconds() + 5.0);
      } else if (id == 10) {
         if (controller != null) {
            controller.setMode(0);
         }

         this.message = "切到玩家视角（录制时的自己）。";
      } else if (id == 11) {
         if (controller != null) {
            if (controller.getMode() != 1) {
               controller.resetFreeFromPlayer();
            }

            if (this.applyCameraFields(controller)) {
               controller.setMode(1);
               this.message = "自由相机：拖动空白处转视角。";
            } else {
               controller.setMode(1);
               this.message = "自由相机（相机数值无效，已忽略）。";
            }
         }
      } else if (id == 12) {
         this.cycleTarget(controller);
      } else if (id == 20) {
         if (controller != null) {
            if (controller.getMode() != 1 || this.applyCameraFields(controller)) {
               double time = this.preview.getCurrentSeconds();
               controller.addCurrentKeyframe(time);
               this.pathDirty = true;
               this.message = String.format(Locale.ROOT, "已在 %.2f 秒添加关键帧（%s）。", time, this.modeName());
            }
         }
      } else if (id == 21) {
         if (controller != null) {
            controller.clearPath();
            this.pathDirty = true;
         }

         this.message = "关键帧轨道已清空。";
      } else if (id != 22) {
         if (id == 32) {
            this.preview.seekToSeconds(this.trimStart());
            this.message = "已跳到片段起点。";
         } else if (id == 33) {
            this.preview.seekToSeconds(this.trimEnd());
            this.message = "已跳到片段终点。";
         } else if (id == 4) {
            if (this.pathDirty) {
               this.pendingLeave = 2;
            } else {
               this.renderClip(controller);
            }
         } else if (id == 5) {
            if (this.pathDirty) {
               this.pendingLeave = 1;
            } else {
               this.doExit();
            }
         } else if (id == 50) {
            if (controller != null && this.applyCameraFields(controller)) {
               this.message = "相机坐标已应用。";
            }
         } else {
            if (id >= 40 && id < 50) {
               if (controller == null) {
                  return;
               }

               if (controller.getMode() != 1) {
                  controller.setMode(1);
               }

               if (id == 40) {
                  controller.rotateFree(-12.0F, 0.0F);
               } else if (id == 41) {
                  controller.rotateFree(12.0F, 0.0F);
               } else if (id == 42) {
                  controller.rotateFree(0.0F, -12.0F);
               } else if (id == 43) {
                  controller.rotateFree(0.0F, 12.0F);
               } else if (id == 44) {
                  this.nudge(controller, 1.0, 0.0, 0.0);
               } else if (id == 45) {
                  this.nudge(controller, -1.0, 0.0, 0.0);
               } else if (id == 46) {
                  this.nudge(controller, 0.0, -1.0, 0.0);
               } else if (id == 47) {
                  this.nudge(controller, 0.0, 1.0, 0.0);
               } else if (id == 48) {
                  this.nudge(controller, 0.0, 0.0, 1.0);
               } else if (id == 49) {
                  this.nudge(controller, 0.0, 0.0, -1.0);
               }

               this.syncCameraFields();
               this.message = String.format(Locale.ROOT, "自由相机 X %.2f Y %.2f Z %.2f", controller.getFreeX(), controller.getFreeY(), controller.getFreeZ());
            }
         }
      } else if (controller != null) {
         double start = this.trimStart();
         double end = this.trimEnd();
         boolean hasKeys = controller.hasPath();
         double total = this.preview == null ? 0.0 : this.preview.getTotalSeconds();
         boolean hasTrim = start > 0.05 || total > 0.0 && end < total - 0.05;
         if (!hasKeys && !hasTrim) {
            this.message = "还没有关键帧或片段范围可保存。";
         } else {
            controller.setTrimRange(start, end);
            if (this.preview != null) {
               controller.setPlayhead(this.preview.getCurrentSeconds());
            }

            controller.savePath();
            this.pathDirty = false;
            this.message = "已保存："
               + (hasKeys ? "关键帧 " + this.keyCount() + " 个" : "无关键帧")
               + " · 片段 "
               + fmt(start)
               + "s → "
               + fmt(end)
               + "s → "
               + controller.getPathFile().getName();
         }
      }
   }

   private void nudge(CameraController controller, double forward, double strafe, double up) {
      for (int i = 0; i < 3; i++) {
         controller.moveFree(forward, strafe, up);
      }
   }

   private void doExit() {
      if (this.preview != null) {
         this.preview.stop();
      }

      class_1600 client = class_1600.method_2965();
      if (client != null) {
         client.method_2928(new ReplayScreen());
      }
   }

   private void saveThenContinue() {
      CameraController controller = this.camera();
      if (controller != null) {
         controller.setTrimRange(this.trimStart(), this.trimEnd());
         controller.savePath();
      }

      this.pathDirty = false;
      int action = this.pendingLeave;
      this.pendingLeave = 0;
      if (action == 1) {
         this.doExit();
      } else if (action == 2) {
         this.renderClip(controller);
      }
   }

   private void discardThenContinue() {
      this.pathDirty = false;
      int action = this.pendingLeave;
      this.pendingLeave = 0;
      if (action == 1) {
         this.doExit();
      } else if (action == 2) {
         this.renderClip(this.camera());
      }
   }

   private boolean applyCameraFields(CameraController controller) {
      try {
         double x = Double.parseDouble(this.fields[2].text.trim());
         double y = Double.parseDouble(this.fields[3].text.trim());
         double z = Double.parseDouble(this.fields[4].text.trim());
         float yaw = Float.parseFloat(this.fields[5].text.trim());
         float pitch = Float.parseFloat(this.fields[6].text.trim());
         float fov = Float.parseFloat(this.fields[7].text.trim());
         controller.setFreeTransform(x, y, z, yaw, pitch, fov);
         return true;
      } catch (Throwable var11) {
         return false;
      }
   }

   private void cycleTarget(CameraController controller) {
      if (controller != null && this.preview != null && this.preview.getSession() != null && this.preview.getSession().getWorld() != null) {
         List<class_988> players = new ArrayList<>();

         for (class_864 entity : this.preview.getSession().getWorld().method_3737()) {
            if (entity instanceof class_988 && entity != class_1600.method_2965().field_10310) {
               players.add((class_988)entity);
            }
         }

         if (players.isEmpty()) {
            this.message = "回放里没有其他玩家。";
         } else {
            this.targetIndex = (this.targetIndex + 1) % players.size();
            class_988 target = players.get(this.targetIndex);
            controller.setMode(2);
            controller.setTargetEntity(target.method_8365());
            this.message = "目标玩家：" + (target.method_8429() == null ? "#" + target.method_8365() : target.method_8429().getName());
         }
      } else {
         this.message = "回放世界尚未加载。";
      }
   }

   private void renderClip(CameraController controller) {
      if (ReplayFfmpegMissingScreen.needsPrompt()) {
         class_1600 mc = class_1600.method_2965();
         if (mc != null) {
            mc.method_2928(new ReplayFfmpegMissingScreen(new Runnable() {
               @Override
               public void run() {
                  ReplayEditorScreen.this.renderClip(controller);
               }
            }));
         }
      } else {
         double start = this.trimStart();
         double end = this.trimEnd();
         double total = this.preview.getTotalSeconds();
         if (start >= end) {
            this.message = "片段终点必须大于起点。";
         } else {
            start = Math.min(start, total);
            end = Math.min(end, total);
            ReplayConfig config = ReplayClient.getConfig();
            if (config != null) {
               ReplayClient.LOGGER.info("[replay] editor render clip: {}s -> {}s", start, end);
               this.preview.stop();
               if (ReplayClient.startRender(
                     this.preview.getFile(),
                     config.defaultFps,
                     config.defaultWidth,
                     config.defaultHeight,
                     config.encoder,
                     (long)(start * 1.0E9),
                     (long)(end * 1.0E9),
                     controller
                  )
                  != null) {
                  this.message = "已开始导出片段，进度页见。";
               } else {
                  this.message = "导出启动失败，请查看日志。";
                  class_1600 client = class_1600.method_2965();
                  if (client != null) {
                     client.method_2928(new ReplayScreen());
                  }
               }
            }
         }
      }
   }

   private double trimStart() {
      double value = this.parse(this.fields[0], 0.0);
      double total = this.preview == null ? 0.01 : Math.max(0.01, this.preview.getTotalSeconds());
      return Math.max(0.0, Math.min(total - 0.05, value));
   }

   private double trimEnd() {
      double total = this.preview == null ? 0.01 : Math.max(0.01, this.preview.getTotalSeconds());
      double value = this.parse(this.fields[1], total);
      return Math.max(this.trimStart() + 0.05, Math.min(total, value));
   }

   private double parse(Md3TextField field, double fallback) {
      try {
         return Double.parseDouble(field.text.trim());
      } catch (Throwable var5) {
         return fallback;
      }
   }

   private static String fmt(double value) {
      return String.format(Locale.ROOT, "%.2f", value);
   }

   private void syncCameraFields() {
      CameraController controller = this.camera();
      if (controller != null && controller.getMode() == 1) {
         if (this.focusField < 2) {
            this.fields[2].text = fmt(controller.getFreeX());
            this.fields[3].text = fmt(controller.getFreeY());
            this.fields[4].text = fmt(controller.getFreeZ());
            this.fields[5].text = fmt(controller.getFreeYaw());
            this.fields[6].text = fmt(controller.getFreePitch());
            this.fields[7].text = fmt(controller.getFreeFov());
         }
      }
   }

   @Override
   public void method_1028(class_1600 client, int width, int height) {
      super.method_1028(client, width, height);
      CameraController controller = this.camera();
      if (controller != null) {
         if (controller.hasPath()) {
            controller.usePath(true);
         }

         double savedStart = controller.getSavedTrimStart();
         double savedEnd = controller.getSavedTrimEnd();
         if (this.fields[0].text == null || this.fields[0].text.isEmpty()) {
            this.fields[0].text = fmt(savedStart >= 0.0 ? savedStart : (this.preview == null ? 0.0 : this.preview.getStartTimeNanos() / 1.0E9));
         }

         if (this.fields[1].text == null || this.fields[1].text.isEmpty()) {
            this.fields[1].text = fmt(savedEnd > 0.0 ? savedEnd : (this.preview == null ? 0.0 : this.preview.getEndTimeNanos() / 1.0E9));
         }

         this.syncCameraFields();
      }
   }

   @Override
   public boolean method_1037() {
      return false;
   }

   @Override
   public void method_1030() {
      this.setGrabbed(false);
      super.method_1030();
   }

   private static final class Btn {
      int id;
      float x;
      float y;
      float w;
      float h;
      String label;
      String icon;
      ReplayUi.Style style;
   }
}
