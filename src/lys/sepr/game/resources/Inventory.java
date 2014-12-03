package lys.sepr.game.resources;

import java.util.ArrayList;
import java.util.HashMap;

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

    public void createInventory () {

    }

    public void initialiseInventory () {

    }

    public Boolean checkResource (Resource resource) {
        return contents.contains(resource);
    }

    public void applyResource (Resource resource) {
        if (!checkResource(resource)) {
            resourceNotInInventoryError("use");
        } else {
            resource.useResource(resource);                  //how specify specific resource if two of same???  index??
        }
    }

    public void discardResource (Resource resource) {
        if (!checkResource(resource)) {
            resourceNotInInventoryError("remove");
        } else {
            contents.remove(resource);                      //how specify specific resource if two of same???  index??
        }
    }

    public void addNewResource (Resource resource) {
        if (resource instanceof Fuel) {
            if (checkResource(resource)) {
                if ((resource.quantity).equals(resource.maxAllowed)) {
                    maximumResourceError(resource);
                } else {
                    resource.quantity += ((Fuel) resource).getValue();   //value is the quantity of fuel bought at once
                }
            } else if (contents.size() == MAX_CAPACITY) {
                inventoryFullError();
            } else {
                contents.add(resource);
                resource.quantity += ((Fuel) resource).getValue();
            }
        } else if (contents.size() == MAX_CAPACITY) {
            inventoryFullError();
        } else {
            if ((resource.quantity).equals(resource.maxAllowed)) {
                maximumResourceError(resource);
            } else {
                contents.add(resource);
                resource.quantity += 1;
            }
        }
    }


}
