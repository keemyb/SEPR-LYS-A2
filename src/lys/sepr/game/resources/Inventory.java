package lys.sepr.game.resources;

import java.util.ArrayList;

public class Inventory {
//trains, power-ups, fuel

    public static final int MAX_CAPACITY = 12;
    public ArrayList<Resource> contents= new ArrayList<Resource>();

    public ArrayList<Resource> getContents() {
        return contents;
    }

    public void setContents(ArrayList<Resource> contents) {
        this.contents = contents;
    }

    public void inventoryFullError(){
        //display error message on GUI  -- I'm sorry, but your inventory is already full
    }

    public void resourceNotInInventoryError(String message){
        //display error message on GUI  -- I'm sorry, but you cannot remove/use a resource that you do not have
    }

    public void maximumResourceError(Resource resource){
        //display error message on GUI  -- I'm sorry, but you already own the maximum number of (resource.name)
    }

    public Boolean containsResource(Resource resource) {
        return contents.contains(resource);
    }

    public void applyResource (Resource resource) {
        if (!containsResource(resource)) {
            resourceNotInInventoryError("use");
        } else {
            resource.useResource(resource);                  //how specify specific resource if two of same???  index??
        }
    }

    public void discardResource (Resource resource) {
        if (!containsResource(resource)) {
            resourceNotInInventoryError("remove");
        } else {
            contents.remove(resource);                      //how specify specific resource if two of same???  index??
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
        } else if (contents.size() == MAX_CAPACITY) {
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
            int newQuantity = existingFuel.getQuantity() + fuel.getQuantity();
            if (existingFuel.getQuantity() >= fuel.maxAllowed) {
                existingFuel.setQuantity(existingFuel.getMaxAllowed());
                maximumResourceError(fuel);
            } else {
                existingFuel.setQuantity(newQuantity);
            }
        } else if (contents.size() == MAX_CAPACITY) {
            inventoryFullError();
        } else {
            if (fuel.getQuantity() >= fuel.getMaxAllowed()) {
                fuel.setQuantity(fuel.getMaxAllowed());
            }
            contents.add(fuel);
        }
    }


}
