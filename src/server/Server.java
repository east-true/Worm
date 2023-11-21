package server;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
public static final int PORT = 5000;
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		List<PrintWriter> players = new ArrayList<PrintWriter>();
		List<int[][]> worms = new ArrayList<int[][]>();
		
		try {
			// 서버 소켓 생성
			serverSocket = new ServerSocket();
			
			// 바인딩
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			serverSocket.bind(new InetSocketAddress(hostAddress, PORT));
			consoleLog("연결 기다림 - " + hostAddress + " : " + PORT);
			
			// 요청 대기
			while(true) {
				Socket socket = serverSocket.accept();
				new ServerProcessThread(socket, players, worms).start();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
			 	if(serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void consoleLog(String log) {
		System.out.println("[server " + Thread.currentThread().getId() + "] " + log);
	}
}
