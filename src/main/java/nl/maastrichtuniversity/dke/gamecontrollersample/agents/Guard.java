package nl.maastrichtuniversity.dke.gamecontrollersample.agents;

import nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.*;
import nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound.Rustle;
import nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound.Yell;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.BoardHelper;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Direction;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.ManhDistance;
import nl.maastrichtuniversity.dke.gamecontrollersample.tools.CRA;
import nl.maastrichtuniversity.dke.gamecontrollersample.tools.RandomBeacon;

import java.util.*;

public class Guard extends Agent {
    private int quota = 3;
    private Intruder lastIntruder;
    private Grid intruderLastPos;
    private boolean atLastPos;
    private Intruder currentIntruder;
    private boolean intruderSeen = false;
    private boolean guardSeen = false;
    private boolean isChasingMode = false;
    private int counter;
    private Grid lastCell;
    private int maxEvalOld;
    private int minEvalOld;
    public boolean usedMM;

    //Cells sensed by detection sense
    private ArrayList<Grid> sensedCells;

    private ArrayList<double[]> listOfYells;
    private ArrayList<double[]> potentialOrientations;
    protected Yell yell;
    // Markers for guards
    private static ArrayList<PositivePheromoneMarker> posPheMarker= new ArrayList<>();
    private static ArrayList<NegativePheromoneMarker> negPheMarker= new ArrayList<>();
    private static ArrayList<VisionMarker> visionMarkerCorners= new ArrayList<>();
    private static ArrayList<VisionMarker> visionMarkerTelep= new ArrayList<>();

    //Intruders that are marked by guards at present
    private static ArrayList<Intruder> markedIntruders= new ArrayList<>();
    private static int traceMarkerExistingTim = 10;// the track marker will last 10 time steps

    // Upperbound on the markers
    private int posPheLimitation = 40;
    private int negPheLimitation = 10;
    private int visionLimitationCorner = 5;
    private int visionLimitationTelep =5;
    private int negPhePutTs = 2; // put negative pheromone every 2 time steps


    public Guard(int number, Grid cell, double angle, double baseSpeed, double scaling, Board gameBoard, boolean isRandomAgent) {
        super(number, cell, angle, baseSpeed, scaling, gameBoard, isRandomAgent);
        this.lastCell = positionCell;
        this.listOfYells = positionCell.getListOfYells();
        this.potentialOrientations =  new ArrayList<>();
        buildSensorModel();
    }

    // updates the agent's position, orientation and knowledge
    public void updateStep(double angle, Grid cell){
        setOrientation(angle);
        setCell(cell);
        visibleCells = discoverVisibleCells();
        listOfRustles = positionCell.getListOfRustles();
        listOfYells =positionCell.getListOfYells();
        updateKnownCells(visibleCells);
        addKnowledge(visibleCells);
        targetDirection = null;
        produceRustle();
    }

    private boolean flagPrint = true;
    @Override
    public void findNextStep(Object o) {
        usedMM = false;
        if (isChasingMode) {
            if (flagPrint) {
//                System.out.println( this + " is chasing");
                flagPrint = false;
            }
            if (o == null) {
                targetGrid = BoardHelper.getByCoordinates(localMap, intruderLastPos.getX(), intruderLastPos.getY());

                if(targetGrid == null || targetGrid.same(new Grid(xRelative, yRelative))){
//                    System.out.println( this + " is not chasing");
                    this.isChasingMode = false;
                    flagPrint = true;
                    pathToTarget.clear();
                    targetGrid = null;
                    exploreNextStep();
                }

            } else if (o.equals(RandomBeacon.class)) {
                RandomBeacon rb = new RandomBeacon(new int[]{this.getxRelative(), this.getyRelative()}, new int[]{intruderLastPos.getX(), intruderLastPos.getY()}, localMap, 10, 2);
                int[] tr = rb.getTarget();
                targetGrid = localMap.get(tr[0]).get(tr[1]);

            } else if (o.equals(CRA.class)) {
/*
                if( !((Guard)this).isOnLastPos()){
                    int[] arr = ((Guard) this).getIntruderLastPos();
                    targetGrid = localMap.get(arr[0]).get(arr[1]);
                }
                else{
                    setInChase(false);
                }
*/
                if (currentIntruder == null) {
                    if (!isOnLastPos()) {
                        targetGrid = intruderLastPos;
//                            targetGrid = localMap.get(arr[0]).get(arr[1]);
                        System.out.println("ItruderLastPos from findNextStep " + (targetGrid.getX() + this.getxOffset()) + " " + (targetGrid.getY() + this.getyOffset()));
                    } else {
                        isChasingMode = false;
                    }
                } else {
                    CRA cra = new CRA(this, currentIntruder, 0.6);
                    int[] tr = cra.getBestMoveG();
                    targetGrid = localMap.get(tr[0]).get(tr[1]);
                }

            }
            else if(o.equals(Guard.class)){
                startMinimax(13);
                //System.out.println("MINIMAX:");
                usedMM = true;
                System.out.println("AYYYYYYYYYYY:  MINIMAX   "+ targetGrid);
            }


        } else {
            exploreNextStep();
        }
    }

    public boolean isOnLastPos(){
        return intruderLastPos.getX() == this.xRelative && intruderLastPos.getY() == this.yRelative;
    }

    public Intruder searchSensedCells(){
        for (int i = 0; i < sensedCells.size(); i++) {
            if (sensedCells.get(i).hasIntruder() && sensedCells.get(i).getReferenceIntruder().isMarked()){
                return sensedCells.get(i).getReferenceIntruder();
            }
        }
        return null;
    }

    // check if the guard sees an intruder at the moment
    public void detectAnotherAgent(){
        intruderSeen = false;
        guardSeen = false;
        Grid currentCell;
        for(int i=0; i<visibleCells.size(); i++ ){
            currentCell = visibleCells.get(i);
            if(currentCell.hasIntruder()){
                intruderSeen = true;
                setSeenIntruder(currentCell.getReferenceIntruder());
                this.intruderLastPos = currentCell;
            }
            else if(currentCell.hasGuard() && !currentCell.same(new Grid(xRelative, yRelative))){
                guardSeen = true;
                if(!isChasingMode) {
                    rustleCleaner = 15;
                } else {
                    rustleCleaner = 0;
                }
            }
        }
        if(intruderSeen){
            isChasingMode = true;
            produceYell();
            return;
        }
        else{
            if(this.currentIntruder != null){
                if(isOnLastPos()){
                    isChasingMode = false;
                }
                else{
                    isChasingMode = true;
                }
            }
            this.currentIntruder =null;
        }

        if(guardSeen || rustleCleaner > 0){
            listOfRustles.clear();
            rustleCleaner--;
        }
        buildSensorModel();
    }

    public void setSeenIntruder(Intruder i){
        this.lastIntruder = currentIntruder;
        this.currentIntruder = i;
    }

    // returns the target cell the agent should go next
    // the coordinates of this cell are in the local coordinate system
    @Override
    public void exploreNextStep(){
        if(lastCell.same(positionCell)){
            counter++;
        } else {
            counter = 0;
            lastCell = positionCell;
        }
        if(counter >= 10){
            pickRandomTarget();
            counter = 0;
        }
        else if (potentialOrientations.size() > 0){
            double targetOrientation;
            targetOrientation = potentialOrientations.get(findMaxValElement(potentialOrientations))[1];

            Direction direction = calculateDirection(targetOrientation);
            int xCoord = xRelative + direction.getxRelative();
            int yCoord = yRelative + direction.getyRelative();
//            System.out.println("X = " + xCoord + "  Y = " + yCoord);
            targetGrid = BoardHelper.getByCoordinates(localMap, xCoord, yCoord);

            if(targetGrid == null || targetGrid.isWall() || targetGrid.hasGuard()){
                calculateFrontier();
            }
        } else {
            calculateFrontier();
        }
    }

    public void calculateFrontier(){
        ArrayList<int[]> neighbours;
        int numberOfUnknownNeighbours;
        int distance;
        double weight;
        double maxWeight = -1;
        int x;
        int y;
        Grid currentCell;
        // looping through the known cells
        for (int i = 0; i < knownCells.size(); i++) {
            numberOfUnknownNeighbours = 0;
            currentCell = knownCells.get(i);
            if (!currentCell.isWall() && !(currentCell.getX() == xRelative && currentCell.getY() == yRelative) && !currentCell.isTelePortal()) {
                neighbours = currentCell.getNeighbours();
                // for each of known cells we retrieve the list of neighbours that are not walls or don't have guards and loop through them
                for (int j = 0; j < neighbours.size(); j++) {
                    x = neighbours.get(j)[0];
                    y = neighbours.get(j)[1];
                    // if a neighbour of a known cell is unknown, we increase the number which will be used in weight calculation
                    if (!localMap.containsKey(x)) {
                        numberOfUnknownNeighbours++;
                    } else if (!localMap.get(x).containsKey(y)) {
                        numberOfUnknownNeighbours++;
                    }
                }
                if (numberOfUnknownNeighbours > 0) {
                    distance = ManhDistance.compute(new Grid(xRelative, yRelative), currentCell);
                    weight = calculateWeight(numberOfUnknownNeighbours, distance);
                } else {
                    weight = 0;
                }
                if (weight > maxWeight) {
                    targetGrid = currentCell;
                    maxWeight = weight;
                }
            }
        }
    }

    // calculates the weight for each potential target cell that will be taking into account for a decision
    public double calculateWeight (int numberNeighbours, double distance){
        double lambda = 0.01;
        double weight = numberNeighbours * Math.exp(-lambda * distance);
//        double weight = numberNeighbours;
        return weight;
    }
    // updates agent's position
    public void setCell(Grid cell) {
        positionCell = cell;
        xRelative = positionCell.getX() - xOffset;
        yRelative = positionCell.getY() - yOffset;
    }

    public void buildSensorModel(){
        potentialOrientations = new ArrayList<>();
        double scoreSound = 1.0;
        double scoreNegPheromone = 0.3;
        double scorePosPheromone = 0.1;
        if (isChasingMode){
            scoreSound = 0.1;
            scoreNegPheromone = 0.7;
            scorePosPheromone = 0.2;
        }
        if (selectSoundOrientation() != -1){
            potentialOrientations.add(new double[]{scoreSound,selectSoundOrientation()});
        }
        if (selectPosPheOrientation() != -1){
            potentialOrientations.add(new double[]{scorePosPheromone,selectPosPheOrientation()});
        }
        if (selectNegPheOrientation() != -1){
            potentialOrientations.add(new double[]{scoreNegPheromone,selectNegPheOrientation()});
        }

    }

    // select most valuable sound orientation
    public double selectSoundOrientation(){
        final double k = 0.6; // coefficient
        double[] maxAmplYell;
        double[] maxAmplRustle;

        int yellIndex = findMaxValElement(listOfYells);
        int rustleIndex = findMaxValElement(listOfRustles);
        if (yellIndex != -1 && rustleIndex != -1 ) {
            maxAmplYell = listOfYells.get(yellIndex);
            maxAmplRustle = listOfRustles.get(rustleIndex);
            if ((k * (Yell.getIntensity() / Rustle.getIntensity()) * (maxAmplRustle[0] / maxAmplYell[0])) >= 1) {
                return maxAmplRustle[1];
            }
            else{
                return maxAmplYell[1];
            }

        }
        else if (yellIndex == -1 && rustleIndex != -1){
            maxAmplRustle = listOfRustles.get(rustleIndex);
            return maxAmplRustle[1];
        }
        else if (yellIndex != -1){
            maxAmplYell = listOfYells.get(yellIndex);
            return maxAmplYell[1];
        }
        else {
            return -1;
        }
    }

    // find the sound with max amplitude
    public int findMaxValElement(ArrayList<double[]> elements){
        double max = -1;
        double current;
        int index = -1;
        for (int i=0; i<elements.size(); i++){
            current = elements.get(i)[0];
            if(current > max){
                max = current;
                index = i;
            }
        }
        return index;
    }

    public void produceYell(){
        yell = new Yell(positionCell, board);
//        System.out.println("The guard is yelling");
    }

    public Yell getYell(){
        return yell;
    }
    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public Intruder getCurrentIntruder(){
        return this.currentIntruder;
    }

    /**
     * Check whether the marker number exceed the limits
     * @param type  1 = Positive Pheromone Marker
     *              2 = Negative Pheromone Maker
     *              3 = Vision Marker corner
     *              4 = Vision Marker teleport
     * @return true if the marker number is out of the upperbound
     *         false if there's fair number of markers
     */
    public boolean exceedMarkerLimits(int type){
        if(type==1){
            return (posPheMarker.size()>posPheLimitation);
        }
        else if(type==2){
            return(negPheMarker.size()>negPheLimitation);
        }
        else if(type==3){
            return(visionMarkerCorners.size()>visionLimitationCorner);
        }
        else{
            return (visionMarkerTelep.size()>visionLimitationTelep);
        }
    }


    /**
     * Methods to add guard markers
     */
    public void addPosPheMarker(Grid markerGrid, int startTimestep){
        if((!exceedMarkerLimits(1))&&(!markerGrid.isPosPheMarker())){
            PositivePheromoneMarker m = new PositivePheromoneMarker(markerGrid,startTimestep,board);
            posPheMarker.add(m);
            markerGrid.setPosPheMarker(true);
        }
    }
    public void addNegPheMarker(Grid markerGrid,int startTimestep){
        if((!exceedMarkerLimits(2))&&(!markerGrid.isNegPheMarker())){
            NegativePheromoneMarker m = new NegativePheromoneMarker(markerGrid,startTimestep,board);
            negPheMarker.add(m);
            markerGrid.setNegPheMarker(true);
        }
    }
    public void addVisionMarkerCorner(Grid markerGrid){
        if((!exceedMarkerLimits(3))&&(!markerGrid.isVisionMarkerCorner())){
            VisionMarker m = new VisionMarker(markerGrid,board);
            m.setBrightArea();
            visionMarkerCorners.add(m);
            markerGrid.setVisionMarkerCorner(true);
        }
    }

    public void addVisionMarkerTeleport(Grid markerGrid){
        if((!exceedMarkerLimits(4))&&(!markerGrid.isVisionMarkerTelep())&&(!markerGrid.isBright())){
            VisionMarker m = new VisionMarker(markerGrid,board);

            m.setBrightArea();
            visionMarkerTelep.add(m);
            markerGrid.setVisionMarkerTelep(true);
        }
    }

    /**
     * Method to clean the markers that exceeded existing time
     */
    public static void cleanMarkers(int currentTimestep){
        if(posPheMarker.size()!=0){
            for(int i =posPheMarker.size()-1;i>=0;i--){
                if(posPheMarker.get(i).exceedExistingTime(currentTimestep)){
                    posPheMarker.get(i).getMarkerGrid().setPosPheMarker(false);
                    posPheMarker.remove(i);
                }
            }
        }

        if(negPheMarker.size()!=0){
            for(int i =negPheMarker.size()-1;i>=0;i--){
                if(negPheMarker.get(i).exceedExistingTime(currentTimestep)){
                    negPheMarker.get(i).getMarkerGrid().setNegPheMarker(false);
                    negPheMarker.remove(i);
                }
            }
        }
        if(markedIntruders.size()!=0){
            if(currentTimestep==0){
                markedIntruders.clear();
            }
            for(int i = markedIntruders.size()-1;i>=0;i--){
                //check whether the track marker exceed time limit
                int startTim = markedIntruders.get(i).getMarkedTim();
                int tim = currentTimestep - startTim;
                if(tim >traceMarkerExistingTim){//existing 10 time step for now
//                    System.out.println("remove trace marker, existing tim "+tim+ " current time:"+currentTimestep);
                    markedIntruders.get(i).setMarkedTim(-1);
                    markedIntruders.get(i).setMarked(false);
                    markedIntruders.remove(i);
//                    System.out.println("Current trace marker num: "+markedIntruders.size());
                }
            }
        }

    }

    /**
     * Rules for the guard to put the marker
     */
    public void addMarkers(int currentTimestep){

        //Place the negative pheromone marker every 2 time step
        if(currentTimestep % negPhePutTs==0){
            addNegPheMarker(positionCell,currentTimestep);

        }
        //Place the positive pheromone the time sees an intruder
        //after 20 ts
        if(lastIntruder==null && currentIntruder !=null){
            if(currentTimestep>=20){
                addPosPheMarker(positionCell,currentTimestep);
            }
        }
        //place the vision marker for corner
        if(isCorner(positionCell.getX(),positionCell.getY())){
            addVisionMarkerCorner(positionCell);

        }
        //place the vision marker for teleport
        if(isTele(positionCell.getX(),positionCell.getY())){
            addVisionMarkerTeleport(positionCell);
        }

        if(this.currentIntruder !=null){//if there's intruder in vision
            if(!markedIntruders.contains(this.currentIntruder)){//if this intruder has NOT been marked
                markedIntruders.add(this.currentIntruder);
//                System.out.println("trace marker set");
            }
            this.currentIntruder.setMarked(true);
            this.currentIntruder.setMarkedTim(currentTimestep);// record the start time
//            System.out.println("trace marker start tim reset to:"+currentTimestep);
//            System.out.println("Current trace marker num: "+markedIntruders.size());
        }
    }


    // select most valuable positive phe marker orientation
    public double selectPosPheOrientation(){
        //if the marker array's empty, return -1
        if(posPheMarker.size()==0){
            return -1;
        }

        //convert to marker arraylist
        ArrayList<Marker> m= new ArrayList<>();
        for(int i=0;i<posPheMarker.size();i++){
            m.add((Marker)posPheMarker.get(i));
        }

        return(getPheOrientationMean(m));
    }


    // select most valuable negative phe marker orientation
    public double selectNegPheOrientation(){
        //if the marker array's empty, return -1
        if(negPheMarker.size()==0){
            return -1;
        }

        //convert to marker arraylist
        ArrayList<Marker> m= new ArrayList<>();
        for(int i=0;i<negPheMarker.size();i++){
            m.add((Marker)negPheMarker.get(i));
        }
        if(getPheOrientationMean(m)==-1){
            return -1;
        }

        //become negative degree
        double degree = getPheOrientationMean(m) +180;
        //convert to 0-360
        if(degree>360){
            degree=degree-360;
        }

        return(degree);
    }



    /**
     *  return the mean of all the phe marker detected
     */
    public double getPheOrientationMean(ArrayList<Marker> pheMarker){
        ArrayList<Double> orientations;
        Smell s = new Smell(positionCell,pheMarker);
        orientations=s.getMarkersOrientations();

        if(orientations.size() == 0){
            return -1;
        }
        //calculate the mean
        double sum=0;
        for(int i=0;i<orientations.size();i++){
            sum= sum+orientations.get(i);
        }
        return(sum/orientations.size());
    }


    public ArrayList<int[]> guardMoves = new ArrayList<int[]>();
    public ArrayList<int[]> intruderMoves = new ArrayList<int[]>();

    //@Override
    public void startMinimax(int depth) {
        //System.out.println("STARTED MINIMAX GUARD SEEN YOO");
        guardMoves.clear();
        intruderMoves.clear();
        int[] currentPosition = new int[] { this.getCell().getX(), this.getCell().getY() };
        maxEvalOld = Integer.MAX_VALUE;
        minEvalOld = Integer.MIN_VALUE;
        minimax(currentPosition, depth, Integer.MAX_VALUE, Integer.MIN_VALUE, true);
        Collections.reverse(guardMoves);
        // System.out.println("GUARDS MOVES" + guardMoves);
        // System.out.println(guardMoves.get(0)[0] + " " + guardMoves.get(0)[1] + " SIZE
        // GUARDMOVES");
        // this.targetGrid = localMap.get(guardMoves.get(guardMoves.
        // size()-1)[0]).get(guardMoves.get(guardMoves. size()-1)[1]);
       // System.out.println("Target X = " + (guardMoves.get(0)[0] - xOffset) + ", Target Y = " + (guardMoves.get(0)[1] - yOffset));
       // System.out.println("CRASH");
        this.targetGrid = new Grid(guardMoves.get(0)[0] - xOffset, guardMoves.get(0)[1] - yOffset);
       // System.out.println("CRASH???");
        // System.out.println("X = " + targetGrid.getX() + " Y = " + targetGrid.getY());
    }

    // maximising player is guard trying to minimize distance
    public int minimax(int position[], int depth, int alpha, int beta, boolean maximizingPlayer) {
        int[] optimalChild = new int[2];
        int[] optimalChildIntruder = new int[2];
        ArrayList<int[]> knownReachable = new ArrayList<int[]>();
        knownReachable = getChildren(position, maximizingPlayer);
        if (depth == 0) {
            if (maximizingPlayer) {
                int[] lastT;
                if (intruderMoves.size() != 0) {
                    lastT = intruderMoves.get((intruderMoves.size() - 1));
                } else {
                    lastT = new int[] { currentIntruder.getCell().getX(), currentIntruder.getCell().getY() };

                }
                return getDistance(position[0], position[1], lastT[0], lastT[1]);
            } else {
                int[] lastT;
                if (guardMoves.size() != 0) {
                    lastT = guardMoves.get((guardMoves.size() - 1));
                } else {
                    lastT = new int[] { this.getX(), this.getY() };

                }
                return getDistance(position[0], position[1], lastT[0], lastT[1]);
            }

        }

        if (maximizingPlayer) {
            int maxEval = Integer.MAX_VALUE;
            for (int[] child : knownReachable) {
                int eval = minimax(child, depth - 1, alpha, beta, false);
                maxEval = Math.min(maxEval, eval);
                if (maxEval < maxEvalOld) {
                    optimalChild = child;
                    maxEvalOld = maxEval;
                }
                alpha = Math.min(alpha, eval);
                if (beta >= alpha) {
                    break;
                }
                guardMoves.add(optimalChild);
            }
            return maxEval;
        } else {
            //System.out.println("TEST222");
            int minEval = Integer.MIN_VALUE;
            for (int[] child : knownReachable) {
                int eval = minimax(child, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                if (minEval < minEvalOld) {
                  //  System.out.println("TEST 2");
                    optimalChildIntruder = child;
                    minEvalOld = minEval;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
                intruderMoves.add(optimalChildIntruder);
            }
            return minEval;
        }
    }

    public ArrayList<int[]> getChildren(int[] position, boolean maximizingPlayer) {

        int h = 80;
        int w = 120;
    //    System.out.println("Pos 0: " + position[0] + " Pos 1: " + position[1]);
        ArrayList<int[]> knownReachable = new ArrayList<int[]>();
        int i = 1;
        int j = 1 ;

        if (knowledgeContains(position[0] - xOffset, position[1] +1 - yOffset)) {

            knownReachable.add(new int[] { (position[0]), (position[1] + 1) });
        }
        if (knowledgeContains(position[0] - xOffset, position[1] -1 - yOffset)) {
            knownReachable.add(new int[] { (position[0]), (position[1] - 1) });
        }
        if (knowledgeContains(position[0] +1 - xOffset, position[1] - yOffset)) {
            knownReachable.add(new int[] { (position[0] + 1), (position[1]) });
        }
        if (knowledgeContains(position[0] - 1 - xOffset, position[1] - yOffset)) {
            knownReachable.add(new int[] { (position[0] - 1), (position[1]) });
        }

        /*
         * knownReachable.add(new int[] { (position[0] ), (position[1] + i) });
         * knownReachable.add(new int[] { (position[0]), (position[1] - i)});
         * knownReachable.add(new int[] { (position[0] + i), (position[1])});
         * knownReachable.add(new int[] { (position[0] - i), (position[1]) });
         */
        for (int k = 0; k < knownReachable.size(); k++) {
         //   System.out.println("known = " + knownReachable.get(k)[0] + ", " + knownReachable.get(k)[1]);
        }
       // System.out.println("X Relative = " + yRelative);
      //  System.out.println("Y Relative = " + xRelative);

      //  System.out.println("COMPLETED KNOWN CHILD");

        return knownReachable;
    }

    // calculates the manhattan distance between 2 cells
    public int getDistance(int xRelative, int yRelative, int x, int y) {
        int distance = Math.abs(xRelative - x) + Math.abs(yRelative - y);
        return distance;
    }

    public boolean knowledgeContains(int x, int y) {
        if (localMap.containsKey(x)) {
            if (localMap.get(x).containsKey(y)) {
              //  System.out.println("HERE");
                return true;
            }
        }
        return false;
    }
}