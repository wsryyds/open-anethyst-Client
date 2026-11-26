package net.client.replay.record;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ReplayMeta {
   public int formatVersion = 2;
   public String modVersion = "1.0.1";
   public String minecraftVersion = "1.8.9";
   public long recordedAtMillis;
   public long durationNanos;
   public long contentStartNanos;
   public String playerName = "";
   public String playerUuid = "";
   public String playerSkin = "";
   public String worldName = "";
   public int dimension;
   public int sampleHz;
   public int packetCount;
   public int snapshotCount;
   public double fpsHint = 60.0;
   public String notes = "";
   public String cameraFile = "";
   public boolean hasAudio = false;
   public boolean hasGameAudio = false;
   public int soundEventCount = 0;
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   public String toJson() {
      return GSON.toJson(this);
   }

   public static ReplayMeta fromJson(String json) {
      ReplayMeta meta = GSON.fromJson(json, ReplayMeta.class);
      return meta == null ? new ReplayMeta() : meta;
   }
}
