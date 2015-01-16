package lys.sepr.game.resources;

import java.util.ArrayList;
import java.util.List;

public class FuelStorage {

    private static List<Fuel> fuelList = new ArrayList<Fuel>();

    //all max values are subject to change, all quantities are set to max_value initially

    static Fuel coal100unlimited = new Fuel ("Coal_100", 200, 50, Integer.MAX_VALUE, 100);

    static Fuel oil100unlimited = new Fuel ("Oil_100", 200, 50, Integer.MAX_VALUE, 100);

    static Fuel oil50unlimited = new Fuel("Oil_50", 100, 0, Integer.MAX_VALUE, 50);

    static Fuel coal50unlimited = new Fuel("Coal_50", 100, 0, Integer.MAX_VALUE, 50);

    static {
        fuelList.add(coal50unlimited);
        fuelList.add(coal100unlimited);
        fuelList.add(oil50unlimited);
        fuelList.add(oil100unlimited);
    }

    public static List<Fuel> getFuelTypes() {
        // Don't want stock trains to be modified at runtime.
        return new ArrayList<Fuel>(fuelList);
    }

}
