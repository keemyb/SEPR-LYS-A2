package lys.sepr.game.world;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MapTest {

    private Map map = new Map();
    private Track track1;
    private Track track2;
    private Track track3;

    @Before
    public void setUp() throws Exception {
        Point startPoint1 = new Point(0, 0);
        Point endPoint1 = new Point(100, 100);

        Point startPoint2 = new Point(200, 200);
        Point endPoint2 = new Point(100, 100);

        Point startPoint3 = new Point(100, 100);
        Point endPoint3 = new Point(200, 100);

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
    public void testAddTrack() throws Exception {
        ArrayList<Track> expectedTracks = new ArrayList<Track>();
        expectedTracks.add(track1);

        map.addTrack(track1);

        assertEquals(expectedTracks, map.getTracks());
    }

    @Test
    public void moveTrackCreateNewIntersection() throws Exception {
        Track track4 = new Track(new Point(0, 100), new Point(90, 100));

        ArrayList<Track> expectedTracks = new ArrayList<Track>();
        expectedTracks.add(track1);
        expectedTracks.add(track4);

        map.addTrack(track1);
        map.addTrack(track4);

        map.moveTrack(track4, new Point(90, 100), new Point(100, 100));

        assertEquals(expectedTracks, map.getIntersections().get(0).getTracks());
    }

    @Test
    public void moveTrackExistingIntersection() throws Exception {
        Track track4 = new Track(new Point(0, 100), new Point(90, 100));

        ArrayList<Track> expectedTracks = new ArrayList<Track>();
        expectedTracks.add(track1);
        expectedTracks.add(track2);
        expectedTracks.add(track4);

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track4);

        map.moveTrack(track4, new Point(90, 100), new Point(100, 100));

        assertEquals(expectedTracks, map.getIntersections().get(0).getTracks());
    }

    @Test
    public void moveIntersectionConsumeSoloTrack() throws Exception {
        Track track4 = new Track(new Point(0, 100), new Point(90, 100));

        ArrayList<Track> expectedTracks = new ArrayList<Track>();
        expectedTracks.add(track1);
        expectedTracks.add(track2);
        expectedTracks.add(track3);
        expectedTracks.add(track4);

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
        map.addTrack(track4);

        Intersection intersection = map.getIntersections().get(0);

        map.moveIntersection(intersection, new Point(90, 100));

        assertEquals(expectedTracks, intersection.getTracks());
    }

    @Test
    public void moveIntersectionMergeIntersection() throws Exception {
        Track track4 = new Track(new Point(0, 100), new Point(90, 100));
        Track track5 = new Track(new Point(0, 200), new Point(90, 100));

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
        map.addTrack(track4);
        map.addTrack(track5);

        Intersection firstIntersection = map.getIntersections().get(0);
        Intersection secondIntersection = map.getIntersections().get(1);

        map.moveIntersection(firstIntersection, new Point(90, 100));

        assertTrue(secondIntersection.getTracks().contains(track1));
        assertTrue(secondIntersection.getTracks().contains(track2));
        assertTrue(secondIntersection.getTracks().contains(track3));
        assertTrue(secondIntersection.getTracks().contains(track4));
        assertTrue(secondIntersection.getTracks().contains(track5));
        // second is the master as it remained in it's original location.
        assertTrue(map.getIntersections().contains(secondIntersection));
        assertEquals(1, map.getIntersections().size());
    }

    @Test
    public void removeIntersection() throws Exception {
        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);

        Intersection intersection = map.getIntersections().get(0);

        map.removeIntersection(intersection);

        assertEquals(0, map.getIntersections().size());
        assertEquals(0, track1.getIntersections().size());
        assertEquals(0, track2.getIntersections().size());
        assertEquals(0, track3.getIntersections().size());
    }

    @Test
    public void removeTrack() throws Exception {
        Track track4 = new Track(new Point(0, 100), new Point(90, 100));

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
        map.addTrack(track4);

        Intersection intersection = map.getIntersections().get(0);

        map.removeTrack(track2);
        map.removeTrack(track4);

        assertEquals(2, map.getTracks().size());
    }

    @Test
    public void testMoveIntersectionNoNewIntersectionCreated() throws Exception {
        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);

        Intersection intersection = map.getIntersections().get(0);

        intersection.move(new Point(300, 300));

        assertEquals(1, map.getIntersections().size());
        assertEquals(intersection, track1.getIntersections().get(0));
        assertEquals(1, track1.getIntersections().size());
    }

    @Test
    public void removeFinalTrack() throws Exception {
        map.addTrack(track1);
        map.addTrack(track2);

        map.removeTrack(track1);
        map.removeTrack(track2);

        assertEquals(0, map.getIntersections().size());
    }
}