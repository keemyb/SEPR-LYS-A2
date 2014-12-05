package lys.sepr.game.resources;

public abstract class Resource {

    public String name;
    public int price;
    public int reqReputation;
    public int maxAllowed;

    Resource(String name, int price, int reqReputation, int maxAllowed) {
        this.name = name;
        this.price = price;
        this.reqReputation = reqReputation;
        this.maxAllowed = maxAllowed;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getReqReputation() {
        return reqReputation;
    }

    public void setReqReputation(int reqReputation) {
        this.reqReputation = reqReputation;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }

    public void setMaxAllowed(int maxAllowed) {
        this.maxAllowed = maxAllowed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void useResource(Resource resource){

    }

}
