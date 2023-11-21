package gameViews;

import java.net.Socket;

import javax.swing.JFrame;

import layouts.FrmaeLayout;

public class GameFrame extends FrmaeLayout {
	public GameFrame(Socket socket) {
		GamePanel gamePanel = new GamePanel(socket, this);
		add(gamePanel);
		
		// ������ â x��ư ������ �ƹ��� �ൿ�� ������ ����
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// x��ư �̺�Ʈ �߻��� setFrameClose�޼ҵ� ȣ��
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