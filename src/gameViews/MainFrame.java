package gameViews;

import java.net.Socket;

import javax.swing.JFrame;

import layouts.FrmaeLayout;

public class MainFrame extends FrmaeLayout {
	public MainFrame(Socket socket) {
		MenuPanel menu = new MenuPanel(socket);
		add(menu);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		setVisible(true);
	}
}