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
    public static final int RENAME_LOCATION_MODE = 9;

    private int mode = INSPECT_TRACK_MODE;

    private Track selectedTrack;
    private boolean startedNewTrack = false;

    private Point newTrackPoint1;
    private Point newTrackPoint2;

    private boolean holdingLocationTrackIntersection = false;
    private Intersection intersectionPickedUp;
    private Point trackPointPickedUp;
    private Point trackPointNotPickedUp;
    private Track trackPickedUp;
    private Location locationPickedUp;

    private boolean startedRouteInspect = false;
    private Location routeLocation1;
    private Location routeLocation2;

    private double zoom = 1d;
    private final double zoomLevels = 4;
    private final double zoomConstant = 1.5;
    private final double maxZoom = Math.pow(zoomConstant, zoomLevels - 1);
    private final double minZoom = Math.pow(zoomConstant, -(zoomLevels - 1));

    private boolean showLocationNames = true;
    private boolean showIntersections = false;

    private lys.sepr.game.world.Point clickPoint;

    public void reset() {
        selectedTrack = null;
        startedNewTrack = false;
        newTrackPoint1 = null;
        newTrackPoint2 = null;
        holdingLocationTrackIntersection = false;
        intersectionPickedUp = null;
        trackPointPickedUp = null;
        trackPointNotPickedUp = null;
        trackPickedUp = null;
        locationPickedUp = null;
        startedRouteInspect = false;
        routeLocation1 = null;
        routeLocation2 = null;
        zoom = 1d;
    }

    public Point getClickPoint() {
        return clickPoint;
    }

    public void setClickPoint(Point clickPoint) {
        this.clickPoint = clickPoint;
    }

    public boolean isShowingIntersections() {
        return showIntersections;
    }

    public void setShowIntersections(boolean showIntersections) {
        this.showIntersections = showIntersections;
    }

    public boolean isShowingLocationNames() {
        return showLocationNames;
    }

    public void setShowLocationNames(boolean showLocationNames) {
        this.showLocationNames = showLocationNames;
    }

    public void zoomIn() {
        if (zoom != maxZoom) {
            zoom *= zoomConstant;
        }
    }

    public void zoomOut() {
        if (zoom != minZoom) {
            zoom /= zoomConstant;
        }
    }

    public void resetZoom() {
        zoom = 1d;
    }

    public double getZoom() {
        return zoom;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Track getSelectedTrack() {
        return selectedTrack;
    }

    public void setSelectedTrack(Track selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    public boolean isStartedNewTrack() {
        return startedNewTrack;
    }

    public void setStartedNewTrack(boolean startedNewTrack) {
        this.startedNewTrack = startedNewTrack;
    }

    public Point getNewTrackPoint1() {
        return newTrackPoint1;
    }

    public void setNewTrackPoint1(Point newTrackPoint1) {
        this.newTrackPoint1 = newTrackPoint1;
    }

    public Point getNewTrackPoint2() {
        return newTrackPoint2;
    }

    public void setNewTrackPoint2(Point newTrackPoint2) {
        this.newTrackPoint2 = newTrackPoint2;
    }

    public boolean isHoldingLocationTrackIntersection() {
        return holdingLocationTrackIntersection;
    }

    public void setHoldingLocationTrackIntersection(boolean holdingLocationTrackIntersection) {
        this.holdingLocationTrackIntersection = holdingLocationTrackIntersection;
    }

    public Intersection getIntersectionPickedUp() {
        return intersectionPickedUp;
    }

    public void setIntersectionPickedUp(Intersection intersectionPickedUp) {
        this.intersectionPickedUp = intersectionPickedUp;
    }

    public Point getTrackPointPickedUp() {
        return trackPointPickedUp;
    }

    public void setTrackPointPickedUp(Point trackPointPickedUp) {
        this.trackPointPickedUp = trackPointPickedUp;
    }

    public Point getTrackPointNotPickedUp() {
        return trackPointNotPickedUp;
    }

    public void setTrackPointNotPickedUp(Point trackPointNotPickedUp) {
        this.trackPointNotPickedUp = trackPointNotPickedUp;
    }

    public Track getTrackPickedUp() {
        return trackPickedUp;
    }

    public void setTrackPickedUp(Track trackPickedUp) {
        this.trackPickedUp = trackPickedUp;
    }

    public Location getLocationPickedUp() {
        return locationPickedUp;
    }

    public void setLocationPickedUp(Location locationPickedUp) {
        this.locationPickedUp = locationPickedUp;
    }

    public boolean isStartedRouteInspect() {
        return startedRouteInspect;
    }

    public void setStartedRouteInspect(boolean startedRouteInspect) {
        this.startedRouteInspect = startedRouteInspect;
    }

    public Location getRouteLocation1() {
        return routeLocation1;
    }

    public void setRouteLocation1(Location routeLocation1) {
        this.routeLocation1 = routeLocation1;
    }

    public Location getRouteLocation2() {
        return routeLocation2;
    }

    public void setRouteLocation2(Location routeLocation2) {
        this.routeLocation2 = routeLocation2;
    }

    public int getMode() {
        return mode;
    }
}
