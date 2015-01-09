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

        updateFacing();
        updateOrientation();
    }

    public Train getTrain() {
        return train;
    }

    public Point getCurrentPosition() {
        return currentPosition;
    }

    public Point getDestination() {
        return destination;
    }

    public Point getFacing() {
        return facing;
    }

    public List<Track> getRemainderOfRoute() {
        return remainderOfRoute;
    }

    public Double getOrientation() {
        return orientation;
    }

    public Double getCurrentSpeed() {
        return currentSpeed;
    }

    private void updateFacing() {
        Track currentTrack = remainderOfRoute.get(0);
        if (remainderOfRoute.size() > 1) {
            Track nextTrack = remainderOfRoute.get(1);
            facing = currentTrack.getCommonPoint(nextTrack);
        } else {
            facing = Utilities.closestPoint(destination, currentTrack);
        }
    }

    private void updateOrientation() {
        orientation = Math.toDegrees(Math.atan2(facing.getY() - currentPosition.getY(),
                facing.getX() - currentPosition.getX()));
    }
}
