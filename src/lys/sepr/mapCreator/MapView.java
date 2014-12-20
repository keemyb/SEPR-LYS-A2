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

    private double lastZoom;

    BufferedImage background;

    JScrollPane scrollPane = new JScrollPane();

    JPanel mapPanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            double zoom = state.getZoom();
            g2.scale(zoom, zoom);
            setMapPanelSize();
            g2.drawImage(background, 0, 0, null);
            g2.scale(1/zoom, 1/zoom);
            Actions.drawMap(map, state, mapView, g2);
        }
    };

    MapView(State state) {
        this.state = state;

        scrollPane.setViewportView(mapPanel);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        setDefaultBackground();

        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        map = new Map();

        mapPanel.addMouseListener(mouseHandler);
    }

    public void setMapPanelSize() {
        double zoom = state.getZoom();
        if (lastZoom != zoom) {
            int width = (int) (background.getWidth() * zoom);
            int height = (int) (background.getHeight() * zoom);
            mapPanel.setPreferredSize(new Dimension(width, height));
            scrollPane.getViewport().revalidate();
            scrollPane.getViewport().repaint();
            lastZoom = zoom;
        }
    }

    public Map getMap() {
        return map;
    }

    public BufferedImage getBackground() {
        return background;
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

        setBackground(background);
    }
    public void setBackground(BufferedImage background) {
        this.background = background;
        setMapPanelSize();
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
            lys.sepr.game.world.Point clickPoint = Actions.screenPointToMapPoint(e.getPoint(), state);
            double scaledPickupDistance = minPickupDistance * state.getZoom();
            switch(state.getMode()) {
                case State.INSPECT_TRACK_MODE:
                    Actions.inspectTrack(map, clickPoint, scaledPickupDistance, state);
                    break;
                case State.INSPECT_ROUTE_MODE:
                    Actions.inspectRoute(map, clickPoint, scaledPickupDistance, state);
                    break;
                case State.CREATE_TRACK_MODE:
                    Actions.createTrack(map, clickPoint, scaledPickupDistance, state);
                    break;
                case State.CREATE_LOCATION_MODE:
                    Actions.createLocation(map, clickPoint, scaledPickupDistance);
                    break;
                case State.MOVE_MODE:
                    Actions.pickupOrMoveLocationTrackIntersection(map, clickPoint, scaledPickupDistance, state);
                    break;
                case State.DELETE_LOCATION_MODE:
                    Actions.removeLocation(map, clickPoint, scaledPickupDistance);
                    break;
                case State.DELETE_TRACK_MODE:
                    Actions.removeTrack(map, clickPoint, scaledPickupDistance);
                    break;
                case State.DELETE_INTERSECTION_MODE:
                    Actions.removeIntersection(map, clickPoint, scaledPickupDistance);
                    break;
                case State.BREAK_TRACK_MODE:
                    Actions.breakTrack(map, clickPoint, scaledPickupDistance);
                    break;
            }
            mapPanel.repaint();
        }
    }
}
