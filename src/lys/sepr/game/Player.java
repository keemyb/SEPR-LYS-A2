package lys.sepr.game;

import lys.sepr.game.resources.Inventory;
import lys.sepr.game.resources.Train;
import lys.sepr.profile.Profile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player {

    // ensure you update assets and paths to this if making new colours.
    public static enum PlayerColor { RED, GREEN, BLUE, YELLOW }

    // defining the actual colours used to paint routes etc with.
    public static Color getColor (PlayerColor playerColor) {
        switch (playerColor) {
            case RED:
                return Color.RED;
            case GREEN:
                return Color.GREEN;
            case BLUE:
                return Color.BLUE;
            case YELLOW:
                return Color.YELLOW;
            default:
                return Color.WHITE;
        }
    }

    private Contract currentContract;
    private long contractStartTime;
    private int money;
    private PlayerColor playerColor;

    private Inventory inventory = new Inventory();

    private ActiveTrain activeTrain;

    private List<Contract> completedContracts = new ArrayList<Contract>();
    private List<Contract> failedContracts = new ArrayList<Contract>();

    private int reputation = 0;
    private Profile profile;

    public Player(int money, PlayerColor playerColor) {
        this.money = money;
        this.playerColor = playerColor;
    }

    public ActiveTrain getActiveTrain() {
        return activeTrain;
    }

    public long getContractStartTime() {
    	return contractStartTime;
    }
    public void setActiveTrain(ActiveTrain activeTrain) {
        this.activeTrain = activeTrain;
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

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public Color getColor() {
        return getColor(playerColor);
    }
}
