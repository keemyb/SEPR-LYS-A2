package lys.sepr.game;

import lys.sepr.game.resources.Resource;
import lys.sepr.game.resources.Train;
import lys.sepr.game.resources.TrainStore;
import lys.sepr.game.resources.TrainType;
import lys.sepr.game.world.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    private static int timePerTurn = 30;
    private static int contractsToChooseFromEachTurn = 3;
    private List<Player> players;
    private int maxContracts;
    private Player activePlayer;
    private Map map;
    private boolean gameStarted = false;
    private List<Contract> possibleContracts = new ArrayList<Contract>();

    //TODO proper exceptions
    Game(List<Player> players, int maxContracts, Map map) throws Exception {
        this.players = players;
        if (players.size() < 2) throw new Exception("Not Enough Players");
        for (Player player : players) {
            if (players.indexOf(player) != players.lastIndexOf(player))
                throw new Exception("Duplicate Player");
        }

        if (maxContracts < 0) maxContracts = 3;
        this.maxContracts = maxContracts;

        if (map == null) throw new Exception("No Map Selected");
        if (map.numberOfPossibleRoutes() < players.size() * maxContracts)
            throw new Exception("Not enough routes");

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
        if (gameStarted) return;
        gameStarted = true;

        for (Player player : players) {
            giveStarterTrains(player);
        }

        if (players.contains(playerToStart)) {
            activePlayer = playerToStart;
        } else {
            activePlayer = players.get(0);
        }
    }

    private void giveStarterTrains(Player player) {
        for (TrainType trainType : TrainType.values()) {
            Train train = TrainStore.getStarterTrain(trainType);
            if (train != null) player.getInventory().addNewResource(train);
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
    }

    public void assignContract(Train train, Contract contract) {
        activePlayer.acceptContract(train, contract);
        possibleContracts.remove(contract);
    }

    public List<Contract> getContracts() {
        List<Contract> contracts = new ArrayList<Contract>();
        Random r = new Random();
        int nextContractIndex = r.nextInt(possibleContracts.size());
        while (contracts.size() < contractsToChooseFromEachTurn) {
            contracts.add(possibleContracts.get(nextContractIndex));
        }
        return contracts;
    }

    public List<Train> getTrains(Contract contract) {
        TrainType requiredTrainType = contract.getRequiredTrainType();
        List<Train> suitableTrains = new ArrayList<Train>();
        for (Resource resource : activePlayer.getInventory().getContents()) {
            if (resource.getClass() != Train.class) continue;

            Train train = (Train) resource;
            if (requiredTrainType == train.getType()) {
                suitableTrains.add((Train) resource);
            }
        }
        return suitableTrains;
    }

    private void generatePossibleContracts() {
        for (java.util.Map.Entry<Map.RouteKey, List<Route>> entry : map.getPossibleRoutes().entrySet()) {
            Map.RouteKey routeKey = entry.getKey();
            List<Route> routeList = entry.getValue();

            int averageDistance = 0;
            for (Route route : routeList) {
                averageDistance += Utilities.routeLength(route.getTracks());
            }
            averageDistance = averageDistance / routeList.size();

            // Doing a random route and not the fastest route as the initial
            // route because we want the user to have a bit of choice!
            Random r = new Random();
            int nextRouteIndex = r.nextInt(routeList.size());
            Route nextRoute = routeList.get(nextRouteIndex);

            int nextTrainTypeIndex = r.nextInt(TrainType.values().length);
            TrainType trainType = TrainType.values()[nextTrainTypeIndex];

            Contract contract = new Contract(nextRoute, new ArrayList<Location>(),
                    averageDistance, trainType, averageDistance, averageDistance, 0);

            possibleContracts.add(contract);
        }

        // TODO filter contracts based on most available routes and length
        // while there are atleast players.size() * maxContracts (+ 2 for a choice of three),
        // drop short contracts and then contracts with fewer routes.
    }

    public boolean changeConnectedTrack(Track track, Track prospectiveNextTrack) {
        Point commonPoint = track.getCommonPoint(prospectiveNextTrack);

        if (commonPoint != null) {
            track.setActiveConnection(commonPoint, prospectiveNextTrack);
        }

        return prospectiveNextTrack == track.getActiveConnectedTrackTowards(commonPoint);
    }

    public void changeRoute(Track trackInRoute, Track prospectiveNextTrack) {
        activePlayer.getActiveTrain().changeRoute(trackInRoute, prospectiveNextTrack);
    }

    public boolean hasCompletedContract(Player player) {
        ActiveTrain activeTrain = player.getActiveTrain();
        return activeTrain.getDestination() == activeTrain.getCurrentPosition();
    }

    public void fulfilledCurrentContract(Player player) {
        player.fulfilledCurrentContract();
    }

    public void failedCurrentContract(Player player) {
        player.failedCurrentContract();
    }

    public boolean hasAContract(Player player){
        return player.getCurrentContract() != null;
    }

    public void increaseTrainSpeed() {
        double currentSpeed = activePlayer.getActiveTrain().getCurrentSpeed();
        double newSpeed = currentSpeed * 1.25;
        activePlayer.getActiveTrain().setCurrentSpeed(newSpeed);
    }

    public void decreaseTrainSpeed() {
        double currentSpeed = activePlayer.getActiveTrain().getCurrentSpeed();
        double newSpeed = currentSpeed / 1.25;
        activePlayer.getActiveTrain().setCurrentSpeed(newSpeed);
    }

    // TODO decide on which way to set train speed, discretely or continuously?
    // Leaving both sets of methods in for the mean time.
    public void setTrainSpeed(double percentage) {
        double newSpeed = activePlayer.getActiveTrain().getTrain().getMaxSpeed() * percentage;
        activePlayer.getActiveTrain().setCurrentSpeed(newSpeed);
    }

    public void reverseTrain() {
        activePlayer.getActiveTrain().reverse();
    }

    public void update(long timePassed) {
        for (Player player : players) {
            ActiveTrain activeTrain = player.getActiveTrain();
            if (activeTrain != null) {
                activeTrain.move(timePassed);
            }
        }
    }

    public void setActiveConnection(Track track, Intersection intersection, Track prospectiveNextTrack) {
        track.setActiveConnection(intersection, prospectiveNextTrack);
    }

}
