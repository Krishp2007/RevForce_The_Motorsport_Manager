package dao;

import model.Race;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RaceDAO {

    public static boolean scheduleRace(int trackId, String date, String time, int laps) {
        String query = "INSERT INTO races (track_id, race_date, race_time, laps) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, trackId);
            ps.setString(2, date);
            ps.setString(3, time);
            ps.setInt(4, laps);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Race> getAllRaces() {
        List<Race> races = new ArrayList<>();
        String query = "SELECT * FROM races ORDER BY race_date, race_time";
        try (Connection conn = DBManager.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                races.add(new Race(rs.getInt("race_id"), rs.getInt("track_id"), rs.getDate("race_date"), rs.getTime("race_time"), rs.getInt("laps")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return races;
    }

    public static List<Race> getAllFutureRaces() {
        List<Race> futureRaces = new ArrayList<>();
        String query = "SELECT * FROM races WHERE TIMESTAMP(race_date, race_time) > NOW() ORDER BY race_date, race_time";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Race race = new Race();
                race.setRaceId(rs.getInt("race_id"));
                race.setTrackId(rs.getInt("track_id"));
                race.setRaceDate(rs.getDate("race_date"));
                race.setRaceTime(rs.getTime("race_time"));
                race.setLaps(rs.getInt("laps"));
                futureRaces.add(race);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return futureRaces;
    }

    public static Race getRaceById(int raceId) {
        String query = "SELECT * FROM races WHERE race_id = ?";
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, raceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Race(rs.getInt("race_id"), rs.getInt("track_id"),
                        rs.getDate("race_date"), rs.getTime("race_time"), rs.getInt("laps"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean cancelRace(int raceId) {
        Connection conn = null;
        PreparedStatement selectPs = null;
        PreparedStatement deleteParticipationsPs = null;
        PreparedStatement deleteRacePs = null;
        ResultSet rs = null;

        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Verify race exists and is in the future
            String selectSql = "SELECT * FROM races WHERE race_id = ? AND TIMESTAMP(race_date, race_time) > NOW()";
            selectPs = conn.prepareStatement(selectSql);
            selectPs.setInt(1, raceId);
            rs = selectPs.executeQuery();

            if (!rs.next()) {
                System.out.println("Race not found or race date/time is not future: " + raceId);
                return false;
            }
            rs.close();
            selectPs.close();

            // 2. Delete participations for this race
            String deleteParticipationsSQL = "DELETE FROM race_participations WHERE race_id = ?";
            deleteParticipationsPs = conn.prepareStatement(deleteParticipationsSQL);
            deleteParticipationsPs.setInt(1, raceId);
            deleteParticipationsPs.executeUpdate();

            // 3. Delete the race itself
            String deleteRaceSQL = "DELETE FROM races WHERE race_id = ?";
            deleteRacePs = conn.prepareStatement(deleteRaceSQL);
            deleteRacePs.setInt(1, raceId);
            int deleted = deleteRacePs.executeUpdate();

            if (deleted != 1) {
                conn.rollback();
                System.out.println("Failed to delete race.");
                return false;
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (selectPs != null) selectPs.close();
                if (deleteParticipationsPs != null) deleteParticipationsPs.close();
                if (deleteRacePs != null) deleteRacePs.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


}
