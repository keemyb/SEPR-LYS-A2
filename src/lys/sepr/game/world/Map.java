package lys.sepr.game.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Map Class represents a collection of Tracks, Intersection and Locations,
 * that can interact with each other.
 */
public class Map {

    private ArrayList<Track> tracks = new ArrayList<Track>();
    private ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    private ArrayList<Location> locations = new ArrayList<Location>();
    private HashMap<RouteKey, List<Route>> possibleRoutes = new HashMap<RouteKey, List<Route>>();

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
        updatePossibleRoutes();
    }

    /**
     * Removes a location from the map.
     * @param location The location to be removed from the map.
     */
    public void removeLocation(Location location) {
        locations.remove(location);
        updatePossibleRoutes();
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
        updatePossibleRoutes();
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
        updatePossibleRoutes();
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
        updatePossibleRoutes();
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

        for (Intersection otherIntersection : intersections) {
            if (otherIntersection == intersection) continue;
            if (!otherIntersection.getPoint().equals(to)) continue;
            for (Track track1 : intersection.getTracks()) {
                for (Track track2 : otherIntersection.getTracks()) {
                    if (track1 == track2) {
                        // if a track is in two intersections, one of which will be moved
                        // to the other, we need to do something because a track cannot
                        // have two points in the same place.
                        handleCollapsedTrack(track1, otherIntersection, intersection);
                        return;
                    }
                }
            }
        }

        for (Intersection otherIntersections : intersections) {
            if (otherIntersections.getPoint().equals(to)) {
                intersection.move(to);
                mergeIntersections(otherIntersections, intersection);
                updatePossibleRoutes();
                return;
            }
        }
        // looking for unconnected tracks to consume.
        for (Track track : tracks) {
            if (track.getPoints().contains(to) &&
                    !intersection.getTracks().contains(track)){
                intersection.move(to);
                intersection.addTrack(track);
                updatePossibleRoutes();
                return;
            }
        }
        intersection.move(to);
        updatePossibleRoutes();
    }

    private void handleCollapsedTrack(Track track, Intersection master,
                                      Intersection slave) {
        // The master intersection will not be moved.
        /* The reason why we cannot simply merge intersection in some of the below cases
        is because tracks cannot be moved to a position where both points are in the same
        place, for safety.
         */

        int masterSize = master.getTracks().size();
        int slaveSize = slave.getTracks().size();

        if (masterSize >= 3 && slaveSize >= 3) {
            // if both intersections have at least three tracks, they are both "stable"
            // and will not be destroyed when one track is removed from it
            removeTrack(track);
            mergeIntersections(master, slave);
        } else if (masterSize >= 3 && slaveSize == 2) {
            // the master will remain after the removal, the slave will not,
            // so we move the remaining slave track into the remaining intersection.
            Track remainingTrack = slave.getTracks().get(0) == track ?
                    slave.getTracks().get(1) : slave.getTracks().get(0);
            Point pointToMove = slave.getPoint();
            Point pointToMoveTo = master.getPoint();
            removeTrack(track);
            moveTrack(remainingTrack, pointToMove, pointToMoveTo);
        } else if (masterSize == 2 && slaveSize >= 3) {
            // the slave will remain after the removal, the master will not,
            // so we move the slave intersection to the point of the remaining track.
            removeTrack(track);
            moveIntersection(slave, master.getPoint());
        } else if (masterSize == 2 && slaveSize == 2) {
            // neither intersection will survive
            Track remainingSlaveTrack = slave.getTracks().get(0) == track ?
                    slave.getTracks().get(1) : slave.getTracks().get(0);
            Point pointToMove = slave.getPoint();
            Point pointToMoveTo = master.getPoint();
            removeTrack(track);
            moveTrack(remainingSlaveTrack, pointToMove, pointToMoveTo);
        }

        updatePossibleRoutes();
    }

    /**
     * Moves a location from one point to another.
     * @param location The location to be moved.
     * @param to       The point where the location will be moved to.
     */
    public void moveLocation(Location location, Point to) {
        location.getPoint().move(to.getX(), to.getY());
        updatePossibleRoutes();
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
        slave.move(master.getPoint());
        List<Track> slaveTracks = new ArrayList<Track>(slave.getTracks());
        slave.dissolve();
        master.addTracks(slaveTracks);
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
        updatePossibleRoutes();
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
        updatePossibleRoutes();
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
        RouteKey routeKey = new RouteKey(from, to);
        if (possibleRoutes.isEmpty()) updatePossibleRoutes();
        return possibleRoutes.get(routeKey);
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

        for (Route route : routes) {
            if (route.isTraversable()) return route;
        }

        return new Route(from, to);
    }

    /**
     * Splits a track into two.
     * @param track The track to be split.
     * @param where Where the track shall be split.
     */
    public void breakTrack(Track track, Point where) {
        for (Point point : track.getPoints()) {
            Track splitTrack = new Track(point, where);
            addTrack(splitTrack);
        }
        removeTrack(track);
        updatePossibleRoutes();
    }

    private void updatePossibleRoutes() {
        possibleRoutes = new HashMap<RouteKey, List<Route>>();

        for (Location locationOne : locations) {
            for (Location locationTwo : locations) {
                if (locationOne == locationTwo) continue;

                Point pointOne = locationOne.getPoint();
                Point pointTwo = locationTwo.getPoint();

                // Using funky routeKey class to get around the fact that
                // I cannot use a simple list of points added in a reverse
                // order for equality.
                RouteKey routeKey = new RouteKey(pointOne, pointTwo);

                // We don't want to regenerate the route if its reverse already
                // exists, we can simply reverse every route found.
                if (possibleRoutes.containsKey(routeKey.reverse())) {
                    List<Route> reversedRoutes = new ArrayList<Route>();
                    for (Route route : possibleRoutes.get(routeKey.reverse())) {
                        reversedRoutes.add(route.reverse());
                    }
                    possibleRoutes.put(routeKey, reversedRoutes);
                } else {
                    possibleRoutes.put(routeKey, Route.getRoutes(pointOne, pointTwo, this));
                }
            }
        }
    }

    public HashMap<RouteKey, List<Route>> getPossibleRoutes() {
        if (possibleRoutes.isEmpty()) updatePossibleRoutes();
        return possibleRoutes;
    }

    public int numberOfPossibleRoutes() {
        // Only concerned about the different locations that can
        // navigated from one another, not the actual amount of
        // possible routes from A to B.
        return getPossibleRoutes().size();
    }

    public class RouteKey {
        private Point from;
        private Point to;

        RouteKey(Point from, Point to) {
            this.from = from;
            this.to = to;
        }

        RouteKey reverse() {
            return new RouteKey(to, from);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RouteKey routeKey = (RouteKey) o;

            if (from != null ? !from.equals(routeKey.from) : routeKey.from != null) return false;
            if (to != null ? !to.equals(routeKey.to) : routeKey.to != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            return result;
        }
    }
}
