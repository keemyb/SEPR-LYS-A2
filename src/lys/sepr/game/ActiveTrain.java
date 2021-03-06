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
    /* How close a train should be to a location to be considered "there",
    Adjust this if trains look as if they have reached their point but
    are not recognised as such.
    */
    private static double destinationRadiusThreshold = 10.0d;

    // This changes if the trains route can be changed so that it is
    // not possible to move from one track to another. (This does
    // not include broken tracks).
    private static boolean mustChooseValidConnectedTrack = true;

    public ActiveTrain(Train train, Route initialRoute) {
        this.train = train;
        remainderOfRoute.add(initialRoute.getTracks().get(0));
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

    public static double getDestinationRadiusThreshold() {
        return destinationRadiusThreshold;
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
        if (facing.equals(closestPoint)) {
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
                currentPosition = facing;
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
        double distance = Utilities.distance(currentPosition, destination);
        boolean withinAcceptableDistance = distance <= destinationRadiusThreshold;
        if (withinAcceptableDistance && currentPosition != destination) {
            currentPosition = destination;
            train.useFuel(distance);
        }
        return withinAcceptableDistance;
    }

    public void changeRoute(Track prospectiveNextTrack) {
        if (remainderOfRoute.contains(prospectiveNextTrack)) return;

        Track currentTrack = remainderOfRoute.get(0);
        if (currentTrack.getValidConnectionsComingFrom(facing).contains(prospectiveNextTrack)) {
            reverse();
            changeRoute(prospectiveNextTrack);
            return;
        }

        Track connectedTrack = null;
        for (Track track : remainderOfRoute) {
            List<Track> validConnections = track.getValidConnections();
            if (validConnections.contains(prospectiveNextTrack)) {
                connectedTrack = track;
                break;
            }
        }

        if (connectedTrack != null) {
            // Removing all tracks after the track in route as the route has changed.
            for (int i=remainderOfRoute.size() - 1; i > remainderOfRoute.indexOf(connectedTrack); i--) {
                remainderOfRoute.remove(remainderOfRoute.get(i));
            }
            remainderOfRoute.add(prospectiveNextTrack);
        }
    }

    public boolean completedRoute() {
        return !hasReachedDestination() && getCurrentPosition().equals(getFacing());
    }

    public void reverse() {
        // Simply clearing the list and then adding the first one back
        // doesn't play nicely with move for some reason
        for (int i = remainderOfRoute.size() - 1; i >= 1; i--) {
            remainderOfRoute.remove(i);
        }

        Track currentTrack = remainderOfRoute.get(0);

        facing = currentTrack.getOtherPoint(facing);
        updateOrientation();
    }
}
