package com.ayou.pulsardemo.infrastructure.annotation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * PulsarListenerExecutor
 *
 * @author ysy
 * @blame ysy
 * @date 2020-01-13
 */
public class PulsarListenerExecutor {
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void execute(Runnable task) {
        pool.execute(task);
    }

    @Override
    protected void finalize() throws Throwable {
        while (true) {
            try {
                pool.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
            break;
        }
        super.finalize();
    }

    public static void shutdown() {
        pool.shutdown();
    }

    public static boolean isRunning() {
        return pool.isTerminated();
    }
}
