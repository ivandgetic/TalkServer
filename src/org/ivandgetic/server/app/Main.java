package org.ivandgetic.server.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

public class Main {

    private static final int SOCKET_PORT = 50000;
    public static ArrayList<Socket> socketList = new ArrayList<Socket>();
    public static int id;

    public static void main(String[] args) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            stmt = c.createStatement();
            String sql = "CREATE TABLE MESSAGE " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " USERNAME           TEXT    NOT NULL, " +
                    " MESSAGE            TEXT     NOT NULL) ";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

