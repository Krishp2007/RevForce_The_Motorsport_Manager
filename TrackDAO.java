package dao;

import model.Track;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrackDAO {

    public static boolean addTrack(String name, String location, float lengthKm, String trackImgUrl,
                                   String difficultyLevel, String trackType) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO tracks (name, location, length_km, track_img_url, difficulty_level, track_type) " +
                            "VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, location);
            ps.setFloat(3, lengthKm);
            ps.setString(4, trackImgUrl);
            ps.setString(5, difficultyLevel);
            ps.setString(6, trackType);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Track> getAllTracks() {
        List<Track> tracks = new ArrayList<>();
        try (Connection conn = DBManager.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tracks");
            while (rs.next()) {
                tracks.add(new Track(
                        rs.getInt("track_id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getFloat("length_km"),
                        rs.getString("track_img_url"),
                        rs.getString("difficulty_level"),
                        rs.getString("track_type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tracks;
    }

    public static Track getTrackById(int trackId) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM tracks WHERE track_id = ?");
            ps.setInt(1, trackId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Track(rs.getInt("track_id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getFloat("length_km"),
                        rs.getString("track_img_url"),
                        rs.getString("difficulty_level"),
                        rs.getString("track_type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
