package lys.sepr.game.resources;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TrainTest {

    Train train;

    @Before
    public void setUp() throws Exception {
         train = new Train("Test Train", 500, 0, Integer.MAX_VALUE,
                TrainType.PASSENGER, 15, 600, 1d, 0, 100, 100, 10);
    }

    @Test
    public void testRefillLessThanMax() throws Exception {
        int amountToAdd = 70;
        train.setAmountOfFuel(0);
        double leftover = train.refill(amountToAdd);

        assertEquals(0, leftover, 0.0d);
        assertEquals(70, train.getAmountOfFuel(), 0.0d);
    }

    @Test
    public void testRefillEqualToMax() throws Exception {
        int amountToAdd = 70;
        train.setAmountOfFuel(530);
        double leftover = train.refill(amountToAdd);

        assertEquals(0, leftover, 0.0d);
        assertEquals(600, train.getAmountOfFuel(), 0.0d);
    }

    @Test
    public void testRefillMoreThanMax() throws Exception {
        int amountToAdd = 70;
        train.setAmountOfFuel(560);
        double leftover = train.refill(amountToAdd);

        assertEquals(30, leftover, 0.0d);
        assertEquals(600, train.getAmountOfFuel(), 0.0d);
    }

    @Test
    public void testRepairLessThanMax() throws Exception {
        int unitsToRepair = 70;
        train.setHealth(10);
        train.repair(unitsToRepair);

        assertEquals(80, train.getHealth());
    }

    @Test
    public void testRepairEqualToMax() throws Exception {
        int unitsToRepair = 70;
        train.setHealth(30);
        train.repair(unitsToRepair);

        assertEquals(100, train.getHealth());
    }

    @Test
    public void testRepairMoreThanMax() throws Exception {
        int unitsToRepair = 70;
        train.setHealth(50);
        train.repair(unitsToRepair);

        assertEquals(100, train.getHealth());
    }
}