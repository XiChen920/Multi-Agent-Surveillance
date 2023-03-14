package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound;

import nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound.Sound;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

public class Rustle extends Sound {
    protected static int radius = 15;
    protected static double intensity = 3;

    public Rustle(Grid agentPosition, Board gameBoard){
        super(agentPosition, gameBoard, radius, intensity);
    }
    public void addSound(Grid cell, double amplitude, double srcOrientation){
        cell.addRustle(new double[]{amplitude,srcOrientation});
    }

    public static double getIntensity(){
        return intensity;
    }

}
