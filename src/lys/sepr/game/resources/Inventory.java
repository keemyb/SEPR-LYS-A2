package lys.sepr.game.resources;

import lys.sepr.game.Player;
import lys.sepr.ui.Dialog;

import java.util.ArrayList;

public class Inventory {
//trains, power-ups, fuel

    private static final int MAX_CAPACITY = 7; //can be edited at a later date
    private ArrayList<Resource> contents= new ArrayList<Resource>();

    public ArrayList<Resource> getContents() {
        return contents;
    }

    public void setContents(ArrayList<Resource> contents) {
        this.contents = contents;
    }

    public int getSize() {
        return contents.size();
    }

    public double getQuantity(Fuel fuel) {
        if (containsResource(fuel)) {
            Fuel existingFuel = (Fuel) contents.get(contents.indexOf(fuel));
            return existingFuel.getQuantity();
        } else return 0;
    }

    private void inventoryFullError(){
        Dialog.error("I'm sorry, but your inventory is already full");
        //display error message on GUI
    }

    private void resourceNotInInventoryError(){
        Dialog.error("I'm sorry, but you cannot remove a resource that you do not have");
        //display error message on GUI
    }

    private void maximumResourceError(Resource resource){
        Dialog.error("I'm sorry, but you already own the maximum number of" + resource.getName());
        //display error message on GUI
    }

    public Boolean containsResource(Resource resource) {
        return contents.contains(resource);
    }

    public void discardResource (Resource resource) {
        if (!containsResource(resource)) {
            resourceNotInInventoryError();
        } else {
            contents.remove(resource);
        }
    }

    public int getOccurrencesOfResource(Resource resource) {
        // Checking how many of the same item are in the inventory,
        // where resources are classed as the same if they have the same name.
        // This is necessary since two trains of the same model are distinct,
        // but not the same object.

        if (contents.contains(resource)) {
            int Occurrences = 0;
            for (Resource currentResources : contents) {
                if (currentResources.getName().equals(resource.getName())) {
                    Occurrences += 1;
                }
            }
            return Occurrences;
        } else return 0;
    }

    public void addNewResource (Resource resource) {
        if (resource instanceof Fuel) {
            // casting resource to fuel
            addNewResource((Fuel) resource);
        } else if (getSize() == MAX_CAPACITY) {
            inventoryFullError();
        } else {
            int occurrences = getOccurrencesOfResource(resource);
            if (occurrences >= resource.getMaxAllowed()) {
                maximumResourceError(resource);
            } else {
                contents.add(resource);
            }
        }
    }

    public void addNewResource (Fuel fuel) {
        if (containsResource(fuel)) {
            Fuel existingFuel = (Fuel) contents.get(contents.indexOf(fuel));
            double newQuantity = existingFuel.getQuantity() + fuel.getQuantity();
            if (newQuantity >= fuel.getMaxAllowed()) {
                existingFuel.setQuantity(existingFuel.getMaxAllowed());
                maximumResourceError(fuel);
            } else {
                existingFuel.setQuantity(newQuantity);
            }
        } else if (getSize() == MAX_CAPACITY) {
            inventoryFullError();
        } else {
            if (fuel.getQuantity() >= fuel.getMaxAllowed()) {
                fuel.setQuantity(fuel.getMaxAllowed());
            }
            contents.add(fuel);
        }
    }


}
