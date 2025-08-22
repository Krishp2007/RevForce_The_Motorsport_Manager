package dao;

import model.Standing;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StandingDAO {

    // Existing parameterless method - defaults to sorting by points
    public static List<Standing> getSeasonStandings() {
        return getSeasonStandings("points");
    }

    // New overloaded method with sorting parameter
    public static List<Standing> getSeasonStandings(String sortBy) {
        List<Standing> standings = new ArrayList<>();

        String orderByClause = "total_points DESC";
        if ("wins".equalsIgnoreCase(sortBy)) {
            orderByClause = "wins DESC";
        }

        String query = "SELECT t.team_id, t.team_name, " +
                "COALESCE(SUM(rr.points),0) AS total_points, " +
                "SUM(CASE WHEN rr.position = 1 THEN 1 ELSE 0 END) AS wins " +
                "FROM teams t " +
                "LEFT JOIN race_participations rp ON t.team_id = rp.team_id " +
                "LEFT JOIN race_results rr ON rp.participation_id = rr.participation_id " +
                "GROUP BY t.team_id, t.team_name " +
                "ORDER BY " + orderByClause;

        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Standing standing = new Standing(
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        rs.getLong("total_points")
                );
                standing.wins = rs.getInt("wins");
                standings.add(standing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return standings;
    }
}
