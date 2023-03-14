package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static java.lang.Math.atan2;

/**
 * Vision based marker,
 * can be seen as a very evey-catching shining flag.
 * And it makes the surrounding bright area.
 * in this area, the guards have greater vision distance
 * (like the opposite to shade area)
 *
 * The guards can use it to mark the corner of the map/teleport
 *
 * This kind of marker will last forever
 *
 */
public class VisionMarker extends Marker{
    /*
    private int visibleRadius = 8;
    private boolean isBlockedNorth = false;
    private boolean isBlockedWest = false;
    private boolean isBlockedSouth = false;
    private boolean isBlockedEast = false;
    private boolean isBlockedNorthEast = false;
    private boolean isBlockedNorthWest = false;
    private boolean isBlockedSouthEast= false;
    private boolean isBlockedSouthWest = false;
    */

    // the radius of the bright area
    private int brightRadius = 8;

    public VisionMarker(Grid markerGrid, Board b) {
        super(markerGrid,b);
        markerType = "Vision Marker";

    }


    /**
     * check whether the grid is inside the game board
     */
    public boolean isLegalGrid(int x, int y){
        if(x>=0 && y>=0 && x< board.length && y<board[0].length){
            return true;
        }
        return false;
    }


    public void setBrightArea(){
        int startX = markerGrid.getX()-brightRadius;
        int startY = markerGrid.getY()-brightRadius;
        for(int i = 0;i<(2*brightRadius)+2;i++){
            for(int j=0;j<(2*brightRadius)+2;j++){
                if(isLegalGrid(startX+i,startY+j)){
                    if(isInRadius(startX+i,startY+j)&&(!board[startX+i][startY+j].isWall())){
                        board[startX+i][startY+j].setBright(true);
                        //System.out.println("x: "+(startX+i)+" y: "+(startY+j));
                    }
                }
            }
        }
    }



    public boolean isInRadius(int gridX, int gridY){
        double distance =  Point2D.distance(gridX, gridY, markerGrid.getX(),markerGrid.getY());
        if(distance<=brightRadius){
            return true;
        }
        return false;
    }


    /*
    // mark a certain visible grid with its intensity
    public void markVisibleGrid(int x, int y, double intensity){

        board[x][y].setInVisionMarkerArea(true);
        board[x][y].setVisionMarkerIntensity(intensity);

    }
     */
 /*
    public void markVisibleArea(){

        int x = markerGrid.getX();
        int y = markerGrid.getY();

        for(int i = 0 ;i<visibleRadius;i++) {

            double intensity = (visibleRadius - i) / visibleRadius;//closer to the marker, stronger the intensity

            if(!isBlockedEast){
                if(!board[x][y+i].isWall()){
                    markVisibleGrid(x,y+i,intensity);
                }
                else{
                    isBlockedEast = true;
                }
            }

            if(!isBlockedWest){
                if(!board[x][y-i].isWall()){
                    markVisibleGrid(x,y-i,intensity);
                }
                else{
                    isBlockedWest = true;
                }
            }

            if(!isBlockedNorth){
                if(!board[x-i][y].isWall()){
                    markVisibleGrid(x-i,y,intensity);
                }
                else{
                    isBlockedNorth = true;
                }
            }

            if(!isBlockedSouth){
                if(!board[x+i][y].isWall()){
                    markVisibleGrid(x+i,y,intensity);
                }
                else{
                    isBlockedSouth = true;
                }
            }

            //diagonally

            int delta = (int) Math.round((i+0.01)/Math.sqrt(2));

            if(!isBlockedNorthEast){
                if(!board[x-delta][y+delta].isWall()){
                    markVisibleGrid(x-delta,y+delta,intensity);
                }
                else{
                    isBlockedNorthEast = true;
                }
            }

            if(!isBlockedNorthWest){
                if(!board[x-delta][y-delta].isWall()){
                    markVisibleGrid(x-delta,y-delta,intensity);
                }
                else{
                    isBlockedNorthEast = true;
                }
            }

            if(!isBlockedSouthEast){
                if(!board[x+delta][y+delta].isWall()){
                    markVisibleGrid(x+delta,y+delta,intensity);
                }
                else{
                    isBlockedSouthEast = true;
                }
            }

            if(!isBlockedSouthWest){
                if(!board[x+delta][y-delta].isWall()){
                    markVisibleGrid(x+delta,y-delta,intensity);
                }
                else{
                    isBlockedSouthWest = true;
                }
            }

        }
    }
    */

    /*
    public double getOrientation(int agentX,int agentY){

        double angle = 180-(atan2(markerGrid.getY() - agentY, markerGrid.getX() - agentX) * 180 / Math.PI) ;
        return angle;
    }*/

}
