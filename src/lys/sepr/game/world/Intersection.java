package lys.sepr.game.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static lys.sepr.game.world.Utilities.crossProduct;
import static lys.sepr.game.world.Utilities.getVector;

/**
 * The Intersection Class governs the relations between two or more connected
 * tracks.
 */
public class Intersection {

    // Tracks must be have at least this angle between them for trains to be able to move from one to another
    private static int minAngle = 100;
    private Point point;
    private ArrayList<Track> tracks = new ArrayList<Track>();
    private HashMap<Track,ArrayList<Track>> validConnections = new HashMap<Track,ArrayList<Track>>();
    private List<Track> activeConnection = new ArrayList<Track>();

    /**
     * Constructor
     * @param point The point where the intersection lies.
     * @param a One track that will be part of this intersection.
     * @param b Another track that will be part of this intersection.
     */
    Intersection(Point point, Track a, Track b) {
        this.point = new Point(point);

        tracks.add(a);
        tracks.add(b);

        a.addIntersection(this);
        b.addIntersection(this);

        updateValidConnections();
    }

    /**
     * Adds a track to this intersection.
     * @param track The track to be added to the intersection.
     */
    public void addTrack(Track track) {
        tracks.add(track);
        track.addIntersection(this);

        updateValidConnections();
    }

    /**
     * Adds a list of track to this intersection.
     * @param tracks The list of tracks to be added to the intersection.
     */
    public void addTracks(List<Track> tracks) {
        for (Track track : tracks) {
            this.tracks.add(track);
            track.addIntersection(this);
        }
        // Not simply calling singular addTrack method over and over as
        // We don't want to update the connections until all tracks have been added.
        updateValidConnections();
    }

    /**
     * Updates the valid connections for each track in this intersection.
     * It should be called when a track in the intersection, or the intersection
     * itself is modified.
     * It is necessary to update the the validConnections HashMap as the angles
     * between tracks may have changed.
     */
    public void updateValidConnections() {
        /* Clear existing valid tracks as we will be regenerating them.
        As the angles between tracks change when an intersection is moved,
        we must recalculate them to see if they are still valid.
        */
        validConnections.clear();
        for (Track track1 : tracks) {
            ArrayList<Track> validNextTracksList = new ArrayList<Track>();
            List<Double> vector1 = getVector(track1.getOtherPoint(point), point);
            for (Track track2 : tracks) {
                if (track1 == track2) continue;

                List<Double> vector2 = getVector(track2.getOtherPoint(point), point);
                double angle = crossProduct(vector1, vector2);

                if (validAngle(angle)) {
                    validNextTracksList.add(track2);
                }
            }
            validConnections.put(track1, validNextTracksList);
        }
        updateActiveConnection();
    }

    /**
     * Sets the active next tracks for all tracks that are a part of this
     * intersection.
     * If a track has an active next track that is valid, it is left untouched,
     * otherwise it is set to an arbitrary valid next track if one exists.
     */
    private void updateActiveConnection() {
        if (!activeConnection.isEmpty()) {
            Track activeConnectionTrack1 = activeConnection.get(0);
            Track activeConnectionTrack2 = activeConnection.get(1);

            // If the current activeConnection is valid leave it, else make a new one.
            // We need to check that the validConnections key still exists as it may not
            // exist in the case that a track is broken.
            List<Track> validConnections = getValidConnections(activeConnectionTrack1);
            if (validConnections != null && validConnections.contains(activeConnectionTrack2)) return;
        }

        for (Track track : tracks) {
            List<Track> validNextTracks = getValidConnections(track);
            if (validNextTracks.isEmpty()) continue;
            setActiveConnection(track, validNextTracks.get(0));
            return;
        }

        clearActiveConnection();
    }

    public void setActiveConnection(Track track1, Track track2) {
        if (!getTracks().contains(track1)) return;
        if (!getTracks().contains(track2)) return;

        if (validConnections.get(track1) != null && validConnections.get(track1).contains(track2)) {
            clearActiveConnection();
            activeConnection.add(track1);
            activeConnection.add(track2);
        }
    }

    private void clearActiveConnection() {
        activeConnection.clear();
    }

    /**
     * Returns True or False depending on the angle provided.
     * @param angle The angle to be checked.
     * @return True if the angle is valid, otherwise False.
     */
    private boolean validAngle(double angle) {
        return angle >= minAngle;
    }

    /**
     * Returns the point where the intersection lies.
     * @return the point where the intersection lies.
     */
    public Point getPoint() {
        return point;
    }

    /**
     * Returns the tracks that are part of the intersection.
     * @return the list of tracks that are part of the intersection.
     */
    public ArrayList<Track> getTracks() {
        return tracks;
    }

    /**
     * Returns the mapping of valid connections for all tracks in the intersection.
     * @return the HashMap of valid connections.
     */
    public ArrayList<Track> getValidConnections(Track track) {
        return validConnections.get(track);
    }

    /**
     * Returns the tracks in the active connection.
     * @return the active connection.
     */
    public List<Track> getActiveConnection() {
        return new ArrayList<Track>(activeConnection);
    }

    /**
     * Removes a track from the intersection.
     * The intersection will be dissolved when only one track remains after a
     * track has been removed.
     * @param track The track to be removed from the intersection.
     */
    public void removeTrack(Track track) {
        track.removeIntersection(this);
        tracks.remove(track);

        if (tracks.size() == 1) {
            /* remove this intersection from the remaining track, since an intersection
            cannot consist of one track */
            Track remainingTrack = tracks.get(0);
            remainingTrack.removeIntersection(this);
            tracks.remove(remainingTrack);
        } else {
            updateValidConnections();
        }
    }

    /**
     * Removes all tracks from the intersection.
     */
    public void dissolve() {
        for (int i=getTracks().size() - 1; i>=0; i--) {
            Track track = getTracks().get(i);
            removeTrack(track);
            // Break after the second last track is removed,
            // as the intersection will remove the last one in the remove track method
            if (i == 1) break;
        }
    }

    /**
     * Moves the intersection from one point to another.
     * All tracks in the intersection will be moved also (the point that the
     * track and intersection have in common).
     * @param to The point that the intersection will be moved to.
     */
    public void move(Point to) {
        if (getPoint().equals(to)) return;

        // We don't want to move an intersection to a point such that a track
        // in the intersection "collapses" (has both points in the same place).
        for (Track track : tracks) {
            Point trackOtherPoint = track.getOtherPoint(getPoint());
            if (trackOtherPoint.equals(to)) return;
        }

        for (int i=0; i < tracks.size(); i++) {
            Track track = tracks.get(i);
            track.move(getPoint(), to);
        }
        point = to;
        updateValidConnections();
    }
}
