package lys.sepr.game.resources;

import java.util.ArrayList;

public class Train extends Resource{

    private static ArrayList<Train> TrainList = new ArrayList<Train>();
    private TrainType type;
    private Integer maxSpeed;
    private Double maxFuelCapacity;
    private Double amountOfFuel;
    private Float fuelEfficiency;
    private Integer rateOfDeterioration;
    private Integer maxDeterioration;
    private Integer deterioration;  //is this health??
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

    public Integer getRateOfDeterioration() {
        return rateOfDeterioration;
    }

    public void setRateOfDeterioration(Integer rateOfDeterioration) {
        this.rateOfDeterioration = rateOfDeterioration;
    }

    public Integer getMaxDeterioration() {
        return maxDeterioration;
    }

    public void setMaxDeterioration(Integer maxDeterioration) {
        this.maxDeterioration = maxDeterioration;
    }

    public Integer getDeterioration() {
        return deterioration;
    }

    public void setDeterioration(Integer deterioration) {
        this.deterioration = deterioration;
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

    public Resource getInitialPassengerTrain() {
        return initialPassengerTrain;
    }

    public void setInitialPassengerTrain(Train initialPassengerTrain) {
        this.initialPassengerTrain = initialPassengerTrain;
    }

    public Train getInitialCargoTrain() {
        return initialCargoTrain;
    }

    public void setInitialCargoTrain(Train initialCargoTrain) {
        this.initialCargoTrain = initialCargoTrain;
    }

    public ArrayList<Train> getTrainList() {
        return TrainList;
    }

    public void setTrainList(ArrayList<Train> trainList) {
        TrainList = trainList;
    }

    //initial train types for inventory
    public Train initialCargoTrain;
    public Train initialPassengerTrain;

    public void tooMuchError(String message){
        //show warning on GUI -- Sorry, but this is too much fuel/health
    }

    public void useTrain(Train train){}

    public void refillTank(Integer fuelQuantity, Train train){
        if ((train.amountOfFuel += fuelQuantity) > train.maxFuelCapacity) {
            tooMuchError("fuel");
        } else {
            train.amountOfFuel += fuelQuantity;
        }
    }

    public void repair(Integer unitsToRepair, Train train){
        //does this change the deterioration variable
        if ((train.deterioration += unitsToRepair) > train.maxDeterioration) {
            tooMuchError("health");
        } else {
            train.deterioration += unitsToRepair;  //is this right???????
        }
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

    public Train(String name, int price, int reqReputation, int maxAllowed) {
        super(name, price, reqReputation, maxAllowed);
    }
}
