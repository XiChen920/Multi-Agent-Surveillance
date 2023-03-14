package nl.maastrichtuniversity.dke.gamecontrollersample.experiments;

public class Results {
    private int gameNumber;
    private int scoreGuard;
    private int scoreIntruder;
    private int timeStepsTaken;

    public Results(int gameNumber, int scoreGuard, int scoreIntruder, int timeStepsTaken) {
        this.gameNumber = gameNumber;
        this.scoreGuard = scoreGuard;
        this.scoreIntruder = scoreIntruder;
        this.timeStepsTaken = timeStepsTaken;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public int getScoreGuard() {
        return scoreGuard;
    }

    public void setScoreGuard(int scoreGuard) {
        this.scoreGuard = scoreGuard;
    }

    public int getScoreIntruder() {
        return scoreIntruder;
    }

    public void setScoreIntruder(int scoreIntruder) {
        this.scoreIntruder = scoreIntruder;
    }

    public int getTimeStepsTaken() {
        return timeStepsTaken;
    }

    public void setTimeStepsTaken(int timeStepsTaken) {
        this.timeStepsTaken = timeStepsTaken;
    }
}
