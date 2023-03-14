
package nl.maastrichtuniversity.dke.gamecontrollersample.tools;

public class Node {
    private Node parent;
    private int[] point;

    public Node(int x, int y){
        point = new int[]{x,y};
    }

    public int getX(){return point[0];}
    public int getY(){return point[1];}
    public int[] getPoint() {return point;}

    public Node getParent() {return parent;}

    public void setParent(Node parent) {this.parent = parent;}

    public String toString() {
        return "X: " + this.getX() + "; Y: " + this.getY();
    }

}