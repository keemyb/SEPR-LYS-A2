package lys.sepr.game.world;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static lys.sepr.game.world.Utilities.*;

public class MapCreator extends JFrame {

    Map map = new Map();

    public static final int INSPECT_TRACK_MODE = 0;
    public static final int MOVE_MODE = 1;
    public static final int DELETE_TRACK_MODE = 2;
    public static final int DELETE_INTERSECTION_MODE = 3;
    public static final int DELETE_LOCATION_MODE = 4;
    public static final int CREATE_TRACK_MODE = 5;
    public static final int CREATE_LOCATION_MODE = 6;
    public static final int INSPECT_ROUTE_MODE = 7;
    public static final int BREAK_TRACK_MODE = 8;

    public int mode = INSPECT_TRACK_MODE;

    public MouseHandler mouseHandler = new MouseHandler();
    public KeyHandler keyHandler = new KeyHandler();
    public double minPickUpDistance = 20;

    public Track selectedTrack;
    public boolean startedNewTrack = false;

    public Point newTrackPoint1;
    public Point newTrackPoint2;

    public boolean holdingLocationTrackIntersection = false;
    public Intersection intersectionPickedUp;
    public Point trackPointPickedUp;
    public Track trackPickedUp;
    public Location locationPickedUp;

    public boolean startedRouteInspect = false;
    public Location location1;
    public Location location2;

    java.awt.Color selectedTrackColour = Color.ORANGE;
    java.awt.Color activeNextTrackColour = Color.GREEN;
    java.awt.Color validNextTrackColour = Color.BLUE;
    java.awt.Color connectedTrackColour = Color.RED;
    java.awt.Color unconnectedTrackColour = Color.BLACK;

    JLabel instructions = new JLabel("<html>" +
            "Once two locations have been created, go to inspect route and click them to see if there is a route between them" +
            "<br>Move will move anything (Location first, then Intersection, then Track End." +
            "<br>Your aim is to find as many bugs as possible." +
            "<br>I cannot fix anything however if it can't be reproduced." +
            "<br>" +
            "<br>Coming Soon:" +
            "<br>Map Guide (Image of map in background)" +
            "<br>Move view (to create maps larger than the screen)" +
            "<br>Location naming (just not implemented in GUI)" +
            "<br>Showing all routes and not just the fastest/closest (just not implemented in GUI)" +
            "<br>Save/Restore Map" +
            "<br>Probably Not Coming Soon:" +
            "<br>Curves" +
            "</html>", SwingConstants.LEFT);

    JLabel selectedTrackLabel = new JLabel("Selected Track", SwingConstants.LEFT);
    JLabel activeNextTrackLabel = new JLabel("Active Next Track", SwingConstants.LEFT);
    JLabel validNextTrackLabel = new JLabel("Valid Next Track", SwingConstants.LEFT);
    JLabel connectedTrackLabel = new JLabel("Connected (Non traversable) Track", SwingConstants.LEFT);
    JLabel unconnectedTrackLabel = new JLabel("Unconnected Track", SwingConstants.LEFT);

    JRadioButton createTrackModeButton = new JRadioButton("Create Track");
    JRadioButton moveModeButton = new JRadioButton("Move");
    JRadioButton inspectTrackModeButton = new JRadioButton("Inspect Track");
    JRadioButton deleteLocationModeButton = new JRadioButton("Delete Location");
    JRadioButton deleteTrackModeButton = new JRadioButton("Delete Track");
    JRadioButton deleteIntersectionModeButton = new JRadioButton("Delete Intersection");
    JRadioButton createLocationModeButton = new JRadioButton("Create Location");
    JRadioButton inspectRouteModeButton = new JRadioButton("Inspect Route");
    JRadioButton breakTrackModeButton = new JRadioButton("Break Track");

    ButtonGroup modeButtons = new ButtonGroup();

    MapCreator() {
        super("Map Creator");
        initialiseMap();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1280, 720);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addKeyListener(keyHandler);

        selectedTrackLabel.setForeground(selectedTrackColour);
        activeNextTrackLabel.setForeground(activeNextTrackColour);
        validNextTrackLabel.setForeground(validNextTrackColour);
        connectedTrackLabel.setForeground(connectedTrackColour);
        unconnectedTrackLabel.setForeground(unconnectedTrackColour);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        container.add(instructions);
        container.add(selectedTrackLabel);
        container.add(activeNextTrackLabel);
        container.add(validNextTrackLabel);
        container.add(connectedTrackLabel);
        container.add(unconnectedTrackLabel);
        getContentPane().add(container, BorderLayout.NORTH);

        modeButtons.add(inspectTrackModeButton);
        modeButtons.add(createTrackModeButton);
        modeButtons.add(moveModeButton);
        modeButtons.add(deleteLocationModeButton);
        modeButtons.add(deleteTrackModeButton);
        modeButtons.add(deleteIntersectionModeButton);
        modeButtons.add(createLocationModeButton);
        modeButtons.add(inspectRouteModeButton);
        modeButtons.add(breakTrackModeButton);

        inspectTrackModeButton.setSelected(true);

        createTrackModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dropHeldLocationTrackIntersection();
                mode = CREATE_TRACK_MODE;
            }
        });

        moveModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearCreateNew();
                mode = MOVE_MODE;
            }
        });

        inspectTrackModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearCreateNew();
                dropHeldLocationTrackIntersection();
                clearInspect();
                mode = INSPECT_TRACK_MODE;
            }
        });

        deleteTrackModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearCreateNew();
                dropHeldLocationTrackIntersection();
                mode = DELETE_TRACK_MODE;
            }
        });

        deleteLocationModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearCreateNew();
                dropHeldLocationTrackIntersection();
                mode = DELETE_LOCATION_MODE;
            }
        });

        deleteIntersectionModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearCreateNew();
                dropHeldLocationTrackIntersection();
                mode = DELETE_INTERSECTION_MODE;
            }
        });

        createLocationModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearCreateNew();
                dropHeldLocationTrackIntersection();
                mode = CREATE_LOCATION_MODE;
            }
        });

        inspectRouteModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearCreateNew();
                dropHeldLocationTrackIntersection();
                mode = INSPECT_ROUTE_MODE;
            }
        });

        breakTrackModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearCreateNew();
                dropHeldLocationTrackIntersection();
                mode = BREAK_TRACK_MODE;
            }
        });

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(inspectTrackModeButton);
        buttonPanel.add(inspectRouteModeButton);
        buttonPanel.add(createTrackModeButton);
        buttonPanel.add(createLocationModeButton);
        buttonPanel.add(moveModeButton);
        buttonPanel.add(deleteLocationModeButton);
        buttonPanel.add(deleteTrackModeButton);
        buttonPanel.add(deleteIntersectionModeButton);
        buttonPanel.add(breakTrackModeButton);

        buttonPanel.setSize(1280, 100);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initialiseMap() {
        Point startPoint1 = new Point(0,0);
        Point endPoint1 = new Point(100,100);

        Point startPoint2 = new Point(200,200);
        Point endPoint2 = new Point(100,100);

        Point startPoint3 = new Point(100,100);
        Point endPoint3 = new Point(200,100);

//        Track track1 = new Track(startPoint1, endPoint1);
//        Track track2 = new Track(startPoint2, endPoint2);
//        Track track3 = new Track(startPoint3, endPoint3);

        Track track1 = new Track(new Point(0, 0), new Point(100, 100));
        Track track2 = new Track(new Point(100, 100), new Point(200, 200));
        Track track3 = new Track(new Point(100, 100), new Point(130, 150));
        Track track4 = new Track(new Point(130, 150), new Point(200, 200));
        Track track5 = new Track(new Point(100, 100), new Point(160, 150));
        Track track6 = new Track(new Point(160, 150), new Point(200, 200));
        Track track7 = new Track(new Point(200, 200), new Point(300, 300));

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
        map.addTrack(track4);
        map.addTrack(track5);
        map.addTrack(track6);
        map.addTrack(track7);
    }

    private class MouseHandler extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            Point clickPoint = clickPointToTrackPoint(e.getPoint(), MapCreator.this);
            switch(mode) {
                case INSPECT_TRACK_MODE:
                    inspectTrack(clickPoint);
                    break;
                case INSPECT_ROUTE_MODE:
                    inspectRoute(clickPoint);
                    break;
                case CREATE_TRACK_MODE:
                    createTrack(clickPoint);
                    break;
                case CREATE_LOCATION_MODE:
                    createLocation(clickPoint);
                    break;
                case MOVE_MODE:
                    pickupOrMoveLocationTrackIntersection(clickPoint);
                    break;
                case DELETE_LOCATION_MODE:
                    removeLocation(clickPoint);
                    break;
                case DELETE_TRACK_MODE:
                    removeTrack(clickPoint);
                    break;
                case DELETE_INTERSECTION_MODE:
                    removeIntersection(clickPoint);
                    break;
                case BREAK_TRACK_MODE:
                    breakTrack(clickPoint);
                    break;
            }
        }
    }

    private void clearCreateNew() {
        startedNewTrack = false;
        newTrackPoint1 = null;
        newTrackPoint2 = null;
    }

    private void dropHeldLocationTrackIntersection() {
        holdingLocationTrackIntersection = false;
        intersectionPickedUp = null;
        trackPointPickedUp = null;
        trackPickedUp = null;
        locationPickedUp = null;
    }

    private void clearInspect() {
        startedRouteInspect = false;
        location1 = null;
        location2 = null;
    }

    private class KeyHandler implements KeyListener{
        public void keyTyped(KeyEvent e) {

        }

        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_1) {
                getContentPane().getComponent(0).setVisible(!getContentPane().getComponent(0).isVisible());
                repaint();
            }
        }

        public void keyReleased(KeyEvent e) {

        }
    }

    private Intersection selectIntersection(Point clickPoint) {
        for (Intersection intersection : map.getIntersections()) {
            if (distance(clickPoint, intersection.getPoint()) < minPickUpDistance) {
                return intersection;
            }
        }
        return null;
    }

    private java.util.List<Object> selectCloseTrackEnd(Point clickPoint) {
        for (Track track : map.getTracks()) {
            for (Point point : track.getPoints()) {
                if (distance(clickPoint, point) < minPickUpDistance) {
                    java.util.List<Object> list = new ArrayList<Object>();
                    list.add(track);
                    list.add(point);
                    return list;
                }
            }
        }
        return null;
    }

    private Track selectTrack(Point clickPoint) {
        return closestTrack(clickPoint, map.getTracks(), minPickUpDistance);
    }

    private Location selectLocation(Point clickPoint) {
        return closestLocation(clickPoint, map.getLocations(), minPickUpDistance);
    }

    private void pickUpLocationIntersectionTrackEnd(Point clickPoint) {
        System.out.println("Pickup");
        // Priority = Location > Intersection > Tracks

        Location location = selectLocation(clickPoint);
        if (location != null) {
            dropHeldLocationTrackIntersection();
            locationPickedUp = location;
            holdingLocationTrackIntersection = true;
            return;
        }

        Intersection intersection = selectIntersection(clickPoint);
        if (intersection != null) {
            dropHeldLocationTrackIntersection();
            intersectionPickedUp = intersection;
            holdingLocationTrackIntersection = true;
            return;
        }

        ArrayList<Object> trackAndPoint = (ArrayList<Object>) selectCloseTrackEnd(clickPoint);
        if (trackAndPoint != null) {
            dropHeldLocationTrackIntersection();
            trackPickedUp = (Track) trackAndPoint.get(0);
            trackPointPickedUp = (Point) trackAndPoint.get(1);
            holdingLocationTrackIntersection = true;
        }
    }

    private void removeLocation(Point clickPoint) {
        Location location = selectLocation(clickPoint);
        if (location != null) {
            map.removeLocation(location);
            repaint();
        }
    }

    private void removeTrack(Point clickPoint) {
        Track track = selectTrack(clickPoint);
        if (track != null) {
            map.removeTrack(track);
            repaint();
        }
    }

    private void removeIntersection(Point clickPoint) {
        Intersection intersection = selectIntersection(clickPoint);
        if (intersection != null) {
            map.removeIntersection(intersection);
            repaint();
        }
    }

    private void inspectTrack(Point clickPoint) {
        Track track = selectTrack(clickPoint);
        if (track != null && track.equals(selectedTrack)) {
            selectedTrack = null;
        } else if (track != null) {
            selectedTrack = track;
        } else selectedTrack = null;
        repaint();
    }

    private void inspectRoute(Point clickPoint) {
        Location location = selectLocation(clickPoint);
        if (!startedRouteInspect) {
            location1 = location;
        } else {
            location2 = location;
            repaint();
        }
        startedRouteInspect = !startedRouteInspect;
    }

    private void moveLocationIntersectionTrackEnd(Point clickPoint) {
        System.out.println("Move");
        // Finding a close existing point for each point in the new track
        // so that we can change the new destination to match (and thus make an intersection).
        ArrayList<Object> trackAndPoint = (ArrayList<Object>) selectCloseTrackEnd(clickPoint);
        if (trackAndPoint != null) {
            clickPoint = (Point) trackAndPoint.get(1);
        }
        if (locationPickedUp != null) {
            map.moveLocation(locationPickedUp, clickPoint);
        } else if (trackPickedUp != null && trackPointPickedUp != null) {
            map.moveTrack(trackPickedUp, trackPointPickedUp, clickPoint);
        } else {
            map.moveIntersection(intersectionPickedUp, clickPoint);
        }
        holdingLocationTrackIntersection = false;
        repaint();
    }

    private void createLocation(Point clickPoint) {
        System.out.println("Create Location");
        for (Location existingLocation : map.getLocations()) {
            if (distance(existingLocation.getPoint(), clickPoint) < minPickUpDistance) return;
        }
        Location location = new Location(clickPoint, "location");
        map.addLocation(location);
        repaint();
    }

    private void createTrack(Point clickPoint) {
        System.out.println("Create Track");
        if (!startedNewTrack) {
            newTrackPoint1 = clickPoint;
        } else {
            newTrackPoint2 = clickPoint;
            Track track = new Track(newTrackPoint1, newTrackPoint2);
            // Finding a close existing point for each point in the new track
            // so that we can change the coordinates to match (and thus make an intersection).
            ArrayList<Object> trackAndPoint1 = (ArrayList<Object>) selectCloseTrackEnd(newTrackPoint1);
            if (trackAndPoint1 != null) {
                Point closePoint = (Point) trackAndPoint1.get(1);
                track.move(newTrackPoint1, closePoint);
            }
            ArrayList<Object> trackAndPoint2 = (ArrayList<Object>) selectCloseTrackEnd(newTrackPoint2);
            if (trackAndPoint2 != null) {
                Point closePoint = (Point) trackAndPoint2.get(1);
                track.move(newTrackPoint2, closePoint);
            }
            map.addTrack(track);
            repaint();
        }
        startedNewTrack = !startedNewTrack;
    }

    private void breakTrack(Point clickPoint) {
        System.out.println("Break Track");
        Track closestTrack = closestTrack(clickPoint, map.getTracks(), minPickUpDistance);
        if (closestTrack != null) {
            // Not Perfect, we should ideally get the closestpoint to the clickpoint that is on the line
            // Currently if the click is not on the line the track will move slightly when broken.
            map.breakTrack(closestTrack, clickPoint);
            repaint();
        }
    }

    private void pickupOrMoveLocationTrackIntersection(Point clickPoint) {
        if (holdingLocationTrackIntersection) {
            moveLocationIntersectionTrackEnd(clickPoint);
        } else {
            pickUpLocationIntersectionTrackEnd(clickPoint);
        }
    }

    private void drawMap(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5));
        if (location2 != null) {
            drawRoute(location1, location2, g2);
        } else if (selectedTrack != null) {
            drawNextTracks(selectedTrack, g2);
        } else {
            drawNormal(g2);
        }
    }

    private void drawRoute(Location location1, Location location2, Graphics2D g2) {
        java.awt.Color lineColour;
        java.awt.Color locationColour;
        ArrayList<Track> fastestRoute = map.fastestRoute(location1, location2);
        for (Track track : map.getTracks()) {
            Line2D.Double line = trackToLine2D(track, this);
            if (fastestRoute.contains(track)) {
                lineColour = selectedTrackColour;
            } else lineColour = unconnectedTrackColour;

            g2.setColor(lineColour);
            g2.draw(line);
        }
        for (Location location : map.getLocations()) {
            if (location.equals(location1) || location.equals(location2)) {
                locationColour = selectedTrackColour;
            } else locationColour = unconnectedTrackColour;
            Rectangle2D.Double rectangle = locationToRect2D(location, 10d, this);
            g2.setColor(locationColour);
            g2.draw(rectangle);
        }
    }

    private void drawNextTracks(Track selectedTrack, Graphics2D g2) {
        java.awt.Color lineColour;
        for (Track track : map.getTracks()) {
            Line2D.Double line = trackToLine2D(track, this);
            if (track.equals(selectedTrack)) {
                lineColour = selectedTrackColour;
            } else if (selectedTrack.getActiveNextTracks().contains(track)) {
                lineColour = activeNextTrackColour;
            } else if (selectedTrack.getValidNextTracks().contains(track)) {
                lineColour = validNextTrackColour;
            } else if (selectedTrack.getConnectedTracks().contains(track)) {
                lineColour = connectedTrackColour;
            } else lineColour = unconnectedTrackColour;

            g2.setColor(lineColour);
            g2.draw(line);
        }
        for (Location location : map.getLocations()) {
            Rectangle2D.Double rectangle = locationToRect2D(location, 10d, this);
            g2.setColor(randomColor());
            g2.draw(rectangle);
        }
    }

    private void drawNormal(Graphics2D g2) {
        for (Track track : map.getTracks()) {
            Line2D.Double line = trackToLine2D(track, this);
            g2.setColor(randomColor());
            g2.draw(line);
        }
        for (Location location : map.getLocations()) {
            Rectangle2D.Double rectangle = locationToRect2D(location, 10d, this);
            g2.setColor(randomColor());
            g2.draw(rectangle);
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        drawMap(g);
    }

    public static void main (String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MapCreator().setVisible(true);
            }
        });
    }

}
