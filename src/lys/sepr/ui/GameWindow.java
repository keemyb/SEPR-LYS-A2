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

	private static final double TRAIN_SPEED_CONST = 0.0000000005;

	Image coinstackImg = new ImageIcon("files/coinstack.png").getImage()
			.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
	Image clockImg = new ImageIcon("files/clock.png").getImage()
			.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
	Image repImg = new ImageIcon("files/Rep_badge.png").getImage()
			.getScaledInstance(50, 50, Image.SCALE_SMOOTH);

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
			
			Player[] players = game.getPlayers().toArray(new Player[0]);
			int offset = (getWidth()-200)/players.length;
			for(int i = 0; i < players.length; i++) {
				g2.setFont(new Font("Courier New", Font.PLAIN, 14));
				g2.drawString("" + game.getPlayerName(i), 105+(offset*i), 25);
				g2.setFont(new Font("Courier New", Font.PLAIN, 24));
				g2.drawImage(coinstackImg, 85+(offset*i), 35, this);
				g2.drawString("" + players[i].getMoney(), 160+(offset*i), 55);
				g2.drawImage(repImg, 85+(offset*i), 90, this);
				g2.drawString("" + players[i].getReputation(), 160+(offset*i), 125);
			}
			g2.drawImage(clockImg, getWidth() - 115, 5, this);
			g2.drawString("" + game.getTurnClock() + "s", getWidth() - 60, 30);

		}
	};
	JPanel contractPanel = new JPanel() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	};
	JPanel trainInfoPanel = new JPanel() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(Color.BLACK);
			g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

			g2.setFont(new Font("Courier New", Font.PLAIN, 14));
			g2.drawString("Train Speed:", 10, 25);
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

	JButton storeButton = new JButton(new ImageIcon(new ImageIcon(
			"files/Shop_icon.png").getImage().getScaledInstance(52, 52,
			Image.SCALE_SMOOTH)));

	JButton inventoryButton = new JButton(new ImageIcon(new ImageIcon(
			"files/Inventory_icon.png").getImage().getScaledInstance(52, 52,
			Image.SCALE_SMOOTH)));

	GameEventListener gameListener = new GameEventListener() {

		@Override
		public void gameEnd() {
			repaint();
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
			repaint();
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
			repaint();
			Dialog.info("You ran out of time and did not complete your contract!");

		}

		@Override
		public void contractChoose() {
			speedSlider.setValue(0);
			repaint();
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
					+ Actions.getContractSummary(contracts[0], game.getMap())
					+ "\n" + "Contract 2:\n"
					+ Actions.getContractSummary(contracts[1], game.getMap())
					+ "\n" + "Contract 3:\n"
					+ Actions.getContractSummary(contracts[2], game.getMap());

			Object[] options = { "Contract 1", "Contract 2", "Contract 3" };

			int n = -1;
			while (n == -1) {
				n = JOptionPane.showOptionDialog(Dialog.parent, message,
						"Choose a contract", JOptionPane.DEFAULT_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, null);
			}

			Contract chosenContract = contracts[n];
			Object[] trains = game.getTrains(chosenContract).toArray();
			Train chosenTrain = null;
			while (chosenTrain == null) {

				chosenTrain = (Train) JOptionPane.showInputDialog(
						Dialog.parent, "Choose a train", "Choose a train",
						JOptionPane.PLAIN_MESSAGE, null, trains, trains[0]);
			}

			game.assignContract(chosenTrain, chosenContract);
		}

		@Override
		public void turnBegin() {
			Player activePlayer = game.getActivePlayer();
			if (game.hasAContract(activePlayer)) {
				speedSlider.setValue((int) Math.round(game.getActivePlayer()
						.getActiveTrain().getCurrentSpeed()
						/ TRAIN_SPEED_CONST) * 100);
			} else {
				speedSlider.setValue(0);
			}
			String playerName = game.getPlayerName(game.getPlayers().indexOf(
					game.getActivePlayer())); // TODO replace this with an
												// actual player name
			Dialog.info("Player " + playerName + ", it is your turn.");
		}

		@Override
		public void update() {
			game.setTrainSpeed(((double) speedSlider.getValue() / 100)
					* TRAIN_SPEED_CONST);
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
		reverseTrainButton.setFocusPainted(false);

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
		add(trainInfoPanel);

		mainInfoPanel.setLayout(null);
		trainInfoPanel.add(reverseTrainButton);
		trainInfoPanel.add(speedSlider);
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
		trainInfoPanel.setBounds(3 * (width / 4), mapHeight, width / 4, 150);

		pauseButton.setBounds(width - 77, 0, 60, 50);

		storeButton.setBounds(15, 80, 60, 60);
		inventoryButton.setBounds(15, 15, 60, 60);

		speedSlider.setBounds(110, 15, trainInfoPanel.getWidth() - 120,
				speedSlider.getPreferredSize().height);

		reverseTrainButton.setBounds(trainInfoPanel.getWidth() - 70,
				speedSlider.getHeight() + 25, 60, 60);

		int zoomButtonWidth = zoomResetButton.getPreferredSize().width;
		int zoomButtonHeight = zoomResetButton.getPreferredSize().height;
		int zoomX = mainInfoPanel.getWidth() - (zoomButtonWidth + 10);
		zoomInButton.setBounds(zoomX, 45, zoomButtonWidth, zoomButtonHeight);
		zoomOutButton.setBounds(zoomX, 55 + zoomButtonHeight, zoomButtonWidth,
				zoomButtonHeight);
		zoomResetButton.setBounds(zoomX, 65 + (2 * zoomButtonHeight),
				zoomButtonWidth, zoomButtonHeight);

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
