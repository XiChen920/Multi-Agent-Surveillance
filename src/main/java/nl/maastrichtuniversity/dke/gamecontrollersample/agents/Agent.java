package nl.maastrichtuniversity.dke.gamecontrollersample.agents;

import nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound.Rustle;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Direction;
import nl.maastrichtuniversity.dke.gamecontrollersample.tools.AStar.AStar;
import nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.Vision;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.util.*;

public abstract class Agent {
    protected int number;
    protected int x;
    protected int y;
    protected double orientation;
    protected Board board;
    protected Grid positionCell;
    protected int xRelative;// x-coordinate of agent's position in the local coordinate system
    protected int yRelative;// y-coordinate of agent's position in the local coordinate system
    protected double baseSpeed;
    protected List<Direction> pathToTarget = new ArrayList<>();
    protected double sprintSpeed;
    protected int stamina;
    protected boolean wantRun = false;
    protected double scaling;
    protected Grid targetGrid;
    protected Vision vision;
    protected ArrayList<Grid> visibleCells;
    protected ArrayList<Grid> knownCells;
    protected int xOffset;
    protected int yOffset;
    protected HashMap <Integer, HashMap<Integer, Grid>> localMap;
    protected ArrayList<double[]> listOfRustles;
    protected Direction targetDirection;
    private Grid[][] boardXY;
    protected int markerLimit; //Upperbound on the markers
    private boolean isRandomAgent;
    protected int rustleCleaner = 0;


    public Agent(int number, Grid cell, double angle, double baseSpeed, double scaling, Board gameBoard, boolean isRandomAgent) {
        this.board = gameBoard;
        this.boardXY= gameBoard.getBoardXY();
        this.number = number;
        this.positionCell = cell;
        this.x = positionCell.getX();
        this.y = positionCell.getY();
        this.orientation = angle;
        this.xRelative = 0;
        this.yRelative = 0;
        this.xOffset = positionCell.getX() - this.xRelative;
        this.yOffset = positionCell.getY() - this.yRelative;
        this.scaling = scaling;
        this.baseSpeed = baseSpeed;
        this.vision = new Vision(positionCell, this.xOffset, this.yOffset, orientation, this.board);
        this.visibleCells = this.vision.getViewingArea();
        this.knownCells = this.visibleCells;
        this.localMap = new HashMap<>();
        this.stamina = 5;
        this.listOfRustles = positionCell.getListOfRustles();
        this.isRandomAgent = isRandomAgent;
        addKnowledge(this.knownCells);
        produceRustle();
    }

    public Agent(int number, Grid cell, double angle, double baseSpeed, double sprintSpeed, double scaling, Board gameBoard, boolean isRandomAgent) {
        this.board = gameBoard;
        this.number = number;
        this.positionCell = cell;
        this.x = positionCell.getX();
        this.y = positionCell.getY();
        this.orientation = angle;
        this.xRelative = 0;
        this.yRelative = 0;
        this.xOffset = positionCell.getX() - this.xRelative;
        this.yOffset = positionCell.getY() - this.yRelative;
        this.scaling = scaling;
        this.baseSpeed = baseSpeed;
        this.sprintSpeed = sprintSpeed;
        this.vision = new Vision(positionCell, this.xOffset, this.yOffset, orientation, this.board);
        this.visibleCells = this.vision.getViewingArea();
        this.knownCells = this.visibleCells;
        this.localMap = new HashMap<>();
        this.stamina = 5;
        this.wantRun = false;
        this.listOfRustles = positionCell.getListOfRustles();
        addKnowledge(this.knownCells);
        produceRustle();
    }

    //Returns the local map
    public HashMap<Integer, HashMap<Integer, Grid>> getLocalMap() {return localMap;}

    // adds the cells that have been seen to the local map
    public void addKnowledge(ArrayList<Grid> viewingArea){
        Grid currentCell;
        int k1;
        int k2;
        for(int i=0; i < viewingArea.size(); i++){
            currentCell = viewingArea.get(i);
            k1 = currentCell.getX();
            k2 = currentCell.getY();
            if (! localMap.containsKey(k1)) {
                localMap.put(k1, new HashMap<>());
                localMap.get(k1).put(k2, currentCell);
            }
            else if (! localMap.get(k1).containsKey(k2)){
                localMap.get(k1).put(k2, currentCell);
            }
        }
    }

    // updates the agent's position, orientation and knowledge
    public void updateStep(double angle, Grid cell){
        setOrientation(angle);
        setCell(cell);
        visibleCells = discoverVisibleCells();
        updateKnownCells(visibleCells);
        addKnowledge(visibleCells);
        targetDirection = null;
        produceRustle();
    }


    public void clear(){
        positionCell.setReferenceIntruder(null);
        positionCell.setReferenceGuard(null);
        visibleCells = null;
        targetDirection = null;
        targetGrid = null;
    }

    // calculates a paths from the current position to the target one
    public void findShortestPath(Grid target){
        this.pathToTarget = new AStar().computePath(localMap, new Grid(xRelative, yRelative), orientation, target);
    }

    // get cells discovered by the current vision
    public ArrayList<Grid> discoverVisibleCells(){
        Vision newVision = new Vision(positionCell, xOffset, yOffset, orientation, board);
        visibleCells = newVision.getViewingArea();
        return visibleCells;
    }

    public void updateKnownCells(ArrayList<Grid> visibleCells){
        int l_visible = visibleCells.size();
        for(int j=0; j< l_visible; j++){
            if( ! localMap.containsKey(visibleCells.get(j).getX())){
                knownCells.add(visibleCells.get(j));
            }
            else if( ! localMap.get(visibleCells.get(j).getX()).containsKey(visibleCells.get(j).getY())){
                knownCells.add(visibleCells.get(j));
            }
        }

    }

    public Direction calculateDirection(double targetOrientation){
        Direction coordinates;
        if(targetOrientation >= 45 && targetOrientation < 135) {
            coordinates = Direction.RIGHT;
        }
        else if(targetOrientation >= 135 && targetOrientation < 225) {
            coordinates = Direction.DOWN;
        }
        else if(targetOrientation >= 225 && targetOrientation < 315) {
            coordinates = Direction.LEFT;
        }
        else {
            coordinates = Direction.UP;
        }
        return coordinates;
    }


    public void produceRustle(){
        new Rustle(positionCell, board);
    }

    public void getSound(){
        positionCell.getListOfRustles();
    }


    public Grid getTargetGrid() {
        return targetGrid;
    }

    public Direction getTargetDirection(){
        return targetDirection;
    }

    public void setTargetGrid(Grid targetGrid) {
        this.targetGrid = targetGrid;
    }

    public int getxOffset(){
        return  xOffset;
    }

    public int getyOffset(){
        return yOffset;
    }

    public int getxRelative(){
        return xRelative;
    }

    public int getyRelative(){
        return yRelative;
    }


    public double getBaseSpeed() {
        return baseSpeed;
    }

    public void setBaseSpeed(double baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public void pickRandomTarget(){
        Set setOfXkeys = this.localMap.keySet();
        Object [] xKeys = setOfXkeys.toArray();
        int xRandom = (int) xKeys[(int) (Math.random()*xKeys.length)];
        Set setOfYkeys = this.localMap.get(xRandom).keySet();
        Object [] yKeys = setOfYkeys.toArray();
        int yRandom = (int) yKeys[(int) (Math.random()*yKeys.length)];
        this.targetGrid = this.localMap.get(xRandom).get(yRandom);
    }

    public Grid getCell() {
        return positionCell;
    }

    public void setCell(Grid cell) {
        this.positionCell = cell;
    }


    public int getNumber() {
        return number;
    }


    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double getOrientation() {
        return orientation;
    }


    public void setNumber(int number) {
        this.number = number;
    }


    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

    public ArrayList<Grid> getCurrentVisibleCells() {
        return visibleCells;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int sta) {
        this.stamina = sta;
    }

    public boolean isWantRun() {
        return wantRun;
    }

    public void setWantRun(boolean wantRun) {
        this.wantRun = wantRun;
    }

    public double getSprintSpeed() {
        return sprintSpeed;
    }

    public void setSprintSpeed(double sprintSpeed) {
        this.sprintSpeed = sprintSpeed;
    }

    //check whether current position is in the corner
    public boolean isCorner(int xx,int yy){
        boolean b1= (boardXY[xx-1][yy].isWall());
        boolean b2= (boardXY[xx+1][yy].isWall());
        boolean b3= (boardXY[xx][yy-1].isWall());
        boolean b4= (boardXY[xx][yy+1].isWall());

        if((b1&&b3)||(b1&&b4)||(b2&&b3)||(b2&&b4)){

            return true;
        }
        return false;
    }
    public boolean isTele(int xx, int yy){

        boolean b1= boardXY[xx+1][yy].isTelePortal();
        boolean b2= boardXY[xx-1][yy].isTelePortal();
        boolean b3= boardXY[xx][yy+1].isTelePortal();
        boolean b4= boardXY[xx][yy-1].isTelePortal();
        return (b1||b2||b3||b4);
    }


    public Grid exploreNextStep2(){
        return null;
    }


    public void exploreNextStep() {

    }

    public void findNextStep(Object o) {
    }

    public boolean isRandomAgent() {
        return isRandomAgent;
    }

    public List<Direction> getPathToTarget() {
        return pathToTarget;
    }

    public void setPathToTarget(List<Direction> pathToTarget) {
        this.pathToTarget = pathToTarget;
    }

    /*
    // returns the target cell the agent should go next
    // the coordinates of this cell are in the local coordinate system
    public void exploreNextStep(){
        ArrayList<int[]> neighbours;
        int numberOfUnknownNeighbours;
        int distance;
        double weight;
        double maxWeight = -1;
        int x;
        int y;
        Grid currentCell;
        // looping through the known cells
        for(int i=0; i<knownCells.size(); i++){
            numberOfUnknownNeighbours = 0;
            currentCell = knownCells.get(i);
            if(!currentCell.hasGuard() && !currentCell.isWall() && !(currentCell.getX() == xRelative && currentCell.getY() == yRelative) && !currentCell.isTelePortal()){
                neighbours = currentCell.getNeighbours();
                // for each of known cells we retrieve the list of neighbours that are not walls or don't have guards and loop through them
//                    System.out.println("looking at currently known cell (" + knownCells.get(i).getX() + "; " + knownCells.get(i).getY() + ") - " + (i+1) + " out of " + knownCells.size());
                for(int j=0; j<neighbours.size(); j++){
                    x = neighbours.get(j)[0];
                    y = neighbours.get(j)[1];
                    // if a neighbour of a known cell is unknown, we increase the number which will be used in weight calculation
                    if(!localMap.containsKey(x)){
                        numberOfUnknownNeighbours++;
                    }
                    else if (!localMap.get(x).containsKey(y)){
                        numberOfUnknownNeighbours++;
                    }
                }
//                    System.out.println("It has " + numberOfUnknownNeighbours + " unknown neighbours");
                if(numberOfUnknownNeighbours > 0) {
                    distance = getDistance(xRelative, yRelative, currentCell.getX(), currentCell.getY());
                    weight = calculateWeight(numberOfUnknownNeighbours, distance);
                }
                else { weight = 0;}
//                        System.out.println("It has weight " + weight);
                if (weight > maxWeight){
                    targetGrid = currentCell;
                    maxWeight = weight;
                }
            }
        }
    }
*/
}
