package model;

public class Team {
    public int teamId;
    public String teamName;
    public long budget;
    public String origin;
    public int foundingYear;
    public String email;

    public Team() {}

    public Team(String teamName,  String origin, String email) {
        this.teamName = teamName;
        this.origin = origin;
        this.email = email;
    }

    public Team(int teamId, String teamName, long budget, String origin, int foundingYear, String email) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.budget = budget;
        this.origin = origin;
        this.foundingYear = foundingYear;
        this.email = email;
    }

    public String toString() {
        return String.format("%d - %s | Origin: %s | Budget: %,d | Founded: %d", teamId, teamName, origin, budget, foundingYear);
    }
}
