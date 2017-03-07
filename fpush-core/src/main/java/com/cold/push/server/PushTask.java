package com.cold.push.server;

import com.cold.push.constant.PushConstants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by faker on 2017/3/5.
 */
public class PushTask implements Runnable{

    private PushListener pushListener;
    private SocketChannel channel;
    private SelectionKey key;
    private volatile boolean isCancel;
    private volatile boolean writePending;

    private byte[] bufferArray;
    private ByteBuffer buffer;

    public PushTask(PushListener pushListener, SocketChannel channel) {
        this.pushListener = pushListener;
        this.channel = channel;
        bufferArray = new byte[PushConstants.PUSH_MSG_HEADER_LEN + PushConstants.PUSH_MSG_CONTENT_LEN];
        buffer = ByteBuffer.wrap(bufferArray);
    }

    @Override
    public void run() {
        if(pushListener == null || channel == null || key == null || isCancel){
            return;
        }

        if (!writePending) {
            if (key.isReadable()) {
                readReq();
            }
        }

    }

    private void readReq() {
        if (writePending) {
            return;
        }

        try {
            if (channel.read(buffer) > 0) {
                if (isWritePending()) {
                    byte result = processReq();

                    buffer.clear();
                    buffer.limit(1);
                    buffer.put(result);
                    buffer.flip();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isWritePending() {
        return false;
    }

    private byte processReq() {
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        buffer.clear();

        PushMessage pushMessage = new PushMessage(data);
        String uuid = pushMessage.getUuidHexString();

        NodeStatus nodeStat = NodeStatus.getInstance();
        ClientStatMachine client = nodeStat.getClientStat(uuid);

        if (client == null) {
            client = ClientStatMachineFactory.create(pushMessage);
            nodeStat.putClientStat(uuid, client);
        }

        client.push(pushMessage);
        return 0;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }
}
