package com.cold.push.server;

import java.nio.ByteBuffer;

/**
 * server->client消息体
 * Created by faker on 2017/3/5.
 */
public class PushMessage {

    private  byte[] data;
    private int cmd;

    public PushMessage(byte[] data) {
        this.data = data;
    }


    public String getUuidHexString() {
        return "1111";
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public byte[] getData() {
        return data;
    }

    /**
     * 取得具体内容长度
     */
    public int getContentLength() {
        return (int) ByteBuffer.wrap(data, 19, 2).getChar();
    }
}
