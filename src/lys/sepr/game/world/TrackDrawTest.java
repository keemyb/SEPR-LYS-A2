package lys.sepr.game.world;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Random;
import javax.swing.*;

import static lys.sepr.game.world.Utilities.clickPointToTrackPoint;
import static lys.sepr.game.world.Utilities.distance;
import static lys.sepr.game.world.Utilities.trackToLine2D;

/* y axis points have been inverted as the window coordinates start from the top left
where as the points start from the bottom left
*/

public class TrackDrawTest extends JFrame {

    public MouseHandler mouseHandler = new MouseHandler();
    public boolean drawing = false;
    public boolean pickupMode = true;
    public Intersection lastIntersectionPickedUp;
    public Track lastTrackPickedUp;
    public Point lastTrackPointPickedUp;
    public boolean trackPickedUpLast = false;

    public Track selectedTrack;
    public boolean inspectSelectedTrack = false;

    public double minPickUpDistance = 20;

    public Point newTrackPoint1;
    public Point newTrackPoint2;

    public Map map = new Map();
    Point startPoint1 = new Point(0,0);
    Point endPoint1 = new Point(100,100);

    Point startPoint2 = new Point(200,200);
    Point endPoint2 = new Point(100,100);

    Point startPoint3 = new Point(100,100);
    Point endPoint3 = new Point(200,100);

    Track track1 = new Track(startPoint1, endPoint1);
    Track track2 = new Track(startPoint2, endPoint2);
    Track track3 = new Track(startPoint3, endPoint3);

    java.awt.Color selectedTrackColour = Color.ORANGE;
    java.awt.Color activeNextTrackColour = Color.GREEN;
    java.awt.Color validNextTrackColour = Color.BLUE;
    java.awt.Color connectedTrackColour = Color.RED;
    java.awt.Color unconnectedTrackColour = Color.BLACK;

    JLabel instructions = new JLabel("<html>" +
            "This will probably be how we set up the track (with our map image behind as the guide)." +
            "<br>To make a new track click once to set the origin and again to set the destination" +
            "<br>To move a track or intersection, click whilst holding CTRL will pick it up," +
            "<br>pressing the mouse again with CTRL will move it." +
            "<br>Placing a new track in the vicinity of an existing track/intersection will merge the track(s)." +
            "<br>Moving an existing track in the vicinity of other tracks/intersections will merge the track(s) " +
            "<br>To inspect a track, shift click one of its ends." +
            "<br>Coming Soon:" +
            "<br>Track Inspector: The ability to select a track by any point along it" +
            "<br>Deleting tracks (Not implemented in GUI)" +
            "<br>CURVES! (Not Implemented at all)" +
            "</html>", SwingConstants.NORTH_EAST);

    JLabel selectedTrackLabel = new JLabel("Selected Track", SwingConstants.NORTH_EAST);
    JLabel activeNextTrackLabel = new JLabel("Active Next Track", SwingConstants.NORTH_EAST);
    JLabel validNextTrackLabel = new JLabel("Valid Next Track", SwingConstants.NORTH_EAST);
    JLabel connectedTrackLabel = new JLabel("Connected Track", SwingConstants.NORTH_EAST);
    JLabel unconnectedTrackLabel = new JLabel("Unconnected Track", SwingConstants.NORTH_EAST);

    TrackDrawTest() {
        super("Track Draw Test");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);

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

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            Point clickPoint = clickPointToTrackPoint(e.getPoint(), TrackDrawTest.this);

            int modifiers = e.getModifiers();
            if ((modifiers & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK) {
                selectTrack(clickPoint);
            } else if ((modifiers & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
                moveTrack(clickPoint);
            } else {
                createTrack(clickPoint);
            }
        }
    }

    private void pickUpIntersectionOrTrack(Point clickPoint) {
        // We look for intersection before tracks when we are looking for something to move.
        for (Intersection intersection : map.getIntersections()) {
            if (distance(clickPoint, intersection.getPoint()) < minPickUpDistance) {
                lastIntersectionPickedUp = intersection;
                pickupMode = false;
                trackPickedUpLast = false;
                return;
            }
        }

        for (Track track : map.getTracks()) {
            for (Point point : track.getPoints()){
                if (distance(clickPoint, point) < minPickUpDistance) {
                    lastTrackPickedUp = track;
                    lastTrackPointPickedUp = point;
                    pickupMode = false;
                    trackPickedUpLast = true;
                    return;
                }
            }
        }
    }

    private void putDownIntersectionOrTrack(Point clickPoint) {
        for (Track track : map.getTracks()) {
            for (Point point : track.getPoints()) {
                if (distance(clickPoint, point) < minPickUpDistance) {
                    clickPoint = point;
                }
            }
        }
        if (trackPickedUpLast) {
            map.moveTrack(lastTrackPickedUp, lastTrackPointPickedUp, clickPoint);
        } else {
            lastIntersectionPickedUp.move(clickPoint);
        }
        pickupMode = true;
        repaint();
    }

    private void createTrack(Point clickPoint) {
        if (!drawing) {
            newTrackPoint1 = clickPoint;
        } else {
            newTrackPoint2 = clickPoint;
            Track originalNewTrack = new Track(newTrackPoint1, newTrackPoint2);
            Track movedNewTrack = new Track(newTrackPoint1, newTrackPoint2);
            // Iterating over the original track so that we can change the coordinates
            // if we find a close track
            for (Track existingTrack : map.getTracks()) {
                for (Point newPoint : originalNewTrack.getPoints()){
                    for (Point existingPoint : existingTrack.getPoints()) {
                        if (distance(newPoint, existingPoint) < minPickUpDistance) {
                            movedNewTrack.move(newPoint, existingPoint);
                        }
                    }
                }
            }
            // Add the 'moved' track, whether it has been moved or not
            map.addTrack(movedNewTrack);
            repaint();
        }
        drawing = !drawing;
    }

    private void moveTrack(Point clickPoint) {
        if (pickupMode) {
            pickUpIntersectionOrTrack(clickPoint);
        } else {
            putDownIntersectionOrTrack(clickPoint);
        }
    }

    private void selectTrack(Point clickPoint) {
        // should find the track within an allowable distance of the click point
        // right now it only looks at end points
        for (Track track : map.getTracks()) {
            for (Point existingPoint : track.getPoints()) {
                if (distance(clickPoint, existingPoint) < minPickUpDistance) {
                    selectedTrack = track;
                    inspectSelectedTrack = true;
                    repaint();
                    return;
                }
            }
        }
    }

    private void drawLines(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5));
        if (inspectSelectedTrack) {
            drawNextTracks(selectedTrack, g2);
        } else {
            drawAllTracksNormal(g2);
        }
    }

    private void drawNextTracks(Track selectedTrack, Graphics2D g2) {
        java.awt.Color lineColour;
        for (Track track : map.getTracks()) {
            Line2D.Double line = trackToLine2D(track, this);
            if (track.equals(selectedTrack)) {
                lineColour = selectedTrackColour;
            } else if (selectedTrack.getNextTracks().contains(track)) {
                lineColour = activeNextTrackColour;
            } else if (selectedTrack.getValidNextTracks().contains(track)) {
                lineColour = validNextTrackColour;
            } else if (selectedTrack.getConnectedTracks().contains(track)) {
                lineColour = connectedTrackColour;
            } else lineColour = unconnectedTrackColour;

            g2.setColor(lineColour);
            g2.draw(line);
        }
        inspectSelectedTrack = false;
    }

    private void drawAllTracksNormal(Graphics2D g2) {
        for (Track track : map.getTracks()) {
            Line2D.Double line = trackToLine2D(track, this);
            Random r = new Random();
            int rgb = Color.HSBtoRGB(r.nextFloat(),0.5f,0.5f);
            g2.setColor(new Color(rgb));
            g2.draw(line);
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        drawLines(g);
    }

    public static void main (String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TrackDrawTest().setVisible(true);
            }
        });
    }

}