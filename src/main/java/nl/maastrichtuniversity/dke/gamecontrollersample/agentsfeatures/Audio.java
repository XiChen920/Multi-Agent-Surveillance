package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.util.ArrayList;

public class Audio {
    private int hearingRadius = 3;
    private Grid agentPosition;
    private Board board;
    private ArrayList<Grid> heardGrids;
    public Audio(Grid agentPosition, Board b){
        this.agentPosition = agentPosition;
        this.board = b;
    }
    public void createHearingArea(){
        heardGrids = new ArrayList<>();
        for(int i=agentPosition.getX()-hearingRadius; i<(agentPosition.getX()+hearingRadius+1); i++){
            for(int j=agentPosition.getY()-hearingRadius; j<(agentPosition.getY()+hearingRadius+1); j++){
                if(i>=0 && j>=0 && i<board.getBoardXY().length && j< board.getBoardXY()[0].length && !(i==agentPosition.getX() && j==agentPosition.getY())) {
                    board.getGrid(i, j).setHeard(true);
                    heardGrids.add(board.getGrid(i,j));
                }
            }
        }
    }

    //here you can change it around to return the direction of the noise to move towards it or away from it
    public boolean detectNoise(){
        for(Grid grid : heardGrids){
            if(grid.isNoise()){
//                System.out.println("NOISE HEARD");
                return true;
            }
        }
        return false;
    }

    public void resetNoiseGrid(){
        heardGrids.clear();
    }

}
