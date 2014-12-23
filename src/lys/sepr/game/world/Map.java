package lys.sepr.game.world;

import java.util.ArrayList;
import java.util.List;

/**
 * The Map Class represents a collection of Tracks, Intersection and Locations,
 * that can interact with each other.
 */
public class Map {

    private ArrayList<Track> tracks = new ArrayList<Track>();
    private ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    private ArrayList<Location> locations = new ArrayList<Location>();

    public double getPointTrackThreshold() {
        return pointTrackThreshold;
    }

    // how close a point has to be to a track to be considered close/connected.
    private final double pointTrackThreshold = 10d;

    /**
     * Adds a location to the map.
     * @param location The location to be added to the map.
     */
    public void addLocation(Location location) {
        for (Location existingLocation : locations) {
            if (existingLocation.getPoint().equals(location.getPoint())) {
                return;
            }
        }
        locations.add(location);
    }

    /**
     * Removes a location from the map.
     * @param location The location to be removed from the map.
     */
    public void removeLocation(Location location) {
        locations.remove(location);
    }

    /**
     * Adds a track to the map.
     *
     * <p> If an intersection in the map shares a point with a point in the
     * added track, the track will be added to the intersection.
     *
     * <p> Otherwise, if a lone track in the map shares a point with a point
     * in the added track, an intersection will be created with both tracks.
     * @param track The track to be added to the map.
     */
    public void addTrack(Track track) {
        if (tracks.contains(track)) return;

        tracks.add(track);

        // If there is only one track there can be no intersections
        if (tracks.size() == 1) return;

        // Keeping track of the points of intersection tracks were added to
        // so that the track isn't added again to an intersection at this point
        ArrayList<Point> intersectionPoints = new ArrayList<Point>();

        // Look for another track that shares a point with this one
        for (Track existingTrack : tracks) {
            if (existingTrack.equals(track)) continue;

            Point commonPoint = track.getCommonPoint(existingTrack);
            if (commonPoint != null && !intersectionPoints.contains(commonPoint)){
                intersectionPoints.add(commonPoint);
                addTracksToIntersection(track, existingTrack, commonPoint);

                // a track can have a maximum of two intersections, one for each end
                if (intersectionPoints.size() == 2) return;
            }
        }
    }

    /**
     * Moves a point of the track from one position to another.
     *
     * <p>The track will be disconnected from the intersection at the point it
     * has been moved from if it exists.
     *
     * <p>Alternatively, a track has been moved as part of an intersection
     * (using the move intersection method), it will remain a part of that
     * intersection.
     *
     * <p>If an intersection in the map shares a point with a point in the
     * added track, the track will be added to the intersection.
     *
     * <p>Otherwise, if a lone track in the map shares a point with a point in
     * the added track, an intersection will be created with both tracks.
     *
     * @param track       The track to be moved
     * @param from        The point of a track to be moved.
     * @param destination Where the point of track will be moved to.
     */
    public void moveTrack(Track track, Point from, Point destination) {
        track.move(from, destination);
        // We don't want to do anymore work if the move is unsuccessful
        if (track.getPoints().contains(from)) return;

        // Look for another track that shares a point with this one
        for (Track existingTrack : tracks) {
            if (track.equals(existingTrack)) continue;
            Point commonPoint = track.getCommonPoint(existingTrack);
            if (commonPoint != null) {
                addTracksToIntersection(track, existingTrack, commonPoint);
                // We return because we don't want to add the track to the intersection again
                // As there will be at least two other tracks sharing the same point for
                // there to be an intersection
                return;
            }
        }
    }

    /**
     * Helper that will add a track to an intersection.
     *
     * <p>If an intersection in the map shares a point with a point in the
     * added track, the track will be added to the intersection.
     *
     * <p>Otherwise, an intersection will be created with both tracks.
     *
     * @param newTrack      The track that is added.
     * @param existingTrack An existing track in the map, that shares the same
     *                      point as the track to be added.
     * @param commonPoint   The point that the new and existing track have in
     *                      common.
     */
    private void addTracksToIntersection(Track newTrack, Track existingTrack, Point commonPoint) {
        // If there is an intersection where both tracks meet, add the new one to it,
        // or make a new intersection with both tracks
        Intersection intersectionAtCommonPoint = existingTrack.getIntersection(commonPoint);
        if (intersectionAtCommonPoint == null) {
            Intersection newIntersection = new Intersection(commonPoint, existingTrack, newTrack);
            intersections.add(newIntersection);
        } else {
            intersectionAtCommonPoint.addTrack(newTrack);
        }
    }

    /**
     * Moves an intersection of the track from one position to another.
     *
     * <p>If an intersection is moved to a point in the map where an
     * intersection exists, the two intersections will be merged into one.
     *
     * <p>If an intersection is moved to a point in the map where an endpoint
     * of a track exists, that track wil be added to the intersection.
     *
     * @param intersection The intersection to be moved.
     * @param to           Where the point of track will be moved to.
     */
    public void moveIntersection(Intersection intersection, Point to) {
        if (to.equals(intersection.getPoint())) return;

        for (Intersection otherIntersections : intersections) {
            if (otherIntersections.getPoint().equals(to)) {
                intersection.move(to);
                mergeIntersections(otherIntersections, intersection);
                return;
            }
        }
        // looking for unconnected tracks to consume.
        for (Track track : tracks) {
            if (track.getPoints().contains(to) &&
                    !intersection.getTracks().contains(track)){
                intersection.move(to);
                intersection.addTrack(track);
                return;
            }
        }
        intersection.move(to);
    }

    /**
     * Moves a location from one point to another.
     * @param location The location to be moved.
     * @param to       The point where the location will be moved to.
     */
    public void moveLocation(Location location, Point to) {
        location.getPoint().move(to.getX(), to.getY());
    }

    /**
     * Merges two intersections together.
     * After this process, only the master intersection will remain, having
     * taken all of the tracks that were a part of the slave intersection.
     * @param master The intersection that will absorb the tracks of the slave
     *               intersection.
     * @param slave  The intersection that will be dissolved.
     */
    private void mergeIntersections(Intersection master, Intersection slave) {
        for (Track slaveTrack : slave.getTracks()) {
            master.addTrack(slaveTrack);
        }
        slave.dissolve();
        intersections.remove(slave);
    }

    /**
     * Removes an intersection from the map.
     * @param intersection The intersection to be removed from the map and
     *                     dissolved.
     */
    public void removeIntersection(Intersection intersection) {
        intersection.dissolve();
        intersections.remove(intersection);
    }

    /**
     * Removes a track from the map.
     * @param track The track to be removed from the map.
     */
    public void removeTrack(Track track) {
        for (int i=track.getIntersections().size() - 1; i>=0; i--) {
            Intersection intersection = track.getIntersections().get(i);
            intersection.removeTrack(track);
            // As an intersection automatically dissolves after the second
            // last remaining track is removed, we check the intersection
            // is empty and remove it from the map if so
            if (intersection.getTracks().size() == 0) intersections.remove(intersection);
        }
        tracks.remove(track);
    }

    /**
     * Returns the tracks that are part of the map.
     * @return the list of tracks that are part of the map.
     */
    public ArrayList<Track> getTracks() {
        return tracks;
    }

    /**
     * Returns the intersections that are part of the map.
     * @return the list of intersections that are part of the map.
     */
    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }

    /**
     * Returns the locations that are part of the map.
     * @return the list of locations that are part of the map.
     */
    public ArrayList<Location> getLocations() {
        return locations;
    }

    /**
     * Returns valid routes from one location to another.
     * @param from The starting location of the route.
     * @param to   The finishing location of the route.
     * @return The list of the routes between the two locations.
     */
    public List<Route> getRoutes(Location from, Location to) {
        return getRoutes(from.getPoint(), to.getPoint());
    }

    /**
     * Returns valid routes from one point to another.
     * @param from The starting point of the route.
     * @param to   The finishing point of the route.
     * @return The list of the routes between the two points.
     */
    public List<Route> getRoutes(Point from, Point to) {
        return Route.getRoutes(from, to, this);
    }

    /**
     * Returns the fastest route from one location to another.
     * @param from The starting location of the route.
     * @param to   The finishing location of the route.
     * @return The fastest route between the two locations, if one exists.
     */
    public Route fastestRoute(Location from, Location to) {
        return fastestRoute(from.getPoint(), to.getPoint());
    }

    /**
     * Returns the fastest route from one point to another.
     * @param from The starting point of the route.
     * @param to   The finishing point of the route.
     * @return The fastest route between the two points, if one exists.
     */
    public Route fastestRoute(Point from, Point to) {
        List<Route> routes = getRoutes(from, to);
        if (routes.isEmpty()) return new Route(from, to);

        // Routes are sorted
        return routes.get(0);
    }

    /**
     * Splits a track into two.
     * @param track The track to be split.
     * @param where Where the track shall be split.
     */
    public void breakTrack(Track track, Point where) {
        // Here, the old track should be removed before the new tracks are added.
        // This is because otherwise the old track will form an intersection
        // with the new tracks, forming an intersection which will be broken
        // by the removal of the old track.
        // Dissolving an intersection moves all tracks in it slightly, so the
        // ends of the track will not be where they should be.
        if (track.getIntersections().isEmpty()) {
            removeTrack(track);
            for (Point existingPoint : track.getPoints()) {
                Track splitTrack = new Track(existingPoint, where);
                addTrack(splitTrack);
            }
        } else if (track.getIntersections().size() == 1) {
            Intersection intersection = track.getIntersections().get(0);
            Point pointOfIntersection = intersection.getPoint();
            Point otherPoint = track.getOtherPoint(pointOfIntersection);
            Track splitTrack1 = new Track(pointOfIntersection, where);
            Track splitTrack2 = new Track(otherPoint, where);
            addTrack(splitTrack1);
            removeTrack(track);
            addTrack(splitTrack2);
        } else {
            for (Point existingPoint : track.getPoints()) {
                Track splitTrack = new Track(existingPoint, where);
                addTrack(splitTrack);
            }
            removeTrack(track);
        }
        // Here, the old track should be removed after the new tracks are added.
        // This is because the track may have been part of an intersection
        // with only one other track, meaning that the connecting track will
        // have been moved slightly from it's original location as the
        // intersection dissolves, meaning the split track will no longer be
        // connected.
    }
}
