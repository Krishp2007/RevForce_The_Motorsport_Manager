package com.revForce.motorsportmanager.db;

import com.revForce.motorsportmanager.race.Race;
import com.revForce.motorsportmanager.race.Track;
import com.revForce.motorsportmanager.team.Car;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrackDAO {

    public boolean insertTrack(Track track) {
        String sql = "INSERT INTO tracks (name, difficulty, laps, length_km, country, city, year_built, turns, weather_type, image_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setTrackParams(stmt, track);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Insert error: " + e.getMessage());
        }
        return false;
    }

    public Track getTrackById(int id) {
        String sql = "SELECT * FROM tracks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractTrackFromResultSet(rs);
            }
        } catch (Exception e) {
            System.out.println("Get by ID error: " + e.getMessage());
        }
        return null;
    }

    public List<Track> getAllTracks() {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tracks.add(extractTrackFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("Get all error: " + e.getMessage());
        }
        return tracks;
    }

    public boolean updateTrack(Track track) {
        String sql = "UPDATE tracks SET name=?, difficulty=?, laps=?, length_km=?, country=?, city=?, year_built=?, turns=?, weather_type=?, image_path=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setTrackParams(stmt, track);
            stmt.setInt(11, track.getId());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Update error: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteTrack(int id) {
        String sql = "DELETE FROM tracks WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Delete error: " + e.getMessage());
        }
        return false;
    }

    public int countTracks() {
        String sql = "SELECT COUNT(*) AS total FROM tracks";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (Exception e) {
            System.out.println("Count error: " + e.getMessage());
        }
        return 0;
    }

    public List<Track> getTracksByPage(int limit, int offset) {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tracks.add(extractTrackFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("Pagination error: " + e.getMessage());
        }
        return tracks;
    }

    public List<Track> filterByDifficulty(String difficulty) {
        return filterByField("difficulty", difficulty);
    }

    public List<Track> filterByCountry(String country) {
        return filterByField("country", country);
    }

    private List<Track> filterByField(String field, String value) {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks WHERE " + field + " = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tracks.add(extractTrackFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("Filter error (" + field + "): " + e.getMessage());
        }
        return tracks;
    }

    public List<Track> getOldestTracks(int topN) {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks ORDER BY year_built ASC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, topN);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tracks.add(extractTrackFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("Oldest track error: " + e.getMessage());
        }
        return tracks;
    }

    public int batchInsertTracks(List<Track> trackList) {
        int insertedCount = 0;
        String sql = "INSERT INTO tracks (name, difficulty, laps, length_km, country, city, year_built, turns, weather_type, image_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Track track : trackList) {
                setTrackParams(stmt, track);
                stmt.addBatch();
            }
            int[] results = stmt.executeBatch();
            for (int result : results) {
                if (result >= 0) insertedCount++;
            }
        } catch (Exception e) {
            System.out.println("Batch insert error: " + e.getMessage());
        }
        return insertedCount;
    }

    private Track extractTrackFromResultSet(ResultSet rs) throws SQLException {
        return new Track(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("difficulty"),
                rs.getInt("laps"),
                rs.getDouble("length_km"),
                rs.getString("country"),
                rs.getString("city"),
                rs.getInt("year_built"),
                rs.getInt("turns"),
                rs.getString("weather_type"),
                rs.getString("image_path")
        );
    }

    private void setTrackParams(PreparedStatement stmt, Track track) throws SQLException {
        stmt.setString(1, track.getName());
        stmt.setString(2, track.getDifficulty());
        stmt.setInt(3, track.getLaps());
        stmt.setDouble(4, track.getLengthKm());
        stmt.setString(5, track.getCountry());
        stmt.setString(6, track.getCity());
        stmt.setInt(7, track.getYearBuilt());
        stmt.setInt(8, track.getTurns());
        stmt.setString(9, track.getWeatherType());
        stmt.setString(10, track.getImagePath());
    }
}
