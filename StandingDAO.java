package dao;

import model.Standing;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StandingDAO {

    public static List<Standing> getSeasonStandings() {
        List<Standing> standings = new ArrayList<>();
        String query =
                "SELECT t.team_id, t.team_name, COALESCE(SUM(rp.points), 0) AS total_points, " +
                        "SUM(CASE WHEN rp.position = 1 THEN 1 ELSE 0 END) AS wins " +
                        "FROM teams t LEFT JOIN race_participations rp ON t.team_id = rp.team_id " +
                        "GROUP BY t.team_id ORDER BY total_points DESC";

        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while(rs.next()) {
                Standing s = new Standing(
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        rs.getLong("total_points")
                );
                s.wins = rs.getInt("wins");
                standings.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return standings;
    }
}
