package lys.sepr.game.resources;

public abstract class Resource {

    // Resources are considered equal if they have the same name
    private String name;
    private int price;
    private int reqReputation;
    private int maxAllowed;

    Resource(String name, int price, int reqReputation, int maxAllowed) {
        this.name = name;
        this.price = price;
        this.reqReputation = reqReputation;
        this.maxAllowed = maxAllowed;
    }

    protected Resource() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        if (name != null ? !name.equals(resource.name) : resource.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
