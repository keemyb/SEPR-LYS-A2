package lys.sepr.game.resources;

import lys.sepr.game.Player;
import lys.sepr.ui.Dialog;

public class Fuel extends Resource {

    private double quantity;

    Fuel(String name, int price, int reqReputation, int maxAllowed, int quantity) {
        super(name, price, reqReputation, maxAllowed);
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void use(double quantity, Player player){
        if (quantity < 0) {
            return;
        }
        Train train  = player.getActiveTrain().getTrain();
        double leftover = train.refill(quantity);
        this.setQuantity(this.quantity + leftover - quantity); //minus amountToFill of fuel from inventory
    }

    public int useDialog() {
        int amountToFill = -1; // amountToFill refers to the amountToFill of fuel that the player wishes to use
        // set to -1 for testing purposes.
        while (amountToFill < 0) {
            try {
                amountToFill = Dialog.intInput("How much fuel would you like to use?");
                if (amountToFill < 0){        //while it is less than 0, display error message
                    Dialog.error("Please input a positive value.");
                }
            } catch (NumberFormatException ex) {   //if not of type Number
                Dialog.error("Please input an integer value.");
            }
        }
        return amountToFill;
    }

}
