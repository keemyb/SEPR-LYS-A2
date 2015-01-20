package lys.sepr.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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

import lys.sepr.game.ActiveTrain;
import lys.sepr.game.Contract;
import lys.sepr.game.Game;
import lys.sepr.game.GameEventListener;
import lys.sepr.game.Player;
import lys.sepr.game.resources.Train;

public class GameWindow extends JFrame {

	public Game game = null;

	private double lastZoom;

	private boolean paused = false;

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
			int offset = (getWidth() - 200) / players.length;
			for (int i = 0; i < players.length; i++) {
				g2.setFont(new Font("Courier New", Font.PLAIN, 14));
				g2.drawString("" + game.getPlayerName(i), 105 + (offset * i),
						25);
				g2.setFont(new Font("Courier New", Font.PLAIN, 24));
				g2.drawImage(coinstackImg, 85 + (offset * i), 35, this);
				g2.drawString("" + players[i].getMoney(), 160 + (offset * i),
						55);
				g2.drawImage(repImg, 85 + (offset * i), 90, this);
				g2.drawString("" + players[i].getReputation(),
						160 + (offset * i), 125);
			}
			g2.drawImage(clockImg, getWidth() - 115, 5, this);
			g2.drawString(
					"" + (game.getTurnClock() < 0 ? 0 : game.getTurnClock())
							+ "s", getWidth() - 60, 30);

		}
	};
	JPanel contractPanel = new JPanel() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(Color.BLACK);
			g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			g2.setFont(new Font("Courier New", Font.PLAIN, 20));
			g2.drawString("Contract Information", 10, 25);
			g2.setFont(new Font("Courier New", Font.PLAIN, 14));
			if (game.hasAContract(game.getActivePlayer())) {
				String destination = game
						.getMap()
						.getLocationFromPoint(
								game.getActivePlayer().getCurrentContract()
										.getInitialRoute().getTo()).getName();

				int moneyPayout = game.getActivePlayer().getCurrentContract()
						.getMoneyPayout();
				int repPayout = game.getActivePlayer().getCurrentContract()
						.getReputationPayout();
				g2.drawString("Destination:" + destination, 10, 45);
				int timeleft = game.getContractClock();
				String mins = (timeleft / 60 == 0 ? "" : "" + timeleft / 60
						+ "m ");
				String secs = "" + timeleft % 60 + "s";
				g2.drawString("Time Left: " + mins + secs, 10, 65);
				g2.drawString("Money Reward: " + moneyPayout, 10, 85);
				g2.drawString("Reputation Reward: " + repPayout, 10, 105);
			} else {
				g2.drawString("No contract", 10, 45);
			}
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
			ActiveTrain train = game.getActivePlayer().getActiveTrain();
			if (train != null) {
				g2.drawString("Train:", 10, 46);
				g2.drawString(train.getTrain().getName(), 20, 67);
				int currentHealth = train.getTrain().getHealth();
				int maxHealth = train.getTrain().getMaxHealth();
				int currentFuel = (int) train.getTrain().getAmountOfFuel();
				int maxFuel = (int) train.getTrain().getMaxFuelCapacity();
				g2.drawString("Health: " + currentHealth + "/" + maxHealth, 10,
						88);
				int healthBarWidth = getWidth() - 90;
				int healthBarFill = (healthBarWidth * currentHealth)
						/ maxHealth;
				int fuelBarWidth = getWidth() - 90;
				int fuelBarFill = (fuelBarWidth * currentFuel) / maxFuel;
				if ((float) currentHealth / maxHealth > 0.5) {
					g2.setColor(Color.GREEN);
				} else {
					g2.setColor(Color.RED);
				}
				g2.fillRect(10, 97, healthBarFill, 15);
				g2.setColor(Color.BLACK);
				g2.drawRect(10, 97, healthBarWidth, 15);
				if ((float) currentFuel / maxFuel > 0.5) {
					g2.setColor(Color.GREEN);
				} else {
					g2.setColor(Color.RED);
				}
				g2.fillRect(10, 117, fuelBarFill, 15);
				g2.setColor(Color.BLACK);
				g2.drawRect(10, 117, fuelBarWidth, 15);
				FontMetrics fm = g2.getFontMetrics(new Font("Courier New",
						Font.PLAIN, 14));
				String fuel = ""
						+ Math.round(train.getTrain().getAmountOfFuel()) + "/"
						+ Math.round(train.getTrain().getMaxFuelCapacity());
				g2.drawString("Fuel: " + fuel, 10, 145);
			} else {
				g2.drawString("No train", 10, 46);
			}
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
			game.pause();
			paused = true;
			repaint();
			int money = game.getActivePlayer().getCurrentContract()
					.getMoneyPayout();
			int reputation = game.getActivePlayer().getCurrentContract()
					.getReputationPayout();
			String moneyStr = (money == 0 ? "" : "\n\t- Money: " + money), repStr = (reputation == 0 ? ""
					: "\n\t- Reputation: " + reputation);
			Dialog.info("You completed your contract and have been rewarded the following:"
					+ moneyStr + repStr);
			game.resume();
			paused = false;
		}

		@Override
		public void contractFailed() {
			game.pause();
			paused = true;
			repaint();
			Dialog.info("You ran out of time and did not complete your contract!");
			game.resume();
			paused = false;

		}

		@Override
		public void contractChoose() {
			speedSlider.setValue(0);
			repaint();
			game.pause();
			paused = true;
			repaint();
			Contract[] contracts = game.getContracts().toArray(new Contract[3]);

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
			game.resume();
			paused = false;
			repaint();
		}

		@Override
		public void turnBegin() {
			game.pause();
			paused = true;
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
			Dialog.info("" + playerName + ", it is your turn.");
			game.resume();
			paused = false;
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

		pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				game.pause();
				paused = true;
				repaint();
				Object[] options = { "Resume", "Exit" };
				int n = JOptionPane.showOptionDialog(Dialog.parent,
						"Game is paused.", "", JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, null);
				if (n == 1) {
					game.stopGame();
					dispose();
				}
				paused = false;
				game.resume();
				repaint();
			}

		});

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
		if (paused) {
			g.setColor(new Color(255, 255, 255, 200));
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	/*
	 * public static void main(String[] args) { // new GameWindow(null);
	 * List<Player> players = new ArrayList<Player>(Arrays.asList( new
	 * Player(0), new Player(0))); try { Game g = new Game(players, 1,
	 * Actions.loadMap()); new GameWindow(g); g.startGame(players.get(0)); }
	 * catch (Exception e) { e.printStackTrace(); } }
	 */
}
