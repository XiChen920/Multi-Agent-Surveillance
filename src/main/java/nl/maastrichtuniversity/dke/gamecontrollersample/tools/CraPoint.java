package nl.maastrichtuniversity.dke.gamecontrollersample.tools;

public class CraPoint {

    private int[] pos;
    private int type; // 0 = guard, 1 = intruder, -1 = not assigned

    public CraPoint(int [] p, int t){
        this.pos = p;
        this.type = t;
    }
    public CraPoint(int x, int y, int t){
        this.pos = new int[] {x, y};
        this.type = t;
    }
    public CraPoint(int [] p){
        this.pos = p;
        this.type = -1;
    }
    public CraPoint(int x, int y){
        this.pos = new int[] {x, y};
        this.type = -1;
    }

    public int[] getPos() {
        return pos;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getX(){
        return this.pos[0];
    }

    public int getY(){
        return this.pos[1];
    }

    public boolean compare(CraPoint p){
        if (this.pos[0] == p.getX() && this.pos[1] == p.getY()){
            return true;
        }
        return false;
    }

    public boolean compare(int[] p){
        if (this.pos[0] == p[0] && this.pos[1] == p[1]){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "{X: " + this.pos[0] + ", Y: " + this.pos[1] + "}, ";
    }
}
