package net.client.ui.md3.render;

import java.util.HashMap;
import java.util.Map;

public final class Md3Shapes {
   private static final Map<Integer, Integer> FILL = new HashMap<>();
   private static final Map<Integer, Integer> STROKE = new HashMap<>();
   private static final Map<Long, Integer> ARC = new HashMap<>();
   private static int glowTexture = -1;

   private Md3Shapes() {
   }

   public static int fillCorner(int radiusPx) {
      Integer cached = FILL.get(radiusPx);
      if (cached != null) {
         return cached;
      } else {
         int id = Md3Gl.uploadAlpha(makeQuadrant(radiusPx, 0), radiusPx, radiusPx);
         FILL.put(radiusPx, id);
         return id;
      }
   }

   public static int strokeCorner(int radiusPx, int strokePx) {
      int key = radiusPx << 12 | Math.min(strokePx, 4095);
      Integer cached = STROKE.get(key);
      if (cached != null) {
         return cached;
      } else {
         int id = Md3Gl.uploadAlpha(makeQuadrant(radiusPx, strokePx), radiusPx, radiusPx);
         STROKE.put(key, id);
         return id;
      }
   }

   public static int arc(int radiusPx, int thicknessPx, int sweepDegrees) {
      int sweep = Math.max(1, Math.min(359, sweepDegrees));
      long key = (long)radiusPx << 32 | (long)thicknessPx << 16 | sweep;
      Integer cached = ARC.get(key);
      if (cached != null) {
         return cached;
      } else {
         int r = Math.max(2, radiusPx);
         int t = Math.max(1, Math.min(thicknessPx, r));
         int n = r * 2 + 2;
         int cx = r + 1;
         int cy = r + 1;
         int ss = 5;
         byte[] alpha = new byte[n * n];

         for (int py = 0; py < n; py++) {
            for (int px = 0; px < n; px++) {
               int hits = 0;

               for (int sy = 0; sy < ss; sy++) {
                  for (int sx = 0; sx < ss; sx++) {
                     double x = px + (sx + 0.5) / ss - cx;
                     double y = py + (sy + 0.5) / ss - cy;
                     double d = Math.sqrt(x * x + y * y);
                     double ang = Math.toDegrees(Math.atan2(y, x));
                     if (ang < 0.0) {
                        ang += 360.0;
                     }

                     if (d <= r && d >= r - t && ang <= sweep) {
                        hits++;
                     }
                  }
               }

               alpha[py * n + px] = (byte)(hits * 255 / (ss * ss));
            }
         }

         int id = Md3Gl.uploadAlpha(alpha, n, n);
         ARC.put(key, id);
         return id;
      }
   }

   public static int glow() {
      if (glowTexture > 0) {
         return glowTexture;
      } else {
         int n = 128;
         byte[] alpha = new byte[n * n];

         for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
               float dx = (x + 0.5F) / n * 2.0F - 1.0F;
               float dy = (y + 0.5F) / n * 2.0F - 1.0F;
               float d = (float)Math.sqrt(dx * dx + dy * dy);
               float a = Math.max(0.0F, 1.0F - d);
               a = a * a * (3.0F - 2.0F * a);
               alpha[y * n + x] = (byte)(a * 255.0F);
            }
         }

         glowTexture = Md3Gl.uploadAlpha(alpha, n, n);
         return glowTexture;
      }
   }

   private static byte[] makeQuadrant(int radius, int stroke) {
      int r = Math.max(1, radius);
      int s = Math.max(0, Math.min(stroke, r));
      int ss = 5;
      byte[] out = new byte[r * r];
      double outer = (double)r * r;
      double inner = (double)(r - s) * (r - s);

      for (int py = 0; py < r; py++) {
         for (int px = 0; px < r; px++) {
            int hits = 0;

            for (int sy = 0; sy < ss; sy++) {
               for (int sx = 0; sx < ss; sx++) {
                  double x = px + (sx + 0.5) / ss;
                  double y = py + (sy + 0.5) / ss;
                  double dx = r - x;
                  double dy = r - y;
                  double d2 = dx * dx + dy * dy;
                  boolean inside = d2 <= outer;
                  if (s > 0) {
                     inside = inside && d2 > inner;
                  }

                  if (inside) {
                     hits++;
                  }
               }
            }

            out[py * r + px] = (byte)(hits * 255 / (ss * ss));
         }
      }

      return out;
   }
}
