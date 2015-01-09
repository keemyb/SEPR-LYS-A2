package lys.sepr.game.resources;

import java.util.ArrayList;

public class PowerUp extends Resource {

    private static ArrayList<PowerUp> powerUps = new ArrayList<PowerUp>();
    // public SOME_TYPE effect;

    public ArrayList<PowerUp> getPowerUps() {
        return powerUps;
    }

    public void setPowerUps(ArrayList<PowerUp> powerUps) {
        PowerUp.powerUps = powerUps;
    }

    PowerUp(String name, int price, int reqReputation, int maxAllowed) {
        super(name, price, reqReputation, maxAllowed);
    }
}
