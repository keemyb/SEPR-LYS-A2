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
    JButton saveMapButton = new JButton("Save Map");
    JButton loadMapButton = new JButton("Load Map");

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

        buttonPanel.add(inspectTrackModeButton);
        buttonPanel.add(inspectRouteModeButton);
        buttonPanel.add(createTrackModeButton);
        buttonPanel.add(createLocationModeButton);
        buttonPanel.add(moveModeButton);
        buttonPanel.add(deleteLocationModeButton);
        buttonPanel.add(deleteTrackModeButton);
        buttonPanel.add(deleteIntersectionModeButton);
        buttonPanel.add(breakTrackModeButton);
        buttonPanel.add(loadMapButton);
        buttonPanel.add(saveMapButton);

        inspectTrackModeButton.setSelected(true);

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
    }
}
