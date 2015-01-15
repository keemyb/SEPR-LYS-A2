package lys.sepr.ui;

import lys.sepr.game.world.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MainMapPanel extends JPanel {

    private Map map;
    private State state;

    private final double locationSize = 10d;

    private Double minPickupDistance = 20d;

    private boolean painting;

    public void setMap(Map map) {
        this.map = map;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        lys.sepr.ui.Actions.drawMap(map, locationSize, state, g2);
    }

}
