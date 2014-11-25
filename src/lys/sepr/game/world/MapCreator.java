package lys.sepr.game.world;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.*;

import javax.swing.*;

import static lys.sepr.game.world.Utilities.*;

public class MapCreator extends JFrame {

	public static final int INSPECT_TRACK_MODE = 0;
	public static final int MOVE_TRACK_MODE = 1;
	public static final int DELETE_TRACK_MODE = 2;
	public static final int DELETE_INTERSECTION_MODE = 3;
	public static final int CREATE_TRACK_MODE = 4;
	
	public int mode = INSPECT_TRACK_MODE;
	
    public MouseHandler mouseHandler = new MouseHandler();
    public KeyHandler keyHandler = new KeyHandler();
    public double minPickUpDistance = 20;

    public Track selectedTrack;

    public boolean startedNewTrack = false;
    public Point newTrackPoint1;
    public Point newTrackPoint2;

    public boolean holdingTrackOrIntersection = false;
    public Intersection intersectionPickedUp;
    public Point trackPointPickedUp;
    public Track trackPickedUp;

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
            "Press 1 to hide/show me" +
            "<br>This will probably be how we set up the track (with our map image behind as the guide)." +
            "<br>Coming Soon:" +
            "<br>CURVES! (Not Implemented at all)" +
            "</html>", SwingConstants.LEFT);

    JLabel selectedTrackLabel = new JLabel("Selected Track", SwingConstants.LEFT);
    JLabel activeNextTrackLabel = new JLabel("Active Next Track", SwingConstants.LEFT);
    JLabel validNextTrackLabel = new JLabel("Valid Next Track", SwingConstants.LEFT);
    JLabel connectedTrackLabel = new JLabel("Connected (Non traversable) Track", SwingConstants.LEFT);
    JLabel unconnectedTrackLabel = new JLabel("Unconnected Track", SwingConstants.LEFT);

    JRadioButton createTrackModeButton = new JRadioButton("Create Track");
    JRadioButton moveTrackModeButton = new JRadioButton("Move Track");
    JRadioButton inspectTrackModeButton = new JRadioButton("Inspect Track");
    JRadioButton deleteTrackModeButton = new JRadioButton("Delete Track");
    JRadioButton deleteIntersectionModeButton = new JRadioButton("Delete Intersection");
    
    ButtonGroup modeButtons = new ButtonGroup();
    
    MapCreator() {
        super("Map Creator");
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
        modeButtons.add(moveTrackModeButton);
        modeButtons.add(deleteTrackModeButton);
        modeButtons.add(deleteIntersectionModeButton);
        
        inspectTrackModeButton.setSelected(true);
        
        createTrackModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearMove();
				mode = CREATE_TRACK_MODE;
			}	
        });
        
        moveTrackModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearCreateNew();
				mode = MOVE_TRACK_MODE;
			}	
        });
        
        inspectTrackModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearCreateNew();
				clearMove();
				mode = INSPECT_TRACK_MODE;
			}	
        });
        
       deleteTrackModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearCreateNew();
				clearMove();
				mode = DELETE_TRACK_MODE;
			}	
        });
       
       deleteIntersectionModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearCreateNew();
				clearMove();
				mode = DELETE_INTERSECTION_MODE;
			}	
       });
       
       JPanel buttonPanel = new JPanel();
       
       buttonPanel.add(inspectTrackModeButton);
       buttonPanel.add(createTrackModeButton);
       buttonPanel.add(moveTrackModeButton);
       buttonPanel.add(deleteTrackModeButton);
       buttonPanel.add(deleteIntersectionModeButton);
       
       buttonPanel.setSize(1280, 100);
       
       getContentPane().add(buttonPanel, BorderLayout.SOUTH);
       
        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
    }

    private class MouseHandler extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            Point clickPoint = clickPointToTrackPoint(e.getPoint(), MapCreator.this);
            switch(mode) {
            case CREATE_TRACK_MODE:
            	createTrack(clickPoint);
            	break;
            case INSPECT_TRACK_MODE:
            	inspectTrack(clickPoint);
            	break;
            case MOVE_TRACK_MODE:
            	moveTrack(clickPoint);
            	break;
            case DELETE_TRACK_MODE:
            	removeTrack(clickPoint);
            	break;
            case DELETE_INTERSECTION_MODE:
            	removeIntersection(clickPoint);
            	break;
            }
        }
    }
    
    private void clearCreateNew() {
    	startedNewTrack = false;
    	newTrackPoint1 = null;
    	newTrackPoint2 = null;
    }
    
    private void clearMove() {
    	holdingTrackOrIntersection = false;
    	intersectionPickedUp = null;
    	trackPointPickedUp = null;
    	trackPickedUp = null;
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

    private java.util.List<Object> selectTrackEnd(Point clickPoint) {
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
                return track;
            }
        }
        return null;
    }

    private void selectIntersectionOrTrackEnd(Point clickPoint) {
        // We look for intersection before tracks when we are looking for something to move.
        Intersection intersection = selectIntersection(clickPoint);
        if (intersection != null) {
            intersectionPickedUp = intersection;
            trackPickedUp = null;
            holdingTrackOrIntersection = true;
            return;
        }

        ArrayList<Object> trackAndPoint = (ArrayList<Object>) selectTrackEnd(clickPoint);
        if (trackAndPoint != null) {
            trackPickedUp = (Track) trackAndPoint.get(0);
            trackPointPickedUp = (Point) trackAndPoint.get(1);
            intersectionPickedUp = null;
            holdingTrackOrIntersection = true;
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

    private void moveSelectedIntersectionOrTrackEnd(Point clickPoint) {
        // Finding a close existing point for each point in the new track
        // so that we can change the new destination to match (and thus make an intersection).
        ArrayList<Object> trackAndPoint = (ArrayList<Object>) selectTrackEnd(clickPoint);
        if (trackAndPoint != null) {
            clickPoint = (Point) trackAndPoint.get(1);
        }
        if (trackPickedUp != null && trackPointPickedUp != null) {
            map.moveTrack(trackPickedUp, trackPointPickedUp, clickPoint);
        } else {
            map.moveIntersection(intersectionPickedUp, clickPoint);
        }
        holdingTrackOrIntersection = false;
        repaint();
    }

    private void createTrack(Point clickPoint) {
        if (!startedNewTrack) {
            newTrackPoint1 = clickPoint;
        } else {
            newTrackPoint2 = clickPoint;
            Track track = new Track(newTrackPoint1, newTrackPoint2);
            // Finding a close existing point for each point in the new track
            // so that we can change the coordinates to match (and thus make an intersection).
            ArrayList<Object> trackAndPoint1 = (ArrayList<Object>) selectTrackEnd(newTrackPoint1);
            if (trackAndPoint1 != null) {
                Point closePoint = (Point) trackAndPoint1.get(1);
                track.move(newTrackPoint1, closePoint);
            }
            ArrayList<Object> trackAndPoint2 = (ArrayList<Object>) selectTrackEnd(newTrackPoint2);
            if (trackAndPoint2 != null) {
                Point closePoint = (Point) trackAndPoint2.get(1);
                track.move(newTrackPoint2, closePoint);
            }
            map.addTrack(track);
            repaint();
        }
        startedNewTrack = !startedNewTrack;
    }

    private void moveTrack(Point clickPoint) {
        if (holdingTrackOrIntersection) {
            moveSelectedIntersectionOrTrackEnd(clickPoint);
        } else {
            selectIntersectionOrTrackEnd(clickPoint);
        }
    }

    private void drawLines(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5));
        if (selectedTrack != null) {
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
                new MapCreator().setVisible(true);
            }
        });
    }

}