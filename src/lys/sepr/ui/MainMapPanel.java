package lys.sepr.ui;

import lys.sepr.game.ActiveTrain;
import lys.sepr.game.Game;
import lys.sepr.game.world.Intersection;
import lys.sepr.game.world.Map;
import lys.sepr.game.world.Track;

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

    private BufferedImage mapBackground;

    private static BufferedImage cursorNormalImage;
    private static BufferedImage cursorIntersectionImage;
    private static BufferedImage cursorRouteChangeImage;
    private static Cursor cursorNormal;
    private static Cursor cursorIntersection;
    private static Cursor cursorRouteChange;
    private static Cursor cursorInvisible;

    static {
        cursorNormalImage = null;
        try {
            cursorNormalImage = ImageIO.read(State.class.getResourceAsStream("/Cursor_normal_small.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        cursorIntersectionImage = null;
        try {
            cursorIntersectionImage = ImageIO.read(State.class.getResourceAsStream("/Cursor_junction_small.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        cursorRouteChangeImage = null;
        try {
            cursorRouteChangeImage = ImageIO.read(State.class.getResourceAsStream("/Cursor_routeChange_small.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        cursorInvisible = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "invisible");
        cursorNormal = Toolkit.getDefaultToolkit().createCustomCursor(cursorNormalImage, new Point(1, 1), "normal");
        cursorIntersection = Toolkit.getDefaultToolkit().createCustomCursor(cursorIntersectionImage, new Point(1, 1), "intersection");
        cursorRouteChange = Toolkit.getDefaultToolkit().createCustomCursor(cursorRouteChangeImage, new Point(1, 1), "route change");
    }

    MainMapPanel() {
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);

        setCursor(cursorNormal);

        minPickupDistance = (double) State.getRailHeight() / 2;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setGame(Game game) {
        this.game = game;
        this.map = game.getMap();
        mapBackground = Actions.getBackground(map);
    }

    public BufferedImage getMapBackground() {
        return mapBackground;
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {

            if (state.getSelectedIntersection() != null) {
                state.clearClickedTrackAndIntersection();
                state.setClickedIntersection();
            } else if (state.getSelectedTrack() != null) {
                state.setClickedTrack();
            } else {
                state.clearClickedTrackAndIntersection();
                setCursor(cursorNormal);
            }

            Track clickedTrack1 = state.getClickedTrack1();
            Track clickedTrack2 = state.getClickedTrack2();
            Intersection clickedIntersection = state.getClickedIntersection();

            if (clickedIntersection != null) {
                setCursor(cursorIntersection);
            }

            if (clickedIntersection != null) {
                if (clickedIntersection.getTracks().contains(clickedTrack1) &&
                        clickedIntersection.getTracks().contains(clickedTrack2)) {
                    game.changeActiveConnection(clickedTrack1, clickedTrack2);
                    state.clearClickedTrackAndIntersection();
                    setCursor(cursorNormal);
                }
            } else if (clickedTrack1 != null) {
                game.changeRoute(clickedTrack1);
                state.clearClickedTrackAndIntersection();
                setCursor(cursorNormal);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            lys.sepr.game.world.Point mousePoint = Actions.screenPointToMapPoint(e.getPoint(), state);
            Actions.deselectTrackIntersection(state);
            Actions.selectIntersectionOrTrack(game, mousePoint, minPickupDistance, state);
            state.setMousePosition(mousePoint);
            if (state.getClickedIntersection() != null) {
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
        lys.sepr.ui.Actions.drawMap(game, mapBackground, state, g2);
        lys.sepr.ui.Actions.drawTrainPathOverlay(game.getActivePlayer(), state, g2);
        for (Intersection intersection : map.getIntersections()) {
            Actions.drawIntersectionOverlay(intersection, state, g2);
        }
        lys.sepr.ui.Actions.drawTrains(game, state, g2);
    }
}
