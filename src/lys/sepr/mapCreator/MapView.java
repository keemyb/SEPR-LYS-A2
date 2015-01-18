package lys.sepr.mapCreator;

import lys.sepr.game.world.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MapView {
    java.awt.Color selectedTrackColour = Color.ORANGE;
    java.awt.Color activeConnectedTrackColour = Color.GREEN;
    java.awt.Color validConnectedTrackColour = Color.BLUE;
    java.awt.Color invalidConnectedTrackColour = Color.RED;
    java.awt.Color unconnectedTrackColour = Color.BLACK;
    java.awt.Color normalTrackColour = new Color(204, 0, 204); // Purple

    private final double locationSize = 10d;

    private MouseHandler mouseHandler = new MouseHandler();

    private Double minPickupDistance = 20d;

    private MapView mapView = this;

    private Map map;
    private State state;

    // This list represents the modes that the map will be redrawn in to
    // show new objects based on the users current mouse location.
    private List<Integer> liveUpdateModes = new ArrayList<Integer>(Arrays.asList(
            State.CREATE_LOCATION_MODE, State.CREATE_TRACK_MODE, State.MOVE_MODE
    ));

    BufferedImage background;
    String defaultBackgroundName = "EuropeMap.png";

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
            Actions.drawMap(map, locationSize, state, mapView, g2);
        }
    };

    MapView(State state) {
        this.state = state;

        scrollPane.setViewportView(mapPanel);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        map = new Map();
        showDefaultBackground();

        mapPanel.addMouseListener(mouseHandler);
        mapPanel.addMouseMotionListener(mouseHandler);
    }

    public void setMapPanelSize() {
        double zoom = state.getZoom();
        int width = (int) (background.getWidth() * zoom);
        int height = (int) (background.getHeight() * zoom);
        mapPanel.setPreferredSize(new Dimension(width, height));
        scrollPane.getViewport().revalidate();
        scrollPane.getViewport().repaint();
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map, String mapPath) {
        this.map = map;
        showBackgroundFullPath(mapPath + "\\" + map.getBackgroundFileName());
        state.reset();
    }

    public void refreshBackground() {
        setMapPanelSize();
        mapPanel.repaint();
    }
    public void showDefaultBackground() {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/" + defaultBackgroundName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshBackground();
    }

    public void showBackgroundFullPath(String backgroundFilePath) {
        if (backgroundFilePath == null) {
            showDefaultBackground();
        } else {
            try {
                System.out.println(backgroundFilePath);
                this.background = ImageIO.read(new File(backgroundFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            refreshBackground();
        }
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
            double scaledPickupDistance = minPickupDistance / state.getZoom();
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
                    Actions.createLocation(map, clickPoint, scaledPickupDistance, mapView);
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
                case State.RENAME_LOCATION_MODE:
                    Actions.renameLocation(map, clickPoint, scaledPickupDistance, mapPanel);
                    break;
            }
            mapPanel.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            if (!liveUpdateModes.contains(state.getMode())) return;

            state.setClickPoint(Actions.screenPointToMapPoint(e.getPoint(), state));
            mapPanel.repaint();
        }
    }
}
