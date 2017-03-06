package com.cold.push.server;

import com.cold.push.constant.PushConstants;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * push监听(应用服务器向DDPUSH服务器推送)
 * Created by faker on 2017/3/4.
 */
public class PushListener implements Runnable{

    private static int PORT = 9999;
    private volatile boolean stop;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ExecutorService pushPool;

    public PushListener() {
        init();
    }

    @Override
    public void run() {

        System.out.println("push listener port:" + PORT);

        while (!stop && selector != null) {
            handleChannel();
        }

        closeSelector();
    }

    private void init() {
        initPool();
        initChannel();
    }

    private void initPool() {
        pushPool = Executors.newFixedThreadPool(PushConstants.PUSH_LISTENER_MAX_THREAD,
                new ThreadFactoryBuilder().setNameFormat("push-pool-%d").build());
    }

    private void initChannel() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("NIO TCP Push Listener nio provider: "+selector.provider().getClass().getCanonicalName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleChannel() {
        try {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            keys.forEach(key -> {
                try {
                    keys.remove(key);
                    handleKey(key);
                } catch (IOException e) {
                    e.printStackTrace();
                    cancel(key);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cancel(SelectionKey key) {
        if (key == null) {
            return;
        }
        try {
            key.cancel();
            key.channel().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleKey(SelectionKey key) throws IOException {
        if (!key.isValid()) {
            return;
        }

        if (key.isAcceptable()) { //当新客户端连接到来时，进行注册read事件
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = channel.accept();
            socketChannel.configureBlocking(false);

            PushTask task = new PushTask(this, socketChannel);
            socketChannel.register(selector, SelectionKey.OP_READ, task);

        } else if (key.isReadable() || key.isWritable()) { //表示准备读取APPServer发来消息，或消息已经接收完毕准备回应APPServer
            PushTask pushTask = (PushTask) key.attachment();
            pushTask.setKey(key);

            // 向终端推送消息 当然这里面包含读取APPServer发来的消息、响应APPServer等流程
            pushPool.execute(pushTask);
        }
    }

    private void closeSelector() {
        if (selector != null) {
            try {
                selector.wakeup();
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stop() {
        stop = true;
        if (selector != null) {
            try {
                selector.wakeup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
