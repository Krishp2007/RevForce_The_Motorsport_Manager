package model;

public class RaceParticipation {
    public int participationId;
    public int raceId;
    public int teamId;

    public RaceParticipation(int participationId, int raceId, int teamId) {
        this.participationId = participationId;
        this.raceId = raceId;
        this.teamId = teamId;
    }
}
