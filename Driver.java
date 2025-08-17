package model;

import java.sql.Date;

public class Driver {
    public int driverId;
    public String driverName;
    public int skillLevel;
    public Date joinedDate;
    public long rentalPrice;
    public String nationality;
    public int teamId;  // -1 if available

    public Driver() {}

    public Driver(int driverId, String driverName, int skillLevel, Date joinedDate, long rentalPrice, String nationality, int teamId) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.skillLevel = skillLevel;
        this.joinedDate = joinedDate;
        this.rentalPrice = rentalPrice;
        this.nationality = nationality;
        this.teamId = (teamId == 0 ? -1 : teamId);
    }

    public int getExperienceYears() {
        long msPerYear = 1000L * 60 * 60 * 24 * 365;
        long diff = System.currentTimeMillis() - joinedDate.getTime();
        return (int)(diff / msPerYear);
    }

    @Override
    public String toString() {
        return String.format("%d - %s | Skill: %d | Rental Price: %,d | Nationality: %s | Experience: %d years | %s",
                driverId, driverName, skillLevel, rentalPrice, nationality, getExperienceYears(), (teamId == -1 ? "Available" : "Rented"));
    }
}
