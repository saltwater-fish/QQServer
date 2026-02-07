package com.lucas.qqserver.service;


import java.util.HashMap;

public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    public static ServerConnectClientThread getClientThread(String userId) {
        return hm.get(userId);
    }

    public static void removeClientThread(String userId) {
        hm.remove(userId);
    }

}
