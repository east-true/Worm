package gameViews;

import java.net.Socket;

import javax.swing.JFrame;

import layouts.FrmaeLayout;

public class GameFrame extends FrmaeLayout {
	public GameFrame(Socket socket) {
		GamePanel gamePanel = new GamePanel(socket, this);
		add(gamePanel);
		
		// 프레임 창 x버튼 누를시 아무런 행동을 취하지 않음
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// x버튼 이벤트 발생시 setFrameClose메소드 호출
		addWindowListener(new java.awt.event.WindowAdapter() {
		       @Override
		       public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		           gamePanel.setFrameClose(true);
		           dispose();
		       }
		});	
		setVisible(true);
	}
	
	public void gameFrmaeClose() {
		dispose();
	}
}