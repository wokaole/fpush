package com.cold.push.server;

import com.cold.push.constant.PushConstants;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author liaowenhui
 * @date 2017/3/6 20:10.
 */
public class ClientStatMachineFactory {

    public static ClientStatMachine create(PushMessage pushMessage) {
        ClientStatMachine client = new ClientStatMachine();
        int cmd = pushMessage.getCmd();

        if (cmd == PushConstants.CMD_0x10) {
            client.setHasMessage0x10(true);
            client.setLast0x10Time(System.currentTimeMillis());
        } else if (cmd == PushConstants.CMD_0x11) {
            byte[] data = pushMessage.getData();
            long messageType = ByteBuffer.wrap(data, data.length - 8, 8).getLong();
            client.setMessage0x11(messageType);
            client.setLast0x11Time(System.currentTimeMillis());
        } else if (cmd == PushConstants.CMD_0x20) {
            client.setMessage0x20Len(pushMessage.getContentLength());
            client.setLast0x20Time(System.currentTimeMillis());
            byte[] data = Arrays.copyOfRange(pushMessage.getData(), PushConstants.PUSH_MSG_HEADER_LEN, pushMessage.getContentLength());
            client.setMessage0x20(data);

        }
        client.setLastTick(System.currentTimeMillis());
        return client;
    }
}
