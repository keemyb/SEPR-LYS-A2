package lys.sepr.game.resources;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TrainStorageTest {

    @Test
    public void testGetStarterTrainGiveDistinctTrain() throws Exception {
        Train train1 = TrainStorage.getStarterTrain(TrainType.PASSENGER);
        Train train2 = TrainStorage.getStarterTrain(TrainType.PASSENGER);

        assertEquals(train1, train2);
        // Shouldn't be the same object
        assertTrue(train1 != train2);
    }

    @Test
    public void testGetTrainsReturnsCopy() throws Exception {
        List<Train> trains1 = TrainStorage.getTrains();
        List<Train> trains2 = TrainStorage.getTrains();

        assertEquals(trains1, trains2);
        // Shouldn't be the same object
        assertTrue(trains1 != trains2);
    }
}