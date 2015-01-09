package lys.sepr.game.resources;

import java.util.ArrayList;

public class Fuel extends Resource {

    private static ArrayList<Fuel> fuelList = new ArrayList<Fuel>();
    private int quantity;

    Fuel(String name, int price, int reqReputation, int maxAllowed, int quantity) {
        super(name, price, reqReputation, maxAllowed);
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ArrayList<Fuel> getFuelList() {
        return fuelList;
    }

    public void setFuelList(ArrayList<Fuel> fuelList) {
        Fuel.fuelList = fuelList;
    }


}
