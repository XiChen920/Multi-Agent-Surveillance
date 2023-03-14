package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay;

import nl.maastrichtuniversity.dke.gamecontrollersample.agents.*;

public class Board {

    private final Grid[][] boardXY;
    /* boundaries
    * 0 = left
    * 1 = right
    * 2 = bottom
    * 3 = top  */
    private final int width;
    private final int height;

    public Board(int h, int w, int gridHeight, int gridWidth){
        this.height = h/gridHeight+1;
        this.width = w/gridWidth+1;
        boardXY = new Grid[this.width][this.height];
        for (int i = 0; i < boardXY.length; i++) {
            for (int j = 0; j < boardXY[i].length; j++) {
                boardXY[i][j] = new Grid(i,j);
            }
        }
    }

    public Grid getGrid(int x, int y){
        return boardXY[x][y];
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public Grid[][] getBoardXY() {
        return boardXY;
    }
}
