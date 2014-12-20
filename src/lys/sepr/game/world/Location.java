package lys.sepr.game.world;

/**
 * The Location Class represents some Point of Interest.
 */
public class Location{

    private Point point;
    private String name;

    /**
     * Constructor
     * @param point The point where the Location will lie.
     * @param name The name of the location.
     */
    public Location(Point point, String name) {
        this.point = point;
        this.name = name;
    }

    /**
     * Returns the point where the location lies.
     * @return the point where the location lies.
     */
    public Point getPoint() {
        return point;
    }

    /**
     * Gets the name of a location.
     * @return The name of the location
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the name of a location.
     * @param name the new name of the location.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param o The object to be compared.
     * @return Two locations are considered equal if they have the same point
     * and name.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (name != null ? !name.equals(location.name) : location.name != null) return false;
        if (point != null ? !point.equals(location.point) : location.point != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = point != null ? point.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
