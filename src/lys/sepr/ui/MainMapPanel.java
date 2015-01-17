package lys.sepr.ui;

import lys.sepr.game.Game;
import lys.sepr.game.world.Intersection;
import lys.sepr.game.world.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MainMapPanel extends JPanel {

    private Map map;
    private State state;
    private Game game;

    private Double minPickupDistance = 20d;

    private MouseHandler mouseHandler = new MouseHandler();

    private static BufferedImage cursorNormalImage;
    private static BufferedImage cursorIntersectionImage;
    private static BufferedImage cursorRouteChangeImage;
    private static Cursor cursorNormal;
    private static Cursor cursorIntersection;
    private static Cursor cursorRouteChange;

    static {
        cursorNormalImage = null;
        try {
            cursorNormalImage = ImageIO.read(State.class.getResourceAsStream("/Cursor_normal.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        cursorIntersectionImage = null;
        try {
            cursorIntersectionImage = ImageIO.read(State.class.getResourceAsStream("/Cursor_junction.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        cursorRouteChangeImage = null;
        try {
            cursorRouteChangeImage = ImageIO.read(State.class.getResourceAsStream("/Cursor_routeChange.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    MainMapPanel() {
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);

        Toolkit toolkit= getToolkit();
        cursorNormal = toolkit.createCustomCursor(cursorNormalImage, new Point(0,0), "normal");
        cursorIntersection = toolkit.createCustomCursor(cursorIntersectionImage, new Point(0,0), "intersection");
        cursorRouteChange = toolkit.createCustomCursor(cursorRouteChangeImage, new Point(0,0), "route change");
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setGame(Game game) {
        this.game = game;
        this.map = game.getMap();
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            lys.sepr.game.world.Point clickPoint = Actions.screenPointToMapPoint(e.getPoint(), state);

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            lys.sepr.game.world.Point mousePoint = Actions.screenPointToMapPoint(e.getPoint(), state);
            Actions.deselectTrackIntersection(state);
            Actions.selectIntersectionOrTrack(game, mousePoint, minPickupDistance, state);

            if (state.getSelectedIntersection() != null) {
                setCursor(cursorIntersection);
            } else if (state.getSelectedTrack() != null) {
                setCursor(cursorRouteChange);
            } else {
                setCursor(cursorNormal);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        lys.sepr.ui.Actions.drawMap(game, state, g2);
        lys.sepr.ui.Actions.drawTrainPathOverlay(game.getActivePlayer(), state, g2);
        for (Intersection intersection : map.getIntersections()) {
            Actions.drawIntersectionOverlay(intersection, state, g2);
        }
        lys.sepr.ui.Actions.drawTrains(game, state, g2);
        lys.sepr.ui.Actions.drawMouseCursor(state, g2);
    }
}
