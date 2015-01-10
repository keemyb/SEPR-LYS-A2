package lys.sepr.game;

import lys.sepr.game.resources.Inventory;
import lys.sepr.game.resources.Train;
import lys.sepr.profile.Profile;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private Contract currentContract;
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

    public void assignContract(Contract contract) {
        currentContract = contract;
    }

    public void completedCurrentContract() {
        if (currentContract != null) completedContracts.add(currentContract);
        addMoney(currentContract.getMoneyPayout());
        updateReputation(currentContract.getReputationPayout());
        endTurn();
    }

    public void failedCurrentContract() {
        if (currentContract != null) failedContracts.add(currentContract);
        updateReputation(-(currentContract.getReputationPayout()) / 2);
        endTurn();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void beginTurn(Train train, Contract contract) {
        activeTrain = new ActiveTrain(train, contract.getInitialRoute());
    }

    private void endTurn() {
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

    public int getReputation() {
        return reputation;
    }
}
