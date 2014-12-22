package lys.sepr.game.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Route {
    private double length;
    private List<Track> tracks;
    private Point from;
    private Point to;

    private Route(Point from, Point to, List<Track> tracks) {
        this.from = from;
        this.to = to;
        this.tracks = tracks;
        updateLength();
    }

    private Route(Point from, Point to) {
        this.from = from;
        this.to = to;
    }

    public Route clone() {
        return new Route(from, to, tracks);
    }

    public int size() {
        return tracks.size();
    }

    private void updateLength() {
        this.length = Utilities.routeLength(tracks);
    }

    private void addTrack(Track track) {
        tracks.add(track);
        updateLength();
    }

    private void removeTrack(Track track) {
        tracks.remove(track);
        updateLength();
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
                        routes.add(currentRoute);
                    }
                } else {
                    getRoutes(destinationTrack, currentRoute, new ArrayList<Track>(), routes);
                }
            }
        }

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
        // If we have backtracked to the first track , and we have visited all its connected tracks,
        // there are no more solutions.
        if (currentRoute.size() == 1 && visitedTracks.contains(currentRoute.getTracks().get(0).getValidNextTracks())) return;

        Track lastTrackInCurrentRoute = currentRoute.getTracks().get(currentRoute.size() - 1);

        ArrayList<Track> validNextTracks;
        if (currentRoute.size() == 1) {
            validNextTracks = lastTrackInCurrentRoute.getValidNextTracks();
        } else {
            // We have come from the point that the last two tracks meet
            Point comingFrom = lastTrackInCurrentRoute.getCommonPoint(currentRoute.getTracks().get(currentRoute.size() - 2));
            Point goingTowards = lastTrackInCurrentRoute.getOtherPoint(comingFrom);
            validNextTracks = lastTrackInCurrentRoute.getValidNextTracks(goingTowards);
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

    public boolean isTraversable() {
        for (Track track : tracks) {
            if (track.isBroken()) return false;
        }
        return true;
    }
}
