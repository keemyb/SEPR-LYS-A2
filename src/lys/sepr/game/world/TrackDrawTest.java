package lys.sepr.game.world;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

import static lys.sepr.game.world.Utilities.*;

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
            "<br>Moving an existing track/intersection in the vicinity of other tracks/intersections will merge them" +
            "<br>To break up an intersection, click it whilst holding ALT" +
            "<br>To inspect a track, click it whilst holding SHIFT." +
            "<br>Coming Soon:" +
            "<br>Deleting tracks (Not implemented in GUI)" +
            "<br>CURVES! (Not Implemented at all)" +
            "</html>", SwingConstants.LEFT);

    JLabel selectedTrackLabel = new JLabel("Selected Track", SwingConstants.LEFT);
    JLabel activeNextTrackLabel = new JLabel("Active Next Track", SwingConstants.LEFT);
    JLabel validNextTrackLabel = new JLabel("Valid Next Track", SwingConstants.LEFT);
    JLabel connectedTrackLabel = new JLabel("Connected (Non traversable) Track", SwingConstants.LEFT);
    JLabel unconnectedTrackLabel = new JLabel("Unconnected Track", SwingConstants.LEFT);

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
            } else if ((modifiers & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK) {
                removeIntersection(clickPoint);
            } else {
                createTrack(clickPoint);
            }
        }
    }

    private void removeIntersection(Point clickPoint) {
        // We look for intersection before tracks when we are looking for something to move.
        for (Intersection intersection : map.getIntersections()) {
            if (distance(clickPoint, intersection.getPoint()) < minPickUpDistance) {
                map.removeIntersection(intersection);
                repaint();
                return;
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
            map.moveIntersection(lastIntersectionPickedUp, clickPoint);
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
        // With help from http://doswa.com/2009/07/13/circle-segment-intersectioncollision.html
        for (Track track : map.getTracks()) {
            Point trackPoint1 = track.getPoints().get(0);
            Point trackPoint2 = track.getPoints().get(1);
            ArrayList<Double> trackVector = getVector(trackPoint1, trackPoint2);
            ArrayList<Double> unitTrackVector = unitVector(trackVector);
            ArrayList<Double> trackPointToClickPointVector = getVector(trackPoint1, clickPoint);
            double lengthProjectedVector = dotProduct(trackPointToClickPointVector, unitTrackVector);
            ArrayList<Double> projectedVector = multiply(unitTrackVector,lengthProjectedVector);
            ArrayList<Double> closestPoint = new ArrayList<Double>();
            if (lengthProjectedVector < 0) {
                closestPoint.add(trackPoint1.getX());
                closestPoint.add(trackPoint1.getY());
            } else if (lengthProjectedVector > magnitude(trackVector)) {
                closestPoint.add(trackPoint2.getX());
                closestPoint.add(trackPoint2.getY());
            } else {
                closestPoint.add(trackPoint1.getX()+projectedVector.get(0));
                closestPoint.add(trackPoint1.getY()+projectedVector.get(1));
            }
            double distance = magnitude(getVector(new Point(closestPoint.get(0), closestPoint.get(1)) , clickPoint));
            if (distance < minPickUpDistance) {
                selectedTrack = track;
                inspectSelectedTrack = true;
                repaint();
                return;
            }
            }

// Only picks up track ends
//        for (Track track : map.getTracks()) {
//            for (Point existingPoint : track.getPoints()) {
//                if (distance(clickPoint, existingPoint) < minPickUpDistance) {
//                    selectedTrack = track;
//                    inspectSelectedTrack = true;
//                    repaint();
//                    return;
//                }
//            }
//        }
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