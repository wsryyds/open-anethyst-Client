package com.mcpacks.async;

import com.mcpacks.core.PackEntry;
import com.mcpacks.core.PackType;
import com.mcpacks.util.FolderUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.client.util.ClientLog;

public class AsyncPackIndexer {
   private final File rootDir;
   private final List<PackEntry> allIndexedPacks = new CopyOnWriteArrayList<>();
   private final Map<String, Integer> folderPackCountMap = new ConcurrentHashMap<>();
   private volatile boolean isIndexing = false;
   private volatile long lastIndexTime = 0L;

   public AsyncPackIndexer(File rootDir) {
      this.rootDir = rootDir;
   }

   public void startIndexing(Runnable onComplete) {
      if (this.rootDir != null && this.rootDir.exists() && !this.isIndexing) {
         this.isIndexing = true;
         ThreadPoolHelper.submitIndexTask(new Runnable() {
            @Override
            public void run() {
               try {
                  List<PackEntry> collected = new ArrayList<>();
                  Map<String, Integer> counts = new HashMap<>();
                  AsyncPackIndexer.this.scanDirectoryRecursive(AsyncPackIndexer.this.rootDir, 0, collected, counts);
                  AsyncPackIndexer.this.allIndexedPacks.clear();
                  AsyncPackIndexer.this.allIndexedPacks.addAll(collected);
                  AsyncPackIndexer.this.folderPackCountMap.clear();
                  AsyncPackIndexer.this.folderPackCountMap.putAll(counts);
                  AsyncPackIndexer.this.lastIndexTime = System.currentTimeMillis();
               } catch (Exception var61) {
                  ClientLog.warn("资源包索引失败，保留上一次索引结果", var61);
               } finally {
                  AsyncPackIndexer.this.isIndexing = false;
                  if (onComplete != null) {
                     onComplete.run();
                  }
               }
            }
         });
      }
   }

   private int scanDirectoryRecursive(File dir, int depth, List<PackEntry> collected, Map<String, Integer> counts) {
      if (dir != null && dir.isDirectory()) {
         File[] files = dir.listFiles();
         if (files == null) {
            return 0;
         } else {
            int packsInThisDirAndChildren = 0;

            for (File file : files) {
               if (!file.isHidden()) {
                  if (FolderUtils.isResourcePack(file)) {
                     collected.add(new PackEntry(PackType.PACK, file, this.rootDir, depth));
                     packsInThisDirAndChildren++;
                  } else if (file.isDirectory() && depth < 3) {
                     packsInThisDirAndChildren += this.scanDirectoryRecursive(file, depth + 1, collected, counts);
                  }
               }
            }

            counts.put(dir.getAbsolutePath(), packsInThisDirAndChildren);
            return packsInThisDirAndChildren;
         }
      } else {
         return 0;
      }
   }

   public int getPackCountForFolder(File folder) {
      if (folder == null) {
         return 0;
      } else {
         Integer count = this.folderPackCountMap.get(folder.getAbsolutePath());
         return count != null ? count : 0;
      }
   }

   public List<PackEntry> searchGlobal(String query) {
      if (query != null && !query.trim().isEmpty()) {
         String lower = query.toLowerCase(Locale.ROOT).trim();
         List<PackEntry> results = new ArrayList<>();

         for (PackEntry entry : this.allIndexedPacks) {
            if (entry.getName().toLowerCase(Locale.ROOT).contains(lower) || entry.getRelativePath().toLowerCase(Locale.ROOT).contains(lower)) {
               results.add(entry);
            }
         }

         return results;
      } else {
         return new ArrayList<>(this.allIndexedPacks);
      }
   }

   public boolean isIndexing() {
      return this.isIndexing;
   }

   public boolean hasIndex() {
      return this.lastIndexTime > 0L;
   }

   public long getLastIndexTime() {
      return this.lastIndexTime;
   }

   public List<PackEntry> getAllIndexedPacks() {
      return this.allIndexedPacks;
   }
}
