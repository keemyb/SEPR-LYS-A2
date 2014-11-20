package lys.sepr.game.world;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class IntersectionTest {

    private Track track1;
    private Track track2;
    private Intersection intersection;

    @Before
    public void setUp() throws Exception {
        Point startPoint1 = new Point(0,0);
        Point endPoint1 = new Point(100,100);

        Point startPoint2 = new Point(200,200);
        Point endPoint2 = new Point(100,100);

        this.track1 = new Track(startPoint1, endPoint1);
        this.track2 = new Track(startPoint2, endPoint2);

        this.intersection = new Intersection(endPoint1, track1, track2);

    }

    @Test
    public void testGetTracks() throws Exception {
        ArrayList<Track> expectedTracks = new ArrayList<Track>();
        expectedTracks.add(track1);
        expectedTracks.add(track2);

        assertEquals(expectedTracks, intersection.getTracks());
    }

    @Test
    public void testValidNextTracks() throws Exception {
        ArrayList<Track> expectedTracks = new ArrayList<Track>();
        expectedTracks.add(track2);

        assertEquals(expectedTracks, intersection.getValidNextTracks(track1));
    }

}