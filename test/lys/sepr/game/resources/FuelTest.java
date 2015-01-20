package lys.sepr.game.resources;

import lys.sepr.game.ActiveTrain;
import lys.sepr.game.Player;
import lys.sepr.game.world.Map;
import lys.sepr.game.world.Point;
import lys.sepr.game.world.Route;
import lys.sepr.game.world.Track;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class FuelTest {

    Train train;
    Player player;
    Fuel oil50unlimited;

    @Before
    public void setUp() throws Exception {
        train = new Train("Test Train", 500, 0, Integer.MAX_VALUE,
                TrainType.PASSENGER, 15, 600, 1d, 0, 100, 100, 10);
        player = new Player(1, Player.PlayerColor.BLUE, "player");
        Route route = new Route(new Point(0,0), new Point(1,1));
        route.addTrack(new Track(new Point(0,0), new Point(1,1)));
        player.setActiveTrain(new ActiveTrain(train, route));
        oil50unlimited = new Fuel("Oil", 100, 0, Integer.MAX_VALUE, 50);
    }

    @Test
    public void testUseAmountNegative() throws Exception {
        oil50unlimited.setQuantity(80);
        train.setAmountOfFuel(550);
        oil50unlimited.use(-5.0, player);

        assertEquals (550, train.getAmountOfFuel(), 0.0d);
        assertEquals (80, oil50unlimited.getQuantity(), 0.0d);
    }

    @Test
    public void testUseAmountLessThanMax() throws Exception {
        oil50unlimited.setQuantity(80);
        train.setAmountOfFuel(550);
        oil50unlimited.use(30, player);

        assertEquals (580, train.getAmountOfFuel(), 0.0d);
        assertEquals (50, oil50unlimited.getQuantity(), 0.0d);
    }

    @Test
    public void testUseAmountEqualToMax() throws Exception {
        oil50unlimited.setQuantity(80);
        train.setAmountOfFuel(550);
        oil50unlimited.use(50, player);

        assertEquals (600, train.getAmountOfFuel(), 0.0d);
        assertEquals (30, oil50unlimited.getQuantity(), 0.0d);
    }

    @Test
    public void testUseAmountMoreThanMax() throws Exception {
        oil50unlimited.setQuantity(80);
        train.setAmountOfFuel(550);
        oil50unlimited.use(70, player);

        assertEquals (600, train.getAmountOfFuel(), 0.0d);
        assertEquals (30, oil50unlimited.getQuantity(), 0.0d);
    }



}