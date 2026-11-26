package net.client.mixin;

import java.nio.FloatBuffer;
import net.client.ClientConfig;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Type;
import net.minecraft.class_1600;
import net.minecraft.class_359;
import net.minecraft.class_370;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_370.class)
public class TextRendererMixin {
   @Inject(method = "method_9417", at = @At("HEAD"), cancellable = true)
   private void client$modernFont(String text, float x, float y, int color, boolean shadow, CallbackInfoReturnable<Integer> cir) {
      this.client$render(text, x, y, color, cir);
   }

   @Inject(method = "method_4247", at = @At("HEAD"), cancellable = true)
   private void client$modernFontInt(String text, int x, int y, int color, CallbackInfoReturnable<Integer> cir) {
      this.client$render(text, x, y, color, cir);
   }

   private void client$render(String text, float x, float y, int color, CallbackInfoReturnable<Integer> cir) {
      if (ClientConfig.modernFont && text != null && !text.isEmpty()) {
         class_1600 client = class_1600.method_2965();
         if (client != null) {
            int width = client.field_3816 != null ? client.field_3816.field_1230 : client.field_3801;
            int height = client.field_3816 != null ? client.field_3816.field_1231 : client.field_3802;
            if (ClientConfig.modernFont && (color & 16777215) == 16777215 && (color >>> 24 & 0xFF) > 0 && (color >>> 24 & 0xFF) < 64) {
               color |= -16777216;
            }

            int base = (color & 0xFF000000) == 0 ? 0xFF000000 | color : color;
            Md3Canvas canvas = new Md3Canvas();
            boolean preserve = !client$isGuiProjection()
               || client.field_3816 == null
               || client.field_3816 instanceof class_359
               || client$hasTransformedModelView();
            if (preserve) {
               canvas.beginPreserve(width, height);
            } else {
               canvas.begin(width, height);
            }

            canvas.pixelSnap = !preserve || !client$hasTransformedModelView();
            float glyphTop = y - 1.5F;
            boolean worldText = preserve && client$hasTransformedModelView();
            this.client$drawFormatted(canvas, text, x, glyphTop, base, worldText);
            if (preserve) {
               canvas.endPreserve();
            } else {
               canvas.end();
            }

            cir.setReturnValue(0);
         }
      }
   }

   private void client$drawFormatted(Md3Canvas canvas, String text, float x, float y, int base, boolean worldText) {
      Md3Type type = worldText ? Md3Type.WORLD_TEXT : Md3Type.VANILLA_TEXT;
      int current = base;
      float pen = x;
      float top = y;

      for (int i = 0; i < text.length(); i++) {
         char ch = text.charAt(i);
         if (ch == '\n') {
            pen = x;
            top += type.lineHeight;
         } else if (ch != '\r') {
            if (ch == 167 && i + 1 < text.length()) {
               char code = Character.toLowerCase(text.charAt(++i));
               if (code == 'r') {
                  current = base;
               } else {
                  int rgb = client$legacyColor(code);
                  if (rgb != -1) {
                     current = base & 0xFF000000 | rgb;
                  }
               }
            } else {
               pen += canvas.text(String.valueOf(ch), pen, top, type, current);
            }
         }
      }
   }

   private static int client$legacyColor(char code) {
      switch (code) {
         case '0':
            return 0;
         case '1':
            return 170;
         case '2':
            return 43520;
         case '3':
            return 43690;
         case '4':
            return 11141120;
         case '5':
            return 11141290;
         case '6':
            return 16755200;
         case '7':
            return 11184810;
         case '8':
            return 5592405;
         case '9':
            return 5592575;
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'A':
         case 'B':
         case 'C':
         case 'D':
         case 'E':
         case 'F':
         case 'G':
         case 'H':
         case 'I':
         case 'J':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'S':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         default:
            return -1;
         case 'a':
            return 5635925;
         case 'b':
            return 5636095;
         case 'c':
            return 16733525;
         case 'd':
            return 16733695;
         case 'e':
            return 16777045;
         case 'f':
            return 16777215;
      }
   }

   private static boolean client$isGuiProjection() {
      FloatBuffer projection = BufferUtils.createFloatBuffer(16);
      GL11.glGetFloat(2983, projection);
      return Math.abs(projection.get(0)) < 0.02F && Math.abs(projection.get(5)) < 0.02F;
   }

   private static boolean client$hasTransformedModelView() {
      FloatBuffer model = BufferUtils.createFloatBuffer(16);
      GL11.glGetFloat(2982, model);
      return Math.abs(model.get(0) - 1.0F) > 0.04F
         || Math.abs(model.get(5) - 1.0F) > 0.04F
         || Math.abs(model.get(1)) > 0.04F
         || Math.abs(model.get(4)) > 0.04F
         || Math.abs(model.get(12)) > 0.05F
         || Math.abs(model.get(13)) > 0.05F
         || Math.abs(model.get(14)) > 0.05F;
   }
}
