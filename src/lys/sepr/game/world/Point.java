package lys.sepr.game.world;

import java.util.InputMismatchException;

public class Point {

    private float x;
    private float y;

    Point(float x, float y) {
        setX(x);
        setY(y);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    private void setY(float y){
        if (y < 0) {
            throw new InputMismatchException("y must be greater than 0");
        } else {
            this.y = y;
        }
    }

    private void setX(float x) {
        if (x < 0) {
            throw new InputMismatchException("x must be greater than 0");
        } else {
            this.x = x;
        }
    }

    public void move(float x, float y) {
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

    public void translate(float x, float y) {
        float newX = x + getX();
        float newY = y + getY();

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

        if (Float.compare(point.x, x) != 0) return false;
        if (Float.compare(point.y, y) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }
}
