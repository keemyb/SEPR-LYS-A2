package lys.sepr.game.world;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

/**
 * This class contains helper functions that may be useful for working with
 * classes in the World package.
 */
public final class Utilities {

    /**
     * Computes the cross product of two vectors.
     * @return The cross product of the two vectors, as a double.
     */
    public static double crossProduct(List<Double> vector1, List<Double> vector2) {
        double dotProduct = dotProduct(vector1, vector2);
        double magnitude = magnitude(vector1) * magnitude(vector2);
        double cosTheta = dotProduct / magnitude;
        double theta = acos(cosTheta);
        return (theta * 180 / PI);
    }

    /**
     * Computes the dot product of two vectors.
     * @return The dot product of the two vectors, as a double.
     */
    public static double dotProduct(List<Double> vector1, List<Double> vector2) {
        double dotProduct = 0;
        for (int i=0; i <vector1.size(); i++) {
            dotProduct += vector1.get(i) * vector2.get(i);
        }
        return dotProduct;
    }

    /**
     * Computes the magnitude of a vector.
     * @return The magnitude of the vector, as a double.
     */
    public static double magnitude(List<Double> vector) {
        double magnitude = 0;
        for (double component : vector) {
            magnitude += pow(component, 2);
        }
        return sqrt(magnitude);
    }

    /**
     * Computes the vector of a track, from one point to another.
     * @return the vector of a track, as an ArrayList of Double with two elements.
     */
    public static List<Double> getVector(Track track) {
        return getVector(track.getPoints().get(0), track.getPoints().get(1));
    }

    /**
     * Computes the un-normalised vector from one point to another.
     * Note that the order of arguments matter.
     * @param from    The point that the vector will start from.
     * @param towards The point that the vector will point towards.
     * @return the vector between the two points,
     * as an ArrayList of Double with two elements.
     */
    public static List<Double> getVector(Point from, Point towards) {
        List<Double> vector = new ArrayList<Double>(2);
        vector.add(towards.getX() - from.getX());
        vector.add(towards.getY() - from.getY());
        return vector;
    }

    //TODO rewrite the closestPoint JavaDoc
    /**
     * Computes the closest point to a certain point, out of a list of points.
     * @param to     The point which you want to find the closest point to.
     * @param points
     * @return The closest point to the specified point.
     */
    public static Point closestPoint(Point to, ArrayList<Point> points) {
        Point closestPoint = null;
        Double closestDistance = null;
        for (Point point : points) {
            Double distance = magnitude(getVector(to, point));
            if (closestDistance == null || distance < closestDistance) {
                closestDistance = distance;
                closestPoint = point;
            }
        }
        return closestPoint;
    }

    /**
     * Computes the distance between two points.
     * @return the distance between two points, as a double.
     */
    public static double distance(Point point1, Point point2) {
        return magnitude(getVector(point1, point2));
    }

    /**
     * Computes the length of a track.
     * @return the length of the track, as a double.
     */
    public static double length(Track track) {
        return distance(track.getPoints().get(0), track.getPoints().get(1));
    }

    /**
     * Computes the normalised vector of a given vector.
     * @param vector The vector to be normalised.
     * @return the normalised vector,
     * as an ArrayList of Double with two elements.
     */
    public static List<Double> unitVector(List<Double> vector) {
        ArrayList<Double> unitVector = new ArrayList<Double>();
        double size = magnitude(vector);
        for (double component : vector) {
            unitVector.add(component/size);
        }
        return unitVector;
    }

    /**
     * Multiplies one vector by a constant.
     * @param vector   The vector to be used as a multiplicand.
     * @param constant The quantity to be used as a multiplicand.
     * @return a new multiplied vector,
     * as an ArrayList of Double with two elements.
     */
    public static List<Double> multiply(List<Double> vector, double constant) {
        List<Double> newVector = new ArrayList<Double>();
        for (double component : vector) {
            newVector.add(component * constant);
        }
        return newVector;
    }

    public static Track closestTrack(Point to, ArrayList<Track> tracks){
        return closestTrack(to, tracks, Double.POSITIVE_INFINITY);
    }

    //TODO rewrite the closestTrack JavaDoc
    /**
     * Computes the closest track to a certain point, out of a list of tracks
     * within a certain range.
     * @param to     The point which you want to find the closest track to.
     * @param tracks
     * @param range  The maximum distance allowed between the track and point.
     * @return The closest track to the specified point.
     */
    public static Track closestTrack(Point to, ArrayList<Track> tracks, double range) {
        Track closestTrack = null;
        Double closestDistance = null;
        for (Track track : tracks) {
            double distance = closestDistance(to, track);
            if (distance < range && (closestDistance == null || distance < closestDistance)) {
                closestDistance = distance;
                closestTrack = track;
            }
        }
        return closestTrack;
    }

    /**
     * Computes the closest distance from a track to a certain point.
     * @param to
     * @param track The track which the closest distance will be found.
     * @return The closest distance from the track to the specified point.
     */
    public static double closestDistance(Point to, Track track) {
        Point point = closestPoint(to, track);
        return magnitude(getVector(point, to));
    }

    /**
     * Gets the closest point from a track to a certain point.
     * @param to
     * @param track The track which the closest point will be found.
     * @return The closest point from the track to the specified point.
     */
    public static Point closestPoint(Point to, Track track) {
        // With help from http://doswa.com/2009/07/13/circle-segment-intersectioncollision.html
        Point trackPoint1 = track.getPoints().get(0);
        Point trackPoint2 = track.getPoints().get(1);
        List<Double> trackVector = getVector(trackPoint1, trackPoint2);
        List<Double> unitTrackVector = unitVector(trackVector);
        List<Double> trackPointToClickPointVector = getVector(trackPoint1, to);
        double lengthProjectedVector = dotProduct(trackPointToClickPointVector, unitTrackVector);
        List<Double> projectedVector = multiply(unitTrackVector, lengthProjectedVector);
        Point closestPoint;
        if (lengthProjectedVector < 0) {
            closestPoint = new Point(trackPoint1.getX(), trackPoint1.getY());
        } else if (lengthProjectedVector > magnitude(trackVector)) {
            closestPoint = new Point(trackPoint2.getX(), trackPoint2.getY());
        } else {
            closestPoint = new Point(trackPoint1.getX() + projectedVector.get(0),
                    trackPoint1.getY() + projectedVector.get(1));
        }
        return closestPoint;
    }

    //TODO rewrite the closestLocation JavaDoc
    /**
     * Computes the closest location to a certain point, out of a list of locations
     * within a certain range.
     * @param to        The point which you want to find the location track to.
     * @param locations
     * @param range     The maximum distance allowed between the locations and point.
     * @return The closest location to the specified point.
     */
    public static Location closestLocation(Point to, ArrayList<Location> locations, double range) {
        Location closestLocation = null;
        Double closestDistance = null;
        for (Location location : locations) {
            double distance = magnitude(getVector(to, location.getPoint()));
            if (distance < range && (closestDistance == null || distance < closestDistance)) {
                closestDistance = distance;
                closestLocation = location;
            }
        }
        return closestLocation;
    }

    public static Location closestLocation(Point to, ArrayList<Location> locations) {
        return closestLocation(to, locations, Double.POSITIVE_INFINITY);
    }

    /**
     * Computes the tracks within a range of a certain point, out of a list of tracks.
     * @param to
     * @param tracks
     * @param range  The maximum distance allowed between the track and point.
     * @return The list of tracks within range of the specified point.
     */
    public static List<Track> tracksWithinRange(Point to, List<Track> tracks, double range) {
        ArrayList<Track> tracksWithinRange = new ArrayList<Track>();
        for (Track track : tracks) {
            if (closestDistance(to, track) <= range) {
                tracksWithinRange.add(track);
            }
        }
        return tracksWithinRange;
    }

    /**
     * Computes the length of a route.
     * @param route A list of tracks representing a route.
     * @return The total length of the route.
     */
    public static double routeLength(List<Track> route) {
        double length = 0;
        for (Track track : route) {
            length += length(track);
        }
        return length;
    }

    public static String titleCase(String input) {
        String[] parts = input.split(" ");
        StringBuilder newString = new StringBuilder();
        for (String part : parts) {
            char[] chars = part.toLowerCase().toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);

            newString.append(new String(chars)).append(" ");
        }
        return newString.toString();
    }

    public static Track getScaledTrack(Track track, Point about, double scale) {
        Point otherPoint = track.getOtherPoint(about);
        List<Double> vector = getVector(about, otherPoint);
        vector = Utilities.multiply(vector, scale);
        Point scaledOtherPoint = new Point(about);
        scaledOtherPoint.translate(vector.get(0), vector.get(1));
        return new Track(about, scaledOtherPoint);
    }
}
