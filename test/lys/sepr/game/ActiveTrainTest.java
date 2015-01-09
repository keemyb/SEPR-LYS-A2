package lys.sepr.game;

import lys.sepr.game.resources.Train;
import lys.sepr.game.world.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ActiveTrainTest {

    Train train;

    Route shortRoute;
    Route longRoute;

    Map map;

    Point point1;
    Point point2;
    Point point3;
    Point point4;

    Track track1;
    Track track2;
    Track track3;

    @Before
    public void setUp() throws Exception {
        train = new Train("test train", 10, 0, 1);

        map = new Map();

        point1 = new Point(0,0);
        point2 = new Point(100,0);
        point3 = new Point(200,100);
        point4 = new Point(300,100);

        Location startLocation = new Location(point1, "Start Point");
        Location endLocationShort = new Location(point2, "Short End Point");
        Location endLocationLong = new Location(point4, "Long End Point");

        map.addLocation(startLocation);
        map.addLocation(endLocationShort);
        map.addLocation(endLocationLong);

        track1 = new Track(point1, point2);
        track2 = new Track(point2, point3);
        track3 = new Track(point3, point4);

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);

        shortRoute = map.fastestRoute(point1, point2);
        longRoute = map.fastestRoute(point1, point4);
    }

    @Test
    public void testShortRouteActiveTrainInit() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, shortRoute);

        assertEquals(point1, activeTrain.getCurrentPosition());
        assertEquals(point2, activeTrain.getFacing());
        assertEquals(point2, activeTrain.getDestination());
    }

    @Test
    public void testLongRouteActiveTrainInit() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, longRoute);

        assertEquals(point1, activeTrain.getCurrentPosition());
        assertEquals(point2, activeTrain.getFacing());
        assertEquals(point4, activeTrain.getDestination());
    }
}