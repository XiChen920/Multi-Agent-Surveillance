package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

public enum Direction {
    UP(-1, 0, 0),
    DOWN(1, 0, 180),
    LEFT(0, -1, 270),
    RIGHT(0, 1, 90);


    private final int xRelative;
    private final int yRelative;
    private final double angle;

    Direction(int xRelative, int yRelative, double angle) {
        this.xRelative = xRelative;
        this.yRelative = yRelative;
        this.angle = angle;
    }

    public int getxRelative() {
        return xRelative;
    }

    public int getyRelative() {
        return yRelative;
    }

    public double getAngle() {
        return angle;
    }

    public static Direction getDirectionByAngle(double angle){
        if(angle != 0.0 && angle != 90.0 && angle != 180.0 && angle != 270.0)
            throw new RuntimeException("Wrong Orientation");

        if (angle == 0.0) {
            return Direction.UP;
        } else if (angle == 90.0) {
            return Direction.RIGHT;
        } else if (angle == 180.0){
            return Direction.DOWN;
        } else {
            return Direction.LEFT;
        }
    }

    public static Direction toTheRight(Direction direction){
        if(direction.equals(Direction.DOWN)){
            return Direction.LEFT;
        } else if(direction.equals(Direction.LEFT)){
            return Direction.UP;
        } else if(direction.equals(Direction.UP)){
            return Direction.RIGHT;
        } else {
            return Direction.DOWN;
        }
    }

    public static Direction getOpposite(Direction direction){
        if(direction.equals(Direction.DOWN)){
            return Direction.UP;
        } else if(direction.equals(Direction.LEFT)){
            return Direction.RIGHT;
        } else if(direction.equals(Direction.UP)){
            return Direction.DOWN;
        } else {
            return Direction.LEFT;
        }
    }
}
