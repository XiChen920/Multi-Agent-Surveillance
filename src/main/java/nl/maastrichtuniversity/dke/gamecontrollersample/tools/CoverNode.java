package nl.maastrichtuniversity.dke.gamecontrollersample.tools;

import nl.maastrichtuniversity.dke.gamecontrollersample.agents.*;
import nl.maastrichtuniversity.dke.gamecontrollersample.agents.*;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.util.ArrayList;
import java.util.HashMap;

public class CoverNode {
    private CoverNode parent = null;
    private ArrayList<CoverNode> children = new ArrayList<CoverNode>();
    private int[] guardPos;
    private int[] intruderPos;
    private double gAngle;
    private double iAngle;
    private double gSpeed;
    private double iSpeed;
    private HashMap <Integer, HashMap<Integer, Grid>> map;
    private ArrayList<CoverPoint> gCover = new ArrayList<CoverPoint>();
    private ArrayList<CoverPoint> iCover = new ArrayList<CoverPoint>();
    private ArrayList<CoverPoint> gQueue = new ArrayList<CoverPoint>();
    private ArrayList<CoverPoint> iQueue = new ArrayList<CoverPoint>();

    public CoverNode(Guard g, Intruder i, HashMap <Integer, HashMap<Integer, Grid>> referenceBoard){
        this.guardPos = new int[]{g.getxRelative(), g.getyRelative()};
        this.intruderPos = new int[]{i.getX() - g.getxOffset(), i.getY() - g.getyOffset()};
        this.map = referenceBoard;
        this.gSpeed = g.getBaseSpeed();
        this.iSpeed = i.getBaseSpeed();
        this.gAngle = Math.floor(g.getOrientation());
        this.iAngle = Math.floor(i.getOrientation());

        this.gQueue.add(new CoverPoint(guardPos));
        this.iQueue.add(new CoverPoint(intruderPos));
    }

    public CoverNode(int[] g, int[] i, double gS, double iS, double gA, double iA, HashMap <Integer, HashMap<Integer, Grid>> referenceBoard, CoverNode p){
        this.guardPos = g;
        this.intruderPos = i;
        this.map = referenceBoard;
        this.gSpeed = gS;
        this.iSpeed = iS;
        this.parent = p;
        this.gAngle = gA;
        this.iAngle = iA;

        this.gQueue.add(new CoverPoint(guardPos));
        this.iQueue.add(new CoverPoint(intruderPos));
    }

    public void expand(){
        //System.out.println("Intruder pos " +this.intruderPos[0] + " " + this.intruderPos[1]);
        for (int i = 1; i <= gSpeed; i++) {
            for (int j = 1; j < iSpeed; j++) {
                // if guard is 270
                if (gAngle == 270 && iAngle == 270){
                    int[] g = new int[] {guardPos[0] - i, guardPos[1]};
                    int[] in = new int[] {intruderPos[0] - j, intruderPos[1]};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 270 && iAngle == 0){
                    int[] g = new int[] {guardPos[0] - i, guardPos[1]};
                    int[] in = new int[] {intruderPos[0], intruderPos[1] + j};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 270 && iAngle == 90){
                    int[] g = new int[] {guardPos[0] - i, guardPos[1]};
                    int[] in = new int[] {intruderPos[0] + j, intruderPos[1]};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 270 && iAngle == 180){
                    int[] g = new int[] {guardPos[0] - i, guardPos[1]};
                    int[] in = new int[] {intruderPos[0], intruderPos[1] - j};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                // if guard is 0
                if (gAngle == 0 && iAngle == 0){
                    int[] g = new int[] {guardPos[0], guardPos[1] + i};
                    int[] in = new int[] {intruderPos[0], intruderPos[1] + j};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 0 && iAngle == 90){
                    int[] g = new int[] {guardPos[0], guardPos[1]+ i};
                    int[] in = new int[] {intruderPos[0] + j, intruderPos[1]};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 0 && iAngle == 180){
                    int[] g = new int[] {guardPos[0], guardPos[1] + i};
                    int[] in = new int[] {intruderPos[0], intruderPos[1] - j};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 0 && iAngle == 270){
                    int[] g = new int[] {guardPos[0], guardPos[1] + i};
                    int[] in = new int[] {intruderPos[0] - j, intruderPos[1]};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                // if guard is 90
                if (gAngle == 90 && iAngle == 0){
                    int[] g = new int[] {guardPos[0] + i, guardPos[1]};
                    int[] in = new int[] {intruderPos[0], intruderPos[1] + j};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 90 && iAngle == 90){
                    int[] g = new int[] {guardPos[0] + i, guardPos[1]};
                    int[] in = new int[] {intruderPos[0] + j, intruderPos[1]};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 90 && iAngle == 180){
                    int[] g = new int[] {guardPos[0] + i, guardPos[1] };
                    int[] in = new int[] {intruderPos[0], intruderPos[1] - j};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 90 && iAngle == 270){
                    int[] g = new int[] {guardPos[0] + i, guardPos[1] };
                    int[] in = new int[] {intruderPos[0] - j, intruderPos[1]};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                // if guard is 180
                if (gAngle == 180 && iAngle == 0){
                    int[] g = new int[] {guardPos[0], guardPos[1] - 1};
                    int[] in = new int[] {intruderPos[0], intruderPos[1] + j};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 180 && iAngle == 90){
                    int[] g = new int[] {guardPos[0], guardPos[1] - 1};
                    int[] in = new int[] {intruderPos[0] + j, intruderPos[1]};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 180 && iAngle == 180){
                    int[] g = new int[] {guardPos[0], guardPos[1] - 1};
                    int[] in = new int[] {intruderPos[0], intruderPos[1] - j};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
                if (gAngle == 180 && iAngle == 270){
                    int[] g = new int[] {guardPos[0] , guardPos[1] - 1};
                    int[] in = new int[] {intruderPos[0] - j, intruderPos[1]};
                    this.children.add(new CoverNode(g, in, this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
                }
            }
        }

    }

//    public void expand(){
//        for (int i = 1; i <= this.gSpeed; i++) {
//
//            int[] nG = {guardPos[0], guardPos[1] + i};
//            for (int j = 1; j <= this.iSpeed ; j++) {
//                int[] nI = {intruderPos[0], intruderPos[1] + j};
//                this.children.add(new CoverNode(nG, nI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] sI = {intruderPos[0], intruderPos[1] - j};
//                this.children.add(new CoverNode(nG, sI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] rI = {intruderPos[0] + j, intruderPos[1]};
//                this.children.add(new CoverNode(nG, rI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] lI = {intruderPos[0] - j, intruderPos[1]};
//                this.children.add(new CoverNode(nG, lI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//            }
//            int[] sG = {guardPos[0], guardPos[1] - i};
//            for (int j = 1; j <= this.iSpeed ; j++) {
//                int[] nI = {intruderPos[0], intruderPos[1] + j};
//                this.children.add(new CoverNode(sG, nI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] sI = {intruderPos[0], intruderPos[1] - j};
//                this.children.add(new CoverNode(sG, sI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] rI = {intruderPos[0] + j, intruderPos[1]};
//                this.children.add(new CoverNode(sG, rI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] lI = {intruderPos[0] - j, intruderPos[1]};
//                this.children.add(new CoverNode(sG, lI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//            }
//            int[] rG = {guardPos[0] + i, guardPos[1]};
//            for (int j = 1; j <= this.iSpeed ; j++) {
//                int[] nI = {intruderPos[0], intruderPos[1] + j};
//                this.children.add(new CoverNode(rG, nI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] sI = {intruderPos[0], intruderPos[1] - j};
//                this.children.add(new CoverNode(rG, sI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] rI = {intruderPos[0] + j, intruderPos[1]};
//                this.children.add(new CoverNode(rG, rI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] lI = {intruderPos[0] - j, intruderPos[1]};
//                this.children.add(new CoverNode(rG, lI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//            }
//            int[] lG = {guardPos[0] - i, guardPos[1]};
//            for (int j = 1; j <= this.iSpeed ; j++) {
//                int[] nI = {intruderPos[0], intruderPos[1] + j};
//                this.children.add(new CoverNode(lG, nI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] sI = {intruderPos[0], intruderPos[1] - j};
//                this.children.add(new CoverNode(lG, sI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] rI = {intruderPos[0] + j, intruderPos[1]};
//                this.children.add(new CoverNode(lG, rI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//                int[] lI = {intruderPos[0] - j, intruderPos[1]};
//                this.children.add(new CoverNode(lG, lI,this.gSpeed, this.iSpeed, this.gAngle, this.iAngle, this.map, this));
//            }
//
//        }
//    }
//    public void calculateCover(){
//        while (!gQueue.isEmpty() && !iQueue.isEmpty()){
//            //Guard Turn
//            if (!gQueue.isEmpty()){
//                CoverPoint current = gQueue.remove(0);
//                if (current.getTurn() == 1){
//                    current.setTurn(2);
//                    if (!contains(gCover, current)){
//                        gCover.add(current);
//                    }
//                    gQueue.add(current);
//                    //generate children
//                    for (int i = 1; i <= this.gSpeed; i++) {
//                        CoverPoint child = new CoverPoint(current.getX() + i, current.getY());
//                        if (checkIfExists(map, child) && !contains(iCover, child) && !contains(gCover, child)){
//                            gQueue.add(child);
//                            gCover.add(child);
//                        }
//                    }
//                }
//                //Agent has turned
//                if (current.getTurn() == 2){
//                    for (int i = 1; i <= this.gSpeed; i++) {
//                        CoverPoint[] children = new CoverPoint[]{new CoverPoint(current.getX() + i, current.getY()),
//                                new CoverPoint(current.getX() - i, current.getY()), new CoverPoint(current.getX() , current.getY() + i),
//                                new CoverPoint(current.getX(), current.getY() - i)};
//                        for (int j = 0; j < children.length; j++) {
//                            if (checkIfExists(map, children[j]) && !contains(iCover, children[j]) && !contains(gCover, children[j])){
//                                gQueue.add(children[j]);
//                                gCover.add(children[j]);
//                            }
//                        }
//                    }
//                }
//            }
//            //Intruder Turn
//            if (!iQueue.isEmpty()){
//                CoverPoint current = iQueue.remove(0);
//                //Agent has not turned
//                if (current.getTurn() == 1){
//                    current.setTurn(2);
//                    if (!contains(iCover, current)){
//                        iCover.add(current);
//                    }
//                    iQueue.add(current);
//                    //generate children
//                    for (int i = 1; i <= this.iSpeed; i++) {
//                        CoverPoint child = new CoverPoint(current.getX() + i, current.getY());
//                        if (checkIfExists(map, child) && !contains(iCover, child) && !contains(gCover, child)){
//                            iQueue.add(child);
//                            iCover.add(child);
//                        }
//                    }
//                }
//                //Agent has turned
//                if (current.getTurn() == 2){
//                    for (int i = 1; i <= this.iSpeed; i++) {
//                        CoverPoint[] children = new CoverPoint[]{new CoverPoint(current.getX() + i, current.getY()),
//                                new CoverPoint(current.getX() - i, current.getY()), new CoverPoint(current.getX() , current.getY() + i),
//                                new CoverPoint(current.getX(), current.getY() - i)};
//                        for (int j = 0; j < children.length; j++) {
//                            if (checkIfExists(map, children[j]) && !contains(iCover, children[j]) && !contains(gCover, children[j])){
//                                iQueue.add(children[j]);
//                                iCover.add(children[j]);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    public void calculateCover(){
        System.out.println("The cover is switched");
//        System.out.println("----------------------------------NEW----------------------------------");
        //System.out.println("Initial: G Size: " + this.gCover.size() + ", I Size " + this.iCover.size() + ", Cell Ammount " + getCellAnount() );
        while (!gQueue.isEmpty() && !iQueue.isEmpty()) {
            //Guard Turn
            if (!gQueue.isEmpty()){
                CoverPoint current = gQueue.remove(0);
                if (!contains(gCover,current) && !contains(iCover, current)){
                    gCover.add(current);
                }
//                System.out.println("G Current: " + current.getX() + ", " + current.getY());
                for (int i = 1; i <= this.gSpeed; i++) {
                    CoverPoint n = new CoverPoint(current.getX(), current.getY() + i);
                    CoverPoint s = new CoverPoint(current.getX(), current.getY() - i);
                    CoverPoint r = new CoverPoint(current.getX() + i, current.getY());
                    CoverPoint l = new CoverPoint(current.getX() - i, current.getY());
                    CoverPoint[] children = new CoverPoint[]{n,s,r,l};
                    for (int j = 0; j < children.length; j++) {
                        if (this.map.containsKey(children[j].getX()) && this.map.get(children[j].getX()).containsKey(children[j].getY())){
                            if (!contains(gCover,children[j]) && !contains(iCover, children[j])){
                                gCover.add(children[j]);
                                gQueue.add(children[j]);
                            }
                        }
                    }
                }
            }
            //Intruder Turn
            if (!iQueue.isEmpty()){
                CoverPoint current = iQueue.remove(0);
                //System.out.println("I Current: " + current.getX() + ", " + current.getY());
                if (!contains(gCover,current) && !contains(iCover, current)){
                    iCover.add(current);
                }
                for (int i = 1; i <= this.iSpeed; i++) {
                    CoverPoint n = new CoverPoint(current.getX(), current.getY() + i);
                    CoverPoint s = new CoverPoint(current.getX(), current.getY() - i);
                    CoverPoint r = new CoverPoint(current.getX() + i, current.getY());
                    CoverPoint l = new CoverPoint(current.getX() - i, current.getY());
                    CoverPoint[] children = new CoverPoint[]{n,s,r,l};
                    for (int j = 0; j < children.length; j++) {
                        if (this.map.containsKey(children[j].getX()) && this.map.get(children[j].getX()).containsKey(children[j].getY())){
                            if (!contains(gCover,children[j]) && !contains(iCover, children[j])){
                                iCover.add(children[j]);
                                iQueue.add(children[j]);
                            }
                        }
                    }
                }
            }
        }
/*
        System.out.println("Last: G Size: " + this.gCover.size() + ", I Size " + this.iCover.size() + ", Cell Ammount " + getCellAnount() );
        System.out.println("gCover " + this.gCover );
        System.out.println("iCover " + this.iCover );
*/
    }

    public boolean contains(ArrayList<CoverPoint> toCheck, CoverPoint point ){
        for (int i = 0; i < toCheck.size(); i++) {
            if (toCheck.get(i).getX() == point.getX() && toCheck.get(i).getY() == point.getY()){
                return true;
            }
        }
        return false;
    }
    public boolean checkIfExists(HashMap <Integer, HashMap<Integer, Grid>> referenceBoard, CoverPoint cP){
        if (referenceBoard.containsKey(cP.getX()) && referenceBoard.get(cP.getX()).containsKey(cP.getY())){
            return true;
        }
        return false;
    }
    public int getCoverScoreG(){
        return this.gCover.size();
    }
    public int getCoverScoreI(){
        return this.iCover.size();
    }

    public ArrayList<CoverNode> getChildren() {return children;}

    public CoverNode getParent() {return parent;}

    public int[] getGuardPos() {return guardPos;}

    public int[] getIntruderPos() {return intruderPos;}
    public int getCellAnount(){
        int total = 0;
        for ( int i : this.map.keySet()) {
            total = total + this.map.get(i).size();
        }
        return total;
    }
}