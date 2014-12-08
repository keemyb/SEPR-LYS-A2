package lys.sepr.mapCreator;

import lys.sepr.game.world.Map;
import lys.sepr.game.world.Track;

import javax.swing.*;
import java.awt.*;

public class MapCreator {

    private static void initialiseMapCreator() {
        Map map = testMap();
        State state = new State();
        MapView mapView = new MapView(map, state);
        KeyInfo keyInfo = new KeyInfo(mapView);
        Buttons buttons = new Buttons(state);

        JFrame MainFrame = new JFrame();
        MainFrame.setSize(1280,720);
        MainFrame.getContentPane().add(keyInfo.getKeyPanel(), BorderLayout.NORTH);
        MainFrame.getContentPane().add(mapView.getMapPanel(), BorderLayout.CENTER);
        MainFrame.getContentPane().add(buttons.getButtonPanel(), BorderLayout.SOUTH);

        MainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainFrame.setVisible(true);
    }

    public static Map testMap() {
        Map map = new Map();

        Track track1 = new Track(new lys.sepr.game.world.Point(0, 0), new lys.sepr.game.world.Point(100, 100));
        Track track2 = new Track(new lys.sepr.game.world.Point(100, 100), new lys.sepr.game.world.Point(200, 200));
        Track track3 = new Track(new lys.sepr.game.world.Point(100, 100), new lys.sepr.game.world.Point(130, 150));
        Track track4 = new Track(new lys.sepr.game.world.Point(130, 150), new lys.sepr.game.world.Point(200, 200));
        Track track5 = new Track(new lys.sepr.game.world.Point(100, 100), new lys.sepr.game.world.Point(160, 150));
        Track track6 = new Track(new lys.sepr.game.world.Point(160, 150), new lys.sepr.game.world.Point(200, 200));
        Track track7 = new Track(new lys.sepr.game.world.Point(200, 200), new lys.sepr.game.world.Point(300, 300));

        map.addTrack(track1);
        map.addTrack(track2);
        map.addTrack(track3);
        map.addTrack(track4);
        map.addTrack(track5);
        map.addTrack(track6);
        map.addTrack(track7);

        return map;
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
