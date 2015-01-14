package lys.sepr.ui;

import java.awt.*;
import java.util.List;
import java.util.*;

import javax.swing.*;

import lys.sepr.game.Game;
import lys.sepr.game.Player;
import lys.sepr.game.world.Map;
import lys.sepr.ui.State;

public class GameWindow extends JFrame {
	
	public Game game = null;
		
	private int trainPanelX = -230;
	private double lastZoom;
	
	MainMapPanel mainMapPanel = new MainMapPanel();
	JScrollPane mainMapScrollPane = new JScrollPane();
	JPanel mainInfoPanel = new JPanel();
	JPanel contractPanel = new JPanel();
	JPanel miniMapPanel = new JPanel();
	JPanel trainInfoPanel = new JPanel();
	State state = new lys.sepr.ui.State();
	
	JButton pauseButton = new JButton(new ImageIcon("files/pause.png"));
	JButton storeButton;
	JButton inventoryButton;
	
	public GameWindow(Game game) {
		this.game = game;
		Dialog.setParent(this);
		setTitle("World War Trains");
		setSize(1280, 720);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		pauseButton.setFocusPainted(false);

		mainMapPanel.setMap(game.getMap());
		mainMapPanel.setState(state);
		mainMapScrollPane.setViewportView(mainMapPanel);
		mainMapScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		add(mainMapScrollPane);

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
		
//		mainMapPanel.setBounds(0, 0, width, mapHeight);
		mainMapScrollPane.setBounds(0, 0, width, mapHeight);
		mainInfoPanel.setBounds(0, mapHeight, width/2, 150);
		contractPanel.setBounds(width/2, mapHeight, width/4, 150);
		miniMapPanel.setBounds(3*(width/4), mapHeight, width/4, 150);
		
		trainInfoPanel.setBounds(trainPanelX, 0, 230, mapHeight);
		
		pauseButton.setBounds(width-80, 0, 80, 60);

		double zoom = state.getZoom();
		if (lastZoom != zoom) {
			int mapBackgroundWidth = (int) (game.getMap().getBackground().getWidth() * zoom);
			int mapBackgroundHeight = (int) (game.getMap().getBackground().getHeight() * zoom);
			mainMapPanel.setPreferredSize(new Dimension(mapBackgroundWidth, mapBackgroundHeight));
			mainMapScrollPane.getViewport().revalidate();
			mainMapScrollPane.getViewport().repaint();
			lastZoom = zoom;
		}
	}
	
	public void paint(Graphics g) {
		setLayouts();
		super.paint(g);
	}
	
	public static void main(String[] args) {
//		new GameWindow(null);
		List<Player> players = new ArrayList<Player>(Arrays.asList(new Player(0), new Player(0)));
		try {
			new GameWindow(new Game(players, 1, Actions.loadMap()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getWidth() {
		return super.getWidth()-16;
	}

}
