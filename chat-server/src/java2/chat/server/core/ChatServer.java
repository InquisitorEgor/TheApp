package java2.chat.server.core;

//import java2.chat.common.Library;
import java2.chat.server.gui.ServerGUI;
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
        //java2.chat.server.core.SqlClient.connect();
    }

    @Override
    public void onServerSocketThreadStop(ServerSocketThread thread) {
        putLog("Server thread stopped");
        //SqlClient.disconnect();
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
        //ClientThread client = (ClientThread) thread;
//        if (client.isAuthorized()) {
//            handleAuthMessage(client, value);
//        } else {
//            handleNonAuthMessage(client, value);
//        }
        for (int i = 0; i < clients.size(); i++) {
            SocketThread client = clients.get(i);
            client.sendMessage(value);
        }
    }

//    private void handleNonAuthMessage(ClientThread clientThread, String msg) {
//        System.out.println(msg);
//        String[] arr = msg.split(Library.DELIMITER);
//        for (int i = 0; i <arr.length ; i++) {
//            System.out.println(arr[i]+" ");
//        }
//        if (arr.length != 3 || !arr[0].equals(Library.AUTH_REQUEST)) {
//
//            clientThread.msgFormatError(msg);
//            return;
//        }
//        String login = arr[1];
//        String password = arr[2];
//        String nickname = SqlClient.getNickname(login, password);
//        if (nickname == null) {
//            putLog(String.format("Invalid login attempt: l='%s', p='%s'", login, password));
//            clientThread.authFail();
//            return;
//        }
//        clientThread.authAccept(nickname);
//        sendToAuthorizedClients(Library.getTypeBroadcast("Server", nickname + " connected!"));
//    }

//    private void handleAuthMessage(ClientThread client, String msg) {
//        sendToAuthorizedClients(msg);
//    }
//
//    private void sendToAuthorizedClients(String value) {
//        for (int i = 0; i < clients.size(); i++) {
//            ClientThread client = (ClientThread) clients.get(i);
//            if (!client.isAuthorized()) continue;
//            client.sendMessage(value);
//        }
//    }

    @Override
    public synchronized void onSocketThreadException(SocketThread thread, Exception e) {
        putLog("socketthread exception");
        clients.remove(thread);
    }
}
