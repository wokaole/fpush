package com.cold.push.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * Created by faker on 2017/3/4.
 */
public class PushListener implements Runnable{

    private static int PORT = 9999;
    private volatile boolean stop;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    @Override
    public void run() {
        init();
        System.out.println("push listener port:" + PORT);

        while (!stop && selector != null) {
            handleChannel();
        }

        closeSelector();
    }

    private void init() {
        initChannel();
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
        } else if (key.isReadable() || key.isWritable()) {

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
