package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.util.ArrayList;

public class DetectionSense {

    private double radius;
    private Grid[][] board;

    public DetectionSense(double r, Grid[][] b){
        this.radius = r;
        this.board = b;
    }

    public ArrayList<Grid> getCellsInRange(int x, int y, int offsetX, int offsetY){
        ArrayList<Grid> cells = new ArrayList<Grid>();

        for (int i = (int) (x - this.radius); i <= (int) (x + this.radius); i++) {
            if (i >= 0 && i < board.length){
                for (int j = (int) (y - this.radius); j <= (int) (y + this.radius); j++) {
                    if (j >= 0 && j < board[i].length){
                        if (((i - x) * (i - x)) + ((j - y)) * (j - y) <= (radius * radius)){
                            Grid clone = board[i][j].getCopy();
                            clone.setX(clone.getX() - offsetX);
                            clone.setY(clone.getY() - offsetY);
                            cells.add(clone);
                        }
                    }
                }
            }
        }

        return cells;
    }
}
