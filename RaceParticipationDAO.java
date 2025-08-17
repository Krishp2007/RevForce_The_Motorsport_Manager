package dao;

import model.ParticipantStats;
import model.RaceParticipation;
import java.sql.*;
import java.util.*;

public class RaceParticipationDAO {

    public static boolean registerTeamForRace(int raceId, int teamId) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement psCheck = conn.prepareStatement("SELECT * FROM race_participations WHERE race_id = ? AND team_id = ?");
            psCheck.setInt(1, raceId);
            psCheck.setInt(2, teamId);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) {
                return false;  // Already registered
            }
            PreparedStatement ps = conn.prepareStatement("INSERT INTO race_participations (race_id, team_id) VALUES (?, ?)");
            ps.setInt(1, raceId);
            ps.setInt(2, teamId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<RaceParticipation> getParticipationsByRace(int raceId) {
        List<RaceParticipation> list = new ArrayList<>();
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM race_participations WHERE race_id = ?");
            ps.setInt(1, raceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RaceParticipation(
                        rs.getInt("participation_id"),
                        rs.getInt("race_id"),
                        rs.getInt("team_id"),
                        (Integer)rs.getObject("position"),
                        rs.getInt("points")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static boolean hasPositions(int raceId) {
        String query = "SELECT COUNT(*) FROM race_participations WHERE race_id = ? AND position IS NOT NULL";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, raceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
