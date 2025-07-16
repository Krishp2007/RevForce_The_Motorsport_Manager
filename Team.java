package com.revForce.team;
import com.revForce.*;
import com.revForce.race.Races;
import com.revForce.sponsorship.Sponsor;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

public class Team {
    private final String teamId; // Unique identifier for the team
    private final String name; // Team name
    private final List<Races.Car> cars; // List of cars owned by the team
    private final List<Driver> drivers; // List of drivers in the team
    private final List<Sponsor> sponsors; // List of sponsors associated with the team
    private int totalPoints; // Total points accumulated in races
    private int championshipsWon; // Number of championships won

    // Constructor
    public Team(String teamId, String name) {
        this.teamId = teamId;
        this.name = name;
        this.cars = new ArrayList<>();
        this.drivers = new ArrayList<>();
        this.sponsors = new ArrayList<>();
        this.totalPoints = 0;
        this.championshipsWon = 0;
    }

    // Getters and Setters
    public String getTeamId() { return teamId; }
    public String getName() { return name; }
    public List<Car> getCars() { return cars; }
    public List<Driver> getDrivers() { return drivers; }
    public List<Sponsor> getSponsors() { return sponsors; }
    public int getTotalPoints() { return totalPoints; }
    public int getChampionshipsWon() { return championshipsWon; }

    // Methods
    public void addCar(Car car) {
        cars.add(car);
    }

    public void addDriver(Driver driver) {
        drivers.add(driver);
    }

    public void addSponsor(Sponsor sponsor) {
        sponsors.add(sponsor);
    }

    public void updatePoints(int points) {
        this.totalPoints += points;
    }

    public void winChampionship() {
        this.championshipsWon++;
    }

    public void displayTeamInfo() {
        System.out.println("Team ID: " + teamId);
        System.out.println("Team Name: " + name);
        System.out.println("Total Points: " + totalPoints);
        System.out.println("Championships Won: " + championshipsWon);
        System.out.println("Drivers: " + drivers);
        System.out.println("Cars: " + cars);
        System.out.println("Sponsors: " + sponsors);
    }
}

