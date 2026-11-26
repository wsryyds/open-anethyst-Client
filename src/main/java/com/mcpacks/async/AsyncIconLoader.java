package com.mcpacks.async;

import com.mcpacks.core.PackEntry;
import com.mcpacks.util.FolderUtils;
import com.mcpacks.util.IconUtils;
import com.mcpacks.util.JsonUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import net.client.util.ClientLog;
import net.minecraft.class_1532;
import net.minecraft.class_1600;
import net.minecraft.class_1636;
import net.minecraft.class_1653;

public class AsyncIconLoader {
   private static final AsyncIconLoader INSTANCE = new AsyncIconLoader();
   private final Map<String, class_1653> textureCache = new ConcurrentHashMap<>();
   private final Map<String, JsonUtils.PackMeta> metaCache = new ConcurrentHashMap<>();
   private class_1653 folderTextureLoc = null;
   private class_1653 parentNavTextureLoc = null;
   private class_1653 defaultPackTextureLoc = null;

   private AsyncIconLoader() {
   }

   public static AsyncIconLoader getInstance() {
      return INSTANCE;
   }

   public void initDefaultTextures() {
      class_1600 mc = class_1600.method_2965();
      if (mc != null) {
         class_1532 tm = mc.method_5570();
         if (this.folderTextureLoc == null) {
            this.folderTextureLoc = tm.method_5851("mcpacks_folder_icon", new class_1636(IconUtils.getFolderIcon()));
         }

         if (this.parentNavTextureLoc == null) {
            this.parentNavTextureLoc = tm.method_5851("mcpacks_nav_icon", new class_1636(IconUtils.getParentNavIcon()));
         }

         if (this.defaultPackTextureLoc == null) {
            this.defaultPackTextureLoc = tm.method_5851("mcpacks_default_pack_icon", new class_1636(IconUtils.getDefaultPackIcon()));
         }
      }
   }

   public class_1653 getFolderTextureLoc() {
      if (this.folderTextureLoc == null) {
         this.initDefaultTextures();
      }

      return this.folderTextureLoc;
   }

   public class_1653 getParentNavTextureLoc() {
      if (this.parentNavTextureLoc == null) {
         this.initDefaultTextures();
      }

      return this.parentNavTextureLoc;
   }

   public class_1653 getDefaultPackTextureLoc() {
      if (this.defaultPackTextureLoc == null) {
         this.initDefaultTextures();
      }

      return this.defaultPackTextureLoc;
   }

   public void requestIconAndMeta(PackEntry entry) {
      if (entry != null) {
         if (entry.isParentNav()) {
            entry.setIconLocation(this.getParentNavTextureLoc());
            entry.setIconLoaded(true);
         } else if (entry.isFolder()) {
            entry.setIconLocation(this.getFolderTextureLoc());
            entry.setIconLoaded(true);
         } else {
            final File file = entry.getFile();
            if (file != null && file.exists()) {
               final String pathKey = file.getAbsolutePath();
               class_1653 cached = this.textureCache.get(pathKey);
               if (cached != null) {
                  entry.setIconLocation(cached);
                  entry.setIconLoaded(true);
                  JsonUtils.PackMeta cachedMeta = this.metaCache.get(pathKey);
                  if (cachedMeta != null) {
                     entry.setPackFormat(cachedMeta.packFormat);
                     entry.setDescription(cachedMeta.description);
                     entry.setMetadataLoaded(true);
                  }
               } else {
                  entry.setIconLocation(this.getDefaultPackTextureLoc());
                  if (!entry.isIconLoadingStarted()) {
                     entry.setIconLoadingStarted(true);
                     ThreadPoolHelper.submitTask(new Runnable() {
                        @Override
                        public void run() {
                           AsyncIconLoader.this.loadPackAsync(entry, file, pathKey);
                        }
                     });
                  }
               }
            }
         }
      }
   }

   private void loadPackAsync(PackEntry entry, File file, String pathKey) {
      BufferedImage loadedImage = null;
      JsonUtils.PackMeta meta = null;
      if (FolderUtils.isResourcePackArchive(file)) {
         ZipFile zipFile = null;

         try {
            zipFile = new ZipFile(file);
            if (!entry.isMetadataLoaded()) {
               ZipEntry metaEntry = zipFile.getEntry("pack.mcmeta");
               if (metaEntry != null) {
                  try (InputStream is = zipFile.getInputStream(metaEntry)) {
                     meta = JsonUtils.parsePackMeta(is);
                  }
               }
            }

            ZipEntry iconEntry = zipFile.getEntry("pack.png");
            if (iconEntry != null) {
               try (InputStream is = zipFile.getInputStream(iconEntry)) {
                  loadedImage = ImageIO.read(is);
               }
            }
         } catch (Exception var33) {
            ClientLog.warn("读取资源包内置图标失败，保留默认图标", var33);
         } finally {
            if (zipFile != null) {
               try {
                  zipFile.close();
               } catch (Exception var25) {
                  ClientLog.warn("关闭图标 ZipFile 失败", var25);
               }
            }
         }
      } else if (FolderUtils.isResourcePackDirectory(file)) {
         try {
            File metaFile = new File(file, "pack.mcmeta");
            if (!entry.isMetadataLoaded() && metaFile.exists()) {
               try (InputStream is = new FileInputStream(metaFile)) {
                  meta = JsonUtils.parsePackMeta(is);
               }
            }

            File iconFile = new File(file, "pack.png");
            if (iconFile.exists()) {
               loadedImage = ImageIO.read(iconFile);
            }
         } catch (Exception var30) {
            ClientLog.warn("读取文件夹资源包图标失败，保留默认图标", var30);
         }
      }

      if (meta != null) {
         entry.setPackFormat(meta.packFormat);
         entry.setDescription(meta.description);
         entry.setMetadataLoaded(true);
         this.metaCache.put(pathKey, meta);
      }

      final BufferedImage finalImage = loadedImage;
      class_1600.method_2965().method_6637(new Callable<Void>() {
         public Void call() {
            try {
               class_1532 tm = class_1600.method_2965().method_5570();
               class_1653 finalLoc;
               if (finalImage != null) {
                  class_1636 texture = new class_1636(finalImage);
                  String safeName = "pack_" + Math.abs(pathKey.hashCode());
                  finalLoc = tm.method_5851(safeName, texture);
                  AsyncIconLoader.this.textureCache.put(pathKey, finalLoc);
               } else {
                  finalLoc = AsyncIconLoader.this.getDefaultPackTextureLoc();
               }

               if (finalImage != null) {
                  entry.setIconTextureSize(finalImage.getWidth(), finalImage.getHeight());
               }

               entry.setIconLocation(finalLoc);
               entry.setIconLoaded(true);
            } catch (Exception var51) {
               entry.setIconLocation(AsyncIconLoader.this.getDefaultPackTextureLoc());
               entry.setIconLoaded(true);
            }

            return null;
         }
      });
   }

   public void clearCache() {
      this.textureCache.clear();
      this.metaCache.clear();
   }
}
