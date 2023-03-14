/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay;
import nl.maastrichtuniversity.dke.gamecontrollersample.agents.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 *
 * @author joel
 */
public class GamePlayer extends FileWatcher {
    
    protected int time;
    protected Scenario scenario;
    
    // This is just the quick and dirty way to keep the game state what we at 
    // minimum need to run something; no way to do this eventually I'd say
    protected List<Guard> guards;
    protected List<Intruder> intruders;
    //protected Explorer[] explorers;
    protected Board board;
    protected int[] guardStates;
    protected Scenario s;
    private final static Charset ENCODING = StandardCharsets.UTF_8;
    private final Path filePath;
    GameWatcher gw;
    private boolean isExplorerGame;
    
    GamePlayer(Scenario s){
        super(s.getGameFile());
        scenario=s;
        time=0;
        filePath = Paths.get(scenario.getGameFile()); // get path
    }

    public boolean getIsExplorerGame() {
        return isExplorerGame;
    }

    
    public void setup(boolean isRandomAgent){
        // we assume that the game mode is 0. That is all for which there is a
        // minimum implementation
//        scenario.readMap();
        this.board = new Board(scenario.getMapHeight(), scenario.getMapWidth(),1,1);
        for(int i = 0; i < this.board.getBoardXY().length; i++){
            for(int j = 0; j < this.board.getBoardXY()[0].length; j++){
                this.board.getBoardXY()[i][j] = new Grid(i,j);
            }
        }

        scenario.fillBoard(this.board);
        guards = scenario.spawnGuards(this.board, isRandomAgent);
        intruders = scenario.spawnIntruders(this.board, isRandomAgent);
        for(Guard g: guards){
            board.getGrid(g.getX(),g.getY()).setReferenceGuard(g);
            g.setCell(board.getGrid(g.getX(),g.getY()));
        }
        for(Intruder g : intruders){
            board.getGrid(g.getX(),g.getY()).setReferenceIntruder(g);
            g.setCell(board.getGrid(g.getX(),g.getY()));
        }


        //guardStates = new int[scenario.getNumGuards()]; // should be initialized to 0 by default
        //writeGameFile();
    }



    
    public void start(boolean isExploreGame, boolean isRandomAgent){
        this.isExplorerGame = isExploreGame;
        if(intruders.size() > 0){
            this.gw = new GameWatcher(guards,intruders, isExploreGame,isRandomAgent, this.board, this );
        }
        else{
            this.gw = new GameWatcher(guards,isExploreGame,isRandomAgent, this.board, this );
        }

        this.gw.watchGame();

    }

    @Override
    public void onModified(){
    }
    
    protected boolean parseLine(String line){
        return false;
    }


    public Grid relativeToTrueGrid(Agent g, Grid c){
        int nextXtarget = c.getX() + g.getxOffset();
        int nextYtarget = c.getY() + g.getyOffset();
        return board.getBoardXY()[nextXtarget][nextYtarget];
    }
    public int relativeToTrueX(Agent g, int x) {
        return  x + g.getxOffset();
    }

    public int relativeToTrueY(Agent g, int y) {
        return  y + g.getyOffset();
    }

    public Grid relativeArraytoTrueGrid(Agent g, int[] arr){
        int nextXtarget = arr[0] + g.getxOffset();
        int nextYtarget = arr[1] + g.getyOffset();
        return board.getBoardXY()[nextXtarget][nextYtarget];
    }

    public void teleport(Agent g){
        TelePortal tp = g.getCell().getReferenceTelePortal();
        if(g instanceof Guard){
            g.getCell().setReferenceGuard(null);
        }
        else{
            g.getCell().setReferenceIntruder(null);
        }

        g.updateStep(tp.getNewOrientation(), board.getBoardXY()[tp.getNewLocation()[0]][tp.getNewLocation()[1]]);
        if(g instanceof Guard){
            board.getBoardXY()[tp.getNewLocation()[0]][tp.getNewLocation()[1]].setReferenceGuard((Guard) g);
        }
        else{
            board.getBoardXY()[tp.getNewLocation()[0]][tp.getNewLocation()[1]].setReferenceIntruder((Intruder) g);
        }
        g.setTargetGrid(null);
    }

    public boolean explored(Board b){
        Grid[][] board = b.getBoardXY();
        for(int i = 0; i<board.length; i++){
            for(int j = 0; j<board[0].length; j++){
                if(!board[i][j].isExplored())
                    return false;
            }
        }
        return true;
    }

}
