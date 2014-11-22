package lys.sepr.game.world;

import java.util.ArrayList;
import static lys.sepr.game.world.Utilities.closestPoint;
import static lys.sepr.game.world.Utilities.getVector;

public class Track {

    private final double nudgeStrength = 0.1;
    private ArrayList<Point> points = new ArrayList<Point>();
    private ArrayList<Track> connectedTracks = new ArrayList<Track>();
    private ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    private ArrayList<Track> nextTracks = new ArrayList<Track>();

    Track(Point a, Point b) {
        points.add(a);
        points.add(b);
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public Track getNextTrack(Point origin) {
        // origin is the point that we are travelling away from.
        if (connectedTracks.isEmpty()) return null;

        /* as tracks have no distinguishable start and end point, the destination
        is the one that is not the origin point */
        Point destination = getOtherPoint(origin);

        // Look for the track that has a point that equals the destination.
        for (Track track : nextTracks) {
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
            for (Track existingNextTrack : nextTracks) {
                if (intersection.getTracks().contains(existingNextTrack)) trackToRemove = existingNextTrack;
            }
            if (trackToRemove != null) nextTracks.remove(trackToRemove);
            nextTracks.add(prospectiveNextTrack);
        }
    }

    public Point getOtherPoint(Point point) {
        return (points.get(0).equals(point)) ? points.get(1) : points.get(0);
    }

    public Intersection getIntersection(Point point) {
        // an orphaned track will have no intersections as it doesn't connect to anything.
        if (connectedTracks.isEmpty()) return null;

        for (Intersection intersection : intersections) {
            if (point.equals(intersection.getPoint())) return intersection;
        }

        return null;
    }

    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);
    }

    public void removeIntersection(Intersection intersection) {
        intersections.remove(intersection);
    }

    public void addConnectedTrack(Track track) {
        connectedTracks.add(track);
    }

    public void removeConnectedTrack(Track track) {
        connectedTracks.remove(track);
        nextTracks.remove(track);
    }

    public void move(Point from, Point to) {
        Intersection existingIntersection = getIntersection(from);
        if (getPoints().contains(from)) {
            // A point cannot be moved to the same location.
            if (to.equals(from)) return;
            // A track cannot have two points in the same place
            if (points.contains(to)) return;
            points.remove(from);
            points.add(to);
        }
        // If there was an intersection at the point where we moved from, break the connection
        if (existingIntersection != null) {
            existingIntersection.removeTrack(this);
            removeIntersection(existingIntersection);
        }
    }

    public void nudge(Point awayFrom) {
        ArrayList<Double> vector = getVector(getOtherPoint(awayFrom), awayFrom);
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
}
