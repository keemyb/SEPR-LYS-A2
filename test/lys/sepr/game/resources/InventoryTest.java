package lys.sepr.game.resources;

import lys.sepr.game.world.Intersection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {

    Inventory inventory;
    Fuel oil50unlimited;
    Fuel coal50unlimited;

    @Before
    public void setUp() throws Exception {
        this.inventory = new Inventory();
        this.oil50unlimited = new Fuel("Oil", 100, 0, Integer.MAX_VALUE, 50);
        this.coal50unlimited = new Fuel("Coal", 100, 0, Integer.MAX_VALUE, 50);

    }

    @Test
    public void testAddFuelNonExisting() throws Exception {
        inventory.addNewResource(oil50unlimited);
        inventory.addNewResource(coal50unlimited);

        assertEquals(2, inventory.getSize());
        assertTrue(inventory.containsResource(oil50unlimited));
        assertTrue(inventory.containsResource(coal50unlimited));
    }

    @Test
    public void testAddFuelExisting() throws Exception {
        inventory.addNewResource(oil50unlimited);
        inventory.addNewResource(oil50unlimited);

        assertEquals(1, inventory.getSize());
        assertTrue(inventory.containsResource(oil50unlimited));
        assertEquals(100, inventory.getQuantity(oil50unlimited),0.0d);
    }

    @Test
    public void testDiscardResource() throws Exception {
        inventory.addNewResource(TrainStorage.comfyCarriage);
        inventory.discardResource(TrainStorage.comfyCarriage);

        assertEquals(0, inventory.getSize());
    }

    @Test
    public void testDiscardNonExistentResource() throws Exception {
        inventory.addNewResource(TrainStorage.machineGunWagon);
        inventory.discardResource(TrainStorage.comfyCarriage);

        assertEquals(1, inventory.getSize());
    }

    @Test
    public void testGetOccurrences() throws Exception {
        inventory.addNewResource(TrainStorage.comfyCarriage);
        inventory.addNewResource(TrainStorage.comfyCarriage);

        assertEquals(2, inventory.getOccurrencesOfResource(TrainStorage.comfyCarriage));
    }

    @Test
    public void testAddTrainNonExisting() throws Exception {
        inventory.addNewResource(TrainStorage.comfyCarriage);

        assertEquals(1, inventory.getSize());
        assertTrue(inventory.containsResource(TrainStorage.comfyCarriage));
    }

    @Test
    public void testTrainExisting() throws Exception {
        inventory.addNewResource(TrainStorage.comfyCarriage);
        inventory.addNewResource(TrainStorage.comfyCarriage);

        assertEquals(2, inventory.getSize());
        assertTrue(inventory.containsResource(TrainStorage.comfyCarriage));
    }

}