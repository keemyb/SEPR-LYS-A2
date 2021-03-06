package lys.sepr.game;

import lys.sepr.game.resources.Train;
import lys.sepr.game.resources.TrainType;
import lys.sepr.game.world.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ActiveTrainTest {

    Train train;

    Route shortRoute;
    Route longRoute;

    Location startLocation;

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
        train = new Train("test train", 10, 0, 1, TrainType.PASSENGER,
                Integer.MAX_VALUE, Double.POSITIVE_INFINITY,
                1d, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        train.setMaxSpeed(Integer.MAX_VALUE);
        train.setMaxFuelCapacity(Double.POSITIVE_INFINITY);
        train.setAmountOfFuel(Double.POSITIVE_INFINITY);
        train.setFuelEfficiency(1f);

        map = new Map();

        point1 = new Point(0,0);
        point2 = new Point(100,0);
        point3 = new Point(200,100);
        point4 = new Point(300,100);

        startLocation = new Location(point1, "Start Point");
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

    /**
     * Simulates passing time on an ActiveTrain, so you can test movement
     * without having to wait realtime.
     * @param activeTrain the Train that should be simulated
     * @param intervals   the number of times that time should be simulated.
     * @param time        the length of time to simulate each step.
     */
    private void advanceTime(ActiveTrain activeTrain, int intervals, long time) {
        while (intervals > 0) {
            activeTrain.move(time);
            intervals--;
        }
    }

    @Test
    public void testMoveOnTrack() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, shortRoute);
        activeTrain.setCurrentSpeed(2d);

        advanceTime(activeTrain, 2, 10);

        assertEquals(new Point(40, 0), activeTrain.getCurrentPosition());

    }

    @Test
    public void testChangeTrack() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, longRoute);
        activeTrain.changeRoute(track2);
        activeTrain.changeRoute(track3);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 10, 10);

        assertEquals(point2, activeTrain.getCurrentPosition());
        assertEquals(point3, activeTrain.getFacing());
        assertFalse(activeTrain.getRemainderOfRoute().contains(track1));
    }

    @Test
    public void testChangeTrackOvershoot() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, longRoute);
        activeTrain.changeRoute(track2);
        activeTrain.changeRoute(track3);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 1, 99);
        advanceTime(activeTrain, 1, 2);

        assertEquals(point3, activeTrain.getFacing());
        assertNotEquals(activeTrain.getCurrentPosition(), point2);
        assertFalse(activeTrain.getRemainderOfRoute().contains(track1));
    }

    @Test
    public void testEndRoute() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, shortRoute);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 10, 10);

        assertEquals(activeTrain.getDestination(), activeTrain.getCurrentPosition());
    }

    @Test
    public void testEndRouteOvershoot() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, shortRoute);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 1, 99);
        advanceTime(activeTrain, 1, 2);

        assertEquals(activeTrain.getDestination(), activeTrain.getCurrentPosition());
    }

    @Test
    public void testChangeRoute() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, longRoute);

        Point newPoint = new Point(200,50);

        Track newTrack = new Track(point2, newPoint);

        map.addTrack(newTrack);

        activeTrain.changeRoute(newTrack);

        assertEquals(2, activeTrain.getRemainderOfRoute().size());
        assertEquals(track1, activeTrain.getRemainderOfRoute().get(0));
        assertEquals(newTrack, activeTrain.getRemainderOfRoute().get(1));
    }

    @Test
    public void testChangeRouteInvalid() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, longRoute);

        ActiveTrain.setMustChooseValidConnectedTrack(true);

        Point newPoint1 = new Point(1000,1000);
        Point newPoint2 = new Point(1001,1001);

        Track newTrack = new Track(newPoint1, newPoint2);

        map.addTrack(newTrack);

        activeTrain.changeRoute(newTrack);

        List<Track> oldRemainderOfRoute = new ArrayList<Track>(activeTrain.getRemainderOfRoute());

        assertEquals(oldRemainderOfRoute, activeTrain.getRemainderOfRoute());
    }

    @Test
    public void testMoveToBrokenTrack() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, longRoute);

        track2.setBroken(true);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 1, 150);

        assertEquals(point2, activeTrain.getCurrentPosition());
    }

    @Test
    public void testReverse() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, longRoute);
        activeTrain.changeRoute(track2);
        activeTrain.changeRoute(track3);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 1, 150);

        activeTrain.reverse();

        assertEquals(point2, activeTrain.getFacing());
        assertEquals(1, activeTrain.getRemainderOfRoute().size());
        assertEquals(track2, activeTrain.getRemainderOfRoute().get(0));

        advanceTime(activeTrain, 1, 50);

        assertEquals(point2, activeTrain.getCurrentPosition());
    }

    @Test
    public void testFuelUsageEmptyTank() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, longRoute);
        activeTrain.getTrain().setAmountOfFuel(100d);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 1, 100);

        assertEquals(0d, activeTrain.getTrain().getAmountOfFuel(), 0.0d);
    }

    @Test
    public void testFuelUsagePartialEmptyTank() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, longRoute);
        activeTrain.getTrain().setAmountOfFuel(100d);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 1, 50);

        assertEquals(50d, activeTrain.getTrain().getAmountOfFuel(), 0.0d);
    }

    @Test
    public void testMoveTrainOnEmptyTank() throws Exception {
        ActiveTrain activeTrain = new ActiveTrain(train, longRoute);
        activeTrain.getTrain().setAmountOfFuel(0d);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 1, 50);

        assertEquals(0d, activeTrain.getTrain().getAmountOfFuel(), 0.0d);
        assertEquals(point1, activeTrain.getCurrentPosition());
    }

    @Test
    public void testMoveTrainNotEnoughFuel() throws Exception {
        // Making a straight track so I don't have to calculate funky distances.
        Point altPoint = new Point(200, 0);
        Track altTrack = new Track(point2, altPoint);
        Location altLocation = new Location(altPoint, "Alternate Location");

        map.addTrack(altTrack);
        map.addLocation(altLocation);
        track1.setActiveConnection(point2, altTrack);

        ActiveTrain activeTrain = new ActiveTrain(train, map.fastestRoute(startLocation, altLocation));
        activeTrain.changeRoute(altTrack);
        activeTrain.getTrain().setAmountOfFuel(150d);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 1, 200);

        assertEquals(0d, activeTrain.getTrain().getAmountOfFuel(), 0.0d);
        assertEquals(new Point(150, 0), activeTrain.getCurrentPosition());
    }

    @Test
    public void testMoveTrainNoPathEnoughFuel() throws Exception {
        // This test is to ensure that the right amount of fuel is used when a
        // train is instructed to move past a certain track, but cannot.

        // Making a straight track so I don't have to calculate funky distances.
        Point altPoint = new Point(200, 0);
        Track altTrack = new Track(point2, altPoint);
        Location altLocation = new Location(altPoint, "Alternate Location");

        map.addTrack(altTrack);
        map.addLocation(altLocation);

        ActiveTrain activeTrain = new ActiveTrain(train, map.fastestRoute(startLocation, altLocation));
        activeTrain.getTrain().setAmountOfFuel(150d);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 1, 200);

        assertEquals(50d, activeTrain.getTrain().getAmountOfFuel(), 0.0d);
    }

    @Test
    public void testNextTrackInRouteNotActiveConnection() throws Exception {
        // This is a test to ensure a train remains at the end of it's current
        // track if the next track (and current track) in route is not the
        // active connection of the upcoming intersection.

        // Creating a map that forks like a sideways Y
        Track startTrack = new Track(new Point(0, 100), new Point(100, 100));
        Track endTrack1 = new Track(new Point(100, 100), new Point(200, 125));
        Track endTrack2 = new Track(new Point(100, 100), new Point(200, 75));
        Location startLocation = new Location(new Point(0, 100), "start");
        Location endLocation = new Location(new Point(200, 75), "end");

        Map map = new Map();

        map.addTrack(startTrack);
        map.addTrack(endTrack1);
        map.addTrack(endTrack2);
        map.addLocation(startLocation);
        map.addLocation(endLocation);

//        endTrack2 should not be part of the activeConnection as it was added last
        startTrack.setActiveConnection(new Point(100, 100), endTrack1);
        assertTrue(map.getIntersections().get(0).getActiveConnection().contains(startTrack));
        assertTrue(map.getIntersections().get(0).getActiveConnection().contains(endTrack1));

        Route route = map.fastestRoute(startLocation, endLocation);

        ActiveTrain activeTrain = new ActiveTrain(train, route);
        activeTrain.getTrain().setAmountOfFuel(200d);

        activeTrain.setCurrentSpeed(1d);

        advanceTime(activeTrain, 1, 110);

        assertEquals(new Point(100, 100), activeTrain.getCurrentPosition());
    }
}