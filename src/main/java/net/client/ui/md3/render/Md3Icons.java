package net.client.ui.md3.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Md3Icons {
   private static int texture = -1;
   private static float atlasSize = 512.0F;
   private static float cell = 64.0F;
   private static final Map<String, float[]> UV = new HashMap<>();
   private static final Map<String, float[]> OPTICAL = new HashMap<>();

   private Md3Icons() {
   }

   private static void ensure() {
      if (texture <= 0) {
         try (InputStreamReader reader = new InputStreamReader(Md3Icons.class.getResourceAsStream("/assets/md3/icon/material_icons.json"), "UTF-8")) {
            JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
            atlasSize = root.get("atlas").getAsFloat();
            cell = root.get("cell").getAsFloat();
            JsonObject icons = root.getAsJsonObject("icons");
            BufferedImage image = Md3Gl.loadPng("/assets/md3/icon/material_icons.png");

            for (Entry<String, JsonElement> e : icons.entrySet()) {
               JsonArray xy = e.getValue().getAsJsonArray();
               float x = xy.get(0).getAsFloat();
               float y = xy.get(1).getAsFloat();
               UV.put(e.getKey(), new float[]{x / atlasSize, y / atlasSize, (x + cell) / atlasSize, (y + cell) / atlasSize});
               OPTICAL.put(e.getKey(), opticalCenter(image, (int)x, (int)y, (int)cell));
            }

            texture = Md3Gl.upload(image, true);
         } catch (Exception var11) {
            System.err.println("[MD3] icon atlas failed: " + var11);
         }
      }
   }

   public static void draw(Md3Canvas canvas, String name, float x, float y, float size, int color) {
      ensure();
      if (texture > 0) {
         float[] uv = UV.get(name);
         if (uv != null) {
            float[] centre = OPTICAL.get(name);
            float drawX = x;
            float drawY = y;
            if (centre != null) {
               drawX = x + size * (0.5F - centre[0]);
               drawY = y + size * (0.5F - centre[1]);
            }

            canvas.image(texture, drawX, drawY, size, size, uv[0], uv[1], uv[2], uv[3], color);
         }
      }
   }

   private static float[] opticalCenter(BufferedImage image, int x0, int y0, int cell) {
      if (image == null) {
         return new float[]{0.5F, 0.5F};
      } else {
         int minX = cell;
         int minY = cell;
         int maxX = -1;
         int maxY = -1;

         for (int y = 0; y < cell; y++) {
            for (int x = 0; x < cell; x++) {
               int px = x0 + x;
               int py = y0 + y;
               if (px >= 0 && py >= 0 && px < image.getWidth() && py < image.getHeight()) {
                  int argb = image.getRGB(px, py);
                  if (argb >>> 24 >= 10) {
                     if (x < minX) {
                        minX = x;
                     }

                     if (y < minY) {
                        minY = y;
                     }

                     if (x > maxX) {
                        maxX = x;
                     }

                     if (y > maxY) {
                        maxY = y;
                     }
                  }
               }
            }
         }

         return maxX < 0 ? new float[]{0.5F, 0.5F} : new float[]{(minX + maxX + 1) / (2.0F * cell), (minY + maxY + 1) / (2.0F * cell)};
      }
   }

   public static boolean has(String name) {
      ensure();
      return UV.containsKey(name);
   }
}
