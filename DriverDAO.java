package com.revForce.motorsportmanager.db;

import com.revForce.motorsportmanager.team.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {

    public void addDriver(Driver driver) {
        String sql = "INSERT INTO drivers (name, age, nationality, skill_level) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, driver.getName());
            stmt.setInt(2, driver.getAge());
            stmt.setString(3, driver.getNationality());
            stmt.setInt(4, driver.getSkillLevel());

            int check = stmt.executeUpdate();
            if(check > 0) {
                System.out.println("Driver Data Inserted Successfully");
            } else {
                System.out.println("Some Problem Occurred!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDriver(Driver driver) {
        String sql = "UPDATE drivers SET name=?, age=?, nationality=?, skill_level=? WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, driver.getName());
            stmt.setInt(2, driver.getAge());
            stmt.setString(3, driver.getNationality());
            stmt.setInt(4, driver.getSkillLevel());
            stmt.setInt(5, driver.getId());

            int check = stmt.executeUpdate();
            if(check > 0) {
                System.out.println("Driver Data Updated Successfully");
            } else {
                System.out.println("Some Problem Occurred!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDriver(int id) {
        String sql = "DELETE FROM drivers WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int check = stmt.executeUpdate();
            if(check > 0) {
                System.out.println("Driver Data Deleted Successfully");
            } else {
                System.out.println("Some Problem Occurred!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Driver> getAllDrivers() {
        ArrayList<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Driver driver = new Driver(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("nationality"),
                        rs.getInt("skill_level")
                );
                drivers.add(driver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    public Driver getDriverById(int id) {
        String sql = "SELECT * FROM drivers WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Driver(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("nationality"),
                        rs.getInt("skill_level")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Driver> getDriversBySkillThreshold(int minSkill) {
        ArrayList<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers WHERE skill_level >= ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, minSkill);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Driver driver = new Driver(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("nationality"),
                        rs.getInt("skill_level")
                );
                drivers.add(driver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }
}
