package nl.maastrichtuniversity.dke.gamecontrollersample.tools.AStar;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Direction;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

public class Node {

    Grid position;
    Direction direction;
    Node parent;
    double distance;

    public Node(Grid position, Direction direction, Node parent, double distance){
        this.parent = parent;
        this.direction = direction;
        this.position = position;
        this.distance = distance;
    }
}
