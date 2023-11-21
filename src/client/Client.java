package client;

import gameViews.GamePanel;

public class Client{
	private final static int MAXSIZE = 30; // ������ �ִ� ũ��
	private int hx,hy,size,score,speed;
	private int[][] bodyXY = new int[2][MAXSIZE]; // ������
	private boolean gameOver,permitRotation;
	private GamePanel panel; // ���� ���ޱ���� ���� ���� �г��� ��Ƶ�

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
	
	// �̵��ϴ� ���⿡ ���� �̺�Ʈ ó��
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
	
	//���̸� �Ծ��� ��
	private void eat() {
		System.out.println("eatEvent");
		plusScore(100); // ��������
		if(size>=MAXSIZE)return; // ������ ����� �ִ��϶� �޼ҵ� ����
		if(size>=1){ // ������ ũ�Ⱑ 1�̻��϶�
			bodyXY[0][size] = bodyXY[0][size-1]; // ���ʹ迭�� ���� ���� �迭�� ���� �־���
			bodyXY[1][size] = bodyXY[1][size-1];
		}
		size++; // ������ ũ�� ����
		controlDifficulty(); // �ӵ�����
		move(); // ������
	}
	
	// ������
	private void move() {
		System.out.println("moveEvent");
		if(size>=2){ // ������ ũ�Ⱑ 2�̻��� �� �ݺ����� ���� �̵�
			for(int i=size-1;i>0;i--) {
				bodyXY[0][i] = bodyXY[0][i-1];
				bodyXY[1][i] = bodyXY[1][i-1];
			}
		}

		bodyXY[0][0] = hx; // �����϶� �ʵ� x��ġ
		bodyXY[1][0] = hy; // �����϶� �ʵ� y��ġ
	}
	
	// ������ ���� �ӵ� ����
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
