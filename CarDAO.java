package dao;

import model.Car; // You'd define Car model similar to others
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {

    public static boolean addCar(String carName, long price, int enginePower, int maxSpeed) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO cars (car_name, price, engine_power, max_speed) VALUES (?, ?, ?, ?)");
            ps.setString(1, carName);
            ps.setLong(2, price);
            ps.setInt(3, enginePower);
            ps.setInt(4, maxSpeed);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Car> getCarsByTeam(int teamId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT * FROM cars WHERE team_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Car car = new Car();
                car.carId = rs.getInt("car_id");
                car.carName = rs.getString("car_name");
                car.price = rs.getLong("price");
                car.enginePower = rs.getInt("engine_power");
                car.maxSpeed = rs.getInt("max_speed");
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    public static List<Car> getAllAvailableCars() {
        List<Car> cars = new ArrayList<>();
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM cars WHERE team_id IS NULL ORDER BY price ASC");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cars.add(new Car(
                        rs.getInt("car_id"),
                        rs.getString("car_name"),
                        rs.getLong("price"),
                        rs.getInt("engine_power"),
                        rs.getInt("max_speed"),
                        -1
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    public static Car getCarById(int carId) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM cars WHERE car_id = ?");
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Car(
                        rs.getInt("car_id"),
                        rs.getString("car_name"),
                        rs.getLong("price"),
                        rs.getInt("engine_power"),
                        rs.getInt("max_speed"),
                        rs.getObject("team_id") == null ? -1 : rs.getInt("team_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean assignCarToTeam(int carId, int teamId, long carPrice) {
        try (Connection conn = DBManager.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement ps1 = conn.prepareStatement("SELECT budget FROM teams WHERE team_id = ?");
            ps1.setInt(1, teamId);
            ResultSet rs = ps1.executeQuery();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            long budget = rs.getLong("budget");
            if (budget < carPrice) {
                conn.rollback();
                return false;
            }

            PreparedStatement ps2 = conn.prepareStatement("UPDATE teams SET budget = budget - ? WHERE team_id = ?");
            ps2.setLong(1, carPrice);
            ps2.setInt(2, teamId);
            ps2.executeUpdate();

            PreparedStatement ps3 = conn.prepareStatement("UPDATE cars SET team_id = ? WHERE car_id = ? AND team_id IS NULL");
            ps3.setInt(1, teamId);
            ps3.setInt(2, carId);
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
