package lys.sepr.game.world;

import org.junit.Before;

import static org.junit.Assert.*;

public class TrackTest {

    private Track track1;
    private Track track2;

    @Before
    public void setUp() throws Exception {
        Point startPoint1 = new Point(0,0);
        Point endPoint1 = new Point(100,100);

        this.track1 = new Track(startPoint1, endPoint1);
    }

    public void testNextTrackSolo() throws Exception {
        assertEquals(null, track1.getNextTrack(new Point(0,0)));
    }

    public void testNextTrack() throws Exception {
        Point startPoint2 = new Point(200,200);
        Point endPoint2 = new Point(100,100);

        this.track2 = new Track(startPoint2, endPoint2);

        assertEquals(track2, track1.getNextTrack(new Point(0,0)));
    }
    
}