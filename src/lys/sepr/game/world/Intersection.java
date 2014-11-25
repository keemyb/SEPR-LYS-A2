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
            ArrayList<Double> vector1 = getVector(track1.getOtherPoint(point), point);
            for (Track track2 : tracks) {
                if (track1 == track2) continue;

                ArrayList<Double> vector2 = getVector(track2.getOtherPoint(point), point);
                double angle = crossProduct(vector1, vector2);

                if (validAngle(angle)) {
                    if (validNextTracks.get(track1) == null) {
                        validNextTracks.put(track1, new ArrayList<Track>());
                    }
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
                    track.removeNextTrack(currentNextTrack);
                }
            }

            // Set the first valid next track, if there is one
            if (getValidNextTracks(track) != null) {
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

        // Move the track away from the intersection a little bit,
        // so that it is not confused as being part of it.
        track.nudge(getPoint());

        if (tracks.size() == 1) {
            /* remove this intersection from the remaining track, since an intersection
            cannot consist of one track */
            tracks.get(0).removeIntersection(this);
        } else {
            updateValidTracks();
        }
    }

    public void move(Point to) {
        if (getPoint().equals(to)) return;

        // Remove all tracks in the intersection, move them, and then add them back
        for (Track track : tracks) {
            track.removeIntersection(this);
            track.move(getPoint(), to);
        }
        for (Track track : tracks) {
            track.addIntersection(this);
        }
        point = to;
        updateValidTracks();
    }

}
