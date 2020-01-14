package java2.network.echo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8190);
             Socket socket = server.accept()) {
            server.setSoTimeout(100);
            System.out.println("We have a connection with a client");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            while (true) {
                String b = in.readUTF();
                out.writeUTF("echo: " + b);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
