package lys.sepr.mapCreator;

import javax.swing.*;
import java.awt.*;

public class KeyInfo {
    JLabel selectedTrackLabel = new JLabel("Selected Track     ", SwingConstants.LEFT);
    JLabel activeConnectedTrackLabel = new JLabel("Active Connection     ", SwingConstants.LEFT);
    JLabel validConnectedTrackLabel = new JLabel("Valid Connection     ", SwingConstants.LEFT);
    JLabel invalidConnectedTrackLabel = new JLabel("Invalid Connection     ", SwingConstants.LEFT);
    JLabel unconnectedTrackLabel = new JLabel("Unconnected Track     ", SwingConstants.LEFT);
    JLabel fastestRouteLabel = new JLabel("     Fastest Route     ", SwingConstants.LEFT);
    JLabel longerRouteLabel = new JLabel("Longer Route     ", SwingConstants.LEFT);
    JLabel shorterRouteLabel = new JLabel("Shorter Route     ", SwingConstants.LEFT);
    JLabel routeExplainLabel = new JLabel("Longer routes are less intense in saturation", SwingConstants.LEFT);

    private JPanel keyPanel = new JPanel();

    KeyInfo(MapView mapView) {
        keyPanel.setLayout(new BoxLayout(keyPanel, BoxLayout.X_AXIS));

        keyPanel.add(selectedTrackLabel);
        keyPanel.add(activeConnectedTrackLabel);
        keyPanel.add(validConnectedTrackLabel);
        keyPanel.add(invalidConnectedTrackLabel);
        keyPanel.add(unconnectedTrackLabel);
        keyPanel.add(fastestRouteLabel);
        keyPanel.add(longerRouteLabel);
        keyPanel.add(shorterRouteLabel);
        keyPanel.add(routeExplainLabel);

        selectedTrackLabel.setForeground(mapView.selectedTrackColour);
        activeConnectedTrackLabel.setForeground(mapView.activeConnectedTrackColour);
        validConnectedTrackLabel.setForeground(mapView.validConnectedTrackColour);
        invalidConnectedTrackLabel.setForeground(mapView.invalidConnectedTrackColour);
        unconnectedTrackLabel.setForeground(mapView.unconnectedTrackColour);
        fastestRouteLabel.setForeground(mapView.selectedTrackColour);
        longerRouteLabel.setForeground(Color.GREEN.darker());
        shorterRouteLabel.setForeground(Color.GREEN.darker().darker().darker());
    }

    public JPanel getKeyPanel() {
        return keyPanel;
    }
}
