package net.client.replay.gui;

import java.util.Locale;
import net.client.replay.ReplayClient;
import net.client.replay.render.RenderJob;
import net.client.replay.util.ReplayPaths;
import net.client.ui.ClientTheme;
import net.client.ui.ClientUi;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3Progress;
import net.client.ui.md3.widget.Md3Text;
import net.minecraft.class_1600;
import net.minecraft.class_388;

public class ReplayProgressScreen extends class_388 {
   private final Md3Canvas canvas = new Md3Canvas();
   private final RenderJob job;
   private float cx;
   private float cy;
   private float cw;
   private float ch;
   private float btnCancelX;
   private float btnCloseX;
   private float btnY;
   private float btnW;
   private long lastNanos;
   private float dt;
   private float mouseX;
   private float mouseY;
   private boolean mouseDown;
   private int openFrames;
   private String message = "";

   public ReplayProgressScreen(RenderJob job) {
      this.job = job;
   }

   @Override
   public void method_1044() {
      this.cw = Math.min(560.0F, this.field_1230 - 24.0F);
      this.ch = Math.min(330.0F, this.field_1231 - 24.0F);
      this.cx = (this.field_1230 - this.cw) / 2.0F;
      this.cy = (this.field_1231 - this.ch) / 2.0F;
      this.btnW = (this.cw - 44.0F) / 2.0F;
      this.btnCancelX = this.cx + 16.0F;
      this.btnCloseX = this.cx + 16.0F + this.btnW + 12.0F;
      this.btnY = this.cy + this.ch - 40.0F;
      this.lastNanos = 0L;
      this.openFrames = 0;
   }

   private boolean running() {
      return this.job != null
         && !this.job.isFinished()
         && !this.job.isCancelled()
         && ReplayClient.getRenderManager() != null
         && ReplayClient.getRenderManager().getJob() == this.job;
   }

   @Override
   public void method_1025(int mx, int my, float tickDelta) {
      this.mouseX = mx;
      this.mouseY = my;
      long now = System.nanoTime();
      this.dt = this.lastNanos == 0L ? 0.05F : Math.min(0.05F, (float)(now - this.lastNanos) / 1.0E9F);
      this.lastNanos = now;
      this.openFrames++;
      Md3Theme t = ClientTheme.get();
      this.canvas.begin(this.field_1230, this.field_1231);
      ClientUi.backdrop(this.canvas, t, this.field_1230, this.field_1231, class_1600.method_2965().field_3803 != null);
      this.canvas.shadow(this.cx, this.cy, this.cw, this.ch, 24.0F, 3);
      this.canvas.roundRect(this.cx, this.cy, this.cw, this.ch, 24.0F, t.colors.surfaceContainerHigh);
      this.canvas.strokeRoundRect(this.cx, this.cy, this.cw, this.ch, 24.0F, 1.0F, Md3Colors.alpha(t.colors.outlineVariant, 0.4F));
      int percent = this.job == null ? 0 : this.job.getPercent();
      Md3Text.leftMiddle(this.canvas, "正在导出回放", this.cx + 18.0F, this.cy + 20.0F, Md3Type.TITLE_MEDIUM, t.colors.onSurface);
      String file = this.job == null ? "-" : this.job.getReplayFile().getName();
      Md3Text.leftMiddle(
         this.canvas,
         ReplayUi.fit(this.canvas, file, Md3Type.BODY_SMALL, this.cw - 36.0F),
         this.cx + 18.0F,
         this.cy + 38.0F,
         Md3Type.BODY_SMALL,
         t.colors.onSurfaceVariant
      );
      float px = this.cx + 18.0F;
      float pw = this.cw - 36.0F;
      Md3Text.leftMiddle(this.canvas, percent + "%", px, this.cy + 64.0F, Md3Type.DISPLAY_SMALL, t.colors.onSurface);
      if (this.job != null) {
         Md3Text.rightMiddle(
            this.canvas,
            String.format(Locale.ROOT, "%.1f 帧/秒", this.job.getActualFps()),
            px + pw,
            this.cy + 64.0F,
            Md3Type.BODY_MEDIUM,
            t.colors.onSurfaceVariant
         );
      }

      Md3Progress.linear(
         this.canvas, px, this.cy + 84.0F, pw, t.colors.primary, t.colors.surfaceContainerHighest, percent / 100.0F, this.job == null, (float)(now / 1.0E9)
      );
      float rowY = this.cy + 100.0F;
      float rowH = 18.0F;
      if (this.job != null) {
         this.row(t, "帧", this.job.getFrameIndex() + " / " + this.job.getTotalFrames(), rowY);
         double eta = this.job.getEtaSeconds();
         this.row(t, "预计剩余", eta < 0.0 ? "计算中…" : ReplayPaths.formatDuration(eta), rowY + rowH);
         this.row(t, "已用时", ReplayPaths.formatDuration(this.job.getElapsedMillis() / 1000.0), rowY + rowH * 2.0F);
         if (this.ch >= 300.0F) {
            this.row(t, "编码器", this.job.getEncoder().toUpperCase(Locale.ROOT), rowY + rowH * 3.0F);
            String out = this.job.getOutputDescription();
            if (out == null || out.isEmpty()) {
               out = "等待初始化…";
            }

            this.row(t, "输出", ReplayUi.fit(this.canvas, out, Md3Type.BODY_SMALL, pw - 60.0F), rowY + rowH * 4.0F);
         }
      }

      String hint = this.hint();
      if (!hint.isEmpty()) {
         int color = this.job != null && !this.job.getLastError().isEmpty() ? t.colors.error : t.colors.onSurfaceVariant;
         Md3Text.leftMiddle(
            this.canvas,
            ReplayUi.fit(this.canvas, hint, Md3Type.BODY_SMALL, pw),
            px,
            this.cy + (this.ch >= 300.0F ? 196.0F : 160.0F),
            Md3Type.BODY_SMALL,
            color
         );
      }

      boolean active = this.running();
      String cancelLabel = active ? "取消并保留" : "已结束";
      ReplayUi.button(
         this.canvas,
         t,
         this.btnCancelX,
         this.btnY,
         this.btnW,
         26.0F,
         cancelLabel,
         active ? "stop" : "check",
         ReplayUi.Style.TONAL,
         active,
         ReplayUi.hit(mx, my, this.btnCancelX, this.btnY, this.btnW, 26.0F),
         this.mouseDown
      );
      ReplayUi.button(
         this.canvas,
         t,
         this.btnCloseX,
         this.btnY,
         this.btnW,
         26.0F,
         "返回控制台",
         "arrow_back",
         ReplayUi.Style.FILLED,
         !active,
         ReplayUi.hit(mx, my, this.btnCloseX, this.btnY, this.btnW, 26.0F),
         this.mouseDown
      );
      if (!this.message.isEmpty()) {
         Md3Text.leftMiddle(
            this.canvas,
            ReplayUi.fit(this.canvas, this.message, Md3Type.BODY_SMALL, pw),
            px,
            this.cy + (this.ch >= 300.0F ? 210.0F : 172.0F),
            Md3Type.BODY_SMALL,
            t.colors.primary
         );
      }

      this.canvas.end();
   }

   private void row(Md3Theme t, String label, String value, float y) {
      Md3Text.leftMiddle(this.canvas, label, this.cx + 18.0F, y, Md3Type.BODY_SMALL, t.colors.onSurfaceVariant);
      Md3Text.rightMiddle(this.canvas, value, this.cx + this.cw - 18.0F, y, Md3Type.BODY_SMALL, t.colors.onSurface);
   }

   private String hint() {
      if (this.job == null) {
         return "";
      } else if (!this.job.getLastError().isEmpty()) {
         String error = this.job.getLastError();
         if (error.contains("回放没有成功创建世界") || error.contains("回放世界加载失败")) {
            return "回放世界创建失败，请查看日志；与编码器无关。";
         } else {
            return "png".equalsIgnoreCase(this.job.getEncoder()) ? "PNG 模式写入失败，请检查 exports 目录权限。" : "导出失败：" + error;
         }
      } else if (this.job.isFinished()) {
         return this.job.isCancelled() ? "已取消，已经渲染的部分保留在输出目录。" : "导出完成，可以关闭此页面。";
      } else {
         return "导出期间请保持此页面打开；Esc 会取消并保留已渲染部分。";
      }
   }

   @Override
   protected void method_1026(int mx, int my, int button) {
      if (this.openFrames >= 3 && button == 0) {
         this.mouseX = mx;
         this.mouseY = my;
         this.mouseDown = true;
         if (ReplayUi.hit(mx, my, this.btnCancelX, this.btnY, this.btnW, 26.0F) && this.running()) {
            ReplayClient.getRenderManager().stop();
            this.message = "已取消导出，已渲染部分会保留。";
            ClientUi.click();
         } else {
            if (ReplayUi.hit(mx, my, this.btnCloseX, this.btnY, this.btnW, 26.0F) && !this.running()) {
               this.closeToConsole();
            }
         }
      }
   }

   @Override
   protected void method_1032(int mx, int my, int button) {
      this.mouseDown = false;
      super.method_1032(mx, my, button);
   }

   @Override
   protected void method_1024(char ch, int code) {
      if (code == 1) {
         if (this.running()) {
            ReplayClient.getRenderManager().stop();
            this.message = "已取消导出，已渲染部分会保留。";
            this.closeToConsole();
         } else {
            this.closeToConsole();
         }
      }
   }

   private void closeToConsole() {
      class_1600 client = class_1600.method_2965();
      if (client != null) {
         client.method_2928(new ReplayScreen());
      }
   }

   @Override
   public void method_1030() {
      if (this.job != null && (this.job.isFinished() || this.job.isCancelled())) {
         this.job.dispose();
      }

      super.method_1030();
   }

   @Override
   public boolean method_1037() {
      return false;
   }
}
