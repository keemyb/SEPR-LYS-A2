package lys.sepr.game.world;

import java.util.ArrayList;
import java.util.HashMap;

import static lys.sepr.game.world.Utilities.crossProduct;
import static lys.sepr.game.world.Utilities.getVector;

public class Intersection {

    // Tracks must be have at least this angle between them for trains to be able to move from one to another
    private int minAngle = 120;
    private Point point;
    private ArrayList<Track> tracks = new ArrayList<Track>();
    private HashMap<Track,ArrayList<Track>> validNextTracks = new HashMap<Track,ArrayList<Track>>();

    Intersection(Point point, Track a, Track b) {
        this.point = point;

        tracks.add(a);
        tracks.add(b);

        a.addIntersection(this);
        b.addIntersection(this);

        updateValidTracks();
    }

    public void addTrack(Track track) {
        tracks.add(track);
        track.addIntersection(this);

        updateValidTracks();
    }

    public void updateValidTracks() {
        /* Clear existing valid tracks as we will be regenerating them.
        As the angles between tracks change when an intersection is moved,
        we must recalculate them to see if they are still valid.
        */
        validNextTracks = new HashMap<Track,ArrayList<Track>>();
        for (Track track1 : tracks) {
            validNextTracks.put(track1, new ArrayList<Track>());
            ArrayList<Double> vector1 = getVector(track1.getOtherPoint(point), point);
            for (Track track2 : tracks) {
                if (track1 == track2) continue;

                ArrayList<Double> vector2 = getVector(track2.getOtherPoint(point), point);
                double angle = crossProduct(vector1, vector2);

                if (validAngle(angle)) {
                    validNextTracks.get(track1).add(track2);
                }
            }
        }
        setDefaultNextTracks();
    }

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

    private boolean validAngle(double angle) {
        return angle >= minAngle;
    }

    public Point getPoint() {
        return point;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public ArrayList<Track> getValidNextTracks(Track track) {
        return validNextTracks.get(track);
    }

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
        } else {
            updateValidTracks();
        }
    }

    public void dissolve() {
        for (int i=getTracks().size() - 1; i>=0; i--) {
            Track track = getTracks().get(i);
            removeTrack(track);
            // Break after the second last track is removed,
            // as the intersection will remove the last one in the remove track method
            if (i == 1) break;
        }
    }

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
