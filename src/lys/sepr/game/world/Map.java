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

        // Creating intersections for tracks with matching points
        int createdIntersections = 0;
        // Look for another track that shares a point with this one
        for (Track existingTrack : tracks) {
            if (existingTrack.equals(track)) continue;

            Point commonPoint = track.getCommonPoint(existingTrack);
            if (commonPoint != null){
                addTracksToIntersection(track, existingTrack, commonPoint);
                // a track can have a maximum of two intersections, one for each end
                createdIntersections++;
                if (createdIntersections == 2) return;
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
        for (Track track : tracks) {
            if (track.getPoints().contains(to)){
                intersection.move(to);
                intersection.addTrack(track);
                return;
            }
        }
        intersection.move(to);
    }

    private void mergeIntersections(Intersection master, Intersection slave) {
        for (Track slaveTrack : slave.getTracks()) {
            slaveTrack.removeIntersection(slave);
            master.addTrack(slaveTrack);
        }
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

    public ArrayList<Track> getRoute(Location from, Location to) {
        return getRoute(from.getPoint(), to.getPoint());
    }

    public ArrayList<Track> getRoute(Point from, Point to) {
        Track startingTrack = Utilities.closestTrack(from, tracks, 10);
        Track destinationTrack = Utilities.closestTrack(to, tracks, 10);

        // There is no track close enough that serves one of the points
        if (startingTrack == null || destinationTrack == null) {
            return null;
        }

        ArrayList<Track> trail = new ArrayList<Track>();
        trail.add(startingTrack);

        ArrayList<Track> route = getRoute(destinationTrack, trail);

        // If the route does not contain the destination track there is no route.
        if (route.contains(destinationTrack)) {
            return route;
        } else {
            return null;
        }
    }

    public ArrayList<Track> getRoute(Track to, ArrayList<Track> trail) {
        int trailSize = trail.size();
        Track lastTrackInTrail = trail.get(trailSize-1);
        if (lastTrackInTrail.equals(to)) return trail;
        // We have reached a dead end, as there are no more tracks connected we
        // haven't visited
        if (trail.containsAll(lastTrackInTrail.getConnectedTracks())) return trail;

        for (Track connectedTrack : lastTrackInTrail.getConnectedTracks()){
            if (trail.contains(connectedTrack)) continue;
                trail.add(connectedTrack);
                return getRoute(to, trail);
        }
        return trail;
    }
}
