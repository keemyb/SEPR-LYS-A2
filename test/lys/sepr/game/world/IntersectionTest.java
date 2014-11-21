package lys.sepr.game.world;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class IntersectionTest {

    private Track track1;
    private Track track2;
    private Track track3;

    private Intersection intersection;

    @Before
    public void setUp() throws Exception {
        Point startPoint1 = new Point(0,0);
        Point endPoint1 = new Point(100,100);

        Point startPoint2 = new Point(200,200);
        Point endPoint2 = new Point(100,100);

        Point startPoint3 = new Point(100,100);
        Point endPoint3 = new Point(200,100);

        this.track1 = new Track(startPoint1, endPoint1);
        this.track2 = new Track(startPoint2, endPoint2);
        this.track3 = new Track(startPoint3, endPoint3);

        /*
            +-----------------------------+
            |                             |
            | Test Track        X Track 2 |
            | Locations        X          |
            | (Approx)        X           |
            |                X            |
            | Intersection  OXXXX         |
            |              X    Track 3   |
            |             X               |
            |            X                |
            |   Track 1 X                 |
            |                             |
            +-----------------------------+
         */

    }

    @Test
    public void testGetTracks() throws Exception {
        ArrayList<Track> expectedTracks = new ArrayList<Track>();
        expectedTracks.add(track1);
        expectedTracks.add(track2);

        this.intersection = new Intersection(new Point(100,100), track1, track2);

        assertEquals(expectedTracks, intersection.getTracks());
    }

    @Test
    public void testCrossProduct() throws Exception {
        this.intersection = new Intersection(new Point(100,100), track1, track2);
        ArrayList<Double> vector1 = track1.getVector(intersection.getPoint());
        ArrayList<Double> vector2 = track2.getVector(intersection.getPoint());

        assertEquals(0, intersection.crossProduct(vector1, vector2)%180, 0.0d);

    }

    public void setUp3Tracks() {
        this.intersection = new Intersection(new Point(100,100), track1, track2);
        intersection.addTrack(track3);
    }

    @Test
    public void testValidNextTracksDuo() throws Exception {
        ArrayList<Track> expectedTracks = new ArrayList<Track>();
        expectedTracks.add(track2);

        this.intersection = new Intersection(new Point(100,100), track1, track2);

        assertEquals(expectedTracks, intersection.getValidNextTracks(track1));
    }

    @Test
    public void testValidNextTracksTrio() throws Exception {
        ArrayList<Track> expectedTracks1 = new ArrayList<Track>();
        expectedTracks1.add(track2);
        expectedTracks1.add(track3);

        ArrayList<Track> expectedTracks2 = new ArrayList<Track>();
        expectedTracks2.add(track1);

        setUp3Tracks();

//        ArrayList<Double> vector1 = track1.getVector(new Point(100,100));
//        ArrayList<Double> vector2 = track2.getVector(new Point(100,100));
//        double angle = intersection.crossProduct(vector1, vector2);
//        System.out.println(angle);
//
//        ArrayList<Double> vector3 = track2.getVector(new Point(100,100));
//        ArrayList<Double> vector4 = track3.getVector(new Point(100,100));
//        double angle2 = intersection.crossProduct(vector3, vector4);
//        System.out.println(angle2);
//
//        ArrayList<Double> vector5 = track1.getVector(new Point(100,100));
//        ArrayList<Double> vector6 = track3.getVector(new Point(100,100));
//        double angle3 = intersection.crossProduct(vector5, vector6);
//        System.out.println(angle3);

        assertEquals(expectedTracks1, intersection.getValidNextTracks(track1));
        assertEquals(expectedTracks2, intersection.getValidNextTracks(track2));
        assertEquals(expectedTracks2, intersection.getValidNextTracks(track3));
    }

    @Test
    public void testSetNextTrackValidChoice() throws Exception {
        setUp3Tracks();

        track1.setNextTrack(intersection, track3);

        assertEquals(track3, track1.getNextTrack(new Point(0,0)));

        track1.setNextTrack(intersection, track2);

        assertEquals(track2, track1.getNextTrack(new Point(0,0)));
    }

    @Test
    public void testSetNextTrackInvalidChoice() throws Exception {
        setUp3Tracks();

        track2.setNextTrack(intersection, track3);

        assertEquals(null, track2.getNextTrack(new Point(0,0)));
    }

    @Test
    public void testNextTrackAvailableTrack() throws Exception {
        this.intersection = new Intersection(new Point(100,100), track1, track2);

        assertEquals(track2, track1.getNextTrack(new Point(0,0)));
    }

    @Test
    public void testNextTrackNonAvailableTrack() throws Exception {
        this.intersection = new Intersection(new Point(100,100), track2, track3);

        assertEquals(null, track2.getNextTrack(new Point(200,200)));
    }

    @Test
    public void testNextTrackMultipleAvailableTracks() throws Exception {
        setUp3Tracks();

        /* Favour the first track added (that can connect)
        We don't want to break an existing connection when adding new tracks.
         */
        assertEquals(track2, track1.getNextTrack(new Point(0,0)));
    }

}