package com.cold.push.server;

import com.cold.push.constant.PushConstants;

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
        }
        return null;
    }
}
