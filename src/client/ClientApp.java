package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import gameViews.MainFrame;

public class ClientApp {
    private static final String SERVER_IP = "172.30.1.17";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        Socket socket = new Socket();
        try {
            socket.connect( new InetSocketAddress(SERVER_IP, SERVER_PORT) ); // 소켓연결
            new MainFrame(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}