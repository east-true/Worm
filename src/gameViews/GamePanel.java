package gameViews;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import javax.swing.JPanel;

import client.Client;

public class GamePanel extends JPanel implements KeyListener{
	private Socket socket;
	private PrintWriter pw;
	private Client gameModel;
	private Thread thread;
	GameFrame gameFrame;
	
	private int location = 39; // 사용자 keycode
	private int[][] field; // 필드
	
	private boolean frameClose = false;
	
	public GamePanel(Socket socket, GameFrame gameFrame) {
		super();
		this.socket = socket;
		this.setBackground(Color.black); // 배경 검은색 지정
		this.gameFrame = gameFrame;
		gameModel = new Client(this); // 게임 모델 객체 생성
		field = new int[25][25]; // 생성된 필드를 얻어옴
		
		addKeyListener(this);
		this.requestFocus();
		setFocusable(true);
		
		gameModel.tern(location, field);
		sendServer("addWorm:" + convertStrToWrom(gameModel.getbodyXY()));
		//ReceiveData();
		
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(!gameModel.isGameOver()) {
						if(frameClose) break; // 프레임 x버튼 이벤트 발생시 게임오버
						Thread.sleep(gameModel.getSpeed()); // 게임 속도조절
						gameModel.tern(location, field); // 향하는 방향에 따른 로직 처리
						// 필드를 문자열로 만들어서 서버에 전송
						sendbody(gameModel.getbodyXY());
						System.out.println("서버전송완료");
						// 다른 사용자의 위치값을 합친 필드를 얻어옴
						ReceiveData(); // 키:데이터 받을 수 있게 receiveData메소드 호출해서 안에서 split를 통해 key값에 따라 실행하게 나눠야함
						System.out.println("서버응답완료");
						repaint();
						System.out.println("필드 업데이트");
					}
					if(frameClose) {
						sendServer("end"); // 창 종료시 db기록 안함		
					} else {
						System.out.println("Game Over");
						sendServer("end:" + gameModel.getScore());
						gameFrame.gameFrmaeClose(); // 벽이나 다른유저에 부딫혀 게임 종료시 프레임 닫음
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
					
			}
		});
		thread.start();
	}
	
	public void setFrameClose(boolean frameClose) {
		System.out.println("창 종료 이벤트 발생");
		this.frameClose = frameClose;
	}
	
	// 변환한 지렁이 위치를 서버에 전달
	private void sendbody(int[][] body) {
		String data = "update:" + convertStrToWrom(body);
		
		System.out.print("지렁이 ");
		sendServer(data);
		
	}
	
	public void sendServer(String data) {
		try {
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			pw.println(data);
			System.out.println("데이터 전송 : " + data);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 지렁이 배열을 문자열로 변환
	private String convertStrToWrom(int[][] target) {
		String data = "";
		
		for(int i = 0; i < target.length; i++) {
			for(int j = 0; j < target[i].length; j++) {
				data += target[i][j] + ":";
			}
		}
		
		return data;
	}
	
	// 서버에서 문자열 데이터 받아옴
	private void ReceiveData() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				String data = br.readLine();
				System.out.println("전달받은 데이터 : " + data);
				if(data.equals("entrance")) { 
					System.out.println("입장 인원 제한");
					gameFrame.gameFrmaeClose();
				} else StrToIntArray(data);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
	}

	// 받아온 문자열 필드를 int배열로 변환 후 필드에 저장
	private void StrToIntArray(String data) {
		System.out.println("서버 데이터 변환");
		String[] serverData = data.split(":");
		int cnt = 0;

		for(int i = 0; i < field.length; i++) {
			for(int j = 0; j < field[i].length; j++) {
				field[i][j] = Integer.parseInt(serverData[cnt++]);
			}
		}
	}
		
	// 호스트ip얻어오기
	private String getHostIP() {
		String hostAddress = null;
		
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostAddress;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		

		// 점수그리기
		g.setColor(Color.gray);
		g.drawString("SCORE: ", 0, 20);
		g.setColor(Color.gray);
		g.drawString(gameModel.getScore()+"", 50, 20);

		// 호스트 ip그리기
		g.setColor(Color.gray);
		g.drawString("HOST: ", 200, 20);
		g.setColor(Color.gray);
		g.drawString(getHostIP()+"", 240, 20);

		// 속도그리기
		g.setColor(Color.gray);
		g.drawString("SPEED: ", 420, 20);
		g.drawString(gameModel.getSpeed()+"", 470, 20);
		
		// 필드그리기
		for(int i =0;i<25;i++) { // 이차원 배열의 25 * 25 크기를 화면에 그리기
			for(int j=0;j<25;j++) {
				System.out.print(field[i][j]);
				if(field[i][j]==1) { //벽 그리기
					g.setColor(Color.gray);
					g.fillRect(j*20, i*20+25, 15, 15);
				}else if(field[i][j]==2) { // 유저 그리기
					g.setColor(Color.RED);
					g.fillRect(j*20, i*20+25, 15, 15);
				}else if(field[i][j]==3) { // 유저 그리기
					g.setColor(Color.CYAN);
					g.fillRect(j*20, i*20+25, 15, 15);
				}else if(field[i][j]==4) { // 유저 그리기
					g.setColor(Color.GREEN);
					g.fillRect(j*20, i*20+25, 15, 15);
				}else if(field[i][j]==5) { // 먹이 그리기, 게임오버 그리기
					g.setColor(Color.YELLOW);
					g.fillRect(j*20, i*20+25, 15, 15);
				}
			}
			System.out.println();
		}
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		location = e.getKeyCode();
		if(gameModel.isPermitRotation()) {
			switch(location) {
			case 37:
				System.out.println("Left Key : " + location);
				break;
				//Up Key
			case 38:
				System.out.println("Up Key : " + location);
				break;
				//Right Key
			case 39:
				System.out.println("Right Key : " + location);
				break;
				//Down Key
			case 40:
				System.out.println("Down Key : " + location);
				break;
			}
			gameModel.setPermitRotation(false);			
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}