package lys.sepr.game;

import lys.sepr.game.resources.Inventory;
import lys.sepr.game.resources.Train;
import lys.sepr.profile.Profile;

import java.util.ArrayList;
import java.util.List;

public class Player {

    Contract currentContract;
    int money;

    Inventory inventory = new Inventory();
    ActiveTrain activeTrain;

    List<Contract> completedContracts = new ArrayList<Contract>();
    List<Contract> failedContracts = new ArrayList<Contract>();

    int reputation = 0;
    Profile profile;

    Player(int money) {
        this.money = money;
    }

    public void assignContract(Contract contract) {
        currentContract = contract;
    }

    public void completedCurrentContract() {
        if (currentContract != null) completedContracts.add(currentContract);
    }

    public void failedCurrentContract() {
        if (currentContract != null) failedContracts.add(currentContract);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void beginTurn(Train train, Contract contract) {
        activeTrain = new ActiveTrain(train, contract.getRoute());
    }

    public void endTurn() {
        activeTrain = null;
    }
}
