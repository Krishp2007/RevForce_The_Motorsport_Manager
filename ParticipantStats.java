package model;

public class ParticipantStats {
    public RaceParticipation participation;
    public int driverSkill;
    public int carPower;
    public long teamBudget;
    public double score;

    public ParticipantStats(RaceParticipation participation, int driverSkill, int carPower, long teamBudget) {
        this.participation = participation;
        this.driverSkill = driverSkill;
        this.carPower = carPower;
        this.teamBudget = teamBudget;
    }

    public void setScore(double score) { this.score = score; }
    public double getScore() { return this.score; }
}
