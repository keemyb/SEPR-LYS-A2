package lys.sepr.game;

import lys.sepr.game.resources.Inventory;
import lys.sepr.game.resources.Train;
import lys.sepr.profile.Profile;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private Contract currentContract;
    private long contractStartTime;
    private int money;

    private Inventory inventory = new Inventory();

    private ActiveTrain activeTrain;

    private List<Contract> completedContracts = new ArrayList<Contract>();
    private List<Contract> failedContracts = new ArrayList<Contract>();

    private int reputation = 0;
    private Profile profile;

    Player(int money) {
        this.money = money;
    }

    public ActiveTrain getActiveTrain() {
        return activeTrain;
    }

    public Contract getCurrentContract() {
        return currentContract;
    }

    public void fulfilledCurrentContract() {
        if (currentContract != null){
            completedContracts.add(currentContract);
            addMoney(currentContract.getMoneyPayout());
            updateReputation(currentContract.getReputationPayout());
            endContract();
        }
    }

    public void failedCurrentContract() {
        if (currentContract != null) {
            failedContracts.add(currentContract);
            updateReputation(-(currentContract.getReputationPayout()) / 2);
            endContract();
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void acceptContract(Train train, Contract contract) {
        currentContract = contract;
        activeTrain = new ActiveTrain(train, contract.getInitialRoute());
        contractStartTime = System.currentTimeMillis();
    }
    
    public boolean isContractOutOfTime() {
    	if(currentContract == null)
    		return false;
    	return (System.currentTimeMillis() - contractStartTime) >= currentContract.getTimeLimit()*1000;
    }

    private void endContract() {
        currentContract = null;
        activeTrain = null;
    }

    public void addMoney(int money) {
        if (money < 0) return;
        this.money += money;
    }

    public int getMoney() {
        return money;
    }

    public boolean spendMoney(int money) {
        //Successful spend returns true.

        if (money < 0) return false;

        if (this.money < money) return false;

        this.money -= money;
        return true;
    }

    public void updateReputation(int amount) {
        reputation += amount;
        if (reputation < 0) reputation = 0;
    }
    
    public int getNumberOfAttemptedContracts() {
    	return completedContracts.size() + failedContracts.size();
    }

    public int getReputation() {
        return reputation;
    }
}
