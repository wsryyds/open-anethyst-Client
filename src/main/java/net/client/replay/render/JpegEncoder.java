package net.client.replay.render;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class JpegEncoder {
   private static final int[] ZIGZAG = new int[]{
      0,
      1,
      8,
      16,
      9,
      2,
      3,
      10,
      17,
      24,
      32,
      25,
      18,
      11,
      4,
      5,
      12,
      19,
      26,
      33,
      40,
      48,
      41,
      34,
      27,
      20,
      13,
      6,
      7,
      14,
      21,
      28,
      35,
      42,
      49,
      56,
      57,
      50,
      43,
      36,
      29,
      22,
      15,
      23,
      30,
      37,
      44,
      51,
      58,
      59,
      52,
      45,
      38,
      31,
      39,
      46,
      53,
      60,
      61,
      54,
      47,
      55,
      62,
      63
   };
   private static final int[] LUMA_Q = new int[]{
      16,
      11,
      10,
      16,
      24,
      40,
      51,
      61,
      12,
      12,
      14,
      19,
      26,
      58,
      60,
      55,
      14,
      13,
      16,
      24,
      40,
      57,
      69,
      56,
      14,
      17,
      22,
      29,
      51,
      87,
      80,
      62,
      18,
      22,
      37,
      56,
      68,
      109,
      103,
      77,
      24,
      35,
      55,
      64,
      81,
      104,
      113,
      92,
      49,
      64,
      78,
      87,
      103,
      121,
      120,
      101,
      72,
      92,
      95,
      98,
      112,
      100,
      103,
      99
   };
   private static final int[] CHROMA_Q = new int[]{
      17,
      18,
      24,
      47,
      99,
      99,
      99,
      99,
      18,
      21,
      26,
      66,
      99,
      99,
      99,
      99,
      24,
      26,
      56,
      99,
      99,
      99,
      99,
      99,
      47,
      66,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99,
      99
   };
   private static final int[] DC_LUMA_BITS = new int[]{0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] DC_LUMA_VALS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
   private static final int[] DC_CHROMA_BITS = new int[]{0, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0};
   private static final int[] DC_CHROMA_VALS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
   private static final int[] AC_LUMA_BITS = new int[]{0, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125};
   private static final int[] AC_LUMA_VALS = new int[]{
      1,
      2,
      3,
      0,
      4,
      17,
      5,
      18,
      33,
      49,
      65,
      6,
      19,
      81,
      97,
      7,
      34,
      113,
      20,
      50,
      129,
      145,
      161,
      8,
      35,
      66,
      177,
      193,
      21,
      82,
      209,
      240,
      36,
      51,
      98,
      114,
      130,
      9,
      10,
      22,
      23,
      24,
      25,
      26,
      37,
      38,
      39,
      40,
      41,
      42,
      52,
      53,
      54,
      55,
      56,
      57,
      58,
      67,
      68,
      69,
      70,
      71,
      72,
      73,
      74,
      83,
      84,
      85,
      86,
      87,
      88,
      89,
      90,
      99,
      100,
      101,
      102,
      103,
      104,
      105,
      106,
      115,
      116,
      117,
      118,
      119,
      120,
      121,
      122,
      131,
      132,
      133,
      134,
      135,
      136,
      137,
      138,
      146,
      147,
      148,
      149,
      150,
      151,
      152,
      153,
      154,
      162,
      163,
      164,
      165,
      166,
      167,
      168,
      169,
      170,
      178,
      179,
      180,
      181,
      182,
      183,
      184,
      185,
      186,
      194,
      195,
      196,
      197,
      198,
      199,
      200,
      201,
      202,
      210,
      211,
      212,
      213,
      214,
      215,
      216,
      217,
      218,
      225,
      226,
      227,
      228,
      229,
      230,
      231,
      232,
      233,
      234,
      241,
      242,
      243,
      244,
      245,
      246,
      247,
      248,
      249,
      250
   };
   private static final int[] AC_CHROMA_BITS = new int[]{0, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119};
   private static final int[] AC_CHROMA_VALS = new int[]{
      0,
      1,
      2,
      3,
      17,
      4,
      5,
      33,
      49,
      6,
      18,
      65,
      81,
      7,
      97,
      113,
      19,
      34,
      50,
      129,
      8,
      20,
      66,
      145,
      161,
      177,
      193,
      9,
      35,
      51,
      82,
      240,
      21,
      98,
      114,
      209,
      10,
      22,
      36,
      52,
      225,
      37,
      241,
      23,
      24,
      25,
      26,
      38,
      39,
      40,
      41,
      42,
      53,
      54,
      55,
      56,
      57,
      58,
      67,
      68,
      69,
      70,
      71,
      72,
      73,
      74,
      83,
      84,
      85,
      86,
      87,
      88,
      89,
      90,
      99,
      100,
      101,
      102,
      103,
      104,
      105,
      106,
      115,
      116,
      117,
      118,
      119,
      120,
      121,
      122,
      130,
      131,
      132,
      133,
      134,
      135,
      136,
      137,
      138,
      146,
      147,
      148,
      149,
      150,
      151,
      152,
      153,
      154,
      162,
      163,
      164,
      165,
      166,
      167,
      168,
      169,
      170,
      178,
      179,
      180,
      181,
      182,
      183,
      184,
      185,
      186,
      194,
      195,
      196,
      197,
      198,
      199,
      200,
      201,
      202,
      210,
      211,
      212,
      213,
      214,
      215,
      216,
      217,
      218,
      226,
      227,
      228,
      229,
      230,
      231,
      232,
      233,
      234,
      242,
      243,
      244,
      245,
      246,
      247,
      248,
      249,
      250
   };
   private static final double[][] COS = new double[8][8];
   private static final int[][] DC_LUMA = buildCodes(DC_LUMA_BITS, DC_LUMA_VALS);
   private static final int[][] AC_LUMA = buildCodes(AC_LUMA_BITS, AC_LUMA_VALS);
   private static final int[][] DC_CHROMA = buildCodes(DC_CHROMA_BITS, DC_CHROMA_VALS);
   private static final int[][] AC_CHROMA = buildCodes(AC_CHROMA_BITS, AC_CHROMA_VALS);

   private JpegEncoder() {
   }

   public static byte[] encode(ByteBuffer rgba, int width, int height, int quality) throws IOException {
      int q = Math.max(1, Math.min(100, quality));
      int scale = q < 50 ? 5000 / q : 200 - 2 * q;
      int[] luma = scaled(LUMA_Q, scale);
      int[] chroma = scaled(CHROMA_Q, scale);
      int paddedW = width + 7 & -8;
      int paddedH = height + 7 & -8;
      byte[] yPlane = new byte[paddedW * paddedH];
      byte[] cbPlane = new byte[paddedW * paddedH];
      byte[] crPlane = new byte[paddedW * paddedH];
      rgba.rewind();

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int p = (y * width + x) * 4;
            int r = rgba.get(p) & 255;
            int g = rgba.get(p + 1) & 255;
            int b = rgba.get(p + 2) & 255;
            int Y = clamp((int)(0.299 * r + 0.587 * g + 0.114 * b));
            int Cb = clamp((int)(-0.168736 * r - 0.331264 * g + 0.5 * b + 128.0));
            int Cr = clamp((int)(0.5 * r - 0.418688 * g - 0.081312 * b + 128.0));
            int idx = y * paddedW + x;
            yPlane[idx] = (byte)Y;
            cbPlane[idx] = (byte)Cb;
            crPlane[idx] = (byte)Cr;
         }
      }

      for (int y = 0; y < paddedH; y++) {
         for (int x = width; x < paddedW; x++) {
            int idx = y * paddedW + x;
            int src = y * paddedW + Math.max(0, width - 1);
            yPlane[idx] = yPlane[src];
            cbPlane[idx] = cbPlane[src];
            crPlane[idx] = crPlane[src];
         }
      }

      for (int y = height; y < paddedH; y++) {
         for (int x = 0; x < paddedW; x++) {
            int idx = y * paddedW + x;
            int src = Math.max(0, height - 1) * paddedW + x;
            yPlane[idx] = yPlane[src];
            cbPlane[idx] = cbPlane[src];
            crPlane[idx] = crPlane[src];
         }
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream(width * height / 2);
      writeMarker(out, 216);
      writeMarker(out, 224);
      writeWord(out, 16);
      out.write(new byte[]{74, 70, 73, 70, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0});
      writeMarker(out, 219);
      writeWord(out, 132);
      out.write(0);

      for (int i = 0; i < 64; i++) {
         out.write(luma[ZIGZAG[i]]);
      }

      out.write(1);

      for (int i = 0; i < 64; i++) {
         out.write(chroma[ZIGZAG[i]]);
      }

      writeMarker(out, 192);
      writeWord(out, 17);
      out.write(8);
      writeWord(out, height);
      writeWord(out, width);
      out.write(3);
      out.write(1);
      out.write(17);
      out.write(0);
      out.write(2);
      out.write(17);
      out.write(1);
      out.write(3);
      out.write(17);
      out.write(1);
      writeMarker(out, 196);
      writeWord(out, 2 + 17 + DC_LUMA_VALS.length + 17 + AC_LUMA_VALS.length + 17 + DC_CHROMA_VALS.length + 17 + AC_CHROMA_VALS.length);
      writeHuffman(out, 0, DC_LUMA_BITS, DC_LUMA_VALS);
      writeHuffman(out, 16, AC_LUMA_BITS, AC_LUMA_VALS);
      writeHuffman(out, 1, DC_CHROMA_BITS, DC_CHROMA_VALS);
      writeHuffman(out, 17, AC_CHROMA_BITS, AC_CHROMA_VALS);
      writeMarker(out, 218);
      writeWord(out, 12);
      out.write(3);
      out.write(1);
      out.write(0);
      out.write(2);
      out.write(17);
      out.write(3);
      out.write(17);
      out.write(0);
      out.write(63);
      out.write(0);
      JpegEncoder.BitWriter bits = new JpegEncoder.BitWriter(out);
      int[] previous = new int[3];
      int[] block = new int[64];
      int[] temp = new int[64];

      for (int by = 0; by < paddedH; by += 8) {
         for (int bx = 0; bx < paddedW; bx += 8) {
            encodeBlock(yPlane, paddedW, bx, by, luma, block, temp, bits, DC_LUMA, AC_LUMA, previous, 0);
            encodeBlock(cbPlane, paddedW, bx, by, chroma, block, temp, bits, DC_CHROMA, AC_CHROMA, previous, 1);
            encodeBlock(crPlane, paddedW, bx, by, chroma, block, temp, bits, DC_CHROMA, AC_CHROMA, previous, 2);
         }
      }

      bits.flush();
      writeMarker(out, 217);
      return out.toByteArray();
   }

   private static void encodeBlock(
      byte[] plane,
      int stride,
      int bx,
      int by,
      int[] quant,
      int[] block,
      int[] temp,
      JpegEncoder.BitWriter bits,
      int[][] dc,
      int[][] ac,
      int[] previous,
      int component
   ) throws IOException {
      for (int y = 0; y < 8; y++) {
         for (int x = 0; x < 8; x++) {
            block[y * 8 + x] = plane[(by + y) * stride + bx + x] - 128;
         }
      }

      for (int v = 0; v < 8; v++) {
         for (int u = 0; u < 8; u++) {
            double sum = 0.0;

            for (int y = 0; y < 8; y++) {
               for (int x = 0; x < 8; x++) {
                  sum += block[y * 8 + x] * COS[u][x] * COS[v][y];
               }
            }

            double c = (u == 0 ? 1.0 / Math.sqrt(2.0) : 1.0) * (v == 0 ? 1.0 / Math.sqrt(2.0) : 1.0);
            temp[v * 8 + u] = (int)Math.round(0.25 * c * sum / quant[v * 8 + u]);
         }
      }

      int diff = temp[0] - previous[component];
      previous[component] = temp[0];
      int category = bitsFor(Math.abs(diff));
      writeHuffmanBits(bits, dc, category);
      if (category > 0) {
         bits.write(amplitude(diff, category), category);
      }

      int run = 0;

      for (int i = 1; i < 64; i++) {
         int value = temp[ZIGZAG[i]];
         if (value == 0) {
            run++;
         } else {
            while (run > 15) {
               writeHuffmanBits(bits, ac, 240);
               run -= 16;
            }

            int size = bitsFor(Math.abs(value));
            writeHuffmanBits(bits, ac, run << 4 | size);
            bits.write(amplitude(value, size), size);
            run = 0;
         }
      }

      if (run > 0) {
         writeHuffmanBits(bits, ac, 0);
      }
   }

   private static int clamp(int value) {
      return value < 0 ? 0 : Math.min(255, value);
   }

   private static int bitsFor(int value) {
      int bits = 0;

      while (value != 0) {
         bits++;
         value >>= 1;
      }

      return bits;
   }

   private static int amplitude(int value, int size) {
      return value >= 0 ? value : value + (1 << size) - 1;
   }

   private static int[] scaled(int[] base, int scale) {
      int[] out = new int[64];

      for (int i = 0; i < 64; i++) {
         out[i] = Math.max(1, Math.min(255, (base[i] * scale + 50) / 100));
      }

      return out;
   }

   private static int[][] buildCodes(int[] bits, int[] values) {
      int[][] result = new int[256][2];
      int code = 0;
      int k = 0;

      for (int len = 1; len <= 16; len++) {
         for (int i = 0; i < bits[len]; i++) {
            result[values[k]][0] = code;
            result[values[k]][1] = len;
            code++;
            k++;
         }

         code <<= 1;
      }

      return result;
   }

   private static void writeHuffmanBits(JpegEncoder.BitWriter out, int[][] table, int symbol) throws IOException {
      int[] entry = table[symbol];
      out.write(entry[0], entry[1]);
   }

   private static void writeHuffman(ByteArrayOutputStream out, int id, int[] bits, int[] values) throws IOException {
      out.write(id);

      for (int i = 1; i <= 16; i++) {
         out.write(bits[i]);
      }

      for (int value : values) {
         out.write(value);
      }
   }

   private static void writeMarker(ByteArrayOutputStream out, int marker) throws IOException {
      out.write(255);
      out.write(marker);
   }

   private static void writeWord(ByteArrayOutputStream out, int value) throws IOException {
      out.write(value >> 8 & 0xFF);
      out.write(value & 0xFF);
   }

   static {
      for (int u = 0; u < 8; u++) {
         for (int x = 0; x < 8; x++) {
            COS[u][x] = Math.cos((2 * x + 1) * u * Math.PI / 16.0);
         }
      }
   }

   private static final class BitWriter {
      private final ByteArrayOutputStream out;
      private int buffer;
      private int count;

      BitWriter(ByteArrayOutputStream out) {
         this.out = out;
      }

      void write(int value, int length) throws IOException {
         for (int i = length - 1; i >= 0; i--) {
            this.buffer = this.buffer << 1 | value >> i & 1;
            this.count++;
            if (this.count == 8) {
               this.emit(this.buffer);
            }
         }
      }

      void flush() throws IOException {
         if (this.count > 0) {
            int value = this.buffer << 8 - this.count;
            value |= (1 << 8 - this.count) - 1;
            this.emit(value);
         }
      }

      private void emit(int value) throws IOException {
         this.out.write(value & 0xFF);
         if ((value & 0xFF) == 255) {
            this.out.write(0);
         }

         this.buffer = 0;
         this.count = 0;
      }
   }
}
