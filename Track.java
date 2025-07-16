package com.revForce.motorsportmanager.race;

import java.util.Objects;

public class Track {

    private int id;
    private String name;
    private String difficulty;         // "EASY", "MEDIUM", "HARD"
    private int laps;
    private double lengthKm;
    private String country;
    private String city;
    private int yearBuilt;
    private int turns;
    private String weatherType;       // "DRY", "WET", "MIXED"
    private String imagePath;

    // Constructors
    public Track(int id, String name, String difficulty, int laps, double lengthKm, String country, String city, int yearBuilt, int turns, String weatherType, String imagePath) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.laps = laps;
        this.lengthKm = lengthKm;
        this.country = country;
        this.city = city;
        this.yearBuilt = yearBuilt;
        this.turns = turns;
        this.weatherType = weatherType;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getLaps() {
        return laps;
    }

    public double getLengthKm() {
        return lengthKm;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public int getYearBuilt() {
        return yearBuilt;
    }

    public int getTurns() {
        return turns;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public String getImagePath() {
        return imagePath;
    }


    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setLaps(int laps) {
        this.laps = laps;
    }

    public void setLengthKm(double lengthKm) {
        this.lengthKm = lengthKm;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setYearBuilt(int yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    public void setWeatherType(String weatherType) {
        this.weatherType = weatherType;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Utility Methods
    public double getTotalRaceDistance() {
        return lengthKm * laps;
    }

    public boolean isHistoric() {
        return yearBuilt < 1980;
    }

    public String getFormattedTrackSummary() {
        return String.format(
                "%s (%s, %s) - %d laps × %.2f km = %.2f km\nDifficulty: %s | Surface: %s | Weather: %s\nTurns: %d | Elevation: %.1f m\nRecord: %s by %s",
                name, city, country, laps, lengthKm, getTotalRaceDistance(),
                difficulty, weatherType,
                turns
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;
        Track track = (Track) o;
        return id == track.id &&
                laps == track.laps &&
                Double.compare(track.lengthKm, lengthKm) == 0 &&
                yearBuilt == track.yearBuilt &&
                turns == track.turns &&
                Objects.equals(name, track.name) &&
                Objects.equals(difficulty, track.difficulty) &&
                Objects.equals(country, track.country) &&
                Objects.equals(city, track.city) &&
                Objects.equals(weatherType, track.weatherType) &&
                Objects.equals(imagePath, track.imagePath);
    }
}
