package lys.sepr.mapCreator;

import javax.swing.*;

public class KeyInfo {
    JLabel selectedTrackLabel = new JLabel("Selected Track     ", SwingConstants.LEFT);
    JLabel activeNextTrackLabel = new JLabel("Active Next Track     ", SwingConstants.LEFT);
    JLabel validNextTrackLabel = new JLabel("Valid Next Track     ", SwingConstants.LEFT);
    JLabel connectedTrackLabel = new JLabel("Connected (Non traversable) Track     ", SwingConstants.LEFT);
    JLabel unconnectedTrackLabel = new JLabel("Unconnected Track     ", SwingConstants.LEFT);

    private JPanel keyPanel = new JPanel();

    KeyInfo(MapView mapView) {
        keyPanel.setLayout(new BoxLayout(keyPanel, BoxLayout.X_AXIS));

        keyPanel.add(selectedTrackLabel);
        keyPanel.add(activeNextTrackLabel);
        keyPanel.add(validNextTrackLabel);
        keyPanel.add(connectedTrackLabel);
        keyPanel.add(unconnectedTrackLabel);

        selectedTrackLabel.setForeground(mapView.selectedTrackColour);
        activeNextTrackLabel.setForeground(mapView.activeNextTrackColour);
        validNextTrackLabel.setForeground(mapView.validNextTrackColour);
        connectedTrackLabel.setForeground(mapView.connectedTrackColour);
        unconnectedTrackLabel.setForeground(mapView.unconnectedTrackColour);
    }

    public JPanel getKeyPanel() {
        return keyPanel;
    }
}
