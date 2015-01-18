package lys.sepr.game;

import lys.sepr.game.resources.TrainType;
import lys.sepr.game.world.Location;
import lys.sepr.game.world.Route;
import lys.sepr.game.world.Utilities;

import java.util.ArrayList;
import java.util.List;

public class Contract {

    private Route initialRoute;

    // Non-mandatory
    private List<Location> bonusStops = new ArrayList<Location>();
    private int timeLimit;
    private TrainType requiredTrainType;
    private int moneyPayout;
    private int reputationPayout;
    private int requiredReputation;

    Contract(Route possibleRoute, List<Location> bonusStops, int timeLimit,
             TrainType requiredTrainType, int moneyPayout,
             int reputationPayout, int requiredReputation) {

        this.initialRoute = possibleRoute;
        this.bonusStops = bonusStops;

        if (timeLimit < 0) timeLimit = Integer.MAX_VALUE;
        this.timeLimit = timeLimit;

        this.requiredTrainType = requiredTrainType;

        if (moneyPayout < 0) moneyPayout = (int) Utilities.routeLength(possibleRoute.getTracks());
        this.moneyPayout = moneyPayout;

        if (reputationPayout < 0) reputationPayout = (int) Utilities.routeLength(possibleRoute.getTracks());
        this.reputationPayout = reputationPayout;

        if (requiredReputation < 0) requiredReputation = 0;
        this.requiredReputation = requiredReputation;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public TrainType getRequiredTrainType() {
        return requiredTrainType;
    }

    public int getMoneyPayout() {
        return moneyPayout;
    }

    public int getReputationPayout() {
        return reputationPayout;
    }

    public int getRequiredReputation() {
        return requiredReputation;
    }

    public Route getInitialRoute() {
        return initialRoute.clone();
    }

}
