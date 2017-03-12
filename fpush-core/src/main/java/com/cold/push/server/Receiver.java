package com.cold.push.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by faker on 2017/3/12.
 */
public class Receiver implements Runnable{

    private DatagramChannel channel;
    private ByteBuffer byteBuffer;
    /** 停止状态位 */
    protected volatile boolean stoped = false;
    private SocketAddress address;

    private AtomicLong queueIn = new AtomicLong(0);
    private AtomicLong queueOut = new AtomicLong(0);

    private LinkedBlockingQueue<ClientMessage> queue = new LinkedBlockingQueue();

    public Receiver(DatagramChannel channel) {
        this.channel = channel;
        this.byteBuffer = ByteBuffer.allocate(1024);
    }

    @Override
    public void run() {
        while (!stoped) {
            try {
                processMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processMessage() throws IOException {
        byteBuffer.clear();

        this.address = this.channel.receive(byteBuffer);

        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);

        ClientMessage clientMessage = new ClientMessage(this.address, bytes);
        addToQueue(clientMessage);
    }

    private void addToQueue(ClientMessage clientMessage) {
        if (this.queue.add(clientMessage)) {
            this.queueIn.incrementAndGet();
        }
    }

    private ClientMessage deQueue() {
        ClientMessage message = this.queue.poll();
        if (message != null){
            this.queueOut.incrementAndGet();
        }
        return message;
    }

    public void stop() {
        this.stoped = true;
    }
}
