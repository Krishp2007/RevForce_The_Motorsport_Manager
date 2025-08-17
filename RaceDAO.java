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
        try (Connection conn = DBManager.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM races ORDER BY race_date, race_time");
            while (rs.next()) {
                races.add(new Race(rs.getInt("race_id"), rs.getInt("track_id"),
                        rs.getDate("race_date"), rs.getTime("race_time"), rs.getInt("laps")));
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
                // Set other properties or load related objects if needed
                futureRaces.add(race);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return futureRaces;
    }

    public static Race getRaceById(int raceId) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM races WHERE race_id = ?");
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

    public static boolean cancelRace(int raceId, String cancelReason) {
        Connection conn = null;
        PreparedStatement selectPs = null;
        PreparedStatement insertPs = null;
        PreparedStatement deletePs = null;
        ResultSet rs = null;

        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Get race data from races table
            String selectSql = "SELECT * FROM races WHERE race_id = ?";
            selectPs = conn.prepareStatement(selectSql);
            selectPs.setInt(1, raceId);
            rs = selectPs.executeQuery();

            if (!rs.next()) {
                System.out.println("Race not found with ID: " + raceId);
                return false;
            }

            int trackId = rs.getInt("track_id");
            Date raceDate = rs.getDate("race_date");
            Time raceTime = rs.getTime("race_time");
            int laps = rs.getInt("laps");

            rs.close();
            selectPs.close();

            // 2. Insert into cancelled_races table
            String insertSql = "INSERT INTO cancelled_races (race_id, track_id, race_date, race_time, laps, cancel_reason) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            insertPs = conn.prepareStatement(insertSql);
            insertPs.setInt(1, raceId);
            insertPs.setInt(2, trackId);
            insertPs.setDate(3, raceDate);
            insertPs.setTime(4, raceTime);
            insertPs.setInt(5, laps);
            insertPs.setString(6, cancelReason);
            int inserted = insertPs.executeUpdate();

            if (inserted != 1) {
                conn.rollback();
                System.out.println("Failed to insert into cancelled_races.");
                return false;
            }

            insertPs.close();


            String deleteParticipationsSQL = "DELETE FROM race_participations WHERE race_id = ?";
            try (PreparedStatement psDelParticipations = conn.prepareStatement(deleteParticipationsSQL)) {
                psDelParticipations.setInt(1, raceId);
                psDelParticipations.executeUpdate();
            }

            // 3. Delete race from races table
            String deleteSql = "DELETE FROM races WHERE race_id = ?";
            deletePs = conn.prepareStatement(deleteSql);
            deletePs.setInt(1, raceId);
            int deleted = deletePs.executeUpdate();

            if (deleted != 1) {
                conn.rollback();
                System.out.println("Failed to delete race from races.");
                return false;
            }

            conn.commit(); // Commit transaction
            System.out.println("Race cancelled successfully and moved to history.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (selectPs != null) selectPs.close();
                if (insertPs != null) insertPs.close();
                if (deletePs != null) deletePs.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
