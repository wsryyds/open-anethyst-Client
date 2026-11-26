package net.client.replay.record;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.client.mixin.ReplayClientChunkProviderAccessor;
import net.client.mixin.ReplayPlayerListS2CPacketAccessor;
import net.client.replay.ReplayClient;
import net.client.replay.util.ReplayPaths;
import net.minecraft.class_1069;
import net.minecraft.class_1160;
import net.minecraft.class_1196;
import net.minecraft.class_1397;
import net.minecraft.class_1600;
import net.minecraft.class_1698;
import net.minecraft.class_1699;
import net.minecraft.class_1900;
import net.minecraft.class_1908;
import net.minecraft.class_1909;
import net.minecraft.class_1957;
import net.minecraft.class_1992;
import net.minecraft.class_1994;
import net.minecraft.class_1995;
import net.minecraft.class_1997;
import net.minecraft.class_1998;
import net.minecraft.class_1999;
import net.minecraft.class_2020;
import net.minecraft.class_2025;
import net.minecraft.class_2151;
import net.minecraft.class_2153;
import net.minecraft.class_2636;
import net.minecraft.class_476;
import net.minecraft.class_621;
import net.minecraft.class_700;
import net.minecraft.class_705;
import net.minecraft.class_864;
import net.minecraft.class_869;
import net.minecraft.class_874;
import net.minecraft.class_958;
import net.minecraft.class_962;
import net.minecraft.class_963;
import net.minecraft.class_964;
import net.minecraft.class_966;
import net.minecraft.class_988;
import net.minecraft.class_1156.class_1157;
import net.minecraft.class_1900.class_1901;
import net.minecraft.class_705.class_2578;
import net.minecraft.class_705.class_2579;

public final class RecordingManager {
   private final class_1600 client;
   private RecordingWriter writer;
   private SnapshotSampler sampler;
   private class_1957 connection;
   private long startNanos;
   private String requestedName;
   private volatile boolean active;
   private long firstChunkNanos = -1L;
   private final List<SoundEventRecord> soundEvents = new ArrayList<>();
   private Thread saveThread;

   public RecordingManager(class_1600 client) {
      this.client = client;
   }

   public boolean isActive() {
      return this.active;
   }

   public long elapsedNanos() {
      return this.active ? System.nanoTime() - this.startNanos : 0L;
   }

   public synchronized void start(String name) {
      if (this.active) {
         ReplayClient.chat("已经在录制，先执行 .replay record stop。");
      } else if (!ReplayClient.isReplayActive() && (!ReplayClient.getRenderManager().isActive() || ReplayClient.getRenderManager().isBackgroundRendering())) {
         if (this.client.field_3803 != null && this.client.field_10310 != null) {
            class_1957 current = null;
            if (this.client.method_2960() != null) {
               current = this.client.method_2960().method_6841();
            }

            this.startWithConnection(current, name, true);
         } else {
            ReplayClient.chat("只能在进入游戏世界后录制；主菜单只用于编辑/渲染回放。");
         }
      } else {
         ReplayClient.chat("先停止回放/渲染任务再录制。");
      }
   }

   private void startWithConnection(class_1957 connection, String name, boolean allowSyntheticState) {
      try {
         this.writer = new RecordingWriter();
         this.sampler = new SnapshotSampler(ReplayClient.getConfig().sampleHz);
         this.connection = connection;
         this.requestedName = name;
         this.startNanos = System.nanoTime();
         this.firstChunkNanos = -1L;
         this.soundEvents.clear();
         this.active = true;
         if (allowSyntheticState) {
            this.writeInitialState();
         }

         ReplayClient.chat(
            "开始录制"
               + (name != null && !name.isEmpty() ? "（" + name + "）" : "")
               + "，采样 "
               + ReplayClient.getConfig().sampleHz
               + " Hz。按 "
               + ReplayClient.stopKeyName()
               + " 停止。"
         );
      } catch (Throwable var5) {
         ReplayClient.LOGGER.error("Could not start recording", var5);
         ReplayClient.chat("无法开始录制：" + var5.getMessage());
         this.active = false;
      }
   }

   private void writeInitialState() {
      if (this.client.field_3803 != null && this.client.field_10310 != null && this.writer != null) {
         try {
            long t = 0L;
            class_1157 gameMode = this.client.field_3800 == null ? class_1157.field_4573 : this.client.field_3800.method_9667();
            if (gameMode == null) {
               gameMode = class_1157.field_4573;
            }

            class_2151 difficulty = this.client.field_3803.method_3588().method_9339();
            if (difficulty == null) {
               difficulty = class_2151.field_9069;
            }

            class_1160 generatorType = this.client.field_3803.method_3588().method_236();
            if (generatorType == null) {
               generatorType = class_1160.field_4580;
            }

            class_2025 join = new class_2025(
               this.client.field_10310.method_8365(),
               gameMode,
               this.client.field_3803.method_3588().method_235(),
               this.client.field_3803.field_4558.method_9179(),
               difficulty,
               100,
               generatorType,
               this.client.field_10310.method_11246()
            );
            this.writer.writePacket(t, join);
            this.writer
               .writePacket(
                  t,
                  new class_1992(
                     this.client.field_10310.field_3252,
                     this.client.field_10310.field_3253,
                     this.client.field_10310.field_3254,
                     this.client.field_10310.field_3258,
                     this.client.field_10310.field_3193,
                     Collections.emptySet()
                  )
               );

            for (class_864 entity : this.client.field_3803.method_3737()) {
               if (entity instanceof class_988 && entity != this.client.field_10310) {
                  class_988 player = (class_988)entity;
                  if (player.method_8429() != null) {
                     this.writePlayerListAdd(player);
                     this.writer.writePacket(t, new class_1999(player));
                  }
               }
            }

            if (this.client.field_3803.method_3586() instanceof class_476) {
               List<class_1196> chunks = ((ReplayClientChunkProviderAccessor)this.client.field_3803.method_3586()).client$getChunks();
               int index = 0;

               for (class_1196 chunk : chunks) {
                  if (chunk != null && !chunk.method_3914()) {
                     this.writer.writePacket(index++, new class_2020(chunk, true, 65535));
                  }
               }

               if (index > 0) {
                  this.firstChunkNanos = 0L;
               }
            }

            for (class_864 entityx : this.client.field_3803.method_3737()) {
               if (entityx != null && entityx != this.client.field_10310 && !(entityx instanceof class_988)) {
                  this.writeInitialEntitySpawn(t, entityx);
               }
            }

            ReplayClient.LOGGER.info("Captured initial world state for mid-world recording: {} chunks", this.writer.getPacketCount());
         } catch (Throwable var11) {
            ReplayClient.LOGGER.warn("Could not capture initial world state", var11);
         }
      }
   }

   private void writeInitialEntitySpawn(long timeNanos, class_864 entity) throws IOException {
      if (entity instanceof class_1699) {
         this.writer.writePacket(timeNanos, new class_1997((class_1699)entity));
      } else if (entity instanceof class_869) {
         this.writer.writePacket(timeNanos, new class_1995((class_869)entity));
      } else if (entity instanceof class_874) {
         this.writer.writePacket(timeNanos, new class_1998((class_874)entity));
      } else {
         int objectType = getObjectSpawnType(entity);
         if (objectType > 0) {
            int dataId = 0;
            if (entity instanceof class_964 item) {
               dataId = item.method_8399() == null ? 0 : class_1069.method_6364(item.method_8399().method_3421());
            }

            this.writer.writePacket(timeNanos, new class_1994(entity, objectType, dataId));
         }
      }
   }

   private static int getObjectSpawnType(class_864 entity) {
      if (entity instanceof class_962) {
         return 1;
      } else if (entity instanceof class_964) {
         return 2;
      } else if (entity instanceof class_966) {
         return 50;
      } else if (entity instanceof class_958) {
         return 51;
      } else if (entity instanceof class_963) {
         return 70;
      } else if (entity instanceof class_1397) {
         return 71;
      } else if (entity instanceof class_1698) {
         return 77;
      } else {
         return entity instanceof class_2636 ? 78 : 0;
      }
   }

   private void writePlayerListAdd(class_988 player) {
      try {
         class_705 packet = new class_705();
         ((ReplayPlayerListS2CPacketAccessor)packet).client$setAction(class_2578.field_11638);
         List var10000 = ((ReplayPlayerListS2CPacketAccessor)packet).client$getEntries();
         Objects.requireNonNull(packet);
         var10000.add(packet.new class_2579(player.method_8429(), 0, class_1157.field_4573, null));
         this.writer.writePacket(0L, packet);
      } catch (Throwable var3) {
         ReplayClient.LOGGER.warn("Could not add player-list entry for {}", player.method_6344().method_7472(), var3);
      }
   }

   public synchronized void onPacket(class_1957 from, class_700<?> packet) {
      if (!this.active && ReplayClient.getConfig().autoRecord && !ReplayClient.isReplayActive() && packet instanceof class_2025) {
         this.startWithConnection(from, "auto", false);
      }

      if (this.active && this.writer != null) {
         if (this.connection == null) {
            this.connection = from;
         }

         if (from == this.connection) {
            long elapsed = System.nanoTime() - this.startNanos;
            if (this.firstChunkNanos < 0L && packet instanceof class_2020) {
               this.firstChunkNanos = elapsed;
            }

            boolean written = this.writer.writePacket(elapsed, packet);
            if (!written && packet instanceof class_2025 join) {
               try {
                  class_2025 fallback = new class_2025(
                     join.method_7796(),
                     join.method_7798() == null ? class_1157.field_4573 : join.method_7798(),
                     join.method_7797(),
                     join.method_7799(),
                     join.method_7800() == null ? class_2151.field_9069 : join.method_7800(),
                     join.method_7801() <= 0 ? 100 : join.method_7801(),
                     join.method_7802() == null ? class_1160.field_4580 : join.method_7802(),
                     join.method_10666()
                  );
                  this.writer.writePacket(elapsed, fallback);
               } catch (Throwable var8) {
                  ReplayClient.LOGGER.warn("Could not build fallback GameJoin", var8);
               }
            }
         }
      }
   }

   public synchronized void onSound(class_1900 instance) {
      if (this.active && this.writer != null && instance != null) {
         if (!ReplayClient.isReplayActive() && !ReplayClient.isReplayCaptureActive()) {
            if (ReplayClient.getConfig().recordGameAudio) {
               try {
                  String path = instance.method_7073().toString();
                  class_1909 manager = this.client.method_6630();
                  class_1908 set = manager == null ? null : manager.method_7119(instance.method_7073());
                  class_621 sound = set == null ? null : set.method_7116();
                  if (sound != null) {
                     path = sound.method_7089().toString();
                  }

                  class_2153 category = set == null ? class_2153.field_9092 : set.method_7118();
                  float volume = instance.method_7076();
                  if (sound != null) {
                     volume = (float)Math.min(1.0, Math.max(0.0, volume * sound.method_7091()));
                  }

                  volume *= this.client.field_3823 == null ? 1.0F : this.client.field_3823.method_6656(category);
                  float pitch = instance.method_7077();
                  if (sound != null) {
                     pitch = (float)Math.min(2.0, Math.max(0.5, pitch * sound.method_7093()));
                  }

                  boolean attenuated = instance.method_7081() == class_1901.field_8163;
                  if (attenuated && this.client.field_10310 != null) {
                     double dx = instance.method_7078() - this.client.field_10310.field_3252;
                     double dy = instance.method_7079() - this.client.field_10310.field_3253;
                     double dz = instance.method_7080() - this.client.field_10310.field_3254;
                     double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                     double rolloff = 16.0 * Math.max(1.0, (double)instance.method_7076());
                     volume *= (float)Math.max(0.0, 1.0 - distance / rolloff);
                  }

                  if (volume <= 1.0E-4F) {
                     return;
                  }

                  SoundEventRecord record = new SoundEventRecord();
                  record.timeNanos = System.nanoTime() - this.startNanos;
                  record.eventPath = instance.method_7073().toString();
                  record.soundPath = path;
                  record.x = instance.method_7078();
                  record.y = instance.method_7079();
                  record.z = instance.method_7080();
                  record.volume = volume;
                  record.pitch = pitch;
                  record.attenuated = attenuated;
                  record.repeatable = instance.method_7074();
                  this.soundEvents.add(record);
                  ReplayClient.noteSoundRecorded();
                  this.writer.writeSoundEvent(record);
               } catch (Throwable var20) {
                  ReplayClient.LOGGER.debug("Could not capture sound {}", instance.method_7073(), var20);
               }
            }
         }
      }
   }

   public synchronized void sample(long nowNanos, float tickDelta) {
      if (this.active && this.sampler != null && this.writer != null) {
         if (this.sampler.shouldSample(nowNanos)) {
            this.writer.writeSnapshot(this.sampler.sample(nowNanos - this.startNanos, this.client.field_3803, tickDelta));
         }
      }
   }

   public void clientTick() {
      if (this.active) {
         if (this.client.field_3803 != null && this.client.field_10310 != null) {
            if (this.connection != null && this.client.method_2960() != null && this.client.method_2960().method_6841() != this.connection) {
               this.stop();
            }
         } else {
            this.stop();
         }
      }
   }

   public synchronized void stop() {
      if (this.active && this.writer != null) {
         this.active = false;
         final long duration = System.nanoTime() - this.startNanos;
         final List<SoundEventRecord> eventsToMix = new ArrayList<>(this.soundEvents);
         this.soundEvents.clear();
         final ReplayMeta meta = new ReplayMeta();
         meta.modVersion = "1.0.1";
         meta.minecraftVersion = "1.8.9";
         meta.recordedAtMillis = System.currentTimeMillis();
         meta.durationNanos = duration;
         meta.sampleHz = ReplayClient.getConfig().sampleHz;
         meta.playerName = this.client.method_5568() == null ? "" : this.client.method_5568().method_5583();

         try {
            GameProfile profile = null;
            if (this.client.field_10310 != null) {
               profile = this.client.field_10310.method_8429();
            } else if (this.client.method_5568() != null) {
               profile = this.client.method_5568().method_6669();
            }

            if (profile != null) {
               if (profile.getId() != null) {
                  meta.playerUuid = profile.getId().toString();
               }

               if (profile.getName() != null && !profile.getName().isEmpty()) {
                  meta.playerName = profile.getName();
               }

               Collection<Property> props = profile.getProperties().get("textures");
               if (props != null) {
                  for (Property prop : props) {
                     if (prop != null && prop.getValue() != null) {
                        meta.playerSkin = prop.getValue();
                        break;
                     }
                  }
               }
            }
         } catch (Throwable var9) {
         }

         meta.worldName = this.requestedName != null && !this.requestedName.isEmpty() ? this.requestedName : "replay";
         if (this.client.field_3803 != null && this.client.field_3803.method_3588() != null) {
            String levelName = this.client.field_3803.method_3588().method_226();
            if (levelName != null && !levelName.isEmpty()) {
               meta.worldName = levelName;
            }
         }

         meta.contentStartNanos = this.firstChunkNanos >= 0L ? this.firstChunkNanos : 0L;
         meta.fpsHint = ReplayClient.getConfig().defaultFps;
         meta.notes = "Recorded with Client replay.";
         final RecordingWriter toSave = this.writer;
         this.writer = null;
         this.soundEvents.clear();
         this.sampler = null;
         this.connection = null;
         this.requestedName = null;
         Thread saver = new Thread(
            new Runnable() {
               @Override
               public void run() {
                  try {
                     if (!eventsToMix.isEmpty()) {
                        GameAudioMixer mixer = new GameAudioMixer(RecordingManager.this.client);
                        mixer.mix(eventsToMix, duration, toSave.getGameAudioFile());
                     }

                     ReplayClient.LOGGER
                        .info("[replay] sound: play() seen={} recorded={} kept={}", ReplayClient.soundSeen, ReplayClient.soundRecorded, meta.soundEventCount);
                     File file = toSave.finish(meta);
                     ReplayClient.chat(
                        "回放已保存："
                           + file.getName()
                           + "（"
                           + meta.packetCount
                           + " 个包，"
                           + meta.snapshotCount
                           + " 个快照"
                           + (meta.soundEventCount > 0 ? "，含声音事件" : "")
                           + "）"
                     );
                  } catch (Throwable var2) {
                     ReplayClient.LOGGER.error("Could not save replay", var2);
                     ReplayClient.chat("回放保存失败：" + var2.getMessage());
                  }
               }
            },
            "ReplayClient-replay-save"
         );
         saver.setDaemon(true);
         this.saveThread = saver;
         saver.start();
      } else {
         if (!this.active) {
            ReplayClient.chat("当前没有在录制。");
         }
      }
   }

   public void shutdown() {
      if (this.active) {
         this.stop();
      }

      Thread saver = this.saveThread;
      if (saver != null && saver.isAlive()) {
         try {
            saver.join(15000L);
         } catch (InterruptedException var3) {
            Thread.currentThread().interrupt();
         }
      }

      if (this.writer != null) {
         this.writer.abort();
      }
   }

   public String statusLine() {
      if (!this.active) {
         return "Idle";
      } else {
         int packets = this.writer == null ? 0 : this.writer.getPacketCount();
         int samples = this.writer == null ? 0 : this.writer.getSnapshotCount();
         return String.format(Locale.US, "%.1f 秒，%d 包，%d 快照", this.elapsedNanos() / 1.0E9, packets, samples);
      }
   }

   public void status() {
      if (!this.active) {
         ReplayClient.chat("录制器空闲。");
      } else {
         double seconds = this.elapsedNanos() / 1.0E9;
         ReplayClient.chat(
            String.format(
               "Recording %.1fs, %d packets, %d samples, target %s",
               seconds,
               this.writer.getPacketCount(),
               this.writer.getSnapshotCount(),
               this.requestedName == null ? "auto" : this.requestedName
            )
         );
         ReplayClient.chat("Replay folder: " + ReplayPaths.replaysDir().getAbsolutePath());
      }
   }
}
