package lys.sepr.game;

import lys.sepr.game.resources.Train;
import lys.sepr.game.world.*;

import java.util.ArrayList;
import java.util.List;

public class ActiveTrain {

    private final Train train;
    private Point currentPosition;
    private final Point destination;
    private Point facing;
    private List<Track> remainderOfRoute = new ArrayList<Track>();
    private double orientation;
    private double currentSpeed;
    private List<Double> directionOfTravel;
    /* Have to be this close to a point to be considered "there".
        This is due to points never really being able to perfectly align when
        using arbitrary time-steps that will never be integers (and also the fact
        that direction vectors may not be calculated perfectly). Adjust this
        if trains look as if they have reached a point but are not recognised
        */
    private static double distanceThreshold = 0.0d;

    // This changes if the trains route can be changed so that it is
    // not possible to move from one track to another. (This does
    // not include broken tracks).
    private static boolean mustChooseValidConnectedTrack = true;

    public ActiveTrain(Train train, Route initialRoute) {
        this.train = train;
        remainderOfRoute.addAll(initialRoute.getTracks());
        Track firstTrack = remainderOfRoute.get(0);

        currentPosition = Utilities.closestPoint(initialRoute.getFrom(), firstTrack);
        destination = initialRoute.getTo();

        updateFacing();
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

    public double getOrientation() {
        return orientation;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public static double getDistanceThreshold() {
        return distanceThreshold;
    }

    public void setCurrentSpeed(double newSpeed) {
        if (newSpeed < 0) {
            currentSpeed = 0d;
        } else if (newSpeed > train.getMaxSpeed()) {
            currentSpeed = (double) train.getMaxSpeed();
        } else {
            currentSpeed = newSpeed;
        }
    }

    public static void setMustChooseValidConnectedTrack(boolean mustChooseValidConnectedTrack) {
        ActiveTrain.mustChooseValidConnectedTrack = mustChooseValidConnectedTrack;
    }

    private void updateFacing() {
        Track currentTrack = remainderOfRoute.get(0);
        if (remainderOfRoute.size() > 1) {
            Track nextTrack = remainderOfRoute.get(1);
            facing = currentTrack.getCommonPoint(nextTrack);
        } else {
            facing = Utilities.closestPoint(destination, currentTrack);
        }
        updateOrientation();
    }

    private void updateOrientation() {
        directionOfTravel = Utilities.getVector(currentPosition, facing);
        directionOfTravel = Utilities.unitVector(directionOfTravel);
        orientation = Math.atan2(-directionOfTravel.get(1), directionOfTravel.get(0));
    }

    public void move(long timePassed) {
        if (hasReachedDestination() || remainderOfRoute.isEmpty()) return;

        Track currentTrack = remainderOfRoute.get(0);

        double distanceToTravel = timePassed * currentSpeed;
        if (train.getFuelRequired(distanceToTravel) > train.getAmountOfFuel()) {
            distanceToTravel = train.getMileageLeft();
        }

        List<Double> vectorOfTravel = Utilities.multiply(directionOfTravel, distanceToTravel);
        Point projectedPosition = new Point(currentPosition);
        projectedPosition.translate(vectorOfTravel.get(0), vectorOfTravel.get(1));

        Point closestPoint = Utilities.closestPoint(projectedPosition, currentTrack);
        if (Utilities.distance(closestPoint, facing) <= distanceThreshold) {
            /* If the closestPoint on the track is (or near enough to) the point
            we are facing then we are at the end of the track, and should go to
            the next track, if possible */

            Point positionBeforeMove = new Point(currentPosition);
            currentPosition = new Point(facing);

            Double distanceTravelledSoFar = Utilities.distance(positionBeforeMove, currentPosition);
            train.useFuel(distanceTravelledSoFar);

            // If there are no tracks left then we have reached our destination,
            // or the end of the route if it was changed and no longer reaches
            // the intended destination.
            // Otherwise we should move again for the remainder of time left after
            // travelling to the point we were facing.
            if (remainderOfRoute.size() == 1) {
                remainderOfRoute.clear();
                return;
            } else {
                Track nextTrack = remainderOfRoute.get(1);
                /* checking to see if it is valid to move to the next track,
                if it isn't we can move no further
                */
                if (nextTrack.isBroken()) return;
                if (nextTrack != currentTrack.getActiveConnectedTrackTowards(facing)) return;

                // we are good to go on.
                remainderOfRoute.remove(currentTrack);
                facing = nextTrack.getOtherPoint(facing);
                updateOrientation();

                Double distanceLeftToTravelOnNextTrack = Utilities.magnitude(vectorOfTravel) - distanceTravelledSoFar;
                long timeLeftToTravelOnNewTrack = (long) (distanceLeftToTravelOnNextTrack / currentSpeed);
                move(timeLeftToTravelOnNewTrack);
            }
        } else {
            // If the closestPoint on the track is not point we are facing
            // we are still on the track.
            currentPosition = closestPoint;
            train.useFuel(distanceToTravel);
        }
    }

    public boolean hasReachedDestination() {
        return Utilities.distance(currentPosition, destination) <= distanceThreshold;
    }

    public void changeRoute(Track trackInRoute, Track prospectiveNextTrack) {
        if (!remainderOfRoute.contains(trackInRoute)) return;

        if (mustChooseValidConnectedTrack) {
            if (!trackInRoute.getValidConnections().contains(prospectiveNextTrack)) return;
        }

        // Removing all tracks after the track in route as the route has changed.
        for (int i=remainderOfRoute.size() - 1; i > remainderOfRoute.indexOf(trackInRoute); i--) {
            remainderOfRoute.remove(remainderOfRoute.get(i));
        }

        remainderOfRoute.add(prospectiveNextTrack);
    }

    public void reverse() {
        Track currentTrack = remainderOfRoute.get(0);

        remainderOfRoute.clear();
        remainderOfRoute.add(currentTrack);

        facing = currentTrack.getOtherPoint(facing);
        updateOrientation();
    }
}
