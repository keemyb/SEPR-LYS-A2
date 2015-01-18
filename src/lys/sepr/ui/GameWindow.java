package lys.sepr.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;

import lys.sepr.game.Contract;
import lys.sepr.game.Game;
import lys.sepr.game.GameEventListener;
import lys.sepr.game.Player;
import lys.sepr.game.resources.Train;

public class GameWindow extends JFrame {

	public Game game = null;

	private int trainPanelX = -230;
	private boolean trainPanelShow = false;
	private double lastZoom;

	Image coinstackImg = new ImageIcon("files/coinstack.png").getImage()
			.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
	Image clockImg = new ImageIcon("files/clock.png").getImage()
			.getScaledInstance(35, 35, Image.SCALE_SMOOTH);

	MainMapPanel mainMapPanel = new MainMapPanel();
	JScrollPane mainMapScrollPane = new JScrollPane();
	JPanel mainInfoPanel = new JPanel() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(Color.BLACK);
			g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

			// Money
			g2.drawImage(coinstackImg, 85, 15, this);
			g2.setFont(new Font("Courier New", Font.PLAIN, 24));
			g2.drawString("" + game.getActivePlayer().getMoney(), 160, 50);
			g2.drawImage(clockImg, getWidth() - 115, 5, this);
			g2.drawString("" + game.getTurnClock() + "s", getWidth() - 60, 30);

			g2.setFont(new Font("Courier New", Font.PLAIN, 14));
			g2.drawString("Train Speed:", 200, 25);

		}
	};
	JPanel contractPanel = new JPanel() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	};
	JPanel miniMapPanel = new JPanel() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	};
	State state = new lys.sepr.ui.State();

	JButton pauseButton = new JButton(new ImageIcon(new ImageIcon(
			"files/pause.png").getImage().getScaledInstance(40, 40,
			Image.SCALE_SMOOTH)));
	JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
	JButton reverseTrainButton = new JButton(new ImageIcon(new ImageIcon(
			"files/Reverse.png").getImage().getScaledInstance(40, 40,
			Image.SCALE_SMOOTH)));
	JButton zoomInButton = new JButton("Zoom In");
	JButton zoomOutButton = new JButton("Zoom Out");
	JButton zoomResetButton = new JButton("Zoom Reset");

	JButton storeButton = new JButton("S");
	JButton inventoryButton = new JButton("I");

	GameEventListener gameListener = new GameEventListener() {

		@Override
		public void gameEnd() {
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
			Contract[] contracts = game.getContracts().toArray(new Contract[3]);
			String start0 = game
					.getMap()
					.getLocationFromPoint(
							contracts[0].getInitialRoute().getFrom()).getName();
			String dest0 = game
					.getMap()
					.getLocationFromPoint(
							contracts[0].getInitialRoute().getTo()).getName();
			String requiredTrainType0 = contracts[0].getRequiredTrainType()
					.toString();
			String moneyPayout0 = "" + contracts[0].getMoneyPayout();
			String repPayout0 = "" + contracts[0].getReputationPayout();
			String timeLimit0 = "" + contracts[0].getTimeLimit() + "s";

			String start1 = game
					.getMap()
					.getLocationFromPoint(
							contracts[1].getInitialRoute().getFrom()).getName();
			String dest1 = game
					.getMap()
					.getLocationFromPoint(
							contracts[1].getInitialRoute().getTo()).getName();
			String requiredTrainType1 = contracts[1].getRequiredTrainType()
					.toString();
			String moneyPayout1 = "" + contracts[1].getMoneyPayout();
			String repPayout1 = "" + contracts[1].getReputationPayout();
			String timeLimit1 = "" + contracts[1].getTimeLimit() + "s";

			String start2 = game
					.getMap()
					.getLocationFromPoint(
							contracts[2].getInitialRoute().getFrom()).getName();
			String dest2 = game
					.getMap()
					.getLocationFromPoint(
							contracts[2].getInitialRoute().getTo()).getName();
			String requiredTrainType2 = contracts[2].getRequiredTrainType()
					.toString();
			String moneyPayout2 = "" + contracts[2].getMoneyPayout();
			String repPayout2 = "" + contracts[2].getReputationPayout();
			String timeLimit2 = "" + contracts[2].getTimeLimit() + "s";

			String message = "Choose your contract\n\n" + "Contract 1:\n"
					+ "\tStart: "
					+ start0
					+ "\n"
					+ "\tDestination: "
					+ dest0
					+ "\n"
					+ "\tTrain type: "
					+ requiredTrainType0
					+ "\n"
					+ "\tMoney reward: "
					+ moneyPayout0
					+ "\n"
					+ "\tReputation reward: "
					+ repPayout0
					+ "\n"
					+ "\tTime limit: "
					+ timeLimit0
					+ "\n"
					+ "\n"
					+ "Contract 2:\n"
					+ "\tStart: "
					+ start1
					+ "\n"
					+ "\tDestination: "
					+ dest1
					+ "\n"
					+ "\tTrain type: "
					+ requiredTrainType1
					+ "\n"
					+ "\tMoney reward: "
					+ moneyPayout1
					+ "\n"
					+ "\tReputation reward: "
					+ repPayout1
					+ "\n"
					+ "\tTime limit: "
					+ timeLimit1
					+ "\n"
					+ "\n"
					+ "Contract 3:\n"
					+ "\tStart: "
					+ start2
					+ "\n"
					+ "\tDestination: "
					+ dest2
					+ "\n"
					+ "\tTrain type: "
					+ requiredTrainType2
					+ "\n"
					+ "\tMoney reward: "
					+ moneyPayout2
					+ "\n"
					+ "\tReputation reward: "
					+ repPayout2 + "\n" + "\tTime limit: " + timeLimit2 + "\n";

			Object[] options = { "Contract 1", "Contract 2", "Contract 3" };

			int n = JOptionPane.showOptionDialog(Dialog.parent, message,
					"Choose a contract", JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, null);

			Contract chosenContract = contracts[n];
			Object[] trains = game.getTrains(chosenContract).toArray();
			Train chosenTrain = (Train) JOptionPane.showInputDialog(
					Dialog.parent, "Choose a train", "Choose a train",
					JOptionPane.PLAIN_MESSAGE, null, trains, trains[0]);
			game.assignContract(chosenTrain, chosenContract);
		}

		@Override
		public void turnBegin() {
			String playerName = "player"; // TODO replace this with an actual
											// player name
			Dialog.info("Player " + playerName + ", it is your turn.");
		}

		@Override
		public void update() {
			game.setTrainSpeed(((double) speedSlider.getValue() / 100) * 0.0000000005);
			repaint();
		}

	};

	public GameWindow(final Game game) {
		this.game = game;
		if (game != null) {
			game.addGameEventListener(gameListener);
		}
		Dialog.setParent(this);
		setTitle("World War Trains");
		setSize(1280, 720);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(null);
		pauseButton.setFocusPainted(false);

		mainMapPanel.setGame(game);
		mainMapPanel.setState(state);
		mainMapScrollPane.setViewportView(mainMapPanel);
		mainMapScrollPane.getViewport().setScrollMode(
				JViewport.SIMPLE_SCROLL_MODE);
		mainMapScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		mainMapScrollPane.getVerticalScrollBar().setUnitIncrement(16);

		zoomInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state.zoomIn();
				setZoom();
			}
		});

		zoomOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state.zoomOut();
				setZoom();
			}
		});

		zoomResetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state.resetZoom();
				setZoom();
			}
		});

		reverseTrainButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				game.reverseTrain();
			}
		});

		// Container contentPane = getContentPane();

		add(pauseButton);
		add(mainInfoPanel);
		add(contractPanel);
		add(miniMapPanel);

		mainInfoPanel.setLayout(null);
		mainInfoPanel.add(reverseTrainButton);
		mainInfoPanel.add(speedSlider);
		mainInfoPanel.add(zoomInButton);
		mainInfoPanel.add(zoomOutButton);
		mainInfoPanel.add(zoomResetButton);
		mainInfoPanel.add(storeButton);
		mainInfoPanel.add(inventoryButton);

		add(mainMapScrollPane);
	}

	public void setLayouts() {
		int mapHeight = getHeight() - 200;
		int width = getWidth() - 16;

		mainMapScrollPane.setBounds(0, 0, width, mapHeight);
		mainInfoPanel.setBounds(0, mapHeight, width / 2, 150);
		contractPanel.setBounds(width / 2, mapHeight, width / 4, 150);
		miniMapPanel.setBounds(3 * (width / 4), mapHeight, width / 4, 150);

		pauseButton.setBounds(width - 77, 0, 60, 50);
		storeButton.setBounds(15, 80, 60, 60);
		inventoryButton.setBounds(15, 15, 60, 60);

		speedSlider.setBounds(300, 15, mainInfoPanel.getWidth() - 415,
				speedSlider.getPreferredSize().height);
		
		int zoomButtonWidth = zoomResetButton.getPreferredSize().width;
		int zoomButtonHeight = zoomResetButton.getPreferredSize().height;
		int zoomX = mainInfoPanel.getWidth() - (zoomButtonWidth + 10);
		zoomInButton.setBounds(zoomX, 45, zoomButtonWidth, zoomButtonHeight);
		zoomOutButton.setBounds(zoomX, 55+zoomButtonHeight, zoomButtonWidth, zoomButtonHeight);
		zoomResetButton.setBounds(zoomX, 65+(2*zoomButtonHeight), zoomButtonWidth, zoomButtonHeight);

		setZoom();
	}

	private void setZoom() {
		double zoom = state.getZoom();
		if (lastZoom != zoom) {
			int mapBackgroundWidth = (int) (mainMapPanel.getMapBackground()
					.getWidth() * zoom);
			int mapBackgroundHeight = (int) (mainMapPanel.getMapBackground()
					.getHeight() * zoom);
			mainMapPanel.setPreferredSize(new Dimension(mapBackgroundWidth,
					mapBackgroundHeight));
			mainMapScrollPane.getViewport().revalidate();
			lastZoom = zoom;
		}
	}

	public void paint(Graphics g) {
		setLayouts();
		super.paint(g);
	}

	/*
	 * public static void main(String[] args) { // new GameWindow(null);
	 * List<Player> players = new ArrayList<Player>(Arrays.asList( new
	 * Player(0), new Player(0))); try { Game g = new Game(players, 1,
	 * Actions.loadMap()); new GameWindow(g); g.startGame(players.get(0)); }
	 * catch (Exception e) { e.printStackTrace(); } }
	 */
}
