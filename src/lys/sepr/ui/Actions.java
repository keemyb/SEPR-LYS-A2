package lys.sepr.ui;

import com.thoughtworks.xstream.XStream;
import lys.sepr.game.world.*;
import lys.sepr.game.world.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Actions {

    private static BufferedImage railAndWood;
    // Have to save the width as the rotations change the size
    private static final int railAndWoodWidth;

    static {
        railAndWood = null;
        try {
            railAndWood = ImageIO.read(Actions.class.getResourceAsStream("/RailAndWood.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        railAndWoodWidth = railAndWood.getWidth();
    }

    public static void drawMap(Map map, double locationSize, State state, Graphics2D g2){

        // Track should be drawn, then rotated as a whole, not in pieces.
        // Also the last rotation is set for all images.
        
        for (Track track : map.getTracks()) {
            double trackLength = Utilities.length(track);
            List<Double> vector = Utilities.getVector(track);
            List<Double> scaledVector = Utilities.multiply(vector, railAndWoodWidth / (trackLength + 1));
            double angle = Math.atan2(-scaledVector.get(1), scaledVector.get(0));
            Point projectedPoint = new Point(track.getPoints().get(0));

            BufferedImage rotatedRailAndWood = rotateImage(duplicate(railAndWood), angle);
            for (int i = 0; i <= trackLength / railAndWoodWidth + 1; i++) {
                java.awt.Point screenPoint = mapPointToScreenPoint(projectedPoint, state);
                g2.drawImage(rotatedRailAndWood, (int) screenPoint.getX(), (int) screenPoint.getY(), null);
                projectedPoint.translate(scaledVector.get(0), scaledVector.get(1));
            }
        }

        for (Location location : map.getLocations()) {
            // draw locations
        }
    }

    public static BufferedImage loadImage(String path) {
        BufferedImage image;
        try {
            image = ImageIO.read(Actions.class.getResource(path));
            return image;
        }
        catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public static BufferedImage duplicate(BufferedImage image) {
        BufferedImage j = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        j.setData(image.getData());
        return j;
    }

    public static BufferedImage rotateImage(BufferedImage image, double angle) {
        AffineTransform tx = new AffineTransform();
        tx.rotate(Math.toRadians(angle), image.getWidth() / 2, image.getHeight() / 2);

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, null);
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
