package lys.sepr.mapCreator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Buttons {
    private ButtonGroup buttonGroup = new ButtonGroup();

    JRadioButton createTrackModeButton = new JRadioButton("Create Track");
    JRadioButton moveModeButton = new JRadioButton("Move");
    JRadioButton inspectTrackModeButton = new JRadioButton("Inspect Track");
    JRadioButton deleteLocationModeButton = new JRadioButton("Delete Location");
    JRadioButton deleteTrackModeButton = new JRadioButton("Delete Track");
    JRadioButton deleteIntersectionModeButton = new JRadioButton("Delete Intersection");
    JRadioButton createLocationModeButton = new JRadioButton("Create Location");
    JRadioButton inspectRouteModeButton = new JRadioButton("Inspect Route");
    JRadioButton breakTrackModeButton = new JRadioButton("Break Track");
    JRadioButton renameLocationButton = new JRadioButton("Rename Location");
    JButton saveMapButton = new JButton("Save Map");
    JButton loadMapButton = new JButton("Load Map");
    JButton zoomInButton = new JButton("Zoom In");
    JButton zoomOutButton = new JButton("Zoom Out");
    JButton zoomResetButton = new JButton("Reset Zoom");
    JCheckBox showLocationNamesCheckBox = new JCheckBox("Show Location Names");

    private JPanel buttonPanel = new JPanel();

    private Actions actions = new Actions();

    public JPanel getButtonPanel() {
        return buttonPanel;
    }

    Buttons(final State state, final MapView mapView) {
        buttonGroup.add(inspectTrackModeButton);
        buttonGroup.add(createTrackModeButton);
        buttonGroup.add(moveModeButton);
        buttonGroup.add(deleteLocationModeButton);
        buttonGroup.add(deleteTrackModeButton);
        buttonGroup.add(deleteIntersectionModeButton);
        buttonGroup.add(createLocationModeButton);
        buttonGroup.add(inspectRouteModeButton);
        buttonGroup.add(breakTrackModeButton);
        buttonGroup.add(renameLocationButton);

        buttonPanel.add(inspectTrackModeButton);
        buttonPanel.add(inspectRouteModeButton);
        buttonPanel.add(createTrackModeButton);
        buttonPanel.add(createLocationModeButton);
        buttonPanel.add(moveModeButton);
        buttonPanel.add(deleteLocationModeButton);
        buttonPanel.add(deleteTrackModeButton);
        buttonPanel.add(deleteIntersectionModeButton);
        buttonPanel.add(breakTrackModeButton);
        buttonPanel.add(renameLocationButton);
        buttonPanel.add(loadMapButton);
        buttonPanel.add(saveMapButton);
        buttonPanel.add(zoomInButton);
        buttonPanel.add(zoomOutButton);
        buttonPanel.add(zoomResetButton);
        buttonPanel.add(showLocationNamesCheckBox);

        inspectTrackModeButton.setSelected(true);

        showLocationNamesCheckBox.setSelected(true);

        createTrackModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.dropHeldLocationTrackIntersection(state);
                state.setMode(state.CREATE_TRACK_MODE);
            }
        });

        moveModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.clearCreateNew(state);
                state.setMode(state.MOVE_MODE);
            }
        });

        inspectTrackModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.clearCreateNew(state);
                actions.dropHeldLocationTrackIntersection(state);
                actions.clearInspect(state);
                state.setMode(state.INSPECT_TRACK_MODE);
            }
        });

        deleteTrackModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.clearCreateNew(state);
                actions.dropHeldLocationTrackIntersection(state);
                state.setMode(state.DELETE_TRACK_MODE);
            }
        });

        deleteLocationModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.clearCreateNew(state);
                actions.dropHeldLocationTrackIntersection(state);
                state.setMode(state.DELETE_LOCATION_MODE);
            }
        });

        deleteIntersectionModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.clearCreateNew(state);
                actions.dropHeldLocationTrackIntersection(state);
                state.setMode(state.DELETE_INTERSECTION_MODE);
            }
        });

        createLocationModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.clearCreateNew(state);
                actions.dropHeldLocationTrackIntersection(state);
                state.setMode(state.CREATE_LOCATION_MODE);
            }
        });

        inspectRouteModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.clearCreateNew(state);
                actions.dropHeldLocationTrackIntersection(state);
                state.setMode(state.INSPECT_ROUTE_MODE);
            }
        });

        breakTrackModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.clearCreateNew(state);
                actions.dropHeldLocationTrackIntersection(state);
                state.setMode(state.BREAK_TRACK_MODE);
            }
        });

        saveMapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.saveMap(mapView.getMap(), mapView.getMapPanel());
            }
        });

        loadMapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.loadMapAndBackground(mapView, mapView.getMapPanel());
            }
        });

        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.zoomIn(state);
                mapView.getMapPanel().repaint();
            }
        });

        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.zoomOut(state);
                mapView.getMapPanel().repaint();
            }
        });

        zoomResetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actions.resetZoom(state);
                mapView.getMapPanel().repaint();
            }
        });

        renameLocationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                state.setMode(state.RENAME_LOCATION_MODE);
            }
        });

        showLocationNamesCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                state.setShowLocationNames(!state.isShowingLocationNames());
                mapView.getMapPanel().repaint();
            }
        });
    }
}
