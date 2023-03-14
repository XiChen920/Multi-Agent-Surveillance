package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Exceptions;

public class CollisionAgentException extends Exception {
    public CollisionAgentException() {
        super("Next tile has a Guard/Intruder");
    }
}
