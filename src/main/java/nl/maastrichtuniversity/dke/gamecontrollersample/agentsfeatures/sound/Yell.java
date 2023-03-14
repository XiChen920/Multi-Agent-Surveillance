package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;


public class Yell extends Sound {

    protected static int radius = 50;
    protected static double intensity = 10;

    public Yell(Grid agentPosition, Board gameBoard){
        super(agentPosition, gameBoard, radius, intensity);
    }

    public void addSound(Grid cell, double amplitude, double srcOrientation){
        cell.addYell(new double[]{amplitude,srcOrientation});
    }

    public static double getIntensity(){
        return intensity;
    }

}
