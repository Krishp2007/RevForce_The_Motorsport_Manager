package dao;

import model.SponsorshipOffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SponsorshipOfferDAO {

    public static List<SponsorshipOffer> getPendingOffersByTeam(int teamId) {
        List<SponsorshipOffer> offers = new ArrayList<>();
        String query = "SELECT * FROM sponsorship_offers WHERE team_id = ? AND status = 'pending'";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                offers.add(new SponsorshipOffer(
                        rs.getInt("offer_id"),
                        rs.getInt("team_id"),
                        rs.getString("sponsor_name"),
                        rs.getString("industry"),
                        rs.getLong("amount"),
                        rs.getInt("contract_duration_months"),
                        rs.getString("status"),
                        rs.getDate("offer_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offers;
    }

    public static boolean updateOfferStatus(int offerId, String newStatus) {
        String query = "UPDATE sponsorship_offers SET status = ? WHERE offer_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newStatus);
            ps.setInt(2, offerId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addSponsorshipAmountToTeam(int teamId, long amount) {
        String query = "UPDATE teams SET budget = budget + ? WHERE team_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(1, amount);
            ps.setInt(2, teamId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean insertOffer(int teamId, String sponsorName, String industry, long amount, int duration, Date offerDate) {
        String query = "INSERT INTO sponsorship_offers (team_id, sponsor_name, industry, amount, contract_duration_months, offer_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ps.setString(2, sponsorName);
            ps.setString(3, industry);
            ps.setLong(4, amount);
            ps.setInt(5, duration);
            ps.setDate(6, offerDate);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<SponsorshipOffer> getAcceptedSponsorsByTeam(int teamId) {
        List<SponsorshipOffer> sponsors = new ArrayList<>();
        String query = "SELECT * FROM sponsorship_offers WHERE team_id = ? AND status = 'accepted'";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sponsors.add(new SponsorshipOffer(
                        rs.getInt("offer_id"),
                        rs.getInt("team_id"),
                        rs.getString("sponsor_name"),
                        rs.getString("industry"),
                        rs.getLong("amount"),
                        rs.getInt("contract_duration_months"),
                        rs.getString("status"),
                        rs.getDate("offer_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sponsors;
    }
}
