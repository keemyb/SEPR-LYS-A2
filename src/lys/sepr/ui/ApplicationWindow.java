package lys.sepr.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thoughtworks.xstream.mapper.Mapper;
import lys.sepr.game.Game;
import lys.sepr.game.Player;

public class ApplicationWindow extends JFrame {

	JButton newGameButton = new JButton(new ImageIcon(new ImageIcon(
			"files/New_Game_button.png").getImage()));
	JButton loadGameButton = new JButton(new ImageIcon(new ImageIcon(
			"files/Load_Game_button.png").getImage()));
	JButton tutorialButton = new JButton(new ImageIcon(new ImageIcon(
			"files/Tutorial_button.png").getImage()));
	JButton exitButton = new JButton(new ImageIcon(new ImageIcon(
			"files/Exit_button.png").getImage()));
	
	JLabel backgroundLabel = new JLabel(new ImageIcon("files/Wallpaper.png"));




	JButton settingsButton = new JButton(new ImageIcon(new ImageIcon(
			"files/gearicon.png").getImage().getScaledInstance(52, 52,
			Image.SCALE_SMOOTH)));

	JLabel titleLabel = new JLabel("World War Trains");

	JPanel menuPanel = new JPanel();

	//reference for the windowListener only
	private Game g = null;


	public static final String TITLE = "World War Trains";

	public ApplicationWindow() {
		super(TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(660, 400);
		addActionListeners();

		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		Font f = titleLabel.getFont();
		Font bigFont = new Font("Courier New", Font.PLAIN, f.getSize() + 10);
		titleLabel.setFont(bigFont);

		
		settingsButton.setFocusPainted(false);
		
		menuPanel.setLayout(null);
		menuPanel.add(titleLabel);
		menuPanel.add(newGameButton);
		menuPanel.add(loadGameButton);
		menuPanel.add(tutorialButton);
		menuPanel.add(exitButton);
		menuPanel.add(settingsButton);
		menuPanel.add(backgroundLabel);
		add(menuPanel);
	}

	private void addActionListeners() {
		newGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
	}

	private void newGame() {
		// TODO ask for player names and number of contracts
		// Don't call a main method
		String player1name = "";
		String player2name = "";
		int numberOfContracts = -1;
		while(player1name.equals("")) {
			player1name = Dialog.stringInput("Player 1, enter your name.");
		}
		while(player2name.equals("")) {
			player2name = Dialog.stringInput("Player 2, enter your name.");
		}
		while(numberOfContracts == -1) {
			try {
				numberOfContracts = Dialog.intInput("Enter the number of contracts to complete.");
				if(numberOfContracts < 1) {
					Dialog.info("Please enter a non zero positive integer.");
				}
			} catch (NumberFormatException e) {
				Dialog.error("You did not enter an integer, try again.");
			}
		}
		List<Player> players = new ArrayList<Player>(Arrays.asList(
				new Player(0, Player.PlayerColor.BLUE, player1name), new Player(0, Player.PlayerColor.GREEN, player2name)));
		try {
			g = new Game(players, numberOfContracts, Actions.loadMap());
			GameWindow gw = new GameWindow(g);
			gw.addWindowListener(new WindowListener() {

				@Override
				public void windowActivated(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowClosed(WindowEvent arg0) {
					setVisible(true);

				}

				@Override
				public void windowClosing(WindowEvent arg0) {
					g.stopGame();
				}

				@Override
				public void windowDeactivated(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowDeiconified(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowIconified(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowOpened(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

			});
			setVisible(false);
			gw.setVisible(true);
			g.startGame(players.get(0));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setMenuLayout() {
		int vgap = (getHeight() - 360) / 5;
		int hinset = (getWidth() - 260) / 2;
		titleLabel.setBounds(hinset, 0, 260, 60);
		newGameButton.setBounds(hinset, vgap + 60, 260, 55);
		loadGameButton.setBounds(hinset, 2 * (vgap + 60), 260, 55);
		tutorialButton.setBounds(hinset,3 * (vgap + 60), 260, 55);
		exitButton.setBounds(hinset,4 * (vgap + 60), 260, 55);
		settingsButton.setBounds(getWidth() - 96, 0, 80, 60);
		backgroundLabel.setBounds(0, 0, getWidth(), getHeight());

	}

	public void paint(Graphics g) {
		setMenuLayout();
		super.paint(g);
	}

	/*public static void main(String[] args) {
		new ApplicationWindow().setVisible(true);
	}*/
}
