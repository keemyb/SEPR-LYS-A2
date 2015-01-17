package lys.sepr.ui;

import lys.sepr.game.Player;
import lys.sepr.game.world.Point;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

    private static final double relativeTrainHeightToRail = 0.8;
    private static String[] trainPaths;
    private static BufferedImage[] originalTrains;
    private static BufferedImage[] scaledTrains;

    static {
        originalRailAndWood = null;
        try {
            originalRailAndWood = ImageIO.read(State.class.getResourceAsStream("/RailAndWood.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scaledRailAndWood = Actions.scaleImage(originalRailAndWood, (double) railHeight / originalRailAndWood.getHeight());

        trainPaths = new String[]{"/Train_red.png", "/Train_green.png", "/Train_blue.png", "/Train_yellow.png"};

        originalTrains = new BufferedImage[trainPaths.length];
        scaledTrains = new BufferedImage[trainPaths.length];

        for (int i=0; i < trainPaths.length; i++) {
            String path = trainPaths[i];

            try {
                originalTrains[i] = ImageIO.read(State.class.getResourceAsStream(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
// Using train width as the train images are vertical
            scaledTrains[i] = Actions.scaleImage(originalTrains[i], (double) railHeight * relativeTrainHeightToRail / originalTrains[i].getWidth());
        }
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

    public BufferedImage getScaledTrain(Player player) {
        int playerColorIndex = Player.PlayerColor.valueOf(player.getPlayerColor().toString()).ordinal();
        if (lastZoom != zoom) {
            for (int i=0; i < trainPaths.length; i++) {
                // Using train width as the train images are vertical
                scaledTrains[i] = Actions.scaleImage(originalTrains[i], zoom * railHeight * relativeTrainHeightToRail / originalTrains[i].getWidth());
            }
        }
        return scaledTrains[playerColorIndex];
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
