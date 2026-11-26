package net.client.replay.gui;

import net.client.replay.ReplayClient;
import net.client.replay.config.ReplayConfig;
import net.client.replay.render.FfmpegLocator;
import net.minecraft.class_1600;
import net.minecraft.class_356;
import net.minecraft.class_388;

public final class ReplayFfmpegMissingScreen extends class_388 {
   private final Runnable onProceed;

   public ReplayFfmpegMissingScreen(Runnable onProceed) {
      this.onProceed = onProceed;
   }

   public static boolean needsPrompt() {
      ReplayConfig config = ReplayClient.getConfig();
      return config != null && "ffmpeg".equals(config.encoder) ? FfmpegLocator.find() == null : false;
   }

   @Override
   public void method_1028(class_1600 client, int width, int height) {
      super.method_1028(client, width, height);
      this.field_1232.clear();
      int bw = Math.min(240, (width - 40) / 2);
      int bh = 20;
      int cx = width / 2;
      int y = height / 2 + 16;
      this.field_1232.add(new class_356(0, cx - bw - 4, y, bw, bh, "改用 PNG 帧"));
      this.field_1232.add(new class_356(1, cx + 4, y, bw, bh, "继续渲染（无 ffmpeg 出 AVI）"));
      this.field_1232.add(new class_356(2, cx - bw / 2, y + 26, bw, bh, "取消，停止渲染"));
   }

   @Override
   public void method_1025(int mouseX, int mouseY, float tickDelta) {
      this.method_1043();
      int cx = this.field_1230 / 2;
      this.method_990(this.field_1234, "未找到可用的 ffmpeg", cx, this.field_1231 / 2 - 56, -1);
      this.method_990(this.field_1234, "无法直接合成 MP4。请选择处理方式：", cx, this.field_1231 / 2 - 38, -4342339);
      this.method_990(this.field_1234, "· 改用 PNG 帧：输出图片序列，可自行合成", cx, this.field_1231 / 2 - 20, -6381922);
      this.method_990(this.field_1234, "· 继续渲染：直接写纯 Java MJPEG 视频（.avi）", cx, this.field_1231 / 2 - 8, -6381922);
      super.method_1025(mouseX, mouseY, tickDelta);
   }

   @Override
   protected void method_1027(class_356 button) {
      ReplayConfig config = ReplayClient.getConfig();
      if (config == null) {
         this.field_1229.method_2928(null);
      } else {
         if (button.field_1054 == 0) {
            config.encoder = "png";
            config.save();
         } else {
            if (button.field_1054 != 1) {
               this.field_1229.method_2928(null);
               return;
            }

            config.encoder = "mjpeg";
            config.save();
         }

         this.field_1229.method_2928(null);
         if (this.onProceed != null) {
            this.onProceed.run();
         }
      }
   }

   @Override
   public boolean method_1037() {
      return false;
   }
}
