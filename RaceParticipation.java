package model;

public class RaceParticipation {
    public int participationId;
    public int raceId;
    public int teamId;
    public Integer position;
    public int points;

    public RaceParticipation(int participationId, int raceId, int teamId, Integer position, int points) {
        this.participationId = participationId;
        this.raceId = raceId;
        this.teamId = teamId;
        this.position = position;
        this.points = points;
    }
}
