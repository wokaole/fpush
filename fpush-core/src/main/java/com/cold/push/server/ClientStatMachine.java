package com.cold.push.server;

/**
 * 客户端信息
 * @author liaowenhui
 * @date 2017/3/6 11:05.
 */
public class ClientStatMachine {

    private boolean hasMessage0x10; //是否有通用信息未接收
    private long last0x10Time;
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
}
