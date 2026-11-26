package net.client.replay.render;

import java.io.File;
import java.io.IOException;
import net.client.replay.ReplayClient;
import net.client.replay.session.ReplayReader;
import net.client.replay.session.ReplaySession;
import net.minecraft.class_1600;

public final class PreviewJob {
   private final class_1600 client;
   private final ReplayReader reader;
   private final File file;
   private final ReplaySession session;
   private final long startTimeNanos;
   private final long endTimeNanos;
   private long clipStartNanos;
   private long clipEndNanos;
   private long currentTimeNanos;
   private long lastWallNanos;
   private long lastChatNanos;
   private boolean paused;
   private boolean finished;
   private boolean loop;

   public PreviewJob(class_1600 client, File file) throws IOException {
      this.client = client;
      this.file = file;
      this.reader = ReplayReader.open(file);
      this.session = new ReplaySession(client, this.reader);
      this.startTimeNanos = this.reader.contentStartNanos();
      long end = Math.max(this.reader.meta.durationNanos, this.reader.lastPacketTime());
      this.endTimeNanos = Math.max(this.startTimeNanos + 100000000L, end);
      this.currentTimeNanos = this.startTimeNanos;
      this.clipStartNanos = this.startTimeNanos;
      this.clipEndNanos = this.endTimeNanos;
      this.session.start(this.startTimeNanos, true);
      this.lastWallNanos = System.nanoTime();
      this.lastChatNanos = this.lastWallNanos;
      ReplayClient.chat("Preview started: " + file.getName());
   }

   public void tick() {
      if (!this.finished) {
         if (!this.paused) {
            long now = System.nanoTime();
            this.currentTimeNanos = this.currentTimeNanos + Math.max(0L, now - this.lastWallNanos);
            this.lastWallNanos = now;
            long loopEnd = Math.max(this.clipStartNanos + 100000L, this.clipEndNanos);
            if (this.currentTimeNanos >= loopEnd) {
               if (this.loop) {
                  this.currentTimeNanos = this.clipStartNanos;
                  this.session.seekTo(this.currentTimeNanos);
                  return;
               }

               this.currentTimeNanos = loopEnd;
               this.session.advanceTo(this.currentTimeNanos);
               ReplayClient.chat("Preview finished.");
               this.finish();
               return;
            }
         }

         this.session.advanceTo(this.currentTimeNanos);
         long now = System.nanoTime();
         if (now - this.lastChatNanos > 30000000000L) {
            this.lastChatNanos = now;
            ReplayClient.chat(String.format("Preview %.1fs / %.1fs", this.getCurrentSeconds(), this.getDurationSeconds()));
         }
      }
   }

   public void stop() {
      if (!this.finished) {
         ReplayClient.chat("Preview stopped.");
      }

      this.finish();
   }

   public void setPaused(boolean paused) {
      this.paused = paused;
      this.lastWallNanos = System.nanoTime();
   }

   public boolean isPaused() {
      return this.paused;
   }

   public void seekTo(long absoluteNanos) {
      this.currentTimeNanos = Math.max(0L, Math.min(this.endTimeNanos, absoluteNanos));
      this.lastWallNanos = System.nanoTime();
      if (!this.finished) {
         this.session.seekTo(this.currentTimeNanos);
      }
   }

   public void seekToSeconds(double absoluteSeconds) {
      this.seekTo((long)(absoluteSeconds * 1.0E9));
   }

   public long getCurrentTimeNanos() {
      return this.currentTimeNanos;
   }

   public long getStartTimeNanos() {
      return this.startTimeNanos;
   }

   public long getEndTimeNanos() {
      return this.endTimeNanos;
   }

   public double getCurrentSeconds() {
      return this.currentTimeNanos / 1.0E9;
   }

   public double getDurationSeconds() {
      return (this.endTimeNanos - this.startTimeNanos) / 1.0E9;
   }

   public double getClipSeconds() {
      return (this.currentTimeNanos - this.startTimeNanos) / 1.0E9;
   }

   public double getTotalSeconds() {
      return this.endTimeNanos / 1.0E9;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public File getFile() {
      return this.file;
   }

   public ReplayReader getReader() {
      return this.reader;
   }

   public ReplaySession getSession() {
      return this.session;
   }

   public void setClipRange(long startNanos, long endNanos) {
      this.clipStartNanos = Math.max(this.startTimeNanos, Math.min(startNanos, this.endTimeNanos));
      this.clipEndNanos = Math.max(this.clipStartNanos + 100000L, Math.min(endNanos, this.endTimeNanos));
   }

   public boolean isLoop() {
      return this.loop;
   }

   public void setLoop(boolean loop) {
      this.loop = loop;
   }

   private void finish() {
      this.finished = true;
      this.session.close();
      if (ReplayClient.getRenderManager() != null) {
         ReplayClient.getRenderManager().onPreviewFinished(this);
      }
   }
}
