package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

/**
 * Positive pheromones is a kind of
 * Trail pheromones
 * are used for recruitment & marking pathways to resources & indicating resource richness.
 *
 * Guards can use this marker when seeing the intruders.
 * As the Intruders tend to be close to the target,
 * so if an area has many Trail Pheromones Markers, there's great possibility it's target area
 *
 * will disappear after certain time step
 */
public class PositivePheromoneMarker extends Marker{

    private int posPheExistTim = 100;// the marker will disappear after certain time step
    private int startTimestep;// the time that put this marker

    public PositivePheromoneMarker(Grid markerGrid, int startTimestep, Board b) {
        super(markerGrid,b);
        markerType = "PositivePheromoneMarker (Trail Pheromone)";
        this.startTimestep = startTimestep;
    }

    public int getPosPheExistTim(){
        return posPheExistTim;
    }

    //true if exceed existing time, and this marker should be removed
    public boolean exceedExistingTime(int currentTimestep){
        return((posPheExistTim + startTimestep)<currentTimestep);

    }
    public int getStartTimestep(){
        return startTimestep;
    }
}