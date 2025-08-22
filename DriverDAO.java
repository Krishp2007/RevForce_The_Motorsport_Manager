package dao;

import model.Driver; // You'd define Driver model similar to others
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {

    public static boolean addDriver(String driverName, int skillLevel, long rentalPrice, String nationality, Date joinedDate) {
        String query = "INSERT INTO drivers (driver_name, skill_level, joined_date, rental_price, nationality, team_id) VALUES (?, ?, ?, ?, ?, NULL)";
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, driverName);
            ps.setInt(2, skillLevel);
            ps.setDate(3, joinedDate);
            ps.setLong(4, rentalPrice);
            ps.setString(5, nationality);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Driver> getDriversByTeam(int teamId) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT * FROM drivers WHERE team_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Driver driver = new Driver();
                driver.driverId = rs.getInt("driver_id");
                driver.driverName = rs.getString("driver_name");
                driver.skillLevel = rs.getInt("skill_level");
                driver.rentalPrice = rs.getLong("rental_price");
                drivers.add(driver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    public static List<Driver> getAvailableDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT * FROM drivers WHERE team_id IS NULL ORDER BY skill_level DESC";
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                drivers.add(new Driver(
                        rs.getInt("driver_id"),
                        rs.getString("driver_name"),
                        rs.getInt("skill_level"),
                        rs.getDate("joined_date"),
                        rs.getLong("rental_price"),
                        rs.getString("nationality"),
                        rs.getObject("team_id") == null ? -1 : rs.getInt("team_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    public static Driver getDriverById(int driverId) {
        String query = "SELECT * FROM drivers WHERE driver_id = ?";
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, driverId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Driver(
                        rs.getInt("driver_id"),
                        rs.getString("driver_name"),
                        rs.getInt("skill_level"),
                        rs.getDate("joined_date"),
                        rs.getLong("rental_price"),
                        rs.getString("nationality"),
                        rs.getObject("team_id") == null ? -1 : rs.getInt("team_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean assignDriverToTeam(int driverId, int teamId, long rentalPrice) {
        try (Connection conn = DBManager.getConnection()) {
            conn.setAutoCommit(false);

            String query = "SELECT budget FROM teams WHERE team_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(query);
            ps1.setInt(1, teamId);
            ResultSet rs = ps1.executeQuery();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            long budget = rs.getLong("budget");
            if (budget < rentalPrice) {
                conn.rollback();
                return false;
            }

            String query2 = "UPDATE teams SET budget = budget - ? WHERE team_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(query2);
            ps2.setLong(1, rentalPrice);
            ps2.setInt(2, teamId);
            ps2.executeUpdate();

            String query3 = "UPDATE drivers SET team_id = ? WHERE driver_id = ? AND team_id IS NULL";
            PreparedStatement ps3 = conn.prepareStatement(query3);
            ps3.setInt(1, teamId);
            ps3.setInt(2, driverId);
            int updated = ps3.executeUpdate();
            if (updated != 1) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
