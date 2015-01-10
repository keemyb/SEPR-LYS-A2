package lys.sepr.game;

import lys.sepr.game.resources.TrainType;
import lys.sepr.game.world.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Game {

    private static int timePerTurn = 30;
    private List<Player> players;
    private int maxContracts;
    private Player activePlayer;
    private Map map;
    private boolean gameStarted;
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

    public void startGame(Player player) {
        if (gameStarted) return;
        gameStarted = true;

        if (players.contains(player)) {
            activePlayer = player;
        } else {
            activePlayer = players.get(0);
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

    public void beginTurn(Contract contract) {
        if (activePlayer.getCurrentContract() == null) {
            Random r = new Random();
            int nextContractIndex = r.nextInt(possibleContracts.size());

            activePlayer.assignContract(possibleContracts.get(nextContractIndex));
            possibleContracts.remove(nextContractIndex);
        }
    }

    public void continueTurn() {

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
    }

    public void changeNextTrack(Track trackInRoute, Track prospectiveNextTrack) {
        for (Player player : players) {
            ActiveTrain activeTrain = player.getActiveTrain();
            if (activeTrain != null) {
                // TODO change activeTrain implementation so that it's
                // route is checked even if the track was changed by a
                // previous iteration of this loop. ~ line 143
                activeTrain.changeRoute(trackInRoute, prospectiveNextTrack);
            }
        }
    }

}
