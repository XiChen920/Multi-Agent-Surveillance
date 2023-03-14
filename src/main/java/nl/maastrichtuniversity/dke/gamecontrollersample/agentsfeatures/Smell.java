package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static java.lang.Math.atan2;

public class Smell {
    //smalling capabilities
    private int distanceSmelling = 8;

    //agent point
    private Grid agentPosition;
    private int x;
    private int y;

    //list of all detected markers' position
    private ArrayList<Grid> detectedMarkersPositions;

    //list of all detected markers' orientation
    private ArrayList<Double> markersOrientations;

    //list of current existing marker
    ArrayList<Marker> markers;


    public Smell(Grid agentPosition, ArrayList<Marker> markers){

        this.agentPosition = agentPosition;
        this.x = agentPosition.getX();
        this.y = agentPosition.getY();
        this.markersOrientations = new ArrayList<>();
        this.detectedMarkersPositions = new ArrayList<>();
        this.markers = markers;

    }

    /**
     * An agent can only know the direction of a pheromone marker,
     * without knowing the actual distance
     *
     * @return a list of orientations of the markers within the smelling capability
     */
    public ArrayList<Double> getMarkersOrientations(){
        getDetectedMarkersPositions();
        if(detectedMarkersPositions==null){
            return null;
        }
        for (Grid detectedMarkerPosition : detectedMarkersPositions) {

            int markerX = detectedMarkerPosition.getX();
            int markerY = detectedMarkerPosition.getY();
            double angel = getOrientation(markerX,markerY);

            markersOrientations.add(angel);
        }


        return markersOrientations;
    }


    /**
     * Get the grids that can be smelled by the agent
     *
     * @return a list of grid contains the markers that can be smelled
     */
    public ArrayList<Grid> getDetectedMarkersPositions(){

        if(markers.size()==0){
            return null;
        }
        for(int i =0; i< markers.size();i++){
            if(canBeSmelled(markers.get(i))){

                detectedMarkersPositions.add(markers.get(i).getMarkerGrid());
            }
        }

        return detectedMarkersPositions;

    }


    /**
     * Check whether a pheromone marker can be smelled by the agent
     *
     * @param marker the marker to be checked
     * @return    true if the marker can be smelled
     */
    public boolean canBeSmelled(Marker marker){

        //marker position
        int markerX = marker.getMarkerGrid().getX();
        int markerY = marker.getMarkerGrid().getY();

        double distance = Point2D.distance(markerX, markerY, x, y);

        if(distance <= distanceSmelling){//if the marker is within the smelling distance
            return true;
        }

        return false;

    }

    /**
     * Check the direction of the marker to the agent
     *
     * @param markerX x coordinate of marker
     * @param markerY y coordinate of marker
     * @return the orientation in degree
     */
    public double getOrientation(int markerX,int markerY){

        double angle = 180-(atan2(markerY - y, markerX - x) * 180 / Math.PI) ;
        return angle;
    }



}
