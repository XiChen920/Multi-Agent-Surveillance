package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

public class Marker{
    protected Grid markerGrid;  // the marker position
    protected String markerType;

    protected Grid[][] board;

    public Marker(Grid markerGrid, Board gameBoard){

        this.markerGrid = markerGrid;
        this.board = gameBoard.getBoardXY();
    }



    public Grid getMarkerGrid() {
        return markerGrid;
    }

    public void setMarkerGrid(Grid markerGrid) {
        this.markerGrid = markerGrid;
    }


    public String getMarkerType() {
        return markerType;
    }


}