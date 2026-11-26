package com.mcpacks.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadPoolHelper {
   private static final int CPU_CORES = Math.max(2, Runtime.getRuntime().availableProcessors());
   private static final int WORKER_THREADS = Math.min(6, CPU_CORES);
   private static final ExecutorService ASYNC_EXECUTOR = new ThreadPoolExecutor(
      WORKER_THREADS, WORKER_THREADS, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolHelper.NamedThreadFactory("McPacks-Worker")
   );
   private static final ExecutorService INDEXER_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadPoolHelper.NamedThreadFactory("McPacks-Indexer"));

   private ThreadPoolHelper() {
   }

   public static ExecutorService getAsyncExecutor() {
      return ASYNC_EXECUTOR;
   }

   public static ExecutorService getIndexerExecutor() {
      return INDEXER_EXECUTOR;
   }

   public static void submitTask(Runnable task) {
      if (!ASYNC_EXECUTOR.isShutdown()) {
         ASYNC_EXECUTOR.submit(task);
      }
   }

   public static void submitIndexTask(Runnable task) {
      if (!INDEXER_EXECUTOR.isShutdown()) {
         INDEXER_EXECUTOR.submit(task);
      }
   }

   private static class NamedThreadFactory implements ThreadFactory {
      private final String prefix;
      private final AtomicInteger threadNumber = new AtomicInteger(1);

      public NamedThreadFactory(String prefix) {
         this.prefix = prefix;
      }

      @Override
      public Thread newThread(Runnable r) {
         Thread t = new Thread(r, this.prefix + "-" + this.threadNumber.getAndIncrement());
         t.setDaemon(true);
         t.setPriority(4);
         return t;
      }
   }
}
