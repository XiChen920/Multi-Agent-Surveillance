package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay;

import nl.maastrichtuniversity.dke.gamecontrollersample.agents.*;

import java.util.ArrayList;

public class Grid {

    private boolean isExplored = false;
    private boolean isViewed = false;
    private boolean isNoise = false;
    //private boolean isInVisionMarkerArea;
    private ArrayList <double[]> listOfYells;
    private ArrayList <double[]> listOfRustles;
    private double targetIntensity;
    private boolean isHeard = false;
    private Guard referenceGuard = null;
    private Intruder referenceIntruder = null;
    private boolean referenceWall = false;
    private TelePortal referenceTelePortal = null;
    private boolean referenceShade = false;
    private boolean referenceTarget = false;
    private boolean referenceGuardSpawn = false;
    private boolean referenceIntruderSpawn = false;
    private int x;
    private int y;
    double bestOrientation;
    //private double visionMarkerIntensity;
    private boolean isBright = false;
    private boolean isPosPheMarker = false;
    private boolean isNegPheMarker = false;
    private boolean isVisionMarkerCorner = false;
    private boolean isVisionMarkerTelep=false;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Grid(int x, int y) {
        this.x = x;
        this.y = y;
        listOfYells = new ArrayList<>();
        listOfRustles = new ArrayList<>();
    }

    public boolean isWall(){return referenceWall;}
    public boolean isTelePortal(){return this.referenceTelePortal != null;}
    public boolean isShade(){return referenceShade;}
    public boolean isTarget(){return referenceTarget;}
    public boolean isGuardSpawn(){return referenceGuardSpawn;}
    public boolean isIntruderSpawn(){return referenceIntruderSpawn;}
    public boolean hasGuard(){return referenceGuard != null;}
    public boolean hasIntruder(){return referenceIntruder != null;}


    public ArrayList<double[]> getListOfYells(){ return listOfYells; }
    public void addYell(double[] params){
        listOfYells.add(params);
    }

    public ArrayList<double[]> getListOfRustles(){ return listOfRustles; }

    public void addTargetSound(double params){
        this.targetIntensity = params;
    }

    public double getTargetIntensity(){
        return this.targetIntensity;
    }

    public void addRustle(double[] params){
        listOfRustles.add(params);
    }

    // clear the information about sounds from the cell
    public void clearCellSounds(){
        listOfRustles = new ArrayList<>();
        listOfYells = new ArrayList<>();
    }

    public TelePortal getReferenceTelePortal(){
        return referenceTelePortal;
    }

    public double getBestOrientation() {
        return this.bestOrientation;
    }

    public void setBestOrientation(double bestOrientation) {
        this.bestOrientation = bestOrientation;
    }

    public boolean isExplored() {
        return isExplored;
    }

    public void setExplored(boolean explored) {
        isExplored = explored;
    }

    public Guard getReferenceGuard() {
        return referenceGuard;
    }

    public void setReferenceGuard(Guard referenceGuard) {
        this.referenceGuard = referenceGuard;
    }

    public Intruder getReferenceIntruder() {
        return referenceIntruder;
    }

    public void setReferenceIntruder(Intruder referenceIntruder) {
        this.referenceIntruder = referenceIntruder;
    }

    public void setReferenceTarget(boolean referenceTarget) {
        this.referenceTarget = referenceTarget;
    }

    public void setReferenceIntruderSpawn(boolean referenceIntruderSpawn) {
        this.referenceIntruderSpawn = referenceIntruderSpawn;
    }

    public void setReferenceGuardSpawn(boolean referenceGuardSpawn) {
        this.referenceGuardSpawn = referenceGuardSpawn;
    }

    public void setReferenceShade(boolean referenceShade) {
        this.referenceShade = referenceShade;
    }

    public void setReferenceTelePortal(TelePortal referenceTelePortal) {
        this.referenceTelePortal = referenceTelePortal;
    }

    public void setReferenceWall(boolean referenceWall) {
        this.referenceWall = referenceWall;
    }

    public Grid getCopy(){
        Grid copyCell = new Grid(this.x, this.y);
        copyCell.setExplored(this.isExplored);
        copyCell.setBestOrientation(this.bestOrientation);
        copyCell.setReferenceGuard(this.referenceGuard);
        copyCell.setReferenceIntruder(this.referenceIntruder);
        copyCell.setReferenceGuardSpawn(this.referenceGuardSpawn);
        copyCell.setReferenceIntruderSpawn(this.referenceIntruderSpawn);
        copyCell.setReferenceShade(this.referenceShade);
        copyCell.setReferenceTarget(this.referenceTarget);
        copyCell.setReferenceWall(this.referenceWall);
        copyCell.setReferenceTelePortal(this.referenceTelePortal);
        copyCell.addTargetSound(this.targetIntensity);
        return copyCell;
    }

    public boolean same(Grid g){
        if(g == null){
            return false;
        }

        return this.x == g.getX() && this.y == g.getY();
    }

    public boolean isViewed() {
        return isViewed;
    }
    public void setViewed(boolean bool){
        this.isViewed = bool;
    }
    public boolean isNoise(){
        return isNoise;
    }
    public void setNoise(boolean bool){
        this.isNoise = bool;
    }
    public boolean isHeard(){
        return isHeard;
    }
    public void setHeard(boolean bool){
        this.isHeard=bool;
    }


    /**
     * Properties of markers
     * TODO mark them in gui
     */
    public boolean isBright(){return isBright;}
    public void setBright(boolean bool){this.isBright=bool;}
    public boolean isVisionMarkerCorner(){return isVisionMarkerCorner;}
    public void setVisionMarkerCorner(boolean bool){this.isVisionMarkerCorner=bool;}
    public boolean isVisionMarkerTelep(){return isVisionMarkerTelep;}
    public void setVisionMarkerTelep(boolean bool){this.isVisionMarkerTelep=bool;}
    public boolean isPosPheMarker(){return isPosPheMarker;}
    public void setPosPheMarker(boolean bool){this.isPosPheMarker=bool;}
    public boolean isNegPheMarker(){return isNegPheMarker;}
    public void setNegPheMarker(boolean bool){this.isNegPheMarker=bool;}




    // vision marker properties
    //public boolean isInVisionMarkerArea() {return isInVisionMarkerArea;}
    //public void setInVisionMarkerArea(boolean bool){this.isInVisionMarkerArea = bool;}
    //public double getVisionMarkerIntensity(){return visionMarkerIntensity;}
    //public void setVisionMarkerIntensity(double intensity){this.visionMarkerIntensity=intensity;}


    public ArrayList<int []> getNeighbours (){
        ArrayList<int []> neighbours = new ArrayList<>();
        int x = this.x;
        int y = this.y;

        neighbours.add(new int [] {x, y-1});
        neighbours.add(new int [] {x, y+1});
        neighbours.add(new int [] {x-1, y});
        neighbours.add(new int [] {x-1, y-1});
        neighbours.add(new int [] {x-1, y+1});
        neighbours.add(new int [] {x+1, y});
        neighbours.add(new int [] {x+1, y-1});
        neighbours.add(new int [] {x+1, y+1});

        return neighbours;
    }


    public boolean getReferenceTarget() {
        return referenceTarget;
    }

    @Override
    public String toString(){
        return "["+this.getX()+","+this.getY()+"]";
    }
}