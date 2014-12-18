package lys.sepr.game.resources;

import java.util.ArrayList;

public class PowerUp extends Resource {

    public ArrayList<PowerUp> PowerUps = new ArrayList<PowerUp>();
    // public SOME_TYPE effect;

    public ArrayList<PowerUp> getPowerUps() {
        return PowerUps;
    }

    public void setPowerUps(ArrayList<PowerUp> powerUps) {
        PowerUps = powerUps;
    }

    PowerUp(String name, int price, int reqReputation, int maxAllowed) {
        super(name, price, reqReputation, maxAllowed);
    }
}
