package lys.sepr.game.world;

import java.util.ArrayList;

public class Map {

    private ArrayList<Track> tracks = new ArrayList<Track>();
    private ArrayList<Intersection> intersections = new ArrayList<Intersection>();

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

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }
}
