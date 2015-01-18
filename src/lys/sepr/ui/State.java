package lys.sepr.ui;

import lys.sepr.game.Player;
import lys.sepr.game.world.Intersection;
import lys.sepr.game.world.Point;
import lys.sepr.game.world.Track;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class State {
    private double zoom = 1d;
    private double lastZoom;
    private final double zoomLevels = 4;
    private final double zoomConstant = 1.5;
    private final double maxZoom = Math.pow(zoomConstant, zoomLevels - 1);
    private final double minZoom = Math.pow(zoomConstant, -(zoomLevels - 1));

    private lys.sepr.game.world.Point mousePosition;
    private boolean hasSelectedTrackOrIntersection;

    private Track selectedTrack;
    private Intersection selectedIntersection;

    private Track clickedTrack1;
    private Track clickedTrack2;
    private Intersection clickedIntersection;

    private static BufferedImage originalRailAndWood;
    private static BufferedImage scaledRailAndWood;

    private static final int railHeight = 20;

    private static final double relativeTrainWidthToRail = 0.8;
    private static String[] trainPaths;
    private static BufferedImage[] originalTrains;
    private static BufferedImage[] scaledTrains;

    private static final int flagSize = 40;
    private static String[] flagPaths;
    private static BufferedImage[] originalFlags;
    private static BufferedImage[] scaledFlags;

    static {
        originalRailAndWood = null;
        try {
            originalRailAndWood = ImageIO.read(State.class.getResourceAsStream("/RailAndWood.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scaledRailAndWood = Actions.scaleImage(originalRailAndWood, (double) railHeight / originalRailAndWood.getHeight());

        trainPaths = new String[]{"/Train_red_small.png", "/Train_green_small.png", "/Train_blue_small.png", "/Train_yellow_small.png"};
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
            scaledTrains[i] = Actions.scaleImage(originalTrains[i], (double) railHeight * relativeTrainWidthToRail / originalTrains[i].getWidth());
        }

        flagPaths = new String[]{"/Flag_red.png", "/Flag_green.png", "/Flag_blue.png", "/Flag_yellow.png"};
        originalFlags = new BufferedImage[trainPaths.length];
        scaledFlags = new BufferedImage[trainPaths.length];

        for (int i=0; i < flagPaths.length; i++) {
            String path = flagPaths[i];

            try {
                originalFlags[i] = ImageIO.read(State.class.getResourceAsStream(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            scaledFlags[i] = Actions.scaleImage(originalFlags[i], flagSize / (double) originalFlags[i].getWidth());
        }
    }

    public void reset() {
        resetZoom();
    }

    public Point getMousePosition() {
        return mousePosition;
    }

    public void setMousePosition(Point mousePosition) {
        this.mousePosition = mousePosition;
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
                scaledTrains[i] = Actions.scaleImage(originalTrains[i], zoom * railHeight * relativeTrainWidthToRail / originalTrains[i].getWidth());
            }
        }
        return scaledTrains[playerColorIndex];
    }

    public BufferedImage getScaledFlag(Player player) {
        int playerColorIndex = Player.PlayerColor.valueOf(player.getPlayerColor().toString()).ordinal();
        if (lastZoom != zoom) {
            for (int i=0; i < scaledFlags.length; i++) {
                scaledFlags[i] = Actions.scaleImage(originalFlags[i], zoom * flagSize / (double) originalFlags[i].getWidth());
            }
        }
        return scaledFlags[playerColorIndex];
    }

    public static int getFlagSize() {
        return flagSize;
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

    public void setSelectedTrack(Track track) {
        this.selectedTrack = track;
        selectedIntersection = null;
    }

    public void selectIntersection(Intersection intersection) {
        this.selectedIntersection = intersection;
        selectedTrack = null;
    }

    public void setHasSelectedTrackOrIntersection(boolean hasSelectedTrackOrIntersection) {
        this.hasSelectedTrackOrIntersection = hasSelectedTrackOrIntersection;
    }

    public boolean hasSelectedTrackOrIntersection() {
        return hasSelectedTrackOrIntersection;
    }

    public Intersection getSelectedIntersection() {
        return selectedIntersection;
    }

    public Track getSelectedTrack() {
        return selectedTrack;
    }

    public static int getRailHeight() {
        return railHeight;
    }

    public Track getClickedTrack1() {
        return clickedTrack1;
    }

    public Track getClickedTrack2() {
        return clickedTrack2;
    }

    public void setClickedTrack() {
        if (clickedTrack1 != null && clickedTrack2 != null) {
            clickedTrack1 = selectedTrack;
            clickedTrack2 = null;
        } else if (clickedTrack1 == null) {
            clickedTrack1 = selectedTrack;
        } else if (clickedTrack2 == null) {
            clickedTrack2 = selectedTrack;
        }
    }

    public Intersection getClickedIntersection() {
        return clickedIntersection;
    }

    public void setClickedIntersection() {
        clearClickedTrackAndIntersection();
        this.clickedIntersection = selectedIntersection;
    }

    public void clearClickedTrackAndIntersection() {
        clickedTrack1 = null;
        clickedTrack2 = null;
        clickedIntersection = null;
    }
}
