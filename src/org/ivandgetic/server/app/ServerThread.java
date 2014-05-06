package org.ivandgetic.server.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ServerThread implements Runnable {
    private Socket socket = null;
    DataOutputStream out;
    DataInputStream in;

    public ServerThread(final Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while (true) try {
            String readline = null;
            readline = in.readUTF();
            System.out.println(readline);
            for (Socket socket : Main.socketList) {
                out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(readline);
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