package ds;

import dao.DBManager;
import model.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserTeamMap {
    private CustomHashMap<Integer, Team> map = new CustomHashMap<>();

    public void addUserTeam(int userId, Team team) {
        map.put(userId, team);
    }

    public Team getTeamByUserId(int userId) {
        return map.get(userId);
    }

    public void removeUser(int userId) {
        map.remove(userId);
    }

    public void loadUserTeamMap() {
        String sql = "SELECT u.user_id, t.team_id, t.team_name, t.budget, t.origin, t.founding_year, t.email FROM users u INNER JOIN teams t ON u.team_id = t.team_id";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                // check if user has a team assigned
                int teamId = rs.getInt("team_id");
                Team team = null;
                if (teamId != 0) {
                    team = new Team(
                            teamId,
                            rs.getString("team_name"),
                            rs.getLong("budget"),
                            rs.getString("origin"),
                            rs.getInt("founding_year"),
                            rs.getString("email")
                    );
                }
                map.put(userId, team);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
