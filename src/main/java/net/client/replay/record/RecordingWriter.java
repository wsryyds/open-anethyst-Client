package net.client.replay.record;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.client.replay.ReplayClient;
import net.client.replay.util.ReplayPaths;
import net.minecraft.class_700;

public final class RecordingWriter {
   private final File tempDir = new File(ReplayPaths.replaysDir(), ".tmp-" + UUID.randomUUID().toString().substring(0, 8));
   private final File packetFile;
   private final File snapshotFile;
   private final File gameAudioFile;
   private final File soundFile;
   private final DataOutputStream packetOut;
   private final DataOutputStream snapshotOut;
   private final DataOutputStream soundOut;
   private int packetCount;
   private int snapshotCount;
   private int soundCount;
   private boolean finished;

   public RecordingWriter() throws IOException {
      if (!this.tempDir.isDirectory() && !this.tempDir.mkdirs()) {
         throw new IOException("Could not create " + this.tempDir);
      } else {
         this.packetFile = new File(this.tempDir, "packets.bin");
         this.snapshotFile = new File(this.tempDir, "snapshots.bin");
         this.gameAudioFile = new File(this.tempDir, "gameaudio.wav");
         this.soundFile = new File(this.tempDir, "soundevents.bin");
         this.packetOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.packetFile)));
         this.snapshotOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.snapshotFile)));
         this.soundOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.soundFile)));
         this.soundOut.writeInt(1397706311);
      }
   }

   public synchronized boolean writePacket(long timeNanos, class_700<?> packet) {
      if (this.finished) {
         return false;
      } else {
         try {
            byte[] data = PacketCodec.encode(packet);
            this.packetOut.writeLong(timeNanos);
            this.packetOut.writeUTF(packet.getClass().getName());
            this.packetOut.writeInt(data.length);
            this.packetOut.write(data);
            this.packetCount++;
            return true;
         } catch (Throwable var5) {
            ReplayClient.LOGGER.warn("Could not record packet {}", packet == null ? "null" : packet.getClass().getName(), var5);
            return false;
         }
      }
   }

   public synchronized void writeSnapshot(SnapshotSample sample) {
      if (!this.finished) {
         try {
            sample.write(this.snapshotOut);
            this.snapshotCount++;
         } catch (Throwable var3) {
         }
      }
   }

   public File getGameAudioFile() {
      return this.gameAudioFile;
   }

   public synchronized void writeSoundEvent(SoundEventRecord event) {
      if (!this.finished && event != null) {
         try {
            event.writeTable(this.soundOut);
            this.soundCount++;
         } catch (Throwable var3) {
         }
      }
   }

   public int getPacketCount() {
      return this.packetCount;
   }

   public int getSnapshotCount() {
      return this.snapshotCount;
   }

   public synchronized File finish(ReplayMeta meta) throws IOException {
      if (this.finished) {
         throw new IOException("Already finished");
      } else {
         this.finished = true;
         this.packetOut.flush();
         this.packetOut.close();
         this.snapshotOut.flush();
         this.snapshotOut.close();
         this.soundOut.flush();
         this.soundOut.close();
         File out = new File(ReplayPaths.replaysDir(), ReplayPaths.sanitizeName(meta.worldName) + "-" + ReplayPaths.timestampName() + ".lcr");

         for (int suffix = 1; out.exists(); suffix++) {
            out = new File(ReplayPaths.replaysDir(), ReplayPaths.sanitizeName(meta.worldName) + "-" + ReplayPaths.timestampName() + "-" + suffix + ".lcr");
         }

         meta.packetCount = this.packetCount;
         meta.snapshotCount = this.snapshotCount;
         meta.hasAudio = false;
         meta.hasGameAudio = this.gameAudioFile.isFile() && this.gameAudioFile.length() > 44L;
         meta.soundEventCount = this.soundCount;

         try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(out)))) {
            zip.setLevel(0);
            writeEntry(zip, "meta.json", meta.toJson().getBytes("UTF-8"));
            copyEntry(zip, "packets.bin", this.packetFile);
            copyEntry(zip, "snapshots.bin", this.snapshotFile);
            if (meta.hasGameAudio) {
               copyEntry(zip, "gameaudio.wav", this.gameAudioFile);
            }

            if (this.soundCount > 0 && this.soundFile.isFile()) {
               copyEntry(zip, "soundevents.bin", this.soundFile);
            }
         } finally {
            deleteTree(this.tempDir);
         }

         return out;
      }
   }

   public synchronized void abort() {
      if (!this.finished) {
         this.finished = true;

         try {
            this.packetOut.close();
         } catch (Throwable var4) {
         }

         try {
            this.snapshotOut.close();
         } catch (Throwable var3) {
         }

         try {
            this.soundOut.close();
         } catch (Throwable var2) {
         }

         deleteTree(this.tempDir);
      }
   }

   private static void writeEntry(ZipOutputStream zip, String name, byte[] data) throws IOException {
      ZipEntry entry = new ZipEntry(name);
      entry.setTime(0L);
      zip.putNextEntry(entry);
      zip.write(data);
      zip.closeEntry();
   }

   private static void copyEntry(ZipOutputStream zip, String name, File file) throws IOException {
      ZipEntry entry = new ZipEntry(name);
      entry.setTime(0L);
      zip.putNextEntry(entry);

      try (InputStream in = new FileInputStream(file)) {
         byte[] buffer = new byte[8192];

         int read;
         while ((read = in.read(buffer)) >= 0) {
            zip.write(buffer, 0, read);
         }
      }

      zip.closeEntry();
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
}
