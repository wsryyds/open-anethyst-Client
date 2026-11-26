package com.amethyst.blur;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlurConfig {
   private static final Logger LOGGER = LogManager.getLogger("Blur");
   public String[] guiExclusions = new String[]{"net.minecraft.client.gui.screen.ChatScreen"};
   public int fadeTime = 200;
   public int radius = 12;
   public String gradientStartColor = "75000000";
   public String gradientEndColor = "75000000";

   public static BlurConfig load(Path path) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      BlurConfig config = null;
      if (Files.exists(path)) {
         try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            config = gson.fromJson(reader, BlurConfig.class);
         } catch (RuntimeException | IOException var12) {
            LOGGER.warn("[Blur] Failed to read config, using defaults", var12);
         }
      }

      if (config == null) {
         config = new BlurConfig();
      }

      config.validate();

      try {
         Path parent = path.getParent();
         if (parent != null) {
            Files.createDirectories(parent);
         }

         try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            gson.toJson(config, writer);
         }
      } catch (IOException var10) {
         LOGGER.warn("[Blur] Failed to save config to {}", path, var10);
      }

      return config;
   }

   private void validate() {
      if (this.guiExclusions == null) {
         this.guiExclusions = new String[0];
      }

      if (this.fadeTime < 0) {
         this.fadeTime = 0;
      }

      if (this.radius < 1) {
         this.radius = 1;
      } else if (this.radius > 100) {
         this.radius = 100;
      }

      if (this.gradientStartColor == null) {
         this.gradientStartColor = "75000000";
      }

      if (this.gradientEndColor == null) {
         this.gradientEndColor = "75000000";
      }
   }

   public static int parseColor(String hex, int fallback) {
      try {
         return Integer.parseUnsignedInt(hex.trim(), 16);
      } catch (RuntimeException var3) {
         LOGGER.warn("[Blur] Invalid color '{}', using default", hex);
         return fallback;
      }
   }
}
