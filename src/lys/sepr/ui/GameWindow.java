package lys.sepr.ui;

import java.awt.Graphics;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lys.sepr.game.Game;
import lys.sepr.game.GameEventListener;
import lys.sepr.game.Player;

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

	GameEventListener gameListener = new GameEventListener() {

		@Override
		public void gameEnd() {
			// TODO Auto-generated method stub
			Player[] players = game.getPlayers().toArray(new Player[0]);
			Player winner = null; // assume one winner, will deal with ties
									// later
			for (Player player : players) {
				if (winner == null) {
					winner = player;
				} else {
					if (player.getMoney() > winner.getMoney()) {
						winner = player;
					}
				}
			}
			String playerName = "player";
			Dialog.info("Congratulations " + playerName
					+ ", you are the winner!");
			dispose();
		}

		@Override
		public void contractCompleted() {
			int money = game.getActivePlayer().getCurrentContract()
					.getMoneyPayout();
			int reputation = game.getActivePlayer().getCurrentContract()
					.getReputationPayout();
			String moneyStr = (money == 0 ? "" : "\n\t- Money: " + money), repStr = (reputation == 0 ? ""
					: "\n\t- Reputation: " + reputation);
			Dialog.info("You completed your contract and have been rewarded the following:"
					+ moneyStr + repStr);
		}

		@Override
		public void contractFailed() {
			Dialog.info("You ran out of time and did not complete your contract!");

		}

		@Override
		public void contractChoose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void turnBegin() {
			String playerName = "player"; // TODO replace this with an actual
											// player name
			Dialog.info("Player " + playerName + ", it is your turn.");
		}

		@Override
		public void update() {
			repaint();

		}

	};

	public GameWindow(Game game) {
		this.game = game;
		if (game != null) {
			game.addGameEventListener(gameListener);
		}
		Dialog.setParent(this);
		setTitle("World War Trains");
		setSize(1280, 720);
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
		int mapHeight = getHeight() - 150;
		int width = getWidth();

		mainMapPanel.setBounds(0, 0, width, mapHeight);
		mainInfoPanel.setBounds(0, mapHeight, width / 2, 150);
		contractPanel.setBounds(width / 2, mapHeight, width / 4, 150);
		miniMapPanel.setBounds(3 * (width / 4), mapHeight, width / 4, 150);

		trainInfoPanel.setBounds(trainPanelX, 0, 230, mapHeight);

		pauseButton.setBounds(width - 80, 0, 80, 60);
	}

	public void paint(Graphics g) {
		setLayouts();
		super.paint(g);
	}

	public static void main(String[] args) {
		new GameWindow(null);
	}

	public int getWidth() {
		return super.getWidth() - 16;
	}

}
