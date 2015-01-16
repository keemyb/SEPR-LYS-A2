package lys.sepr.game.resources;

import java.util.ArrayList;
import java.util.List;

public class TrainStorage {

    private static List<Train> trains = new ArrayList<Train>();

    //all max values are subject to change, all quantities are set to max_value initially

    static Train machineGunWagon = new Train("Machine-gun Wagon", 500, 0, Integer.MAX_VALUE,
            TrainType.PASSENGER, 15, 600, 1d, 0, 100, 100, 10);

    static Train toddlerTerror = new Train("FatherCare Toddler Terror", 500, 0, Integer.MAX_VALUE,
            TrainType.CARGO, 10, 1000, 0.85d, 0, 200, 70, 20);

    static Train comfyCarriage = new Train("DreamTec Comfy Carriage", 750, 0, Integer.MAX_VALUE,
            TrainType.PASSENGER, 20, 500, 0.9d, 0, 150, 150, 10);

    static Train tinyTank = new Train("Propaganda Co. Tiny Tank", 750, 0, Integer.MAX_VALUE,
            TrainType.CARGO, 20, 750, 0.9d, 0, 200, 30, 5);

    static {
        trains.add(machineGunWagon);
        trains.add(toddlerTerror);
        trains.add(comfyCarriage);
        trains.add(tinyTank);
    }

    public static Train getStarterTrain(TrainType trainType){
        int lowestRating = Integer.MAX_VALUE;
        Train lowestTrain = null;

        for (Train train : trains) {
            if (train.getType() == trainType) continue;

            int rating = train.getRating();
            if (rating < lowestRating) {
                lowestTrain = train;
            }
        }
        return new Train(lowestTrain);
    }

    public static List<Train> getTrains() {
        // Don't want stock trains to be modified at runtime.
        return new ArrayList<Train>(trains);
    }
}
