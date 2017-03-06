package com.cold.push.server;

import org.apache.commons.lang3.StringUtils;

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
}
