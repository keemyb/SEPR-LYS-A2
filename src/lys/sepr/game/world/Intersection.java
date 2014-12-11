package lys.sepr.game.world;

import java.util.ArrayList;
import java.util.HashMap;

import static lys.sepr.game.world.Utilities.crossProduct;
import static lys.sepr.game.world.Utilities.getVector;

/**
 * The Intersection Class governs the relations between two or more connected
 * tracks.
 */
public class Intersection {

    // Tracks must be have at least this angle between them for trains to be able to move from one to another
    private int minAngle = 120;
    private Point point;
    private ArrayList<Track> tracks = new ArrayList<Track>();
    private HashMap<Track,ArrayList<Track>> validNextTracks = new HashMap<Track,ArrayList<Track>>();

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

        updateValidTracks();
    }

    /**
     * Adds a track to this intersection.
     * @param track The track to be added to the intersection.
     */
    public void addTrack(Track track) {
        tracks.add(track);
        track.addIntersection(this);

        updateValidTracks();
    }

    /**
     * Updates the valid tracks of each track that is a part of this intersection.
     * It will be called when a track in the intersection, or the intersection
     * itself is moved.
     * It is necessary to update the the validNextTracks HashMap as the angles
     * between tracks may have changed.
     */
    public void updateValidTracks() {
        /* Clear existing valid tracks as we will be regenerating them.
        As the angles between tracks change when an intersection is moved,
        we must recalculate them to see if they are still valid.
        */
        validNextTracks = new HashMap<Track,ArrayList<Track>>();
        for (Track track1 : tracks) {
            ArrayList<Track> validNextTracksList = new ArrayList<Track>();
            ArrayList<Double> vector1 = getVector(track1.getOtherPoint(point), point);
            for (Track track2 : tracks) {
                if (track1 == track2) continue;

                ArrayList<Double> vector2 = getVector(track2.getOtherPoint(point), point);
                double angle = crossProduct(vector1, vector2);

                if (validAngle(angle)) {
                    validNextTracksList.add(track2);
                }
            }
            validNextTracks.put(track1, validNextTracksList);
        }
        setDefaultNextTracks();
    }

    /**
     * Sets the active next tracks for all tracks that are a part of this
     * intersection.
     * If a track has an active next track that is valid, it is left untouched,
     * otherwise it is set to an arbitrary valid next track if one exists.
     */
    private void setDefaultNextTracks() {
        for (Track track : tracks) {
            // We want the point that is not at this intersection so that we can find its current next track
            Point destination = track.getOtherPoint(getPoint());
            Track currentNextTrack = track.getNextTrack(destination);

            if (currentNextTrack != null) {
                // If there are valid next tracks and the current next track is one of them,
                // leave it, or else remove it
                if (validNextTracks.get(track) != null && validNextTracks.get(track).contains(currentNextTrack)){
                    continue;
                } else {
                    track.removeActiveNextTrack(currentNextTrack);
                }
            }

            // Set the first valid next track, if there is one
            if (!getValidNextTracks(track).isEmpty()) {
                track.setNextTrack(this, getValidNextTracks(track).get(0));
            }
        }
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
     * Returns the mapping of valid next tracks for all tracks in the intersection.
     * @return the HashMap of valid next tracks.
     */
    public ArrayList<Track> getValidNextTracks(Track track) {
        return validNextTracks.get(track);
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

        // Remove the removed track from all other active next tracks
        for (int i=0; i < tracks.size(); i++) {
            Track remainingTrack = tracks.get(i);
            track.removeActiveNextTrack(remainingTrack);
            remainingTrack.removeActiveNextTrack(track);
        }

        // Move the track away from the intersection a little bit,
        // so that it is not confused as being part of it.
        track.nudge(getPoint());

        if (tracks.size() == 1) {
            /* remove this intersection from the remaining track, since an intersection
            cannot consist of one track */
            Track remainingTrack = tracks.get(0);
            remainingTrack.removeIntersection(this);
            tracks.remove(remainingTrack);
            remainingTrack.nudge(getPoint());
        } else {
            updateValidTracks();
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

        // Remove the reference to this intersection of every track,
        // before moving them, and then add the reference back
        for (int i=0; i < tracks.size(); i++) {
            Track track = tracks.get(i);
            track.removeIntersection(this);
            track.move(getPoint(), to);
            track.addIntersection(this);
        }
        point = to;
        updateValidTracks();
    }
}
