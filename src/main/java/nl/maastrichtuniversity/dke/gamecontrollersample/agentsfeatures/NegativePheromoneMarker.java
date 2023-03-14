package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

/**
 * Negative Pheromone is a kind of
 * Territorial Pheromones Marker
 * Laid down in the environment, these pheromones mark the boundaries of an organism's territory.
 *
 * The guard can use this marker to mark its own watched area,
 * this can avoid different guards explore one area repeatedly.
 *
 * This type of marker will only exist a
 */
public class NegativePheromoneMarker extends Marker{

    private int negPheExistTim = 5;// the marker will disappear after certain time step
    private int startTimestep;// the time that put this marker

    public NegativePheromoneMarker(Grid markerGrid, int startTimestep, Board b) {
        super(markerGrid,b);
        markerType = "NegativePheromoneMarker (Territorial Pheromone)";
        this.startTimestep = startTimestep;
    }

    public int getNegPheExistTim(){
        return negPheExistTim;
    }

    //true if exceed existing time, and this marker should be removed
    public boolean exceedExistingTime(int currentTimestep){
        //System.out.println((negPheExistTim + startTimestep)<currentTimestep);
        return((negPheExistTim + startTimestep)<currentTimestep);

    }


    public int getStartTimestep(){
        return startTimestep;
    }


}