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
	private String nickname = null; // 유저의 닉네임
	private Socket socket = null; // 유저의 소켓
	private PrintWriter player; // 유저
	List<PrintWriter> players = null; // 채팅 서버에 연결된 모든 클라이언트를 저장하는 list
	List<int[][]> worms = null; // 지렁이 위치값 모음
	int[][] field = new int[25][25]; // 반환해줄 필드
	int ex, ey; // 먹이 위치
	static boolean over = false;
	
	// 소켓 연결이 되면 필드를 그리고 해당 소켓의 bodyXY배열을 List<Worm>에 생성
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
				// 요청이 없다면 연결 종료
				if(request == null) {
					log("클라이언트로부터 연결 끊김");
					end();
					break;
				}
				
				log("님의 전송 데이터 : " + request);
				requestCheck(request);
				
			}
		} catch(IOException e) {
			log("게임 종료");
			if(isPlayer()) end(); // 유저가 리스트에 존재하는지 체크해주는 return boolean 메소드 필요 
		}
	}
	
	private void requestCheck(String request) {
		String[] token = request.split(":");
		String key = token[0];
		
		if(key.equals("alone")) {
			if (players.size() < 1) {
				addplayer(token[1]); // 유저 등록
			} else entrance();
		} else if(key.equals("together")) {
			if (players.size() < 3) {
				addplayer(token[1]); // 유저 등록
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
	// 입장인원 제한
	private void entrance() {
		String data = "entrance";
		over = true;
		sendData(data);
	}
	
	// 유저가 리스트에 있는지 체크
	private boolean isPlayer() {
		int index = getIndex(); // 리스트에서 인덱스 값 반환 해당 플레이어가 없다면 -1반환
		if(index >= 0) return true;
		return false;
	}
	
	//먹이 만들기
	private void makeEat() {
		Random rand = new Random();
		do {
			this.ex = rand.nextInt(23)+1; // rand.nextInt(23) 0 ~ 23-1 사이의 난수 생성
			this.ey = rand.nextInt(23)+1; 
		}
		while((field[ex][ey]!=0));
	}
	
	// 게임 종료시 호출
	private void end() {
		over = false;
		removeWorm();
		removeplayer();
		System.out.println("유저 수 : " + players.size());
		System.out.println(players.toString());
		System.out.println("지렁이 수 : " + worms.size());
		System.out.println(worms.toString());
		
	}
	
	// 게임 종료시 점수 저장
	private void regScore(String score) {
		log("최종 점수 : " + score);
		DB.getInstance().insert(this.nickname, Integer.parseInt(score));
	}
	
	// 유저 리스트에서 유저의 인덱스 값 찾기
	private int getIndex() {
		int index;
		synchronized(players) {
			index = players.indexOf(player);
		}
		
		return index;
	}
	
	// 게임 종료시 유저 위치 삭제
	private void removeWorm() {
		int index = getIndex();
		
		synchronized(worms) {
			worms.remove(index);
		}
	}
	
	// 새 유저 생성
	private void newWorm(String[] token) {
		int[][] newWorm = convertStrToWrom(token);
				
		log("유저 위치 추가");
		addWorm(newWorm);
		setField();
	}
	
	// 유저 위치값 저장
	private void addWorm(int[][] data) {
		synchronized(worms) {
			worms.add(data);
		}
		System.out.println("지렁이 수 : " + worms.size());
		System.out.println(worms.toString());
	}
	
	// 유저 이동
	private void updateWorm(String[] token) {
		int index = getIndex();
		int[][] newWorm = convertStrToWrom(token);
		 
		log("지렁이 업데이트");
		updateWorm(index, newWorm);
		setField();
	}
	
	// 유저 위치 업데이트
	private void updateWorm(int index, int[][] data) {
		synchronized(worms) {
			worms.set(index, data);
		}
		System.out.println(worms.toString());
		System.out.println("유저 수 : " + players.size());
		System.out.println("지렁이 수 : " + worms.size());
	}
	
	// 문자열 -> int[][] return
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
	
	// 필드에  그리기  (유저 : 2, 3, 4)
	private void setField() {
		for(int i = 0; i < worms.size(); i++) {
			int [][] tmp = worms.get(i);
			
				for(int j = 0; j < tmp[0].length; j++) {
					if(tmp[0][j] != 0 && tmp[1][j] != 0) {
						field[tmp[0][j]][tmp[1][j]] = (i+2);
					}
				}
		}
		log("필드 세팅");
		sendField();
	}
	
	// 필드 보내기
	private void sendField() {
		String data = FieldToStr();
		
		broadcast(data);
	}
	
	// 필드 -> 문자열 return
	private String FieldToStr() {
		String data = "";
		
		synchronized(field) {
			for(int i = 0; i < field.length; i++) {
				for(int j = 0; j < field[i].length; j++) {
					data += field[i][j] + ":";
				}
			}
		}
		log("필드 -> 문자열 변환완료");
		return data;
	}
	
	// 필드 초기화
	private void init() {
		for(int i=0;i<25;i++) {
			for(int j=0;j<25;j++) {
				if(i==0||i==24||j==0||j==24)field[i][j] = 1;
				else field[i][j] = 0;
			}
		}
		
		field[ey][ex] = 5; // 먹이생성
		
		System.out.println("필드 초기화");
	}

	// 유저 추가
	private void addplayer(String nickname) {
		this.nickname = nickname;
		
		synchronized(players) {
			players.add(player);
		}
		log("유저 추가완료");
		System.out.println(players.toString());
	}
		
	// 유저삭제
	private void removeplayer() {
		synchronized(players) {
			players.remove(player);
		}
		log("유저 삭제완료");
	}
	
	// 서버에서 단일 대상에게 전송
	private void sendData(String data) {
		player.println(data);
		player.flush();
	}
	
	// 서버에 연결된 모든 클라이언트에게 전송
	private void broadcast(String data) { 
		synchronized(players) {
			for(PrintWriter player : players) {
				System.out.println(player.toString() + "님에게 데이터 배포");
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
