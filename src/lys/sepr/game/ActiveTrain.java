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
    private List<Track> remainderOfRoute;
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

    public void setCurrentSpeed(Double newSpeed) {
        currentSpeed = newSpeed;
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

    public void move(long timePassed) {
        Track currentTrack = remainderOfRoute.get(0);

        List<Double> directionOfTravel = Utilities.getVector(currentPosition, facing);
        directionOfTravel = Utilities.unitVector(directionOfTravel);
        List<Double> vectorOfTravel = Utilities.multiply(directionOfTravel, timePassed * currentSpeed);
        Point projectedPosition = new Point(currentPosition);
        projectedPosition.translate(vectorOfTravel.get(0), vectorOfTravel.get(1));

        Point closestPoint = Utilities.closestPoint(projectedPosition, currentTrack);
        if (closestPoint.equals(facing)) {
            // If the closestPoint on the track is the point we are facing
            // we have passed the end of track, and should go to the next track
            remainderOfRoute.remove(currentTrack);

            if (remainderOfRoute.isEmpty()){
                // If there are no tracks left then we have reached our destination,
                // or the end of the route if it was changed and no longer reaches
                // the intended destination.
                currentPosition = new Point(facing);
            } else {
                // Otherwise we should move again for the remainder of time left after
                // travelling to the point we were facing.
                Point previousPosition = new Point(currentPosition);
                currentPosition = new Point(facing);
                Track nextTrack = remainderOfRoute.get(0);
                facing = nextTrack.getOtherPoint(nextTrack.getCommonPoint(currentTrack));
                updateOrientation();
                Double distanceTravelledOnPreviousTrack = Utilities.distance(previousPosition, currentPosition);
                Double distanceLeftToTravelOnNextTrack = Utilities.magnitude(vectorOfTravel) - distanceTravelledOnPreviousTrack;
                long timeLeftToTravelOnNewTrack = (long) (distanceLeftToTravelOnNextTrack / currentSpeed);
                move(timeLeftToTravelOnNewTrack);
            }
        } else {
            // If the closestPoint on the track is not point we are facing
            // we are still on the track.
            currentPosition = projectedPosition;
        }
    }
}
