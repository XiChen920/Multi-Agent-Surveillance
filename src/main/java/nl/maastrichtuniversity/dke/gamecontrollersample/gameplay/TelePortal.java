/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Area;

/**
 *
 * @author joel
 */
public class TelePortal extends Area {
    protected int yTarget;
    protected int xTarget;
    protected double outOrientation;
    
    public TelePortal(int x1, int y1, int x2, int y2, int targetX, int targetY){
        super(x1,y1,x2,y2);
        yTarget=targetY;
        xTarget=targetX;
        outOrientation = 0.0;
    }

    public TelePortal(int x1, int y1, int x2, int y2, int targetX, int targetY, double orient){
        super(x1,y1,x2,y2);
        yTarget=targetY;
        xTarget=targetX;
        outOrientation = orient;
    }
    
    public int[] getNewLocation(){
        return new int[] {xTarget,yTarget};
    }

    public double getNewOrientation(){
        int temp = (int) (Math.random()*4);
        switch (temp){
            case 0:
                return 0;
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
                return 270;
        }
        return 0;
    }

    public int getYtarget(){
        return yTarget;
    }

    public int getXtarget(){
        return xTarget;
    }

}
