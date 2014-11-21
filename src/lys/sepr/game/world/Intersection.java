package lys.sepr.game.world;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.*;

public class Intersection {

    private int minAngle = 120;
    private Point point;
    private ArrayList<Track> tracks = new ArrayList<Track>();
    private HashMap<Track,ArrayList<Track>> validNextTracks = new HashMap<Track,ArrayList<Track>>();

    Intersection(Point point, Track a, Track b) {
        this.point = point;

        tracks.add(a);
        tracks.add(b);

        a.addIntersection(this);
        b.addIntersection(this);

        a.addConnectedTrack(b);
        b.addConnectedTrack(a);

        updateValidTracks();
    }

    public void addTrack(Track track) {
        for (Track existingTrack : tracks) {
            existingTrack.addConnectedTrack(track);
            track.addConnectedTrack(existingTrack);
        }
        tracks.add(track);
        track.addIntersection(this);

        updateValidTracks();
    }

    public void updateValidTracks() {
        validNextTracks = new HashMap<Track,ArrayList<Track>>();
        for (Track track1 : tracks) {
            ArrayList<Double> vector1 = track1.getVector(point);
            for (Track track2 : tracks) {
                if (track1 == track2) continue;
                ArrayList<Double> vector2 = track2.getVector(point);
                double angle = crossProduct(vector1, vector2);
                if (validAngle(angle)) {
                    if (validNextTracks.get(track1) == null) {
                        validNextTracks.put(track1, new ArrayList<Track>());
                    }
                    validNextTracks.get(track1).add(track2);
//                    if (!validNextTracks.get(track1).contains(track2)){
//                        validNextTracks.get(track1).add(track2);
//                    }
//                    if (validNextTracks.get(track2) == null) {
//                        validNextTracks.put(track2, new ArrayList<Track>());
//                    }
//                    if (!validNextTracks.get(track2).contains(track1)){
//                        validNextTracks.get(track2).add(track1);
//                    }
                }
            }
        }
    }

    private boolean validAngle(double angle) {
        return angle >= minAngle;
    }
    
    public double crossProduct(ArrayList<Double> vector1, ArrayList<Double> vector2) {
        double dotProduct = dotProduct(vector1, vector2);
        double magnitude = magnitude(vector1) * magnitude(vector2);
        double cosTheta = dotProduct / magnitude;
        double theta = acos(cosTheta);
        return (theta * 180 / PI);
    }

    private double dotProduct(ArrayList<Double> vector1, ArrayList<Double> vector2) {
        double dotProduct = 0;
        for (int i=0; i <vector1.size(); i++) {
            dotProduct += vector1.get(i) * vector2.get(i);
        }
        return dotProduct;
    }

    private double magnitude(ArrayList<Double> vector) {
        double magnitude = 0;
        for (double component : vector) {
            magnitude += pow(component, 2);
        }
        return sqrt(magnitude);
    }

    public Point getPoint() {
        return point;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public ArrayList<Track> getValidNextTracks(Track track) {
        return validNextTracks.get(track);
    }

}
