package com.cold.push.server;

import com.cold.push.constant.PushConstants;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 客户端信息
 * @author liaowenhui
 * @date 2017/3/6 11:05.
 */
public class ClientStatMachine {

    private boolean hasMessage0x10; //是否有通用信息未接收
    private long last0x10Time;
    private long message0x11; //最新分类信息通知
    private long last0x11Time;
    private int message0x20Len;
    private long last0x20Time;
    private byte[] message0x20; //最新自定义内容数组
    private long lastTick = -1;// 最后心跳时间
    private SocketAddress socketAddress;// 最后心跳等的网络地址

    public boolean isHasMessage0x10() {
        return hasMessage0x10;
    }

    public void setHasMessage0x10(boolean hasMessage0x10) {
        this.hasMessage0x10 = hasMessage0x10;
    }

    public long getLast0x10Time() {
        return last0x10Time;
    }

    public void setLast0x10Time(long last0x10Time) {
        this.last0x10Time = last0x10Time;
    }

    public long getMessage0x11() {
        return message0x11;
    }

    public void setMessage0x11(long message0x11) {
        this.message0x11 = message0x11;
    }

    public long getLast0x11Time() {
        return last0x11Time;
    }

    public void setLast0x11Time(long last0x11Time) {
        this.last0x11Time = last0x11Time;
    }

    public int getMessage0x20Len() {
        return message0x20Len;
    }

    public void setMessage0x20Len(int message0x20Len) {
        this.message0x20Len = message0x20Len;
    }

    public long getLast0x20Time() {
        return last0x20Time;
    }

    public void setLast0x20Time(long last0x20Time) {
        this.last0x20Time = last0x20Time;
    }

    public byte[] getMessage0x20() {
        return message0x20;
    }

    public void setMessage0x20(byte[] message0x20) {
        this.message0x20 = message0x20;
    }

    public long getLastTick() {
        return lastTick;
    }

    public void setLastTick(long lastTick) {
        this.lastTick = lastTick;
    }

    public void push(PushMessage pushMessage) {
        int cmd = pushMessage.getCmd();
        if (cmd == PushConstants.CMD_0x10) {
            this.setHasMessage0x10(true);
            this.setLast0x10Time(System.currentTimeMillis());
            pushOx10();
        } else if (cmd == PushConstants.CMD_0x11) {
            this.setMessage0x11(this.getMessage0x11() | ByteBuffer.wrap(pushMessage.getData(), PushConstants.PUSH_MSG_HEADER_LEN, 8).getLong());
            this.setLast0x11Time(System.currentTimeMillis());
            push0x11();
        } else if (cmd == PushConstants.CMD_0x20) {
            this.setMessage0x20Len(pushMessage.getContentLength());
            this.setLast0x20Time(System.currentTimeMillis());
            this.setMessage0x20(new byte[message0x20Len]);
            byte[] data = Arrays.copyOfRange(pushMessage.getData(), PushConstants.PUSH_MSG_HEADER_LEN, pushMessage.getContentLength());
            this.setMessage0x20(data);
            push0x20();
        }
    }

    private void pushOx10() {
        byte[] data = new byte[PushConstants.SERVER_MESSAGE_MIN_LENGTH];// 5 bytes
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.put((byte) 1);// version
        bb.put((byte) 0);// app id,0 here
        bb.put((byte) PushConstants.CMD_0x10);// cmd
        bb.putShort((short) 0);// length 0
        bb.flip();

        //todo socketAddress 初始化
        ServerMessage serverMessage = new ServerMessage(this.socketAddress, data);
        IMServer.getInstance().pushInstanceMessage(serverMessage);
    }

    private void push0x11() {
        byte[] data = new byte[PushConstants.SERVER_MESSAGE_MIN_LENGTH + 8];// 13
        // bytes
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.put((byte) 1);// version
        bb.put((byte) 0);// app id, 0 here
        bb.put((byte) PushConstants.CMD_0x11);// cmd
        bb.putShort((short) 8);// length 8
        bb.putLong(message0x11);
        bb.flip();

        ServerMessage serverMessage = new ServerMessage(this.socketAddress, data);
        IMServer.getInstance().pushInstanceMessage(serverMessage);

    }

    private void push0x20() {

        byte[] data = new byte[PushConstants.SERVER_MESSAGE_MIN_LENGTH + message0x20Len];
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.put((byte) 1);// version
        bb.put((byte) 0);// app id, 0 here
        bb.put((byte) PushConstants.CMD_0x20);// cmd
        bb.putShort((short) message0x20Len);
        bb.put(this.message0x20);
        bb.flip();

        ServerMessage serverMessage = new ServerMessage(this.socketAddress, data);
        IMServer.getInstance().pushInstanceMessage(serverMessage);
    }
}
