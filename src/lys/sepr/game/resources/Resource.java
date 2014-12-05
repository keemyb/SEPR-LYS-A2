package lys.sepr.game.resources;

public abstract class Resource {

    public String name;
    public Integer price;
    public Integer reqReputation;
    public Integer maxAllowed;

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getReqReputation() {
        return reqReputation;
    }

    public void setReqReputation(Integer reqReputation) {
        this.reqReputation = reqReputation;
    }

    public Integer getMaxAllowed() {
        return maxAllowed;
    }

    public void setMaxAllowed(Integer maxAllowed) {
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
