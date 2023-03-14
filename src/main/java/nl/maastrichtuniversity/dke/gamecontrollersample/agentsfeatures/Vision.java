package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Vision {

    private boolean switchToConeVersion = true; //false: rectangle viewing area
                                                //true: cone viewing area

    //visual capabilities
    private int distanceViewing = 6;

    //observation point
    private Grid agentPosition;
    private double viewingOrientation;
    private int x;
    private int y;
    private int xOffset;
    private int yOffset;


    //special areas
    private ArrayList<Grid> localViewingArea;
    private ArrayList<Grid> viewingArea;
    private ArrayList<Grid> previousViewingArea;

    private ArrayList<Grid> wallArea;

    //game board
    private Grid[][] board;

    public Vision(Grid agentPosition,int xOffset, int yOffset, double viewingOrientation, Board gameBoard){

        this.agentPosition = agentPosition;
        this.x = agentPosition.getX();
        this.y = agentPosition.getY();
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.board = gameBoard.getBoardXY();
        this.viewingOrientation = changeLayout(viewingOrientation);
        this.localViewingArea = new ArrayList<>();
        this.viewingArea = new ArrayList<>();
        this.previousViewingArea = new ArrayList<>();
        this.wallArea = new ArrayList<>();
        //if the agent is in shaded area, distance viewing will reduce
        if(board[x][y].isShade()){
            this.distanceViewing = 4;
        }
        //if the agent is in bright area, distance viewing will rise
        //around vision marker is the bright area
        if(board[x][y].isBright()){
            this.distanceViewing+=3;
        }
    }

    public Vision(Grid agentPosition, double viewingOrientation, Grid[][] testingBoard){//for testing

        this.agentPosition = agentPosition;
        this.x = agentPosition.getX();
        this.y = agentPosition.getY();
        this.viewingOrientation = changeLayout(viewingOrientation);
        this.wallArea = new ArrayList<>();
        this.viewingArea = new ArrayList<>();
        this.board =  testingBoard;
        //if the agent is in shaded area, distance viewing will reduce
        if(board[x][y].isShade()){
            this.distanceViewing = 4;
        }
    }

    public double getViewingOrientation(){
        return this.viewingOrientation;
    }

    public ArrayList<Grid> getViewingArea(){
        resetViewingArea();
        resetLocalViewingArea();
        int xRelative;
        int yRelative;
        Grid copyCell;
        if(viewingOrientation<45){
            double degree =viewingOrientation;
            for(int i =0;i<distanceViewing;i++){
                int deltaX = (int) Math.round(Math.tan(Math.toRadians(degree)) * i);
                    addLeftRightArea(x+deltaX,y+i,i);
            }
        }
        else if(viewingOrientation<90){
            double degree = 90-viewingOrientation;
            for(int i =0;i<distanceViewing;i++){
                int deltaY =(int) Math.round(Math.tan(Math.toRadians(degree)) * i) ;
                    addUpperDownArea(x+i,y+deltaY,i);
            }
        }
        else if(viewingOrientation<135){
            double degree = viewingOrientation-90;
            for(int i =0;i<distanceViewing;i++){
                int deltaY = -(int) Math.round(Math.tan(Math.toRadians(degree)) * i);
                addUpperDownArea(x+i,y+deltaY,i);
            }
        }
        else if(viewingOrientation<180){
            double degree =180-viewingOrientation;
            for(int i =0;i<distanceViewing;i++){
                int deltaX = (int) Math.round(Math.tan(Math.toRadians(degree)) * i);
                addLeftRightArea(x+deltaX,y-i,i);
            }
        }
        else if(viewingOrientation<225){
            double degree =viewingOrientation-180;
            for(int i =0;i<distanceViewing;i++){
                int deltaX = -(int) Math.round(Math.tan(Math.toRadians(degree)) * i);
                    addLeftRightArea(x+deltaX,y-i,i);
            }
        }
        else if(viewingOrientation<270){
            double degree =270-viewingOrientation;
            for(int i =0;i<distanceViewing;i++){
                int deltaY = -(int) Math.round(Math.tan(Math.toRadians(degree)) * i);
                    addUpperDownArea(x-i,y+deltaY,i);
            }
        }
        else if(viewingOrientation<315){
            double degree =viewingOrientation-270;
            for(int i =0;i<distanceViewing;i++){
                int deltaY = (int) Math.round(Math.tan(Math.toRadians(degree)) * i);
                addUpperDownArea(x-i,y+deltaY,i);
            }
        }
        else if(viewingOrientation<360){
            double degree =360-viewingOrientation;
            for(int i =0;i<distanceViewing;i++){
                int deltaX = -(int) Math.round(Math.tan(Math.toRadians(degree)) * i);
                addLeftRightArea(x+deltaX,y+i,i);
            }
        }
        detectWall();//record the grid in the vision that is a wall
        wallInteraction();//remove the grid blocked by wall from viewing area

        // convert real coordinates of each cell to agent's own coordinate system
        for(int i=0; i<viewingArea.size(); i++){
//            viewingArea.get(i).setViewed(true);
            viewingArea.get(i).setExplored(true);
            xRelative = viewingArea.get(i).getX() - xOffset;
            yRelative = viewingArea.get(i).getY() - yOffset;
            copyCell = viewingArea.get(i).getCopy();
            copyCell.setX(xRelative);
            copyCell.setY(yRelative);
            localViewingArea.add(copyCell);
        }

        return localViewingArea;
    }

    public void addLeftRightArea(int xx, int yy,int layer){

        //cone
        if(switchToConeVersion==true){
            for(int i=xx-layer;i<xx+layer+1;i++){
                if(isLegalGrid(i,yy)){
                    viewingArea.add(board[i][yy]);
                }
            }
        }
        else{//rectangle
            for(int i=xx-1;i<xx+2;i++){
                if(isLegalGrid(i,yy)){
                    viewingArea.add(board[i][yy]);
                }
            }
        }

        if(layer==0){
            viewingArea.add(board[xx+1][yy]);
            viewingArea.add(board[xx-1][yy]);
        }

    }

    public void addUpperDownArea(int xx, int yy,int layer){

        // cone
        if(switchToConeVersion==true){
            for(int i=yy-layer;i<yy+layer+1;i++){
                if(isLegalGrid(xx,i)){
                    viewingArea.add(board[xx][i]);
                }
            }
        }
        else{ //rectangle
            for(int i=yy-1;i<yy+2;i++){
                if(isLegalGrid(xx,i)){
                    viewingArea.add(board[xx][i]);
                }
            }
        }
        if(layer==0){
            viewingArea.add(board[xx][yy+1]);
            viewingArea.add(board[xx][yy-1]);
        }

    }

    // check whether the grid is inside the game board
    public boolean isLegalGrid(int x, int y){
        if(x>=0 && y>=0 && x< board.length && y<board[0].length){
            return true;
        }
        return false;
    }

    // the original implementation is based on a plane
    // the actual layout of this board puts the(0,0) on top left
    // this method is to make the original implementation fit the actual layout
    public double changeLayout(double originalDegree){
        double actualDegree = originalDegree - 90;
        if(actualDegree<0){
            actualDegree = 360 + actualDegree;
        }
        return actualDegree;
    }

    // check whether the grid in the vision is a wall
    public void detectWall(){
        for(int i = 0; i<viewingArea.size();i++){
            if(viewingArea.get(i).isWall()){
                wallArea.add(viewingArea.get(i));
            }
        }
    }

    /**
     *  check whether a grid is blocked by wall
     * @param g  the grid to check
     * @param wall the wall grid
     * @return true if is blocked
     */
    public boolean blockedByWall(Grid g, Grid wall){

        double wallX = wall.getX();
        double wallY = wall.getY();

        //check whether is blocked horizontally or vertically
        boolean isBlockedByWallHorizontally = Line2D.linesIntersect(wallX-0.5,wallY,wallX+0.5,wallY,x,y,g.getX(),g.getY());
        boolean isBlockedByWallVertically = Line2D.linesIntersect(wallX,wallY-0.5,wallX,wallY+0.5,x,y,g.getX(),g.getY());

        //prevent the situation that a grid is detected to be unseen because itself is a wall
        boolean isSamePoint = false;
        if(wallX == g.getX() && wallY == g.getY()){
            isSamePoint = true;
        }

        return((isBlockedByWallHorizontally||isBlockedByWallVertically)&&(!isSamePoint));
    }


    //remove the grid blocked by wall from viewing area
    public void wallInteraction(){

        for(int i = (viewingArea.size()-1); i >=0;i--){
            for(int j = 0; j < wallArea.size();j++){
                if(blockedByWall(viewingArea.get(i),wallArea.get(j))){
                    viewingArea.remove(i);
                    //System.out.println(i);
                    break;
                }
            }
        }
    }



    // after one step we reset the viewing area to avoid storing all the previous information there, we have maps for that
    public void resetViewingArea(){

        this.viewingArea = new ArrayList<>();
    }
    public void resetLocalViewingArea(){
        this.localViewingArea = new ArrayList<>();
    }


}
