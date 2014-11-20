package lys.sepr.game.world;

import org.junit.Before;
import org.junit.Test;

import java.util.InputMismatchException;

import static org.junit.Assert.*;

public class PointTest {

    private Point point;

    @Before
    public void setUp() throws Exception {
        this.point = new Point(50, 60);
    }

    @Test
    public void testGetX() throws Exception {
        assertEquals(50, point.getX(), 0.0f);
    }

    @Test
    public void testGetY() throws Exception {
        assertEquals(60, point.getY(), 0.0f);
    }

    @Test
    public void testMove() throws Exception {
        point.move(100, 90);
        assertEquals(100, point.getX(), 0.0f);
        assertEquals(90, point.getY(), 0.0f);
    }

    @Test
    public void testTranslate() throws Exception {
        point.translate(20, 30);
        assertEquals(70, point.getX(), 0.0f);
        assertEquals(90, point.getY(), 0.0f);
    }

    @Test(expected = InputMismatchException.class)
         public void testBadSetUp() throws Exception {
        Point badPoint = new Point(-100, -50);
    }

    @Test
    public void testBadMove() throws Exception {
        float oldX = point.getX();
        float oldY = point.getY();

        point.move(-100, -50);

        assertEquals(oldX, point.getX(), 0.0f);
        assertEquals(oldY, point.getY(), 0.0f);
    }

    @Test
    public void testBadTranslate() throws Exception {
        float oldX = point.getX();
        float oldY = point.getY();

        point.translate(-51, -61);

        assertEquals(oldX, point.getX(), 0.0f);
        assertEquals(oldY, point.getY(), 0.0f);
    }

}