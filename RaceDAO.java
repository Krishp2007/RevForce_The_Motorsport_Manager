package com.revForce.motorsportmanager.db;

import com.revForce.motorsportmanager.race.Race;
import com.revForce.motorsportmanager.race.Track;
import com.revForce.motorsportmanager.team.Car;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RaceDAO {

    private final TrackDAO trackDAO = new TrackDAO();

    public void saveRace(Race race) {
        String sql = "INSERT INTO races (track_id, date, is_completed, result_summary) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, race.getTrack().getId());
            stmt.setTimestamp(2, Timestamp.valueOf(race.getRaceDate()));
            stmt.setBoolean(3, race.isCompleted());
            stmt.setString(4, race.getResultSummary());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        race.setRaceId(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Race getRaceById(int raceId) {
        String sql = "SELECT * FROM races WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, raceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int trackId = rs.getInt("track_id");
                    Track track = trackDAO.getTrackById(trackId);
                    LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
                    boolean completed = rs.getBoolean("is_completed");
                    String summary = rs.getString("result_summary");

                    return new Race(raceId, track, date, new ArrayList<>(), completed, summary);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void markRaceCompleted(int raceId, String resultSummary) {
        String sql = "UPDATE races SET is_completed = ?, result_summary = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, true);
            stmt.setString(2, resultSummary);
            stmt.setInt(3, raceId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Race> getAllRaces() {
        List<Race> races = new ArrayList<>();
        String sql = "SELECT * FROM races";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                races.add(buildRaceFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return races;
    }

    // NEW FUNCTIONALITIES BELOW

    public boolean deleteRace(int raceId) {
        String sql = "DELETE FROM races WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, raceId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Race> getUpcomingRaces() {
        List<Race> races = new ArrayList<>();
        String sql = "SELECT * FROM races WHERE is_completed = false ORDER BY date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                races.add(buildRaceFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return races;
    }

    public List<Race> getCompletedRaces() {
        List<Race> races = new ArrayList<>();
        String sql = "SELECT * FROM races WHERE is_completed = true ORDER BY date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                races.add(buildRaceFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return races;
    }

    public List<Race> getRacesByTrack(int trackId) {
        List<Race> races = new ArrayList<>();
        String sql = "SELECT * FROM races WHERE track_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trackId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    races.add(buildRaceFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return races;
    }

    public List<Race> getRecentRaces(int limit) {
        List<Race> races = new ArrayList<>();
        String sql = "SELECT * FROM races ORDER BY date DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    races.add(buildRaceFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return races;
    }

    public List<Race> searchRacesByDateRange(LocalDateTime from, LocalDateTime to) {
        List<Race> races = new ArrayList<>();
        String sql = "SELECT * FROM races WHERE date BETWEEN ? AND ? ORDER BY date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(from));
            stmt.setTimestamp(2, Timestamp.valueOf(to));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    races.add(buildRaceFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return races;
    }

    public int getRaceCount() {
        String sql = "SELECT COUNT(*) FROM races";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Helper method to reduce repetition
    private Race buildRaceFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int trackId = rs.getInt("track_id");
        LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
        boolean isCompleted = rs.getBoolean("is_completed");
        String summary = rs.getString("result_summary");

        Track track = trackDAO.getTrackById(trackId);
        List<Car> dummyCars = new ArrayList<>(); // to be loaded later
        return new Race(id, track, date, dummyCars, isCompleted, summary);
    }
}
