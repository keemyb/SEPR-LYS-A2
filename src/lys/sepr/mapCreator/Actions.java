package lys.sepr.mapCreator;

import lys.sepr.game.world.*;
import lys.sepr.game.world.Point;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static lys.sepr.game.world.Utilities.*;

public final class Actions {

    public static void clearCreateNew(State state) {
        state.startedNewTrack = false;
        state.newTrackPoint1 = null;
        state.newTrackPoint2 = null;
    }

    public static void dropHeldLocationTrackIntersection(State state) {
        state.holdingLocationTrackIntersection = false;
        state.intersectionPickedUp = null;
        state.trackPointPickedUp = null;
        state.trackPickedUp = null;
        state.locationPickedUp = null;
    }

    public static void clearInspect(State state) {
        state.startedRouteInspect = false;
        state.routeLocation1 = null;
        state.routeLocation2 = null;
    }

    public static Intersection selectIntersection(Map map, Point clickPoint, Double minPickUpDistance) {
        for (Intersection intersection : map.getIntersections()) {
            if (distance(clickPoint, intersection.getPoint()) < minPickUpDistance) {
                return intersection;
            }
        }
        return null;
    }

    public static java.util.List<Object> selectCloseTrackEnd(Map map, Point clickPoint, Double minPickUpDistance) {
        for (Track track : map.getTracks()) {
            for (Point point : track.getPoints()) {
                if (distance(clickPoint, point) < minPickUpDistance) {
                    java.util.List<Object> list = new ArrayList<Object>();
                    list.add(track);
                    list.add(point);
                    return list;
                }
            }
        }
        return null;
    }

    public static Track selectTrack(Map map, Point clickPoint, Double minPickUpDistance) {
        return closestTrack(clickPoint, map.getTracks(), minPickUpDistance);
    }

    public static Location selectLocation(Map map, Point clickPoint, Double minPickUpDistance) {
        return closestLocation(clickPoint, map.getLocations(), minPickUpDistance);
    }

    public static void pickUpLocationIntersectionTrackEnd(Map map, Point clickPoint, Double minPickUpDistance, State state) {
        System.out.println("Pickup");
        // Priority = Location > Intersection > Tracks

        Location location = selectLocation(map, clickPoint, minPickUpDistance);
        if (location != null) {
            dropHeldLocationTrackIntersection(state);
            state.locationPickedUp = location;
            state.holdingLocationTrackIntersection = true;
            return;
        }

        Intersection intersection = selectIntersection(map, clickPoint, minPickUpDistance);
        if (intersection != null) {
            dropHeldLocationTrackIntersection(state);
            state.intersectionPickedUp = intersection;
            state.holdingLocationTrackIntersection = true;
            return;
        }

        ArrayList<Object> trackAndPoint = (ArrayList<Object>) selectCloseTrackEnd(map, clickPoint, minPickUpDistance);
        if (trackAndPoint != null) {
            dropHeldLocationTrackIntersection(state);
            state.trackPickedUp = (Track) trackAndPoint.get(0);
            state.trackPointPickedUp = (Point) trackAndPoint.get(1);
            state.holdingLocationTrackIntersection = true;
        }
    }

    public static void removeLocation(Map map, Point clickPoint, Double minPickUpDistance) {
        Location location = selectLocation(map, clickPoint, minPickUpDistance);
        if (location != null) {
            map.removeLocation(location);
        }
    }

    public static void removeTrack(Map map, Point clickPoint, Double minPickUpDistance) {
        Track track = selectTrack(map, clickPoint, minPickUpDistance);
        if (track != null) {
            map.removeTrack(track);
        }
    }

    public static void removeIntersection(Map map, Point clickPoint, Double minPickUpDistance) {
        Intersection intersection = selectIntersection(map, clickPoint, minPickUpDistance);
        if (intersection != null) {
            map.removeIntersection(intersection);
        }
    }

    public static void inspectTrack(Map map, Point clickPoint, Double minPickUpDistance, State state) {
        Track track = selectTrack(map, clickPoint, minPickUpDistance);
        if (track != null && track.equals(state.selectedTrack)) {
            state.selectedTrack = null;
        } else if (track != null) {
            state.selectedTrack = track;
        } else state.selectedTrack = null;
    }

    public static void inspectRoute(Map map, Point clickPoint, Double minPickUpDistance, State state) {
        Location location = selectLocation(map, clickPoint, minPickUpDistance);
        if (!state.startedRouteInspect) {
            // Clearing the second location so that only the first one is highlighted
            state.routeLocation2 = null;
            state.routeLocation1 = location;
        } else {
            state.routeLocation2 = location;
        }
        state.startedRouteInspect = !state.startedRouteInspect;
    }

    public static void moveLocationIntersectionTrackEnd(Map map, Point clickPoint, Double minPickUpDistance, State state) {
        System.out.println("Move");
        // Finding a close existing point for each point in the new track
        // so that we can change the new destination to match (and thus make an intersection).
        ArrayList<Object> trackAndPoint = (ArrayList<Object>) selectCloseTrackEnd(map, clickPoint, minPickUpDistance);
        if (trackAndPoint != null) {
            clickPoint = (Point) trackAndPoint.get(1);
        }
        if (state.locationPickedUp != null) {
            map.moveLocation(state.locationPickedUp, clickPoint);
        } else if (state.trackPickedUp != null && state.trackPointPickedUp != null) {
            map.moveTrack(state.trackPickedUp, state.trackPointPickedUp, clickPoint);
        } else {
            map.moveIntersection(state.intersectionPickedUp, clickPoint);
        }
        state.holdingLocationTrackIntersection = false;
    }

    public static void createLocation(Map map, Point clickPoint, Double minPickUpDistance) {
        System.out.println("Create Location");
        for (Location existingLocation : map.getLocations()) {
            if (distance(existingLocation.getPoint(), clickPoint) < minPickUpDistance) return;
        }
        Location location = new Location(clickPoint, "location");
        map.addLocation(location);
    }

    public static void createTrack(Map map, Point clickPoint, Double minPickUpDistance, State state) {
        System.out.println("Create Track");
        if (!state.startedNewTrack) {
            state.newTrackPoint1 = clickPoint;
        } else {
            state.newTrackPoint2 = clickPoint;
            Track track = new Track(state.newTrackPoint1, state.newTrackPoint2);
            // Finding a close existing point for each point in the new track
            // so that we can change the coordinates to match (and thus make an intersection).
            ArrayList<Object> trackAndPoint1 = (ArrayList<Object>) selectCloseTrackEnd(map, state.newTrackPoint1, minPickUpDistance);
            if (trackAndPoint1 != null) {
                Point closePoint = (Point) trackAndPoint1.get(1);
                track.move(state.newTrackPoint1, closePoint);
            }
            ArrayList<Object> trackAndPoint2 = (ArrayList<Object>) selectCloseTrackEnd(map, state.newTrackPoint2, minPickUpDistance);
            if (trackAndPoint2 != null) {
                Point closePoint = (Point) trackAndPoint2.get(1);
                track.move(state.newTrackPoint2, closePoint);
            }
            map.addTrack(track);
        }
        state.startedNewTrack = !state.startedNewTrack;
    }

    public static void breakTrack(Map map, Point clickPoint, Double minPickUpDistance) {
        System.out.println("Break Track");
        Track closestTrack = closestTrack(clickPoint, map.getTracks(), minPickUpDistance);
        if (closestTrack != null) {
            // Not Perfect, we should ideally get the closestpoint to the clickpoint that is on the line
            // Currently if the click is not on the line the track will move slightly when broken.
            map.breakTrack(closestTrack, clickPoint);
        }
    }

    public static void pickupOrMoveLocationTrackIntersection(Map map, Point clickPoint, Double minPickUpDistance, State state) {
        if (state.holdingLocationTrackIntersection) {
            moveLocationIntersectionTrackEnd(map, clickPoint, minPickUpDistance, state);
        } else {
            pickUpLocationIntersectionTrackEnd(map, clickPoint, minPickUpDistance, state);
        }
    }

    public static void drawMap(Map map, State state, MapView mapView, Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5));
        if (state.routeLocation2 != null) {
            drawRoute(map, state, mapView, g2);
        } else if (state.selectedTrack != null) {
            drawNextTracks(map, state, mapView, g2);
        } else {
            drawNormal(map, state, mapView, g2);
        }
    }

    public static void drawRoute(Map map, State state, MapView mapView, Graphics2D g2) {
        java.awt.Color lineColour;
        java.awt.Color locationColour;
        ArrayList<Track> fastestRoute = map.fastestRoute(state.routeLocation1, state.routeLocation2);
        for (Track track : map.getTracks()) {
            Line2D.Double line = trackToLine2D(track, mapView.getMapPanel());
            if (fastestRoute.contains(track)) {
                lineColour = mapView.selectedTrackColour;
            } else lineColour = mapView.unconnectedTrackColour;

            g2.setColor(lineColour);
            g2.draw(line);
        }
        for (Location location : map.getLocations()) {
            if (location.equals(state.routeLocation1) || location.equals(state.routeLocation2)) {
                locationColour = mapView.selectedTrackColour;
            } else locationColour = mapView.unconnectedTrackColour;
            Rectangle2D.Double rectangle = locationToRect2D(location, 10d, mapView.getMapPanel());
            g2.setColor(locationColour);
            g2.draw(rectangle);
        }
    }

    public static void drawNextTracks(Map map, State state, MapView mapView, Graphics2D g2) {
        java.awt.Color lineColour;
        for (Track track : map.getTracks()) {
            Line2D.Double line = trackToLine2D(track, mapView.getMapPanel());
            if (track.equals(state.selectedTrack)) {
                lineColour = mapView.selectedTrackColour;
            } else if (state.selectedTrack.getActiveNextTracks().contains(track)) {
                lineColour = mapView.activeNextTrackColour;
            } else if (state.selectedTrack.getValidNextTracks().contains(track)) {
                lineColour = mapView.validNextTrackColour;
            } else if (state.selectedTrack.getConnectedTracks().contains(track)) {
                lineColour = mapView.connectedTrackColour;
            } else lineColour = mapView.unconnectedTrackColour;

            g2.setColor(lineColour);
            g2.draw(line);
        }
        for (Location location : map.getLocations()) {
            Rectangle2D.Double rectangle = locationToRect2D(location, 10d, mapView.getMapPanel());
            g2.setColor(randomColor());
            g2.draw(rectangle);
        }
    }

    public static void drawNormal(Map map, State state, MapView mapView, Graphics2D g2) {
        for (Track track : map.getTracks()) {
            Line2D.Double line = trackToLine2D(track, mapView.getMapPanel());
            g2.setColor(randomColor());
            g2.draw(line);
        }
        for (Location location : map.getLocations()) {
            Rectangle2D.Double rectangle = locationToRect2D(location, 10d, mapView.getMapPanel());
            if (state.routeLocation1 == location) {
                // highlight the first location selected when inspecting track
                g2.setColor(Color.GREEN);
            } else {
                g2.setColor(randomColor());
            }
            g2.draw(rectangle);
        }
    }
}
