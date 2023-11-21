package client;

import gameViews.GamePanel;

public class Client{
	private final static int MAXSIZE = 30; // 지렁이 최대 크기
	private int hx,hy,size,score,speed;
	private int[][] bodyXY = new int[2][MAXSIZE]; // 지렁이
	private boolean gameOver,permitRotation;
	private GamePanel panel; // 서버 전달기능을 위해 게임 패널을 담아둠

	public Client(GamePanel panel) {
		this.panel = panel;
		this.hx= (int)((Math.random()*22)+1);
		this.hy= (int)((Math.random()*22)+1);
		this.size=1;
		this.score = 0;
		this.gameOver = false;
		this.permitRotation = true;
		this.speed = 800;
		
		bodyXY[0][0] = hy;
		bodyXY[1][0] = hx;
		
		panel.sendServer("makeEat");
	}
	
	// 이동하는 방향에 따라 이벤트 처리
	public void tern(int keycode, int[][] field) {
		System.out.println(bodyXY[0][0] + ":" + bodyXY[1][0] + " => " + hx + ":" + hy);
		System.out.print("tern :");
		switch (keycode) {
		case 37:
			System.out.println("37");
			if(field[hx][hy]==1 || field[hx][hy]==2 || field[hx][hy]==3 || field[hx][hy]==4) {
				gameOver = true;
				break;
			}else if(field[hx][hy]==0) {
				move();
			}else if(field[hx][hy]==5) {
				eat();
				panel.sendServer("makeEat");
			}
			hy-=1;
			break;
		case 39:
			System.out.println("39");
			if(field[hx][hy]==1 || field[hx][hy]==2 || field[hx][hy]==3 || field[hx][hy]==4) {
				gameOver = true;
				break;
			}else if(field[hx][hy]==0) {
				move();
			}else if(field[hx][hy]==5) {
				eat();
				panel.sendServer("makeEat");
			}
			hy+=1;
			break;
		case 38:
			System.out.println("38");
			if(field[hx][hy]==1 || field[hx][hy]==2 || field[hx][hy]==3 || field[hx][hy]==4) {
				gameOver = true;
				break;
			}else if(field[hx][hy]==0) {
				move();
			}else if(field[hx][hy]==5) {
				eat();
				panel.sendServer("makeEat");
			}
			hx-=1;
			break;
		case 40:
			System.out.println("40");
			if(field[hx][hy]==1 || field[hx][hy]==2 || field[hx][hy]==3 || field[hx][hy]==4) {
				gameOver = true;
				break;
			}else if(field[hx][hy]==0) {
				move();
			}else if(field[hx][hy]==5) {
				eat();
				panel.sendServer("makeEat");
			}
			hx+=1;
			break;

		default:
			break;
		}
		permitRotation = true;
	}
	
	//먹이를 먹었을 때
	private void eat() {
		System.out.println("eatEvent");
		plusScore(100); // 점수증가
		if(size>=MAXSIZE)return; // 지렁이 사이즈가 최대일때 메소드 종료
		if(size>=1){ // 지렁이 크기가 1이상일때
			bodyXY[0][size] = bodyXY[0][size-1]; // 앞쪽배열의 값을 뒤쪽 배열의 값에 넣어줌
			bodyXY[1][size] = bodyXY[1][size-1];
		}
		size++; // 지렁이 크기 증가
		controlDifficulty(); // 속도조절
		move(); // 움직임
	}
	
	// 움직임
	private void move() {
		System.out.println("moveEvent");
		if(size>=2){ // 지렁이 크기가 2이상일 시 반복문을 통해 이동
			for(int i=size-1;i>0;i--) {
				bodyXY[0][i] = bodyXY[0][i-1];
				bodyXY[1][i] = bodyXY[1][i-1];
			}
		}

		bodyXY[0][0] = hx; // 움직일때 필드 x위치
		bodyXY[1][0] = hy; // 움직일때 필드 y위치
	}
	
	// 점수에 따라 속도 증가
	private void controlDifficulty() {
		if(score==200)setSpeed(650);
		if(score==400)setSpeed(550);
		if(score==600)setSpeed(450);
		if(score==1200)setSpeed(380);
		if(score==1600)setSpeed(340);
		if(score==2500)setSpeed(300);
		if(score==3000)setSpeed(270);
		if(score==4000)setSpeed(240);
		if(score==6000)setSpeed(220);
		if(score==7000)setSpeed(200);
		if(score==8000)setSpeed(190);
		if(score==9000)setSpeed(180);
	}
	
	private void plusScore(int point) {
		score += point;
	}

	public int[][] getbodyXY() {
		return bodyXY;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getScore() {
		return score;
	}

	public boolean isGameOver() {
		return gameOver;
	}
	
	public boolean isPermitRotation() {
		return permitRotation;
	}
	
	public void setPermitRotation(boolean permitRotation) {
		this.permitRotation = permitRotation;
	}
}
