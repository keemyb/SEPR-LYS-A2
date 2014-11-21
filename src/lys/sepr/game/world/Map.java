package lys.sepr.game.world;

import java.util.ArrayList;

/*TODO create tests for adding more than 2 tracks with the same point (and implement)
will require changes to the intersection constructor
 */

public class Map {

    private ArrayList<Track> tracks = new ArrayList<Track>();

    public void addTrack(Track track) {
        tracks.add(track);

        // if there is only one track there can be no intersections
        if (tracks.size() == 1) return;

        // creating intersections for tracks with matching points
        int createdIntersections = 0;
        for (Track existingTrack : tracks) {
            // we don't want to make an intersection with ourself
            if (existingTrack.equals(track)) continue;
            for (Point existingPoint : existingTrack.getPoints()) {
                for (Point newPoint : track.getPoints()) {
                    if (newPoint.equals(existingPoint)) {
                        new Intersection(newPoint, track, existingTrack);
                        // a track can have a maximum of two intersections
                        createdIntersections++;
                        if (createdIntersections == 2) return;
                    }
                }
            }
        }
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

}
