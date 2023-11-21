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
	
	private int location = 39; // ����� keycode
	private int[][] field; // �ʵ�
	
	private boolean frameClose = false;
	
	public GamePanel(Socket socket, GameFrame gameFrame) {
		super();
		this.socket = socket;
		this.setBackground(Color.black); // ��� ������ ����
		this.gameFrame = gameFrame;
		gameModel = new Client(this); // ���� �� ��ü ����
		field = new int[25][25]; // ������ �ʵ带 ����
		
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
						if(frameClose) break; // ������ x��ư �̺�Ʈ �߻��� ���ӿ���
						Thread.sleep(gameModel.getSpeed()); // ���� �ӵ�����
						gameModel.tern(location, field); // ���ϴ� ���⿡ ���� ���� ó��
						// �ʵ带 ���ڿ��� ���� ������ ����
						sendbody(gameModel.getbodyXY());
						System.out.println("�������ۿϷ�");
						// �ٸ� ������� ��ġ���� ��ģ �ʵ带 ����
						ReceiveData(); // Ű:������ ���� �� �ְ� receiveData�޼ҵ� ȣ���ؼ� �ȿ��� split�� ���� key���� ���� �����ϰ� ��������
						System.out.println("��������Ϸ�");
						repaint();
						System.out.println("�ʵ� ������Ʈ");
					}
					if(frameClose) {
						sendServer("end"); // â ����� db��� ����		
					} else {
						System.out.println("Game Over");
						sendServer("end:" + gameModel.getScore());
						gameFrame.gameFrmaeClose(); // ���̳� �ٸ������� �΋H�� ���� ����� ������ ����
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
					
			}
		});
		thread.start();
	}
	
	public void setFrameClose(boolean frameClose) {
		System.out.println("â ���� �̺�Ʈ �߻�");
		this.frameClose = frameClose;
	}
	
	// ��ȯ�� ������ ��ġ�� ������ ����
	private void sendbody(int[][] body) {
		String data = "update:" + convertStrToWrom(body);
		
		System.out.print("������ ");
		sendServer(data);
		
	}
	
	public void sendServer(String data) {
		try {
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			pw.println(data);
			System.out.println("������ ���� : " + data);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ������ �迭�� ���ڿ��� ��ȯ
	private String convertStrToWrom(int[][] target) {
		String data = "";
		
		for(int i = 0; i < target.length; i++) {
			for(int j = 0; j < target[i].length; j++) {
				data += target[i][j] + ":";
			}
		}
		
		return data;
	}
	
	// �������� ���ڿ� ������ �޾ƿ�
	private void ReceiveData() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				String data = br.readLine();
				System.out.println("���޹��� ������ : " + data);
				if(data.equals("entrance")) { 
					System.out.println("���� �ο� ����");
					gameFrame.gameFrmaeClose();
				} else StrToIntArray(data);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
	}

	// �޾ƿ� ���ڿ� �ʵ带 int�迭�� ��ȯ �� �ʵ忡 ����
	private void StrToIntArray(String data) {
		System.out.println("���� ������ ��ȯ");
		String[] serverData = data.split(":");
		int cnt = 0;

		for(int i = 0; i < field.length; i++) {
			for(int j = 0; j < field[i].length; j++) {
				field[i][j] = Integer.parseInt(serverData[cnt++]);
			}
		}
	}
		
	// ȣ��Ʈip������
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
		

		// �����׸���
		g.setColor(Color.gray);
		g.drawString("SCORE: ", 0, 20);
		g.setColor(Color.gray);
		g.drawString(gameModel.getScore()+"", 50, 20);

		// ȣ��Ʈ ip�׸���
		g.setColor(Color.gray);
		g.drawString("HOST: ", 200, 20);
		g.setColor(Color.gray);
		g.drawString(getHostIP()+"", 240, 20);

		// �ӵ��׸���
		g.setColor(Color.gray);
		g.drawString("SPEED: ", 420, 20);
		g.drawString(gameModel.getSpeed()+"", 470, 20);
		
		// �ʵ�׸���
		for(int i =0;i<25;i++) { // ������ �迭�� 25 * 25 ũ�⸦ ȭ�鿡 �׸���
			for(int j=0;j<25;j++) {
				System.out.print(field[i][j]);
				if(field[i][j]==1) { //�� �׸���
					g.setColor(Color.gray);
					g.fillRect(j*20, i*20+25, 15, 15);
				}else if(field[i][j]==2) { // ���� �׸���
					g.setColor(Color.RED);
					g.fillRect(j*20, i*20+25, 15, 15);
				}else if(field[i][j]==3) { // ���� �׸���
					g.setColor(Color.CYAN);
					g.fillRect(j*20, i*20+25, 15, 15);
				}else if(field[i][j]==4) { // ���� �׸���
					g.setColor(Color.GREEN);
					g.fillRect(j*20, i*20+25, 15, 15);
				}else if(field[i][j]==5) { // ���� �׸���, ���ӿ��� �׸���
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