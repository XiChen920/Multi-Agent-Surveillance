package nl.maastrichtuniversity.dke.gamecontrollersample.tools;

import nl.maastrichtuniversity.dke.gamecontrollersample.agents.*;
import nl.maastrichtuniversity.dke.gamecontrollersample.agents.*;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.util.ArrayList;
import java.util.HashMap;

public class Cover {

    private int mode; // guard = 1, intruder = 2
    private Guard guard;
    private Intruder intruder;
    private HashMap <Integer, HashMap<Integer, Grid>> referenceBoard;
    private CoverNode root;

    public Cover(Guard me, Intruder enemy, HashMap <Integer, HashMap<Integer, Grid>> board){
        this.guard = me;
        this.intruder = enemy;
        this.referenceBoard = board;
        this.mode = 1;
        this.root = new CoverNode(me,enemy, referenceBoard);
    }

    public Cover(Intruder me, Guard enemy, HashMap <Integer, HashMap<Integer, Grid>> board){
        this.guard = enemy;
        this.intruder = me;
        this.referenceBoard = board;
        this.mode = 2;
        this.root = new CoverNode(enemy,me, referenceBoard);
    }

    public void calculate(){
        this.root.calculateCover();
        this.root.expand();
        for (int i = 0; i < this.root.getChildren().size(); i++) {
            this.root.getChildren().get(i).calculateCover();
        }
    }

    public int[] getBestMoveG(){
        calculate();
        ArrayList<CoverNode> children = this.root.getChildren();
/*
        System.out.println("Children amount " + children.size());
        System.out.println("Root: " + "Gs = " + this.root.getCoverScoreG() + ", Is = " + this.root.getCoverScoreI() + ", Cell amount = " + this.root.getCellAnount());
*/
        int max = -1;
        int[] best = this.root.getGuardPos();
        for (int i = 0; i < children.size(); i++) {
            //System.out.println("Child " + children.get(i).getCoverScoreG());
            if (this.referenceBoard.containsKey(children.get(i).getGuardPos()[0]) && this.referenceBoard.get(children.get(i).getGuardPos()[0]).containsKey(children.get(i).getGuardPos()[1]) && !this.referenceBoard.get(children.get(i).getGuardPos()[0]).get(children.get(i).getGuardPos()[1]).isWall()){
                if (children.get(i).getCoverScoreG() > max){
                    max = children.get(i).getCoverScoreG();
                    best = children.get(i).getGuardPos();
                }
            }
        }
        return best;
    }

    public int[] getBestMoveI(){
//        System.out.println("Orientation " + guard.getOrientation());
        calculate();
        ArrayList<CoverNode> children = this.root.getChildren();
        int max = -1;
        int[] best = this.root.getIntruderPos();
        for (int i = 0; i < children.size(); i++) {
            if (this.referenceBoard.containsKey(children.get(i).getIntruderPos()[0]) && this.referenceBoard.get(children.get(i).getIntruderPos()[0]).containsKey(children.get(i).getIntruderPos()[1]) && !this.referenceBoard.get(children.get(i).getIntruderPos()[0]).get(children.get(i).getIntruderPos()[1]).isWall()){
                if (children.get(i).getCoverScoreI() > max){
                    max = children.get(i).getCoverScoreI();
                    best = children.get(i).getIntruderPos();
                }
            }
        }
        return best;
    }
}