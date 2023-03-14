package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Exceptions;

import nl.maastrichtuniversity.dke.gamecontrollersample.agents.Agent;

public class PathIsEmptyException extends Exception{
    public PathIsEmptyException(Agent agent) {
        super(agent + " calculates the empty path" +
                "\nYour algorithm should find a path to the target or at least calculate some path, it calculates an empty pathList");
    }
}
