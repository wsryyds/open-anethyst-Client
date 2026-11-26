package com.mcpacks.core;

import com.mcpacks.util.FolderUtils;
import java.io.File;
import net.minecraft.class_1653;

public class PackEntry {
   private final PackType type;
   private final File file;
   private final String name;
   private final String relativePath;
   private final int depth;
   private int childPackCount;
   private int packFormat = 1;
   private String description = "";
   private class_1653 iconLocation = null;
   private int iconTextureWidth = 64;
   private int iconTextureHeight = 64;
   private volatile boolean iconLoaded = false;
   private volatile boolean iconLoadingStarted = false;
   private volatile boolean metadataLoaded = false;
   private Object repositoryEntry;

   public PackEntry(PackType type, File file, File rootDir, int depth) {
      this.type = type;
      this.file = file;
      this.depth = depth;
      if (type == PackType.PARENT_NAV) {
         this.name = ".. (Up to parent folder)";
         this.relativePath = "";
         this.description = "Go up one folder level";
         this.metadataLoaded = true;
         this.iconLoaded = true;
      } else {
         String rawName = file != null ? file.getName() : "";
         this.name = type == PackType.PACK && rawName.toLowerCase().endsWith(".zip") ? rawName.substring(0, rawName.length() - 4) : rawName;
         this.relativePath = file != null && rootDir != null ? FolderUtils.getRelativePath(rootDir, file) : "";
      }
   }

   public PackType getType() {
      return this.type;
   }

   public boolean isFolder() {
      return this.type == PackType.FOLDER;
   }

   public boolean isPack() {
      return this.type == PackType.PACK;
   }

   public boolean isParentNav() {
      return this.type == PackType.PARENT_NAV;
   }

   public File getFile() {
      return this.file;
   }

   public String getName() {
      return this.name;
   }

   public String getRelativePath() {
      return this.relativePath;
   }

   public int getDepth() {
      return this.depth;
   }

   public int getChildPackCount() {
      return this.childPackCount;
   }

   public void setChildPackCount(int childPackCount) {
      this.childPackCount = childPackCount;
   }

   public int getPackFormat() {
      return this.packFormat;
   }

   public void setPackFormat(int packFormat) {
      this.packFormat = packFormat;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public class_1653 getIconLocation() {
      return this.iconLocation;
   }

   public int getIconTextureWidth() {
      return this.iconTextureWidth;
   }

   public int getIconTextureHeight() {
      return this.iconTextureHeight;
   }

   public void setIconTextureSize(int iconTextureWidth, int iconTextureHeight) {
      this.iconTextureWidth = iconTextureWidth > 0 ? iconTextureWidth : 64;
      this.iconTextureHeight = iconTextureHeight > 0 ? iconTextureHeight : 64;
   }

   public void setIconLocation(class_1653 iconLocation) {
      this.iconLocation = iconLocation;
   }

   public boolean isIconLoaded() {
      return this.iconLoaded;
   }

   public void setIconLoaded(boolean iconLoaded) {
      this.iconLoaded = iconLoaded;
   }

   public boolean isIconLoadingStarted() {
      return this.iconLoadingStarted;
   }

   public void setIconLoadingStarted(boolean iconLoadingStarted) {
      this.iconLoadingStarted = iconLoadingStarted;
   }

   public boolean isMetadataLoaded() {
      return this.metadataLoaded;
   }

   public void setMetadataLoaded(boolean metadataLoaded) {
      this.metadataLoaded = metadataLoaded;
   }

   public Object getRepositoryEntry() {
      return this.repositoryEntry;
   }

   public void setRepositoryEntry(Object repositoryEntry) {
      this.repositoryEntry = repositoryEntry;
   }

   public boolean isVersionCompatible() {
      return this.packFormat == 1;
   }
}
