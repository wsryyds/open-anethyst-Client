package com.mcpacks.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

public final class FolderUtils {
   public static final int MAX_NESTED_DEPTH = 3;

   private FolderUtils() {
   }

   public static int calculateDepth(File rootDir, File currentDir) {
      if (rootDir != null && currentDir != null) {
         try {
            Path rootPath = rootDir.getCanonicalFile().toPath();
            Path currentPath = currentDir.getCanonicalFile().toPath();
            if (currentPath.equals(rootPath)) {
               return 0;
            } else if (!currentPath.startsWith(rootPath)) {
               return 0;
            } else {
               Path relative = rootPath.relativize(currentPath);
               return relative.getNameCount();
            }
         } catch (IOException var51) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public static boolean canCreateSubfolder(File rootDir, File currentDir) {
      int depth = calculateDepth(rootDir, currentDir);
      return depth < 3;
   }

   public static String getRelativePath(File rootDir, File targetFile) {
      if (rootDir != null && targetFile != null) {
         try {
            Path rootPath = rootDir.getCanonicalFile().toPath();
            Path targetPath = targetFile.getCanonicalFile().toPath();
            if (targetPath.equals(rootPath)) {
               return "";
            } else if (targetPath.startsWith(rootPath)) {
               Path rel = rootPath.relativize(targetPath);
               return rel.toString().replace('\\', '/');
            } else {
               return targetFile.getName();
            }
         } catch (IOException var51) {
            return targetFile.getName();
         }
      } else {
         return "";
      }
   }

   public static boolean isResourcePackArchive(File file) {
      if (file != null && file.isFile()) {
         String name = file.getName().toLowerCase(Locale.ROOT);
         return name.endsWith(".zip") || name.endsWith(".jar");
      } else {
         return false;
      }
   }

   public static boolean isResourcePackDirectory(File file) {
      if (file != null && file.isDirectory()) {
         File mcmeta = new File(file, "pack.mcmeta");
         return mcmeta.exists() && mcmeta.isFile();
      } else {
         return false;
      }
   }

   public static boolean isResourcePack(File file) {
      return isResourcePackArchive(file) || isResourcePackDirectory(file);
   }

   public static boolean isCategoryFolder(File file) {
      return file != null && file.isDirectory() && !file.isHidden() ? !isResourcePackDirectory(file) : false;
   }

   public static int countDirectPacks(File folder) {
      if (folder != null && folder.isDirectory()) {
         File[] files = folder.listFiles();
         if (files == null) {
            return 0;
         } else {
            int count = 0;

            for (File f : files) {
               if (isResourcePack(f)) {
                  count++;
               }
            }

            return count;
         }
      } else {
         return 0;
      }
   }

   public static int countAllPacksRecursive(File folder, int currentDepth) {
      if (folder != null && folder.isDirectory()) {
         File[] files = folder.listFiles();
         if (files == null) {
            return 0;
         } else {
            int count = 0;

            for (File f : files) {
               if (!f.isHidden()) {
                  if (isResourcePack(f)) {
                     count++;
                  } else if (f.isDirectory() && currentDepth < 3) {
                     count += countAllPacksRecursive(f, currentDepth + 1);
                  }
               }
            }

            return count;
         }
      } else {
         return 0;
      }
   }

   public static String sanitizeFolderName(String name) {
      if (name == null) {
         return "";
      } else {
         String sanitized = name.replaceAll("[\\\\/:*?\"<>|]+", "_").trim();
         return sanitized.replaceAll("^_+|_+$", "");
      }
   }
}
