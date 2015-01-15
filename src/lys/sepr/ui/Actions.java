package lys.sepr.ui;

import com.thoughtworks.xstream.XStream;
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

    static {
        railAndWood = null;
        try {
            railAndWood = ImageIO.read(Actions.class.getResourceAsStream("/RailAndWood.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void drawMap(Map map, double locationSize, State state, Graphics2D g2){
        double zoom = state.getZoom();
        g2.scale(zoom, zoom);
        g2.drawImage(map.getBackground(), 0, 0, null);
        g2.scale(1/zoom, 1/zoom);
        
        for (Track track : map.getTracks()) {
            drawTrack(track, state, g2);
        }

        for (Location location : map.getLocations()) {
            // draw locations
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

        AffineTransform preTransform = new AffineTransform();
        preTransform.translate(0, -rectangle.getHeight() / 2);
        preTransform.translate(closestEndToOriginScreenPoint.getX(), closestEndToOriginScreenPoint.getY());
        preTransform.rotate(-angle);
        g2.setTransform(preTransform);

        g2.fill(rectangle);
    }

    public static BufferedImage scaleImage(BufferedImage image, double scale) {
        BufferedImage scaledImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = scaledImage.createGraphics();
        AffineTransform affineTransform = AffineTransform.getScaleInstance(scale, scale);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.drawImage(image, affineTransform, null);
        graphics2D.dispose();
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
