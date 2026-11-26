package com.mcpacks.core;

import com.mcpacks.async.AsyncPackIndexer;
import com.mcpacks.util.FolderUtils;
import com.mcpacks.util.Lang;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_1600;

public class PackManager {
   private static PackManager instance;
   private final class_1600 mc;
   private final File rootDir;
   private File currentDir;
   private final AsyncPackIndexer indexer;

   public PackManager(class_1600 mc) {
      this.mc = mc;
      this.rootDir = new File(mc.field_3762, "resourcepacks");
      if (!this.rootDir.exists()) {
         this.rootDir.mkdirs();
      }

      this.currentDir = this.rootDir;
      this.indexer = new AsyncPackIndexer(this.rootDir);
   }

   public static synchronized void init(class_1600 mc) {
      if (instance == null) {
         instance = new PackManager(mc);
      }
   }

   public static PackManager getInstance() {
      if (instance == null) {
         instance = new PackManager(class_1600.method_2965());
      }

      return instance;
   }

   public File getRootDir() {
      return this.rootDir;
   }

   public File getCurrentDir() {
      return this.currentDir;
   }

   public int getCurrentDepth() {
      return FolderUtils.calculateDepth(this.rootDir, this.currentDir);
   }

   public AsyncPackIndexer getIndexer() {
      return this.indexer;
   }

   public boolean navigateTo(File targetDir) {
      if (targetDir != null && targetDir.exists() && targetDir.isDirectory()) {
         if (FolderUtils.calculateDepth(this.rootDir, targetDir) > 3) {
            return false;
         } else {
            this.currentDir = targetDir;
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean navigateUp() {
      if (this.isAtRoot()) {
         return false;
      } else {
         File parent = this.currentDir.getParentFile();
         if (parent != null && parent.exists()) {
            this.currentDir = parent;
         } else {
            this.currentDir = this.rootDir;
         }

         return true;
      }
   }

   public void navigateToRoot() {
      this.currentDir = this.rootDir;
   }

   public boolean isAtRoot() {
      return this.currentDir.equals(this.rootDir);
   }

   public List<PackManager.BreadcrumbNode> getBreadcrumbs() {
      List<PackManager.BreadcrumbNode> list = new ArrayList<>();
      list.add(new PackManager.BreadcrumbNode("Home", this.rootDir, 0));
      if (this.isAtRoot()) {
         return list;
      } else {
         try {
            Path rootPath = this.rootDir.getCanonicalFile().toPath();
            Path currentPath = this.currentDir.getCanonicalFile().toPath();
            if (currentPath.startsWith(rootPath)) {
               Path rel = rootPath.relativize(currentPath);
               Path accum = rootPath;
               int depth = 0;

               for (int i = 0; i < rel.getNameCount(); i++) {
                  accum = accum.resolve(rel.getName(i));
                  list.add(new PackManager.BreadcrumbNode(rel.getName(i).toString(), accum.toFile(), ++depth));
               }
            }
         } catch (IOException var81) {
            list.add(new PackManager.BreadcrumbNode(this.currentDir.getName(), this.currentDir, this.getCurrentDepth()));
         }

         return list;
      }
   }

   public List<PackEntry> getEntriesInCurrentFolder(String filterQuery) {
      List<PackEntry> entries = new ArrayList<>();
      int depth = this.getCurrentDepth();
      if (!this.isAtRoot()) {
         entries.add(new PackEntry(PackType.PARENT_NAV, this.currentDir.getParentFile(), this.rootDir, depth - 1));
      }

      File[] files = this.currentDir.listFiles();
      if (files == null) {
         return entries;
      } else {
         List<PackEntry> folderEntries = new ArrayList<>();
         List<PackEntry> packEntries = new ArrayList<>();
         String filter = filterQuery != null ? filterQuery.trim().toLowerCase(Locale.ROOT) : "";

         for (File file : files) {
            if (!file.isHidden()) {
               if (FolderUtils.isCategoryFolder(file)) {
                  if (depth < 3 && (filter.isEmpty() || file.getName().toLowerCase(Locale.ROOT).contains(filter))) {
                     PackEntry folderEntry = new PackEntry(PackType.FOLDER, file, this.rootDir, depth + 1);
                     int count = this.indexer.getPackCountForFolder(file);
                     if (count == 0) {
                        count = FolderUtils.countAllPacksRecursive(file, depth + 1);
                     }

                     folderEntry.setChildPackCount(count);
                     folderEntry.setDescription(
                        Lang.isChinese()
                           ? count + " 个资源包  -  第 " + (depth + 1) + "/3 层"
                           : count + (count == 1 ? " pack" : " packs") + "  -  Layer " + (depth + 1) + "/3"
                     );
                     folderEntry.setMetadataLoaded(true);
                     folderEntries.add(folderEntry);
                  }
               } else if (FolderUtils.isResourcePack(file) && (filter.isEmpty() || file.getName().toLowerCase(Locale.ROOT).contains(filter))) {
                  packEntries.add(new PackEntry(PackType.PACK, file, this.rootDir, depth));
               }
            }
         }

         Comparator<PackEntry> byName = new Comparator<PackEntry>() {
            public int compare(PackEntry o1, PackEntry o2) {
               return o1.getName().compareToIgnoreCase(o2.getName());
            }
         };
         Collections.sort(folderEntries, byName);
         Collections.sort(packEntries, byName);
         entries.addAll(folderEntries);
         entries.addAll(packEntries);
         return entries;
      }
   }

   public boolean createNewFolder(String rawName) {
      if (!FolderUtils.canCreateSubfolder(this.rootDir, this.currentDir)) {
         return false;
      } else {
         String safeName = FolderUtils.sanitizeFolderName(rawName);
         if (safeName.isEmpty()) {
            return false;
         } else {
            File newDir = new File(this.currentDir, safeName);
            if (newDir.exists()) {
               return false;
            } else {
               boolean success = newDir.mkdirs();
               if (success) {
                  this.refresh();
               }

               return success;
            }
         }
      }
   }

   public void refresh() {
      this.indexer.startIndexing(null);
   }

   public void openCurrentFolderInOS() {
      try {
         if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.OPEN)) {
            Desktop.getDesktop().open(this.currentDir);
            return;
         }

         String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
         if (os.contains("mac")) {
            Runtime.getRuntime().exec(new String[]{"open", this.currentDir.getAbsolutePath()});
         } else if (os.contains("win")) {
            Runtime.getRuntime().exec(new String[]{"explorer.exe", this.currentDir.getAbsolutePath()});
         } else {
            Runtime.getRuntime().exec(new String[]{"xdg-open", this.currentDir.getAbsolutePath()});
         }
      } catch (Exception var21) {
         var21.printStackTrace();
      }
   }

   public static class BreadcrumbNode {
      public final String name;
      public final File targetDir;
      public final int depth;

      public BreadcrumbNode(String name, File targetDir, int depth) {
         this.name = name;
         this.targetDir = targetDir;
         this.depth = depth;
      }
   }
}
