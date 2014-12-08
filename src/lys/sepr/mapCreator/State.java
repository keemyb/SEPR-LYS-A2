package lys.sepr.mapCreator;

import lys.sepr.game.world.Intersection;
import lys.sepr.game.world.Location;
import lys.sepr.game.world.Point;
import lys.sepr.game.world.Track;

public class State {

    public static final int INSPECT_TRACK_MODE = 0;
    public static final int MOVE_MODE = 1;
    public static final int DELETE_TRACK_MODE = 2;
    public static final int DELETE_INTERSECTION_MODE = 3;
    public static final int DELETE_LOCATION_MODE = 4;
    public static final int CREATE_TRACK_MODE = 5;
    public static final int CREATE_LOCATION_MODE = 6;
    public static final int INSPECT_ROUTE_MODE = 7;
    public static final int BREAK_TRACK_MODE = 8;

    public int mode = INSPECT_TRACK_MODE;

    public Track selectedTrack;
    public boolean startedNewTrack = false;

    public Point newTrackPoint1;
    public Point newTrackPoint2;

    public boolean holdingLocationTrackIntersection = false;
    public Intersection intersectionPickedUp;
    public Point trackPointPickedUp;
    public Track trackPickedUp;
    public Location locationPickedUp;

    public boolean startedRouteInspect = false;
    public Location routeLocation1;
    public Location routeLocation2;

    public void setMode(int mode) {
        this.mode = mode;
    }

}
