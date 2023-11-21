package gameViews;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import db.DB;
import db.WormVo;

public class RankPanel extends JPanel {
	Font f;
	List<WormVo> ranks = null;
	
	RankPanel(RankFrame frame) {
		try {
			f = new Font("San Serif", Font.BOLD, 20);
			ranks = DB.getInstance().select();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g) {
		g.setFont(f);
		g.setColor(Color.WHITE);
		g.drawString("·©              Å·", 190, 100);
		
		for(int i = 0; i < ranks.size(); i++) {
			WormVo value = ranks.get(i);
			
			String nickname = value.getNickname();
			int score = value.getScore();
			
			g.drawString("" + (i+1) + ".", 100, 150+(35*i));
			g.drawString(nickname, 140, 150+(35*i));
			g.drawString("" + score, 300, 150+(35*i));
		}
	}
	
	
}