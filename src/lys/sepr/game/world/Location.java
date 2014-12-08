package lys.sepr.game.world;

public class Location{

    private Point point;
    private String name;

    public Location(Point point, String name) {
        this.point = point;
        this.name = name;
    }

    public Point getPoint() {
        return point;
    }

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
