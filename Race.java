package model;

import java.sql.Date;
import java.sql.Time;

public class Race {
    public int raceId;
    public int trackId;
    public Date raceDate;
    public Time raceTime;
    public int laps;

    public Race() {}

    public Race(int raceId, int trackId, Date raceDate, Time raceTime, int laps) {
        this.raceId = raceId;
        this.trackId = trackId;
        this.raceDate = raceDate;
        this.raceTime = raceTime;
        this.laps = laps;
    }

    public int getRaceId() {
        return raceId;
    }

    public int getTrackId() {
        return trackId;
    }

    public Date getRaceDate() {
        return raceDate;
    }

    public Time getRaceTime() {
        return raceTime;
    }

    public int getLaps() {
        return laps;
    }

    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public void setRaceDate(Date raceDate) {
        this.raceDate = raceDate;
    }

    public void setRaceTime(Time raceTime) {
        this.raceTime = raceTime;
    }

    public void setLaps(int laps) {
        this.laps = laps;
    }

    @Override
    public String toString() {
        return String.format("Race %d on Track %d, Date: %s %s, Laps: %d", raceId, trackId, raceDate.toString(), raceTime.toString(), laps);
    }
}
