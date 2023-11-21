package db;

public class WormVo {
	private int wno;
	private String nickname;
	private int score;
	
	WormVo() {
		
	}
	
	public WormVo(String nickname, int score) {
		this.nickname = nickname;
		this.score = score;
	}
	
	public int getWno() {
		return wno;
	}
	public void setWno(int wno) {
		this.wno = wno;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	
}
