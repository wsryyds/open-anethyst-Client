package net.client.replay.session;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.client.mixin.ReplayClientPlayNetworkHandlerAccessor;
import net.client.replay.ReplayClient;
import net.client.replay.record.EntitySnapshot;
import net.client.replay.record.PacketCodec;
import net.client.replay.record.SnapshotSample;
import net.client.replay.record.SoundEventRecord;
import net.minecraft.class_1156;
import net.minecraft.class_1160;
import net.minecraft.class_1600;
import net.minecraft.class_1653;
import net.minecraft.class_1845;
import net.minecraft.class_1989;
import net.minecraft.class_2010;
import net.minecraft.class_2011;
import net.minecraft.class_2025;
import net.minecraft.class_2151;
import net.minecraft.class_388;
import net.minecraft.class_477;
import net.minecraft.class_478;
import net.minecraft.class_503;
import net.minecraft.class_518;
import net.minecraft.class_530;
import net.minecraft.class_625;
import net.minecraft.class_700;
import net.minecraft.class_864;
import net.minecraft.class_1156.class_1157;

public final class ReplaySession implements AutoCloseable {
   private final class_1600 client;
   private final ReplayReader reader;
   private class_1845 handler;
   private ReplayClientConnection connection;
   private int packetIndex;
   private long currentTime;
   private final Map<Integer, EntitySnapshot> baseStates = new HashMap<>();
   private int cachedA = -1;
   private int cachedB = -1;
   private Map<Integer, EntitySnapshot> mapA;
   private Map<Integer, EntitySnapshot> mapB;
   private CameraController cameraController;
   private boolean closed;
   private boolean replayWorldReady;
   private List<SoundEventRecord> soundEvents;
   private int soundIndex;
   private long soundCursorNanos;
   private boolean soundEventsLoaded;
   private boolean background;
   private boolean backgroundFrame;
   private class_478 backgroundLiveWorld;
   private class_518 backgroundLivePlayer;
   private class_477 backgroundLiveInteraction;
   private class_530 backgroundLiveRenderer;
   private class_503 backgroundLiveParticles;
   private class_864 backgroundLiveCamera;
   private class_478 replayWorld;
   private class_518 replayPlayer;
   private class_477 replayInteraction;
   private class_530 replayRenderer;
   private class_503 replayParticles;

   public ReplaySession(class_1600 client, ReplayReader reader) {
      this.client = client;
      this.reader = reader;
   }

   public void start(long startTimeNanos) throws IOException {
      this.start(startTimeNanos, false);
   }

   public void start(long startTimeNanos, boolean preserveScreen) throws IOException {
      this.background = false;
      if (!this.reader.hasGameJoin()) {
         throw new IOException("Replay 里没有 GameJoin 数据包（录制是从世界中途开始的）。请用新版重新录制，或选择包含加入世界记录的 .lcr。");
      } else {
         class_388 previousScreen = this.client.field_3816;

         try {
            if (this.client.method_2960() != null && this.client.method_2960().method_6841() != null) {
               this.client.method_2960().method_6841().method_7394(new class_1989("[回放] Client"));
            }
         } catch (Throwable var8) {
         }

         try {
            if (this.client.field_3803 != null) {
               this.client.field_3803.method_3577();
            }
         } catch (Throwable var7) {
         }

         this.client.field_3803 = null;
         this.client.field_10310 = null;
         this.replayWorldReady = false;
         this.connection = new ReplayClientConnection();
         this.handler = new class_1845(this.client, null, this.connection, this.client.method_5568().method_6669());
         this.connection.method_7393(this.handler);
         ((ReplayClientPlayNetworkHandlerAccessor)this.handler).client$setPositionLookSetup(true);
         this.packetIndex = 0;
         this.currentTime = 0L;
         this.baseStates.clear();
         this.loadSoundEvents(startTimeNanos);

         try {
            this.applyFirstJoin();
            this.advanceTo(startTimeNanos);
         } catch (Throwable var6) {
            this.abortStart(previousScreen);
            throw new IOException("回放世界加载失败：" + var6.getClass().getSimpleName() + ": " + var6.getMessage(), var6);
         }

         if (this.client.field_3803 == null) {
            this.abortStart(previousScreen);
            throw new IOException("回放没有成功创建世界（GameJoin 数据包可能损坏）。");
         } else {
            this.cameraController = new CameraController(this.client, this.reader);
            if (preserveScreen && previousScreen != null) {
               this.client.field_3816 = previousScreen;
            } else {
               this.client.method_2928(null);
            }
         }
      }
   }

   public void startBackground(long startTimeNanos) throws IOException {
      if (!this.reader.hasGameJoin()) {
         throw new IOException("这个 .lcr 没有 GameJoin 记录，无法载入回放世界；请用新版重新录制。");
      } else if (this.client.field_3803 != null && this.client.field_10310 != null) {
         this.captureLiveState();
         this.background = true;
         ReplayClient.setReplayCaptureActive(true);
         this.connection = new ReplayClientConnection();
         this.handler = new class_1845(this.client, null, this.connection, this.client.method_5568().method_6669());
         this.connection.method_7393(this.handler);
         ((ReplayClientPlayNetworkHandlerAccessor)this.handler).client$setPositionLookSetup(true);
         this.packetIndex = 0;
         this.currentTime = 0L;
         this.baseStates.clear();

         try {
            this.applyFirstJoin();
            this.advanceTo(startTimeNanos);
         } catch (Throwable var4) {
            this.abortStartBackground();
            throw new IOException("回放世界加载失败：" + var4.getClass().getSimpleName() + ": " + var4.getMessage(), var4);
         }

         if (this.replayWorld == null) {
            this.abortStartBackground();
            throw new IOException("回放没有成功创建世界（GameJoin 数据包可能损坏）。");
         } else {
            this.cameraController = new CameraController(this.client, this.reader);
            this.restoreLiveState();
            ReplayClient.setReplayCaptureActive(false);
         }
      } else {
         throw new IOException("后台渲染需要先进入一个游戏世界。");
      }
   }

   public boolean isBackground() {
      return this.background;
   }

   public boolean isLiveStateCurrent() {
      if (!this.background) {
         return true;
      } else {
         return this.backgroundFrame ? true : this.client.field_3803 == this.backgroundLiveWorld && this.client.field_10310 == this.backgroundLivePlayer;
      }
   }

   public boolean isBackgroundFrameActive() {
      return this.backgroundFrame;
   }

   private void captureLiveState() {
      this.backgroundLiveWorld = this.client.field_3803;
      this.backgroundLivePlayer = this.client.field_10310;
      this.backgroundLiveInteraction = this.client.field_3800;
      this.backgroundLiveRenderer = this.client.field_3804;
      this.backgroundLiveParticles = this.client.field_3807;
      this.backgroundLiveCamera = this.client.method_9388();
   }

   public void beginBackgroundFrame() {
      if (this.background && !this.backgroundFrame && this.replayWorld != null) {
         this.client.field_3803 = this.replayWorld;
         this.client.field_10310 = this.replayPlayer;
         this.client.field_3800 = this.replayInteraction;
         this.client.field_3804 = this.replayRenderer;
         this.client.field_3807 = this.replayParticles;
         this.client.method_9390().method_1522(this.replayWorld);
         this.client.method_9384(this.replayPlayer);
         ReplayClient.setReplayCaptureActive(true);
         this.backgroundFrame = true;
      }
   }

   public void endBackgroundFrame() {
      if (this.backgroundFrame) {
         try {
            if (this.cameraController != null) {
               this.cameraController.restore();
            }
         } catch (Throwable var2) {
         }

         this.restoreLiveState();
         ReplayClient.setReplayCaptureActive(false);
         this.backgroundFrame = false;
      }
   }

   private void restoreLiveState() {
      if (this.background) {
         this.client.field_3803 = this.backgroundLiveWorld;
         this.client.field_10310 = this.backgroundLivePlayer;
         this.client.field_3800 = this.backgroundLiveInteraction;
         this.client.field_3804 = this.backgroundLiveRenderer;
         this.client.field_3807 = this.backgroundLiveParticles;
         this.client.method_9390().method_1522(this.backgroundLiveWorld);
         this.client.method_9384(this.backgroundLiveCamera);
      }
   }

   private void abortStartBackground() {
      try {
         if (this.handler != null) {
            this.handler.method_6842();
         }
      } catch (Throwable var4) {
      }

      try {
         if (this.replayWorld != null) {
            this.replayWorld.method_3577();
         }
      } catch (Throwable var3) {
      }

      try {
         if (this.replayRenderer != null) {
            this.replayRenderer.method_1371(null);
            this.replayRenderer = null;
         }
      } catch (Throwable var2) {
      }

      this.replayWorld = null;
      this.replayPlayer = null;
      this.replayInteraction = null;
      this.replayParticles = null;
      this.replayWorldReady = false;
      this.restoreLiveState();
      ReplayClient.setReplayCaptureActive(false);
      this.backgroundFrame = false;
   }

   private class_700<?> sanitizeGameJoin(class_700<?> packet) {
      if (!(packet instanceof class_2025 join)) {
         return packet;
      } else {
         int dimension = join.method_7799();
         if (dimension >= -1 && dimension <= 1) {
            return packet;
         } else {
            class_1157 mode = join.method_7798();
            if (mode == null) {
               mode = class_1157.field_4573;
            }

            class_2151 difficulty = join.method_7800();
            if (difficulty == null) {
               difficulty = class_2151.field_9069;
            }

            class_1160 generator = join.method_7802();
            if (generator == null) {
               generator = class_1160.field_4580;
            }

            ReplayClient.LOGGER.warn("Repairing GameJoin dimension {} -> 0 (old synthetic recording)", dimension);
            return new class_2025(
               join.method_7796(), mode, join.method_7797(), 0, difficulty, join.method_7801() <= 0 ? 100 : join.method_7801(), generator, join.method_10666()
            );
         }
      }
   }

   private void applyFirstJoin() {
      String runtimeName = class_2025.class.getName();

      for (int i = 0; i < this.reader.getPacketCount(); i++) {
         ReplayReader.PacketRecord record = this.reader.packet(i);
         if (record.className != null) {
            boolean isJoin = record.className.equals(runtimeName) || record.className.endsWith("GameJoinS2CPacket");
            if (!isJoin) {
               try {
                  isJoin = class_2025.class.isAssignableFrom(Class.forName(record.className));
               } catch (Throwable var7) {
               }
            }

            if (isJoin) {
               try {
                  class_700<?> packet = this.sanitizeGameJoin(PacketCodec.decode(record.className, this.reader.readPacketPayload(record)));
                  this.applyPacket(packet);
                  ReplayClient.LOGGER.info("Replay GameJoin applied at {}ns, world={}", record.timeNanos, this.client.field_3803 != null);
               } catch (Throwable var6) {
                  ReplayClient.LOGGER.error("Replay GameJoin decode/apply failed ({} @ {}ns)", record.className, record.timeNanos, var6);
               }
            }
         }
      }
   }

   private void abortStart(class_388 previousScreen) {
      try {
         if (this.handler != null) {
            this.handler.method_6842();
         }
      } catch (Throwable var4) {
      }

      try {
         if (this.client.field_3803 != null) {
            this.client.field_3803.method_3577();
         }
      } catch (Throwable var3) {
      }

      this.client.field_3803 = null;
      this.client.field_10310 = null;
      if (previousScreen != null) {
         this.client.field_3816 = previousScreen;
      } else {
         this.client.method_2928(null);
      }
   }

   public class_478 getWorld() {
      return this.client.field_3803;
   }

   public class_1600 getClient() {
      return this.client;
   }

   public ReplayReader getReader() {
      return this.reader;
   }

   public long currentTime() {
      return this.currentTime;
   }

   private void restoreBaseStates() {
      if (this.client.field_3803 != null) {
         List<class_864> entities = this.client.field_3803.method_3737();
         if (entities != null) {
            for (class_864 entity : entities) {
               EntitySnapshot base = this.baseStates.get(entity.method_8365());
               if (base != null) {
                  base.applyTo(entity);
               }
            }
         }
      }
   }

   private void captureBaseStates() {
      this.baseStates.clear();
      if (this.client.field_3803 != null) {
         List<class_864> entities = this.client.field_3803.method_3737();
         if (entities != null) {
            for (class_864 entity : entities) {
               this.baseStates.put(entity.method_8365(), EntitySnapshot.capture(entity, 1.0F));
            }
         }
      }
   }

   public void seekTo(long timeNanos) {
      if (!this.closed) {
         if (timeNanos < this.currentTime) {
            this.resetWorld(timeNanos);
         } else {
            this.advanceTo(timeNanos);
         }
      }
   }

   private void resetWorld(long timeNanos) {
      class_388 previousScreen = this.client.field_3816;

      try {
         if (this.handler != null) {
            this.handler.method_6842();
         }
      } catch (Throwable var6) {
      }

      try {
         if (this.client.field_3803 != null) {
            this.client.field_3803.method_3577();
         }
      } catch (Throwable var5) {
      }

      this.client.field_3803 = null;
      this.client.field_10310 = null;
      this.replayWorldReady = false;
      this.connection = new ReplayClientConnection();
      this.handler = new class_1845(this.client, null, this.connection, this.client.method_5568().method_6669());
      this.connection.method_7393(this.handler);
      ((ReplayClientPlayNetworkHandlerAccessor)this.handler).client$setPositionLookSetup(true);
      this.packetIndex = 0;
      this.currentTime = 0L;
      this.baseStates.clear();
      this.cachedA = -1;
      this.cachedB = -1;
      this.loadSoundEvents(timeNanos);
      if (this.cameraController != null) {
         this.cameraController.resetForWorld();
      }

      this.applyFirstJoin();
      this.advanceTo(timeNanos);
      if (previousScreen != null) {
         this.client.field_3816 = previousScreen;
      }
   }

   public void advanceTo(long timeNanos) {
      if (!this.closed) {
         int count = this.reader.getPacketCount();
         if (this.packetIndex < count && this.reader.packet(this.packetIndex).timeNanos <= timeNanos) {
            this.restoreBaseStates();

            while (this.packetIndex < count) {
               ReplayReader.PacketRecord record = this.reader.packet(this.packetIndex);
               if (record.timeNanos > timeNanos) {
                  break;
               }

               this.packetIndex++;

               try {
                  byte[] payload = this.reader.readPacketPayload(record);
                  class_700<?> packet = this.sanitizeGameJoin(PacketCodec.decode(record.className, payload));
                  this.applyPacket(packet);
               } catch (Throwable var7) {
                  if (ReplayClient.getConfig() != null
                     && (ReplayClient.getConfig().debug || this.client.field_3803 == null || record.className != null && record.className.contains("GameJoin"))
                     )
                   {
                     ReplayClient.LOGGER.warn("Replay packet failed: {} @ {}ns", record.className, record.timeNanos, var7);
                  }
               }
            }

            this.captureBaseStates();
         }

         this.applySnapshots(timeNanos);
         this.currentTime = timeNanos;
         this.playSoundsTo(timeNanos);
         if (this.cameraController != null) {
            this.cameraController.update(timeNanos / 1.0E9);
         }

         if (this.cameraController != null) {
            this.cameraController.syncFirstPersonHand();
         }
      }
   }

   private void applyPacket(class_700<?> packet) {
      if (packet instanceof class_2025) {
         if (!this.replayWorldReady) {
            this.applyReplayGameJoin((class_2025)packet);
         }
      } else if (!(packet instanceof class_2011) && !(packet instanceof class_2010)) {
         ((class_700<class_1845>)packet).method_1831(this.handler);
      }
   }

   private void applyReplayGameJoin(class_2025 join) {
      ReplayLocalSkin.capture(this.client.field_10310);
      ReplayLocalSkin.installEntry(
         this.handler,
         this.reader == null ? null : this.reader.meta.playerUuid,
         this.reader == null ? null : this.reader.meta.playerName,
         this.reader == null ? null : this.reader.meta.playerSkin
      );
      class_1157 mode = join.method_7798();
      if (mode == null) {
         mode = class_1157.field_4573;
      }

      class_2151 difficulty = join.method_7800();
      if (difficulty == null) {
         difficulty = class_2151.field_9069;
      }

      class_1160 generator = join.method_7802();
      if (generator == null) {
         generator = class_1160.field_4580;
      }

      int dimension = join.method_7799();
      if (dimension < -1 || dimension > 1) {
         dimension = 0;
      }

      class_477 interaction = new class_477(this.client, this.handler);
      class_478 world = new class_478(
         this.handler, new class_1156(0L, mode, false, join.method_7797(), generator), dimension, difficulty, this.client.field_3767
      );
      ((ReplayClientPlayNetworkHandlerAccessor)this.handler).client$setWorld(world);
      if (this.background) {
         this.replayWorld = world;
         this.replayInteraction = interaction;
         if (this.replayRenderer == null) {
            this.replayRenderer = new class_530(this.client);
            this.replayRenderer.method_1371(world);
         } else {
            this.replayRenderer.method_1371(world);
         }

         this.replayParticles = new class_503(world, this.client.method_5570());
         this.replayPlayer = new class_518(this.client, world, this.handler, new class_625());
         ReplayLocalSkin.applyModelParts(this.replayPlayer);
         this.client.field_3800 = interaction;
         this.client.field_3803 = world;
         this.client.field_10310 = this.replayPlayer;
         this.client.field_3804 = this.replayRenderer;
         this.client.field_3807 = this.replayParticles;
         world.method_3686(this.replayPlayer);
         this.replayPlayer.method_2562();
         this.client.method_9384(this.replayPlayer);
      } else {
         this.client.field_3800 = interaction;
         this.client.method_2929(world);
         if (this.client.field_10310 == null) {
            this.client.field_10310 = new class_518(this.client, world, this.handler, new class_625());
            ReplayLocalSkin.applyModelParts(this.client.field_10310);
            world.method_3686(this.client.field_10310);
            this.client.field_10310.method_2562();
            this.client.method_9384(this.client.field_10310);
         }
      }

      this.client.field_10310.field_5323 = dimension;
      this.client.field_10310.method_8362(join.method_7796());
      this.client.field_10310.method_11248(join.method_10666());
      this.client.field_3800.method_1233(mode);
      if (this.client.field_3823 != null) {
         this.client.field_3823.method_4228();
      }

      this.replayWorldReady = true;
   }

   private void applySnapshots(long timeNanos) {
      if (this.client.field_3803 != null && this.reader.getSnapshotCount() != 0) {
         int index = this.reader.findSnapshotIndex(timeNanos);
         if (index < 0) {
            index = 0;
         }

         SnapshotSample a;
         SnapshotSample b;
         try {
            a = this.reader.snapshot(index);
            b = index + 1 < this.reader.getSnapshotCount() ? this.reader.snapshot(index + 1) : null;
         } catch (IOException var13) {
            return;
         }

         if (a != null) {
            if (index != this.cachedA) {
               this.cachedA = index;
               this.mapA = toMap(a);
            }

            if (b != null && index + 1 != this.cachedB) {
               this.cachedB = index + 1;
               this.mapB = toMap(b);
            } else if (b == null) {
               this.cachedB = -1;
               this.mapB = null;
            }

            float progress = 1.0F;
            if (b != null && b.timeNanos > a.timeNanos) {
               progress = (float)((timeNanos - a.timeNanos) * 1.0 / (b.timeNanos - a.timeNanos));
               if (progress < 0.0F) {
                  progress = 0.0F;
               }

               if (progress > 1.0F) {
                  progress = 1.0F;
               }
            }

            List<class_864> entities = this.client.field_3803.method_3737();
            if (entities != null) {
               for (class_864 entity : entities) {
                  EntitySnapshot sa = this.mapA.get(entity.method_8365());
                  EntitySnapshot sb = this.mapB == null ? null : this.mapB.get(entity.method_8365());
                  EntitySnapshot visual = interpolate(sa, sb, progress);
                  if (visual != null) {
                     visual.applyTo(entity);
                  }
               }
            }
         }
      }
   }

   private static Map<Integer, EntitySnapshot> toMap(SnapshotSample sample) {
      Map<Integer, EntitySnapshot> map = new HashMap<>();
      if (sample != null) {
         for (EntitySnapshot entity : sample.entities) {
            map.put(entity.entityId, entity);
         }
      }

      return map;
   }

   private static EntitySnapshot interpolate(EntitySnapshot a, EntitySnapshot b, float progress) {
      if (a == null) {
         return b;
      } else if (b == null || progress >= 1.0F) {
         return a;
      } else if (progress <= 0.0F) {
         return a;
      } else {
         EntitySnapshot s = new EntitySnapshot();
         s.entityId = a.entityId;
         s.x = lerp(a.x, b.x, progress);
         s.y = lerp(a.y, b.y, progress);
         s.z = lerp(a.z, b.z, progress);
         s.yaw = EntitySnapshot.lerpAngle(a.yaw, b.yaw, progress);
         s.pitch = lerp(a.pitch, b.pitch, progress);
         s.headYaw = EntitySnapshot.lerpAngle(a.headYaw, b.headYaw, progress);
         s.bodyYaw = EntitySnapshot.lerpAngle(a.bodyYaw, b.bodyYaw, progress);
         s.velocityX = lerp(a.velocityX, b.velocityX, progress);
         s.velocityY = lerp(a.velocityY, b.velocityY, progress);
         s.velocityZ = lerp(a.velocityZ, b.velocityZ, progress);
         s.limbA = lerp(a.limbA, b.limbA, progress);
         s.limbB = lerp(a.limbB, b.limbB, progress);
         s.limbC = lerp(a.limbC, b.limbC, progress);
         s.limbD = lerp(a.limbD, b.limbD, progress);
         s.swingProgress = lerp(a.swingProgress, b.swingProgress, progress);
         s.flags = progress < 0.5F ? a.flags : b.flags;
         s.hurtTime = progress < 0.5F ? a.hurtTime : b.hurtTime;
         s.width = lerp(a.width, b.width, progress);
         s.height = lerp(a.height, b.height, progress);
         EntitySnapshot equip = progress < 0.5F ? a : b;
         s.hotbarId = equip.hotbarId;
         s.hotbarDamage = equip.hotbarDamage;
         s.hotbarCount = equip.hotbarCount;
         s.selectedSlot = equip.selectedSlot;
         return s;
      }
   }

   private static double lerp(double a, double b, float t) {
      return a + (b - a) * t;
   }

   private static float lerp(float a, float b, float t) {
      return a + (b - a) * t;
   }

   private void loadSoundEvents(long startTimeNanos) {
      this.soundEventsLoaded = true;
      this.soundIndex = 0;
      this.soundCursorNanos = startTimeNanos;
      if (this.background) {
         this.soundEvents = null;
      } else {
         this.soundEvents = this.reader.hasSoundEvents() ? this.reader.readSoundEvents() : Collections.emptyList();
         if (this.soundEvents != null) {
            while (this.soundIndex < this.soundEvents.size() && this.soundEvents.get(this.soundIndex).timeNanos <= startTimeNanos) {
               this.soundIndex++;
            }
         }
      }
   }

   private void playSoundsTo(long timeNanos) {
      if (!this.closed && !this.background && this.soundEventsLoaded) {
         if (this.soundEvents != null && !this.soundEvents.isEmpty()) {
            if (timeNanos < this.soundCursorNanos) {
               this.soundIndex = 0;

               while (this.soundIndex < this.soundEvents.size() && this.soundEvents.get(this.soundIndex).timeNanos <= timeNanos) {
                  this.soundIndex++;
               }

               this.soundCursorNanos = timeNanos;
            } else if (timeNanos - this.soundCursorNanos > 400000000L) {
               while (this.soundIndex < this.soundEvents.size() && this.soundEvents.get(this.soundIndex).timeNanos <= timeNanos) {
                  this.soundIndex++;
               }

               this.soundCursorNanos = timeNanos;
            } else {
               while (this.soundIndex < this.soundEvents.size() && this.soundEvents.get(this.soundIndex).timeNanos <= timeNanos) {
                  this.playRecordedSound(this.soundEvents.get(this.soundIndex));
                  this.soundIndex++;
               }

               this.soundCursorNanos = timeNanos;
            }
         }
      }
   }

   private void playRecordedSound(SoundEventRecord event) {
      if (event != null && this.client.method_6630() != null) {
         if (!(event.volume <= 1.0E-4F)) {
            String id = event.eventPath != null && !event.eventPath.isEmpty() ? event.eventPath : event.soundPath;
            if (id != null && !id.isEmpty()) {
               try {
                  System.out.println("[ReplaySound] play " + id + " vol=" + event.volume);
                  if (Boolean.getBoolean("client.replay.soundDebug")) {
                     System.out.println("[ReplaySound] play " + id + " vol=" + event.volume);
                  }

                  this.client.method_6630().method_7121(new ReplaySoundInstance(new class_1653(id), event.volume, event.pitch, event.x, event.y, event.z));
                  System.out.println("[ReplaySound] play " + id + " @" + event.timeNanos + "ns vol=" + event.volume);
               } catch (Throwable var4) {
               }
            }
         }
      }
   }

   public CameraController getCameraController() {
      return this.cameraController;
   }

   @Override
   public void close() {
      if (!this.closed) {
         this.closed = true;
         ReplayLocalSkin.clear();
         if (this.background && !this.backgroundFrame && !this.isLiveStateCurrent()) {
            try {
               if (this.handler != null) {
                  this.handler.method_6842();
               }
            } catch (Throwable var5) {
            }

            try {
               if (this.replayWorld != null) {
                  this.replayWorld.method_3577();
               }
            } catch (Throwable var4) {
            }

            try {
               if (this.replayRenderer != null) {
                  this.replayRenderer.method_1371(null);
                  this.replayRenderer = null;
               }

               if (this.client.field_3803 != null) {
                  this.client.method_9390().method_1522(this.client.field_3803);
               }
            } catch (Throwable var3) {
            }

            this.replayWorld = null;
            this.replayPlayer = null;
            this.replayInteraction = null;
            this.replayParticles = null;
            this.replayWorldReady = false;
            this.background = false;

            try {
               this.reader.close();
            } catch (IOException var2) {
            }
         } else if (this.background) {
            this.beginBackgroundFrame();
            if (this.cameraController != null) {
               try {
                  this.cameraController.restore();
               } catch (Throwable var10) {
               }
            }

            try {
               if (this.handler != null) {
                  this.handler.method_6842();
               }
            } catch (Throwable var9) {
            }

            try {
               if (this.replayWorld != null) {
                  this.replayWorld.method_3577();
               }
            } catch (Throwable var8) {
            }

            try {
               if (this.replayRenderer != null) {
                  this.replayRenderer.method_1371(null);
                  this.replayRenderer = null;
               }
            } catch (Throwable var7) {
            }

            this.endBackgroundFrame();
            this.replayWorld = null;
            this.replayPlayer = null;
            this.replayInteraction = null;
            this.replayParticles = null;
            this.replayWorldReady = false;
            this.background = false;

            try {
               this.reader.close();
            } catch (IOException var6) {
            }
         } else {
            if (this.cameraController != null) {
               try {
                  this.cameraController.restore();
               } catch (Throwable var14) {
               }
            }

            try {
               if (this.handler != null) {
                  this.handler.method_6842();
               }
            } catch (Throwable var13) {
            }

            try {
               if (this.client.field_3803 != null) {
                  this.client.field_3803.method_3577();
               }
            } catch (Throwable var12) {
            }

            this.client.field_3803 = null;
            this.client.field_10310 = null;
            this.replayWorldReady = false;

            try {
               this.reader.close();
            } catch (IOException var11) {
            }
         }
      }
   }
}
