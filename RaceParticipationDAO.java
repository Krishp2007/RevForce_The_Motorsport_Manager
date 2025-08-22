package dao;

import model.RaceParticipation;
import java.sql.*;
import java.util.*;

public class RaceParticipationDAO {

    public static boolean registerTeamForRace(int raceId, int teamId, int carId, int driverId) {
        String checkSql = "SELECT COUNT(*) FROM race_participations WHERE race_id = ? AND team_id = ?";
        String insertSql = "INSERT INTO race_participations (race_id, team_id, car_id, driver_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection()) {
            // Check if already registered
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, raceId);
                checkPs.setInt(2, teamId);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // Already registered
                }
            }

            // Register participation
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setInt(1, raceId);
                insertPs.setInt(2, teamId);
                insertPs.setInt(3, carId);
                insertPs.setInt(4, driverId);
                int affectedRows = insertPs.executeUpdate();
                return affectedRows == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static List<RaceParticipation> getParticipationsByRaceId(int raceId) {
        List<RaceParticipation> list = new ArrayList<>();
        String query = "SELECT * FROM race_participations WHERE race_id = ?";

        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, raceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RaceParticipation(
                        rs.getInt("participation_id"),
                        rs.getInt("race_id"),
                        rs.getInt("team_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



}
