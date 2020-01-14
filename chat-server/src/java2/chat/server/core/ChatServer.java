package java2.chat.server.core;


import java2.network.ServerSocketThread;
import java2.network.ServerSocketThreadListener;
import java2.network.SocketThread;
import java2.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {

    private ServerSocketThread server;
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: ");
    private final ChatServerListener listener;
    private Vector<SocketThread> clients = new Vector<>();

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
    }

    public void start(int port) {
        if (server != null && server.isAlive())
            System.out.println("Server is already running");
        else
            server = new ServerSocketThread(this, "Chat server", port, 1000);
    }

    public void stop() {
        if (server == null || !server.isAlive())
            System.out.println("Server not running");
        else
            server.interrupt();
    }

    private void putLog(String msg) {
        msg = dateFormat.format(System.currentTimeMillis()) + Thread.currentThread().getName() + ": " + msg;
        listener.onChatServerMessage(msg);
    }

    /**
     * ServerSocketThread methods
     * */

    @Override
    public void onServerSocketThreadStart(ServerSocketThread thread) {
        putLog("Server thread started");
    }

    @Override
    public void onServerSocketThreadStop(ServerSocketThread thread) {
        putLog("Server thread stopped");
    }

    @Override
    public void onServerSocketCreate(ServerSocketThread thread, ServerSocket server) {
        putLog("server created");
    }

    @Override
    public void onServerSocketAcceptTimeout(ServerSocketThread thread, ServerSocket server) {
        //putLog("socket timeout");
    }

    @Override
    public void onSocketAccepted(ServerSocketThread thread, Socket socket) {
        String name = "SocketThread" + socket.getInetAddress() + ":" + socket.getPort();
        new SocketThread(this, name, socket);

    }

    @Override
    public void onServerSocketThreadException(ServerSocketThread thread, Exception e) {
        putLog("server exception");
    }

    /**
     * SocketThread methods
     * */

    @Override
    public synchronized void onSocketThreadStart(SocketThread thread, Socket socket) {
        putLog("socketthread start");
    }

    @Override
    public synchronized void onSocketThreadStop(SocketThread thread) {
        clients.remove(thread);
    }


    @Override
    public synchronized void onSocketThreadReady(SocketThread thread, Socket socket) {
        clients.add(thread);

    }

    @Override
    public synchronized void onReceiveString(SocketThread thread, Socket socket, String value) {

        for (int i = 0; i < clients.size(); i++) {
            SocketThread client = clients.get(i);
            client.sendMessage(dateFormat.format(System.currentTimeMillis()) + " " + value);
        }
    }

    @Override
    public synchronized void onSocketThreadException(SocketThread thread, Exception e) {
        putLog("socketthread exception");
        clients.remove(thread);
    }
}
