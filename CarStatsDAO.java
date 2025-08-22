package dao;

import java.sql.*;

public class CarStatsDAO {

    public static class CarStats {
        public int carId;
        public String carName;
        public int totalRaces;
        public int wins;
        public long totalPoints;

        public CarStats(int carId, String carName, int totalRaces, int wins, long totalPoints) {
            this.carId = carId;
            this.carName = carName;
            this.totalRaces = totalRaces;
            this.wins = wins;
            this.totalPoints = totalPoints;
        }
    }

    public static CarStats getCarStats(int carId, int teamId) {
        String query = "SELECT c.car_id, c.car_name, COUNT(DISTINCT rr.race_id) AS total_races, SUM(CASE WHEN rr.position = 1 THEN 1 ELSE 0 END) AS wins, COALESCE(SUM(rr.points), 0) AS total_points FROM cars c LEFT JOIN race_participations rp ON c.car_id = rp.car_id AND c.team_id = rp.team_id LEFT JOIN race_results rr ON rp.participation_id = rr.participation_id WHERE c.car_id = ? AND c.team_id = ? GROUP BY c.car_id, c.car_name";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, carId);
            ps.setInt(2, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CarStats(
                            rs.getInt("car_id"),
                            rs.getString("car_name"),
                            rs.getInt("total_races"),
                            rs.getInt("wins"),
                            rs.getLong("total_points")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
