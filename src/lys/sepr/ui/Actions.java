package lys.sepr.ui;

import com.thoughtworks.xstream.XStream;
import lys.sepr.game.ActiveTrain;
import lys.sepr.game.Game;
import lys.sepr.game.Player;
import lys.sepr.game.world.*;
import lys.sepr.game.world.Point;
import lys.sepr.mapCreator.MapView;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static lys.sepr.game.world.Utilities.closestTrack;
import static lys.sepr.game.world.Utilities.distance;

public class Actions {

    public static void drawTrains(Game game, State state, Graphics2D g2) {
        for (Player player : game.getPlayers()) {
            ActiveTrain activeTrain = player.getActiveTrain();
            if (activeTrain != null) {
                double angle = activeTrain.getOrientation();
                java.awt.Point currentPosition = mapPointToScreenPoint(activeTrain.getCurrentPosition(), state);

                BufferedImage train = state.getScaledTrain(player);

                g2.translate(currentPosition.getX(), currentPosition.getY());
                g2.rotate(Math.PI / 2 - angle);
                g2.translate(-train.getWidth() / 2, -train.getHeight() / 2);

                g2.drawImage(train, new AffineTransform(), null);

                g2.translate(train.getWidth() / 2, train.getHeight() / 2);
                g2.rotate(angle - Math.PI / 2);
                g2.translate(-currentPosition.getX(), -currentPosition.getY());
            }
        }
    }

    public static void drawTrainPathOverlay(Player player, State state, Graphics2D g2) {
        ActiveTrain activeTrain = player.getActiveTrain();
        if (activeTrain == null) return;
        if (activeTrain.getRemainderOfRoute().isEmpty()) return;

        Track currentTrack = activeTrain.getRemainderOfRoute().get(0);
        // We only want to paint the part of the track left to cover.
        Track partialTrackToPaint = new Track(activeTrain.getCurrentPosition(), activeTrain.getFacing());
        drawTrackOverlay(partialTrackToPaint, player.getColor(), state, g2);

        for (Track track : activeTrain.getRemainderOfRoute()) {
            if (currentTrack ==  track) continue;
            drawTrackOverlay(track, player.getColor(), state, g2);
        }
    }

    private static void drawTrackOverlay(Track track, Color color, State state, Graphics2D g2) {
        double zoom = state.getZoom();
        double trackLength = Utilities.length(track);
        Point closestEndToOrigin = Utilities.closestPoint(new Point(0d, 0d), track.getPoints());
        java.awt.Point closestEndToOriginScreenPoint = mapPointToScreenPoint(closestEndToOrigin, state);
        Point furthestEndToOrigin = track.getOtherPoint(closestEndToOrigin);
        List<Double> vector = Utilities.getVector(closestEndToOrigin, furthestEndToOrigin);
        double angle = Math.atan2(-vector.get(1), vector.get(0));

        BufferedImage scaledRailAndWood = state.getScaledRailAndWood();
        Rectangle2D rectangle =  new Rectangle((int) (zoom * trackLength), (int) (scaledRailAndWood.getHeight() * 0.6));

        Color transparentColour = new Color(color.getRed(), color.getGreen(), color.getBlue(), 76);
        g2.setColor(transparentColour);

        g2.translate(closestEndToOriginScreenPoint.getX(), closestEndToOriginScreenPoint.getY());
        g2.rotate(-angle);
        g2.translate(0, -rectangle.getHeight() / 2);

        g2.fill(rectangle);

        g2.translate(0, rectangle.getHeight() / 2);
        g2.rotate(angle);
        g2.translate(-closestEndToOriginScreenPoint.getX(), -closestEndToOriginScreenPoint.getY());
    }

    public static void drawIntersectionOverlay(Intersection intersection, State state, Graphics2D g2) {
        Point intersectionPoint = intersection.getPoint();
        java.awt.Point scaledIntersectionPoint = mapPointToScreenPoint(intersectionPoint, state);
        int distanceFromIntersection = 6;
        double zoom = state.getZoom();

        for (Track track : intersection.getActiveConnection()) {
            List<Double> vector = Utilities.getVector(intersectionPoint, track.getOtherPoint(intersectionPoint));
            double angle = Math.atan2(-vector.get(1), vector.get(0));

            int triangleHeight = (int) (state.getScaledRailAndWood().getHeight() * 0.8);

            int[] xPoints = {0, 0, triangleHeight};
            int[] yPoints = {0, triangleHeight, triangleHeight/2};
            Shape shape = new Polygon(xPoints, yPoints, 3);

            Color transparentColour = new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 220);
            g2.setColor(transparentColour);

            g2.translate(scaledIntersectionPoint.getX(), scaledIntersectionPoint.getY());
            g2.rotate(-angle);
            g2.translate(distanceFromIntersection * zoom, 0);
            g2.translate(0, -triangleHeight / 2);

            g2.fill(shape);

            g2.translate(0, triangleHeight / 2);
            g2.translate(-distanceFromIntersection * zoom, 0);
            g2.rotate(angle);
            g2.translate(-scaledIntersectionPoint.getX(), -scaledIntersectionPoint.getY());
        }
    }

    public static void drawMap(Game game, BufferedImage background, State state, Graphics2D g2) {
        double zoom = state.getZoom();
        Map map = game.getMap();

        g2.scale(zoom, zoom);
        g2.drawImage(background, 0, 0, null);
        g2.scale(1/zoom, 1/zoom);
        
        for (Track track : map.getTracks()) {
            drawTrack(track, state, g2);
        }

        drawLocations(game, state, g2);
    }

    private static void drawTrack(Track track, State state, Graphics2D g2) {
        double zoom = state.getZoom();
        double trackLength = Utilities.length(track);
        Point closestEndToOrigin = Utilities.closestPoint(new Point(0d, 0d), track.getPoints());
        java.awt.Point closestEndToOriginScreenPoint = mapPointToScreenPoint(closestEndToOrigin, state);
        Point furthestEndToOrigin = track.getOtherPoint(closestEndToOrigin);
        List<Double> vector = Utilities.getVector(closestEndToOrigin, furthestEndToOrigin);
        double angle = Math.atan2(-vector.get(1), vector.get(0));

        BufferedImage scaledRailAndWood = state.getScaledRailAndWood();
        Rectangle2D rectangle =  new Rectangle((int) (trackLength * zoom), scaledRailAndWood.getHeight());

        TexturePaint texturePaint = new TexturePaint(scaledRailAndWood, new Rectangle(scaledRailAndWood.getWidth(), scaledRailAndWood.getHeight()));
        g2.setPaint(texturePaint);

        g2.translate(closestEndToOriginScreenPoint.getX(), closestEndToOriginScreenPoint.getY());
        g2.rotate(-angle);
        g2.translate(0, -rectangle.getHeight() / 2);

        g2.fill(rectangle);

        g2.translate(0, rectangle.getHeight() / 2);
        g2.rotate(angle);
        g2.translate(-closestEndToOriginScreenPoint.getX(), -closestEndToOriginScreenPoint.getY());
    }

    private static void drawLocations(Game game, State state, Graphics2D g2) {
        Map map = game.getMap();

        for (Location location : game.getMap().getLocations()) {
            drawRegularLocation(location, state, g2);
        }

        for (Player player : game.getPlayers()) {
            ActiveTrain activeTrain = player.getActiveTrain();
            if (activeTrain == null) continue;
            Location destinationLocation = map.getLocationFromPoint(activeTrain.getDestination());
            drawDestinationLocation(destinationLocation, player, state, g2);
        }
    }

    private static void drawRegularLocation(Location location, State state, Graphics2D g2) {
        g2.setColor(Color.WHITE);
        double locationSize = state.getZoom() * state.getFlagSize() * 0.5;
        g2.setStroke(new BasicStroke((float) locationSize / 4));
        java.awt.Point locationPoint = mapPointToScreenPoint(location.getPoint(), state);
        Rectangle2D.Double rectangle = new Rectangle2D.Double(locationPoint.getX() - locationSize / 2, locationPoint.getY() - locationSize /2, locationSize, locationSize);
        g2.draw(rectangle);

        g2.setColor(Color.BLACK);
        int fontSize = 18;
        g2.setFont(new Font("Courier New", Font.BOLD, fontSize));
        String locationName = location.getName();
        // To be horizontally centered
        int xOffset = locationName.length() * fontSize / 4;
        // To be positioned under location.
        int yOffset = (int) locationSize;
        g2.drawString(locationName, (float) (locationPoint.getX() - xOffset),
                (float) (locationPoint.getY() + yOffset));
    }

    private static void drawDestinationLocation(Location location, Player player, State state, Graphics2D g2) {
        BufferedImage flag = state.getScaledFlag(player);
        java.awt.Point point = mapPointToScreenPoint(location.getPoint(), state);

        g2.translate(point.getX(), point.getY());
        g2.translate(0, -flag.getHeight());

        g2.drawImage(flag, new AffineTransform(), null);

        g2.translate(0, flag.getHeight());
        g2.translate(-point.getX(), -point.getY());
    }

    public static BufferedImage scaleImage(BufferedImage image, double scale) {
        int newWidth = (int) (image.getWidth() * scale);
        int newHeight = (int) (image.getHeight() * scale);
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics g = scaledImage.createGraphics();
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return scaledImage;
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

    // TODO remove testMap Hardcoding
    public static Map loadMap() {
        XStream xstream = new XStream();

        File mapXml = null;
        try {
            mapXml = new File(Actions.class.getResource("/eu1.trmp").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Map map = (Map) xstream.fromXML(mapXml);
        return map;
    }

    public static void selectIntersectionOrTrack(Game game, Point mapMousePoint, Double minPickUpDistance, State state) {
        // Priority = Intersection > Tracks

        Map map = game.getMap();

        // Decreased pickup distance for intersections as they have priority over tracks
        Intersection intersection = selectIntersection(map, mapMousePoint, minPickUpDistance * 0.7);
        if (intersection != null) {
            state.selectIntersection(intersection);
            state.setHasSelectedTrackOrIntersection(true);
            return;
        }

        Track track = selectTrack(map, mapMousePoint, minPickUpDistance);
        if (track != null) {
            state.setSelectedTrack(track);
            state.setHasSelectedTrackOrIntersection(true);
        }
    }

    public static Intersection selectIntersection(Map map, Point clickPoint, Double minPickUpDistance) {
        for (Intersection intersection : map.getIntersections()) {
            if (distance(clickPoint, intersection.getPoint()) < minPickUpDistance) {
                return intersection;
            }
        }
        return null;
    }

    public static Track selectTrack(Map map, Point clickPoint, Double minPickUpDistance) {
        return closestTrack(clickPoint, map.getTracks(), minPickUpDistance);
    }

    public static void deselectTrackIntersection(State state) {
        state.setHasSelectedTrackOrIntersection(false);
        state.selectIntersection(null);
        state.setSelectedTrack(null);
    }

    public static BufferedImage getBackground(Map map) {
        try {
            return ImageIO.read(Actions.class.getResourceAsStream("/" + map.getBackgroundFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
