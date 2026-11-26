package net.client;

import java.util.List;
import net.client.settings.SettingsSpec;
import net.client.util.ClientLog;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client implements ClientModInitializer {
   public static final String MOD_ID = "client";
   public static final Logger LOGGER = LogManager.getLogger("Client");

   @Override
   public void onInitializeClient() {
      ClientConfig.ensureLoaded();
      List<String> specProblems = SettingsSpec.validate();
      if (!specProblems.isEmpty()) {
         LOGGER.error("[Client] 设置表自检失败: {}", specProblems);
      }

      probeNanoVg();
      LOGGER.info("[Client] initialised, press Right Shift to open the menu");
   }

   private static void probeNanoVg() {
      String arch = System.getProperty("os.arch", "?");
      String os = System.getProperty("os.name", "?");
      boolean core = false;
      boolean gl2 = false;
      String err = "";

      try {
         Class.forName("org.lwjgl.nanovg.NanoVG");
         core = true;
      } catch (Exception var7) {
         err = var7.toString();
      }

      try {
         Class.forName("org.lwjgl.nanovg.NanoVGGL2");
         gl2 = true;
      } catch (Exception var6) {
         ClientLog.warn("NanoVGGL2 类探测失败", var6);
      }

      LOGGER.info("[Client] NanoVG 自检: os={} arch={} class={} gl2={} err={}", os, arch, core, gl2, err);
   }
}
