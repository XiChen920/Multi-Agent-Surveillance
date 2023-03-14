package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

public class ManhDistance {
    public static int compute(Grid position, Grid target) {
        return Math.abs(position.getX() - target.getX()) + Math.abs(position.getY() - target.getY());
    }
}
