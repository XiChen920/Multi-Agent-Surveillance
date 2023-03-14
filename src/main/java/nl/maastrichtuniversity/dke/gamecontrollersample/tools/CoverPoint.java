package nl.maastrichtuniversity.dke.gamecontrollersample.tools;

public class CoverPoint {

    private  int[] coordinates;
    private int turn;

    public CoverPoint(int[] point){
        this.coordinates = point;
        this.turn = 1;
    }
    public CoverPoint(int x, int y){
        this.coordinates = new int[]{x,y};
        this.turn = 1;
    }

    public int getX(){return this.coordinates[0];}
    public int getY(){return this.coordinates[1];}

    public int getTurn(){return this.turn;}

    public void setTurn(int turn) {this.turn = turn;}

    @Override
    public String toString() {
        return "(X: " + this.getX() + ", Y: " + this.getY() + ")";
    }

}