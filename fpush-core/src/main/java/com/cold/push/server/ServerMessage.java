package com.cold.push.server;

import java.net.SocketAddress;

/**
 * 服务端消息体
 * @author liaowenhui
 * @date 2017/3/7 19:27.
 */
public class ServerMessage {

    private SocketAddress socketAddress; //套接字地址
    private byte[] data; //消息体数组

    public ServerMessage(SocketAddress socketAddress, byte[] data) {
        this.socketAddress = socketAddress;
        this.data = data;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
