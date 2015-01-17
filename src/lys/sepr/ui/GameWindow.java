package lys.sepr.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import lys.sepr.game.Contract;
import lys.sepr.game.Game;
import lys.sepr.game.GameEventListener;
import lys.sepr.game.Player;
import lys.sepr.game.resources.Train;

public class GameWindow extends JFrame {

	public Game game = null;

	private int trainPanelX = -230;
	private double lastZoom;

	Image coinstack = new ImageIcon(
			"files/coinstack.png").getImage().getScaledInstance(60, 60,
					Image.SCALE_SMOOTH);
	
	MainMapPanel mainMapPanel = new MainMapPanel();
	JScrollPane mainMapScrollPane = new JScrollPane();
	JPanel mainInfoPanel = new JPanel() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth()-1, getHeight()-1);
			
			//Money
			g.drawImage(coinstack, 90, 80, this);
			g.setFont(new Font("Courier New", Font.PLAIN, 30));
			g.drawString(""+game.getActivePlayer().getMoney(), 165, 115);
			
			g.drawString("" + game.getTurnClock() + "s", getWidth()-60, 30);
			
		}
	};
	JPanel contractPanel = new JPanel() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth()-1, getHeight()-1);	
		}
	};
	JPanel miniMapPanel = new JPanel() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth()-1, getHeight()-1);	
		}
	};
	JPanel trainInfoPanel = new JPanel();
	State state = new lys.sepr.ui.State();

	JButton pauseButton = new JButton(new ImageIcon("files/pause.png"));
	JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
	JButton storeButton = new JButton("Store");
	JButton inventoryButton = new JButton("Inventory");

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

		mainMapPanel.setGame(game);
		mainMapPanel.setState(state);
		mainMapScrollPane.setViewportView(mainMapPanel);
		mainMapScrollPane.getViewport().setScrollMode(
				JViewport.SIMPLE_SCROLL_MODE);
		mainMapScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		mainMapScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(mainMapScrollPane);

		add(mainInfoPanel);
		add(contractPanel);
		add(miniMapPanel);
		add(trainInfoPanel);
		mainInfoPanel.add(speedSlider);
		setVisible(true);
	}

	public void setLayouts() {
		int mapHeight = getHeight() - 200;
		int width = getWidth()-16;

		// mainMapPanel.setBounds(0, 0, width, mapHeight);
		mainMapScrollPane.setBounds(0, 0, width, mapHeight);
		mainInfoPanel.setBounds(0, mapHeight, width / 2, 150);
		contractPanel.setBounds(width / 2, mapHeight, width / 4, 150);
		miniMapPanel.setBounds(3 * (width / 4), mapHeight, width / 4, 150);

		trainInfoPanel.setBounds(trainPanelX, 0, 230, mapHeight);

		pauseButton.setBounds(width - 80, 0, 80, 60);

		double zoom = state.getZoom();
		if (lastZoom != zoom) {
			int mapBackgroundWidth = (int) (game.getMap().getBackground()
					.getWidth() * zoom);
			int mapBackgroundHeight = (int) (game.getMap().getBackground()
					.getHeight() * zoom);
			mainMapPanel.setPreferredSize(new Dimension(mapBackgroundWidth,
					mapBackgroundHeight));
			mainMapScrollPane.getViewport().revalidate();
			mainMapScrollPane.getViewport().repaint();
			lastZoom = zoom;
		}
	}

	public void paint(Graphics g) {
		setLayouts();
		super.paint(g);
	}

	/*public static void main(String[] args) {
		// new GameWindow(null);
		List<Player> players = new ArrayList<Player>(Arrays.asList(
				new Player(0), new Player(0)));
		try {
			Game g = new Game(players, 1, Actions.loadMap());
			new GameWindow(g);
			g.startGame(players.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
