package lys.sepr.ui;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lys.sepr.game.Game;

public class GameWindow extends JFrame {
	
	public Game game = null;
		
	private int trainPanelX = -230;
	
	JPanel mainMapPanel = new JPanel();
	JPanel mainInfoPanel = new JPanel();
	JPanel contractPanel = new JPanel();
	JPanel miniMapPanel = new JPanel();
	JPanel trainInfoPanel = new JPanel();
	
	JButton pauseButton = new JButton(new ImageIcon("files/pause.png"));
	JButton storeButton;
	JButton inventoryButton;
	
	public GameWindow(Game game) {
		this.game = game;
		Dialog.setParent(this);
		setTitle("World War Trains");
		setSize(1280,720);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		pauseButton.setFocusPainted(false);
		add(mainMapPanel);
		add(mainInfoPanel);
		add(contractPanel);
		add(miniMapPanel);
		add(trainInfoPanel);
		add(pauseButton);
		setVisible(true);
	}
	
	public void setLayouts() {
		int mapHeight = getHeight()-150;
		int width = getWidth();
		
		mainMapPanel.setBounds(0, 0, width, mapHeight);
		mainInfoPanel.setBounds(0, mapHeight, width/2, 150);
		contractPanel.setBounds(width/2, mapHeight, width/4, 150);
		miniMapPanel.setBounds(3*(width/4), mapHeight, width/4, 150);
		
		trainInfoPanel.setBounds(trainPanelX, 0, 230, mapHeight);
		
		pauseButton.setBounds(width-80, 0, 80, 60);
	}
	
	public void paint(Graphics g) {
		setLayouts();
		super.paint(g);
	}
	
	public static void main(String[] args) {
		new GameWindow(null);
	}
	
	public int getWidth() {
		return super.getWidth()-16;
	}

}
