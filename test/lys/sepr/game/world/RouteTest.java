package lys.sepr.game.world;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RouteTest {

    private Map map = new Map();
    private Track track1;
    private Track track2;
    private Track track3;
    private Track track4;
    private Track track5;
    private Track track6;
    private Track track7;
    private Location locationOne;
    private Location locationTwo;

    @Before
    public void setUp() throws Exception {
        Point startPoint1 = new Point(0, 0);
        Point endPoint1 = new Point(100, 100);

        Point startPoint2 = new Point(200, 200);
        Point endPoint2 = new Point(100, 100);

        Point startPoint3 = new Point(100, 100);
        Point endPoint3 = new Point(200, 100);

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
    public void testFindRouteOnePath() throws Exception {
        Location locationOne = new Location(new Point(0,0), "one");
        Location locationTwo = new Location(new Point(200,200), "two");
        map.addLocation(locationOne);
        map.addLocation(locationTwo);

        map.addTrack(track1);
        map.addTrack(track2);

        Route expectedRoute = new Route(locationOne.getPoint(), locationTwo.getPoint());
        expectedRoute.addTrack(track1);
        expectedRoute.addTrack(track2);

        assertEquals(expectedRoute, map.getRoutes(locationOne, locationTwo).get(0));
        assertEquals(1, map.getRoutes(locationOne, locationTwo).size());
    }

    @Test
    public void testFindRouteNoPathLocationNotOnPath() throws Exception {
        Location locationOne = new Location(new Point(0,0), "one");
        Location locationTwo = new Location(new Point(300,300), "two");
        map.addLocation(locationOne);
        map.addLocation(locationTwo);

        map.addTrack(track1);
        map.addTrack(track2);

        assertEquals(0, map.getRoutes(locationOne, locationTwo).size());
    }

    @Test
    public void testFindRouteLocationUnreachable() throws Exception {
        Track track3 = new Track(new Point(250,250), new Point(300,300));

        Location locationOne = new Location(new Point(0,0), "one");
        Location locationTwo = new Location(new Point(300,300), "two");
        map.addLocation(locationOne);
        map.addLocation(locationTwo);

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);

        assertEquals(0, map.getRoutes(locationOne, locationTwo).size());
    }

    public void setUpMultiplePaths() {
//        +-------------------------------+
//        |                               |
//        | Approx Locations           7a |
//        | O = Intersection          X   |
//        |                          X    |
//        | From 1a to 7a:          7     |
//        |                  O4XXX4O      |
//        | 1-2-7 is fastest 3    26      |
//        | Then 1-5-6-7     X   X X      |
//        | Then 1-3-4-7     X  X  6      |
//        |                  X X  5O      |
//        |                  32 XX        |
//        |                  O5X          |
//        |                 1             |
//        |                X              |
//        |               X               |
//        |              1a               |
//        |                               |
//        +-------------------------------+

        this.track1 = new Track(new Point(0, 0), new Point(100, 100));
        this.track2 = new Track(new Point(100, 100), new Point(200, 200));
        this.track3 = new Track(new Point(100, 100), new Point(130, 150));
        this.track4 = new Track(new Point(130, 150), new Point(200, 200));
        this.track5 = new Track(new Point(100, 100), new Point(160, 150));
        this.track6 = new Track(new Point(160, 150), new Point(200, 200));
        this.track7 = new Track(new Point(200, 200), new Point(300, 300));

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
        map.addTrack(track4);
        map.addTrack(track5);
        map.addTrack(track6);
        map.addTrack(track7);

        this.locationOne = new Location(new Point(0, 0), "location one");
        this.locationTwo = new Location(new Point(300, 300), "location two");

        map.addLocation(locationOne);
        map.addLocation(locationTwo);
    }

    @Test
    public void testFindRouteMultiplePath() throws Exception {
        setUpMultiplePaths();

        Route expectedRoute1 = new Route(locationOne.getPoint(), locationTwo.getPoint());
        expectedRoute1.addTrack(track1);
        expectedRoute1.addTrack(track2);
        expectedRoute1.addTrack(track7);

        Route expectedRoute2 = new Route(locationOne.getPoint(), locationTwo.getPoint());
        expectedRoute2.addTrack(track1);
        expectedRoute2.addTrack(track5);
        expectedRoute2.addTrack(track6);
        expectedRoute2.addTrack(track7);

        Route expectedRoute3 = new Route(locationOne.getPoint(), locationTwo.getPoint());
        expectedRoute3.addTrack(track1);
        expectedRoute3.addTrack(track3);
        expectedRoute3.addTrack(track4);
        expectedRoute3.addTrack(track7);

        List<Route> routes = map.getRoutes(locationOne, locationTwo);

        assertTrue(routes.contains(expectedRoute1));
        assertTrue(routes.contains(expectedRoute2));
        assertTrue(routes.contains(expectedRoute3));
        assertEquals(3, routes.size());
    }

    @Test
    public void testFindFastestPath() throws Exception {
        setUpMultiplePaths();

        ArrayList<Track> expectedRoute = new ArrayList<Track>();
        expectedRoute.add(track1);
        expectedRoute.add(track2);
        expectedRoute.add(track7);

        assertEquals(expectedRoute, map.fastestRoute(locationOne, locationTwo).getTracks());
    }

    @Test
    public void testFindImmediatePath() throws Exception {
        map.addTrack(track1);
//        map.addTrack(track2);

        this.locationOne = new Location(new Point(0, 0), "location one");
        this.locationTwo = new Location(new Point(100, 100), "location two");

        map.addLocation(locationOne);
        map.addLocation(locationTwo);

        Route expectedRoute = new Route(locationOne.getPoint(), locationTwo.getPoint());
        expectedRoute.addTrack(track1);

        assertTrue(map.getRoutes(locationOne, locationTwo).contains(expectedRoute));
        assertEquals(1, map.getRoutes(locationOne, locationTwo).size());
    }

    @Test
    public void testFilterRedundantRoute() throws Exception {
        this.track1 = new Track(new Point(0, 0), new Point(100, 100));
        this.track2 = new Track(new Point(100, 100), new Point(200, 200));
        this.track3 = new Track(new Point(200, 200), new Point(300, 300));

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);

        Route expectedRoute = new Route(new Point(100, 100), new Point(200, 200));
        expectedRoute.addTrack(track2);

        // A redundant route in this case would feature all 3 tracks.
        // Or maybe even 2 tracks (1&2, 2&3).
        List<Route> routes = Route.getRoutes(new Point(100, 100), new Point(200, 200), map);

        assertEquals(1, routes.size());
        assertEquals(expectedRoute, routes.get(0));
    }

    @Test
    public void testTraversableRoute() throws Exception {
        Route route = new Route(new Point(0, 0), new Point(200, 200));
        route.addTrack(track1);
        route.addTrack(track2);

        track1.setBroken(true);

        assertFalse(route.isTraversable());
    }

    @Test
    public void testTraversableRouteTrack() throws Exception {
        Route route = new Route(new Point(0, 0), new Point(300, 300));

        Track track3 = new Track(new Point(200, 200), new Point(300, 300));

        route.addTrack(track1);
        route.addTrack(track2);
        route.addTrack(track3);

        track2.setBroken(true);

        assertFalse(route.isTraversable(track1));
        assertFalse(route.isTraversable(track2));
        assertTrue(route.isTraversable(track3));
    }
}
