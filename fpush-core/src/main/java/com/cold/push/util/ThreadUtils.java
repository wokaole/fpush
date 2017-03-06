package com.cold.push.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by faker on 2017/3/4.
 */
public class ThreadUtils {

    public static void shutdown(ExecutorService pool, int timeout, TimeUnit timeUnit) {

        try {
            pool.shutdownNow();
            if (!pool.awaitTermination(timeout, timeUnit)) {
                System.err.println("Pool did not terminated");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
