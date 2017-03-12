package com.cold.push.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by faker on 2017/3/12.
 */
public class Sender implements Runnable{

    private DatagramChannel channel;
    private ByteBuffer byteBuffer;
    /** 停止状态位 */
    protected volatile boolean stoped = false;

    private AtomicLong queueIn = new AtomicLong(0);
    private AtomicLong queueOut = new AtomicLong(0);
    protected final Object enQueSignal = new Object();


    /** 客户端消息队列 */
    private ConcurrentLinkedQueue<ServerMessage> queue = new ConcurrentLinkedQueue<>();

    public Sender(DatagramChannel channel) {
        this.channel = channel;
        this.byteBuffer = ByteBuffer.allocate(1024);
    }

    @Override
    public void run() {
        while(!stoped) {
            synchronized (enQueSignal) {
                while (queue.isEmpty() || !stoped) {
                    try {
                        enQueSignal.wait(1);
                        processMessage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private void processMessage() throws IOException {
        byteBuffer.clear();
        ServerMessage serverMessage = dequeue();

        if (serverMessage != null) {
            byteBuffer.put(serverMessage.getData());
            byteBuffer.flip();
            channel.send(byteBuffer, serverMessage.getSocketAddress());
        }

    }

    private ServerMessage dequeue() {
        ServerMessage message = this.queue.poll();
        if (message != null) {
            this.queueOut.decrementAndGet();
        }
        return message;
    }

    private void enqueue(ServerMessage serverMessage) {
        if (this.queue.add(serverMessage)) {
            this.queueIn.incrementAndGet();
        }

    }

    public void stop() {
        this.stoped = true;
    }

    public void send(ServerMessage serverMessage) {
        enqueue(serverMessage);
    }

}
