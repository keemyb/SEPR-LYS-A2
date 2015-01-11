package lys.sepr.game.world;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static lys.sepr.game.world.Utilities.getVector;
import static org.junit.Assert.*;

public class TrackTest {

    private Map map = new Map();
    private Track track1;
    private Track track2;
    private Track track3;

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
    public void testConnectedTrackSolo() throws Exception {
        assertEquals(0, track1.getConnectedTracks().size());
        assertEquals(null, track1.getActiveConnectedTrackComingFrom(new Point(0, 0)));
        assertEquals(null, track1.getActiveConnectedTrackTowards(new Point(0, 0)));
    }

    @Test
    public void testConnectedTrackDuo() throws Exception {
        map.addTrack(track1);
        map.addTrack(track2);

        assertEquals(1, track1.getConnectedTracks().size());
        assertEquals(1, track2.getConnectedTracks().size());

        assertEquals(track2, track1.getActiveConnectedTrackComingFrom(new Point(0, 0)));
        assertEquals(null, track1.getActiveConnectedTrackComingFrom(new Point(100, 100)));

        assertEquals(track2, track1.getActiveConnectedTrackTowards(new Point(100, 100)));
        assertEquals(null, track1.getActiveConnectedTrackTowards(new Point(0, 0)));

        assertEquals(track1, track2.getActiveConnectedTrackTowards(new Point(100, 100)));
        assertEquals(null, track2.getActiveConnectedTrackTowards(new Point(200, 200)));
    }

    @Test
    public void testConnectedTrackTrio() throws Exception {
        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);

        assertEquals(2, track1.getConnectedTracks().size());
        assertEquals(2, track2.getConnectedTracks().size());
        assertEquals(2, track3.getConnectedTracks().size());

        assertEquals(track2, track1.getActiveConnectedTrackComingFrom(new Point(0, 0)));
        assertEquals(track1, track2.getActiveConnectedTrackComingFrom(new Point(200, 200)));
        assertEquals(null, track3.getActiveConnectedTrackComingFrom(new Point(200, 100)));
    }

    @Test
    public void testGetVector() throws Exception {
        ArrayList<Double> expectedVector1 = new ArrayList<Double>();
        expectedVector1.add(100d);
        expectedVector1.add(100d);

        ArrayList<Double> expectedVector2 = new ArrayList<Double>();
        expectedVector2.add(-100d);
        expectedVector2.add(0d);

        Point point = new Point(100,100);

        assertEquals(expectedVector1, getVector(track1.getOtherPoint(point), point));
        assertEquals(expectedVector2, getVector(track3.getOtherPoint(point), point));
    }

    @Test
    public void testGetIntersection() throws Exception {
        assertEquals(null, track1.getIntersection(new Point(100, 100)));

        Intersection intersection = new Intersection(new Point(100, 100), track1, track2);

        assertEquals(intersection, track1.getIntersection(new Point(100, 100)));
        assertEquals(null, track1.getIntersection(new Point(0, 0)));
    }

    @Test
    public void testMove() throws Exception {
        track1.move(new Point(0,0), new Point(20,10));

        assertEquals(new Point(20,10), track1.getOtherPoint(new Point(100,100)));
    }

    @Test
    public void testMoveBad() throws Exception {
        track1.move(new Point(0,0), new Point(100,100));
        // We don't want to move a point onto itself.
        assertEquals(new Point(0, 0), track1.getOtherPoint(new Point(100, 100)));
    }

    @Test
    public void testConnectedTrackContainsNoDuplicateNoSelf() throws Exception {
        // Looks like a sideways Y
        Track track1 = new Track(new Point(100,100), new Point(200,100));
        Track track2 = new Track(new Point(200,100), new Point(300,200));
        Track track3 = new Track(new Point(200,100), new Point(300,0));

        Map map = new Map();

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);

        Set<Track> expectedConnections1 = new HashSet<Track>();
        expectedConnections1.add(track2);
        expectedConnections1.add(track3);

        Set<Track> expectedConnections2 = new HashSet<Track>();
        expectedConnections2.add(track1);
        expectedConnections2.add(track3);

        Set<Track> expectedConnections3 = new HashSet<Track>();
        expectedConnections3.add(track1);
        expectedConnections3.add(track2);

        assertEquals(new HashSet<Track>(expectedConnections1), new HashSet<Track>(track1.getConnectedTracks()));
        assertEquals(new HashSet<Track>(expectedConnections2), new HashSet<Track>(track2.getConnectedTracks()));
        assertEquals(new HashSet<Track>(expectedConnections3), new HashSet<Track>(track3.getConnectedTracks()));
    }

    @Test
    public void testGetIntersections() throws Exception {
        map.addTrack(track1);
        map.addTrack(track2);
        assertEquals(null, track1.getIntersection(new Point(0,0)));
        assertNotEquals(null, track1.getIntersection(new Point(100,100)));
    }
}