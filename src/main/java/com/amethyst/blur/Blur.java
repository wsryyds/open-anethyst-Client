package com.amethyst.blur;

import com.amethyst.blur.mixin.GameRendererInvoker;
import com.amethyst.blur.mixin.MinecraftClientAccessor;
import com.amethyst.blur.util.ShaderResourcePack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.class_1600;
import net.minecraft.class_1653;
import net.minecraft.class_1655;
import net.minecraft.class_388;
import net.minecraft.class_524;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Blur implements ClientModInitializer {
   public static final String MOD_ID = "blur";
   public static final String MOD_NAME = "Blur";
   private static final Logger LOGGER = LogManager.getLogger("Blur");
   private static final class_1653 SHADER = new class_1653("shaders/post/fade_in_blur.json");
   private static Blur instance;
   private BlurConfig config;
   private Path configPath;
   private final ShaderResourcePack pack = new ShaderResourcePack();
   private boolean packAdded;
   private int colorFirst;
   private int colorSecond;
   private long startTime;

   @Override
   public void onInitializeClient() {
      bootstrap();
   }

   public static Blur getInstance() {
      return instance;
   }

   public static void bootstrap() {
      if (instance == null) {
         Blur blur = new Blur();
         blur.configPath = FabricLoader.getInstance().getConfigDir().resolve("blur.json");
         blur.config = BlurConfig.load(blur.configPath);
         blur.colorFirst = BlurConfig.parseColor(blur.config.gradientStartColor, 1962934272);
         blur.colorSecond = BlurConfig.parseColor(blur.config.gradientEndColor, 1962934272);
         instance = blur;
         LOGGER.info("[Blur] Loaded config (fadeTime={}ms, radius={})", blur.config.fadeTime, blur.config.radius);
      }
   }

   public static void onScreenChange(class_388 screen) {
      bootstrap();
      class_1600 client = class_1600.method_2965();
      if (client != null && client.field_3803 != null && client.field_3818 != null) {
         class_524 renderer = client.field_3818;
         boolean excluded = screen == null || instance.isExcluded(screen);
         if (!renderer.method_6878() && !excluded) {
            ((GameRendererInvoker)renderer).blur$loadShader(SHADER);
            instance.startTime = System.currentTimeMillis();
         } else if (renderer.method_6878() && excluded) {
            renderer.method_6880();
         }
      }
   }

   public static void ensureResourcePack(class_1600 client) {
      bootstrap();
      if (!instance.packAdded) {
         List<class_1655> packs = ((MinecraftClientAccessor)client).blur$getResourcePacks();
         if (!packs.contains(instance.pack)) {
            packs.add(instance.pack);
         }

         instance.packAdded = true;
      }
   }

   private boolean isExcluded(class_388 screen) {
      String runtimeName = screen.getClass().getName();
      MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();

      for (String exclusion : this.config.guiExclusions) {
         if (exclusion != null && !exclusion.isEmpty()) {
            String name = exclusion.trim();
            if (runtimeName.equals(name)) {
               return true;
            }

            for (String namespace : resolver.getNamespaces()) {
               try {
                  if (runtimeName.equals(resolver.mapClassName(namespace, name))) {
                     return true;
                  }
               } catch (RuntimeException var12) {
               }
            }
         }
      }

      return false;
   }

   public static float getProgress() {
      Blur blur = instance;
      if (blur == null) {
         return 1.0F;
      } else {
         int fadeTime = blur.config.fadeTime;
         return fadeTime <= 0 ? 1.0F : Math.min((float)(System.currentTimeMillis() - blur.startTime) / fadeTime, 1.0F);
      }
   }

   public static int getBackgroundColor(boolean second) {
      Blur blur = instance;
      if (blur == null) {
         return 0;
      } else {
         int color = second ? blur.colorSecond : blur.colorFirst;
         int a = color >>> 24;
         int r = color >> 16 & 0xFF;
         int b = color >> 8 & 0xFF;
         int g = color & 0xFF;
         float prog = getProgress();
         a = (int)(a * prog);
         r = (int)(r * prog);
         g = (int)(g * prog);
         b = (int)(b * prog);
         return a << 24 | r << 16 | b << 8 | g;
      }
   }

   public int getRadius() {
      return this.config.radius;
   }

   public static void setRadiusValue(int radius) {
      bootstrap();
      Blur blur = instance;
      if (blur != null) {
         int clamped = radius < 1 ? 1 : (radius > 100 ? 100 : radius);
         if (blur.config.radius != clamped) {
            blur.config.radius = clamped;
            blur.saveConfig();
         }
      }
   }

   public static int radiusValue() {
      bootstrap();
      Blur blur = instance;
      return blur == null ? 12 : blur.config.radius;
   }

   private void saveConfig() {
      if (this.configPath != null) {
         try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Path parent = this.configPath.getParent();
            if (parent != null) {
               Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(this.configPath, StandardCharsets.UTF_8)) {
               gson.toJson(this.config, writer);
            }
         } catch (IOException var8) {
            LOGGER.warn("[Blur] Failed to save config to {}", this.configPath, var8);
         }
      }
   }
}
