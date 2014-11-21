package lys.sepr.game.world;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MapTest {

    private Map map = new Map();
    private Track track1;

    @Before
    public void setUp() throws Exception {
        Point startPoint1 = new Point(0,0);
        Point endPoint1 = new Point(100,100);

        this.track1 = new Track(startPoint1, endPoint1);
    }

    @Test
    public void testAddTrack() throws Exception {
        ArrayList<Track> expectedTracks = new ArrayList<Track>();
        expectedTracks.add(track1);

        map.addTrack(track1);

        assertEquals(expectedTracks, map.getTracks());
    }

}