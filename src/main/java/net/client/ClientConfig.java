package net.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import net.client.util.ClientLog;
import net.minecraft.class_1600;

public final class ClientConfig {
   public static boolean hurtColorOn = false;
   public static boolean realDrops = false;
   public static boolean uiOverride = false;
   public static boolean lightTheme = false;
   public static int themeColor = -10006364;
   public static boolean modernFont = false;
   public static int glassMode = 0;
   public static boolean dropRest = false;
   public static int hurtColor = 1308557312;
   public static boolean noHurtCam = false;
   public static boolean blockHitAnim = false;
   public static boolean hurtBlinkOff = false;
   public static boolean afterimage = false;
   public static int afterimageColor = -1;
   public static int afterimageLife = 1380;
   public static float afterimageStrength = 1.0F;
   public static boolean freelook = false;
   public static int freelookKey = 56;
   public static int afterimageLayers = 10;
   public static boolean afterimageSelf = false;
   public static boolean fullbright = false;
   public static boolean minimalBobbing = false;
   public static boolean autoSprint = false;
   public static boolean fovClient = false;
   public static float fovBase = 70.0F;
   public static boolean fovSmooth = true;
   public static float fovGlobal = 1.0F;
   public static float fovMin = 10.0F;
   public static float fovMax = 160.0F;
   public static boolean fovSprintOn = true;
   public static float fovSprint = 80.0F;
   public static boolean fovSpeedOn = true;
   public static float fovSpeed = 80.0F;
   public static boolean fovSlowOn = true;
   public static float fovSlow = 65.0F;
   public static boolean fovFlyOn = true;
   public static float fovFly = 77.0F;
   public static boolean fovAimOn = true;
   public static float fovAim = 60.0F;
   public static boolean fovWaterOn = true;
   public static float fovWater = 60.0F;
   public static boolean customGlint = false;
   public static int glintColor = -6606593;
   public static boolean fogModify = false;
   public static int fogColor = -9535576;
   public static float fogDensity = 1.0F;
   public static boolean fogSky = false;
   public static boolean highlightBlock = false;
   public static int highlightFill = 1344470920;
   public static int highlightOutline = -16711800;
   public static boolean selfNameTag = false;
   public static int nameBgColor = 1073741824;
   public static boolean customCrosshair = false;
   public static int crosshairColor = -1;
   public static String crosshairMask = "000010000/000010000/000010000/000010000/111111111/000010000/000010000/000010000/000010000";
   public static int nameTextColor = -1;
   public static float screenDim = 0.8F;
   public static float backdropBlur = 0.5F;
   public static boolean chatBackground = false;
   public static int chatBgColor = Integer.MIN_VALUE;
   public static boolean scoreboardBackground = false;
   public static boolean hideScoreboard = false;
   public static int scoreboardBgColor = 1342177280;
   public static boolean chatVisible = true;
   public static boolean hideScoreNumbers = false;
   public static boolean scoreboardCustom = false;
   public static String scoreboardTitle = "";
   public static String scoreboardText = "";
   public static boolean scoreboardShowTitle = true;
   public static boolean scoreboardShowNames = true;
   public static boolean scoreboardShowScores = true;
   public static int scoreboardMaxLines = 15;
   public static int weatherMode = 0;
   public static boolean weatherThroughBlocks = false;
   public static float handForward = 0.0F;
   public static float handRight = 0.0F;
   public static float handUp = 0.0F;
   public static boolean timeOverride = false;
   public static int timeTicks = 6000;
   public static boolean motionBlur = false;
   public static float blurAmount = 0.5F;
   public static boolean hideOwnParticles = false;
   public static boolean rawInput = false;
   public static boolean fovDefaultOffMigrated = false;
   public static boolean customParticles = false;
   public static boolean colorDefaultsMigrated = false;
   public static boolean nameBgMigrated = false;
   public static boolean hideMiningParticles = false;
   public static boolean hideSprintParticles = false;
   public static float critParticleMultiplier = 1.0F;
   public static float sharpParticleMultiplier = 1.0F;
   public static boolean imeFix = false;
   public static int fireHeight = 100;
   public static boolean leftHanded = false;
   public static String msClientId = "";
   public static int blurStyle = 0;
   public static final float FULLBRIGHT_GAMMA = 100.0F;
   private static boolean loaded = false;
   private static boolean dirty = false;
   private static long lastFlush = 0L;
   private static final Map<String, Object> DEFAULTS = new HashMap<>();

   public static int langIndex() {
      try {
         class_1600 mc = class_1600.method_2965();
         return mc != null && mc.field_3823 != null && mc.field_3823.field_963 != null && mc.field_3823.field_963.toLowerCase(Locale.ROOT).startsWith("zh")
            ? 0
            : 1;
      } catch (Exception var11) {
         return 1;
      }
   }

   private ClientConfig() {
   }

   private static File configFile() {
      return new File(class_1600.method_2965().field_3762, "config/client.properties");
   }

   public static synchronized void ensureLoaded() {
      if (!loaded) {
         loaded = true;
         File file = configFile();
         if (file.isFile()) {
            Properties props = new Properties();

            try (InputStreamReader in = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
               props.load(in);
            } catch (Exception var71) {
               return;
            }

            hurtColorOn = readBool(props, "hurtColorOn", hurtColorOn);
            realDrops = readBool(props, "realDrops", realDrops);
            uiOverride = readBool(props, "uiOverride", uiOverride);
            lightTheme = readBool(props, "lightTheme", lightTheme);
            themeColor = readColor(props, "themeColor", themeColor);
            modernFont = readBool(props, "modernFont", modernFont);
            glassMode = readInt(props, "glassMode", glassMode);
            dropRest = readBool(props, "dropRest", readBool(props, "dropNoSpin", dropRest));
            hurtColor = readInt(props, "hurtColor", hurtColor);
            noHurtCam = readBool(props, "noHurtCam", noHurtCam);
            blockHitAnim = readBool(props, "blockHitAnim", blockHitAnim);
            hurtBlinkOff = readBool(props, "hurtBlinkOff", hurtBlinkOff);
            afterimage = readBool(props, "afterimage", afterimage);
            freelook = readBool(props, "freelook", freelook);
            freelookKey = readInt(props, "freelookKey", freelookKey);
            afterimageColor = readColor(props, "afterimageColor", afterimageColor);
            afterimageLife = readInt(props, "afterimageLife", afterimageLife);
            afterimageStrength = readFloat(props, "afterimageStrength", afterimageStrength);
            afterimageLayers = readInt(props, "afterimageLayers", afterimageLayers);
            afterimageSelf = readBool(props, "afterimageSelf", afterimageSelf);
            fullbright = readBool(props, "fullbright", fullbright);
            minimalBobbing = readBool(props, "minimalBobbing", minimalBobbing);
            autoSprint = readBool(props, "autoSprint", autoSprint);
            fovBase = readFloat(props, "fovBase", fovBase);
            fovClient = readBool(props, "fovClient", fovClient);
            fovDefaultOffMigrated = readBool(props, "fovDefaultOffMigrated", fovDefaultOffMigrated);
            if (!fovDefaultOffMigrated) {
               fovClient = false;
               fovDefaultOffMigrated = true;
            }

            fovSmooth = readBool(props, "fovSmooth", fovSmooth);
            fovGlobal = readFloat(props, "fovGlobal", fovGlobal);
            fovMin = readFloat(props, "fovMin", fovMin);
            fovMax = readFloat(props, "fovMax", fovMax);
            fovSprintOn = readBool(props, "fovSprintOn", fovSprintOn);
            fovSprint = readFloat(props, "fovSprint", fovSprint);
            fovSpeedOn = readBool(props, "fovSpeedOn", fovSpeedOn);
            fovSpeed = readFloat(props, "fovSpeed", fovSpeed);
            fovSlowOn = readBool(props, "fovSlowOn", fovSlowOn);
            fovSlow = readFloat(props, "fovSlow", fovSlow);
            fovFlyOn = readBool(props, "fovFlyOn", fovFlyOn);
            fovFly = readFloat(props, "fovFly", fovFly);
            fovAimOn = readBool(props, "fovAimOn", fovAimOn);
            fovAim = readFloat(props, "fovAim", fovAim);
            fovWaterOn = readBool(props, "fovWaterOn", fovWaterOn);
            fovWater = readFloat(props, "fovWater", fovWater);
            customGlint = readBool(props, "customGlint", customGlint);
            glintColor = readColor(props, "glintColor", glintColor);
            fogModify = readBool(props, "fogModify", fogModify);
            fogColor = readColor(props, "fogColor", fogColor);
            fogDensity = readFloat(props, "fogDensity", fogDensity);
            fogSky = readBool(props, "fogSky", readBool(props, "fogSkyFollow", fogSky));
            highlightBlock = readBool(props, "highlightBlock", highlightBlock);
            highlightFill = readColor(props, "highlightFill", highlightFill);
            highlightOutline = readColor(props, "highlightOutline", highlightOutline);
            selfNameTag = readBool(props, "selfNameTag", selfNameTag);
            nameBgColor = readColor(props, "nameBgColor", nameBgColor);
            customCrosshair = readBool(props, "customCrosshair", customCrosshair);
            crosshairColor = readColor(props, "crosshairColor", crosshairColor);
            crosshairMask = props.getProperty("crosshairMask", crosshairMask);
            screenDim = readFloat(props, "screenDim", screenDim);
            backdropBlur = readFloat(props, "backdropBlur", backdropBlur);
            if (!readBool(props, "screenDimVanillaDefault", false) && screenDim <= 0.02F) {
               screenDim = 0.8F;
            }

            nameTextColor = readColor(props, "nameTextColor", nameTextColor);
            chatBackground = readBool(props, "chatBackground", chatBackground);
            chatBgColor = readColor(props, "chatBgColor", chatBgColor);
            scoreboardBackground = readBool(props, "scoreboardBackground", scoreboardBackground);
            chatVisible = readBool(props, "chatVisible", chatVisible);
            hideScoreboard = readBool(props, "hideScoreboard", hideScoreboard);
            hideScoreNumbers = readBool(props, "hideScoreNumbers", hideScoreNumbers);
            scoreboardBgColor = readColor(props, "scoreboardBgColor", scoreboardBgColor);
            scoreboardCustom = readBool(props, "scoreboardCustom", scoreboardCustom);
            scoreboardTitle = props.getProperty("scoreboardTitle", scoreboardTitle);
            scoreboardText = props.getProperty("scoreboardText", scoreboardText);
            scoreboardShowTitle = readBool(props, "scoreboardShowTitle", scoreboardShowTitle);
            scoreboardShowNames = readBool(props, "scoreboardShowNames", scoreboardShowNames);
            scoreboardShowScores = readBool(props, "scoreboardShowScores", scoreboardShowScores);
            scoreboardMaxLines = readInt(props, "scoreboardMaxLines", scoreboardMaxLines);
            weatherMode = readInt(props, "weatherMode", weatherMode);
            weatherThroughBlocks = readBool(props, "weatherThroughBlocks", weatherThroughBlocks);
            handForward = readFloat(props, "handForward", handForward);
            handRight = readFloat(props, "handRight", handRight);
            handUp = readFloat(props, "handUp", handUp);
            timeOverride = readBool(props, "timeOverride", timeOverride);
            timeTicks = readInt(props, "timeTicks", timeTicks);
            motionBlur = readBool(props, "motionBlur", motionBlur);
            blurAmount = readFloat(props, "blurAmount", blurAmount);
            hideOwnParticles = readBool(props, "hideOwnParticles", hideOwnParticles);
            rawInput = readBool(props, "rawInput", rawInput);
            customParticles = readBool(props, "customParticles", customParticles);
            colorDefaultsMigrated = readBool(props, "colorDefaultsMigrated", colorDefaultsMigrated);
            nameBgMigrated = readBool(props, "nameBgMigrated", nameBgMigrated);
            if (!colorDefaultsMigrated) {
               chatBgColor = Integer.MIN_VALUE;
               scoreboardBgColor = 1342177280;
               hurtColor = 1308557312;
               colorDefaultsMigrated = true;
            }

            if (!nameBgMigrated) {
               nameBgColor = 1073741824;
               nameBgMigrated = true;
               saveSoon();
            }

            hideMiningParticles = readBool(props, "hideMiningParticles", hideMiningParticles);
            hideSprintParticles = readBool(props, "hideSprintParticles", hideSprintParticles);
            critParticleMultiplier = readFloat(props, "critParticleMultiplier", critParticleMultiplier);
            sharpParticleMultiplier = readFloat(props, "sharpParticleMultiplier", sharpParticleMultiplier);
            imeFix = readBool(props, "imeFix", imeFix);
            fireHeight = readInt(props, "fireHeight", fireHeight);
            leftHanded = readBool(props, "leftHanded", leftHanded);
            msClientId = props.getProperty("msClientId", msClientId);
            blurStyle = readInt(props, "blurStyle", blurStyle);
            validate();
         }
      }
   }

   public static synchronized void save() {
      File file = configFile();
      File dir = file.getParentFile();
      if (dir != null && !dir.isDirectory()) {
         dir.mkdirs();
      }

      Properties props = new Properties();
      props.setProperty("hurtColorOn", Boolean.toString(hurtColorOn));
      props.setProperty("realDrops", Boolean.toString(realDrops));
      props.setProperty("uiOverride", Boolean.toString(uiOverride));
      props.setProperty("lightTheme", Boolean.toString(lightTheme));
      props.setProperty("themeColor", toHex(themeColor));
      props.setProperty("modernFont", Boolean.toString(modernFont));
      props.setProperty("glassMode", Integer.toString(glassMode));
      props.setProperty("dropRest", Boolean.toString(dropRest));
      props.setProperty("hurtColor", Integer.toString(hurtColor));
      props.setProperty("noHurtCam", Boolean.toString(noHurtCam));
      props.setProperty("blockHitAnim", Boolean.toString(blockHitAnim));
      props.setProperty("hurtBlinkOff", Boolean.toString(hurtBlinkOff));
      props.setProperty("afterimage", Boolean.toString(afterimage));
      props.setProperty("freelook", Boolean.toString(freelook));
      props.setProperty("freelookKey", Integer.toString(freelookKey));
      props.setProperty("afterimageColor", toHex(afterimageColor));
      props.setProperty("afterimageLife", Integer.toString(afterimageLife));
      props.setProperty("afterimageStrength", Float.toString(afterimageStrength));
      props.setProperty("afterimageLayers", Integer.toString(afterimageLayers));
      props.setProperty("afterimageSelf", Boolean.toString(afterimageSelf));
      props.setProperty("fullbright", Boolean.toString(fullbright));
      props.setProperty("minimalBobbing", Boolean.toString(minimalBobbing));
      props.setProperty("autoSprint", Boolean.toString(autoSprint));
      props.setProperty("fovBase", Float.toString(fovBase));
      props.setProperty("fovClient", Boolean.toString(fovClient));
      props.setProperty("fovSmooth", Boolean.toString(fovSmooth));
      props.setProperty("fovGlobal", Float.toString(fovGlobal));
      props.setProperty("fovMin", Float.toString(fovMin));
      props.setProperty("fovMax", Float.toString(fovMax));
      props.setProperty("fovSprintOn", Boolean.toString(fovSprintOn));
      props.setProperty("fovSprint", Float.toString(fovSprint));
      props.setProperty("fovSpeedOn", Boolean.toString(fovSpeedOn));
      props.setProperty("fovSpeed", Float.toString(fovSpeed));
      props.setProperty("fovSlowOn", Boolean.toString(fovSlowOn));
      props.setProperty("fovSlow", Float.toString(fovSlow));
      props.setProperty("fovFlyOn", Boolean.toString(fovFlyOn));
      props.setProperty("fovFly", Float.toString(fovFly));
      props.setProperty("fovAimOn", Boolean.toString(fovAimOn));
      props.setProperty("fovAim", Float.toString(fovAim));
      props.setProperty("fovWaterOn", Boolean.toString(fovWaterOn));
      props.setProperty("fovWater", Float.toString(fovWater));
      props.setProperty("customGlint", Boolean.toString(customGlint));
      props.setProperty("glintColor", toHex(glintColor));
      props.setProperty("fogModify", Boolean.toString(fogModify));
      props.setProperty("fogColor", toHex(fogColor));
      props.setProperty("fogDensity", Float.toString(fogDensity));
      props.setProperty("fogSky", Boolean.toString(fogSky));
      props.setProperty("highlightBlock", Boolean.toString(highlightBlock));
      props.setProperty("highlightFill", toHex(highlightFill));
      props.setProperty("highlightOutline", toHex(highlightOutline));
      props.setProperty("selfNameTag", Boolean.toString(selfNameTag));
      props.setProperty("nameBgColor", toHex(nameBgColor));
      props.setProperty("customCrosshair", Boolean.toString(customCrosshair));
      props.setProperty("crosshairColor", toHex(crosshairColor));
      props.setProperty("crosshairMask", crosshairMask);
      props.setProperty("screenDim", String.format(Locale.ROOT, "%.2f", screenDim));
      props.setProperty("backdropBlur", String.format(Locale.ROOT, "%.2f", backdropBlur));
      props.setProperty("screenDimVanillaDefault", "true");
      props.setProperty("nameTextColor", toHex(nameTextColor));
      props.setProperty("chatBackground", Boolean.toString(chatBackground));
      props.setProperty("chatBgColor", toHex(chatBgColor));
      props.setProperty("scoreboardBackground", Boolean.toString(scoreboardBackground));
      props.setProperty("chatVisible", Boolean.toString(chatVisible));
      props.setProperty("hideScoreboard", Boolean.toString(hideScoreboard));
      props.setProperty("hideScoreNumbers", Boolean.toString(hideScoreNumbers));
      props.setProperty("scoreboardBgColor", toHex(scoreboardBgColor));
      props.setProperty("scoreboardCustom", Boolean.toString(scoreboardCustom));
      props.setProperty("scoreboardTitle", scoreboardTitle == null ? "" : scoreboardTitle);
      props.setProperty("scoreboardText", scoreboardText == null ? "" : scoreboardText);
      props.setProperty("scoreboardShowTitle", Boolean.toString(scoreboardShowTitle));
      props.setProperty("scoreboardShowNames", Boolean.toString(scoreboardShowNames));
      props.setProperty("scoreboardShowScores", Boolean.toString(scoreboardShowScores));
      props.setProperty("scoreboardMaxLines", Integer.toString(scoreboardMaxLines));
      props.setProperty("weatherMode", Integer.toString(weatherMode));
      props.setProperty("weatherThroughBlocks", Boolean.toString(weatherThroughBlocks));
      props.setProperty("handForward", Float.toString(handForward));
      props.setProperty("handRight", Float.toString(handRight));
      props.setProperty("handUp", Float.toString(handUp));
      props.setProperty("timeOverride", Boolean.toString(timeOverride));
      props.setProperty("timeTicks", Integer.toString(timeTicks));
      props.setProperty("motionBlur", Boolean.toString(motionBlur));
      props.setProperty("blurAmount", Float.toString(blurAmount));
      props.setProperty("nameBgMigrated", Boolean.toString(nameBgMigrated));
      props.setProperty("hideOwnParticles", Boolean.toString(hideOwnParticles));
      props.setProperty("customParticles", Boolean.toString(customParticles));
      props.setProperty("colorDefaultsMigrated", Boolean.toString(colorDefaultsMigrated));
      props.setProperty("fovDefaultOffMigrated", Boolean.toString(fovDefaultOffMigrated));
      props.setProperty("rawInput", Boolean.toString(rawInput));
      props.setProperty("hideMiningParticles", Boolean.toString(hideMiningParticles));
      props.setProperty("hideSprintParticles", Boolean.toString(hideSprintParticles));
      props.setProperty("critParticleMultiplier", Float.toString(critParticleMultiplier));
      props.setProperty("sharpParticleMultiplier", Float.toString(sharpParticleMultiplier));
      props.setProperty("imeFix", Boolean.toString(imeFix));
      props.setProperty("fireHeight", Integer.toString(fireHeight));
      props.setProperty("leftHanded", Boolean.toString(leftHanded));
      props.setProperty("msClientId", msClientId == null ? "" : msClientId);
      props.setProperty("blurStyle", Integer.toString(blurStyle));
      File tmp = new File(file.getPath() + ".tmp");

      try {
         try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8)) {
            props.store(out, "Client 1.8.9 settings");
         }

         try {
            Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException var8) {
            Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
         }

         dirty = false;
      } catch (Exception var101) {
         ClientLog.warn("配置写入失败: " + file, var101);
      }
   }

   public static void saveSoon() {
      dirty = true;
   }

   public static synchronized void flushIfDirty() {
      if (dirty) {
         long now = System.currentTimeMillis();
         if (now - lastFlush >= 2000L) {
            lastFlush = now;
            save();
         }
      }
   }

   public static void validate() {
      afterimageLife = clamp(afterimageLife, 200, 4000);
      afterimageStrength = clamp(afterimageStrength, 0.2F, 2.0F);
      afterimageLayers = clamp(afterimageLayers, 0, 10);
      fireHeight = clamp(fireHeight, 0, 100);
      fovBase = clamp(fovBase, 30.0F, 110.0F);
      fovGlobal = clamp(fovGlobal, 0.5F, 1.5F);
      fovMin = clamp(fovMin, 10.0F, 110.0F);
      fovMax = clamp(fovMax, 30.0F, 160.0F);
      if (fovMax <= fovMin) {
         fovMax = fovMin + 10.0F;
      }

      fovSprint = clamp(fovSprint, 10.0F, 160.0F);
      fovSpeed = clamp(fovSpeed, 10.0F, 160.0F);
      fovSlow = clamp(fovSlow, 10.0F, 160.0F);
      fovFly = clamp(fovFly, 10.0F, 160.0F);
      fovAim = clamp(fovAim, 10.0F, 160.0F);
      fovWater = clamp(fovWater, 10.0F, 160.0F);
      if (afterimageColor < 0) {
         afterimageColor = 16777215;
      }

      blurStyle = clamp(blurStyle, 0, 3);
      scoreboardMaxLines = clamp(scoreboardMaxLines, 1, 15);
      if (scoreboardTitle == null) {
         scoreboardTitle = "";
      }

      if (scoreboardTitle.length() > 48) {
         scoreboardTitle = scoreboardTitle.substring(0, 48);
      }

      if (scoreboardText == null) {
         scoreboardText = "";
      }

      scoreboardCustom = true;
      scoreboardShowTitle = true;
      scoreboardText = scoreboardText.replace("\r", "");
      if (scoreboardText.length() > 960) {
         scoreboardText = scoreboardText.substring(0, 960);
      }
   }

   public static synchronized void resetAll() {
      for (Field f : ClientConfig.class.getDeclaredFields()) {
         Object def = DEFAULTS.get(f.getName());
         if (def != null) {
            try {
               f.setAccessible(true);
               f.set(null, def);
            } catch (Throwable var6) {
            }
         }
      }

      validate();
   }

   private static int clamp(int v, int lo, int hi) {
      return v < lo ? lo : (v > hi ? hi : v);
   }

   private static float clamp(float v, float lo, float hi) {
      return v < lo ? lo : (v > hi ? hi : v);
   }

   private static boolean readBool(Properties props, String key, boolean def) {
      String v = props.getProperty(key);
      return v == null ? def : Boolean.parseBoolean(v.trim());
   }

   private static int readInt(Properties props, String key, int def) {
      try {
         String v = props.getProperty(key);
         return v == null ? def : Integer.parseInt(v.trim());
      } catch (Exception var41) {
         return def;
      }
   }

   private static float readFloat(Properties props, String key, float def) {
      try {
         String v = props.getProperty(key);
         return v == null ? def : Float.parseFloat(v.trim());
      } catch (Exception var41) {
         return def;
      }
   }

   private static int readColor(Properties props, String key, int def) {
      try {
         String v = props.getProperty(key);
         if (v == null) {
            return def;
         } else {
            v = v.trim();
            if (v.startsWith("#")) {
               v = v.substring(1);
            }

            if (v.length() == 6) {
               v = "FF" + v;
            }

            return (int)Long.parseLong(v, 16);
         }
      } catch (Exception var41) {
         return def;
      }
   }

   public static String t(String zh, String en) {
      return langIndex() == 0 ? zh : en;
   }

   public static String toHex(int argb) {
      return String.format("#%08X", argb);
   }

   static {
      for (Field f : ClientConfig.class.getDeclaredFields()) {
         int mod = f.getModifiers();
         if (Modifier.isStatic(mod) && !Modifier.isFinal(mod)) {
            String name = f.getName();
            if (!name.equals("loaded")
               && !name.equals("dirty")
               && !name.equals("lastFlush")
               && !name.equals("msClientId")
               && !name.equals("DEFAULTS")
               && !name.equals("FULLBRIGHT_GAMMA")) {
               try {
                  f.setAccessible(true);
                  Object v = f.get(null);
                  if (v != null) {
                     DEFAULTS.put(name, v);
                  }
               } catch (Throwable var7) {
               }
            }
         }
      }
   }
}
