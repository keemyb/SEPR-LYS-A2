package lys.sepr.game.resources;

import java.util.ArrayList;

public class Train extends Resource{

    public ArrayList<Train> TrainList = new ArrayList<Train>();
    public TrainType type;
    public Integer maxSpeed;
    public Integer maxFuelCapacity;
    public Integer fuelCapacity;
    public Float fuelEfficiency;
    public Integer rateOfDeterioration;
    public Integer maxDeterioration;
    public Integer deterioration;  //is this health??
    public Integer comfort;
    public Integer repairUnitCost;

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

    public Integer getMaxFuelCapacity() {
        return maxFuelCapacity;
    }

    public void setMaxFuelCapacity(Integer maxFuelCapacity) {
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
        if ((train.fuelCapacity += fuelQuantity) > train.maxFuelCapacity) {
            tooMuchError("fuel");
        } else {
            train.fuelCapacity += fuelQuantity;
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



    public Train(String name, int price, int reqReputation, int maxAllowed) {
        super(name, price, reqReputation, maxAllowed);
    }
}
