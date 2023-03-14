package nl.maastrichtuniversity.dke.gamecontrollersample.tools;

import nl.maastrichtuniversity.dke.gamecontrollersample.agents.Guard;
import nl.maastrichtuniversity.dke.gamecontrollersample.agents.Intruder;

import java.util.ArrayList;

public class CRA {
    private CraNode root;
    private double w;
    private int depth;
    private ArrayList<CraNode> children;

    public CRA (Guard g, Intruder i, double aW){ //USE THIS
        int [] gPos = new int[] {g.getxRelative(), g.getyRelative()};
        int [] iPos = new int[] {i.getX() - g.getxOffset(), i.getY() - g.getyOffset()};

        this.depth = 1;
        this.w = aW;
        this.root = new CraNode(gPos, iPos, g.getBaseSpeed(), i.getBaseSpeed(), g.getLocalMap());
        children = this.root.getChildren();
        calculate();
    }

/*
    public CRA (Guard g, int[] i, double intruderSpeed, double aW){ //USE THIS! (IF USING THIS CONSTRUCTOR, PASS THE REAL COORDINATES OF THE INTRUDER NOT THE RELATIVE!)
        int [] gPos = new int[] {g.getxRelative(), g.getyRelative()};
        int [] iPos = new int[] {i[0] - g.getxOffset(), i[1] - g.getyOffset()};

        this.depth = 1;
        this.w = aW;
        this.root = new CraNode(gPos, iPos, g.getBaseSpeed(), intruderSpeed, g.getLocalMap());
        children = this.root.getChildren();
        calculate();
    }

    public CRA (Guard g, Intruder i, double aW, int d){ // NOT USED
        int [] gPos = new int[] {g.getxRelative(), g.getyRelative()};
        int [] iPos = new int[] {i.getX() - g.getxOffset(), i.getY() - g.getyOffset()};

        this.depth = d;
        this.w = aW;
        this.root = new CraNode(gPos, iPos, g.getBaseSpeed(), i.getBaseSpeed(), g.getLocalMap());
        children = this.root.getChildren();
        calculate();
    }
*/
    public CraNode getMaxCoverPosG(){
        CraNode best = null;
        double maxC = -1;
        int distanceMin = Integer.MAX_VALUE;
        CraNode currentChild;
        double currentScore;
        int currentDistance;
        for (int i = 0; i < children.size(); i++) {
            currentChild = children.get(i);
            currentScore = currentChild.getGScore();
            currentDistance = currentChild.getDistance();
            if ( (currentScore > maxC) || ((currentScore == maxC) && (currentDistance < distanceMin)) ){
                best = currentChild;
                maxC = currentScore;
                distanceMin = currentDistance;
            }
        }
        return best;
    }

    public CraNode getMaxCoverPosI(){
        CraNode best = null;
        double maxC = -1;
        int distanceMin = Integer.MAX_VALUE;
        CraNode currentChild;
        for (int i = 0; i < children.size(); i++) {
            currentChild = children.get(i);
            if (currentChild.getIScore() > maxC){
                best = currentChild;
                maxC = currentChild.getIScore();
                distanceMin = currentChild.getDistance();
            }
            else if (currentChild.getIScore() == maxC){
                if (currentChild.getDistance() < distanceMin){
                    best = currentChild;
                    maxC = currentChild.getIScore();
                    distanceMin = currentChild.getDistance();
                }
            }
        }
        return best;
    }
    public void calculate(){
        root.calculateCover();
        root.expand();
        for (CraNode child : children) {
            child.calculateCover();
        }
    }

    public CraNode getShortestDistanceMove(){
        CraNode best = null;
        double maxC = -1;
        int distanceMin = Integer.MAX_VALUE;
        CraNode currentChild;
        int currentDistance;
        double currentScore;
        for (CraNode child : children) {
            currentChild = child;
            currentDistance = currentChild.getDistance();
            currentScore = currentChild.getGScore();
            if ( (currentDistance < distanceMin) || ( (currentDistance == distanceMin) && (currentScore > maxC)) ) {
                best = currentChild;
                distanceMin = currentDistance;
                maxC = currentScore;
            }
        }
        return best;
    }

    public int[] getBestMoveG(){
        CraNode distance = getShortestDistanceMove();
        CraNode cover = getMaxCoverPosG();
        if ( (distance.getGScore() / cover.getGScore()) > (1.0 - this.w)  ){
            System.out.println("CRA");
            return cover.getGPos();
        }
        else {
            System.out.println("CRA");
            return distance.getGPos();
        }
    }

    public int[] getBestMoveI(){
        CraNode i = getMaxCoverPosI();
        return i.getIPos();
    }
}
