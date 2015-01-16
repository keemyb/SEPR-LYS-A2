package lys.sepr.game.resources;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {

    Inventory inventory;


    @Before
    public void setUp() throws Exception {
        this.inventory = new Inventory();
    }

    @Test
    public void testAddFuelNonExisting() throws Exception {
        inventory.addNewResource(FuelStorage.oil50unlimited);
        inventory.addNewResource(FuelStorage.coal50unlimited);

        assertEquals(2, inventory.getSize());
        assertTrue(inventory.containsResource(FuelStorage.oil50unlimited));
        assertTrue(inventory.containsResource(FuelStorage.coal50unlimited));
    }

    @Test
    public void testAddFuelExisting() throws Exception {
        inventory.addNewResource(FuelStorage.oil50unlimited);
        inventory.addNewResource(FuelStorage.oil50unlimited);

        assertEquals(1, inventory.getSize());
        assertTrue(inventory.containsResource(FuelStorage.oil50unlimited));
        assertEquals(100, inventory.getQuantity(FuelStorage.oil50unlimited),0.0d);
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
    public void testGetOccurrencesNonExisting() throws Exception {
        assertEquals(0, inventory.getOccurrencesOfResource(TrainStorage.comfyCarriage));
    }

    @Test
    public void testAddTrainNonExisting() throws Exception {
        inventory.addNewResource(TrainStorage.comfyCarriage);

        assertEquals(1, inventory.getSize());
        assertTrue(inventory.containsResource(TrainStorage.comfyCarriage));
    }

    @Test
    public void testAddTrainExisting() throws Exception {
        inventory.addNewResource(TrainStorage.comfyCarriage);
        inventory.addNewResource(TrainStorage.comfyCarriage);

        assertEquals(2, inventory.getSize());
        assertTrue(inventory.containsResource(TrainStorage.comfyCarriage));
    }

    @Test
    public void testAddTrainFullInventory() throws Exception {
        inventory.addNewResource(TrainStorage.comfyCarriage);
        inventory.addNewResource(TrainStorage.comfyCarriage);
        inventory.addNewResource(TrainStorage.comfyCarriage);
        inventory.addNewResource(TrainStorage.comfyCarriage);
        inventory.addNewResource(TrainStorage.comfyCarriage);
        inventory.addNewResource(TrainStorage.comfyCarriage);
        inventory.addNewResource(TrainStorage.comfyCarriage);
        //full inventory

        assertEquals(7, inventory.getSize());

        inventory.addNewResource(TrainStorage.comfyCarriage);
        assertEquals(7, inventory.getSize());
    }

}