package lys.sepr.mapCreator;

import com.thoughtworks.xstream.XStream;
import lys.sepr.game.world.*;
import lys.sepr.game.world.Point;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static lys.sepr.game.world.Utilities.*;

public final class Actions {

    public static Point clickPointToTrackPoint(java.awt.Point clickPoint, JPanel jPanel) {
        /* y axis points have been inverted as the window coordinates start from the top left
        where as the points start from the bottom left
        */
        double clickPointX = clickPoint.x;
        double clickPointY = jPanel.getHeight() - clickPoint.y;
        return new Point(clickPointX, clickPointY);
    }

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
            dropHeldLocationTrackIntersection(state);
            state.setTrackPickedUp((Track) trackAndPoint.get(0));
            state.setTrackPointPickedUp((Point) trackAndPoint.get(1));
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
        state.setHoldingLocationTrackIntersection(false);
    }

    public static void createLocation(Map map, Point clickPoint, Double minPickUpDistance, MapView mapView) {
        System.out.println("Create Location");
        for (Location existingLocation : map.getLocations()) {
            if (distance(existingLocation.getPoint(), clickPoint) < minPickUpDistance) return;
        }
        Location location = new Location(clickPoint, "location");
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
        }
        state.setStartedNewTrack(!state.isStartedNewTrack());
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
        if (state.isHoldingLocationTrackIntersection()) {
            moveLocationIntersectionTrackEnd(map, clickPoint, minPickUpDistance, state);
        } else {
            pickUpLocationIntersectionTrackEnd(map, clickPoint, minPickUpDistance, state);
        }
    }

    public static void drawMap(Map map, double locationSize, State state, MapView mapView, Graphics2D g2) {
        g2.setStroke(new BasicStroke(5));
        if (state.getRouteLocation2() != null) {
            drawRoute(map, state, mapView, g2);
        } else if (state.getSelectedTrack() != null) {
            drawNextTracks(map, state, mapView, g2);
        } else {
            drawNormal(map, state, mapView, g2);
        }
        for (Location location : map.getLocations()) {
            drawLocationName(location, locationSize, state, g2);
        }
    }

    public static void drawRoute(Map map, State state, MapView mapView, Graphics2D g2) {
        java.awt.Color lineColour;
        java.awt.Color locationColour;
        ArrayList<ArrayList<Track>> routes = map.getRoutes(state.getRouteLocation1(), state.getRouteLocation2());
        for (Track track : map.getTracks()) {
            lineColour = mapView.unconnectedTrackColour;
            Line2D.Double line = trackToLine2D(track, state);
            g2.setColor(lineColour);
            g2.draw(line);
        }

//        Random r = new Random();
//        float routesHue = r.nextFloat();
        float routesHue = 126f/360f;

        for (int i=0; i < routes.size(); i++) {
            if (i == 0){
                lineColour = mapView.selectedTrackColour;
            } else {
                // The shorter the route, the more saturation it has.
                lineColour = new Color(Color.HSBtoRGB(routesHue,
                        (routes.size() - i) / (float) routes.size(), 0.9f));
            }
            g2.setColor(lineColour);

            ArrayList<Track> route = routes.get(i);
            for (Track track : route) {
                Line2D.Double line = trackToLine2D(track, state);
                g2.draw(line);
            }
        }
        for (Location location : map.getLocations()) {
            if (location.equals(state.getRouteLocation1()) || location.equals(state.getRouteLocation2())) {
                locationColour = mapView.selectedTrackColour;
            } else locationColour = mapView.unconnectedTrackColour;
            Rectangle2D.Double rectangle = locationToRect2D(location, 10d, state);
            g2.setColor(locationColour);
            g2.draw(rectangle);
        }
    }

    public static void drawNextTracks(Map map, State state, MapView mapView, Graphics2D g2) {
        java.awt.Color lineColour;
        for (Track track : map.getTracks()) {
            Line2D.Double line = trackToLine2D(track, state);
            if (track.equals(state.getSelectedTrack())) {
                lineColour = mapView.selectedTrackColour;
            } else if (state.getSelectedTrack().getActiveNextTracks().contains(track)) {
                lineColour = mapView.activeNextTrackColour;
            } else if (state.getSelectedTrack().getValidNextTracks().contains(track)) {
                lineColour = mapView.validNextTrackColour;
            } else if (state.getSelectedTrack().getConnectedTracks().contains(track)) {
                lineColour = mapView.connectedTrackColour;
            } else lineColour = mapView.unconnectedTrackColour;

            g2.setColor(lineColour);
            g2.draw(line);
        }
        for (Location location : map.getLocations()) {
            Rectangle2D.Double rectangle = locationToRect2D(location, 10d, state);
            g2.setColor(randomColor());
            g2.draw(rectangle);
        }
    }

    public static void drawNormal(Map map, State state, MapView mapView, Graphics2D g2) {
        for (Track track : map.getTracks()) {
            Line2D.Double line = trackToLine2D(track, state);
            g2.setColor(randomColor());
            g2.draw(line);
        }
        for (Location location : map.getLocations()) {
            Rectangle2D.Double rectangle = locationToRect2D(location, 10d, state);
            if (state.getRouteLocation1() == location) {
                // highlight the first location selected when inspecting track
                g2.setColor(Color.GREEN);
            } else {
                g2.setColor(randomColor());
            }
            g2.draw(rectangle);
        }
    }

    public static void drawLocationName(Location location, double locationSize, State state, Graphics2D g2) {
        java.awt.Point locationPoint = mapPointToScreenPoint(location.getPoint(), state);
        double offset = locationSize / 2;
        g2.drawString(location.getName(), (float) (locationPoint.getX() + locationSize),
                (float) (locationPoint.getY() + offset));
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
