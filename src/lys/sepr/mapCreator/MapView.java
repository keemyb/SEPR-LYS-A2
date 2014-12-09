package lys.sepr.mapCreator;

import lys.sepr.game.world.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static lys.sepr.game.world.Utilities.clickPointToTrackPoint;

public class MapView {
    java.awt.Color selectedTrackColour = Color.ORANGE;
    java.awt.Color activeNextTrackColour = Color.GREEN;
    java.awt.Color validNextTrackColour = Color.BLUE;
    java.awt.Color connectedTrackColour = Color.RED;
    java.awt.Color unconnectedTrackColour = Color.BLACK;

    private MouseHandler mouseHandler = new MouseHandler();

    private Double minPickupDistance = 20d;

    private MapView mapView = this;

    private Map map;
    private State state;

    BufferedImage background;

    JScrollPane scrollPane = new JScrollPane();

    JPanel mapPanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, null);
            Actions.drawMap(map, state, mapView, g);
        }
    };

    MapView(Map map, State state) {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/europe.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Setting the size of the map panel to that of the image so that it can be scrolled if
        // necessary.
        mapPanel.setPreferredSize(new Dimension(background.getWidth(), background.getHeight()));
        scrollPane.setViewportView(mapPanel);

        this.map = map;
        this.state = state;
        mapPanel.addMouseListener(mouseHandler);
    }

    public JPanel getMapPanel() {
        return mapPanel;
    }
    public Map getMap() {
        return map;
    }
    public void setMap(Map map) {
        this.map = map;
    }
    public void setBackground(BufferedImage background) {
        this.background = background;
        mapPanel.setPreferredSize(new Dimension(background.getWidth(), background.getHeight()));
    }
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    private class MouseHandler extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            lys.sepr.game.world.Point clickPoint = clickPointToTrackPoint(e.getPoint(), mapPanel);
            switch(state.mode) {
                case State.INSPECT_TRACK_MODE:
                    Actions.inspectTrack(map, clickPoint, minPickupDistance, state);
                    break;
                case State.INSPECT_ROUTE_MODE:
                    Actions.inspectRoute(map, clickPoint, minPickupDistance, state);
                    break;
                case State.CREATE_TRACK_MODE:
                    Actions.createTrack(map, clickPoint, minPickupDistance, state);
                    break;
                case State.CREATE_LOCATION_MODE:
                    Actions.createLocation(map, clickPoint, minPickupDistance);
                    break;
                case State.MOVE_MODE:
                    Actions.pickupOrMoveLocationTrackIntersection(map, clickPoint, minPickupDistance, state);
                    break;
                case State.DELETE_LOCATION_MODE:
                    Actions.removeLocation(map, clickPoint, minPickupDistance);
                    break;
                case State.DELETE_TRACK_MODE:
                    Actions.removeTrack(map, clickPoint, minPickupDistance);
                    break;
                case State.DELETE_INTERSECTION_MODE:
                    Actions.removeIntersection(map, clickPoint, minPickupDistance);
                    break;
                case State.BREAK_TRACK_MODE:
                    Actions.breakTrack(map, clickPoint, minPickupDistance);
                    break;
            }
            mapPanel.repaint();
        }
    }
}
