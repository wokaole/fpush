package com.cold.push.server;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 节点状态，用来存储所有的客户端信息
 * nodeStat
 * @author liaowenhui
 * @date 2017/3/6 10:52.
 */
public class NodeStatus {

    private ConcurrentHashMap<String, ClientStatMachine> nodeStat;

    private NodeStatus() {
        nodeStat = new ConcurrentHashMap<>(10000000);
    }

    public static NodeStatus getInstance() {
        return NodeStatusHolder.INSTANCE;
    }

    public ClientStatMachine getClientStat(String uuid) {
        return nodeStat.get(uuid);
    }

    private static class NodeStatusHolder{
        private static final NodeStatus INSTANCE = new NodeStatus();
    }
}
