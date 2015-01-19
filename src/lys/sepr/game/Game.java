package lys.sepr.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lys.sepr.game.resources.Resource;
import lys.sepr.game.resources.Train;
import lys.sepr.game.resources.TrainStorage;
import lys.sepr.game.resources.TrainType;
import lys.sepr.game.world.Location;
import lys.sepr.game.world.Map;
import lys.sepr.game.world.Point;
import lys.sepr.game.world.Route;
import lys.sepr.game.world.Track;
import lys.sepr.game.world.Utilities;

public class Game implements Runnable {

	private static int timePerTurn = 30000; // ms
	private static int contractsToChooseFromEachTurn = 3;
	private List<Player> players;
	private int maxContracts;
	private Player activePlayer;
	private Map map;
	private boolean gameRunning = false;
	private List<Contract> possibleContracts = new ArrayList<Contract>();

	// thread variables
	private long turnStartTime = 0;
	private long loopTime = 0;

	// An empty listener so we can stop messages after game has closed without
	// NullPointerExceptions
	private GameEventListener dummyListener = new GameEventListener() {
		@Override
		public void gameEnd() {}
		@Override
		public void contractCompleted() {}
		@Override
		public void contractFailed() {}
		@Override
		public void contractChoose() {}
		@Override
		public void turnBegin() {}
		@Override
		public void update() {}

	};

	private GameEventListener gameListener = dummyListener;

	// TODO proper exceptions
	public Game(List<Player> players, int maxContracts, Map map)
			throws Exception {
		this.players = players;
		if (players.size() < 2)
			throw new Exception("Not Enough Players");
		for (Player player : players) {
			if (players.indexOf(player) != players.lastIndexOf(player))
				throw new Exception("Duplicate Player");
		}

		if (maxContracts < 0)
			maxContracts = 3;
		this.maxContracts = maxContracts;

		// if (map == null) throw new Exception("No Map Selected");
		// May want to disable this when testing the game on a small test map
		// if (map.numberOfPossibleRoutes() < players.size() * maxContracts)
		// throw new Exception("Not enough routes");

		this.map = map;

		generatePossibleContracts();
	}

	public List<Player> getPlayers() {
		return players;
	}

	public int getMaxContracts() {
		return maxContracts;
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public Map getMap() {
		return map;
	}

	public void startGame(Player playerToStart) {
		if (gameRunning)
			return;
		gameRunning = true;

		for (Player player : players) {
			giveStarterTrains(player);
		}

		if (players.contains(playerToStart)) {
			activePlayer = playerToStart;
		} else {
			activePlayer = players.get(0);
		}
		Thread runner = new Thread(this);
		runner.start();
	}

	// only to be used by the windowClosing call in ApplicationWindow
	public void stopGame() {
		gameListener = dummyListener;
		gameRunning = false;
	}

	// Gives a player a train from each type with the lowest stats
	private void giveStarterTrains(Player player) {
		for (TrainType trainType : TrainType.values()) {
			Train train = TrainStorage.getStarterTrain(trainType);
			if (train != null)
				player.getInventory().addNewResource(train);
		}
	}

	public void switchPlayer() {
		int nextPlayerIndex;
		int currentPlayerIndex = players.indexOf(activePlayer);

		// Going round in a cycle instead of a simple toggle
		// for 2+ player support.
		if (players.size() == currentPlayerIndex + 1) {
			nextPlayerIndex = 0;
		} else {
			nextPlayerIndex = currentPlayerIndex + 1;
		}
		activePlayer = players.get(nextPlayerIndex);
		turnStartTime = System.currentTimeMillis();
		gameListener.turnBegin();
	}

	// Starting the players turn by giving them the contract and
	// train they have chosen.
	// Note there is no choose method in here, should be handled in GUI after
	// presenting them contracts and then trains as shown below.
	public void assignContract(Train train, Contract contract) {
		activePlayer.acceptContract(train, contract);
		possibleContracts.remove(contract);
	}

	// Returns a list of contracts for player to choose from
	public List<Contract> getContracts() {
		List<Contract> contracts = new ArrayList<Contract>();
		Random r = new Random();
		while (contracts.size() < contractsToChooseFromEachTurn) {
			int nextContractIndex = r.nextInt(possibleContracts.size());
			Contract nextContract = possibleContracts.get(nextContractIndex);
			if (!contracts.contains(nextContract)) {
				contracts.add(nextContract);
			}
		}
		return contracts;
	}

	// Returns the trains that the activePlayer can choose subject to their
	// contract
	public List<Train> getTrains(Contract contract) {
		TrainType requiredTrainType = contract.getRequiredTrainType();
		List<Train> suitableTrains = new ArrayList<Train>();
		for (Resource resource : activePlayer.getInventory().getContents()) {
			if (resource.getClass() != Train.class)
				continue;

			Train train = (Train) resource;
			if (requiredTrainType == train.getType()) {
				suitableTrains.add((Train) resource);
			}
		}
		return suitableTrains;
	}

	public int getTurnClock() {
		return (int) (timePerTurn - (System.currentTimeMillis() - turnStartTime)) / 1000;
	}

	private void generatePossibleContracts() {
		for (List<Route> routeList : map.getPossibleRoutes().values()) {
			int numberOfRoutes = routeList.size();
			if (numberOfRoutes == 0)
				continue;
			int averageDistance = 0;
			for (Route route : routeList) {
				averageDistance += Utilities.routeLength(route.getTracks());
			}
			averageDistance = averageDistance / numberOfRoutes;

			// Doing a random route and not the fastest route as the initial
			// route because we want the user to have a bit of choice!
			Random r = new Random();
			int nextRouteIndex = r.nextInt(numberOfRoutes);
			Route nextRoute = routeList.get(nextRouteIndex);

			int nextTrainTypeIndex = r.nextInt(TrainType.values().length);
			TrainType trainType = TrainType.values()[nextTrainTypeIndex];

			Contract contract = new Contract(nextRoute,
					new ArrayList<Location>(), averageDistance, trainType,
					averageDistance, averageDistance, 0);

			possibleContracts.add(contract);
		}

		// TODO filter contracts based on most available routes and length
		// while there are atleast players.size() * maxContracts (+ 2 for a
		// choice of three),
		// drop short contracts and then contracts with fewer routes.
	}

	// Changing the intersection/junction, setting which track a train will move
	// to next
	// Note the distinction between this and the changeRoute method.
	// Returns true if the change was successful
	public boolean changeActiveConnection(Track track,
			Track prospectiveNextTrack) {
		Point commonPoint = track.getCommonPoint(prospectiveNextTrack);

		if (commonPoint != null) {
			track.setActiveConnection(commonPoint, prospectiveNextTrack);
		}

		return prospectiveNextTrack == track
				.getActiveConnectedTrackTowards(commonPoint);
	}

	// This method modifies where the active players train.
	// Note the distinction between this and changeActiveConnection
	// A train will wait at an intersection until there is an activeConnection
	// between the track it is on currently and the next track it wants to go to
	// (the next track in the route).
	public void changeRoute(Track trackInRoute, Track prospectiveNextTrack) {
		activePlayer.getActiveTrain().changeRoute(trackInRoute,
				prospectiveNextTrack);
	}

	public boolean hasCompletedContract(Player player) {
		ActiveTrain activeTrain = player.getActiveTrain();
		return activeTrain.hasReachedDestination();
	}

	public void fulfilledCurrentContract(Player player) {
		player.fulfilledCurrentContract();
	}

	public void failedCurrentContract(Player player) {
		player.failedCurrentContract();
	}

	public boolean hasAContract(Player player) {
		return player.getCurrentContract() != null;
	}

	public void setTrainSpeed(double percentage) {
		ActiveTrain activeTrain = activePlayer.getActiveTrain();
		if (activeTrain == null)
			return;
		double newSpeed = activeTrain.getTrain().getMaxSpeed() * percentage;
		activeTrain.setCurrentSpeed(newSpeed);
	}

	public void reverseTrain() {
		ActiveTrain activeTrain = activePlayer.getActiveTrain();
		if (activeTrain == null)
			return;
		activeTrain.reverse();
	}

	// timePassed = time since last frame update
	public void update(long timePassed) {
		for (Player player : players) {
			ActiveTrain activeTrain = player.getActiveTrain();
			if (activeTrain != null) {
				// Infinite fuel
				activeTrain.getTrain().refill(Integer.MAX_VALUE);
				activeTrain.move(timePassed);
			}
		}
	}

	@Override
	public void run() {
		turnStartTime = System.currentTimeMillis();
		loopTime = System.currentTimeMillis();
		while (gameRunning) {
			long nowTime = System.currentTimeMillis();
			update(nowTime - loopTime);
			gameListener.update();
			if (hasAContract(activePlayer)) {
				if (hasCompletedContract(activePlayer)) {
					gameListener.contractCompleted();
					fulfilledCurrentContract(activePlayer);
				}
				if (activePlayer.isContractOutOfTime()) {
					gameListener.contractFailed();
					failedCurrentContract(activePlayer);
				}
			}
			if (allPlayersAttemptedEnoughContracts()) {
				gameListener.gameEnd();
				gameRunning = false;
				break;
			}
			if (!hasAContract(activePlayer)) {
				gameListener.contractChoose(); // will be handled all in
												// GameWindow
			}
			if (nowTime - turnStartTime >= timePerTurn) {
				switchPlayer();
			}
		}
	}

	private boolean allPlayersAttemptedEnoughContracts() {
		for (Player player : players) {
            if (player.getNumberOfAttemptedContracts() < maxContracts) {
                return false;
            }
        }
		return true;
	}

	public void addGameEventListener(GameEventListener gameEventListener) {
		gameListener = gameEventListener;
	}
	
	
	public String getPlayerName(int playerNumber) {
		return "Player"; //TODO this properly
	}
}
