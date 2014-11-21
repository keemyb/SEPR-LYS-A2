package lys.sepr.game.world;

import java.util.InputMismatchException;

public class Point {

    private double x;
    private double y;

    Point(double x, double y) {
        setX(x);
        setY(y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    private void setY(double y){
        if (y < 0) {
            throw new InputMismatchException("y must be greater than 0");
        } else {
            this.y = y;
        }
    }

    private void setX(double x) {
        if (x < 0) {
            throw new InputMismatchException("x must be greater than 0");
        } else {
            this.x = x;
        }
    }

    public void move(double x, double y) {
        try {
            setX(x);
        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
        }

        try {
            setY(y);
        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
        }
    }

    public void translate(double x, double y) {
        double newX = x + getX();
        double newY = y + getY();

        try {
            setX(newX);
        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
        }

        try {
            setY(newY);
        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (Double.compare(point.x, x) != 0) return false;
        if (Double.compare(point.y, y) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
