package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures;

import nl.maastrichtuniversity.dke.gamecontrollersample.agents.Guard;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class TestSence {
    /**
     * Test the method of getting the orientations of different phe-based markers
     */
    @Test
    public void testSmell(){
        //create a 10*10 testing board myself for testing
        Board board = new Board(10,10,1,1);
        for(int i = 0; i < board.getBoardXY().length; i++){
            for(int j = 0; j < board.getBoardXY()[0].length; j++){
                board.getBoardXY()[i][j] = new Grid(i,j);
            }
        }
        Grid[][] testingBoard = board.getBoardXY();
        NegativePheromoneMarker n=new NegativePheromoneMarker(testingBoard[1][2],1,board);
        PositivePheromoneMarker p=new PositivePheromoneMarker(testingBoard[2][1],1,board);

        ArrayList<Marker> m= new ArrayList<>();
        m.add((Marker)n);
        m.add((Marker)p);
        Smell s = new Smell(testingBoard[2][2],m);
        System.out.println(s.getMarkersOrientations());

    }
    /**
     * Test vision
     */
    @Test
    public void testVision(){
        //create a 10*10 testing board myself for testing
        Board board = new Board(10,10,1,1);
        for(int i = 0; i < board.getBoardXY().length; i++){
            for(int j = 0; j < board.getBoardXY()[0].length; j++){
                board.getBoardXY()[i][j] = new Grid(i,j);
            }
        }
        Grid[][] testingBoard = board.getBoardXY();

        //set the wall
        testingBoard[0][2].setReferenceWall(true);
        testingBoard[2][2].setReferenceWall(true);
        testingBoard[1][1].setReferenceShade(true);


        //create vision object
        double viewingOrientation = 0;
        Vision v = new Vision(testingBoard[4][4],viewingOrientation,testingBoard);
        ArrayList<Grid> testArea=v.getViewingArea();

        System.out.println("Grids can be seen");
        for(int i=0;i<testArea.size();i++){
            System.out.println(i +":  "+ "x:"+testArea.get(i).getX()+"    y:"+testArea.get(i).getY() + "  wall:" +testArea.get(i).isWall());

        }
    }


}
