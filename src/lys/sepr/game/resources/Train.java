package lys.sepr.game.resources;

import java.util.ArrayList;

public class Train extends Resource{

    private static ArrayList<Train> TrainList = new ArrayList<Train>();
    private TrainType type;
    private Integer maxSpeed;
    private Double maxFuelCapacity;
    private Double amountOfFuel;
    private Float fuelEfficiency;
    private Integer rateOfHealthReduction;
    private Integer maxHealth;
    private Integer health;
    private Integer comfort;
    private Integer repairUnitCost;

    public Double getAmountOfFuel() {
        return amountOfFuel;
    }

    public void setAmountOfFuel(Double amountOfFuel) {
        this.amountOfFuel = amountOfFuel;
    }

    public TrainType getType() {
        return type;
    }

    public void setType(TrainType type) {
        this.type = type;
    }

    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Integer maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Double getMaxFuelCapacity() {
        return maxFuelCapacity;
    }

    public void setMaxFuelCapacity(Double maxFuelCapacity) {
        this.maxFuelCapacity = maxFuelCapacity;
    }

    public Float getFuelEfficiency() {
        return fuelEfficiency;
    }

    public void setFuelEfficiency(Float fuelEfficiency) {
        this.fuelEfficiency = fuelEfficiency;
    }

    public Integer getRateOfHealthReduction() {
        return rateOfHealthReduction;
    }

    public void setRateOfHealthReduction(Integer rateOfHealthReduction) {
        this.rateOfHealthReduction = rateOfHealthReduction;
    }

    public Integer getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(Integer maxHealth) {
        this.maxHealth = maxHealth;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public Integer getComfort() {
        return comfort;
    }

    public void setComfort(Integer comfort) {
        this.comfort = comfort;
    }

    public Integer getRepairUnitCost() {
        return repairUnitCost;
    }

    public void setRepairUnitCost(Integer repairUnitCost) {
        this.repairUnitCost = repairUnitCost;
    }

    public ArrayList<Train> getTrainList() {
        return TrainList;
    }

    public void setTrainList(ArrayList<Train> trainList) {
        TrainList = trainList;
    }

    public void useFuel(Double distanceTravelled) {
        amountOfFuel -= getFuelRequired(distanceTravelled);
    }

    public double getFuelRequired(Double distanceTravelled) {
        return distanceTravelled / getFuelEfficiency();
    }

    public double getMileageLeft() {
        return getAmountOfFuel() * getFuelEfficiency();
    }

    public void refill(Integer fuelQuantity){
        if ((amountOfFuel + fuelQuantity) > maxFuelCapacity) {
            amountOfFuel = maxFuelCapacity;
        } else {
            amountOfFuel += fuelQuantity;
        }
    }

    public void repair(Integer unitsToRepair){
        if ((health + unitsToRepair) > maxHealth) {
            health = maxHealth;
        } else {
            health += unitsToRepair;
        }
    }

    public Train(String name, int price, int reqReputation, int maxAllowed) {
        super(name, price, reqReputation, maxAllowed);
    }
}
