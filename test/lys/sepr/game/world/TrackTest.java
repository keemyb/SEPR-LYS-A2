package lys.sepr.game.world;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TrackTest {

    private Track track;

    @Before
    public void setUp() throws Exception {
        Point startPoint = new Point(0,0);
        Point endPoint = new Point(100,100);

        this.track = new Track(startPoint, endPoint);
    }

    @Test
    public void testNextTrackSolo() throws Exception {
        assertEquals(null, track.getNextTrack(new Point(0, 0)));
    }

    /* testNextTrack can be found in MapTest, as there can be no next track without
    a reference to a list of tracks. */
    
}