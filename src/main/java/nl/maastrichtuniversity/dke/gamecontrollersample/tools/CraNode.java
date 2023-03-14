package nl.maastrichtuniversity.dke.gamecontrollersample.tools;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.util.ArrayList;
import java.util.HashMap;

public class CraNode {
    //Map
    private HashMap <Integer, HashMap<Integer, Grid>> map;

    //Positions of the agents
    private int[] gPos;
    private int[] iPos;

    //Distance between the agents
    private int distance;

    //Speed of the agents
    private double gS;
    private double iS;

    //Orientation ot the agents
    private double gO;
    private double iO;

    //Cover scores of the agents
    private double gScore;
    private double iScore;

    //Parent of the node
    private CraNode parent = null;

    //Children of the node
    private ArrayList<CraNode> children = new ArrayList<>();

/*
    public CraNode(int[] guard, int[] intruder, double gSpeed, double iSpeed, double gOrient, double iOrient, HashMap <Integer, HashMap<Integer, Grid>> m){ //NOT USED
        this.gPos = guard;
        this.iPos = intruder;
        this.gS = gSpeed;
        this.iS = iSpeed;
        this.gO = gOrient;
        this.iO = iOrient;
        this.map = m;
        calcManhattan();
    }
*/

    public CraNode(int[] guard, int[] intruder, double gSpeed, double iSpeed, HashMap <Integer, HashMap<Integer, Grid>> m){
        this.gPos = guard;
        this.iPos = intruder;
        this.gS = gSpeed;
        this.iS = iSpeed;
        this.map = m;
        calcManhattan();
    }

/*
    public CraNode(int[] guard, int[] intruder, double gSpeed, double iSpeed, double gOrient, double iOrient, HashMap <Integer, HashMap<Integer, Grid>> m, CraNode p){ //NOT USED
        this.gPos = guard;
        this.iPos = intruder;
        this.gS = gSpeed;
        this.iS = iSpeed;
        this.gO = gOrient;
        this.iO = iOrient;
        this.map = m;
        this.parent = p;
        calcManhattan();
    }
*/

    public CraNode(int[] guard, int[] intruder, double gSpeed, double iSpeed, HashMap <Integer, HashMap<Integer, Grid>> m, CraNode p){
        this.gPos = guard;
        this.iPos = intruder;
        this.gS = gSpeed;
        this.iS = iSpeed;
        this.map = m;
        this.parent = p;
        calcManhattan();
    }

    //Getters Start
    public int getDistance() {
        return distance;
    }

    public int[] getGPos() {
        return gPos;
    }

    public int[] getIPos() {
        return iPos;
    }

    public double getGScore() {
        return gScore;
    }

    public double getIScore() {
        return iScore;
    }

    public CraNode getParent() {
        return parent;
    }

    public ArrayList<CraNode> getChildren() {
        return children;
    }
    //Getters End

    //Checks if node is a root
    public boolean isRoot(){
        if (this.getParent() == null){
            return true;
        }
        return false;
    }

    //Checks if node is terminal
    public boolean isTerminal(){
        if (this.getChildren().size() == 0){
            return true;
        }
        return false;
    }

    //Calculates the Manhattan distance
    public void calcManhattan(){
        distance = Math.abs(gPos[0] - iPos[0]) + Math.abs(gPos[1] - iPos[1]);
    }

    public ArrayList<Grid> areaAroundIntruder(Grid intruderPos){
        ArrayList<Grid> area = new ArrayList<>();
        return area;
    }

    //Checks if point exists on the map
    public boolean exists(CraPoint p){
        return map.containsKey(p.getX()) && map.get(p.getX()).containsKey(p.getY()) && !map.get(p.getX()).get(p.getY()).isWall();
    }
    public boolean exists(int[] p){
        return map.containsKey(p[0]) && map.get(p[0]).containsKey(p[1]) && !map.get(p[0]).get(p[1]).isWall();
    }

    //Checks if the point has already been marked
    public boolean marked(CraPoint p, ArrayList<CraPoint> toCheck){
        for (int i = 0; i < toCheck.size(); i++) {
            if (toCheck.get(i).compare(p)){
                return true;
            }
        }
        return false;
    }

/*
    public boolean marked(int[] p, ArrayList<CraPoint> toCheck){
        for (int i = 0; i < toCheck.size(); i++) {
            if (toCheck.get(i).compare(p)){
                return true;
            }
        }
        return false;
    }
*/

    //Generates Children for the point
    public ArrayList<CraPoint> generateChildrenPoint(CraPoint p, double sp){
        ArrayList<CraPoint> children = new ArrayList<>();
        boolean blockN = false;
        boolean blockS = false;
        boolean blockR = false;
        boolean blockL = false;
        int pX = p.getX();
        int pY = p.getY();
        int pType = p.getType();

        for (int i = 1; i <= sp; i++) {
            ArrayList<CraPoint> candidates = new ArrayList<>();
            if (!blockN){
                CraPoint n = new CraPoint(pX, pY + i, pType);
                candidates.add(n);
            }
            if (!blockS){
                CraPoint s = new CraPoint(pX, pY - i, pType);
                candidates.add(s);
            }
            if (!blockR){
                CraPoint r = new CraPoint(pX + i, pY, pType);
                candidates.add(r);
            }
            if (!blockL){
                CraPoint l = new CraPoint(pX - i, pY, pType);
                candidates.add(l);
            }
            for (CraPoint candidate : candidates) {
                if (exists(candidate)) {
                    children.add(candidate);
                } else if (!exists(candidate)) {
                    if (candidate.getX() > pX) {
                        blockR = true;
                    } else if (candidate.getX() < pX) {
                        blockL = true;
                    } else if (candidate.getY() > pY) {
                        blockN = true;
                    } else if (candidate.getY() < pY) {
                        blockS = true;
                    }
                }
            }
        }
        return children;
    }

    //Generates Children for the point
    public ArrayList<int[]> generateChildrenPoint(int[] p, double sp){
        ArrayList<int[]> children = new ArrayList<>();
        boolean blockN = false;
        boolean blockS = false;
        boolean blockR = false;
        boolean blockL = false;
        for (int i = 1; i <= sp; i++) {
            ArrayList<int[]> candidates = new ArrayList<>();
            if (!blockN){
                int[] n = new int[] {p[0], p[1] + i};
                candidates.add(n);
            }
            if (!blockS){
                int[] s = new int[] {p[0], p[1] - i};
                candidates.add(s);
            }
            if (!blockR){
                int[] r = new int[] {p[0] + i, p[1]};
                candidates.add(r);
            }
            if (!blockL){
                int[] l = new int[] {p[0] - i, p[1]};
                candidates.add(l);
            }
            for (int[] candidate : candidates) {
                if (exists(candidate)) {
                    children.add(candidate);
                } else if (!exists(candidate)) {
                    if (candidate[0] > p[0]) {
                        blockR = true;
                    } else if (candidate[0] < p[0]) {
                        blockL = true;
                    } else if (candidate[1] > p[1]) {
                        blockN = true;
                    } else if (candidate[1] < p[1]) {
                        blockS = true;
                    }
                }
            }
        }
        return children;
    }

    public void expand(){
        ArrayList<int[]> guard = generateChildrenPoint(gPos, gS);
        ArrayList<int[]> intruder = generateChildrenPoint(iPos, iS);
        if (intruder.size() == 0){
            for (int i = 0; i < guard.size(); i++) {
                children.add(new CraNode(guard.get(i), iPos, gS, iS, map, this));
            }
        }
        else if (guard.size() == 0){
            for (int i = 0; i < intruder.size(); i++) {
                children.add(new CraNode(gPos, intruder.get(i), gS, iS, map, this));
            }
        }
        else {
            for (int i = 0; i < guard.size(); i++) {
                for (int j = 0; j < intruder.size(); j++) {
                    //System.out.println("G = {X: " + guard.get(i)[0] + ", Y: " + guard.get(i)[1] + ", I = {X: " + intruder.get(j)[0] + ", Y: " + intruder.get(j)[1]);
                    children.add(new CraNode(guard.get(i), intruder.get(j), gS, iS, map, this));
                }
            }
        }
    }

    //Calculates the cover scores for this node
    public void calculateCover(){
        ArrayList<CraPoint> qPriority = new ArrayList<>();
        qPriority.add(new CraPoint(iPos, 1));
        qPriority.add(new CraPoint(gPos, 0));

        ArrayList<CraPoint> gCover = new ArrayList<>();
        ArrayList<CraPoint> iCover = new ArrayList<>();

        while (!qPriority.isEmpty()){
            CraPoint current = qPriority.remove(0);
            if (!marked(current, gCover) && !marked(current, iCover)){
                if (current.getType() == 0){
                    gCover.add(current);
                    //Generating children
                    ArrayList<CraPoint> children = generateChildrenPoint(current, gS);
                    qPriority.addAll(children);
                }
                else if (current.getType() == 1){
                    iCover.add(current);
                    ArrayList<CraPoint> children = generateChildrenPoint(current, iS);
                    qPriority.addAll(children);
                }
            }
        }
        gScore = gCover.size();
        iScore = iCover.size();
    }


//    public void expand(){ NOT FINISHED
//
//        //GOING UP GUARD
//        int gU = 1;
//        boolean gBlockedU = false;
//        while (gBlockedU == false && gU <= gS){
//            int[] gUP = new int[]{this.gPos[0], this.gPos[1] + gU};
//            if (exists(gUP)){
//
//                //GOING UP INTRUDER
//                int iU = 1;
//                boolean iBlockedU = false;
//                while (iBlockedU == false && iU <= iS){
//                    int[] iUP = new int[]{this.iPos[0], this.iPos[1] + iU};
//                    if (exists(iUP)){
//                        this.children.add(new CraNode(gUP, iUP, this.gS, this.iS, this.map, this));
//                        iU = iU + 1;
//                    }
//                    else if (!exists(iUP)){
//                        iBlockedU = true;
//                    }
//                }
//
//                //GOING DOWN INTRUDER
//                int iD = 1;
//                boolean iBlockedD = false;
//                while (iBlockedD == false && iD <= iS){
//                    int[] iDOWN = new int[]{this.iPos[0], this.iPos[1] - iD};
//                    if (exists(iDOWN)){
//                        this.children.add(new CraNode(gUP, iDOWN, this.gS, this.iS, this.map, this));
//                        iD = iD + 1;
//                    }
//                    else if (!exists(iDOWN)){
//                        iBlockedD = true;
//                    }
//                }
//
//                //GOING RIGHT INTRUDER
//                int iR = 1;
//                boolean iBlockedR = false;
//                while (iBlockedR == false && iR <= iS){
//                    int[] iRIGHT = new int[]{this.iPos[0] + iR, this.iPos[1]};
//                    if (exists(iRIGHT)){
//                        this.children.add(new CraNode(gUP, iRIGHT, this.gS, this.iS, this.map, this));
//                        iR = iR + 1;
//                    }
//                    else if (!exists(iRIGHT)){
//                        iBlockedR = true;
//                    }
//                }
//
//                //GOING LEFT INTRUDER
//                int iL = 1;
//                boolean iBlockedL = false;
//                while (iBlockedL == false && iL <= iS){
//                    int[] iLEFT = new int[]{this.iPos[0] - iL, this.iPos[1]};
//                    if (exists(iLEFT)){
//                        this.children.add(new CraNode(gUP, iLEFT, this.gS, this.iS, this.map, this));
//                        iL = iL + 1;
//                    }
//                    else if (!exists(iLEFT)){
//                        iBlockedL = true;
//                    }
//                }
//
//                gU = gU + 1;
//            }
//            else if (!exists(gUP)){
//                gBlockedU = true;
//            }
//        }
//
//        //GOING DOWN GUARD
//        int gD = 1;
//        boolean gBlockedD = false;
//        while (gBlockedD == false && gD <= gS){
//            int iU = 1;
//            int iD = 1;
//            int iR = 1;
//            int iL = 1;
//            int[] gDOWN = new int[]{this.gPos[0], this.gPos[1] - gD};
//            if (exists(gDOWN)){
//
//                //GOING UP INTRUDER
//                boolean iBlockedU = false;
//                while (iBlockedU == false && iU <= iS){
//                    int[] iUP = new int[]{this.iPos[0], this.iPos[1] + iU};
//                    if (exists(iUP)){
//                        this.children.add(new CraNode(gDOWN, iUP, this.gS, this.iS, this.map, this));
//                        iU = iU + 1;
//                    }
//                    else if (!exists(iUP)){
//                        iBlockedU = true;
//                    }
//                }
//
//                //GOING DOWN INTRUDER
//                boolean iBlockedD = false;
//                while (iBlockedD == false && iD <= iS){
//                    int[] iDOWN = new int[]{this.iPos[0], this.iPos[1] - iD};
//                    if (exists(iDOWN)){
//                        this.children.add(new CraNode(gDOWN, iDOWN, this.gS, this.iS, this.map, this));
//                        iD = iD + 1;
//                    }
//                    else if (!exists(iDOWN)){
//                        iBlockedD = true;
//                    }
//                }
//
//                //GOING RIGHT INTRUDER
//                boolean iBlockedR = false;
//                while (iBlockedR == false && iR <= iS){
//                    int[] iRIGHT = new int[]{this.iPos[0] + iR, this.iPos[1]};
//                    if (exists(iRIGHT)){
//                        this.children.add(new CraNode(gDOWN, iRIGHT, this.gS, this.iS, this.map, this));
//                        iR = iR + 1;
//                    }
//                    else if (!exists(iRIGHT)){
//                        iBlockedR = true;
//                    }
//                }
//
//                //GOING LEFT INTRUDER
//                boolean iBlockedL = false;
//                while (iBlockedL == false && iL <= iS){
//                    int[] iLEFT = new int[]{this.iPos[0] - iL, this.iPos[1]};
//                    if (exists(iLEFT)){
//                        this.children.add(new CraNode(gDOWN, iLEFT, this.gS, this.iS, this.map, this));
//                        iL = iL + 1;
//                    }
//                    else if (!exists(iLEFT)){
//                        iBlockedL = true;
//                    }
//                }
//
//                gD = gD + 1;
//            }
//            else if (!exists(gDOWN)){
//                gBlockedD = true;
//            }
//        }
//
//        //GOING RIGHT GUARD
//        int gR = 1;
//        boolean gBlockedR = false;
//        while (gBlockedR == false && gR <= gS){
//            int iU = 1;
//            int iD = 1;
//            int iR = 1;
//            int iL = 1;
//            int[] gRIGHT = new int[]{this.gPos[0] + gR, this.gPos[1]};
//            if (exists(gRIGHT)){
//
//                //GOING UP INTRUDER
//                boolean iBlockedU = false;
//                while (iBlockedU == false && iU <= iS){
//                    int[] iUP = new int[]{this.iPos[0], this.iPos[1] + iU};
//                    if (exists(iUP)){
//                        this.children.add(new CraNode(gRIGHT, iUP, this.gS, this.iS, this.map, this));
//                        iU = iU + 1;
//                    }
//                    else if (!exists(iUP)){
//                        iBlockedU = true;
//                    }
//                }
//
//                //GOING DOWN INTRUDER
//                boolean iBlockedD = false;
//                while (iBlockedD == false && iD <= iS){
//                    int[] iDOWN = new int[]{this.iPos[0], this.iPos[1] - iD};
//                    if (exists(iDOWN)){
//                        this.children.add(new CraNode(gRIGHT, iDOWN, this.gS, this.iS, this.map, this));
//                        iD = iD + 1;
//                    }
//                    else if (!exists(iDOWN)){
//                        iBlockedD = true;
//                    }
//                }
//
//                //GOING RIGHT INTRUDER
//                boolean iBlockedR = false;
//                while (iBlockedR == false && iR <= iS){
//                    int[] iRIGHT = new int[]{this.iPos[0] + iR, this.iPos[1]};
//                    if (exists(iRIGHT)){
//                        this.children.add(new CraNode(gRIGHT, iRIGHT, this.gS, this.iS, this.map, this));
//                        iR = iR + 1;
//                    }
//                    else if (!exists(iRIGHT)){
//                        iBlockedR = true;
//                    }
//                }
//
//                //GOING LEFT INTRUDER
//                boolean iBlockedL = false;
//                while (iBlockedL == false && iL <= iS){
//                    int[] iLEFT = new int[]{this.iPos[0] - iL, this.iPos[1]};
//                    if (exists(iLEFT)){
//                        this.children.add(new CraNode(gRIGHT, iLEFT, this.gS, this.iS, this.map, this));
//                        iL = iL + 1;
//                    }
//                    else if (!exists(iLEFT)){
//                        iBlockedL = true;
//                    }
//                }
//
//                gR = gR + 1;
//            }
//            else if (!exists(gRIGHT)){
//                gBlockedR = true;
//            }
//        }
//
//
//    }


    @Override
    public String toString() {
        return "G = {X: " + this.gPos[0] + ", Y: " +this.gPos[1] + "}, I = {X: " + this.iPos[0] + ", Y: " + this.iPos[1] + "}";
    }
}
