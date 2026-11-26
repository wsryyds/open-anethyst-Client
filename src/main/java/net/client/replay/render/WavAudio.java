package net.client.replay.render;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class WavAudio {
   public final byte[] pcm;
   public final int channels;
   public final int sampleRate;
   public final int bits;
   public final int blockAlign;
   public final int frames;

   private WavAudio(byte[] pcm, int channels, int sampleRate, int bits, int blockAlign) {
      this.pcm = pcm;
      this.channels = channels;
      this.sampleRate = sampleRate;
      this.bits = bits;
      this.blockAlign = blockAlign;
      this.frames = blockAlign <= 0 ? 0 : pcm.length / blockAlign;
   }

   public static WavAudio read(File file) {
      if (file != null && file.isFile()) {
         try {
            WavAudio var22;
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
               if (raf.length() < 12L) {
                  return null;
               }

               byte[] riff = new byte[4];
               raf.readFully(riff);
               if (!"RIFF".equals(new String(riff, "US-ASCII"))) {
                  return null;
               }

               raf.readInt();
               byte[] wave = new byte[4];
               raf.readFully(wave);
               if (!"WAVE".equals(new String(wave, "US-ASCII"))) {
                  return null;
               }

               int channels = 2;
               int sampleRate = 44100;
               int bits = 16;
               int blockAlign = 4;
               byte[] pcm = null;

               while (raf.getFilePointer() + 8L <= raf.length()) {
                  byte[] id = new byte[4];
                  raf.readFully(id);
                  int size = readIntLE(raf);
                  String name = new String(id, "US-ASCII");
                  long next = raf.getFilePointer() + size;
                  if ("fmt ".equals(name) && size >= 16) {
                     int format = readShortLE(raf);
                     channels = readShortLE(raf);
                     sampleRate = readIntLE(raf);
                     raf.skipBytes(6);
                     bits = readShortLE(raf);
                     blockAlign = Math.max(1, channels * Math.max(1, bits / 8));
                     if (format != 1) {
                        return null;
                     }
                  } else if ("data".equals(name)) {
                     int length = (int)Math.min((long)size, raf.length() - raf.getFilePointer());
                     pcm = new byte[length];
                     raf.readFully(pcm);
                     break;
                  }

                  if (next > raf.length()) {
                     break;
                  }

                  raf.seek(next + (size & 1));
               }

               if (pcm == null) {
                  return null;
               }

               var22 = new WavAudio(pcm, Math.max(1, channels), sampleRate, bits, blockAlign);
            }

            return var22;
         } catch (Throwable var18) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static int readIntLE(RandomAccessFile raf) throws IOException {
      int b0 = raf.read();
      int b1 = raf.read();
      int b2 = raf.read();
      int b3 = raf.read();
      if (b3 < 0) {
         throw new IOException("eof");
      } else {
         return b0 | b1 << 8 | b2 << 16 | b3 << 24;
      }
   }

   private static short readShortLE(RandomAccessFile raf) throws IOException {
      int b0 = raf.read();
      int b1 = raf.read();
      if (b1 < 0) {
         throw new IOException("eof");
      } else {
         return (short)(b0 | b1 << 8);
      }
   }
}
