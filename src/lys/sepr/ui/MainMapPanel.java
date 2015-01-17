package lys.sepr.ui;

import lys.sepr.game.Game;
import lys.sepr.game.world.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MainMapPanel extends JPanel {

    private Map map;
    private State state;
    private Game game;

    private final double locationSize = 10d;

    private Double minPickupDistance = 20d;

    private boolean painting;

    public void setState(State state) {
        this.state = state;
    }

    public void setGame(Game game) {
        this.game = game;
        this.map = game.getMap();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        lys.sepr.ui.Actions.drawMap(map, locationSize, state, g2);
        lys.sepr.ui.Actions.drawTrains(game, state, g2);
        lys.sepr.ui.Actions.drawTrainPathOverlay(game.getActivePlayer(), state, g2);
    }

}
