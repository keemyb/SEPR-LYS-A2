package lys.sepr.ui;

import lys.sepr.game.world.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class State {
    private double zoom = 1d;
    private double lastZoom = zoom;
    private final double zoomLevels = 4;
    private final double zoomConstant = 1.5;
    private final double maxZoom = Math.pow(zoomConstant, zoomLevels - 1);
    private final double minZoom = Math.pow(zoomConstant, -(zoomLevels - 1));

    private lys.sepr.game.world.Point clickPoint;

    private static BufferedImage originalRailAndWood;
    private static BufferedImage scaledRailAndWood;
    private static final int railHeight = 20;

    static {
        originalRailAndWood = null;
        try {
            originalRailAndWood = ImageIO.read(Actions.class.getResourceAsStream("/RailAndWood.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scaledRailAndWood = Actions.scaleImage(originalRailAndWood, (double) railHeight / originalRailAndWood.getHeight());
    }

    public void reset() {
        resetZoom();
    }

    public Point getClickPoint() {
        return clickPoint;
    }

    public void setClickPoint(Point clickPoint) {
        this.clickPoint = clickPoint;
    }

    public BufferedImage getScaledRailAndWood() {
        if (lastZoom != zoom) {
            scaledRailAndWood = Actions.scaleImage(originalRailAndWood, (double) railHeight * zoom / originalRailAndWood.getHeight());
        }
        return scaledRailAndWood;
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
}
