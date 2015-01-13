package lys.sepr.game.world;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    public void testMoveTrackCreateNewIntersection() throws Exception {
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
    public void testMoveTrackExistingIntersection() throws Exception {
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
    public void testMoveIntersectionConsumeSoloTrack() throws Exception {
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
    public void testMoveIntersectionMergeIntersection() throws Exception {
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
    public void testRemoveIntersection() throws Exception {
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
    public void testRemoveTrack() throws Exception {
        Track track4 = new Track(new Point(0, 100), new Point(90, 100));

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
        map.addTrack(track4);

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
    public void testRemoveFinalTrack() throws Exception {
        map.addTrack(track1);
        map.addTrack(track2);

        map.removeTrack(track1);
        map.removeTrack(track2);

        assertEquals(0, map.getIntersections().size());
    }

    @Test
    public void testAddTrackInTwoIntersectionsAllConnections() throws Exception {
        Track track1 = new Track(new Point(0,0), new Point(100,0));
        Track track2 = new Track(new Point(100,0), new Point(200, 10));
        Track track3 = new Track(new Point(200,10), new Point(300,0));
        Track track4 = new Track(new Point(300,0), new Point(400,0));
        Track track5 = new Track(new Point(100,0), new Point(300,0));

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
        map.addTrack(track4);
        map.addTrack(track5);
        // Trying to trigger odd bug, when adding a new track breaks intersections
        map.addTrack(new Track(new Point(1,1), new Point(2,2)));

        Set<Track> expectedConnections1 = new HashSet<Track>();
        expectedConnections1.add(track2);
        expectedConnections1.add(track5);

        Set<Track> expectedConnections2 = new HashSet<Track>();
        expectedConnections2.add(track1);
        expectedConnections2.add(track3);
        expectedConnections2.add(track5);

        Set<Track> expectedConnections3 = new HashSet<Track>();
        expectedConnections3.add(track2);
        expectedConnections3.add(track4);
        expectedConnections3.add(track5);

        Set<Track> expectedConnections4 = new HashSet<Track>();
        expectedConnections4.add(track3);
        expectedConnections4.add(track5);

        Set<Track> expectedConnections5 = new HashSet<Track>();
        expectedConnections5.add(track1);
        expectedConnections5.add(track2);
        expectedConnections5.add(track3);
        expectedConnections5.add(track4);

        assertEquals(3, map.getIntersections().size());
        assertEquals(expectedConnections1, new HashSet(track1.getAllConnectedTracks()));
        assertEquals(expectedConnections2, new HashSet(track2.getAllConnectedTracks()));
        assertEquals(expectedConnections3, new HashSet(track3.getAllConnectedTracks()));
        assertEquals(expectedConnections4, new HashSet(track4.getAllConnectedTracks()));
        assertEquals(expectedConnections5, new HashSet(track5.getAllConnectedTracks()));
    }


    @Test
    public void testAddTrackInTwoIntersectionsValidConnections() throws Exception {
        Track track1 = new Track(new Point(0,0), new Point(100,0));
        Track track2 = new Track(new Point(100,0), new Point(200, 10));
        Track track3 = new Track(new Point(200,10), new Point(300,0));
        Track track4 = new Track(new Point(300,0), new Point(400,0));
        Track track5 = new Track(new Point(100,0), new Point(300,0));

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
        map.addTrack(track4);
        map.addTrack(track5);
        // Trying to trigger odd bug, when adding a new track breaks intersections
        map.addTrack(new Track(new Point(1,1), new Point(2,2)));

        Set<Track> expectedValidConnections1 = new HashSet<Track>();
        expectedValidConnections1.add(track2);
        expectedValidConnections1.add(track5);

        Set<Track> expectedValidConnections2 = new HashSet<Track>();
        expectedValidConnections2.add(track1);
        expectedValidConnections2.add(track3);

        Set<Track> expectedValidConnections3 = new HashSet<Track>();
        expectedValidConnections3.add(track2);
        expectedValidConnections3.add(track4);

        Set<Track> expectedValidConnections4 = new HashSet<Track>();
        expectedValidConnections4.add(track3);
        expectedValidConnections4.add(track5);

        Set<Track> expectedValidConnections5 = new HashSet<Track>();
        expectedValidConnections5.add(track1);
        expectedValidConnections5.add(track4);

        assertEquals(3, map.getIntersections().size());
        assertEquals(expectedValidConnections1, new HashSet(track1.getValidConnections()));
        assertEquals(expectedValidConnections2, new HashSet(track2.getValidConnections()));
        assertEquals(expectedValidConnections3, new HashSet(track3.getValidConnections()));
        assertEquals(expectedValidConnections4, new HashSet(track4.getValidConnections()));
        assertEquals(expectedValidConnections5, new HashSet(track5.getValidConnections()));
    }

    @Test
    public void testBreakTrackSolo() throws Exception {
        Track track1 = new Track(new Point(0,0), new Point(100,100));
        Track splitTrack1 = new Track(new Point(0, 0), new Point(50, 50));
        Track splitTrack2 = new Track(new Point(50, 50), new Point(100, 100));

        map.addTrack(track1);

        map.breakTrack(track1, new Point(50,50));

        Intersection intersection = map.getIntersections().get(0);
        // The ones created above aren't the same as the ones in the map
        Track newSplitTrack1 = map.getTracks().get(map.getTracks().indexOf(splitTrack1));
        Track newSplitTrack2 = map.getTracks().get(map.getTracks().indexOf(splitTrack2));

        assertEquals(2, map.getTracks().size());
        assertEquals(1, map.getIntersections().size());
        assertTrue(map.getTracks().contains(splitTrack1));
        assertTrue(map.getTracks().contains(splitTrack2));
        assertTrue(intersection.getTracks().contains(splitTrack1));
        assertTrue(intersection.getTracks().contains(splitTrack2));
        assertTrue(newSplitTrack1.getAllConnectedTracks().contains(newSplitTrack2));
        assertTrue(newSplitTrack2.getAllConnectedTracks().contains(newSplitTrack1));
    }

    @Test
    public void testBreakTrackInOneIntersection() throws Exception {
        map.addTrack(track1);
        map.addTrack(track2);

        Track splitTrack1 = new Track(new Point(0, 0), new Point(50, 50));
        Track splitTrack2 = new Track(new Point(50, 50), new Point(100, 100)); // The part connected to the intersection

        map.breakTrack(track1, new Point(50,50));

        assertEquals(2, map.getIntersections().size());

        for (Intersection intersection : map.getIntersections()) {
            assertEquals(2, intersection.getTracks().size());
            if (intersection.getTracks().contains(splitTrack1)) {
                assertFalse(intersection.getTracks().contains(track2));
            } else if (intersection.getTracks().contains(track2)){
                assertFalse(intersection.getTracks().contains(splitTrack1));
            }
        }

        // The ones created above aren't the same as the ones in the map
        Track newSplitTrack2 = map.getTracks().get(map.getTracks().indexOf(splitTrack2));

        assertTrue(newSplitTrack2.getAllConnectedTracks().contains(track2));
        assertTrue(track2.getAllConnectedTracks().contains(newSplitTrack2));
    }

    @Test
    public void testBreakTrackInTwoIntersections() throws Exception {
        Track track3 = new Track(new Point(200, 200), new Point(300, 300));

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);

        Track splitTrack1 = new Track(new Point(100, 100), new Point(150, 150));
        Track splitTrack2 = new Track(new Point(150, 150), new Point(200, 200));

        map.breakTrack(track2, new Point(150, 150));

        assertEquals(3, map.getIntersections().size());

        for (Intersection intersection : map.getIntersections()) {
            if (intersection.getTracks().contains(splitTrack1)
                    && !intersection.getTracks().contains(splitTrack2)) {
                assertTrue(intersection.getTracks().contains(track1));
            } else if (intersection.getTracks().contains(splitTrack2)
                        && !intersection.getTracks().contains(splitTrack1)) {
                assertTrue(intersection.getTracks().contains(track3));
            }
        }

        // The ones created above aren't the same as the ones in the map
        Track newSplitTrack1 = map.getTracks().get(map.getTracks().indexOf(splitTrack1));
        Track newSplitTrack2 = map.getTracks().get(map.getTracks().indexOf(splitTrack2));

        assertFalse(map.getTracks().contains(track2));
        assertTrue(track1.getAllConnectedTracks().contains(newSplitTrack1));
        assertTrue(track3.getAllConnectedTracks().contains(newSplitTrack2));
    }
}
