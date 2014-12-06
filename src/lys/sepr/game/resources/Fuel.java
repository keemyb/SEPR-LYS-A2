package lys.sepr.game.resources;

public class Fuel extends Resource {

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

}
