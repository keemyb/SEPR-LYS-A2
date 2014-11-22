package lys.sepr.game.world;

import java.util.ArrayList;

public class Track {

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

    public ArrayList<Double> getVector(Point towards) {
        Point from = getOtherPoint(towards);

        ArrayList<Double> vector = new ArrayList<Double>(2);
        vector.add(towards.getX() - from.getX());
        vector.add(towards.getY() - from.getY());
        return vector;
    }

    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);
    }

    public void addConnectedTrack(Track track) {
        connectedTracks.add(track);
    }
}
