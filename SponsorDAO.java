package dao;

import model.Sponsor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SponsorDAO {

    public static boolean addSponsor(String name, String industry, long contractValue, int durationMonths) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO sponsors (name, industry, contract_value, contract_duration_months) VALUES (?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, industry);
            ps.setLong(3, contractValue);
            ps.setInt(4, durationMonths);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Sponsor> getAllSponsors() {
        List<Sponsor> sponsors = new ArrayList<>();
        try (Connection conn = DBManager.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM sponsors");
            while (rs.next()) {
                sponsors.add(new Sponsor(rs.getInt("sponsor_id"), rs.getString("name"), rs.getString("industry"),
                        rs.getLong("contract_value"), rs.getInt("contract_duration_months")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sponsors;
    }

    public static Sponsor getSponsorById(int sponsorId) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM sponsors WHERE sponsor_id = ?");
            ps.setInt(1, sponsorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Sponsor(rs.getInt("sponsor_id"), rs.getString("name"), rs.getString("industry"),
                        rs.getLong("contract_value"), rs.getInt("contract_duration_months"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
