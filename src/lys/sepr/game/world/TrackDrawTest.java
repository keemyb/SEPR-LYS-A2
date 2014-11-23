package lys.sepr.game.world;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Random;
import javax.swing.*;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import static lys.sepr.game.world.Utilities.clickPointToTrackPoint;
import static lys.sepr.game.world.Utilities.distance;

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

    public double pickupSensitivity = 20;

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

    JLabel instructions = new JLabel("<html>" +
            "This will probably be how we set up the track (with our map image behind as the guide)." +
            "<br>To make a new track click once to set the origin and again to set the destination" +
            "<br>To move a track or intersection, click whilst holding CTRL will pick it up," +
            "<br>pressing the mouse again with CTRL will move it." +
            "<br>Placing a new track in the vicinity of an existing track/intersection will merge the track(s)." +
            "<br>Moving an existing track in the vicinity of other tracks/intersections will merge the track(s) " +
            "<br>Coming Soon:" +
            "<br>CURVES! (Not Implemented)" +
            "<br>Displaying which tracks can be traversed from another (Not Implemented in the GUI)" +
            "</html>", SwingConstants.NORTH_EAST);

    TrackDrawTest() {
        super("Track Draw Test");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);

        getContentPane().add(instructions, BorderLayout.NORTH);

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            Point clickPoint = clickPointToTrackPoint(e.getPoint(), TrackDrawTest.this);

            int modifiers = e.getModifiers();
            if ((modifiers & ActionEvent.CTRL_MASK) ==ActionEvent.CTRL_MASK) {
                if (pickupMode) {
                    // We look for intersection before tracks when we are looking for something to move.
                    for (Intersection intersection : map.getIntersections()) {
                        if (distance(clickPoint, intersection.getPoint()) < pickupSensitivity) {
                            lastIntersectionPickedUp = intersection;
                            pickupMode = false;
                            trackPickedUpLast = false;
                            return;
                        }
                    }

                    for (Track track : map.getTracks()) {
                        for (Point point : track.getPoints()){
                            if (distance(clickPoint, point) < pickupSensitivity) {
                                lastTrackPickedUp = track;
                                lastTrackPointPickedUp = point;
                                pickupMode = false;
                                trackPickedUpLast = true;
                                return;
                            }
                        }
                    }
                    // we are still in pickup mode if there was no track or intersection found
                    pickupMode = true;

                } else {
                    for (Track track : map.getTracks()) {
                        for (Point point : track.getPoints()) {
                            if (distance(clickPoint, point) < pickupSensitivity) {
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


            } else {
                if (!drawing) {
                    newTrackPoint1 = clickPoint;
                } else {
                    newTrackPoint2 = clickPoint;
                    Track originalNewTrack = new Track(newTrackPoint1, newTrackPoint2);
                    Track movedNewTrack = new Track(newTrackPoint1, newTrackPoint2);
                    for (Track existingTrack : map.getTracks()) {
                        for (Point newPoint : originalNewTrack.getPoints()){
                            for (Point existingPoint : existingTrack.getPoints()) {
                                if (distance(newPoint, existingPoint) < pickupSensitivity) {
                                    movedNewTrack.move(newPoint, existingPoint);
                                }
                            }
                        }
                    }
                    map.addTrack(movedNewTrack);
                    repaint();
                }
                drawing = !drawing;
            }
        }
    }

    void drawLines(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        for (Track track : map.getTracks()) {
            Line2D.Double line = new Line2D.Double(
                    track.getPoints().get(0).getX(),
                    getSize().getHeight() - track.getPoints().get(0).getY(),
                    track.getPoints().get(1).getX(),
                    getSize().getHeight() - track.getPoints().get(1).getY());
            Random r = new Random();
            g2.setColor(new Color(r.nextInt(256),r.nextInt(256),r.nextInt(256)));
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