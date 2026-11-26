package net.client.replay.gui;

import net.client.replay.ReplayClient;
import net.client.replay.config.ReplayConfig;
import net.client.replay.record.RecordingManager;
import net.client.ui.ClientTheme;
import net.client.ui.ClientUi;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3Text;
import net.minecraft.class_1600;
import net.minecraft.class_388;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ReplayControlScreen extends class_388 {
   private static final int RECORD_KEY = 1;
   private static final int STOP_KEY = 2;
   private static final int BACK = 5;
   private final Md3Canvas canvas = new Md3Canvas();
   private final class_388 parent;
   private int capture;
   private String message = "";
   private long messageUntil;
   private float mouseX;
   private float mouseY;
   private boolean mouseDown;
   private float panelX;
   private float panelY;
   private float panelW;
   private float panelH;

   public ReplayControlScreen(class_388 parent) {
      this.parent = parent;
   }

   @Override
   public void method_1044() {
      this.panelW = Math.min(360.0F, this.field_1230 - 24.0F);
      this.panelH = Math.min(268.0F, this.field_1231 - 24.0F);
      this.panelX = (this.field_1230 - this.panelW) / 2.0F;
      this.panelY = (this.field_1231 - this.panelH) / 2.0F;
   }

   private RecordingManager recording() {
      return ReplayClient.getRecordingManager();
   }

   private static String keyName(int code) {
      if (code < 0) {
         return "未设置";
      } else {
         String name = Keyboard.getKeyName(code);
         return name != null && !name.isEmpty() ? name : "键码 " + code;
      }
   }

   private void say(String text) {
      this.message = text;
      this.messageUntil = System.currentTimeMillis() + 2600L;
   }

   @Override
   public void method_1025(int mx, int my, float tickDelta) {
      this.mouseX = mx;
      this.mouseY = my;
      Md3Theme t = ClientTheme.get();
      this.canvas.begin(this.field_1230, this.field_1231);
      ClientUi.backdrop(this.canvas, t, this.field_1230, this.field_1231, true);
      this.canvas.shadow(this.panelX, this.panelY, this.panelW, this.panelH, 20.0F, 2);
      this.canvas.roundRect(this.panelX, this.panelY, this.panelW, this.panelH, 20.0F, t.colors.surfaceContainerLow);
      this.canvas.strokeRoundRect(this.panelX, this.panelY, this.panelW, this.panelH, 20.0F, 1.0F, Md3Colors.alpha(t.colors.outlineVariant, 0.35F));
      float x = this.panelX + 14.0F;
      float w = this.panelW - 28.0F;
      float y = this.panelY + 14.0F;
      this.canvas.roundRect(x, y, 26.0F, 26.0F, 9.0F, t.colors.primaryContainer);
      this.canvas.icon("schedule", x + 5.0F, y + 5.0F, 16.0F, t.colors.onPrimaryContainer);
      Md3Text.leftMiddle(this.canvas, "回放", x + 36.0F, y + 13.0F, Md3Type.TITLE_SMALL, t.colors.onSurface);
      boolean active = this.recording() != null && this.recording().isActive();
      Md3Text.leftMiddle(
         this.canvas, active ? "录制中" : "空闲", x + 36.0F + 90.0F, y + 13.0F, Md3Type.BODY_SMALL, active ? t.colors.primary : t.colors.onSurfaceVariant
      );
      y += 40.0F;
      Md3Text.leftMiddle(this.canvas, "点一下热键行，再按下要绑定的键（Esc 取消）", x, y, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
      y += 22.0F;
      this.drawKeyRow(t, x, y, w, "开始录制", keyName(ReplayClient.getConfig().hotkeyRecord), this.capture == 1);
      y += 34.0F;
      this.drawKeyRow(t, x, y, w, "结束录制", keyName(ReplayClient.getConfig().hotkeyStop), this.capture == 2);
      y += 44.0F;
      float gap = 8.0F;
      float bw = (w - gap * 2.0F) / 3.0F;
      ReplayUi.button(
         this.canvas, t, x, y, bw, 26.0F, "开始录制", "play_arrow", ReplayUi.Style.FILLED, !active && this.canRecord(), this.hit(x, y, bw, 26.0F), this.mouseDown
      );
      ReplayUi.button(
         this.canvas, t, x + bw + gap, y, bw, 26.0F, "结束录制", "square", ReplayUi.Style.DANGER, active, this.hit(x + bw + gap, y, bw, 26.0F), this.mouseDown
      );
      ReplayUi.button(
         this.canvas,
         t,
         x + (bw + gap) * 2.0F,
         y,
         bw,
         26.0F,
         "返回",
         "arrow_back",
         ReplayUi.Style.TEXT,
         true,
         this.hit(x + (bw + gap) * 2.0F, y, bw, 26.0F),
         this.mouseDown
      );
      y += 36.0F;
      String status = this.recording() == null ? "回放子系统未加载" : (active ? this.recording().statusLine() : "进入世界后点“开始录制”，或直接按热键。");
      Md3Text.leftMiddle(this.canvas, ReplayUi.fit(this.canvas, status, Md3Type.BODY_SMALL, w), x, y, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
      if (!this.message.isEmpty() && System.currentTimeMillis() < this.messageUntil) {
         Md3Text.leftMiddle(this.canvas, ReplayUi.fit(this.canvas, this.message, Md3Type.BODY_SMALL, w), x, y + 16.0F, Md3Type.BODY_SMALL, t.colors.primary);
      }

      this.canvas.end();
   }

   private void drawKeyRow(Md3Theme t, float x, float y, float w, String label, String key, boolean capturing) {
      int base = capturing ? t.colors.primaryContainer : t.colors.surfaceContainerHigh;
      this.canvas.roundRect(x, y, w, 26.0F, 9.0F, base);
      Md3Text.leftMiddle(this.canvas, label, x + 10.0F, y + 13.0F, Md3Type.BODY_MEDIUM, capturing ? t.colors.onPrimaryContainer : t.colors.onSurface);
      String value = capturing ? "按下按键…" : key;
      Md3Text.rightMiddle(this.canvas, value, x + w - 10.0F, y + 13.0F, Md3Type.LABEL_MEDIUM, capturing ? t.colors.onPrimaryContainer : t.colors.primary);
   }

   private boolean hit(float x, float y, float w, float h) {
      return ReplayUi.hit(this.mouseX, this.mouseY, x, y, w, h);
   }

   private boolean canRecord() {
      class_1600 client = class_1600.method_2965();
      return client != null && client.field_3803 != null && client.field_10310 != null;
   }

   @Override
   protected void method_1026(int mx, int my, int button) {
      if (button != 0) {
         super.method_1026(mx, my, button);
      } else {
         this.mouseDown = true;
         float x = this.panelX + 14.0F;
         float w = this.panelW - 28.0F;
         float keyY = this.panelY + 14.0F + 40.0F + 22.0F;
         if (ReplayUi.hit(mx, my, x, keyY, w, 26.0F)) {
            this.capture = 1;
            ClientUi.click();
         } else if (ReplayUi.hit(mx, my, x, keyY + 34.0F, w, 26.0F)) {
            this.capture = 2;
            ClientUi.click();
         } else {
            float btnY = keyY + 78.0F;
            float gap = 8.0F;
            float bw = (w - gap * 2.0F) / 3.0F;
            if (ReplayUi.hit(mx, my, x, btnY, bw, 26.0F)) {
               this.startRecording();
            } else if (ReplayUi.hit(mx, my, x + bw + gap, btnY, bw, 26.0F)) {
               this.stopRecording();
            } else if (ReplayUi.hit(mx, my, x + (bw + gap) * 2.0F, btnY, bw, 26.0F)) {
               this.close();
            } else {
               super.method_1026(mx, my, button);
            }
         }
      }
   }

   private void startRecording() {
      if (this.recording() == null) {
         this.say("回放子系统未加载。");
      } else if (this.recording().isActive()) {
         this.say("已经在录制。");
      } else if (!this.canRecord()) {
         this.say("进入世界后才能录制。");
      } else {
         this.recording().start("");
         this.say("已开始录制，返回游戏后按 " + keyName(ReplayClient.getConfig().hotkeyStop) + " 结束。");
      }
   }

   private void stopRecording() {
      if (this.recording() != null && this.recording().isActive()) {
         this.recording().stop();
         this.say("已结束录制。");
      } else {
         this.say("当前没有录制任务。");
      }
   }

   @Override
   protected void method_1024(char ch, int code) {
      if (this.capture != 0) {
         if (code == 1) {
            this.capture = 0;
            this.say("已取消改键。");
         } else {
            ReplayConfig config = ReplayClient.getConfig();
            if (config != null) {
               if (this.capture == 1) {
                  config.hotkeyRecord = code;
                  this.say("开始录制热键已设为 " + keyName(code) + "。");
               } else {
                  config.hotkeyStop = code;
                  this.say("结束录制热键已设为 " + keyName(code) + "。");
               }

               config.save();
               this.capture = 0;
            }
         }
      } else if (code == 1) {
         this.close();
      } else {
         super.method_1024(ch, code);
      }
   }

   private void close() {
      class_1600 client = class_1600.method_2965();
      if (client != null) {
         client.method_2928(this.parent);
      }
   }

   @Override
   public void method_1030() {
      this.capture = 0;

      try {
         Mouse.setGrabbed(false);
      } catch (Throwable var2) {
      }
   }
}
