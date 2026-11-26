package net.client.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.class_485;

public final class ServerListPingFix {
   private static final int MAX_THREADS = 50;
   private static final int PING_TIMEOUT_SECONDS = 4;
   private static final AtomicInteger RUNNING = new AtomicInteger();
   private static final ScheduledExecutorService TIMEOUT = new ScheduledThreadPoolExecutor(
      1, new ThreadFactoryBuilder().setNameFormat("Client Ping Timeout #%d").setDaemon(true).build()
   );
   private static volatile int serverCount;
   private static volatile int poolSize;
   private static volatile ThreadPoolExecutor executor;

   private ServerListPingFix() {
   }

   public static synchronized void configure(int count) {
      serverCount = Math.max(0, count);
      int wanted = Math.min(50, Math.max(6, serverCount + 5));
      if (executor == null || poolSize != wanted) {
         ThreadPoolExecutor old = executor;
         poolSize = wanted;
         ThreadPoolExecutor pool = new ThreadPoolExecutor(
            wanted,
            wanted,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("Client Server Pinger #%d").setDaemon(true).build()
         );
         pool.allowCoreThreadTimeOut(true);
         executor = pool;
      }
   }

   public static Future<?> submit(ThreadPoolExecutor vanillaPool, Runnable task, class_485 server) {
      if (task == null) {
         return CompletableFuture.completedFuture(null);
      } else {
         ThreadPoolExecutor pool = executor;
         if (pool == null) {
            configure(serverCount);
            pool = executor;
         }

         if (pool != null && !pool.isShutdown()) {
            int maxRunning = Math.min(80, Math.max(12, serverCount * 2));
            if (RUNNING.get() >= maxRunning) {
               fail(server, "§7Refreshing too fast");
               return CompletableFuture.completedFuture(null);
            } else {
               RUNNING.incrementAndGet();

               final Future<?> future;
               try {
                  future = pool.submit(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           task.run();
                        } finally {
                           ServerListPingFix.RUNNING.decrementAndGet();
                        }
                     }
                  });
               } catch (RejectedExecutionException var7) {
                  RUNNING.decrementAndGet();
                  fail(server, "§7Ping skipped");
                  return CompletableFuture.completedFuture(null);
               }

               TIMEOUT.schedule(new Runnable() {
                  @Override
                  public void run() {
                     if (!future.isDone()) {
                        future.cancel(true);
                     }

                     if (server.field_1690 < 0L) {
                        ServerListPingFix.fail(server, "§cTimed out");
                     }
                  }
               }, 4L, TimeUnit.SECONDS);
               return future;
            }
         } else {
            fail(server, "§7Ping skipped");
            return CompletableFuture.completedFuture(null);
         }
      }
   }

   private static void fail(class_485 server, String label) {
      if (server != null) {
         server.field_1690 = -1L;
         server.field_1691 = true;
         server.field_1689 = label;
         server.field_1688 = "";
      }
   }
}
