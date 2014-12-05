package lys.sepr.ui;

import javax.swing.JFrame;

public class ApplicationWindow extends JFrame {

	public static final String TITLE = "Train Game";	
	
	public ApplicationWindow() {
		super(TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 800);
		
	}
	
	public static void main(String[] args) {
		new ApplicationWindow().setVisible(true);
	}
}
