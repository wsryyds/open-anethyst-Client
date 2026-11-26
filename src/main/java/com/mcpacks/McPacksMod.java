package com.mcpacks;

import com.mcpacks.core.PackManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_1600;

public class McPacksMod implements ClientModInitializer {
   public static final String MODID = "mcpacks";
   public static final String NAME = "McPacks";
   public static final String VERSION = "1.0.0";

   @Override
   public void onInitializeClient() {
      PackManager.init(class_1600.method_2965());
   }
}
