package net.client.replay.render;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class MjpegAviWriter implements VideoWriter {
   private final RandomAccessFile raf;
   private final File output;
   private final int width;
   private final int height;
   private final double fps;
   private final int totalFrames;
   private final WavAudio audio;
   private final int audioOffsetFrames;
   private final long riffSizePos;
   private final long avihTotalFramesPos;
   private final long moviSizePos;
   private final long moviDataStart;
   private final List<MjpegAviWriter.IndexEntry> index = new ArrayList<>();
   private final int maxAudioBytes;
   private final int audioStreams;
   private int framesWritten;
   private boolean closed;

   public MjpegAviWriter(File output, int width, int height, double fps, int totalFrames, File audioFile, double audioOffsetSeconds) throws IOException {
      this.output = output;
      this.width = width;
      this.height = height;
      this.fps = fps;
      this.totalFrames = Math.max(1, totalFrames);
      this.audio = WavAudio.read(audioFile);
      this.audioOffsetFrames = this.audio == null ? 0 : Math.max(0, (int)Math.round(audioOffsetSeconds * this.audio.sampleRate));
      this.audioStreams = this.audio == null ? 0 : 1;
      this.maxAudioBytes = this.audio == null ? 0 : Math.min(1048576, this.audio.blockAlign * Math.max(1, this.audio.sampleRate / 2));
      File parent = output.getParentFile();
      if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
         throw new IOException("Could not create " + parent);
      } else {
         this.raf = new RandomAccessFile(output, "rw");
         this.raf.setLength(0L);
         this.writeFourCC("RIFF");
         this.riffSizePos = this.raf.getFilePointer();
         this.writeInt(0);
         this.writeFourCC("AVI ");
         this.writeFourCC("LIST");
         long hdrlSizePos = this.raf.getFilePointer();
         this.writeInt(0);
         this.writeFourCC("hdrl");
         this.writeFourCC("avih");
         this.writeInt(56);
         this.writeInt((int)Math.round(1000000.0 / fps));
         this.writeInt(0);
         this.writeInt(0);
         this.writeInt(this.audio == null ? 16 : 272);
         this.avihTotalFramesPos = this.raf.getFilePointer();
         this.writeInt(totalFrames);
         this.writeInt(0);
         this.writeInt(1 + this.audioStreams);
         this.writeInt(Math.max(width * height * 3, 1024));
         this.writeInt(width);
         this.writeInt(height);

         for (int i = 0; i < 4; i++) {
            this.writeInt(0);
         }

         this.writeFourCC("LIST");
         long strlSizePos = this.raf.getFilePointer();
         this.writeInt(0);
         this.writeFourCC("strl");
         this.writeFourCC("strh");
         this.writeInt(56);
         this.writeFourCC("vids");
         this.writeFourCC("MJPG");
         this.writeInt(0);
         this.writeShort(0);
         this.writeShort(0);
         this.writeInt(0);
         this.writeInt(1000);
         this.writeInt((int)Math.round(fps * 1000.0));
         this.writeInt(0);
         this.writeInt(totalFrames);
         this.writeInt(Math.max(width * height * 3, 1024));
         this.writeInt(0);
         this.writeInt(0);
         this.writeShort(0);
         this.writeShort(0);
         this.writeShort(width);
         this.writeShort(height);
         this.writeFourCC("strf");
         this.writeInt(40);
         this.writeInt(40);
         this.writeInt(width);
         this.writeInt(height);
         this.writeShort(1);
         this.writeShort(24);
         this.writeFourCC("MJPG");
         this.writeInt(width * height * 3);
         this.writeInt(0);
         this.writeInt(0);
         this.writeInt(0);
         this.writeInt(0);
         this.patchListSize(strlSizePos);
         if (this.audio != null) {
            this.writeFourCC("LIST");
            long astrlSizePos = this.raf.getFilePointer();
            this.writeInt(0);
            this.writeFourCC("strl");
            this.writeFourCC("strh");
            this.writeInt(56);
            this.writeFourCC("auds");
            this.writeInt(0);
            this.writeInt(0);
            this.writeShort(0);
            this.writeShort(0);
            this.writeInt(0);
            this.writeInt(1);
            this.writeInt(this.audio.sampleRate);
            this.writeInt(0);
            this.writeInt(this.audio.frames);
            this.writeInt(this.maxAudioBytes);
            this.writeInt(0);
            this.writeInt(this.audio.blockAlign);
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(this.audio.channels);
            this.writeShort(this.audio.bits);
            this.writeFourCC("strf");
            this.writeInt(16);
            this.writeShort(1);
            this.writeShort(this.audio.channels);
            this.writeInt(this.audio.sampleRate);
            this.writeInt(this.audio.sampleRate * this.audio.blockAlign);
            this.writeShort(this.audio.blockAlign);
            this.writeShort(this.audio.bits);
            this.patchListSize(astrlSizePos);
         }

         this.patchListSize(hdrlSizePos);
         this.writeFourCC("LIST");
         this.moviSizePos = this.raf.getFilePointer();
         this.writeInt(0);
         this.writeFourCC("movi");
         this.moviDataStart = this.raf.getFilePointer();
         this.closed = false;
      }
   }

   @Override
   public void writeFrame(ByteBuffer rgba, int width, int height) throws IOException {
      if (this.closed) {
         throw new IOException("AVI already closed");
      } else if (width == this.width && height == this.height) {
         byte[] jpeg = JpegEncoder.encode(rgba, width, height, 82);
         long offset = this.raf.getFilePointer() - this.moviDataStart;
         this.writeChunk("00dc", jpeg);
         this.index.add(new MjpegAviWriter.IndexEntry("00dc", 16, offset, jpeg.length));
         if (this.audio != null) {
            int start = this.audioOffsetFrames + (int)((long)this.framesWritten * this.audio.sampleRate / this.fps);
            int end = this.audioOffsetFrames + (int)((long)(this.framesWritten + 1) * this.audio.sampleRate / this.fps);
            start = Math.max(0, Math.min(this.audio.frames, start));
            end = Math.max(start, Math.min(this.audio.frames, end));
            int bytes = (end - start) * this.audio.blockAlign;
            if (bytes > 0) {
               byte[] pcm = new byte[bytes];
               System.arraycopy(this.audio.pcm, start * this.audio.blockAlign, pcm, 0, bytes);
               long audioOffset = this.raf.getFilePointer() - this.moviDataStart;
               this.writeChunk("01wb", pcm);
               this.index.add(new MjpegAviWriter.IndexEntry("01wb", 0, audioOffset, pcm.length));
            }
         }

         this.framesWritten++;
      } else {
         throw new IOException("AVI frame size changed");
      }
   }

   @Override
   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         long moviEnd = this.raf.getFilePointer();
         this.writeFourCC("idx1");
         this.writeInt(this.index.size() * 16);

         for (MjpegAviWriter.IndexEntry entry : this.index) {
            this.writeFourCC(entry.id);
            this.writeInt(entry.flags);
            this.writeInt((int)entry.offset);
            this.writeInt(entry.size);
         }

         this.patchListSize(this.moviSizePos);
         long end = this.raf.getFilePointer();
         this.raf.seek(this.riffSizePos);
         this.writeInt((int)(end - 8L));
         this.raf.seek(this.avihTotalFramesPos);
         this.writeInt(this.framesWritten);
         this.raf.getFD().sync();
         this.raf.close();
      }
   }

   @Override
   public String describe() {
      String source = this.audio == null ? "无声" : "PCM 游戏音轨";
      return "MJPEG AVI (" + source + ") -> " + this.output.getAbsolutePath();
   }

   private void writeChunk(String id, byte[] data) throws IOException {
      this.writeFourCC(id);
      this.writeInt(data.length);
      this.raf.write(data);
      if ((data.length & 1) != 0) {
         this.raf.write(0);
      }
   }

   private void patchListSize(long sizePos) throws IOException {
      long end = this.raf.getFilePointer();
      this.raf.seek(sizePos);
      this.writeInt((int)(end - sizePos - 4L));
      this.raf.seek(end);
   }

   private void writeFourCC(String s) throws IOException {
      this.raf.write(s.getBytes(StandardCharsets.US_ASCII));
   }

   private void writeInt(int value) throws IOException {
      this.raf.write(value & 0xFF);
      this.raf.write(value >> 8 & 0xFF);
      this.raf.write(value >> 16 & 0xFF);
      this.raf.write(value >> 24 & 0xFF);
   }

   private void writeShort(int value) throws IOException {
      this.raf.write(value & 0xFF);
      this.raf.write(value >> 8 & 0xFF);
   }

   private static final class IndexEntry {
      final String id;
      final int flags;
      final long offset;
      final int size;

      IndexEntry(String id, int flags, long offset, int size) {
         this.id = id;
         this.flags = flags;
         this.offset = offset;
         this.size = size;
      }
   }
}
