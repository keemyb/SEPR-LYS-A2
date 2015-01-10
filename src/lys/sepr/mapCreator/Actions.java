package lys.sepr.mapCreator;

import com.thoughtworks.xstream.XStream;
import lys.sepr.game.world.*;
import lys.sepr.game.world.Point;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static lys.sepr.game.world.Utilities.*;

public final class Actions {

    public static Point screenPointToMapPoint(java.awt.Point clickPoint, State state) {
        double clickPointX = clickPoint.x;
        double clickPointY = clickPoint.y;
        clickPointX /= state.getZoom();
        clickPointY /= state.getZoom();
        return new Point(clickPointX, clickPointY);
    }

    public static java.awt.Point mapPointToScreenPoint(Point point, State state) {
        double pointX = point.getX();
        double pointY = point.getY();
        pointX *= state.getZoom();
        pointY *= state.getZoom();
        return new java.awt.Point((int) pointX, (int) pointY);
    }

    public static Line2D.Double trackToLine2D(Track track, State state) {
        java.awt.Point point1 = mapPointToScreenPoint(track.getPoints().get(0), state);
        java.awt.Point point2 = mapPointToScreenPoint(track.getPoints().get(1), state);
        return new Line2D.Double(point1, point2);
    }

    public static Rectangle2D.Double locationToRect2D(Location location, Double size, State state) {
        size *= state.getZoom();
        java.awt.Point point = mapPointToScreenPoint(location.getPoint(), state);
        return new Rectangle2D.Double(point.getX() - size / 2, point.getY() - size /2, size, size);
    }

    public static Ellipse2D.Double intersectionToEllipse2D(Intersection intersection, Double size, State state) {
        size *= state.getZoom();
        java.awt.Point point = mapPointToScreenPoint(intersection.getPoint(), state);
        return new Ellipse2D.Double(point.getX() - size / 2, point.getY() - size /2, size, size);
    }

    public static Color randomColor() {
        Random r = new Random();
        int rgb = Color.HSBtoRGB(r.nextFloat(),0.9f,1.0f);
        return new Color(rgb);
    }

    public static void clearCreateNew(State state) {
        state.setStartedNewTrack(false);
        state.setNewTrackPoint1(null);
        state.setNewTrackPoint2(null);
    }

    public static void dropHeldLocationTrackIntersection(State state) {
        state.setHoldingLocationTrackIntersection(false);
        state.setIntersectionPickedUp(null);
        state.setTrackPointPickedUp(null);
        state.setTrackPointNotPickedUp(null);
        state.setTrackPickedUp(null);
        state.setLocationPickedUp(null);
    }

    public static void clearInspect(State state) {
        state.setStartedRouteInspect(false);
        state.setRouteLocation1(null);
        state.setRouteLocation2(null);
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
            state.setLocationPickedUp(location);
            state.setHoldingLocationTrackIntersection(true);
            return;
        }

        Intersection intersection = selectIntersection(map, clickPoint, minPickUpDistance);
        if (intersection != null) {
            dropHeldLocationTrackIntersection(state);
            state.setIntersectionPickedUp(intersection);
            state.setHoldingLocationTrackIntersection(true);
            return;
        }

        ArrayList<Object> trackAndPoint = (ArrayList<Object>) selectCloseTrackEnd(map, clickPoint, minPickUpDistance);
        if (trackAndPoint != null) {
            Track trackPickedUp = (Track) trackAndPoint.get(0);
            Point pointPickedUp = (Point) trackAndPoint.get(1);
            dropHeldLocationTrackIntersection(state);
            state.setTrackPickedUp(trackPickedUp);
            state.setTrackPointPickedUp(pointPickedUp);
            state.setTrackPointNotPickedUp(trackPickedUp.getOtherPoint(pointPickedUp));
            state.setHoldingLocationTrackIntersection(true);
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
        if (track != null && track.equals(state.getSelectedTrack())) {
            state.setSelectedTrack(null);
        } else if (track != null) {
            state.setSelectedTrack(track);
        } else state.setSelectedTrack(null);
    }

    public static void inspectRoute(Map map, Point clickPoint, Double minPickUpDistance, State state) {
        Location location = selectLocation(map, clickPoint, minPickUpDistance);
        if (!state.isStartedRouteInspect()) {
            // Clearing the second location so that only the first one is highlighted
            state.setRouteLocation2(null);
            state.setRouteLocation1(location);
        } else {
            state.setRouteLocation2(location);
        }
        state.setStartedRouteInspect(!state.isStartedRouteInspect());
    }

    public static void moveLocationIntersectionTrackEnd(Map map, Point clickPoint, Double minPickUpDistance, State state) {
        System.out.println("Move");
        // Finding a close existing point for each point in the new track
        // so that we can change the new destination to match (and thus make an intersection).
        ArrayList<Object> trackAndPoint = (ArrayList<Object>) selectCloseTrackEnd(map, clickPoint, minPickUpDistance);
        if (trackAndPoint != null) {
            clickPoint = (Point) trackAndPoint.get(1);
        }
        if (state.getLocationPickedUp() != null) {
            map.moveLocation(state.getLocationPickedUp(), clickPoint);
        } else if (state.getTrackPickedUp() != null && state.getTrackPointPickedUp() != null) {
            map.moveTrack(state.getTrackPickedUp(), state.getTrackPointPickedUp(), clickPoint);
        } else {
            map.moveIntersection(state.getIntersectionPickedUp(), clickPoint);
        }
        Actions.dropHeldLocationTrackIntersection(state);
    }

    public static void createLocation(Map map, Point clickPoint, Double minPickUpDistance, MapView mapView) {
        System.out.println("Create Location");
        for (Location existingLocation : map.getLocations()) {
            if (distance(existingLocation.getPoint(), clickPoint) < minPickUpDistance) return;
        }
        Location location = new Location(clickPoint, "location " + (map.getLocations().size() + 1));
        nameLocation(location, mapView.getMapPanel());
        map.addLocation(location);
    }

    public static void createTrack(Map map, Point clickPoint, Double minPickUpDistance, State state) {
        System.out.println("Create Track");
        if (!state.isStartedNewTrack()) {
            state.setNewTrackPoint1(clickPoint);
        } else {
            state.setNewTrackPoint2(clickPoint);
            Track track = new Track(state.getNewTrackPoint1(), state.getNewTrackPoint2());
            // Finding a close existing point for each point in the new track
            // so that we can change the coordinates to match (and thus make an intersection).
            ArrayList<Object> trackAndPoint1 = (ArrayList<Object>) selectCloseTrackEnd(map, state.getNewTrackPoint1(), minPickUpDistance);
            if (trackAndPoint1 != null) {
                Point closePoint = (Point) trackAndPoint1.get(1);
                track.move(state.getNewTrackPoint1(), closePoint);
            }
            ArrayList<Object> trackAndPoint2 = (ArrayList<Object>) selectCloseTrackEnd(map, state.getNewTrackPoint2(), minPickUpDistance);
            if (trackAndPoint2 != null) {
                Point closePoint = (Point) trackAndPoint2.get(1);
                track.move(state.getNewTrackPoint2(), closePoint);
            }
            map.addTrack(track);
            state.setNewTrackPoint1(null);
            state.setNewTrackPoint2(null);
        }
        state.setStartedNewTrack(!state.isStartedNewTrack());
    }

    public static void breakTrack(Map map, Point clickPoint, Double minPickUpDistance) {
        System.out.println("Break Track");
        Track closestTrack = closestTrack(clickPoint, map.getTracks(), minPickUpDistance);
        Point closestPoint = closestPoint(clickPoint, closestTrack);
        if (closestTrack != null) {
            map.breakTrack(closestTrack, closestPoint);
        }
    }

    public static void pickupOrMoveLocationTrackIntersection(Map map, Point clickPoint, Double minPickUpDistance, State state) {
        if (state.isHoldingLocationTrackIntersection()) {
            moveLocationIntersectionTrackEnd(map, clickPoint, minPickUpDistance, state);
        } else {
            pickUpLocationIntersectionTrackEnd(map, clickPoint, minPickUpDistance, state);
        }
    }

    public static void drawMap(Map map, double locationSize, State state, MapView mapView, Graphics2D g2) {
        g2.setStroke(new BasicStroke(5));

        if (state.getRouteLocation2() != null) {
            drawRoute(map, state, locationSize, mapView, g2);
        } else if (state.getSelectedTrack() != null) {
            drawConnectedTracks(map, state, locationSize, mapView, g2);
        } else {
            drawNormal(map, state, locationSize, mapView, g2);
        }
        if (state.isShowingLocationNames()) {
            for (Location location : map.getLocations()) {
                drawLocationName(location, locationSize, state, g2);
            }
        }
        if (state.isShowingIntersections()) {
            for (Intersection intersection : map.getIntersections()) {
                drawIntersection(intersection, locationSize / 2, Color.RED, state, g2);
            }
        }
        if (state.getMode() == State.CREATE_TRACK_MODE) {
            drawTemporaryTrack(mapView.selectedTrackColour, state, mapView, g2);
        } else if (state.getMode() == State.CREATE_LOCATION_MODE) {
            drawTemporaryLocation(locationSize, mapView.selectedTrackColour, state, mapView, g2);
        }
    }

    public static void drawRoute(Map map, State state, double locationSize, MapView mapView, Graphics2D g2) {
        java.awt.Color lineColour;
        List<Route> routes = map.getRoutes(state.getRouteLocation1(), state.getRouteLocation2());

        // drawing the tracks not part of a route.
        lineColour = mapView.unconnectedTrackColour;
        for (Track track : map.getTracks()) {
            drawTrack(track, lineColour, state, mapView, g2);
        }

        float routesHue = 126f/360f;

        for (int i=routes.size()- 1; i>=0; i--) {
            // fastest route
            if (i == 0){
                lineColour = mapView.selectedTrackColour;
            } else {
                // The shorter the route, the more saturation it has.
                lineColour = new Color(Color.HSBtoRGB(routesHue,
                        (routes.size() - i) / (float) routes.size(), 0.9f));
            }

            Route route = routes.get(i);
            for (Track track : route.getTracks()) {
                drawTrack(track, lineColour, state, mapView, g2);
            }
        }

        for (Location location : map.getLocations()) {
            if (location.equals(state.getRouteLocation1()) || location.equals(state.getRouteLocation2())) {
                drawLocation(location, locationSize, mapView.selectedTrackColour, state, mapView, g2);
            } else {
                drawLocation(location, locationSize, mapView.unconnectedTrackColour, state, mapView, g2);
            }
        }
    }

    public static void drawConnectedTracks(Map map, State state, double locationSize, MapView mapView, Graphics2D g2) {
        java.awt.Color lineColour;
        for (Track track : map.getTracks()) {
            if (track.equals(state.getSelectedTrack())) {
                lineColour = mapView.selectedTrackColour;
            } else if (state.getSelectedTrack().getConnectedTracks().contains(track)) {
                lineColour = mapView.activeConnectedTrackColour;
            } else if (state.getSelectedTrack().getValidConnections().contains(track)) {
                lineColour = mapView.validConnectedTrackColour;
            } else if (state.getSelectedTrack().getConnectedTracks().contains(track)) {
                lineColour = mapView.connectedTrackColour;
            } else lineColour = mapView.unconnectedTrackColour;

            drawTrack(track, lineColour, state, mapView, g2);
        }
        for (Location location : map.getLocations()) {
            drawLocation(location, locationSize, mapView.normalTrackColour, state, mapView, g2);
        }
    }

    public static void drawNormal(Map map, State state, double locationSize, MapView mapView, Graphics2D g2) {
        for (Track track : map.getTracks()) {
            drawTrack(track, mapView.normalTrackColour, state, mapView, g2);
        }

        for (Location location : map.getLocations()) {
            if (state.getRouteLocation1() == location) {
                // highlight the first location selected when inspecting track
                drawLocation(location, locationSize, Color.GREEN, state, mapView, g2);
            } else {
                drawLocation(location, locationSize, mapView.normalTrackColour, state, mapView, g2);
            }
        }
    }

    public static void drawTrack(Track track, java.awt.Color color, State state, MapView mapView, Graphics2D g2) {
        Track pickedUpTrack = state.getTrackPickedUp();
        Intersection pickedUpIntersection = state.getIntersectionPickedUp();
        Track trackToDraw;

        if (isTrackTemporary(track, state)){
            g2.setColor(mapView.selectedTrackColour);
            if (pickedUpTrack != null) {
                trackToDraw = new Track(state.getTrackPointNotPickedUp(), state.getClickPoint());
            } else {
                trackToDraw = new Track(state.getClickPoint(),
                        track.getOtherPoint(pickedUpIntersection.getPoint()));
            }
        } else {
            g2.setColor(color);
            trackToDraw = track;
        }

        Line2D.Double line = trackToLine2D(trackToDraw, state);
        g2.draw(line);
    }

    public static void drawTemporaryTrack(java.awt.Color color, State state, MapView mapView, Graphics2D g2) {
        if (state.isStartedNewTrack()) {
            Track tempTrack = new Track(state.getNewTrackPoint1(), state.getClickPoint());
            drawTrack(tempTrack, color, state, mapView, g2);
        }
    }

    public static boolean isTrackTemporary(Track track, State state) {
        Track pickedUpTrack = state.getTrackPickedUp();
        Intersection pickedUpIntersection = state.getIntersectionPickedUp();
        if (state.getMode() == State.MOVE_MODE) {
            // If we are about to move a track or intersection we don't
            // want to draw these tracks just yet, we will draw them
            // according to the current mouse position.
            if (track == pickedUpTrack) {
                return true;
            } else if (pickedUpIntersection != null &&
                    pickedUpIntersection.getTracks().contains(track)) {
                return true;
            }
        }
        return false;
    }

    public static void drawLocation(Location location, double locationSize, java.awt.Color color, State state, MapView mapView, Graphics2D g2) {
        Location locationToDraw;

        if (state.getLocationPickedUp() == location) {
            g2.setColor(mapView.selectedTrackColour);
            locationToDraw = new Location(state.getClickPoint(), "New Location");
        } else {
            g2.setColor(color);
            locationToDraw = location;
        }

        Rectangle2D.Double rectangle = locationToRect2D(locationToDraw, locationSize, state);
        g2.draw(rectangle);
    }

    public static void drawTemporaryLocation(double locationSize, java.awt.Color color, State state, MapView mapView, Graphics2D g2) {
        Location tempLocation = new Location(state.getClickPoint(), "New Location");
        drawLocation(tempLocation, locationSize, color, state, mapView, g2);
    }

    public static void drawLocationName(Location location, double locationSize, State state, Graphics2D g2) {
        g2.setColor(Color.orange);
        java.awt.Point locationPoint = mapPointToScreenPoint(location.getPoint(), state);
        double offset = locationSize / 2;
        g2.drawString(location.getName(), (float) (locationPoint.getX() + locationSize),
                (float) (locationPoint.getY() + offset));
    }

    public static void drawIntersection(Intersection intersection, double intersectionSize, java.awt.Color color, State state, Graphics2D g2) {
        Ellipse2D.Double circle = intersectionToEllipse2D(intersection, intersectionSize, state);
        g2.setColor(color);
        g2.draw(circle);
    }

    public static void loadMapAndBackground(MapView mapView, JPanel jPanel) {
        XStream xstream = new XStream();

        JFileChooser chooser = new JFileChooser();
        FileFilter fileFilter = new FileNameExtensionFilter("Map Files", "trmp");
        chooser.setFileFilter(fileFilter);
        chooser.setDialogTitle("Load map");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int resultMap = chooser.showOpenDialog(jPanel);

        if (resultMap == chooser.APPROVE_OPTION) {
            File mapXml = chooser.getSelectedFile();
            Map map = (Map) xstream.fromXML(mapXml);
            mapView.setMap(map);
        } else return;

        chooser = new JFileChooser();
        fileFilter = new FileNameExtensionFilter("Images", ImageIO.getReaderFileSuffixes());
        chooser.setFileFilter(fileFilter);
        chooser.setDialogTitle("Load background");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int resultBackground = chooser.showOpenDialog(jPanel);

        if (resultBackground == chooser.APPROVE_OPTION) {
            try {
                BufferedImage background = ImageIO.read(chooser.getSelectedFile());
                mapView.setBackground(background);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveMap(Map map, JPanel jPanel) {
        XStream xstream = new XStream();
        String mapXml = xstream.toXML(map);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save map");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showSaveDialog(jPanel);

        File file = null;
        if (result == chooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        }

        if (file != null) {
            try {
                FileWriter fw = new FileWriter(chooser.getSelectedFile()+".trmp");
                fw.write(mapXml);
                fw.flush();
                fw.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void nameLocation(Location location, JPanel jPanel) {
        String newName = JOptionPane.showInputDialog(jPanel,
                "New location name", location.getName());
        if (newName != null) {
            location.setName(newName);
        }
    }

    public static void renameLocation(Map map, Point clickPoint, double minPickUpDistance, JPanel jPanel) {
        Location location = closestLocation(clickPoint, map.getLocations(), minPickUpDistance);
        if (location != null) {
            nameLocation(location, jPanel);
        }
    }

    public static void zoomIn(State state) {
        state.zoomIn();
    }

    public static void zoomOut(State state) {
        state.zoomOut();
    }

    public static void resetZoom(State state) {
        state.resetZoom();
    }

    public static double getZoom(State state) {
        return state.getZoom();
    }
}
