package com.cold.push.server;

import com.cold.push.util.ThreadUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author liaowenhui
 * @date 2017/3/3 19:00.
 */
public class IMServer {

    private volatile boolean stop;
    private ExecutorService pushListenerPool;
    private PushListener pushListener;

    private IMServer() {
        pushListenerPool = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("pushListener-pool-%d").build());
    }

    public void start() {
        init();
        System.out.println("imServer is up...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
            System.out.println("imServer shutdown!");
        }));

        while (!stop) {

        }

        quit();
    }



    private void init() {
        initPushListener();
    }

    private void initPushListener() {
        pushListener = new PushListener();
        pushListenerPool.execute(pushListener);

    }

    private void quit() {
        ThreadUtils.shutdown(pushListenerPool, 5, TimeUnit.SECONDS);
    }

    public void stop() {
        stop = true;
    }

    public static IMServer getInstance() {
        return IMServerHolder.INSTANCE;
    }

    private static class IMServerHolder {
        private static final IMServer INSTANCE = new IMServer();
    }
}
