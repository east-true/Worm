package gameViews;

import java.awt.Color;

import javax.swing.JFrame;

import layouts.FrmaeLayout;

public class RankFrame extends FrmaeLayout {
	RankFrame() {
		setBackground(Color.BLACK);
		
		RankPanel rankPanel = new RankPanel(this);
		add(rankPanel);
		
		setVisible(true);
	}
}
