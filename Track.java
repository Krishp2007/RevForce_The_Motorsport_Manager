package model;

public class Track {
    public int trackId;
    public String name;
    public String location;
    public float lengthKm;
    public String trackImgUrl;
    public String difficultyLevel;
    public String trackType;

    public Track(int trackId, String name, String location, float lengthKm, String trackImgUrl,
                 String difficultyLevel, String trackType) {
        this.trackId = trackId;
        this.name = name;
        this.location = location;
        this.lengthKm = lengthKm;
        this.trackImgUrl = trackImgUrl;
        this.difficultyLevel = difficultyLevel;
        this.trackType = trackType;
    }

    @Override
    public String toString() {
        return String.format("%d - %s | Location: %s | Length: %.2f km | Difficulty: %s | Type: %s",
                trackId, name, location, lengthKm, difficultyLevel, trackType);
    }
}
