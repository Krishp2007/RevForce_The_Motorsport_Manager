package model;

public class Standing {
    public int teamId;
    public String teamName;
    public long totalPoints;
    public int wins;

    public Standing(int teamId, String teamName, long totalPoints) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.totalPoints = totalPoints;
    }

    @Override
    public String toString() {
        return String.format("%s (Team ID: %d) - Points: %d, Wins: %d", teamName, teamId, totalPoints, wins);
    }
}
