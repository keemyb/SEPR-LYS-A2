package lys.sepr.game.world;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

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
    }

    @Test
    public void testNextTrackSolo() throws Exception {
        assertEquals(null, track1.getNextTrack(new Point(0, 0)));
    }

    @Test
    public void testNextTrackDuo() throws Exception {
        map.addTrack(track1);
        map.addTrack(track2);

        assertEquals(track2, track1.getNextTrack(new Point(0,0)));
        assertEquals(null, track1.getNextTrack(new Point(100,100)));
    }

    @Test
    public void testNextTrackTrio() throws Exception {
        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);

        assertEquals(track1, track3.getNextTrack(new Point(200,100)));
        assertEquals(track1, track2.getNextTrack(new Point(200,200)));
        assertEquals(track2, track1.getNextTrack(new Point(0,0)));
    }

    @Test
    public void testGetVector() throws Exception {
        ArrayList<Double> expectedVector1 = new ArrayList<Double>();
        expectedVector1.add(100d);
        expectedVector1.add(100d);

        ArrayList<Double> expectedVector2 = new ArrayList<Double>();
        expectedVector2.add(-100d);
        expectedVector2.add(0d);

        assertEquals(expectedVector1, track1.getVector(new Point(100,100)));
        assertEquals(expectedVector2, track3.getVector(new Point(100,100)));
    }
}