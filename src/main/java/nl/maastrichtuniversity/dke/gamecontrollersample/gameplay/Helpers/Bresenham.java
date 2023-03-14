package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.util.HashMap;

public class Bresenham {

    public static boolean isWallBetweenTiles(Grid[][] map , Grid tile1, Grid tile2){
        int dx = Math.abs(tile1.getX() - tile2.getX());
        int dy = Math.abs(tile1.getY() - tile2.getY());

        if (dx > dy) {
            return calculateLine(map, tile1, tile2, dx, dy, 0);
        } else {
            return calculateLine(map, tile1, tile2, dx, dy, 1);
        }
    }

    private static boolean calculateLine(Grid[][] map, Grid tile1, Grid tile2, int dx, int dy, int slope){
        int pk = 2 * dy - dx;
        int x1 = tile1.getX();
        int x2 = tile2.getX();
        int y1 = tile1.getY();
        int y2 = tile2.getY();
        for (int i = 0; i <= dx; i++) {

            if(map[x1][y1].isWall())
                return true;

            if(x1 < x2) {
                x1++;
            } else {
                x1--;
            }

            if (pk < 0) {
                if (slope == 0) {
                    pk = pk + 2 * dy;
                } else {
                    pk = pk + 2 * dy;
                }
            } else {
                if(y1 < y2)
                    y1++;
                else
                    y1--;
                pk = pk + 2 * dy - 2 * dx;
            }
        }
        return false;
    }

}
