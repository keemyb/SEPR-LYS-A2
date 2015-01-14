package lys.sepr.ui;

import lys.sepr.game.world.Point;

public class State {
    private double zoom = 1d;
    private final double zoomLevels = 4;
    private final double zoomConstant = 1.5;
    private final double maxZoom = Math.pow(zoomConstant, zoomLevels - 1);
    private final double minZoom = Math.pow(zoomConstant, -(zoomLevels - 1));

    private lys.sepr.game.world.Point clickPoint;

    public void reset() {
        resetZoom();
    }

    public Point getClickPoint() {
        return clickPoint;
    }

    public void setClickPoint(Point clickPoint) {
        this.clickPoint = clickPoint;
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
