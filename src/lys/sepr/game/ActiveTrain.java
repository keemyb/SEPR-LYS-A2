package lys.sepr.game;

import lys.sepr.game.resources.Train;
import lys.sepr.game.world.Point;
import lys.sepr.game.world.Route;
import lys.sepr.game.world.Track;
import lys.sepr.game.world.Utilities;

import java.util.ArrayList;
import java.util.List;

public class ActiveTrain {

    private Train train;
    private Point currentPosition;
    private Point destination;
    private Point facing;
    private List<Track> remainderOfRoute = new ArrayList<Track>();
    private Double orientation;
    private Double currentSpeed;

    public ActiveTrain(Train train, Route initialRoute) {
        this.train = train;
        remainderOfRoute = initialRoute.getTracks();
        Track firstTrack = remainderOfRoute.get(0);

        currentPosition = Utilities.closestPoint(initialRoute.getFrom(), firstTrack);
        destination = initialRoute.getTo();

        if (remainderOfRoute.size() > 1) {
            Track secondTrack = remainderOfRoute.get(1);
            facing = firstTrack.getCommonPoint(secondTrack);
        } else {
            facing = Utilities.closestPoint(destination, firstTrack);
        }

        orientation = Math.toDegrees(Math.atan2(facing.getY() - currentPosition.getY(),
                facing.getX() - currentPosition.getX()));
    }
}
