package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

public class TargetSound{

    private final int x_src;
    private final int y_src;
    private final int INTENSITY = 1;

    public TargetSound(int x, int y) {
        this.x_src = x;
        this.y_src = y;
    }

    public void markSound(Grid cell){
        int x = cell.getX();
        int y = cell.getY();
        double amplitude = calculateAmplitude(x,y);
        cell.addTargetSound(amplitude);
    }

    // calculate the amplitude of the sound in the current tile
    private double calculateAmplitude(int x, int y){
        double distance = calculateDistance(x_src,y_src, x,y);
        return INTENSITY/Math.pow(distance,2);
    }

    private double calculateDistance(int x1, int y1, int x2, int y2){
        return Math.sqrt(Math.pow((x1-x2),2) + Math.pow((y1-y2),2));
    }
}
