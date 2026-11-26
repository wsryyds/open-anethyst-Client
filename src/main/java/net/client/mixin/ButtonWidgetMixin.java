package net.client.mixin;

import net.client.ClientConfig;
import net.client.ui.ClientTheme;
import net.client.ui.md3.render.Md3Canvas;
import net.client.ui.md3.theme.Md3Colors;
import net.client.ui.md3.theme.Md3Theme;
import net.client.ui.md3.theme.Md3Type;
import net.client.ui.md3.widget.Md3Text;
import net.minecraft.class_1600;
import net.minecraft.class_356;
import net.minecraft.class_370;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_356.class)
public class ButtonWidgetMixin {
   @Shadow
   protected int field_1049;
   @Shadow
   protected int field_1050;
   @Shadow
   public int field_1051;
   @Shadow
   public int field_1052;
   @Shadow
   public String field_1053;
   @Shadow
   public boolean field_1055;
   @Shadow
   public boolean field_1056;
   @Shadow
   protected boolean field_5056;
   private final Md3Canvas lx$canvas = new Md3Canvas();

   @Inject(method = "method_891", at = @At("HEAD"), cancellable = true, require = 0)
   private void client$flatButton(class_1600 client, int mouseX, int mouseY, CallbackInfo ci) {
      if (ClientConfig.uiOverride && this.field_1056) {
         this.field_5056 = mouseX >= this.field_1051
            && mouseY >= this.field_1052
            && mouseX < this.field_1051 + this.field_1049
            && mouseY < this.field_1052 + this.field_1050;
         Md3Theme t = ClientTheme.get();
         int guiW = client.field_3816 != null ? client.field_3816.field_1230 : client.field_3801;
         int guiH = client.field_3816 != null ? client.field_3816.field_1231 : client.field_3802;
         this.lx$canvas.begin(guiW, guiH);
         int base = !this.field_1055
            ? t.colors.surfaceContainerHighest
            : (this.field_5056 ? Md3Colors.composite(Md3Colors.alpha(t.colors.primary, 0.08F), t.colors.secondaryContainer) : t.colors.secondaryContainer);
         int content = this.field_1055 ? t.colors.onSecondaryContainer : Md3Colors.alpha(t.colors.onSurface, 0.38F);
         float radius = Math.min(10.0F, this.field_1050 / 2.0F);
         this.lx$canvas.roundRect(this.field_1051, this.field_1052, this.field_1049, this.field_1050, radius, base);
         if (!this.field_1055) {
            this.lx$canvas
               .strokeRoundRect(this.field_1051, this.field_1052, this.field_1049, this.field_1050, radius, 1.0F, Md3Colors.alpha(t.colors.onSurface, 0.1F));
         }

         class_370 tr = client.field_3814;
         String text = this.field_1053 == null ? "" : this.field_1053;
         if (tr != null) {
            text = tr.method_955(text, this.field_1049 - 28);
         }

         String fitted = fit(this.lx$canvas, text, this.field_1049 - 24.0F);
         Md3Text.centered(
            this.lx$canvas, fitted, this.field_1051 + this.field_1049 / 2.0F, this.field_1052 + this.field_1050 / 2.0F, Md3Type.LABEL_SMALL, content
         );
         this.lx$canvas.end();
         ci.cancel();
      }
   }

   private static String fit(Md3Canvas c, String text, float maxWidth) {
      if (c.textWidth(text, Md3Type.LABEL_SMALL) <= maxWidth) {
         return text;
      } else {
         int end = text.length();

         while (end > 0 && c.textWidth(text.substring(0, end) + "...", Md3Type.LABEL_SMALL) > maxWidth) {
            end--;
         }

         return text.substring(0, end) + "...";
      }
   }
}
