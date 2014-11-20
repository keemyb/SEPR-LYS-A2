package lys.sepr.game.world;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MapTest {

    private Map map;
    private Track track1;
    private Track track2;

    @Before
    public void setUp() throws Exception {
        Point startPoint1 = new Point(0,0);
        Point endPoint1 = new Point(100,100);

        Point startPoint2 = new Point(200,200);
        Point endPoint2 = new Point(100,100);

        this.track1 = new Track(startPoint1, endPoint1);
        this.track2 = new Track(startPoint2, endPoint2);

    }

    @Test
    public void testAddTrack() throws Exception {
        ArrayList<Track> expectedTracks = new ArrayList<Track>();
        expectedTracks.add(track1);

        map.addTrack(track1);

        assertEquals(expectedTracks, map.getTracks());
    }

    @Test
    public void testNextTrack() throws Exception {
        map.addTrack(track2);

        assertEquals(track2, track1.getNextTrack(new Point(0,0)));
    }

}