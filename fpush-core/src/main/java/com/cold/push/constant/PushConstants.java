package com.cold.push.constant;

/**
 * Created by faker on 2017/3/5.
 */
public class PushConstants {

    public static final int PUSH_MSG_HEADER_LEN = 21;
    public static final int PUSH_MSG_CONTENT_LEN = 60;
    public static final int PUSH_LISTENER_MAX_THREAD = 10;

    public static final int CMD_0x10 = 16;// 通用信息
    public static final int CMD_0x11 = 17;// 分类信息
    public static final int CMD_0x20 = 32;// 自定义信息
    public static final int SERVER_MESSAGE_MIN_LENGTH = 5; //服务器消息最小长度，通用命令时5

    private PushConstants() {

    }

}
