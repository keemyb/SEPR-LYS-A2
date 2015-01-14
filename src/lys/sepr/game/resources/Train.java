package lys.sepr.game.resources;

import lys.sepr.game.Player;

import java.util.ArrayList;

public class Train extends Resource{

    private TrainType type;
    private int maxSpeed;
    private double maxFuelCapacity;
    private double amountOfFuel;
    private double fuelEfficiency;
    private int rateOfHealthReduction;
    private int maxHealth;
    private int health;
    private int comfort;
    private int repairUnitCost;

    public double getAmountOfFuel() {
        return amountOfFuel;
    }

    public void setAmountOfFuel(double amountOfFuel) {
        this.amountOfFuel = amountOfFuel;
    }

    public TrainType getType() {
        return type;
    }

    public void setType(TrainType type) {
        this.type = type;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getMaxFuelCapacity() {
        return maxFuelCapacity;
    }

    public void setMaxFuelCapacity(double maxFuelCapacity) {
        this.maxFuelCapacity = maxFuelCapacity;
    }

    public double getFuelEfficiency() {
        return fuelEfficiency;
    }

    public void setFuelEfficiency(double fuelEfficiency) {
        this.fuelEfficiency = fuelEfficiency;
    }

    public int getRateOfHealthReduction() {
        return rateOfHealthReduction;
    }

    public void setRateOfHealthReduction(int rateOfHealthReduction) {
        this.rateOfHealthReduction = rateOfHealthReduction;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getComfort() {
        return comfort;
    }

    public void setComfort(int comfort) {
        this.comfort = comfort;
    }

    public int getRepairUnitCost() {
        return repairUnitCost;
    }

    public void setRepairUnitCost(int repairUnitCost) {
        this.repairUnitCost = repairUnitCost;
    }

    public void useFuel(double distanceTravelled) {
        amountOfFuel -= getFuelRequired(distanceTravelled);
    }

    public double getFuelRequired(double distanceTravelled) {
        return distanceTravelled / getFuelEfficiency();
    }

    public double getMileageLeft() {
        return getAmountOfFuel() * getFuelEfficiency();
    }

    public double refill(double fuelQuantity){
        if (fuelQuantity < 0) {
            return 0;
        } else if ((amountOfFuel + fuelQuantity) > maxFuelCapacity) {
            double previousFuel = amountOfFuel;
            amountOfFuel = maxFuelCapacity;
            return (previousFuel + fuelQuantity)- maxFuelCapacity;
        } else {
            amountOfFuel += fuelQuantity;
            return 0;
        }
    }

    public void repair(int unitsToRepair){
        if ((health + unitsToRepair) > maxHealth) {
            health = maxHealth;
        } else {
            health += unitsToRepair;
        }
    }

    public int getRating() {
        int rating = 0;
        rating += getMaxSpeed();
        rating += getMaxHealth();
        rating += getMaxFuelCapacity() * getFuelEfficiency();
        rating += getComfort();
        rating -= getRepairUnitCost();

        return rating;
    }

    public Train(String name, int price, int reqReputation, int maxAllowed,
                 TrainType trainType, int maxSpeed, double maxFuelCapacity,
                 double fuelEfficiency, int rateOfHealthReduction,
                 int maxHealth, int comfort, int repairUnitCost) {

        super(name, price, reqReputation, maxAllowed);

        setType(trainType);
        setMaxSpeed(maxSpeed);
        setMaxFuelCapacity(maxFuelCapacity);
        setAmountOfFuel(maxFuelCapacity);
        setFuelEfficiency(fuelEfficiency);
        setRateOfHealthReduction(rateOfHealthReduction);
        setMaxHealth(maxHealth);
        setHealth(maxHealth);
        setComfort(comfort);
        setRepairUnitCost(repairUnitCost);
    }

    public Train(Train train) {
        new Train(train.getName(), train.getPrice(), train.getReqReputation(),
                train.getMaxAllowed(), train.getType(), train.getMaxSpeed(),
                train.getMaxFuelCapacity(), train.getFuelEfficiency(),
                train.getRateOfHealthReduction(), train.getMaxHealth(),
                train.getComfort(), train.getRepairUnitCost());
    }

    @Override
    public void use(double quantity, Player player) {
        return;
    }
}
