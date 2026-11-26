package net.client.ui.md3.render;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.ImageIO;
import net.minecraft.class_2403;
import org.lwjgl.opengl.GL11;

public final class Md3Gl {
   private Md3Gl() {
   }

   public static int upload(BufferedImage image, boolean mipmap) {
      int w = image.getWidth();
      int h = image.getHeight();
      int[] pixels = new int[w * h];
      image.getRGB(0, 0, w, h, pixels, 0, w);
      return uploadArgb(pixels, w, h, mipmap);
   }

   public static int uploadText(BufferedImage image) {
      int w = image.getWidth();
      int h = image.getHeight();
      if (isGrayscale(image)) {
         ByteBuffer buffer = ByteBuffer.allocateDirect(w * h * 4).order(ByteOrder.nativeOrder());
         byte[] gray = grayPixels(image, w, h);

         for (int i = 0; i < gray.length; i++) {
            buffer.put((byte)-1);
            buffer.put((byte)-1);
            buffer.put((byte)-1);
            buffer.put(gray[i]);
         }

         buffer.flip();
         return uploadBuffer(buffer, w, h, false, true, 9728);
      } else {
         int[] pixels = new int[w * h];
         image.getRGB(0, 0, w, h, pixels, 0, w);
         ByteBuffer buffer = ByteBuffer.allocateDirect(w * h * 4).order(ByteOrder.nativeOrder());

         for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            buffer.put((byte)(p >> 16 & 0xFF));
            buffer.put((byte)(p >> 8 & 0xFF));
            buffer.put((byte)(p & 0xFF));
            buffer.put((byte)(p >> 24 & 0xFF));
         }

         buffer.flip();
         return uploadBuffer(buffer, w, h, false, true, 9728);
      }
   }

   private static boolean isGrayscale(BufferedImage image) {
      return image.getColorModel().getColorSpace().getType() == 6;
   }

   private static byte[] grayPixels(BufferedImage image, int w, int h) {
      Raster raster = image.getRaster();
      DataBuffer db = raster.getDataBuffer();
      if (raster.getNumBands() == 1 && db instanceof DataBufferByte) {
         byte[] data = ((DataBufferByte)db).getData();
         if (data.length >= w * h) {
            return data;
         }
      }

      int[] pixels = new int[w * h];
      image.getRGB(0, 0, w, h, pixels, 0, w);
      byte[] out = new byte[w * h];

      for (int i = 0; i < pixels.length; i++) {
         out[i] = (byte)(pixels[i] >> 16 & 0xFF);
      }

      return out;
   }

   public static int uploadArgb(int[] pixels, int w, int h, boolean mipmap) {
      ByteBuffer buffer = ByteBuffer.allocateDirect(w * h * 4).order(ByteOrder.nativeOrder());

      for (int i = 0; i < pixels.length; i++) {
         int p = pixels[i];
         buffer.put((byte)(p >> 16 & 0xFF));
         buffer.put((byte)(p >> 8 & 0xFF));
         buffer.put((byte)(p & 0xFF));
         buffer.put((byte)(p >> 24 & 0xFF));
      }

      buffer.flip();
      return uploadBuffer(buffer, w, h, mipmap, true);
   }

   public static int uploadAlpha(byte[] alpha, int w, int h) {
      ByteBuffer buffer = ByteBuffer.allocateDirect(w * h * 4).order(ByteOrder.nativeOrder());

      for (int i = 0; i < alpha.length; i++) {
         buffer.put((byte)-1);
         buffer.put((byte)-1);
         buffer.put((byte)-1);
         buffer.put(alpha[i]);
      }

      buffer.flip();
      return uploadBuffer(buffer, w, h, false, true);
   }

   private static int uploadBuffer(ByteBuffer buffer, int w, int h, boolean mipmap, boolean clamp) {
      return uploadBuffer(buffer, w, h, mipmap, clamp, 9729);
   }

   private static int uploadBuffer(ByteBuffer buffer, int w, int h, boolean mipmap, boolean clamp, int magFilter) {
      int id = GL11.glGenTextures();
      class_2403.method_9839(id);
      GL11.glTexParameteri(3553, 10241, mipmap ? 9987 : 9729);
      GL11.glTexParameteri(3553, 10240, magFilter);
      GL11.glTexParameteri(3553, 10242, clamp ? '脯' : 10497);
      GL11.glTexParameteri(3553, 10243, clamp ? '脯' : 10497);
      if (mipmap) {
         GL11.glTexParameteri(3553, 33169, 1);
      }

      GL11.glTexImage2D(3553, 0, 6408, w, h, 0, 6408, 5121, buffer);
      return id;
   }

   public static void delete(int id) {
      if (id > 0) {
         GL11.glDeleteTextures(id);
      }
   }

   public static BufferedImage loadPng(String path) {
      try {
         BufferedImage var2;
         try (InputStream in = Md3Gl.class.getResourceAsStream(path)) {
            if (in == null) {
               System.err.println("[MD3] missing texture " + path);
               return null;
            }

            var2 = ImageIO.read(in);
         }

         return var2;
      } catch (Exception var6) {
         System.err.println("[MD3] failed to read " + path + ": " + var6);
         return null;
      }
   }
}
