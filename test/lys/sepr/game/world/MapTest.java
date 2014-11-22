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
    public void testAddTrack() throws Exception {
        ArrayList<Track> expectedTracks = new ArrayList<Track>();
        expectedTracks.add(track1);

        map.addTrack(track1);

        assertEquals(expectedTracks, map.getTracks());
    }

    @Test
    public void moveTrackCreateNewIntersection() throws Exception {
        Track track4 = new Track(new Point(0,100), new Point(90, 100));

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
        Track track4 = new Track(new Point(0,100), new Point(90, 100));

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
}