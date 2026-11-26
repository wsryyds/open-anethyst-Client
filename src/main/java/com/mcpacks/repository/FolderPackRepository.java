package com.mcpacks.repository;

import com.mcpacks.core.PackEntry;
import com.mcpacks.core.PackManager;
import com.mcpacks.util.FolderUtils;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import net.client.util.ClientLog;
import net.minecraft.class_1600;
import net.minecraft.class_1657;
import net.minecraft.class_1657.class_1659;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FolderPackRepository {
   private static final Logger LOGGER = LogManager.getLogger("mcpacks");
   private final class_1600 mc;
   private final class_1657 loader;
   private static Constructor<?> entryConstructor;
   private static Field resourcePackFileField;
   private static boolean reflected;

   public FolderPackRepository(class_1600 mc) {
      this.mc = mc;
      this.loader = mc.method_5572();
   }

   private static synchronized void reflectEntryClass() {
      if (!reflected && !reflected) {
         reflected = true;

         try {
            Class<?> entryClass = class_1659.class;

            for (Constructor<?> c : entryClass.getDeclaredConstructors()) {
               Class<?>[] params = c.getParameterTypes();
               if (params.length == 1 && File.class.isAssignableFrom(params[0])) {
                  c.setAccessible(true);
                  entryConstructor = c;
                  break;
               }

               if (params.length == 2 && File.class.isAssignableFrom(params[1])) {
                  c.setAccessible(true);
                  entryConstructor = c;
                  break;
               }
            }

            for (Field f : entryClass.getDeclaredFields()) {
               if (File.class.isAssignableFrom(f.getType())) {
                  f.setAccessible(true);
                  resourcePackFileField = f;
                  break;
               }
            }
         } catch (Exception var61) {
            var61.printStackTrace();
         }
      }
   }

   public class_1659 createEntryForFile(File packFile) {
      if (packFile != null && packFile.exists()) {
         reflectEntryClass();
         if (entryConstructor == null) {
            return null;
         } else {
            try {
               class_1659 entry;
               if (entryConstructor.getParameterTypes().length == 2) {
                  entry = (class_1659)entryConstructor.newInstance(this.loader, packFile);
               } else {
                  entry = (class_1659)entryConstructor.newInstance(packFile);
               }

               initializeEntry(entry);
               return entry;
            } catch (Exception var31) {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static void initializeEntry(class_1659 entry) {
      if (entry != null && entry.method_5913() == null) {
         for (Method method : class_1659.class.getDeclaredMethods()) {
            if (method.getParameterTypes().length == 0 && method.getReturnType() == void.class && !Modifier.isStatic(method.getModifiers())) {
               try {
                  method.setAccessible(true);
                  method.invoke(entry);
               } catch (Exception var6) {
                  ClientLog.warn("反射调用资源包条目方法失败", var6);
               }

               if (entry.method_5913() != null) {
                  return;
               }
            }
         }
      }
   }

   public static File getFileFromEntry(class_1659 entry) {
      if (entry == null) {
         return null;
      } else {
         reflectEntryClass();

         try {
            if (resourcePackFileField != null) {
               return (File)resourcePackFileField.get(entry);
            }
         } catch (Exception var2) {
            ClientLog.warn("反射读取资源包条目文件字段失败", var2);
         }

         return null;
      }
   }

   public File findPackFile(String rawPath) {
      if (rawPath != null && !rawPath.trim().isEmpty()) {
         File rootDir = PackManager.getInstance().getRootDir();
         String normalized = rawPath.replace('\\', '/').trim();
         File direct = new File(rootDir, normalized);
         if (direct.exists()) {
            return direct;
         } else {
            File rootFile = new File(rootDir, new File(normalized).getName());
            if (rootFile.exists()) {
               return rootFile;
            } else {
               for (PackEntry indexed : PackManager.getInstance().getIndexer().getAllIndexedPacks()) {
                  if (indexed.getRelativePath().equalsIgnoreCase(normalized) || indexed.getFile().getName().equalsIgnoreCase(normalized)) {
                     return indexed.getFile();
                  }
               }

               return null;
            }
         }
      } else {
         return null;
      }
   }

   public List<class_1659> getSelectedRepositoryEntries() {
      List<class_1659> selected = new ArrayList<>();
      List<class_1659> current = this.loader.method_5905();
      if (current != null) {
         selected.addAll(current);
      }

      return selected;
   }

   public void applySelectedPacks(List<PackEntry> selectedEntries) {
      List<class_1659> repoEntries = new ArrayList<>();
      List<String> settingNames = new ArrayList<>();
      File rootDir = PackManager.getInstance().getRootDir();

      for (PackEntry entry : selectedEntries) {
         if (entry.isPack() && entry.getFile() != null) {
            class_1659 repoEntry = null;
            Object cached = entry.getRepositoryEntry();
            if (cached instanceof class_1659) {
               repoEntry = (class_1659)cached;
            }

            if (repoEntry == null) {
               repoEntry = this.createEntryForFile(entry.getFile());
               entry.setRepositoryEntry(repoEntry);
            }

            if (repoEntry != null) {
               repoEntries.add(repoEntry);
            }

            settingNames.add(FolderUtils.getRelativePath(rootDir, entry.getFile()));
         }
      }

      this.loader.method_7038(repoEntries);
      LOGGER.info("Applying {} pack(s): {}", repoEntries.size(), settingNames);
      List<String> currentSettings = this.mc.field_3823.field_7670;
      boolean changed = currentSettings == null || !settingNames.equals(currentSettings);
      if (currentSettings != null) {
         currentSettings.clear();
         currentSettings.addAll(settingNames);
      }

      this.mc.field_3823.method_873();
      if (changed) {
         this.mc.method_5576();
      }
   }
}
