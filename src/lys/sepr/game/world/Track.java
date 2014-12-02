package lys.sepr.game.world;

import java.util.ArrayList;
import static lys.sepr.game.world.Utilities.closestPoint;
import static lys.sepr.game.world.Utilities.getVector;

public class Track {

    private final double nudgeStrength = 0.1;
    private ArrayList<Point> points = new ArrayList<Point>();
    private ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    private ArrayList<Track> activeNextTracks = new ArrayList<Track>();
    private Boolean broken = false;

    Track(Point a, Point b) {
        points.add(a);
        points.add(b);
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public Track getNextTrack(Point origin) {
        // origin is the point that we are travelling away from.
        if (intersections.isEmpty()) return null;

        /* as tracks have no distinguishable start and end point, the destination
        is the one that is not the origin point */
        Point destination = getOtherPoint(origin);

        // Look for the track that has a point that equals the destination.
        for (Track track : activeNextTracks) {
            for (Point point : track.getPoints()) {
                if (point.equals(destination)) return track;
            }
        }

        return null;
    }

    public void setNextTrack(Intersection intersection, Track prospectiveNextTrack) {
        if (intersection.getValidNextTracks(this).contains(prospectiveNextTrack)) {
            Track trackToRemove = null;
            // Remove existing next tracks if they connect via this intersection
            for (Track existingNextTrack : activeNextTracks) {
                if (intersection.getTracks().contains(existingNextTrack)) trackToRemove = existingNextTrack;
            }
            if (trackToRemove != null) activeNextTracks.remove(trackToRemove);
            activeNextTracks.add(prospectiveNextTrack);
        }
    }

    public Point getOtherPoint(Point point) {
        return (points.get(0).equals(point)) ? points.get(1) : points.get(0);
    }

    public Intersection getIntersection(Point point) {
        if (intersections.isEmpty()) return null;

        for (Intersection intersection : intersections) {
            if (point.equals(intersection.getPoint())) return intersection;
        }

        return null;
    }

    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }

    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);
    }

    public void removeIntersection(Intersection intersection) {
        intersections.remove(intersection);
    }

    public void move(Point from, Point to) {
        if (getPoints().contains(from)) {
            // A point cannot be moved to the same location.
            if (to.equals(from)) return;
            // A track cannot have two points in the same place
            if (points.contains(to)) return;
            points.remove(from);
            points.add(to);
        }

        // If there was an intersection at the point where we moved from, break the connection
        Intersection existingIntersection = getIntersection(from);
        if (existingIntersection != null) {
            existingIntersection.removeTrack(this);
            removeIntersection(existingIntersection);
        }

        // Update the valid tracks as the angle between tracks may have changed
        for (Intersection intersection : intersections) {
            intersection.updateValidTracks();
        }
    }

    public void nudge(Point awayFrom) {
        // TODO: recursive nudge to prevent nudging into another track/intersection
        ArrayList<Double> vector = getVector(awayFrom, getOtherPoint(awayFrom));
        for (int i=0; i < vector.size(); i++) {
            vector.set(i, vector.get(i) * nudgeStrength);
        }
        // only move the point closest to the point we want to move away from
        Point closestPoint = closestPoint(awayFrom, points);
        closestPoint.translate(vector.get(0), vector.get(1));
    }

    public Point getCommonPoint(Track other) {
        for (Point point : points) {
            for (Point otherPoint : other.getPoints()) {
                if (point.equals(otherPoint)) return point;
            }
        }
        return null;
    }

    public ArrayList<Track> getConnectedTracks() {
        ArrayList<Track> connectedTracks = new ArrayList<Track>();
        for (Intersection intersection : intersections) {
            for (Track track : intersection.getTracks()) {
                if (!track.equals(this) && !connectedTracks.contains(track)){
                    connectedTracks.add(track);
                }
            }
        }
        return connectedTracks;
    }

    public ArrayList<Track> getActiveNextTracks() {
        return activeNextTracks;
    }

    public ArrayList<Track> getValidNextTracks() {
        ArrayList<Track> validNextTracks = new ArrayList<Track>();
        for (Point point : points) {
            validNextTracks.addAll(getValidNextTracks(point));
        }
        return validNextTracks;
    }

    public ArrayList<Track> getValidNextTracks(Point towards) {
        Intersection nextIntersection = getIntersection(towards);
        return nextIntersection.getValidNextTracks(this);
    }

    public void removeActiveNextTrack(Track track) {
        activeNextTracks.remove(track);
    }

    public Boolean isBroken() {
        return broken;
    }

    public void setBroken(Boolean broken) {
        this.broken = broken;
    }
}
