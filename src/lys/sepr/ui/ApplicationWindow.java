package lys.sepr.ui;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ApplicationWindow extends JFrame {
	
	JButton newGameButton = new JButton("New Game");
	JButton loadGameButton = new JButton("Load Game");
	JButton tutorialButton = new JButton("Tutorial");
	JButton exitButton = new JButton("Exit");
	
	JButton settingsButton = new JButton("set");

	JPanel menuPanel = new JPanel();
	
	public static final String TITLE = "Train Game";	
	
	public ApplicationWindow() {
		super(TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(660, 400);
		menuPanel.setLayout(null);
		menuPanel.add(newGameButton);
		menuPanel.add(loadGameButton);
		menuPanel.add(tutorialButton);
		menuPanel.add(exitButton);
		menuPanel.add(settingsButton);
		add(menuPanel);
	}
	
	private void setMenuLayout() {
		int vgap = (getHeight()-360)/5;
		int hinset = (getWidth()-400)/2;
		newGameButton.setBounds(hinset, vgap+60, 400, 60);
		loadGameButton.setBounds(hinset, 2*vgap+120, 400, 60);
		tutorialButton.setBounds(hinset, 3*vgap+180, 400, 60);
		exitButton.setBounds(hinset, 4*vgap+240, 400, 60);
		
		settingsButton.setBounds(getWidth()-96, 0, 80, 60);

	}
	
	public void paint(Graphics g) {
		setMenuLayout();
		super.paint(g);
	}
	
	public static void main(String[] args) {
		new ApplicationWindow().setVisible(true);
	}
}
