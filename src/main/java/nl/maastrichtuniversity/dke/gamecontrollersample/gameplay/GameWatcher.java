package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay;

import nl.maastrichtuniversity.dke.gamecontrollersample.agents.*;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Bresenham;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Exceptions.CollisionAgentException;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Exceptions.CollisionWallException;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Direction;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Exceptions.PathIsEmptyException;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.ManhDistance;
import nl.maastrichtuniversity.dke.gamecontrollersample.tools.CRA;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;


public class GameWatcher {
    private List<Agent> agents = new ArrayList<>();
    private List<Guard> guards;
    private List<Intruder> intruders;
    private List<Intruder> escapedIntruder;
    private List<Intruder> caughtIntruder;
    private boolean explorerGame;
    private boolean randomAgent;
    private Board board;
    private GamePlayer gp;
    private int currentTimestep;
    private final Class algo = null;
    //Example how to set algo
//    private final Class algo = CRA.class;
//    private final Class algo = RandomBeacon.class;

    private final boolean DEBUG = true;

    public GameWatcher(List<Guard> guards, List<Intruder> intruders, boolean explorerGame, boolean randomAgent, Board b, GamePlayer gp) {
        this.guards = guards;
        this.intruders = intruders;
        this.explorerGame = explorerGame;
        this.randomAgent = randomAgent;
        this.board =  b;
        this.gp = gp;
        this.currentTimestep=0;
        this.agents.addAll(intruders);
        this.agents.addAll(guards);
        this.caughtIntruder = new ArrayList<>();
        this.escapedIntruder = new ArrayList<>();

    }
    public GameWatcher(List<Guard> guards, boolean explorerGame,boolean randomAgent, Board b, GamePlayer gp) {
        this.guards = guards;
        this.explorerGame = explorerGame;
        this.randomAgent = randomAgent;
        this.board =  b;
        this.gp = gp;
        this.currentTimestep=0;
        this.agents.addAll(guards);
    }

    public void watchGame(){
        if(explorerGame) {
            Guard.cleanMarkers(currentTimestep);// clean current markers
            for (Guard g : guards) {
                //add the markers
                g.addMarkers(currentTimestep);
                logic(g);
            }
        }
        else{
            try {
                Guard.cleanMarkers(currentTimestep);
                for (Agent agent : agents) {
                    if (agent instanceof Guard guard) {
                        caughtIntruder(guard.getCell());
                        guard.detectAnotherAgent();
                        guard.addMarkers(currentTimestep);
                        logic(guard);

                    } else if (agent instanceof Intruder intruder) {
                        if (!intruder.isEscaped() || !intruder.isCaught()) {
                            intruder.atTargetArea();
                            intruder.detectAnotherAgent();
                            logic(intruder);
                            escaped(intruder);
                        }
                    }
                }
            } catch (ConcurrentModificationException e){
                //Concurrency Error, which doesn't harm the game
            }
        }
        currentTimestep++;
        clearSounds();
    }

    public void clearSounds(){
        Grid[][] bxy = board.getBoardXY();
        for(int i=0;i< bxy.length; i++){
            for(int j=0; j< bxy[0].length; j++){
                bxy[i][j].clearCellSounds();
            }
        }
    }

    private void logic(Agent a){
        int xOffset = a.getxOffset();
        int yOffset = a.getyOffset();

        if(a.getPathToTarget().isEmpty()) {
            a.findNextStep(algo);

            Grid targetCell = a.getTargetGrid();

            if (targetCell != null) {
                a.findShortestPath(targetCell);
            }
        }

        a.setTargetGrid(null);


        if(DEBUG) debug(a);


//        if(a.getPathToTarget().isEmpty()){
//            System.out.println( a + " executes random move");
//            int random = new Random().nextInt(Direction.values().length);
//            List<Direction> path = new ArrayList<>();
//            path.add(Direction.values()[random]);
//            a.setPathToTarget(path);
//        }

        try {
            move(a);
        } catch (CollisionWallException | PathIsEmptyException | CollisionAgentException e) {
//            e.printStackTrace();
        }

        for (Grid grid : a.getCurrentVisibleCells()) {
            board.getGrid(grid.getX() + xOffset, grid.getY() + yOffset).setViewed(true);
        }
    }

    private void move(Agent a) throws CollisionWallException, PathIsEmptyException, CollisionAgentException {
        List<Direction> pathToTarget = a.getPathToTarget();

        if(pathToTarget.isEmpty())
            throw new PathIsEmptyException(a);

        int speed = (int) a.getBaseSpeed();
        int stamina = a.getStamina();

        if (a.isWantRun()) {
            if (stamina != 0) {
                speed = (int) a.getSprintSpeed();
                a.setStamina(stamina - 1);
            } else {
                speed = (int) a.getBaseSpeed();
                a.setStamina(stamina + 1);
            }
        } else {
            a.setStamina(stamina + 1);
        }

        //To update more than one-step change 1 to the speed
        for (int i = 0; i < 1 && !pathToTarget.isEmpty(); i++) {
            Direction direction = pathToTarget.remove(0);
            if (a.getOrientation() != direction.getAngle()) {
                a.updateStep(direction.getAngle(), a.getCell());
            } else {
                Grid nextCell = board.getBoardXY()[a.getCell().getX() + direction.getxRelative()][a.getCell().getY() + direction.getyRelative()];

                //If Random/Sound gives the direction into the Wall, we clear the list, and it can't go there
                if(nextCell.isWall()) {
                    pathToTarget.clear();
                    throw new CollisionWallException();
                } else if(nextCell.hasGuard() || nextCell.hasIntruder()){
                    pathToTarget.clear();
                    throw new CollisionAgentException();
                }

                if (a instanceof Guard) {
                    a.getCell().setReferenceGuard(null);
                    nextCell.setReferenceGuard((Guard) a);
                } else {
                    a.getCell().setReferenceIntruder(null);
                    nextCell.setReferenceIntruder((Intruder) a);
                }

                a.updateStep(direction.getAngle(), nextCell);

                if (a.getCell().isTelePortal())
                    gp.teleport(a);
            }

        }
    }

    private void escaped(Intruder intruder){
        if(intruder.isEscaped()){
            intruder.clear();
            this.intruders.remove(intruder);
            this.agents.remove(intruder);
            this.escapedIntruder.add(intruder);
        }
    }

    private void caughtIntruder(Grid guardPos){
        int RANGE = 2;
        for(Intruder intruder : this.intruders){
            int distanceBetween = ManhDistance.compute(guardPos, intruder.getCell());
            if(distanceBetween <= RANGE) {
                if(!Bresenham.isWallBetweenTiles(board.getBoardXY(), guardPos, intruder.getCell())) {
                    intruder.setCaught(true);
                    intruder.clear();
                    this.intruders.remove(intruder);
                    this.agents.remove(intruder);
                    this.caughtIntruder.add(intruder);
                }
            }
        }
    }

    public int getEscapedIntruderSize() {
        return escapedIntruder.size();
    }

    public int getCaughtIntruderSize() {
        return caughtIntruder.size();
    }

    private void debug(Agent a){
        if(a instanceof Guard) {
//          //      System.out.println("AGENT NUMBER:   " + a.getNumber());
//                System.out.println("True start   " + currentPos.getX() + "   " + currentPos.getY());
//                System.out.println("True target   " + (a.getTargetGrid().getX() + a.getxOffset())  + "   " + (a.getTargetGrid().getY() + a.getyOffset()));
//                System.out.println("Local start guard   " + a.getxRelative() + "   " + a.getyRelative());
////                System.out.println("Local target  " + a.getTargetGrid().getX() + "   " + a.getTargetGrid().getY());
//                 System.out.println("Current orientation " + a.getOrientation());
/*
                 for( int[] i : a.getPathToTarget()){
                     System.out.println(Arrays.toString(i));
                 }
*/
        }
        else {
//                System.out.println("Local Intruder's position   " + currentPos.getX() + "   " + currentPos.getY());

        }
    }
}
