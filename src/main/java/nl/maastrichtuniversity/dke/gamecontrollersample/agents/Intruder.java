package nl.maastrichtuniversity.dke.gamecontrollersample.agents;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;

import nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound.Rustle;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.BoardHelper;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Direction;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.ManhDistance;

public class Intruder extends Agent {
    private int x;
    private int y;
    private double[] lastSeenGuardLoc;
    private boolean caught = false;
    private boolean escaped = false;
    private int targetSecond = 0;
    private boolean guardSeen;
    private final int TARGET_TIMER = 3;
    //track marker by guards
    private boolean marked;
    private int markedTim;// the time that the intruder is marked



    public Intruder(int number, Grid cell, double angle, double baseSpeed, double sprintS, double scaling, Board gameBoard, boolean isRandomAgent) {
        super(number, cell, angle, baseSpeed,sprintS, scaling, gameBoard, isRandomAgent);
    }

    @Override
    public void findNextStep(Object o) {
            ChaseGA();
    }

    public void updateStep(double angle, Grid cell){
        setOrientation(angle);
        setCell(cell);
        visibleCells = discoverVisibleCells();
        updateKnownCells(visibleCells);
        addKnowledge(visibleCells);
        listOfRustles = positionCell.getListOfRustles();
        targetDirection = null;
        produceRustle();
    }

    public void atTargetArea(){
        if(this.positionCell.isTarget()){
            targetSecond++;
            System.out.println("Intruder " + number + " is at Target Area (" + targetSecond + ")");
        } else {
            if(targetSecond > 0) System.out.println("Intruder " + number + " left Target Area");
            targetSecond = 0;
        }

       if(targetSecond == TARGET_TIMER)
            this.escaped = true;
    }


    public int[] getTargetCell() {
        int[] target = new int[2];
        for (int i = 0; i < board.getBoardXY().length; i++) {
            for (int j = 0; j < board.getBoardXY()[0].length; j++) {
                if (board.getGrid(i, j).isTarget()) {
                    target[0] = i;
                    target[1] = j;
                    return target;
                }
            }
        }
        return null;
    }

    public void ChaseGA() {
        if(listOfRustles.size() > 0){

            Direction direction = Direction.getOpposite(findSoundDirection());

            int xCoord = xRelative + direction.getxRelative();
            int yCoord = yRelative + direction.getyRelative();
//            System.out.println("X = " + xCoord + "  Y = " + yCoord);
            targetGrid = BoardHelper.getByCoordinates(localMap, xCoord, yCoord);

            if(targetGrid == null || targetGrid.isWall()){
                calculateFrontier();
            }
        } else {
            calculateFrontier();
        }
    }

    private Direction findSoundDirection(){
        double[] maxIntensity = new double[]{Double.MIN_VALUE, 0};
        for(double[] rustle : listOfRustles){
            if(rustle[0] > maxIntensity[0])
                maxIntensity = rustle;
        }
        return calculateDirection(maxIntensity[1]);
    }

    // check if the guard sees an intruder at the moment
    public void detectAnotherAgent(){
        boolean intruderSeen = false;
        guardSeen = false;
        Grid currentCell;
        for(int i=0; i<visibleCells.size(); i++ ){
            currentCell = visibleCells.get(i);
            if(currentCell.hasIntruder()){
                intruderSeen = true;
                rustleCleaner = 15;
            } else if(currentCell.isTarget()){
                targetGrid = currentCell;
            } else if(currentCell.hasGuard()){
                guardSeen = true;
            }
        }

        if(intruderSeen || rustleCleaner > 0){
            listOfRustles.clear();
            rustleCleaner--;
        }
    }

    private final HashMap <Integer, HashMap<Integer, Boolean>> visited = new HashMap<>();

    private void calculateFrontier(){
        double weight;
        double maxWeight = -1;
        // looping through the known cells
        for (Grid knownCell : knownCells) {
            if (!knownCell.isWall() && !(knownCell.getX() == xRelative && knownCell.getY() == yRelative) && !knownCell.isTelePortal()) {

                double distance = ManhDistance.compute(new Grid(xRelative, yRelative), knownCell);
                weight = knownCell.getTargetIntensity() * calculateWeight(distance);

                if (visited.containsKey(knownCell.getX())) {
                    if (visited.get(knownCell.getX()).containsKey(knownCell.getY())) {
                        continue;
                    }
                }

                if (weight > maxWeight) {
                    targetGrid = knownCell;
                    maxWeight = weight;
                }
            }
        }

        if (!visited.containsKey(targetGrid.getX())) {
            visited.put(targetGrid.getX(), new HashMap<>());
        }
        visited.get(targetGrid.getX()).put(targetGrid.getY(), Boolean.TRUE);
    }


//    public void ChaseGA(){
//        Grid targetCell = null;
//        double lowerBoundAngle = 0;
//        double upperBoundAngle = 0;
//        double[] guardLoc = null;
//        // the agent has certain scenarios in which it has a different plan of movement
//
//        //first we check for triggers to see what scenario we put the agent in defeault sceneario will be moving to target
//        // sceneraio 1 move to target, default
//        int scenario = 1;
//        ArrayList<Grid> currentVis = visibleCells;
//        getSound();
//
//        ArrayList<double[]> rustles = listOfRustles;
//        if(!rustles.isEmpty() && pathToTarget.isEmpty()){
//            scenario = 2;
//        }
//        for(Grid i : currentVis){
//            if(i.getReferenceTarget()){
//                scenario = 4;
//            } else if(i.hasGuard()){
//                guardLoc = new double[]{i.getX(), i.getY()};
//                lastSeenGuardLoc = new double[]{i.getX(), i.getY()};
//                scenario = 3;
//            }
//        }
////        System.out.println("Scenario: "+scenario);
//        if(scenario == 1 ){
//            ArrayList<int[]> neighbours;
//            double targetAngle = getAngleInt(new double[] {getDirectionToTarget()[0]+this.getCell().getX(),getDirectionToTarget()[1]+this.getCell().getY()},new double[] {this.getCell().getX(),this.getCell().getY()});
//            if(targetAngle<0){
//                targetAngle = 2*Math.PI+targetAngle;
//            }
//            int numberOfUnknownNeighbours;
//            int distance;
//            double weight;
//            double maxWeight = -1;
//            Grid currentCell;
//
//            // replace with if statement
//            if(targetAngle >= 0 && targetAngle < 0.25*Math.PI){
//                lowerBoundAngle = 0;
//                upperBoundAngle = 0.25*Math.PI;
//            }
//            if(targetAngle >= 0.25*Math.PI && targetAngle < 0.5*Math.PI){
//                lowerBoundAngle = 0.25*Math.PI;
//                upperBoundAngle = 0.5*Math.PI;
//            }
//            if(targetAngle >= 0.5*Math.PI && targetAngle < 0.75*Math.PI){
//                lowerBoundAngle = 0.5*Math.PI;
//                upperBoundAngle = 0.75*Math.PI;
//            }
//            if(targetAngle >= 0.75*Math.PI && targetAngle < Math.PI){
//                lowerBoundAngle = 0.75*Math.PI;
//                upperBoundAngle = Math.PI;
//            }
//            if(targetAngle >= Math.PI && targetAngle < 1.25*Math.PI){
//                lowerBoundAngle = Math.PI;
//                upperBoundAngle = 1.25*Math.PI;
//            }
//            if(targetAngle >= 1.25*Math.PI && targetAngle < (3/2)*Math.PI){
//                lowerBoundAngle = 1.25*Math.PI;
//                upperBoundAngle = (3/2)*Math.PI;
//            }
//            if(targetAngle >= (3/2)*Math.PI && targetAngle < 1.75*Math.PI){
//                lowerBoundAngle = (3/2)*Math.PI;
//                upperBoundAngle = 1.75*Math.PI;
//            }
//            if(targetAngle >= 1.75*Math.PI && targetAngle < 2*Math.PI){
//                lowerBoundAngle = 1.75*Math.PI;
//                upperBoundAngle = 2*Math.PI;
//            }
//            //System.out.println(knownCells.size()+"known cell");
//            // looping through the known cells
//            for(int i=0; i<knownCells.size(); i++){
//                numberOfUnknownNeighbours = 0;
//                currentCell = knownCells.get(i);
//                double currentCellAngle = getAngleInt(new double[]{currentCell.getX()+this.xOffset, currentCell.getY()+this.yOffset},new double[] {getCell().getX(),getCell().getY()});
//               //System.out.println("cell angle "+currentCellAngle);
//                //System.out.println("cell location "+Arrays.toString(new double[]{currentCell.getX()+this.xOffset,currentCell.getY()+this.yOffset}));
//                if(currentCellAngle<0){
//                    currentCellAngle = 2*Math.PI+currentCellAngle;
//                }
//                /*
//                System.out.println("--------------------");
//                System.out.println(!currentCell.hasGuard());
//                System.out.println(!currentCell.isWall());
//                System.out.println(!(currentCell.getX() == xRelative && currentCell.getY() == yRelative));
//
//                 */
//                if(!currentCell.hasGuard() && !currentCell.isWall() &&!(currentCell.getX() == xRelative && currentCell.getY() == yRelative)){
//                    neighbours = currentCell.getNeighbours();
//                    // for each of known cells we retrieve the list of neighbours that are not walls or don't have guards and loop through them
//                    //                    System.out.println("looking at currently known cell (" + knownCells.get(i).getX() + "; " + knownCells.get(i).getY() + ") - " + (i+1) + " out of " + knownCells.size());
//                    for(int j=0; j<neighbours.size(); j++){
//                        x = neighbours.get(j)[0];
//                        y = neighbours.get(j)[1];
//                        // if a neighbour of a known cell is unknown, we increase the number which will be used in weight calculation
//                        if(!localMap.containsKey(x)){
//                            numberOfUnknownNeighbours++;
//                        }
//                        else if (!localMap.get(x).containsKey(y)){
//                            numberOfUnknownNeighbours++;
//                        }
//                    }
//                    //System.out.println("It has " + numberOfUnknownNeighbours + " unknown neighbours");
//                    if(numberOfUnknownNeighbours > 0) {
//                        distance = ManhDistance.compute(new Grid(xRelative, yRelative), currentCell);
//                        if(currentCellAngle <= upperBoundAngle && currentCellAngle >= lowerBoundAngle){
//                            weight = calculateWeightBiased(numberOfUnknownNeighbours, distance, 10);
//                        }
//                        else{
//                            weight = calculateWeightBiased(numberOfUnknownNeighbours, distance, 0);
//                        }
//                    }
//                    else { weight = 0;}
//                                        //System.out.println("It has weight " + weight);
//                    if (weight > maxWeight){
//                        targetCell = currentCell;
//                        maxWeight = weight;
//                    }
//
//                }
//            }
//        } if(scenario == 2){
//            double targetAngle1 = getAngleInt(new double[] {getDirectionToTarget()[0]+this.getCell().getX(),getDirectionToTarget()[1]+this.getCell().getY()},new double[] {this.getCell().getX(),this.getCell().getY()});
//            double upperBoundAngle1=0;
//            double lowerBoundAngle1=0;
//            if(targetAngle1<0){
//                targetAngle1 = 2*Math.PI+targetAngle1;
//            }
//
//            if(targetAngle1 >= 0 && targetAngle1 < 0.25*Math.PI){
//                lowerBoundAngle1 = 0;
//                upperBoundAngle1 = 0.25*Math.PI;
//            }
//            if(targetAngle1 >= 0.25*Math.PI && targetAngle1 < 0.5*Math.PI){
//                lowerBoundAngle1 = 0.25*Math.PI;
//                upperBoundAngle1 = 0.5*Math.PI;
//            }
//            if(targetAngle1 >= 0.5*Math.PI && targetAngle1 < 0.75*Math.PI){
//                lowerBoundAngle1 = 0.5*Math.PI;
//                upperBoundAngle1 = 0.75*Math.PI;
//            }
//            if(targetAngle1 >= 0.75*Math.PI && targetAngle1 < Math.PI){
//                lowerBoundAngle1 = 0.75*Math.PI;
//                upperBoundAngle1 = Math.PI;
//            }
//            if(targetAngle1 >= Math.PI && targetAngle1 < 1.25*Math.PI){
//                lowerBoundAngle1 = Math.PI;
//                upperBoundAngle1 = 1.25*Math.PI;
//            }
//            if(targetAngle1 >= 1.25*Math.PI && targetAngle1 < (3/2)*Math.PI){
//                lowerBoundAngle1 = 1.25*Math.PI;
//                upperBoundAngle1 = (3/2)*Math.PI;
//            }
//            if(targetAngle1 >= (3/2)*Math.PI && targetAngle1 < 1.75*Math.PI){
//                lowerBoundAngle1 = (3/2)*Math.PI;
//                upperBoundAngle1 = 1.75*Math.PI;
//            }
//            if(targetAngle1 >= 1.75*Math.PI && targetAngle1 < 2*Math.PI){
//                lowerBoundAngle1 = 1.75*Math.PI;
//                upperBoundAngle1 = 2*Math.PI;
//            }
//            ArrayList<int[]> neighbours;
//            // direction towards sounds
//            int numberOfUnknownNeighbours;
//            int distance;
//            double weight;
//            double maxWeight = -1;
//            int x;
//            int y;
//            Grid currentCell;
//
//            // looping through the known cells
//            for(int i=0; i<knownCells.size(); i++){
//                boolean hasSound = false;
//                numberOfUnknownNeighbours = 0;
//                currentCell = knownCells.get(i);
//                double currentCellAngle = getAngleInt(new double[]{currentCell.getX(), currentCell.getY()},new double[] {getCell().getX(),getCell().getY()});
//                if(currentCellAngle<0){
//                    currentCellAngle = 2*Math.PI+currentCellAngle;
//                }
//                for(double[] j:listOfRustles){
//                    double targetAngle = j[1];
//                    if(targetAngle > 0 && targetAngle <= 0.25*Math.PI){
//                        lowerBoundAngle = 0;
//                        upperBoundAngle = 0.25*Math.PI;
//                    }
//                    if(targetAngle > 0.25*Math.PI && targetAngle <= 0.5*Math.PI){
//                        lowerBoundAngle = 0.25*Math.PI;
//                        upperBoundAngle = 0.5*Math.PI;
//                    }
//                    if(targetAngle > 0.5*Math.PI && targetAngle <= 0.75*Math.PI){
//                        lowerBoundAngle = 0.5*Math.PI;
//                        upperBoundAngle = 0.75*Math.PI;
//                    }
//                    if(targetAngle > 0.75*Math.PI && targetAngle <= Math.PI){
//                        lowerBoundAngle = 0.75*Math.PI;
//                        upperBoundAngle = Math.PI;
//                    }
//                    if(targetAngle > Math.PI && targetAngle <= 1.25*Math.PI){
//                        lowerBoundAngle = Math.PI;
//                        upperBoundAngle = 1.25*Math.PI;
//                    }
//                    if(targetAngle >=1.25*Math.PI && targetAngle <= (3/2)*Math.PI){
//                        lowerBoundAngle = 1.25*Math.PI;
//                        upperBoundAngle = (3/2)*Math.PI;
//                    }
//                    if(targetAngle > (3/2)*Math.PI && targetAngle <= 1.75*Math.PI){
//                        lowerBoundAngle = (3/2)*Math.PI;
//                        upperBoundAngle = 1.75*Math.PI;
//                    }
//                    if(targetAngle > 1.75*Math.PI && targetAngle <= 2*Math.PI){
//                        lowerBoundAngle = 1.75*Math.PI;
//                        upperBoundAngle = 2*Math.PI;
//                    }
//                    if(currentCellAngle <= upperBoundAngle&& currentCellAngle > lowerBoundAngle){
//                        hasSound = true;
//                    }
//                }
//                if(!currentCell.hasGuard() && !currentCell.isWall() && !(currentCell.getX() == xRelative && currentCell.getY() == yRelative)){
//                    neighbours = currentCell.getNeighbours();
//                    // for each of known cells we retrieve the list of neighbours that are not walls or don't have guards and loop through them
//                    //                    System.out.println("looking at currently known cell (" + knownCells.get(i).getX() + "; " + knownCells.get(i).getY() + ") - " + (i+1) + " out of " + knownCells.size());
//                    for(int j=0; j<neighbours.size(); j++){
//                        x = neighbours.get(j)[0];
//                        y = neighbours.get(j)[1];
//                        // if a neighbour of a known cell is unknown, we increase the number which will be used in weight calculation
//                        if(!localMap.containsKey(x)){
//                            numberOfUnknownNeighbours++;
//                        }
//                        else if (!localMap.get(x).containsKey(y)){
//                            numberOfUnknownNeighbours++;
//                        }
//                    }
//                    //                    System.out.println("It has " + numberOfUnknownNeighbours + " unknown neighbours");
//                    if(numberOfUnknownNeighbours > -1) {
//
//                        distance = ManhDistance.compute(new Grid(xRelative, yRelative), currentCell);
//                        if(hasSound &&!(currentCellAngle <= upperBoundAngle1&& currentCellAngle >= lowerBoundAngle1)){
//                            weight = calculateWeightBiased(numberOfUnknownNeighbours, distance, -10);
//                        }
//                        else if((currentCellAngle <= upperBoundAngle1&& currentCellAngle >= lowerBoundAngle1)&&hasSound){
//                            weight = calculateWeightBiased(numberOfUnknownNeighbours, distance, -2);
//                        }
//                        else if((currentCellAngle <= upperBoundAngle1&& currentCellAngle >= lowerBoundAngle1)&&!hasSound){
//                            weight = calculateWeightBiased(numberOfUnknownNeighbours, distance, 10);
//                        }
//                        else{
//                            weight = calculateWeightBiased(numberOfUnknownNeighbours, distance, 0);
//                        }
//                    }
//                    else {
//                        weight = 0;
//                    }
//                    //                        System.out.println("It has weight " + weight);
//                    if (weight > maxWeight){
//                        targetCell = currentCell;
//                        maxWeight = weight;
//                    }
//                }
//            }
//        }
//        if(scenario == 3){
//            ArrayList<int[]> neighbours;
//            if(guardLoc == null){
//                guardLoc = lastSeenGuardLoc;
//            }
//            double targetAngle = getAngleInt(guardLoc,new double[] {getCell().getX(),getCell().getY()});
//            if(targetAngle<0){
//                targetAngle = 2*Math.PI+targetAngle;
//            }
//            int numberOfUnknownNeighbours;
//            int distance;
//            double weight;
//            double maxWeight = -1;
//            int x;
//            int y;
//            Grid currentCell;
//
//            // Might need narrowing down
//            if(targetAngle >= 0 && targetAngle < 0.5*Math.PI){
//                lowerBoundAngle = 0;
//                upperBoundAngle = 0.5*Math.PI;
//            }
//            if(targetAngle >= 0.5*Math.PI && targetAngle < Math.PI){
//                lowerBoundAngle = 0.5*Math.PI;
//                upperBoundAngle = Math.PI;
//            }
//            if(targetAngle >= Math.PI && targetAngle < (3/2)*Math.PI){
//                lowerBoundAngle = Math.PI;
//                upperBoundAngle = (3/2)*Math.PI;
//            }
//            if(targetAngle >= (3/2)*Math.PI && targetAngle < 2*Math.PI){
//                lowerBoundAngle = (3/2)*Math.PI;
//                upperBoundAngle = 2*Math.PI;
//            }
//            // looping through the known cells
//            for(int i=0; i<knownCells.size(); i++){
//                numberOfUnknownNeighbours = 0;
//                currentCell = knownCells.get(i);
//                double currentCellAngle = getAngleInt(new double[]{currentCell.getX(), currentCell.getY()},new double[] {getCell().getX(),getCell().getY()});
//                if(currentCellAngle<0){
//                    currentCellAngle = 2*Math.PI+currentCellAngle;
//                }
//                if(currentCellAngle<0){
//                    currentCellAngle = 2*Math.PI+currentCellAngle;
//                }
//                if(!currentCell.hasGuard() && !currentCell.isWall() && !(currentCell.getX() == xRelative && currentCell.getY() == yRelative)){
//                    neighbours = currentCell.getNeighbours();
//                    // for each of known cells we retrieve the list of neighbours that are not walls or don't have guards and loop through them
//                    //                    System.out.println("looking at currently known cell (" + knownCells.get(i).getX() + "; " + knownCells.get(i).getY() + ") - " + (i+1) + " out of " + knownCells.size());
//                    for(int j=0; j<neighbours.size(); j++){
//                        x = neighbours.get(j)[0];
//                        y = neighbours.get(j)[1];
//                        // if a neighbour of a known cell is unknown, we increase the number which will be used in weight calculation
//                        if(!localMap.containsKey(x)){
//                            numberOfUnknownNeighbours++;
//                        }
//                        else if (!localMap.get(x).containsKey(y)){
//                            numberOfUnknownNeighbours++;
//                        }
//                    }
//                    //                    System.out.println("It has " + numberOfUnknownNeighbours + " unknown neighbours");
//                    if(numberOfUnknownNeighbours > -1) {
//                        distance = ManhDistance.compute(new Grid(xRelative, yRelative), currentCell);
//                        if(currentCellAngle < upperBoundAngle&& currentCellAngle > lowerBoundAngle){
//                            weight = calculateWeightBiased(numberOfUnknownNeighbours, distance, -20);
//                        }
//                        else{
//                            weight = calculateWeightBiased(numberOfUnknownNeighbours, distance, 10);
//                        }
//                    }
//                    else { weight = 0;}
//                    //                        System.out.println("It has weight " + weight);
//                    if (weight > maxWeight){
//                        targetCell = currentCell;
//                        maxWeight = weight;
//                    }
//
//                }
//            }
//        }
//        if(scenario == 4){
//            double maxDist = 0;
//            for(Grid i:getCurrentVisibleCells()){
//                if(i.getReferenceTarget() == true){
//                    int dist = Math.abs(i.getX()-getX())+Math.abs(i.getY()-getY());
//                    if(dist > maxDist){
//                        maxDist = dist;
//                        targetCell = i;
//                    }
//                }
//            }
//        }
//        targetGrid = targetCell;
//    }




    public double calculateWeightBiased (int numberNeighbours, double distance, double bias){
        double lambda = 0.01;
        double weight = numberNeighbours * Math.exp(-lambda * distance)+bias;
//        double weight = numberNeighbours;
        return weight;
    }

    public double calculateWeight(double distance){
        double lambda = 0.01;
        return Math.exp(-lambda * distance);
    }

    public double getAngleInt(double[] vector1, double[] vector2){
        //System.out.println(vector2[1] + " "+  vector1[1]+" " + vector1[0] +" "+ vector2[0]);
        return Math.atan2(vector2[1] - vector1[1], vector1[0] - vector2[0]);
        //return Math.acos((vector1[0]*vector2[0])/(Math.sqrt(Math.pow(vector1[1],2)+Math.pow(vector1[0],2))*Math.sqrt(Math.pow(vector2[1],2)+Math.pow(vector2[0],2))));
    }


    public void setCell(Grid cell) {
        positionCell = cell;
        xRelative = positionCell.getX() - xOffset;
        yRelative = positionCell.getY() - yOffset;
    }

    public double[] getDirectionToTarget() {
        int[] targetLocation = getTargetCell();
        //ie. if target is located at (20,20) and current position is (2,4). directionToTarget = (18,16)
        //if
        double xDirection = targetLocation[0] - this.getCell().getX();
        double yDirection = targetLocation[1] - this.getCell().getY();

        double[] directionToTarget = new double[2];
        directionToTarget[0] = xDirection;
        directionToTarget[1] = yDirection;

        //now need to normalize the vector, so agent only has sense of direction, not distance.
        double[] directionToTargetNormalized = new double[2];
        directionToTargetNormalized[0] = directionToTarget[0] / Math.abs(directionToTarget[0] + directionToTarget[1]);
        directionToTargetNormalized[1] = directionToTarget[1] / Math.abs(directionToTarget[0] + directionToTarget[1]);

        return directionToTargetNormalized;
    }

    public boolean isEscaped() {
        return escaped;
    }

    //if it is marked by guard
    public boolean isMarked(){return marked;}

    public void setMarked(boolean m){this.marked = m;}

    public int getMarkedTim(){return markedTim;}

    public void setMarkedTim(int t){markedTim=t;}


    public Grid getTargetGrid(){
        return this.targetGrid;
    }

    public boolean isCaught() {
        return caught;
    }

    public void setCaught(boolean caught) {
        this.caught = caught;
    }
}