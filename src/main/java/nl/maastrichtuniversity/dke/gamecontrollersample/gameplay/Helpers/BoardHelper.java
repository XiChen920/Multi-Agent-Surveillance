package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.util.HashMap;

public class BoardHelper {

    public static Grid getByCoordinates(HashMap <Integer, HashMap<Integer, Grid>> Map, int x, int y){
        if( Map.containsKey(x)){
            if( Map.get(x).containsKey(y)){
                return Map.get(x).get(y);
            }
        }
        return null;
    }
}
