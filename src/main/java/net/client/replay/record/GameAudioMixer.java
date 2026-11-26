package net.client.replay.record;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import net.client.replay.ReplayClient;
import net.client.replay.util.ReplayPaths;
import net.minecraft.class_1600;
import net.minecraft.class_1653;
import paulscode.sound.SoundBuffer;
import paulscode.sound.codecs.CodecJOrbis;

public final class GameAudioMixer {
   private static final int SAMPLE_RATE = 44100;
   private final class_1600 client;
   private final Map<String, GameAudioMixer.DecodedSound> cache = new HashMap<>();

   public GameAudioMixer(class_1600 client) {
      this.client = client;
   }

   public boolean mix(List<SoundEventRecord> events, long durationNanos, File out) {
      if (events != null && !events.isEmpty()) {
         long samplesLong = (long)(durationNanos / 1.0E9 * 44100.0) + 1L;
         if (samplesLong > 0L && samplesLong <= 536870900L) {
            int samples = (int)samplesLong;
            int chunkFrames = 44100;
            List<SoundEventRecord> sorted = new ArrayList<>(events);
            Collections.sort(sorted, new Comparator<SoundEventRecord>() {
               public int compare(SoundEventRecord a, SoundEventRecord b) {
                  return Long.compare(a.timeNanos, b.timeNanos);
               }
            });

            try {
               boolean hasAudio;
               try (RandomAccessFile raf = new RandomAccessFile(out, "rw")) {
                  raf.setLength(0L);
                  int dataBytes = samples * 4;
                  raf.writeBytes("RIFF");
                  raf.writeInt(Integer.reverseBytes(36 + dataBytes));
                  raf.writeBytes("WAVEfmt ");
                  raf.writeInt(Integer.reverseBytes(16));
                  raf.writeShort(Short.reverseBytes((short)1));
                  raf.writeShort(Short.reverseBytes((short)2));
                  raf.writeInt(Integer.reverseBytes(44100));
                  raf.writeInt(Integer.reverseBytes(176400));
                  raf.writeShort(Short.reverseBytes((short)4));
                  raf.writeShort(Short.reverseBytes((short)16));
                  raf.writeBytes("data");
                  raf.writeInt(Integer.reverseBytes(dataBytes));
                  float[] left = new float[44100];
                  float[] right = new float[44100];
                  List<GameAudioMixer.ActiveSound> active = new ArrayList<>();
                  int next = 0;
                  int written = 0;

                  while (written < samples) {
                     int end = Math.min(samples, written + 44100);
                     Arrays.fill(left, 0.0F);
                     Arrays.fill(right, 0.0F);

                     while (next < sorted.size() && startFrame(sorted.get(next)) < end) {
                        SoundEventRecord event = sorted.get(next++);
                        GameAudioMixer.DecodedSound sound = this.decode(event.soundPath);
                        if (sound != null && sound.samples.length != 0) {
                           int frames = sound.samples.length / sound.channels;
                           if (frames > 0) {
                              GameAudioMixer.ActiveSound a = new GameAudioMixer.ActiveSound();
                              a.event = event;
                              a.sound = sound;
                              a.start = startFrame(event);
                              a.frames = frames;
                              if (a.start + frames > written) {
                                 active.add(a);
                              }
                           }
                        }
                     }

                     Iterator<GameAudioMixer.ActiveSound> it = active.iterator();

                     while (it.hasNext()) {
                        GameAudioMixer.ActiveSound a = it.next();
                        long soundEnd = a.start + a.frames;
                        if (!a.event.repeatable && soundEnd <= written) {
                           it.remove();
                        } else {
                           long from = Math.max((long)written, (long)a.start);
                           long to = a.event.repeatable ? end : Math.min((long)end, soundEnd);
                           if (from < to) {
                              float gain = Math.max(0.0F, Math.min(1.0F, a.event.volume));

                              for (long global = from; global < to; global++) {
                                 long sourceFrame = (global - a.start) % a.frames;
                                 int src = (int)sourceFrame * a.sound.channels;
                                 float l = a.sound.samples[src];
                                 float r = a.sound.channels > 1 ? a.sound.samples[src + 1] : l;
                                 int dst = (int)(global - written);
                                 left[dst] += l * gain;
                                 right[dst] += r * gain;
                              }
                           }
                        }
                     }

                     int count = end - written;
                     ByteBuffer pcm = ByteBuffer.allocate(count * 4).order(ByteOrder.LITTLE_ENDIAN);

                     for (int i = 0; i < count; i++) {
                        short l = (short)Math.max(-32768, Math.min(32767, (int)(left[i] * 32767.0F)));
                        short r = (short)Math.max(-32768, Math.min(32767, (int)(right[i] * 32767.0F)));
                        pcm.putShort(l);
                        pcm.putShort(r);
                     }

                     raf.write(pcm.array());
                     written = end;
                  }

                  hasAudio = out.isFile() && out.length() > 44L;
               }

               return hasAudio;
            } catch (Throwable var37) {
               ReplayClient.LOGGER.warn("Game audio mix failed", var37);
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static int startFrame(SoundEventRecord event) {
      long start = (long)(event.timeNanos / 1.0E9 * 44100.0);
      return (int)Math.max(0L, Math.min(2147483647L, start));
   }

   private GameAudioMixer.DecodedSound decode(String path) {
      if (path != null && !path.isEmpty()) {
         GameAudioMixer.DecodedSound cached = this.cache.get(path);
         if (cached != null) {
            return cached;
         } else {
            File ogg = null;

            GameAudioMixer.DecodedSound var10;
            try {
               class_1653 id = new class_1653(path);
               File cacheDir = new File(ReplayPaths.cacheDir(), "sounds");
               if (!cacheDir.isDirectory() && !cacheDir.mkdirs()) {
                  return null;
               }

               ogg = new File(cacheDir, Integer.toHexString(path.hashCode()) + ".ogg");

               try (
                  InputStream in = this.client.method_5571().method_5893(id).method_5888();
                  OutputStream out = new FileOutputStream(ogg);
               ) {
                  byte[] buffer = new byte[8192];

                  int read;
                  while ((read = in.read(buffer)) >= 0) {
                     out.write(buffer, 0, read);
                  }
               }

               CodecJOrbis codec = new CodecJOrbis();
               URL url = ogg.toURI().toURL();
               if (!codec.initialize(url)) {
                  return null;
               }

               SoundBuffer buffer = codec.readAll();
               codec.cleanup();
               if (buffer == null || buffer.audioData == null || buffer.audioFormat == null) {
                  return null;
               }

               GameAudioMixer.DecodedSound decoded = convert(buffer, 44100);
               this.cache.put(path, decoded);
               var10 = decoded;
            } catch (Throwable var22) {
               ReplayClient.LOGGER.debug("Could not decode sound {}", path, var22);
               return null;
            } finally {
               if (ogg != null) {
                  ogg.delete();
               }
            }

            return var10;
         }
      } else {
         return null;
      }
   }

   private static GameAudioMixer.DecodedSound convert(SoundBuffer buffer, int targetRate) {
      AudioFormat format = buffer.audioFormat;
      int channels = Math.max(1, format.getChannels());
      int sourceRate = Math.round(format.getSampleRate());
      if (sourceRate <= 0) {
         sourceRate = targetRate;
      }

      int frameSize = Math.max(1, format.getFrameSize());
      int sampleBytes = Math.max(1, format.getSampleSizeInBits() / 8);
      int frames = Math.max(0, buffer.audioData.length / frameSize);
      float[] source = new float[frames * channels];

      for (int i = 0; i < frames; i++) {
         int base = i * frameSize;

         for (int c = 0; c < channels; c++) {
            int off = base + c * sampleBytes;
            int value = 0;
            if (sampleBytes == 2) {
               value = format.isBigEndian()
                  ? buffer.audioData[off] << 8 | buffer.audioData[off + 1] & 255
                  : buffer.audioData[off + 1] << 8 | buffer.audioData[off] & 255;
            } else {
               value = buffer.audioData[off] << 8;
            }

            source[i * channels + c] = value / 32768.0F;
         }
      }

      int targetFrames = (int)Math.max(1L, (long)frames * targetRate / sourceRate);
      float[] out = new float[targetFrames * channels];

      for (int i = 0; i < targetFrames; i++) {
         double srcPos = (double)i * sourceRate / targetRate;
         int i0 = (int)Math.floor(srcPos);
         int i1 = Math.min(frames - 1, i0 + 1);
         float frac = (float)(srcPos - i0);
         if (i0 >= frames) {
            i0 = frames - 1;
         }

         if (i0 < 0) {
            i0 = 0;
         }

         for (int c = 0; c < channels; c++) {
            float a = source[i0 * channels + c];
            float b = source[i1 * channels + c];
            out[i * channels + c] = a + (b - a) * frac;
         }
      }

      return new GameAudioMixer.DecodedSound(out, channels);
   }

   private static void writeWav(File out, float[] left, float[] right, int frames) throws Exception {
      int dataBytes = frames * 2 * 2;

      try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(out))) {
         stream.writeBytes("RIFF");
         stream.writeInt(Integer.reverseBytes(36 + dataBytes));
         stream.writeBytes("WAVEfmt ");
         stream.writeInt(Integer.reverseBytes(16));
         stream.writeShort(Short.reverseBytes((short)1));
         stream.writeShort(Short.reverseBytes((short)2));
         stream.writeInt(Integer.reverseBytes(44100));
         stream.writeInt(Integer.reverseBytes(176400));
         stream.writeShort(Short.reverseBytes((short)4));
         stream.writeShort(Short.reverseBytes((short)16));
         stream.writeBytes("data");
         stream.writeInt(Integer.reverseBytes(dataBytes));

         for (int i = 0; i < frames; i++) {
            short l = (short)Math.max(-32768, Math.min(32767, (int)(left[i] * 32767.0F)));
            short r = (short)Math.max(-32768, Math.min(32767, (int)(right[i] * 32767.0F)));
            stream.writeShort(Short.reverseBytes(l));
            stream.writeShort(Short.reverseBytes(r));
         }
      }
   }

   private static final class ActiveSound {
      SoundEventRecord event;
      GameAudioMixer.DecodedSound sound;
      int start;
      int frames;
   }

   private static final class DecodedSound {
      final float[] samples;
      final int channels;

      DecodedSound(float[] samples, int channels) {
         this.samples = samples;
         this.channels = channels;
      }
   }
}
