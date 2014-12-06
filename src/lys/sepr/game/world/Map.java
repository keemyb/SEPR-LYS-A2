package lys.sepr.game.world;

import java.util.ArrayList;

public class Map {

    private ArrayList<Track> tracks = new ArrayList<Track>();
    private ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    private ArrayList<Location> locations = new ArrayList<Location>();

    public void addLocation(Location location) {
        for (Location existingLocation : locations) {
            if (existingLocation.getPoint().equals(location.getPoint())) {
                return;
            }
        }
        locations.add(location);
    }

    public void removeLocation(Location location) {
        locations.remove(location);
    }

    public void addTrack(Track track) {
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

    public void moveIntersection(Intersection intersection, Point to) {
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

    public void moveLocation(Location location, Point to) {
        location.getPoint().move(to.getX(), to.getY());
    }

    private void mergeIntersections(Intersection master, Intersection slave) {
        for (Track slaveTrack : slave.getTracks()) {
            master.addTrack(slaveTrack);
        }
        slave.dissolve();
        intersections.remove(slave);
    }

    public void removeIntersection(Intersection intersection) {
        intersection.dissolve();
        intersections.remove(intersection);
    }

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

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public ArrayList<ArrayList<Track>> getRoutes(Location from, Location to) {
        return getRoutes(from.getPoint(), to.getPoint());
    }

    public ArrayList<ArrayList<Track>> getRoutes(Point from, Point to) {
        Track startingTrack = Utilities.closestTrack(from, tracks, 10);
        Track destinationTrack = Utilities.closestTrack(to, tracks, 10);

        ArrayList<ArrayList<Track>> routes = new ArrayList<ArrayList<Track>>();

        // There is no track close enough that serves one of the points
        if (startingTrack == null || destinationTrack == null) {
            return routes;
        } else if (startingTrack == destinationTrack){
            ArrayList<Track> route = new ArrayList<Track>();
            route.add(startingTrack);
            routes.add(route);
            return routes;
        }

        ArrayList<Track> currentRoute = new ArrayList<Track>();
        currentRoute.add(startingTrack);

        getRoutes(destinationTrack, currentRoute, new ArrayList<Track>(), routes);

        return routes;
    }

    private void getRoutes(Track destination,
                           ArrayList<Track> currentRoute,
                           ArrayList<Track> visitedTracks,
                           ArrayList<ArrayList<Track>> routes) {
        // If we have backtracked to the first track , and we have visited all its connected tracks,
        // there are no more solutions.
        if (currentRoute.size() == 1 && visitedTracks.contains(currentRoute.get(0).getValidNextTracks())) return;

        Track lastTrackInCurrentRoute = currentRoute.get(currentRoute.size() - 1);

        ArrayList<Track> validNextTracks;
        if (currentRoute.size() == 1) {
            validNextTracks = lastTrackInCurrentRoute.getValidNextTracks();
        } else {
            // We have come from the point that the last two tracks meet
            Point comingFrom = lastTrackInCurrentRoute.getCommonPoint(currentRoute.get(currentRoute.size()-2));
            Point goingTowards = lastTrackInCurrentRoute.getOtherPoint(comingFrom);
            validNextTracks = lastTrackInCurrentRoute.getValidNextTracks(goingTowards);
        }

        // Discard all visited tracks
        validNextTracks.removeAll(visitedTracks);

        // Recurse over all non visited valid tracks
        for (Track nextTrack : validNextTracks){
            currentRoute.add(nextTrack);
            // If the last track visited is our destination, add the route
            // and keep looking for more routes
            if (nextTrack == destination) {
                // Cloning the route as we don't want it to be mutated.
                routes.add((ArrayList<Track>) currentRoute.clone());
                currentRoute.remove(destination);
            } else {
                visitedTracks.add(nextTrack);
                getRoutes(destination, currentRoute, visitedTracks, routes);
                currentRoute.remove(nextTrack);
            }
        }
    }

    public ArrayList<Track> fastestRoute(Location from, Location to) {
        return fastestRoute(from.getPoint(), to.getPoint());
    }

    public ArrayList<Track> fastestRoute(Point from, Point to) {
        ArrayList<ArrayList<Track>> routes = getRoutes(from, to);
        if (routes.isEmpty()) return new ArrayList<Track>();

        ArrayList<Track> fastestRoute = null;
        Double shortestDistance = null;
        for (ArrayList<Track> route : routes) {
            Double distance = 0d;
            for (Track track : route) {
                distance += Utilities.length(track);
            }
            if (shortestDistance == null || distance < shortestDistance) {
                shortestDistance = distance;
                fastestRoute = route;
            }
        }
        return fastestRoute;
    }

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
