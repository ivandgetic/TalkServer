package org.ivandgetic.server.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
    private static final int SOCKET_PORT = 50000;
    public static ArrayList<Socket> socketList = new ArrayList<Socket>();

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(SOCKET_PORT);
                    System.out.println("服务器启动！");
                    while (true) {
                        Socket socket = serverSocket.accept();
                        System.out.println(socket.getRemoteSocketAddress() + " is Connected");//在控制台显示
                        socketList.add(socket);//把socket添加到list
                        new Thread(new ServerThread(socket)).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
