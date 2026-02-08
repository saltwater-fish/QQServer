package com.lucas.qqserver.service;


import java.util.HashMap;
import java.util.Iterator;

public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    public static String getOnlineUser() {
        String onlineUser = "";
        Iterator<String> iterator = hm.keySet().iterator();
        while (iterator.hasNext()) {
            onlineUser += iterator.next().toString() + " ";
        }
        return onlineUser;
    }

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
