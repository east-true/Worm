package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import db.DB;

public class ServerProcessThread extends Thread {
	private String nickname = null; // ������ �г���
	private Socket socket = null; // ������ ����
	private PrintWriter player; // ����
	List<PrintWriter> players = null; // ä�� ������ ����� ��� Ŭ���̾�Ʈ�� �����ϴ� list
	List<int[][]> worms = null; // ������ ��ġ�� ����
	int[][] field = new int[25][25]; // ��ȯ���� �ʵ�
	int ex, ey; // ���� ��ġ
	static boolean over = false;
	
	// ���� ������ �Ǹ� �ʵ带 �׸��� �ش� ������ bodyXY�迭�� List<Worm>�� ����
	public ServerProcessThread(Socket socket, List<PrintWriter> players,  List<int[][]> worms) {
		this.socket = socket;
		this.players = players;
		this.worms = worms;
		init();
	}

	@Override
	public void run() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            player = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

			while(true) {
				String request = bufferedReader.readLine();
				// ��û�� ���ٸ� ���� ����
				if(request == null) {
					log("Ŭ���̾�Ʈ�κ��� ���� ����");
					end();
					break;
				}
				
				log("���� ���� ������ : " + request);
				requestCheck(request);
				
			}
		} catch(IOException e) {
			log("���� ����");
			if(isPlayer()) end(); // ������ ����Ʈ�� �����ϴ��� üũ���ִ� return boolean �޼ҵ� �ʿ� 
		}
	}
	
	private void requestCheck(String request) {
		String[] token = request.split(":");
		String key = token[0];
		
		if(key.equals("alone")) {
			if (players.size() < 1) {
				addplayer(token[1]); // ���� ���
			} else entrance();
		} else if(key.equals("together")) {
			if (players.size() < 3) {
				addplayer(token[1]); // ���� ���
			}  else entrance();
		} else if(key.equals("addWorm")) {
			if(!over)newWorm(token);
		} else if(key.equals("update")) {
			init();
			updateWorm(token);
		} else if(key.equals("makeEat")) {
			makeEat();
		} else if(key.equals("end")) {
			if(token.length >= 2 && !token[1].equals("0")) regScore(token[1]);
			end();
		}
	}
	// �����ο� ����
	private void entrance() {
		String data = "entrance";
		over = true;
		sendData(data);
	}
	
	// ������ ����Ʈ�� �ִ��� üũ
	private boolean isPlayer() {
		int index = getIndex(); // ����Ʈ���� �ε��� �� ��ȯ �ش� �÷��̾ ���ٸ� -1��ȯ
		if(index >= 0) return true;
		return false;
	}
	
	//���� �����
	private void makeEat() {
		Random rand = new Random();
		do {
			this.ex = rand.nextInt(23)+1; // rand.nextInt(23) 0 ~ 23-1 ������ ���� ����
			this.ey = rand.nextInt(23)+1; 
		}
		while((field[ex][ey]!=0));
	}
	
	// ���� ����� ȣ��
	private void end() {
		over = false;
		removeWorm();
		removeplayer();
		System.out.println("���� �� : " + players.size());
		System.out.println(players.toString());
		System.out.println("������ �� : " + worms.size());
		System.out.println(worms.toString());
		
	}
	
	// ���� ����� ���� ����
	private void regScore(String score) {
		log("���� ���� : " + score);
		DB.getInstance().insert(this.nickname, Integer.parseInt(score));
	}
	
	// ���� ����Ʈ���� ������ �ε��� �� ã��
	private int getIndex() {
		int index;
		synchronized(players) {
			index = players.indexOf(player);
		}
		
		return index;
	}
	
	// ���� ����� ���� ��ġ ����
	private void removeWorm() {
		int index = getIndex();
		
		synchronized(worms) {
			worms.remove(index);
		}
	}
	
	// �� ���� ����
	private void newWorm(String[] token) {
		int[][] newWorm = convertStrToWrom(token);
				
		log("���� ��ġ �߰�");
		addWorm(newWorm);
		setField();
	}
	
	// ���� ��ġ�� ����
	private void addWorm(int[][] data) {
		synchronized(worms) {
			worms.add(data);
		}
		System.out.println("������ �� : " + worms.size());
		System.out.println(worms.toString());
	}
	
	// ���� �̵�
	private void updateWorm(String[] token) {
		int index = getIndex();
		int[][] newWorm = convertStrToWrom(token);
		 
		log("������ ������Ʈ");
		updateWorm(index, newWorm);
		setField();
	}
	
	// ���� ��ġ ������Ʈ
	private void updateWorm(int index, int[][] data) {
		synchronized(worms) {
			worms.set(index, data);
		}
		System.out.println(worms.toString());
		System.out.println("���� �� : " + players.size());
		System.out.println("������ �� : " + worms.size());
	}
	
	// ���ڿ� -> int[][] return
	private int[][] convertStrToWrom (String[] target) {
		int[][] tmp = new int[2][30];
		int cnt = 1;
		
		for(int i = 0; i < tmp.length; i++) {
			for(int j = 0; j < tmp[i].length; j++) {
				tmp[i][j] = Integer.parseInt(target[cnt++]);
			}
		}
		
		return tmp;
	}
	
	// �ʵ忡  �׸���  (���� : 2, 3, 4)
	private void setField() {
		for(int i = 0; i < worms.size(); i++) {
			int [][] tmp = worms.get(i);
			
				for(int j = 0; j < tmp[0].length; j++) {
					if(tmp[0][j] != 0 && tmp[1][j] != 0) {
						field[tmp[0][j]][tmp[1][j]] = (i+2);
					}
				}
		}
		log("�ʵ� ����");
		sendField();
	}
	
	// �ʵ� ������
	private void sendField() {
		String data = FieldToStr();
		
		broadcast(data);
	}
	
	// �ʵ� -> ���ڿ� return
	private String FieldToStr() {
		String data = "";
		
		synchronized(field) {
			for(int i = 0; i < field.length; i++) {
				for(int j = 0; j < field[i].length; j++) {
					data += field[i][j] + ":";
				}
			}
		}
		log("�ʵ� -> ���ڿ� ��ȯ�Ϸ�");
		return data;
	}
	
	// �ʵ� �ʱ�ȭ
	private void init() {
		for(int i=0;i<25;i++) {
			for(int j=0;j<25;j++) {
				if(i==0||i==24||j==0||j==24)field[i][j] = 1;
				else field[i][j] = 0;
			}
		}
		
		field[ey][ex] = 5; // ���̻���
		
		System.out.println("�ʵ� �ʱ�ȭ");
	}

	// ���� �߰�
	private void addplayer(String nickname) {
		this.nickname = nickname;
		
		synchronized(players) {
			players.add(player);
		}
		log("���� �߰��Ϸ�");
		System.out.println(players.toString());
	}
		
	// ��������
	private void removeplayer() {
		synchronized(players) {
			players.remove(player);
		}
		log("���� �����Ϸ�");
	}
	
	// �������� ���� ��󿡰� ����
	private void sendData(String data) {
		player.println(data);
		player.flush();
	}
	
	// ������ ����� ��� Ŭ���̾�Ʈ���� ����
	private void broadcast(String data) { 
		synchronized(players) {
			for(PrintWriter player : players) {
				System.out.println(player.toString() + "�Կ��� ������ ����");
				player.println(data);
				player.flush();
			}
		}
	}

	// log
	private void log(String data) {
		System.out.println("[" + this.nickname +"(" + this.socket.getInetAddress() + ")" +  "]" + data);
	}
}
