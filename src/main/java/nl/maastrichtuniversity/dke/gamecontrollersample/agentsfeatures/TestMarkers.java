package nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Board;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class TestMarkers {

    /**
     * Check set bright area in vision marker
     */
    @Test
    public void testVisionMarker() {
        //create a 10*10 testing board myself for testing
        Board board = new Board(10,10,1,1);
        for(int i = 0; i < board.getBoardXY().length; i++){
            for(int j = 0; j < board.getBoardXY()[0].length; j++){
                board.getBoardXY()[i][j] = new Grid(i,j);
            }
        }
        Grid[][] testingBoard = board.getBoardXY();

        //create vision marker
        VisionMarker v = new VisionMarker(testingBoard[2][2],board);
        v.setBrightArea();

        //check the bright areas
        for(int i = 0; i < board.getBoardXY().length; i++){
            for(int j = 0; j < board.getBoardXY()[0].length; j++){
                if(testingBoard[i][j].isBright()){
                    System.out.println("x: "+i+" y:"+j);
                }
            }
        }
    }

    /**
     * Test exceeding time-step limit method of phe markers
     */
    @Test
    public void testPheMarker(){
        Board board = new Board(10,10,1,1);
        for(int i = 0; i < board.getBoardXY().length; i++){
            for(int j = 0; j < board.getBoardXY()[0].length; j++){
                board.getBoardXY()[i][j] = new Grid(i,j);
            }
        }
        Grid[][] testingBoard = board.getBoardXY();


        int current = 3;
        // a neg phe marker not exceeded
        NegativePheromoneMarker n=new NegativePheromoneMarker(testingBoard[2][2],1,board);
        System.out.println(n.exceedExistingTime(current));
        System.out.println("start time "+n.getStartTimestep()+" exist "+n.getNegPheExistTim()+ " current "+current);
        System.out.println("Existing Time:"+n.getNegPheExistTim());

        // a pos phe marker exceeded
        current = 10000;
        PositivePheromoneMarker p=new PositivePheromoneMarker(testingBoard[2][2],1,board);
        System.out.println(p.exceedExistingTime(current));
        System.out.println("start time "+p.getStartTimestep()+" exist "+p.getPosPheExistTim()+ " current "+current);
        System.out.println("Existing Time:"+p.getPosPheExistTim());


    }
    /**
     * Test the method of getting orientation mean
     */
    @Test
    public void testGetOrientationMean(){

        //create a 10*10 testing board myself for testing
        Board board = new Board(10,10,1,1);
        for(int i = 0; i < board.getBoardXY().length; i++){
            for(int j = 0; j < board.getBoardXY()[0].length; j++){
                board.getBoardXY()[i][j] = new Grid(i,j);
            }
        }
        Grid[][] testingBoard = board.getBoardXY();
        NegativePheromoneMarker n=new NegativePheromoneMarker(testingBoard[1][2],1,board);
        NegativePheromoneMarker nn=new NegativePheromoneMarker(testingBoard[1][3],1,board);


        ArrayList<Marker> m= new ArrayList<>();
        m.add((Marker)n);
        m.add((Marker)nn);

        ArrayList<Double> orientations;
        Smell s = new Smell(testingBoard[2][2],m);
        orientations=s.getMarkersOrientations();

        if(orientations!=null){
            //calculate the mean
            double sum=0;
            for(int i=0;i<orientations.size();i++){
                sum= sum+orientations.get(i);
            }
            System.out.println(sum/orientations.size());
        }



    }


}
