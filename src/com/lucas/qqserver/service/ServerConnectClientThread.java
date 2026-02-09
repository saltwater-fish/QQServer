package com.lucas.qqserver.service;

import com.lucas.qqcommon.Message;
import com.lucas.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

public class ServerConnectClientThread extends Thread{

    private Socket socket;
    private String userId;

    public ServerConnectClientThread(Socket socket, String userId){
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket(){
        return socket;
    }

    @Override
    public void run() {
        while(true){
            try {
                System.out.println("服务端和客户端" + userId + "保持通信，读取数据...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    System.out.println(message.getSender() + "请求在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();

                    // 构建一个message对象，返回在线用户列表
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);

                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    // 接收到发送文件的请求, 将消息转发给对应的用户
                    ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getClientThread(message.getGetter()).getSocket().getOutputStream());
                    oos.writeObject(message);
                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    // 群发消息,首先获得管理线程的hashmap
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    // 遍历在线用户集合
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()) {
                        String onLineUserId = iterator.next();
                        if (!onLineUserId.equals(message.getSender())) {
                            // 转发message
                            ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getClientThread(onLineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }

                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    // 接收到客户端发来的私聊请求，将信息进行转发
                    ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getClientThread(message.getGetter()).getSocket().getOutputStream());
                    oos.writeObject(message);


                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    // 某个用户客户端退出
                    System.out.println(message.getSender() + "退出");
                    ManageClientThreads.removeClientThread(message.getSender());
                    socket.close();// 关闭与这个用户传输信息的连接
                    // 退出线程
                    break;
                } else {
                    System.out.println("其他请求暂时不做处理");
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }




    }
}
