package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Exceptions;

public class CollisionWallException extends Exception {
    public CollisionWallException() {
        super("Next tile has a Wall");
    }
}
