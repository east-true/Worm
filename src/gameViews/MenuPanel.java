package gameViews;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MenuPanel extends JPanel {
	private TextField text;
	Socket socket;
	PrintWriter pw;
	
	MenuPanel(Socket socket) {
		this.socket = socket;
		
		setLayout(new GridLayout(3, 3));
		
		for(int i = 0; i < 4; i++) {
			add(new JPanel());			
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(7, 0));
		
		text = new TextField("�г����� �Է��ϼ���", 15);
		
		JButton rankbtn = new JButton("��ŷ����");
		addAction(rankbtn);
		JButton alonebtn = new JButton("ȥ���ϱ�");
		addAction(alonebtn);
		JButton Togetherbtn = new JButton("�����ϱ�");
		addAction(Togetherbtn);
		
		
		panel.add(text);
		panel.add(new JPanel());			
		panel.add(rankbtn);
		panel.add(new JPanel());			
		panel.add(alonebtn);
		panel.add(new JPanel());			
		panel.add(Togetherbtn);
		add(panel, BorderLayout.CENTER);
		
		for(int i = 0; i < 4; i++) {
			add(new JPanel());			
		}
	}
	
	// ��ư Ŭ�� �̺�Ʈ ó��
	private void addAction(JButton target) {
		target.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String menu = target.getText();
				
				System.out.println(text.getText() + "���� " + menu + "�� �����߽��ϴ�.");
				if(menu.equals("��ŷ����")) {
					new RankFrame();
				} else if(menu.equals("ȥ���ϱ�")) {
					sendServer("alone");
				} else if(menu.equals("�����ϱ�")) {
					sendServer("together");
				}
			}
		});
	}
	
	// ������ ������ ����
	private void sendServer(String data) {
		try {
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			String nickname = text.getText();
			System.out.println("������ ���� : "+ data + ":" + nickname);
			pw.println(data + ":" + nickname);
			
			new GameFrame(socket);
		} catch (IOException err) {
			err.printStackTrace();
		}
	}
}
