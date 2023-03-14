package nl.maastrichtuniversity.dke.gamecontrollersample.tools;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.util.ArrayList;
import java.util.HashMap;

public class MTS {
    private double gS;
    private double iS;
    private int[] gPos;
    private int[] iPos;
    private HashMap <Integer, HashMap<Integer, Grid>> map;

    public MTS(int[] guard, int[] intruder, double gSpeed, double iSpeed, HashMap <Integer, HashMap<Integer, Grid>> m){
        this.gS = gSpeed;
        this.iS = iSpeed;
        this.gPos = guard;
        this.iPos = intruder;
        this.map = m;
    }

    public int[] getMove(){
        int[] currentG = this.gPos;
        int[] currentI = this.iPos;

        ArrayList<int[]> childrenG = generateChildrenPoint(currentG, this.gS);
        ArrayList<int[]> childrenI = generateChildrenPoint(currentI, this.iS);

        int[] bestNeighbourG = getMinDist(childrenG, currentI);
        int[] bestNeighbourI = getMaxDist(childrenI, currentG);

        if ((calcManhattan(bestNeighbourG, currentI) + 1) > calcManhattan(currentG, currentI)){
            this.gPos = bestNeighbourG;
        }

        if ((calcManhattan(bestNeighbourI, currentG) - 1) > calcManhattan(currentI, currentG)){
            this.iPos = bestNeighbourI;
        }

        return this.gPos;
    }


    public boolean exists(int[] p){
        if (map.containsKey(p[0]) && this.map.get(p[0]).containsKey(p[1]) && !this.map.get(p[0]).get(p[1]).isWall()){
            return true;
        }
        return false;
    }

    //Generates Children for the point
    public ArrayList<int[]> generateChildrenPoint(int[] p, double sp){
        ArrayList<int[]> children = new ArrayList<int[]>();
        boolean blockN = false;
        boolean blockS = false;
        boolean blockR = false;
        boolean blockL = false;
        for (int i = 1; i <= sp; i++) {
            ArrayList<int[]> candidates = new ArrayList<int[]>();
            if (blockN == false){
                int[] n = new int[] {p[0], p[1] + i};
                candidates.add(n);
            }
            if (blockS == false){
                int[] s = new int[] {p[0], p[1] - i};
                candidates.add(s);
            }
            if (blockR == false){
                int[] r = new int[] {p[0] + i, p[1]};
                candidates.add(r);
            }
            if (blockL == false){
                int[] l = new int[] {p[0] - i, p[1]};
                candidates.add(l);
            }
            for (int j = 0; j < candidates.size(); j++) {
                if (exists(candidates.get(j))){
                    children.add(candidates.get(j));
                }
                else if (!exists(candidates.get(j))){
                    if (candidates.get(j)[0] > p[0]){
                        blockR = true;
                    }
                    else if (candidates.get(j)[0] < p[0]){
                        blockL = true;
                    }
                    else if (candidates.get(j)[1] > p[1]){
                        blockN = true;
                    }
                    else if (candidates.get(j)[1] < p[1]){
                        blockS = true;
                    }
                }
            }
        }
        return children;
    }

    public int calcManhattan(int[] point, int[] target){
        return Math.abs(point[0] - target[0]) + Math.abs(point[1] - target[1]);
    }

    public int[] getMinDist(ArrayList<int[]> toCheck, int[] target){
        int min = Integer.MAX_VALUE;
        int[] best = null;
        for (int i = 0; i < toCheck.size(); i++) {
            int currentDist = calcManhattan(toCheck.get(i), target);
            if (currentDist < min){
                min = currentDist;
                best = toCheck.get(i);
            }
        }
        return best;
    }

    public int[] getMaxDist(ArrayList<int[]> toCheck, int[] target){
        int max = Integer.MIN_VALUE;
        int[] best = null;
        for (int i = 0; i < toCheck.size(); i++) {
            int currentDist = calcManhattan(toCheck.get(i), target);
            if (currentDist > max){
                max = currentDist;
                best = toCheck.get(i);
            }
        }
        return best;
    }
}
