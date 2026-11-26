package net.client.mixin;

import java.util.ArrayList;
import java.util.List;
import net.client.ClientConfig;
import net.client.util.HudFill;
import net.minecraft.class_1467;
import net.minecraft.class_1468;
import net.minecraft.class_1469;
import net.minecraft.class_1471;
import net.minecraft.class_1600;
import net.minecraft.class_370;
import net.minecraft.class_371;
import net.minecraft.class_389;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = class_371.class, priority = 1200)
public class InGameHudMixin {
   @Shadow
   private long field_10343;

   @Inject(method = "method_9434", at = @At("HEAD"), require = 1)
   private void client$noHeartBlink(class_389 window, CallbackInfo ci) {
      if (ClientConfig.hurtBlinkOff) {
         this.field_10343 = 0L;
      }
   }

   @Redirect(method = "method_9423", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_371;method_988(IIIII)V"))
   private void client$scoreboardBackground(int x1, int y1, int x2, int y2, int color) {
      if (ClientConfig.scoreboardBackground) {
         color = ClientConfig.scoreboardBgColor;
      }

      HudFill.fill(x1, y1, x2, y2, color);
   }

   @Inject(method = "method_9423", at = @At("HEAD"), cancellable = true, require = 0)
   private void client$hideScoreboard(class_1467 objective, class_389 window, CallbackInfo ci) {
      if (ClientConfig.hideScoreboard) {
         ci.cancel();
      } else {
         if (ClientConfig.scoreboardCustom) {
            ci.cancel();
         }
      }
   }

   @Redirect(method = "method_9423", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_370;method_4247(Ljava/lang/String;III)I"), require = 0)
   private int client$hideRedNumbers(class_370 fontRenderer, String text, int x, int y, int color) {
      if (ClientConfig.hideScoreNumbers && text != null && text.startsWith("§c")) {
         return 0;
      } else {
         int argb = ClientConfig.modernFont ? color | 0xFF000000 : color;
         return fontRenderer.method_4247(text, x, y, argb);
      }
   }

   private void client$renderCustomScoreboard(class_1467 objective, class_389 window) {
      class_1600 mc = class_1600.method_2965();
      if (mc != null && window != null) {
         class_370 font = mc.field_3814;
         if (font != null) {
            int maxLines = ClientConfig.scoreboardMaxLines;
            if (maxLines < 1) {
               maxLines = 1;
            } else if (maxLines > 15) {
               maxLines = 15;
            }

            List<String[]> rows = new ArrayList<>();
            String customText = ClientConfig.scoreboardText;
            boolean literal = customText != null && !customText.trim().isEmpty();
            if (literal) {
               String[] split = customText.replace("\r", "").split("\n");
               List<String> lines = new ArrayList<>();

               for (String raw : split) {
                  if (raw != null && !raw.trim().isEmpty()) {
                     lines.add(raw.trim());
                  }
               }

               if (lines.size() > maxLines) {
                  lines = new ArrayList<>(lines.subList(0, maxLines));
               }

               for (String line : lines) {
                  int colon = line.indexOf(58);
                  if (colon <= 0 || colon >= line.length() - 1) {
                     rows.add(new String[]{line, null});
                  } else if (ClientConfig.hideScoreNumbers) {
                     rows.add(new String[]{line.substring(0, colon).trim(), null});
                  } else {
                     rows.add(new String[]{line.substring(0, colon).trim(), line.substring(colon + 1).trim()});
                  }
               }
            } else {
               if (objective == null) {
                  return;
               }

               class_1471 scoreboard = objective.method_4845();
               List<class_1469> scores = new ArrayList<>();

               for (class_1469 score : scoreboard.method_4903(objective)) {
                  if (score != null && score.method_4872() != null && !score.method_4872().startsWith("#")) {
                     scores.add(score);
                  }
               }

               if (scores.size() > maxLines) {
                  scores = new ArrayList<>(scores.subList(scores.size() - maxLines, scores.size()));
               }

               boolean showNames = ClientConfig.scoreboardShowNames;
               boolean showScores = ClientConfig.scoreboardShowScores;
               if (!showNames && !showScores) {
                  showNames = true;
               }

               for (class_1469 scorex : scores) {
                  String name = class_1468.method_5565(scoreboard.method_4904(scorex.method_4872()), scorex.method_4872());
                  boolean wantNames = showNames;
                  boolean wantScores = showScores && !ClientConfig.hideScoreNumbers;
                  if (!showNames && !wantScores) {
                     wantNames = true;
                  }

                  if (wantNames && wantScores) {
                     rows.add(new String[]{name, this.client$scoreText(scorex)});
                  } else if (wantNames) {
                     rows.add(new String[]{name, null});
                  } else {
                     rows.add(new String[]{null, this.client$scoreText(scorex)});
                  }
               }
            }

            String title = ClientConfig.scoreboardTitle;
            if (title == null || title.trim().isEmpty()) {
               title = objective == null ? "" : objective.method_4849();
            }

            boolean showTitle = ClientConfig.scoreboardShowTitle && title != null && !title.isEmpty();
            int width = showTitle ? font.method_954(title) : 0;

            for (String[] row : rows) {
               int lineWidth = row[0] == null ? 0 : font.method_954(row[0]);
               if (row[1] != null) {
                  lineWidth += font.method_954("  ") + font.method_954(row[1]);
               }

               width = Math.max(width, lineWidth);
            }

            if (width > 0) {
               int fontHeight = font.field_1141;
               int y = window.method_1046() / 2 + rows.size() * fontHeight / 3;
               int x = window.method_1045() - width - 3;
               int right = window.method_1045() - 1;
               int bg = ClientConfig.scoreboardBackground ? ClientConfig.scoreboardBgColor : 1342177280;

               for (int i = 0; i < rows.size(); i++) {
                  String[] row = rows.get(i);
                  int lineY = y - (i + 1) * fontHeight;
                  HudFill.fill(x - 2, lineY, right, lineY + fontHeight, bg);
                  if (row[0] != null) {
                     font.method_4247(row[0], x, lineY, -1);
                  }

                  if (row[1] != null) {
                     int rightWidth = font.method_954(row[1]);
                     font.method_4247(row[1], right - rightWidth - 1, lineY, -1);
                  }
               }

               if (showTitle) {
                  int titleY = y - rows.size() * fontHeight - fontHeight - 1;
                  HudFill.fill(x - 2, titleY, right, y - rows.size() * fontHeight - 1, bg);
                  int titleX = x + (width - font.method_954(title)) / 2;
                  font.method_4247(title, titleX, titleY, -1);
               }
            }
         }
      }
   }

   private String client$customLine(class_1471 scoreboard, class_1469 score, boolean showNames, boolean showScores) {
      String name = class_1468.method_5565(scoreboard.method_4904(score.method_4872()), score.method_4872());
      if (showNames && showScores) {
         return name;
      } else {
         return showScores ? this.client$scoreText(score) : name;
      }
   }

   private String client$scoreText(class_1469 score) {
      return "§c" + score.method_4869();
   }

   @Inject(method = "method_9420", at = @At("TAIL"))
   private void client$renderCustomScoreboardEarly(float tickDelta, CallbackInfo ci) {
      class_1600 mc = class_1600.method_2965();
      if (ClientConfig.scoreboardCustom
         && !ClientConfig.hideScoreboard
         && mc != null
         && mc.field_3803 != null
         && mc.field_10310 != null
         && !mc.field_3823.field_948) {
         class_1467 objective = mc.field_3803.method_4711().method_4875(1);
         this.client$renderCustomScoreboard(objective, new class_389(mc));
      }
   }

   @Inject(method = "method_9429", at = @At("HEAD"), cancellable = true)
   private void client$hideVanillaCrosshair(CallbackInfoReturnable<Boolean> cir) {
      if (ClientConfig.customCrosshair) {
         cir.setReturnValue(false);
      }
   }

   @Inject(method = "method_9420", at = @At("TAIL"))
   private void client$drawCrosshair(float delta, CallbackInfo ci) {
      if (ClientConfig.customCrosshair) {
         class_1600 client = class_1600.method_2965();
         if (client != null && client.field_3803 != null && client.field_10310 != null && !client.field_3823.field_948) {
            String[] rows = ClientConfig.crosshairMask.split("/");
            if (rows.length == 9) {
               class_389 window = new class_389(client);
               int cx = window.method_1045() / 2 - 4;
               int cy = window.method_1046() / 2 - 4;

               for (int r = 0; r < 9; r++) {
                  String row = rows[r];

                  for (int c = 0; c < 9 && c < row.length(); c++) {
                     if (row.charAt(c) == '1') {
                        HudFill.fill(cx + c, cy + r, cx + c + 1, cy + r + 1, ClientConfig.crosshairColor);
                     }
                  }
               }
            }
         }
      }
   }
}
