package lys.sepr.mapCreator;

import javax.swing.*;
import java.awt.*;

public class KeyInfo {
    JLabel selectedTrackLabel = new JLabel("Selected Track     ", SwingConstants.LEFT);
    JLabel activeNextTrackLabel = new JLabel("Active Next Track     ", SwingConstants.LEFT);
    JLabel validNextTrackLabel = new JLabel("Valid Next Track     ", SwingConstants.LEFT);
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
        keyPanel.add(activeNextTrackLabel);
        keyPanel.add(validNextTrackLabel);
        keyPanel.add(connectedTrackLabel);
        keyPanel.add(unconnectedTrackLabel);
        keyPanel.add(fastestRouteLabel);
        keyPanel.add(longerRouteLabel);
        keyPanel.add(shorterRouteLabel);
        keyPanel.add(routeExplainLabel);

        selectedTrackLabel.setForeground(mapView.selectedTrackColour);
        activeNextTrackLabel.setForeground(mapView.activeNextTrackColour);
        validNextTrackLabel.setForeground(mapView.validNextTrackColour);
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
