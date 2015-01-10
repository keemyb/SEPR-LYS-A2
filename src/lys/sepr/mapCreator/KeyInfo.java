package lys.sepr.mapCreator;

import javax.swing.*;
import java.awt.*;

public class KeyInfo {
    JLabel selectedTrackLabel = new JLabel("Selected Track     ", SwingConstants.LEFT);
    JLabel activeConnectionTrackLabel = new JLabel("Active Connection     ", SwingConstants.LEFT);
    JLabel validConnectionTrackLabel = new JLabel("Valid Connection     ", SwingConstants.LEFT);
    JLabel connectedTrackLabel = new JLabel("Connected (Non traversable) Track     ", SwingConstants.LEFT);
    JLabel unconnectedTrackLabel = new JLabel("Unconnected Track     ", SwingConstants.LEFT);
    JLabel fastestRouteLabel = new JLabel("     Fastest Route     ", SwingConstants.LEFT);
    JLabel longerRouteLabel = new JLabel("Longer Route     ", SwingConstants.LEFT);
    JLabel shorterRouteLabel = new JLabel("Shorter Route     ", SwingConstants.LEFT);
    JLabel routeExplainLabel = new JLabel("Longer routes are less intense in saturation", SwingConstants.LEFT);

    private JPanel keyPanel = new JPanel();

    KeyInfo(MapView mapView) {
        keyPanel.setLayout(new BoxLayout(keyPanel, BoxLayout.X_AXIS));

        keyPanel.add(selectedTrackLabel);
        keyPanel.add(activeConnectionTrackLabel);
        keyPanel.add(validConnectionTrackLabel);
        keyPanel.add(connectedTrackLabel);
        keyPanel.add(unconnectedTrackLabel);
        keyPanel.add(fastestRouteLabel);
        keyPanel.add(longerRouteLabel);
        keyPanel.add(shorterRouteLabel);
        keyPanel.add(routeExplainLabel);

        selectedTrackLabel.setForeground(mapView.selectedTrackColour);
        activeConnectionTrackLabel.setForeground(mapView.activeConnectedTrackColour);
        validConnectionTrackLabel.setForeground(mapView.validConnectedTrackColour);
        connectedTrackLabel.setForeground(mapView.connectedTrackColour);
        unconnectedTrackLabel.setForeground(mapView.unconnectedTrackColour);
        fastestRouteLabel.setForeground(mapView.selectedTrackColour);
        longerRouteLabel.setForeground(Color.GREEN.darker());
        shorterRouteLabel.setForeground(Color.GREEN.darker().darker().darker());
    }

    public JPanel getKeyPanel() {
        return keyPanel;
    }
}
