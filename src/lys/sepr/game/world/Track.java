package lys.sepr.game.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static lys.sepr.game.world.Utilities.closestPoint;
import static lys.sepr.game.world.Utilities.getVector;

/**
 * The Track class represents a straight railway track between two points.
 * A track is bi-directional, there is no distinction between a "start" point
 * and an "end" point.
 */
public class Track {

    private ArrayList<Point> points = new ArrayList<Point>();
    private ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    private Boolean broken = false;

    /**
     * @param a The point representing an end of the track.
     * @param b The point representing the other end of the track.
     */
    public Track(Point a, Point b) {
        points.add(new Point(a));
        points.add(new Point(b));
    }

    /**
     * @return The two points that the track is defined by.
     * These points are not guaranteed to be in any specific order.
     */
    public ArrayList<Point> getPoints() {
        return points;
    }

    /**
     * Returns the tracks that a train will move on towards, after it has
     * completed this one.
     * The list will contain a maximum of two tracks, one for each
     * end of the track.
     * @return The list of track that the train will move on towards.
     */
    public List<Track> getActiveConnectedTracks() {
        List<Track> activeConnectedTracks = new ArrayList<Track>();

        for (Point point : points) {
            Track activeConnectedTrack = getActiveConnectedTrackTowards(point);
            if (activeConnectedTrack != null) activeConnectedTracks.add(activeConnectedTrack);
        }
        return activeConnectedTracks;
    }

    /**
     * Returns the track that a train will move on towards, after it has
     * completed this one.
     * @param origin The point that a train has come from. This point
     *               must be one of the track's points.
     * @return The track that the train will move on towards.
     */
    public Track getActiveConnectedTrackComingFrom(Point origin) {
        Point destination = getOtherPoint(origin);

        return getActiveConnectedTrackTowards(destination);
    }

    /**
     * Returns the track that a train will move on towards, after it has
     * completed this one.
     * @param destination The point that a train is going towards. This point
     *               must be one of the track's points.
     * @return The track that the train will move on towards.
     */
    public Track getActiveConnectedTrackTowards(Point destination) {
        // destination is the point that we are travelling to.
        if (intersections.isEmpty()) return null;

        // Look for the track that has a point that equals the destination.
        Intersection intersection = getIntersection(destination);
        if (intersection == null) return null;

        List<Track> activeConnection = intersection.getActiveConnection();
        if (activeConnection.contains(this)) {
            for (Track track : activeConnection) {
                if (track != this) return track;
            }
        }

        return null;
    }

    /**
     * Sets the track that a train will move to after this track has been completed.
     * The next track will only be set if it is a valid next track, that is if it
     * can be traversed from this track. This is to prevent impossible situations
     * such as a train being able to complete hairpin turns.
     * This function is symmetric and so sets the prospective next tracks next track
     * to this track
     * @param point The point of the intersection where the routing will be changed.
     * @param prospectiveNextTrack The track that will be set as the next track,
     *                             if it is valid.
     */
    public void setActiveConnection(Point point, Track prospectiveNextTrack) {
        Intersection intersection = getIntersection(point);
        if (intersection != null) setActiveConnection(intersection, prospectiveNextTrack);
    }

    /**
     * Sets the track that a train will move to after this track has been completed.
     * The next track will only be set if it is a valid next track, that is if it
     * can be traversed from this track. This is to prevent impossible situations
     * such as a train being able to complete hairpin turns.
     * This function is symmetric and so sets the prospective next tracks next track
     * to this track
     * @param intersection The intersection where the routing will be changed.
     * @param prospectiveNextTrack The track that will be set as the next track,
     *                             if it is valid.
     */
    // Should this be called setActiveConnection
    public void setActiveConnection(Intersection intersection, Track prospectiveNextTrack) {
        if (!intersection.getValidConnections(this).contains(prospectiveNextTrack)) return;

        intersection.setActiveConnection(this, prospectiveNextTrack);
    }

    /**
     * Returns the other end point of a track.
     * @param point The known point of the track.
     * @return The other point of the track, unless the point supplied is not
     * part of the track, in which case null is returned.
     */
    public Point getOtherPoint(Point point) {
        if (points.contains(point)) {
            return (points.get(0).equals(point)) ? points.get(1) : points.get(0);
        } else return null;
    }

    /**
     * Returns the Intersection at the given point.
     * @param point The point where an intersection lies.
     * @return The intersection at the point given, if it exists.
     * If it does not then null is returned.
     */
    public Intersection getIntersection(Point point) {
        if (intersections.isEmpty()) return null;

        for (Intersection intersection : intersections) {
            if (point.equals(intersection.getPoint())) return intersection;
        }

        return null;
    }

    /**
     * Returns the intersections that the track is a member of.
     * @return a list of intersections the track is a member of.
     */
    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }

    /**
     * Adds an intersection to the track.
     * Note that any related initialisation is handled by the intersection
     * that has been added.
     * @param intersection the intersection to be added to the track.
     */
    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);
    }

    /**
     * Removes an intersection to the track.
     * Note that any related cleanup is handled by the intersection that
     * has been removed.
     * @param intersection the intersection to be removed from the track.
     */
    public void removeIntersection(Intersection intersection) {
        intersections.remove(intersection);
    }

    /**
     * Moves a point of the track from one position to another.
     *
     * <p>If a track is moved using this method directly, it will be
     * disconnected from the intersection at the point it has been moved from
     * if it exists.
     *
     * <p>If a track has been moved as part of intersection (using the
     * intersection's move method), it will remain a part of that
     * intersection.
     *
     * @param from The point of a track to be moved.
     * @param to Where the point of track will be moved to.
     */
    public void move(Point from, Point to) {
        if (getPoints().contains(from)) {
            // A point cannot be moved to the same location.
            if (to.equals(from)) return;
            // A track cannot have two points in the same place
            if (points.contains(to)) return;

            // We want to get the point that is part of the track,
            // as the point "from" may actually be another point
            // (from another track/intersection) with the same location.
            points.remove(points.indexOf(from));
            points.add(new Point(to));
        }

        // If there was an intersection at the point where we moved from, break the connection
        Intersection existingIntersection = getIntersection(from);
        if (existingIntersection != null) {
            existingIntersection.removeTrack(this);
            removeIntersection(existingIntersection);
        }

        // Update the valid tracks as the angle between tracks may have changed
        // Only doing this for the intersection which was at the end of the track
        // that was not moved, as if the track was moved as part of an intersection
        // it will update itself.
        Intersection intersectionAtStationaryPoint = getIntersection(getOtherPoint(to));
        if (intersectionAtStationaryPoint != null) {
            intersectionAtStationaryPoint.updateValidConnections();
        }
    }

    /**
     * Returns a point that two tracks share
     * @param other The track whose points will be compared to this one.
     * @return The point shared by the two tracks, if one exists.
     * If a point does not exist then null is returned.
     */
    public Point getCommonPoint(Track other) {
        for (Point point : points) {
            for (Point otherPoint : other.getPoints()) {
                if (point.equals(otherPoint)) return point;
            }
        }
        return null;
    }

    /**
     * Returns the list of all tracks that share an intersection with this one
     * @return The list of all connected next tracks.
     */
    public List<Track> getAllConnectedTracks() {
        List<Track> connectedTracks = new ArrayList<Track>();
        for (Point point : points) {
            connectedTracks.addAll(getAllConnectedTrackTowards(point));
        }
        return connectedTracks;
    }

    /**
     * Returns the tracks that are connected to this, towards the point given.
     * @param destination The point that the train is going towards. This point
     *               must be one of the track's points.
     * @return The list of tracks that the train is connected to towards the given point.
     */
    public List<Track> getAllConnectedTrackTowards(Point destination) {
        // destination is the point that we are travelling to.
        if (intersections.isEmpty()) return new ArrayList<Track>();

        // Look for the track that has a point that equals the destination.
        Intersection intersection = getIntersection(destination);
        if (intersection == null) return new ArrayList<Track>();

        List<Track> connectedTracks = new ArrayList<Track>(intersection.getTracks());
        connectedTracks.remove(this);

        return connectedTracks;
    }

    /**
     * Returns the tracks that are connected to this, coming from the point given.
     * @param origin The point that the train is coming from. This point
     *               must be one of the track's points.
     * @return The list of tracks that the train is connected to coming from the given point.
     */
    public List<Track> getAllConnectedTrackComingFrom(Point origin) {
        return getAllConnectedTrackTowards(getOtherPoint(origin));
    }

    /**
     * Returns the list of tracks that can be traversed from this track.
     * Note that this is not necessarily the same as getAllConnectedTracks,
     * as the intersection may not have connected two tracks (even though
     * they can be)
     * @return The list of tracks that can be traversed from this track.
     */
    public List<Track> getValidConnections() {
        List<Track> validNextTracks = new ArrayList<Track>();
        for (Point point : points) {
            validNextTracks.addAll(getValidConnectionsTowards(point));
        }
        return validNextTracks;
    }

    /**
     * Returns the possible tracks that a train may move on towards,
     * after it has completed this one.
     * @param from The point that the train is coming from.
     *             This point must be one of the track's points.
     * @return The list of tracks that can be traversed from a particular
     * point on the track.
     */
    public List<Track> getValidConnectionsComingFrom(Point from) {
        return getValidConnectionsTowards(getOtherPoint(from));
    }

    /**
     * Returns the possible tracks that a train may move on towards,
     * after it has completed this one.
     * @param towards The point that the train is going towards.
     *                This point must be one of the track's points.
     * @return The list of tracks that can be traversed from a particular
     * point on the track.
     */
    public List<Track> getValidConnectionsTowards(Point towards) {
        Intersection intersection = getIntersection(towards);
        if (intersection != null) {
            return getIntersection(towards).getValidConnections(this);
        } else {
            return new ArrayList<Track>();
        }
    }

    public Boolean isBroken() {
        return broken;
    }

    public void setBroken(Boolean broken) {
        this.broken = broken;
    }

    /**
     * @param o The object to be compared.
     * @return Two tracks are considered equal if they have the same points.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        // Hashset as we don't care about the order of the points
        if (points != null ? !new HashSet(points).equals(new HashSet(track.points)) : track.points != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return points != null ? points.hashCode() : 0;
    }
}
