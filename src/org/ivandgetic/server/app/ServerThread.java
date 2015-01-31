package org.ivandgetic.server.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ServerThread implements Runnable {
    DataOutputStream out;
    DataInputStream in;
    Connection c = null;
    Statement stmt = null;
    private Socket socket = null;

    public ServerThread(final Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while (true) try {
            String readline = null;
            readline = in.readUTF();
            String[] separate = readline.split(":", 3);
            if (separate[0].equals("Message")) {
                System.out.println(separate[1] + ":" + separate[2]);
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection("jdbc:sqlite:test.db");
                    c.setAutoCommit(false);
                    stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM MESSAGE;");
                    while (rs.next()) {
                        Main.id = rs.getInt("id");
                    }
                    rs.close();
                    stmt.close();
                    c.close();
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }
                Main.id++;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection("jdbc:sqlite:test.db");
                    c.setAutoCommit(false);
                    stmt = c.createStatement();
                    String sql = "INSERT INTO MESSAGE (ID,USERNAME,MESSAGE) " + "VALUES (" + Main.id + ", '" + separate[1] + "', '" + separate[2] + "' );";
                    stmt.executeUpdate(sql);
                    stmt.close();
                    c.commit();
                    c.close();
                    Main.id++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (Socket socket : Main.socketList) {
                    out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("Message:" + separate[1] + ":" + separate[2]);
                }
            } else if (separate[0].equals("Operate")) {
                if (separate[1].equals("GetAllMessage")) {
                    try {
                        Class.forName("org.sqlite.JDBC");
                        c = DriverManager.getConnection("jdbc:sqlite:test.db");
                        c.setAutoCommit(false);
                        stmt = c.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM MESSAGE;");
                        while (rs.next()) {
                            String userName = rs.getString("USERNAME");
                            String message = rs.getString("MESSAGE");
                            out = new DataOutputStream(socket.getOutputStream());
                            out.writeUTF("Operate:" + userName + ":" + message);
                        }
                        rs.close();
                        stmt.close();
                        c.close();
                    } catch (Exception e) {
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                        System.exit(0);
                    }
                }
            }
        } catch (SocketException e) {
            Main.socketList.remove(socket);
            System.out.println(socket.getRemoteSocketAddress() + " is Disconnected");
            break;
        } catch (EOFException e) {
            Main.socketList.remove(socket);
            System.out.println(socket.getRemoteSocketAddress() + " is Disconnected");
            break;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
