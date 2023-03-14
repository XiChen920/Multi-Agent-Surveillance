package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Bresenham;

import java.util.ArrayList;

public abstract class Sound {
    protected int radius;
    protected double intensity;
    protected int x_src;
    protected int y_src;
    protected Grid[][] board;
    protected ArrayList<Grid> soundArea;

    public Sound(Grid agentPosition, Board gameBoard, int r, double i){
        this.x_src = agentPosition.getX();
        this.y_src = agentPosition.getY();
        this.board = gameBoard.getBoardXY();
        radius = r;
        intensity = i;
        this.soundArea = calculateSoundArea();
        spreadSound();
    }

    // record the information about the sound in the relevant tiles
    public void spreadSound(){
        for(int i=0; i<soundArea.size(); i++){
            markSound(soundArea.get(i));
        }
    }

    public void markSound(Grid cell){
        int x = cell.getX();
        int y = cell.getY();
        double amplitude = calculateAmplitude(x,y);
        double srcOrientation = calculateSrcOrientation(x,y);
        addSound(cell, amplitude, srcOrientation);
    }

    // add the sound to the list of sounds in the current tile
    public void addSound(Grid cell, double amplitude, double srcOrientation){}

    // calculate the amplitude of the sound in the current tile
    public double calculateAmplitude(int x, int y){
        double distance = calculateDistance(x_src,y_src, x,y);
        return intensity/Math.pow(distance,2);
    }

    // calculate orientation from the current tile to the source of the sound
    public double calculateSrcOrientation(int x, int y){
        double tan = Math.abs((double)(x_src-x)/(double) (y_src-y)); // calculating tan for acute angle for simplicity
        double v = Math.atan(tan) * 360 / (2 * Math.PI); // calculating this acute angle
        // further calculations depend on direction (1 out of 4 quadrants)
        if (x == x_src) {
            if (y == y_src) return 0;
            else if (y < y_src) return 90;
            else return 270;
        }

        if (x < x_src){
            if (y < y_src) return 90 + v;
            else return 270 - v;
        }
        else {
            if (y <= y_src) return 90 - v;
            else return 270 + v;
        }
    }

    // calculate the area that is covered by the sound
    public ArrayList<Grid> calculateSoundArea(){
        ArrayList<Grid> area =  new ArrayList<>();
        for (int i= x_src-radius; i<= x_src+radius; i++){
            for (int j= y_src-radius; j<= y_src+radius;j++){
                if( calculateDistance(i,j,x_src,y_src) <= radius+1){
                    if( (i>=0 && i< board.length) && (j>=0 && j< board[0].length) && i != x_src && j != y_src ){
                        if(!Bresenham.isWallBetweenTiles(board, new Grid(i, j), new Grid(x_src, y_src)))
                            area.add(board[i][j]);
                    }
                }
            }
        }
        return area;
    }

    public double calculateDistance(int x1, int y1, int x2, int y2){
        double distance = Math.sqrt(Math.pow((x1-x2),2) + Math.pow((y1-y2),2));
        return distance;
    }

    public ArrayList<Grid> getSoundArea() {
        return soundArea;
    }

}
