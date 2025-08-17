package dao;

import model.Team;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {
    public static Team getTeamById(int teamId) {
        String query = "SELECT * FROM teams WHERE team_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Team t = new Team();
                t.teamId = rs.getInt("team_id");
                t.teamName = rs.getString("team_name");
                t.budget = rs.getLong("budget");
                t.origin = rs.getString("origin");
                t.foundingYear = rs.getInt("founding_year");
                t.email = rs.getString("email");
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateBudget(int teamId, long newBudget) {
        String query = "UPDATE teams SET budget = ? WHERE team_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(1, newBudget);
            ps.setInt(2, teamId);
            int rows = ps.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static int addTeam(String teamName, String origin) {
        int teamId = -1;
        String email = teamName.replaceAll("\\s+", "").toLowerCase() + "@email.com";
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO teams (team_name, origin, email) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, teamName);
            ps.setString(2, origin);
            ps.setString(3, email);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                teamId = rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return teamId;
    }

    public static List<Team> getAllTeams() {
        List<Team> teams = new ArrayList<>();
        try (Connection conn = DBManager.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM teams");
            while (rs.next()) {
                teams.add(new Team(
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        rs.getLong("budget"),
                        rs.getString("origin"),
                        rs.getInt("founding_year"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }
}
