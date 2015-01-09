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
    private Double orientation;
    private Double currentSpeed;

    public ActiveTrain(Train train, Route initialRoute) {
        this.train = train;
        remainderOfRoute.addAll(initialRoute.getTracks());
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
        if (newSpeed < 0) {
            currentSpeed = 0d;
        } else if (newSpeed > train.getMaxSpeed()) {
            currentSpeed = (double) train.getMaxSpeed();
        } else {
            currentSpeed = newSpeed;
        }
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

        Double distanceToTravel = timePassed * currentSpeed;
        if (train.getFuelRequired(distanceToTravel) > train.getAmountOfFuel()) {
            distanceToTravel = train.getMileageLeft();
        }

        List<Double> vectorOfTravel = Utilities.multiply(directionOfTravel, distanceToTravel);
        Point projectedPosition = new Point(currentPosition);
        projectedPosition.translate(vectorOfTravel.get(0), vectorOfTravel.get(1));

        Point closestPoint = Utilities.closestPoint(projectedPosition, currentTrack);
        if (closestPoint.equals(facing)) {
            // If the closestPoint on the track is the point we are facing
            // we have passed the end of track, and should go to the next track
            remainderOfRoute.remove(currentTrack);

            Point previousPosition = new Point(currentPosition);
            currentPosition = new Point(facing);

            Double distanceTravelledOnPreviousTrack = Utilities.distance(previousPosition, currentPosition);
            train.useFuel(distanceTravelledOnPreviousTrack);

            // If there are no tracks left then we have reached our destination,
            // or the end of the route if it was changed and no longer reaches
            // the intended destination.
            // Otherwise we should move again for the remainder of time left after
            // travelling to the point we were facing.
            if (!remainderOfRoute.isEmpty()) {
                Track nextTrack = remainderOfRoute.get(0);
                facing = nextTrack.getOtherPoint(nextTrack.getCommonPoint(currentTrack));
                updateOrientation();

                if (nextTrack.isBroken()) return;

                Double distanceLeftToTravelOnNextTrack = Utilities.magnitude(vectorOfTravel) - distanceTravelledOnPreviousTrack;
                long timeLeftToTravelOnNewTrack = (long) (distanceLeftToTravelOnNextTrack / currentSpeed);
                move(timeLeftToTravelOnNewTrack);
            }
        } else {
            // If the closestPoint on the track is not point we are facing
            // we are still on the track.
            currentPosition = projectedPosition;
            train.useFuel(distanceToTravel);
        }
    }

    public void changeRoute(Track trackInRoute, Track prospectiveNextTrack) {
        if (!remainderOfRoute.contains(trackInRoute)) return;

        Track currentTrack = remainderOfRoute.get(0);
        Intersection intersection = currentTrack.getIntersection(facing);

        if (intersection == null) return;

        Track oldNextTrack = trackInRoute.getNextTrack(trackInRoute.getOtherPoint(facing));
        trackInRoute.setNextTrack(intersection, prospectiveNextTrack);

        // If the prospective next track was not set successfully then we don't
        // want to change the route.
        if (oldNextTrack.equals(trackInRoute.getNextTrack(trackInRoute.getOtherPoint(facing)))) return;

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
