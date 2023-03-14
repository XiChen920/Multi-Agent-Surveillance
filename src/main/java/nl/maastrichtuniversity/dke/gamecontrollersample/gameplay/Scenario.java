/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay;

import nl.maastrichtuniversity.dke.gamecontrollersample.agents.*;
import nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound.TargetSound;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Direction;

import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author joel
 */
public class Scenario {
    
    protected double baseSpeedIntruder;
    protected double sprintSpeedIntruder;
    protected double baseSpeedGuard;
    
    protected String mapDoc;
    protected int gameMode;
    private final Path filePath;
    private final static Charset ENCODING = StandardCharsets.UTF_8;
    
    protected String name;
    protected String gameFile;
    protected int mapHeight;
    protected int mapWidth;
    protected double scaling;
    protected int numIntruders;
    protected int numGuards;
    protected Area spawnAreaIntruders;
    protected Area spawnAreaGuards;
    protected Area targetArea;
    protected ArrayList<Area> walls;
    protected ArrayList<TelePortal> teleports;
    protected ArrayList<Area> shaded;
    
    public Scenario(String mapFile){
        // set parameters
        mapDoc=mapFile;
                
        // initialize variables
         walls = new ArrayList<>(); // create list of walls
         shaded = new ArrayList<>(); // create list of low-visability areas
         teleports = new ArrayList<>(); // create list of teleports e.g. stairs
        
        // read scenario
        filePath = Paths.get(mapDoc); // get path
        System.out.println(filePath);
        readMap();
    }
    
    public void readMap(){ 
        try (Scanner scanner =  new Scanner(filePath, ENCODING.name())){
            while (scanner.hasNextLine()){
                parseLine(scanner.nextLine());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /*
        
     */    
    protected void parseLine(String line){
        //use a second Scanner to parse the content of each line 
        String[] split  = line.split("=");
        String id = split[0].trim();
        String value = split[1].trim();
        value = value.split("//")[0].trim();
        String[] items = value.split(" ");

        Area tmp;
        switch(id)
        {
            case "name":
                name = value;
                break;
            case "gameFile":
                gameFile = value;
                break;
            case "gameMode":
                gameMode = Integer.parseInt(value); // 0 is exploration, 1 evasion pursuit game
                break;
            case "scaling":
                scaling = Double.parseDouble(value);
                break;
            case "height":
                mapHeight = Integer.parseInt(value);
                break;
            case "width":
                mapWidth = Integer.parseInt(value);
                break;
            case "numGuards":
                numGuards = Integer.parseInt(value);
                break;
            case "numIntruders":
                numIntruders = Integer.parseInt(value);
                break;
            case "baseSpeedIntruder":
                baseSpeedIntruder = Double.parseDouble(value);
                break;
            case "sprintSpeedIntruder":
                sprintSpeedIntruder = Double.parseDouble(value);
                break;
            case "baseSpeedGuard":
                baseSpeedGuard = Double.parseDouble(value);
                break;
            case "targetArea":
                targetArea = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                break;
            case "spawnAreaIntruders":
                spawnAreaIntruders = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                break;
            case "spawnAreaGuards":
                spawnAreaGuards = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                break;
            case "wall":
                tmp = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                walls.add(tmp);
                break;
            case "shaded":
                tmp = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                shaded.add(tmp);
                break;
            case "teleport":
                TelePortal teletmp = new TelePortal(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]),Integer.parseInt(items[4]),Integer.parseInt(items[5]),Double.parseDouble(items[6]));
                teleports.add(teletmp);
                break;
            case "texture":
                // still to do. First the coordinates, then an int with texture type and then a double with orientation
        }

    }  
    
    public ArrayList<Area> getWalls(){
        return walls;
    }

    public boolean inWall(double x, double y){
        boolean tmp = false;
        for(int j=0;j<walls.size();j++){
            if(walls.get(j).isHit(x,y)){
                tmp=true;
            }
        }
        return(tmp);
    }
    
    public ArrayList<Area> getShaded(){
        return shaded;
    }
    
    public ArrayList<TelePortal> getTeleportals(){
        return teleports;
    }
    
    public Area getTargetArea(){
        return targetArea;
    }
    
    public List<Guard> spawnGuards(Board board, boolean isRandomAgent){
        int[][] tmp = new int[numGuards][4];
        double dx=spawnAreaGuards.rightBoundary-spawnAreaGuards.leftBoundary;
        double dy=spawnAreaGuards.topBoundary-spawnAreaGuards.bottomBoundary;
        List<Guard> guards = new ArrayList<>();
        for(int i=0; i<numGuards; i++){
            int random = new Random().nextInt(Direction.values().length);
            tmp[i][0]= (int) (spawnAreaGuards.leftBoundary+Math.random()*dx);
            tmp[i][1]= (int) (spawnAreaGuards.bottomBoundary+Math.random()*dy);
            tmp[i][2]= (int) Direction.values()[random].getAngle();
            guards.add(new Guard(i, board.getBoardXY()[tmp[i][0]][tmp[i][1]],tmp[i][2], baseSpeedGuard, scaling, board, isRandomAgent));
        }
        return guards;
    }

    public List<Intruder> spawnIntruders(Board board, boolean isRandomAgent){
        int[][] tmp = new int[numIntruders][4];
        double dx=spawnAreaIntruders.rightBoundary-spawnAreaIntruders.leftBoundary;
        double dy=spawnAreaIntruders.topBoundary-spawnAreaIntruders.bottomBoundary;
        List<Intruder> intruders = new ArrayList<>();
        for(int i=0; i<numIntruders; i++){
            int random = new Random().nextInt(Direction.values().length);
            tmp[i][0]= (int) (spawnAreaIntruders.leftBoundary+Math.random()*dx);
            tmp[i][1]= (int) (spawnAreaIntruders.bottomBoundary+Math.random()*dy);
            tmp[i][2]= (int) Direction.values()[random].getAngle();
            intruders.add(new Intruder(i, board.getBoardXY()[tmp[i][0]][tmp[i][1]],tmp[i][2], baseSpeedIntruder, sprintSpeedIntruder, scaling, board, isRandomAgent));
        }
        return intruders;
    }
    
    public int getNumGuards(){
        return numGuards;
    }
    
    public String getGameFile(){
        return gameFile;
    }

    public String getMapDoc(){
        return mapDoc;
    }
    
    public double getScaling(){
        return scaling;
    }

    public int[] teleportPosition(double x, double y){
        for(int j=0;j<teleports.size();j++){
            if(teleports.get(j).isHit(x,y)){
                //TO DO if we are going to have stairs where it changes the level of the game (like changing to a new floor or something)

                return teleports.get(j).getNewLocation();
            }
        }
        System.out.println("No teleports :p");
        return null;
    }

    public double teleportOrientation(double x, double y){
        for(int j=0;j<teleports.size();j++){
            if(teleports.get(j).isHit(x,y)){
                //TO DO if we are going to have stairs where it changes the level of the game (like changing to a new floor or something)

                return teleports.get(j).getNewOrientation();
            }
        }
        System.out.println("No teleports :p");
        return 0;
    }

    public double getBaseSpeedIntruder() {
        return baseSpeedIntruder;
    }

    public void setBaseSpeedIntruder(double baseSpeedIntruder) {
        this.baseSpeedIntruder = baseSpeedIntruder;
    }

    public double getSprintSpeedIntruder() {
        return sprintSpeedIntruder;
    }

    public void setSprintSpeedIntruder(double sprintSpeedIntruder) {
        this.sprintSpeedIntruder = sprintSpeedIntruder;
    }

    public double getBaseSpeedGuard() {
        return baseSpeedGuard;
    }

    public void setBaseSpeedGuard(double baseSpeedGuard) {
        this.baseSpeedGuard = baseSpeedGuard;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGameFile(String gameFile) {
        this.gameFile = gameFile;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void setScaling(double scaling) {
        this.scaling = scaling;
    }

    public int getNumIntruders() {
        return numIntruders;
    }

    public void setNumIntruders(int numIntruders) {
        this.numIntruders = numIntruders;
    }

    public void setNumGuards(int numGuards) {
        this.numGuards = numGuards;
    }

    public Area getSpawnAreaIntruders() {
        return spawnAreaIntruders;
    }

    public void setSpawnAreaIntruders(Area spawnAreaIntruders) {
        this.spawnAreaIntruders = spawnAreaIntruders;
    }

    public Area getSpawnAreaGuards() {
        return spawnAreaGuards;
    }

    public void setSpawnAreaGuards(Area spawnAreaGuards) {
        this.spawnAreaGuards = spawnAreaGuards;
    }

    public void setTargetArea(Area targetArea) {
        this.targetArea = targetArea;
    }

    public void setWalls(ArrayList<Area> walls) {
        this.walls = walls;
    }

    public ArrayList<TelePortal> getTeleports() {
        return teleports;
    }

    public void setTeleports(ArrayList<TelePortal> teleports) {
        this.teleports = teleports;
    }

    public void setShaded(ArrayList<Area> shaded) {
        this.shaded = shaded;
    }

    public void fillBoard(Board b){
        TargetSound targetSound = new TargetSound(targetArea.leftBoundary, targetArea.topBoundary);
        for(Grid[] gg : b.getBoardXY()){
            int x;
            int y;
            for(Grid g :gg){
                x = g.getX();
                y = g.getY();
                if(spawnAreaGuards.isHit(x,y)) {
                    g.setReferenceGuardSpawn(true);
                }
                if(spawnAreaIntruders.isHit(x,y)) {
                    g.setReferenceIntruderSpawn(true);
                }
                if(targetArea.isHit(x,y)) {
                    g.setReferenceTarget(true);
                }
                for(Area w: walls){
                    if(w.isHit(x,y)) {
                        g.setReferenceWall(true);
                    }
                }
                for(TelePortal t: teleports){
                    if(t.isHit(x,y)) {
                        g.setReferenceTelePortal(t);
                    }
                }
                for(Area s : shaded){
                    if(s.isHit(x,y)) {
                        g.setReferenceShade(true);
                    }
                }
                targetSound.markSound(g);
            }
        }
        fillInaccessibleCells(b);
    }

    public void fillInaccessibleCells(Board b) {
        int x;
        int y;
        ArrayList<int[]> neighbours;
        Grid currentNeighbour;
        boolean accessible;

        for (Grid[] gg : b.getBoardXY()) {
            for (Grid g : gg) {
                accessible = false;
                neighbours = g.getNeighbours();
                for (int j = 0; j < neighbours.size(); j++) {
                    x = neighbours.get(j)[0];
                    y = neighbours.get(j)[1];
//                    System.out.println("x " + x + " " + "y " + y);
                    if ((x >= 0 && x < b.getWidth()) && (y >= 0 && y < b.getHeight())) {
                        currentNeighbour = b.getBoardXY()[x][y];
                        if (!currentNeighbour.isWall()) {
                            accessible = true;
//                            System.out.println("accessible " + g.getX() + " " + g.getY());
                        }
                    }
                }
                if (!accessible) {
//                    System.out.println("inaccessible " + g.getX() + " " + g.getY());
                    g.setExplored(true);
                }
            }
        }
    }
}
