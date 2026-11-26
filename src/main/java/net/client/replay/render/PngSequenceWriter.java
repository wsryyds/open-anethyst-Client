package net.client.replay.render;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

public final class PngSequenceWriter implements VideoWriter {
   private final File directory;
   private final File output;
   private final double fps;
   private final double audioOffsetSeconds;
   private final File audioFile;
   private final String ffmpegBinary;
   private final boolean keepFrames;
   private int index;
   private boolean closed;
   private String description;

   public PngSequenceWriter(File directory) throws IOException {
      this(directory, 60.0, null, new File(directory.getAbsolutePath() + ".mp4"), null, true, 0.0);
   }

   public PngSequenceWriter(File directory, double fps, File audioFile, File output, String ffmpegBinary, boolean keepFrames) throws IOException {
      this(directory, fps, audioFile, output, ffmpegBinary, keepFrames, 0.0);
   }

   public PngSequenceWriter(File directory, double fps, File audioFile, File output, String ffmpegBinary, boolean keepFrames, double audioOffsetSeconds) throws IOException {
      this.directory = directory;
      this.fps = fps;
      this.audioOffsetSeconds = audioOffsetSeconds;
      this.audioFile = audioFile;
      this.output = output;
      this.ffmpegBinary = ffmpegBinary;
      this.keepFrames = keepFrames;
      if (!directory.isDirectory() && !directory.mkdirs()) {
         throw new IOException("Could not create " + directory);
      } else {
         this.description = "PNG sequence in " + directory.getAbsolutePath();
      }
   }

   @Override
   public void writeFrame(ByteBuffer rgba, int width, int height) throws IOException {
      byte[] raw = new byte[(width * 4 + 1) * height];
      int out = 0;

      for (int y = height - 1; y >= 0; y--) {
         raw[out++] = 0;
         int rowStart = y * width * 4;

         for (int x = 0; x < width * 4; x++) {
            raw[out++] = rgba.get(rowStart + x);
         }
      }

      File file = new File(this.directory, String.format("frame_%06d.png", this.index));
      writePng(file, width, height, raw);
      this.index++;
   }

   private static void writePng(File file, int width, int height, byte[] raw) throws IOException {
      ByteArrayOutputStream compressed = new ByteArrayOutputStream();
      Deflater deflater = new Deflater(-1);

      try {
         deflater.setInput(raw);
         deflater.finish();
         byte[] buffer = new byte[8192];

         while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            compressed.write(buffer, 0, count);
         }
      } finally {
         deflater.end();
      }

      byte[] var16 = compressed.toByteArray();

      try (DataOutputStream var17 = new DataOutputStream(new FileOutputStream(file))) {
         var17.write(new byte[]{-119, 80, 78, 71, 13, 10, 26, 10});
         ByteArrayOutputStream ihdr = new ByteArrayOutputStream();
         DataOutputStream header = new DataOutputStream(ihdr);
         header.writeInt(width);
         header.writeInt(height);
         header.writeByte(8);
         header.writeByte(6);
         header.writeByte(0);
         header.writeByte(0);
         header.writeByte(0);
         header.flush();
         writeChunk(var17, "IHDR", ihdr.toByteArray());
         writeChunk(var17, "IDAT", var16);
         writeChunk(var17, "IEND", new byte[0]);
      }
   }

   private static void writeChunk(DataOutputStream out, String type, byte[] data) throws IOException {
      out.writeInt(data.length);
      byte[] typeBytes = type.getBytes("US-ASCII");
      out.write(typeBytes);
      out.write(data);
      CRC32 crc = new CRC32();
      crc.update(typeBytes);
      crc.update(data);
      out.writeInt((int)crc.getValue());
   }

   @Override
   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         if (this.index > 0) {
            if (this.output != null && this.ffmpegBinary != null && !this.ffmpegBinary.isEmpty()) {
               this.encodeVideo();
            } else {
               throw new IOException("未找到 ffmpeg：PNG 帧已保留在 " + this.directory.getAbsolutePath() + "。安装 ffmpeg 或填写 ffmpegPath 后即可自动合成 MP4。");
            }
         }
      }
   }

   private void encodeVideo() throws IOException {
      List<String> command = new ArrayList<>();
      command.add(this.ffmpegBinary);
      command.add("-y");
      command.add("-hide_banner");
      command.add("-loglevel");
      command.add("warning");
      command.add("-framerate");
      command.add(String.format(Locale.US, "%.6f", this.fps));
      command.add("-start_number");
      command.add("0");
      command.add("-i");
      command.add(new File(this.directory, "frame_%06d.png").getAbsolutePath());
      boolean withAudio = this.audioFile != null && this.audioFile.isFile() && this.audioFile.length() > 44L;
      if (withAudio) {
         if (this.audioOffsetSeconds > 0.0) {
            command.add("-ss");
            command.add(String.format(Locale.US, "%.6f", this.audioOffsetSeconds));
         }

         command.add("-i");
         command.add(this.audioFile.getAbsolutePath());
      }

      command.add("-c:v");
      command.add("libx264");
      command.add("-preset");
      command.add("veryfast");
      command.add("-crf");
      command.add("18");
      command.add("-pix_fmt");
      command.add("yuv420p");
      if (withAudio) {
         command.add("-c:a");
         command.add("aac");
         command.add("-b:a");
         command.add("128k");
      } else {
         command.add("-an");
      }

      command.add("-movflags");
      command.add("+faststart");
      command.add(this.output.getAbsolutePath());
      ProcessBuilder builder = new ProcessBuilder(command);
      builder.redirectErrorStream(true);
      File binary = new File(this.ffmpegBinary);
      String parent = binary.getParentFile() == null ? null : binary.getParentFile().getAbsolutePath();
      if (parent != null) {
         String old = builder.environment().get("LD_LIBRARY_PATH");
         builder.environment().put("LD_LIBRARY_PATH", old != null && !old.isEmpty() ? parent + ":" + old : parent);
         String oldPath = builder.environment().get("PATH");
         builder.environment().put("PATH", oldPath != null && !oldPath.isEmpty() ? parent + ":" + oldPath : parent);
      }

      Process process = builder.start();
      StringBuilder log = new StringBuilder();

      String line;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
         while ((line = reader.readLine()) != null) {
            if (log.length() < 8192) {
               log.append(line).append('\n');
            }
         }
      } catch (Throwable var14) {
      }

      try {
         int exit = process.waitFor();
         if (exit != 0) {
            throw new IOException("PNG 合成 MP4 失败（ffmpeg exit " + exit + "）：" + log.toString().trim() + "；PNG 帧保留在 " + this.directory.getAbsolutePath());
         }
      } catch (InterruptedException var12) {
         Thread.currentThread().interrupt();
         throw new IOException("等待 ffmpeg 合成时被中断", var12);
      }

      this.description = "MP4 (" + (withAudio ? "含麦克风音轨" : "无声") + ") -> " + this.output.getAbsolutePath();
      if (!this.keepFrames) {
         deleteTree(this.directory);
      }
   }

   private static void deleteTree(File file) {
      if (file != null) {
         File[] children = file.listFiles();
         if (children != null) {
            for (File child : children) {
               deleteTree(child);
            }
         }

         file.delete();
      }
   }

   @Override
   public String describe() {
      return this.description;
   }

   public int getIndex() {
      return this.index;
   }
}
