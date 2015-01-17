package lys.sepr.ui;

import com.thoughtworks.xstream.XStream;
import lys.sepr.game.ActiveTrain;
import lys.sepr.game.Game;
import lys.sepr.game.Player;
import lys.sepr.game.world.*;
import lys.sepr.game.world.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Actions {

    private static BufferedImage railAndWood;
    private static final int railHeight = 20;

    static {
        railAndWood = null;
        try {
            railAndWood = ImageIO.read(Actions.class.getResourceAsStream("/RailAndWood.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        railAndWood = scaleImage(railAndWood, (double) railHeight/railAndWood.getHeight());
    }

    public static void drawTrains(Game game, State state, Graphics2D g2) {
        for (Player player : game.getPlayers()) {
            ActiveTrain activeTrain = player.getActiveTrain();
            if (activeTrain != null) {
                double angle = activeTrain.getOrientation();
                java.awt.Point currentPosition = mapPointToScreenPoint(activeTrain.getCurrentPosition(), state);

                g2.setColor(Color.blue);
                Rectangle2D train = new Rectangle(railAndWood.getHeight(), railAndWood.getHeight() / 2);

                g2.translate(currentPosition.getX(), currentPosition.getY());
                g2.rotate(-angle);
                g2.translate(-train.getWidth(), -train.getHeight() / 2);

                g2.fill(train);

                g2.translate(train.getWidth(), train.getHeight() / 2);
                g2.rotate(angle);
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
        drawTrackOverlay(partialTrackToPaint, Color.ORANGE, state, g2);

        for (Track track : activeTrain.getRemainderOfRoute()) {
            if (currentTrack ==  track) continue;
            drawTrackOverlay(track, Color.ORANGE, state, g2);
        }
    }

    private static void drawTrackOverlay(Track track, Color color, State state, Graphics2D g2) {
        double trackLength = Utilities.length(track);
        Point closestEndToOrigin = Utilities.closestPoint(new Point(0d, 0d), track.getPoints());
        java.awt.Point closestEndToOriginScreenPoint = mapPointToScreenPoint(closestEndToOrigin, state);
        Point furthestEndToOrigin = track.getOtherPoint(closestEndToOrigin);
        List<Double> vector = Utilities.getVector(closestEndToOrigin, furthestEndToOrigin);
        double angle = Math.atan2(-vector.get(1), vector.get(0));

        Rectangle2D rectangle =  new Rectangle((int) trackLength, (int) (railAndWood.getHeight() * 0.6));

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
        int distanceFromIntersection = 6;
        double zoom = state.getZoom();

        for (Track track : intersection.getActiveConnection()) {
            List<Double> vector = Utilities.getVector(intersectionPoint, track.getOtherPoint(intersectionPoint));
            double angle = Math.atan2(-vector.get(1), vector.get(0));

            int triangleHeight = (int) (railAndWood.getHeight() * 0.8);

            int[] xPoints = {0, 0, triangleHeight};
            int[] yPoints = {0, triangleHeight, triangleHeight/2};
            Shape shape = new Polygon(xPoints, yPoints, 3);

            Color transparentColour = new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 220);
            g2.setColor(transparentColour);

            g2.translate(intersectionPoint.getX(), intersectionPoint.getY());
            g2.rotate(-angle);
            g2.translate(distanceFromIntersection * zoom, 0);
            g2.translate(0, -triangleHeight / 2);

            g2.fill(shape);

            g2.translate(0, triangleHeight / 2);
            g2.translate(-distanceFromIntersection * zoom, 0);
            g2.rotate(angle);
            g2.translate(-intersectionPoint.getX(), -intersectionPoint.getY());
        }
    }

    public static void drawMap(Map map, double locationSize, State state, Graphics2D g2) {
        double zoom = state.getZoom();
        g2.scale(zoom, zoom);
        g2.drawImage(map.getBackground(), 0, 0, null);
        g2.scale(1/zoom, 1/zoom);
        
        for (Track track : map.getTracks()) {
            drawTrack(track, state, g2);
        }

        for (Location location : map.getLocations()) {
            drawLocation(location, locationSize, state, g2);
        }
    }

    private static void drawTrack(Track track, State state, Graphics2D g2) {
        double trackLength = Utilities.length(track);
        Point closestEndToOrigin = Utilities.closestPoint(new Point(0d, 0d), track.getPoints());
        java.awt.Point closestEndToOriginScreenPoint = mapPointToScreenPoint(closestEndToOrigin, state);
        Point furthestEndToOrigin = track.getOtherPoint(closestEndToOrigin);
        List<Double> vector = Utilities.getVector(closestEndToOrigin, furthestEndToOrigin);
        double angle = Math.atan2(-vector.get(1), vector.get(0));

        Rectangle2D rectangle =  new Rectangle((int) trackLength, railAndWood.getHeight());

        TexturePaint texturePaint = new TexturePaint(railAndWood, new Rectangle(railAndWood.getWidth(), railAndWood.getHeight()));
        g2.setPaint(texturePaint);

        g2.translate(closestEndToOriginScreenPoint.getX(), closestEndToOriginScreenPoint.getY());
        g2.rotate(-angle);
        g2.translate(0, -rectangle.getHeight() / 2);

        g2.fill(rectangle);

        g2.translate(0, rectangle.getHeight() / 2);
        g2.rotate(angle);
        g2.translate(-closestEndToOriginScreenPoint.getX(), -closestEndToOriginScreenPoint.getY());
    }

    private static void drawLocation(Location location, double size, State state, Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(5));
        size *= state.getZoom();
        java.awt.Point point = mapPointToScreenPoint(location.getPoint(), state);
        Rectangle2D.Double rectangle = new Rectangle2D.Double(point.getX() - size / 2, point.getY() - size /2, size, size);
        g2.draw(rectangle);
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

        File mapXml = new File("files/testMap.trmp");
        Map map = (Map) xstream.fromXML(mapXml);
        return map;
    }
}
