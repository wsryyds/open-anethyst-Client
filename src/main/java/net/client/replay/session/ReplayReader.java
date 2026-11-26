package net.client.replay.session;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.client.replay.record.EntitySnapshot;
import net.client.replay.record.ReplayMeta;
import net.client.replay.record.SnapshotSample;
import net.client.replay.record.SoundEventRecord;
import net.client.replay.util.ReplayPaths;
import net.minecraft.class_2025;

public final class ReplayReader implements AutoCloseable {
   public final File file;
   public final ReplayMeta meta;
   private final List<ReplayReader.PacketRecord> packets = new ArrayList<>();
   private final List<ReplayReader.SnapshotRecord> snapshots = new ArrayList<>();
   private final RandomAccessFile packetFile;
   private final RandomAccessFile snapshotFile;
   private final File audioFile;
   private final File gameAudioFile;
   private final File soundEventsFile;
   private final Map<Integer, SnapshotSample> sampleCache = new HashMap<>();

   private ReplayReader(File file, ReplayMeta meta, File packetFile, File snapshotFile, File audioFile, File gameAudioFile, File soundEventsFile) throws IOException {
      this.file = file;
      this.meta = meta;
      this.packetFile = new RandomAccessFile(packetFile, "r");
      this.snapshotFile = new RandomAccessFile(snapshotFile, "r");
      this.audioFile = audioFile;
      this.gameAudioFile = gameAudioFile;
      this.soundEventsFile = soundEventsFile;
      this.indexPackets();
      this.indexSnapshots();
   }

   public static ReplayReader open(File file) throws IOException {
      ZipFile zip = new ZipFile(file);

      ReplayReader ignored;
      try {
         ZipEntry metaEntry = zip.getEntry("meta.json");
         if (metaEntry == null) {
            throw new IOException("不是有效的回放文件：缺少 meta.json");
         }

         ReplayMeta meta;
         try (InputStream in = zip.getInputStream(metaEntry)) {
            meta = ReplayMeta.fromJson(readString(in));
         }

         EntitySnapshot.FORMAT_VERSION = meta.formatVersion >= 2 ? 2 : 1;
         File cache = new File(ReplayPaths.cacheDir(), file.getName() + "-" + file.length() + "-v" + Math.max(1, meta.formatVersion));
         if (!cache.isDirectory() && !cache.mkdirs()) {
            throw new IOException("Could not create cache " + cache);
         }

         File packetCache = new File(cache, "packets.bin");
         File snapshotCache = new File(cache, "snapshots.bin");
         extract(zip, "packets.bin", packetCache);
         extract(zip, "snapshots.bin", snapshotCache);
         File audioCache = new File(cache, "audio.wav");
         if (zip.getEntry("audio.wav") != null) {
            try {
               extract(zip, "audio.wav", audioCache);
            } catch (Throwable var21) {
               audioCache = null;
            }
         } else {
            audioCache = null;
         }

         File gameAudioCache = new File(cache, "gameaudio.wav");
         if (zip.getEntry("gameaudio.wav") != null) {
            try {
               extract(zip, "gameaudio.wav", gameAudioCache);
            } catch (Throwable var20) {
               gameAudioCache = null;
            }
         } else {
            gameAudioCache = null;
         }

         File soundsCache = new File(cache, "soundevents.bin");
         if (zip.getEntry("soundevents.bin") != null) {
            try {
               extract(zip, "soundevents.bin", soundsCache);
            } catch (Throwable var19) {
               soundsCache = null;
            }
         } else {
            soundsCache = null;
         }

         ignored = new ReplayReader(file, meta, packetCache, snapshotCache, audioCache, gameAudioCache, soundsCache);
      } finally {
         zip.close();
      }

      return ignored;
   }

   private static String readString(InputStream in) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];

      int read;
      while ((read = in.read(buffer)) >= 0) {
         out.write(buffer, 0, read);
      }

      return new String(out.toByteArray(), "UTF-8");
   }

   private static void extract(ZipFile zip, String name, File out) throws IOException {
      ZipEntry entry = zip.getEntry(name);
      if (entry == null) {
         throw new IOException("Missing " + name);
      } else {
         try (
            InputStream in = new BufferedInputStream(zip.getInputStream(entry));
            OutputStream os = new FileOutputStream(out);
         ) {
            byte[] buffer = new byte[8192];

            int read;
            while ((read = in.read(buffer)) >= 0) {
               os.write(buffer, 0, read);
            }
         }
      }
   }

   private void indexPackets() throws IOException {
      long length = this.packetFile.length();
      long pos = 0L;

      while (pos < length) {
         this.packetFile.seek(pos);
         long time = this.packetFile.readLong();
         String className = this.packetFile.readUTF();
         int payloadLength = this.packetFile.readInt();
         long payloadOffset = this.packetFile.getFilePointer();
         this.packets.add(new ReplayReader.PacketRecord(time, className, payloadOffset, payloadLength));
         pos = payloadOffset + payloadLength;
      }
   }

   private void indexSnapshots() throws IOException {
      long length = this.snapshotFile.length();
      long pos = 0L;

      while (pos < length) {
         this.snapshotFile.seek(pos);
         long time = this.snapshotFile.readLong();
         int count = this.snapshotFile.readInt();
         this.snapshots.add(new ReplayReader.SnapshotRecord(time, pos, count));
         int stride = this.meta.formatVersion >= 2 ? 142 : 92;
         pos = this.snapshotFile.getFilePointer() + (long)count * stride;
      }
   }

   public int getPacketCount() {
      return this.packets.size();
   }

   public int getSnapshotCount() {
      return this.snapshots.size();
   }

   public ReplayReader.PacketRecord packet(int index) {
      return this.packets.get(index);
   }

   public byte[] readPacketPayload(ReplayReader.PacketRecord record) throws IOException {
      byte[] data = new byte[record.payloadLength];
      this.packetFile.seek(record.payloadOffset);
      this.packetFile.readFully(data);
      return data;
   }

   public List<ReplayReader.SnapshotRecord> snapshots() {
      return this.snapshots;
   }

   public long firstPacketTime() {
      return this.packets.isEmpty() ? 0L : this.packets.get(0).timeNanos;
   }

   public long lastPacketTime() {
      return this.packets.isEmpty() ? 0L : this.packets.get(this.packets.size() - 1).timeNanos;
   }

   public boolean hasGameJoin() {
      String runtimeName = class_2025.class.getName();

      for (ReplayReader.PacketRecord record : this.packets) {
         if (record.className != null) {
            if (record.className.equals(runtimeName) || record.className.endsWith("GameJoinS2CPacket")) {
               return true;
            }

            try {
               if (class_2025.class.isAssignableFrom(Class.forName(record.className))) {
                  return true;
               }
            } catch (Throwable var5) {
            }
         }
      }

      return false;
   }

   public File getAudioFile() {
      if (this.gameAudioFile != null && this.gameAudioFile.isFile() && this.gameAudioFile.length() > 44L) {
         return this.gameAudioFile;
      } else {
         return this.audioFile != null && this.audioFile.isFile() && this.audioFile.length() > 44L ? this.audioFile : null;
      }
   }

   public boolean hasSoundEvents() {
      return this.soundEventsFile != null && this.soundEventsFile.isFile() && this.soundEventsFile.length() > 0L;
   }

   public List<SoundEventRecord> readSoundEvents() {
      List<SoundEventRecord> list = new ArrayList<>();
      if (!this.hasSoundEvents()) {
         return list;
      } else {
         try {
            Object var3;
            try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(this.soundEventsFile)))) {
               if (in.readInt() == 1397706311) {
                  while (true) {
                     list.add(SoundEventRecord.readTable(in));
                  }
               }

               var3 = list;
            }

            return (List<SoundEventRecord>)var3;
         } catch (EOFException var7) {
         } catch (Throwable var8) {
         }

         return list;
      }
   }

   public long contentStartNanos() {
      return this.meta.contentStartNanos > 0L ? this.meta.contentStartNanos : 0L;
   }

   public int findSnapshotIndex(long timeNanos) {
      int low = 0;
      int high = this.snapshots.size() - 1;
      int result = -1;

      while (low <= high) {
         int mid = low + high >>> 1;
         if (this.snapshots.get(mid).timeNanos <= timeNanos) {
            result = mid;
            low = mid + 1;
         } else {
            high = mid - 1;
         }
      }

      return result;
   }

   public SnapshotSample snapshot(int index) throws IOException {
      if (index >= 0 && index < this.snapshots.size()) {
         SnapshotSample sample = this.sampleCache.get(index);
         if (sample != null) {
            return sample;
         } else {
            ReplayReader.SnapshotRecord record = this.snapshots.get(index);
            this.snapshotFile.seek(record.offset);
            sample = SnapshotSample.read(this.snapshotFile);
            if (this.sampleCache.size() > 24) {
               this.sampleCache.clear();
            }

            this.sampleCache.put(index, sample);
            return sample;
         }
      } else {
         return null;
      }
   }

   @Override
   public void close() throws IOException {
      this.packetFile.close();
      this.snapshotFile.close();
   }

   public static final class PacketRecord {
      public final long timeNanos;
      public final String className;
      public final long payloadOffset;
      public final int payloadLength;

      PacketRecord(long timeNanos, String className, long payloadOffset, int payloadLength) {
         this.timeNanos = timeNanos;
         this.className = className;
         this.payloadOffset = payloadOffset;
         this.payloadLength = payloadLength;
      }
   }

   public static final class SnapshotRecord {
      public final long timeNanos;
      public final long offset;
      public final int entityCount;

      SnapshotRecord(long timeNanos, long offset, int entityCount) {
         this.timeNanos = timeNanos;
         this.offset = offset;
         this.entityCount = entityCount;
      }
   }
}
