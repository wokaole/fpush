package com.cold.push.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * UDP服务端
 * @author liaowenhui
 * @date 2017/3/7 19:34.
 */
public class UdpConnector {

    private DatagramChannel channel;
    private int port = 9996;
    /** 接收器 */
    protected Receiver receiver;
    /** 发送器 */
    protected Sender sender;
    /** 接收线程 */
    protected Thread receiverThread;
    /** 发送线程 */
    protected Thread senderThread;

    public void send(ServerMessage serverMessage) {
        this.sender.send(serverMessage);
    }

    public void start() {
        if (channel != null) {
            throw new RuntimeException("antenna is not null, may have run before");
        }

        try {
            channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(false);
            channel.socket().setReceiveBufferSize(100);
            channel.socket().setSendBufferSize(100);

            // 初始化接收和发送服务
            this.receiver = new Receiver(channel);
            this.sender = new Sender(channel);
            // 启动接收和发送线程
            this.senderThread = new Thread(sender, "AsynUdpConnector-sender");
            this.receiverThread = new Thread(receiver, "AsynUdpConnector-receiver");
            this.receiverThread.start();
            this.senderThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 停止服务
     *
     * @throws Exception
     */
    public void stop() throws Exception {
        receiver.stop();
        sender.stop();
        try {
            receiverThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            senderThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            channel.socket().close();
        } catch (Exception e) {
        }
        try {
            channel.close();
        } catch (Exception e) {
        }
    }
}
