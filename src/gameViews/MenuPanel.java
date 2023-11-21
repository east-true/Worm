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
		
		text = new TextField("닉네임을 입력하세요", 15);
		
		JButton rankbtn = new JButton("랭킹보기");
		addAction(rankbtn);
		JButton alonebtn = new JButton("혼자하기");
		addAction(alonebtn);
		JButton Togetherbtn = new JButton("같이하기");
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
	
	// 버튼 클릭 이벤트 처리
	private void addAction(JButton target) {
		target.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String menu = target.getText();
				
				System.out.println(text.getText() + "님이 " + menu + "를 선택했습니다.");
				if(menu.equals("랭킹보기")) {
					new RankFrame();
				} else if(menu.equals("혼자하기")) {
					sendServer("alone");
				} else if(menu.equals("같이하기")) {
					sendServer("together");
				}
			}
		});
	}
	
	// 서버에 데이터 전송
	private void sendServer(String data) {
		try {
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			String nickname = text.getText();
			System.out.println("서버에 전달 : "+ data + ":" + nickname);
			pw.println(data + ":" + nickname);
			
			new GameFrame(socket);
		} catch (IOException err) {
			err.printStackTrace();
		}
	}
}
