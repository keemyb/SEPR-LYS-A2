package lys.sepr.game.world;

import java.util.ArrayList;

public class Intersection {

    private Point point;
    private ArrayList<Track> tracks = new ArrayList<Track>();

    Intersection(Point point, Track a, Track b) {
        this.point = point;

        tracks.add(a);
        tracks.add(b);

        a.addIntersection(this);
        b.addIntersection(this);

        a.addConnectedTrack(b);
        b.addConnectedTrack(a);
    }

    public Point getPoint() {
        return point;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    /* TODO Discriminate using angles to find tracks that cannot realistically be traversed.
    e.g a Y junction, you cannot move from one fork to another.
     */
    public ArrayList<Track> getValidNextTracks(Track track) {
        return tracks;
    }

}
