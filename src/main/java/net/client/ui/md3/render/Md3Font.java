package net.client.ui.md3.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.client.ui.md3.theme.Md3Type;
import org.lwjgl.opengl.GL11;

public final class Md3Font {
   public final float ascent;
   public final float descent;
   public final float lineHeight;
   public final float supersample;
   public final float renderScale;
   public final int texture;
   public final Map<Character, Md3Font.Glyph> glyphs = new HashMap<>();
   public float targetSize = 1.0F;
   public Md3Font fallback;
   private static final Map<String, Integer> TEXTURES = new HashMap<>();

   private Md3Font(float ascent, float descent, float lineHeight, float supersample, float renderScale, int texture) {
      this.ascent = ascent;
      this.descent = descent;
      this.lineHeight = lineHeight;
      this.supersample = supersample;
      this.renderScale = renderScale;
      this.texture = texture;
   }

   public static Md3Font load(Md3Type type) {
      String base = "/assets/md3/font/" + type.atlasKey();

      try {
         Md3Font var22;
         try (InputStreamReader reader = new InputStreamReader(Md3Font.class.getResourceAsStream(base + ".json"), "UTF-8")) {
            JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
            float atlas = root.get("atlas").getAsFloat();
            float ss = root.get("supersample").getAsFloat();
            Integer cached = TEXTURES.get(base);
            int tex;
            if (cached != null) {
               tex = cached;
            } else {
               BufferedImage image = Md3Gl.loadPng(base + ".png");
               if (image == null) {
                  return null;
               }

               tex = Md3Gl.uploadText(image);
               TEXTURES.put(base, tex);
            }

            float atlasSize = root.has("size") ? root.get("size").getAsFloat() : type.size;
            float renderScale = atlasSize > 0.0F ? type.size / atlasSize : 1.0F;
            Md3Font font = new Md3Font(
               root.get("ascent").getAsFloat(), root.get("descent").getAsFloat(), root.get("lineHeight").getAsFloat(), ss, renderScale, tex
            );
            font.targetSize = type.size;
            JsonObject glyphs = root.getAsJsonObject("glyphs");

            for (Entry<String, JsonElement> e : glyphs.entrySet()) {
               String key = e.getKey();
               if (!key.isEmpty() && key.length() <= 1) {
                  JsonObject g = e.getValue().getAsJsonObject();
                  if (!g.has("x")) {
                     float adv = g.get("adv").getAsFloat();
                     font.glyphs.put(key.charAt(0), new Md3Font.Glyph(0, 0, 0, 0, 0.0F, 0.0F, adv, atlas));
                  } else {
                     font.glyphs
                        .put(
                           key.charAt(0),
                           new Md3Font.Glyph(
                              g.get("x").getAsInt(),
                              g.get("y").getAsInt(),
                              g.get("w").getAsInt(),
                              g.get("h").getAsInt(),
                              g.get("ox").getAsFloat(),
                              g.get("oy").getAsFloat(),
                              g.get("adv").getAsFloat(),
                              atlas
                           )
                        );
                  }
               }
            }

            var22 = font;
         }

         return var22;
      } catch (Exception var19) {
         System.err.println("[MD3] font atlas failed: " + type.atlasKey() + " " + var19);
         return null;
      }
   }

   public float width(String text, float tracking) {
      float w = 0.0F;

      for (int i = 0; i < text.length(); i++) {
         char ch = text.charAt(i);
         if (ch == 167 && i + 1 < text.length()) {
            i++;
         } else {
            Md3Font.Glyph g = this.glyphs.get(ch);
            if (g == null) {
               if (this.fallback != null) {
                  Md3Font.Glyph fg = this.fallback.glyphs.get(ch);
                  if (fg != null) {
                     w += fg.advance * this.fallback.renderScale * shift(this.fallback, this) + tracking;
                  }
               }
            } else {
               w += g.advance * this.renderScale + tracking;
            }
         }
      }

      return Math.max(0.0F, w - tracking);
   }

   private static float shift(Md3Font fb, Md3Font host) {
      return fb.targetSize > 0.0F ? host.targetSize / fb.targetSize : 1.0F;
   }

   private static void glyph(Md3Canvas canvas, Md3Font font, Md3Font.Glyph g, float rs, float pen, float yTop, int color) {
      if (canvas.pixelSnap) {
         float ps = Math.max(1.0F, canvas.pixelScale);
         float dx = Math.round((pen + g.ox * rs) * ps) / ps;
         float dy = Math.round((yTop + g.oy * rs) * ps) / ps;
         float gw = Math.max(1.0F, (float)Math.round(g.w / font.supersample * rs * ps)) / ps;
         float gh = Math.max(1.0F, (float)Math.round(g.h / font.supersample * rs * ps)) / ps;
         canvas.image(font.texture, dx, dy, gw, gh, g.u0, g.v0, g.u1, g.v1, color);
      } else {
         GL11.glBindTexture(3553, font.texture);
         GL11.glTexParameteri(3553, 10240, 9729);
         canvas.image(font.texture, pen + g.ox * rs, yTop + g.oy * rs, g.w / font.supersample * rs, g.h / font.supersample * rs, g.u0, g.v0, g.u1, g.v1, color);
         GL11.glTexParameteri(3553, 10240, 9728);
      }
   }

   public void draw(Md3Canvas canvas, String text, float x, float yTop, int color, float tracking) {
      float pen = x;

      for (int i = 0; i < text.length(); i++) {
         char ch = text.charAt(i);
         if (ch == 167 && i + 1 < text.length()) {
            i++;
         } else {
            Md3Font.Glyph g = this.glyphs.get(ch);
            if (g == null) {
               Md3Font fb = this.fallback;
               if (fb != null) {
                  Md3Font.Glyph fg = fb.glyphs.get(ch);
                  if (fg != null) {
                     float scale = fb.targetSize > 0.0F ? this.targetSize / fb.targetSize : 1.0F;
                     glyph(canvas, fb, fg, fb.renderScale * scale, pen, yTop, color);
                     pen += fg.advance * fb.renderScale * shift(fb, this) + tracking;
                  }
               }
            } else {
               if (g.w > 0) {
                  glyph(canvas, this, g, this.renderScale, pen, yTop, color);
               }

               pen += g.advance * this.renderScale + tracking;
            }
         }
      }
   }

   public static final class Glyph {
      public final int x;
      public final int y;
      public final int w;
      public final int h;
      public final float ox;
      public final float oy;
      public final float advance;
      public final float u0;
      public final float v0;
      public final float u1;
      public final float v1;

      Glyph(int x, int y, int w, int h, float ox, float oy, float advance, float atlas) {
         this.x = x;
         this.y = y;
         this.w = w;
         this.h = h;
         this.ox = ox;
         this.oy = oy;
         this.advance = advance;
         this.u0 = x / atlas;
         this.v0 = y / atlas;
         this.u1 = (x + w) / atlas;
         this.v1 = (y + h) / atlas;
      }
   }
}
