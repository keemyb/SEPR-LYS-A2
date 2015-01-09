package lys.sepr.game.world;

import java.util.ArrayList;
import java.util.HashSet;

import static lys.sepr.game.world.Utilities.closestPoint;
import static lys.sepr.game.world.Utilities.getVector;

/**
 * The Track class represents a straight railway track between two points.
 * A track is bi-directional, there is no distinction between a "start" point
 * and an "end" point.
 */
public class Track {

    private static double nudgeStrength = 0.1;
    private ArrayList<Point> points = new ArrayList<Point>();
    private ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    private ArrayList<Track> activeNextTracks = new ArrayList<Track>();
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
     * Returns the track that a train will move on towards, after it has
     * completed this one.
     * @param origin The point that a train has come from. This point
     *               must be one of the track's points.
     * @return The track that the train will move on towards.
     */
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

    /**
     * Sets the track that a train will move to after this track has been completed.
     * The next track will only be set if it is a valid next track, that is if it
     * can be traversed from this track. This is to prevent unfeasible situations
     * such as a train being able to complete sharp, hairpin angles.
     * @param intersection The intersection where the routing will be changed.
     * @param prospectiveNextTrack The track that will be set as the next track,
     *                             if it is valid.
     */
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
        // that was not moved, as the other intersection will update itself (if it
        // exists).
        Intersection intersectionAtStationaryPoint = getIntersection(getOtherPoint(to));
        if (intersectionAtStationaryPoint != null) {
            intersectionAtStationaryPoint.updateValidTracks();
        }
    }

    /**
     * Moves a point of the track away from a specified point.
     * This method is called when a track has been removed from an intersection
     * to move it away from the intersection, so that it is not later mistaken
     * as being part of that intersection.
     * @param awayFrom the point which the track should be moved away from.
     *                 Only the point of the track closest to the point is
     *                 moved away.
     */
    public void nudge(Point awayFrom) {
        // only move the point closest to the point we want to move away from
        Point closestPoint = closestPoint(awayFrom, points);
        ArrayList<Double> vector = getVector(closestPoint, getOtherPoint(closestPoint));
        for (int i=0; i < vector.size(); i++) {
            vector.set(i, vector.get(i) * nudgeStrength);
        }
        closestPoint.translate(vector.get(0), vector.get(1));
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
     * Returns a list of tracks that this track is connected to.
     */
    public ArrayList<Track> getConnectedTracks() {
        ArrayList<Track> connectedTracks = new ArrayList<Track>();
        for (Intersection intersection : intersections) {
            for (Track track : intersection.getTracks()) {
                if (track.equals(this)) {
                    continue;
                } else if (!connectedTracks.contains(track)){
                    connectedTracks.add(track);
                }
            }
        }
        return connectedTracks;
    }

    /**
     * Returns the list of tracks that a train will travel from after it has
     * completed this one.
     * The returned list will contain a maximum of two tracks, one for each
     * end of the track.
     * @return The list of active next tracks.
     */
    public ArrayList<Track> getActiveNextTracks() {
        return activeNextTracks;
    }

    /**
     * Returns the list of tracks that can be traversed from this track.
     * Note that this is not necessarily the same as getConnectedTracks,
     * as it may not be possible for a train to move from one track to another,
     * depending on the angle between them.
     * @return The list of tracks that can be traversed from this track.
     */
    public ArrayList<Track> getValidNextTracks() {
        ArrayList<Track> validNextTracks = new ArrayList<Track>();
        for (Point point : points) {
            validNextTracks.addAll(getValidNextTracks(point));
        }
        return validNextTracks;
    }

    /**
     * Returns the list of tracks that can be traversed from a particular
     * point on the track.
     * @param towards The point that the next tracks will be reached from.
     * @return The list of tracks that can be traversed from a particular
     * point on the track.
     */
    public ArrayList<Track> getValidNextTracks(Point towards) {
        Intersection intersection = getIntersection(towards);
        if (intersection != null) {
            return getIntersection(towards).getValidNextTracks(this);
        } else {
            return new ArrayList<Track>();
        }
    }

    /**
     * Removes a track from the list of Active Next Tracks.
     * This method probably should not be called directly, as removing the next
     * track while one still (visually) exists could create problems.
     * @param track
     */
    public void removeActiveNextTrack(Track track) {
        activeNextTracks.remove(track);
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
