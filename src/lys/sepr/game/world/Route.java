package lys.sepr.game.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Route {
    private double length;
    private List<Track> tracks;

    public Point getFrom() {
        return from;
    }

    public Point getTo() {
        return to;
    }

    private Point from;
    private Point to;

    private Route(Point from, Point to, List<Track> tracks) {
        this.from = from;
        this.to = to;
        this.tracks = new ArrayList(tracks);
        updateLength();
    }

    public Route(Point from, Point to) {
        this.from = from;
        this.to = to;
        tracks = new ArrayList<Track>();
    }

    @Override
    public Route clone() {
        return new Route(from, to, tracks);
    }

    public int size() {
        return tracks.size();
    }

    private void updateLength() {
        this.length = Utilities.routeLength(tracks);
    }

    protected void addTrack(Track track) {
        tracks.add(track);
        updateLength();
    }

    private void removeTrack(Track track) {
        tracks.remove(track);
        updateLength();
    }

    public boolean contains(Track track) {
        return tracks.contains(track);
    }

    public boolean containsAll(Route route) {
        return containsAll(route.getTracks());
    }

    public boolean containsAll(List<Track> tracks) {
        //TODO Use stronger assertion that the list of tracks should be in the same order.
        return tracks.containsAll(tracks);
    }

    public Route reverse() {
        List<Track> reversedTracks = new ArrayList<Track>();

        for (int i=tracks.size() - 1; i >= 0; i--) {
            reversedTracks.add(tracks.get(i));
        }

        return new Route(to, from, reversedTracks);
    }

    public List<Track> getTracks() {
        return tracks;
    }

    /**
     * Returns valid routes from one point to another.
     * @param from The starting point of the route.
     * @param to   The finishing point of the route.
     * @return The list of the routes between the two points.
     */
    public static List<Route> getRoutes(Point from, Point to, Map map) {
        List<Track> startingTracks = Utilities.tracksWithinRange(from, map.getTracks(), map.getPointTrackThreshold());
        List<Track> destinationTracks = Utilities.tracksWithinRange(to, map.getTracks(), map.getPointTrackThreshold());

        ArrayList<Route> routes = new ArrayList<Route>();

        // There is no track close enough that serves one of the points
        if (startingTracks.isEmpty() || destinationTracks.isEmpty()) {
            return routes;
        }

        for (Track startingTrack : startingTracks) {
            for (Track destinationTrack : destinationTracks) {
                Route currentRoute = new Route(from, to);
                currentRoute.addTrack(startingTrack);
                if (startingTrack.equals(destinationTrack)){
                    if (!routes.contains(currentRoute)){
                        routes.add(currentRoute.clone());
                    }
                } else {
                    getRoutes(destinationTrack, currentRoute, new ArrayList<Track>(), routes);
                }
            }
        }

        // If a route (1) contains all tracks in another route (2), then (1) is
        // redundant. The reason why a redundant route is generated is because
        // there are multiple starting and destination tracks, and a pair of
        // starting or destination tracks that are connected may be selected as
        // the start/end of a route. So removing routes with extraneous tracks
        // means that all routes are unambiguous and as short as possible.
        List<Route> redundantRoutes = new ArrayList<Route>();
        for (Route route1 : routes) {
            for (Route route2 : routes) {
                if (route1 == route2) continue;
                if (route1.getTracks().containsAll(route2.getTracks())) {
                    redundantRoutes.add(route1);
                }
            }
        }

        routes.removeAll(redundantRoutes);

        Collections.sort(routes, new Comparator<Route>() {
            public int compare(Route route1, Route route2) {
                double route1Length = route1.length;
                double route2Length = route2.length;
                return Double.valueOf(route1Length).compareTo(route2Length);
            }
        });

        return routes;
    }

    /**
     * Returns valid routes from one track to another.
     * @param destination   The finishing point of the route.
     * @param currentRoute  The route traversed so far.
     * @param visitedTracks The tracks that have been visited so far.
     * @param routes        The list of valid routes found so far.
     * @return The list of the routes between the two points.
     */
    private static void getRoutes(Track destination,
                           Route currentRoute,
                           List<Track> visitedTracks,
                           List<Route> routes) {
        Track lastTrackInCurrentRoute = currentRoute.getTracks().get(currentRoute.size() - 1);

        // If we have backtracked to the first track , and we have visited all its connected tracks,
        // there are no more solutions.
        if (currentRoute.size() == 1 && visitedTracks.containsAll(lastTrackInCurrentRoute.getValidConnections())) return;

        ArrayList<Track> validNextTracks;
        if (currentRoute.size() == 1) {
            validNextTracks = lastTrackInCurrentRoute.getValidConnections();
        } else {
            // We have come from the point that the last two tracks meet
            Point comingFrom = lastTrackInCurrentRoute.getCommonPoint(currentRoute.getTracks().get(currentRoute.size() - 2));
            Point goingTowards = lastTrackInCurrentRoute.getOtherPoint(comingFrom);
            validNextTracks = lastTrackInCurrentRoute.getValidConnections(goingTowards);
        }

        // Discard all visited tracks
        validNextTracks.removeAll(visitedTracks);

        // Recurse over all non visited valid tracks
        for (Track nextTrack : validNextTracks){
            currentRoute.addTrack(nextTrack);
            // If the last track visited is our destination, add the route
            // and keep looking for more routes
            if (nextTrack == destination) {
                // Cloning the route as we don't want it to be mutated.
                routes.add(currentRoute.clone());
                currentRoute.removeTrack(destination);
            } else {
                visitedTracks.add(nextTrack);
                getRoutes(destination, currentRoute, visitedTracks, routes);
                currentRoute.removeTrack(nextTrack);
            }
        }
    }

    /**
     * Returns true if all of the tracks in the route are not broken.
     * @return true if all of the tracks in the route are unbroken, false otherwise.
     */
    public boolean isTraversable() {
        for (Track track : tracks) {
            if (track.isBroken()) return false;
        }
        return true;
    }

    /**
     * Returns true if all of the tracks in the route after (and including) the given track are not broken.
     * Returns false if the given track is not in the route.
     * @return true if all of the tracks in the route after the given track are unbroken, false otherwise.
     */
    public boolean isTraversable(Track track) {
        if (tracks.contains(track)) {
            for (int i=tracks.indexOf(track); i<tracks.size(); i++) {
                if (tracks.get(i).isBroken()) return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        if (from != null ? !from.equals(route.from) : route.from != null) return false;
        if (to != null ? !to.equals(route.to) : route.to != null) return false;
        if (tracks != null ? !tracks.equals(route.tracks) : route.tracks != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tracks != null ? tracks.hashCode() : 0;
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}
