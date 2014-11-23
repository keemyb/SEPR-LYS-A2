package lys.sepr.game.world;

import javax.swing.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Vector;

import static java.lang.Math.*;

public final class Utilities {

    public static double crossProduct(ArrayList<Double> vector1, ArrayList<Double> vector2) {
        double dotProduct = dotProduct(vector1, vector2);
        double magnitude = magnitude(vector1) * magnitude(vector2);
        double cosTheta = dotProduct / magnitude;
        double theta = acos(cosTheta);
        return (theta * 180 / PI);
    }

    public static double dotProduct(ArrayList<Double> vector1, ArrayList<Double> vector2) {
        double dotProduct = 0;
        for (int i=0; i <vector1.size(); i++) {
            dotProduct += vector1.get(i) * vector2.get(i);
        }
        return dotProduct;
    }

    public static double magnitude(ArrayList<Double> vector) {
        double magnitude = 0;
        for (double component : vector) {
            magnitude += pow(component, 2);
        }
        return sqrt(magnitude);
    }

    public static ArrayList<Double> getVector(Point from, Point towards) {
        ArrayList<Double> vector = new ArrayList<Double>(2);
        vector.add(towards.getX() - from.getX());
        vector.add(towards.getY() - from.getY());
        return vector;
    }

    public static Point closestPoint(Point to, ArrayList<Point> points) {
        Point closestPoint = null;
        Double closestDistance = null;
        for (Point point : points) {
            Double distance = magnitude(getVector(to, point));
            if (closestDistance == null || distance < closestDistance) {
                closestDistance = distance;
                closestPoint = point;
            }
        }
        return closestPoint;
    }

    public static Point clickPointToTrackPoint(java.awt.Point clickPoint, JFrame jFrame) {
        /* y axis points have been inverted as the window coordinates start from the top left
        where as the points start from the bottom left
        */
        double clickPointX = clickPoint.x;
        double clickPointY = jFrame.getHeight() - clickPoint.y;
        return new Point(clickPointX, clickPointY);
    }

    public static java.awt.Point trackPointToClickPoint(Point point, JFrame jFrame) {
        /* y axis points have been inverted as the window coordinates start from the top left
        where as the points start from the bottom left
        */
        double pointX = point.getX();
        double pointY = jFrame.getHeight() - point.getY();
        return new java.awt.Point((int) pointX, (int) pointY);
    }

    public static double distance(Point point1, Point point2) {
        return magnitude(getVector(point1, point2));
    }

    public static Line2D.Double trackToLine2D(Track track, JFrame jFrame) {
        double pointX1 = track.getPoints().get(0).getX();
        double pointY1 = jFrame.getHeight() - track.getPoints().get(0).getY();
        double pointX2 = track.getPoints().get(1).getX();
        double pointY2 = jFrame.getHeight() - track.getPoints().get(1).getY();
        return new Line2D.Double(pointX1, pointY1, pointX2, pointY2);
    }

    public static ArrayList<Double> unitVector(ArrayList<Double> vector) {
        ArrayList<Double> unitVector = new ArrayList<Double>();
        double size = magnitude(vector);
        for (double component : vector) {
            unitVector.add(component/size);
        }
        return unitVector;
    }

    public static ArrayList<Double> multiply(ArrayList<Double> vector, double constant) {
        ArrayList<Double> newVector = new ArrayList<Double>();
        for (double component : vector) {
            newVector.add(component * constant);
        }
        return newVector;
    }
}
