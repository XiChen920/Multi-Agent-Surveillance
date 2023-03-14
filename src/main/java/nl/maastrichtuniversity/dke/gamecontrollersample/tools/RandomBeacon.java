package nl.maastrichtuniversity.dke.gamecontrollersample.tools;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;
import nl.maastrichtuniversity.dke.gamecontrollersample.tools.BFS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RandomBeacon {

    private int[] myPosition;
    private int[] enemyPosition;
    private HashMap <Integer, HashMap<Integer, Grid>> referenceBoard;
    private ArrayList<int[]> beacons = new ArrayList<int[]>();
    private double threshold;
    private int beaconAmount;
    int mode; // 1 = guard, 2 = intruder

    // Constructor (Provide Relative Positions) (Mde 1 is guard mode [Chasing], Mode 2 is Intruder mode [Escaping])
    public RandomBeacon(int[] me, int[] enemy, HashMap <Integer, HashMap<Integer, Grid>> board, int b, int m){
        this.myPosition = me;
        this.enemyPosition = enemy;
        this.referenceBoard = board;
        this.beaconAmount = b;
        this.mode = m;
        calculateThreshold();
    }

    public void calculateThreshold(){
        double total = 0.0;
        for (int i : this.referenceBoard.keySet()) {
            for (int j : this.referenceBoard.get(i).keySet()) {
                if (this.referenceBoard.get(i).get(j).isWall() == false){
                    total = total + 1;
                }
            }
        }
        this.threshold = 1.0/(total - 1.0);
    }

    public void GenerateBeacons() {
        this.beacons.clear();
        Random r = new Random();
        int total = 0;
        while (total != this.beaconAmount) {
            for (int i : this.referenceBoard.keySet()) {
                for (int j : referenceBoard.get(i).keySet()) {
                    if (this.referenceBoard.get(i).get(j).isWall() == false){
                        double number = r.nextDouble();
                        if (number < this.threshold && total < this.beaconAmount) {
                            beacons.add(new int[]{referenceBoard.get(i).get(j).getX(), referenceBoard.get(i).get(j).getY()});
                            total = total + 1;
                        }
                    }
                }
            }
        }
    }
    public int[] getBestBeacon(){
        int[] currentBest = {-99999,-99999};
        if (this.mode == 1){
            int minValue = Integer.MAX_VALUE;
            for (int i = 0; i < beacons.size(); i++) {
                int distance = Math.abs(enemyPosition[0] - beacons.get(i)[0]) + Math.abs(enemyPosition[1] - beacons.get(i)[1]);
                if (distance < minValue){
                    minValue = distance;
                    currentBest[0] = beacons.get(i)[0];
                    currentBest[1] = beacons.get(i)[1];
                }
            }
            return currentBest;
        }
        if (this.mode == 2){
            int maxValue = Integer.MIN_VALUE;
            for (int i = 0; i < beacons.size(); i++) {
                int distance = Math.abs(enemyPosition[0] - beacons.get(i)[0]) + Math.abs(enemyPosition[1] - beacons.get(i)[1]);
                if (distance > maxValue){
                    maxValue = distance;
                    currentBest[0] = beacons.get(i)[0];
                    currentBest[1] = beacons.get(i)[1];
                }
            }
            return currentBest;
        }
        return new int[]{-99999,-99999};
    }

    public int[] getTarget(){
        GenerateBeacons();
        int[] target = getBestBeacon();
        return target;
    }

    public ArrayList<int[]> getPath(int[] target){
        BFS search = new BFS(referenceBoard, this.myPosition, target);
        return search.getPath();
    }

    public ArrayList<int[]> getBeacons() {
        return beacons;
    }
}
