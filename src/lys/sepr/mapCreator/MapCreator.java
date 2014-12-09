package lys.sepr.mapCreator;

import javax.swing.*;
import java.awt.*;

public class MapCreator {

    private static void initialiseMapCreator() {
        State state = new State();
        MapView mapView = new MapView(state);
        KeyInfo keyInfo = new KeyInfo(mapView);
        Buttons buttons = new Buttons(state, mapView);

        JFrame MainFrame = new JFrame();
        MainFrame.setSize(1280,720);
        MainFrame.getContentPane().add(keyInfo.getKeyPanel(), BorderLayout.NORTH);
        MainFrame.getContentPane().add(mapView.getScrollPane(), BorderLayout.CENTER);
        MainFrame.getContentPane().add(buttons.getButtonPanel(), BorderLayout.SOUTH);

        MainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainFrame.setVisible(true);
    }

    public static void main (String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initialiseMapCreator();
            }
        });
    }

}
