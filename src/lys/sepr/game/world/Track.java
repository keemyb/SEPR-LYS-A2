package lys.sepr.game.world;

import java.util.ArrayList;

public class Track {

    private ArrayList<Point> points = new ArrayList<Point>();
    private ArrayList<Track> connectedTracks = new ArrayList<Track>();
    private ArrayList<Intersection> intersections = new ArrayList<Intersection>();

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
        the one that is not the origin point */
        Point destination = null;
        for (Point point : points) {
            if (!point.equals(origin)) destination = point;
        }

        // the next track is the one that shares a point with this track.
        for (Track connectedTrack : connectedTracks) {
            for (Point point: connectedTrack.getPoints()) {
                if (point.equals(destination)) return connectedTrack;
            }
        }

        return null;

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
        Point from = (points.get(0).equals(towards)) ? points.get(1) : points.get(0);

        ArrayList<Double> vector = new ArrayList<Double>(2);
//        System.out.println(from.getX());
//        System.out.println(from.getY());
////        System.out.println(from.getX());
////        System.out.println(from.getY());
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
