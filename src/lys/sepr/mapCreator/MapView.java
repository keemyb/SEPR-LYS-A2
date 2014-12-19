package lys.sepr.mapCreator;

import lys.sepr.game.world.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
            Graphics2D g2 = (Graphics2D) g;
            double zoom = state.getZoom();
            g2.scale(zoom, zoom);
            g2.drawImage(background, 0, 0, null);
            Actions.drawMap(map, state, mapView, g2);
        }
    };

    MapView(State state) {
        setDefaultBackground();

        map = new Map();

        this.state = state;
        mapPanel.addMouseListener(mouseHandler);
    }

    public Map getMap() {
        return map;
    }
    public void setMap(Map map) {
        this.map = map;
        mapPanel.repaint();
    }

    public void setDefaultBackground() {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/europe.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Setting the size of the map panel to that of the image so that it can be scrolled if
        // necessary.
        mapPanel.setPreferredSize(new Dimension(background.getWidth(), background.getHeight()));
        scrollPane.setViewportView(mapPanel);
    }
    public void setBackground(BufferedImage background) {
        this.background = background;
        mapPanel.setPreferredSize(new Dimension(background.getWidth(), background.getHeight()));
        mapPanel.repaint();
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
    public JPanel getMapPanel() {
        return mapPanel;
    }

    private class MouseHandler extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            lys.sepr.game.world.Point clickPoint = Actions.screenPointToMapPoint(e.getPoint(), mapView, state);
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
