package dao;

import java.sql.*;

public class DriverStatsDAO {

    public static class DriverStats {
        public int driverId;
        public String driverName;
        public int totalRaces;
        public int wins;
        public long totalPoints;

        public DriverStats(int driverId, String driverName, int totalRaces, int wins, long totalPoints) {
            this.driverId = driverId;
            this.driverName = driverName;
            this.totalRaces = totalRaces;
            this.wins = wins;
            this.totalPoints = totalPoints;
        }
    }

    public static DriverStats getDriverStats(int driverId, int teamId) {
        String query = "SELECT d.driver_id, d.driver_name, " +
                "COUNT(rp.race_id) as total_races, " +
                "SUM(CASE WHEN rp.position = 1 THEN 1 ELSE 0 END) as wins, " +
                "COALESCE(SUM(rp.points), 0) as total_points " +
                "FROM drivers d " +
                "LEFT JOIN race_participations rp ON d.team_id = rp.team_id " +
                "WHERE d.driver_id = ? AND d.team_id = ?" +
                "GROUP BY d.driver_id, d.driver_name";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, driverId);
            ps.setInt(2, teamId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new DriverStats(
                        rs.getInt("driver_id"),
                        rs.getString("driver_name"),
                        rs.getInt("total_races"),
                        rs.getInt("wins"),
                        rs.getLong("total_points")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
