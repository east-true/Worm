package layouts;

import javax.swing.JFrame;

public class FrmaeLayout extends JFrame {
	final static int Width = 510;
	final static int Height = 560;
	
	public FrmaeLayout() {
		setSize(Width, Height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);		
		setResizable(false);
	}
}
