package dao;

import model.SponsorshipOffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SponsorshipOfferDAO {

    public static List<SponsorshipOffer> getPendingOffersByTeamId(int teamId) {
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
                        rs.getDate("offer_start_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offers;
    }

    public static boolean existsActiveOrPendingOffer(int teamId, String sponsorName) {
        String sql = "SELECT COUNT(*) FROM sponsorship_offers " +
                "WHERE team_id = ? AND sponsor_name = ? " +
                "AND status IN ('pending', 'accepted') " +
                "AND offer_expire_date >= CURDATE()";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.setString(2, sponsorName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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

    public static boolean generateSponsorship(int teamId, String sponsorName, String industry, long amount, int duration) {
        String query = "{CALL add_sponsorship_offer(?, ?, ?, ?, ?)}";
        try (Connection conn = DBManager.getConnection();
             CallableStatement cs = conn.prepareCall(query)) {

            cs.setInt(1, teamId);
            cs.setString(2, sponsorName);
            cs.setString(3, industry);
            cs.setLong(4, amount);
            cs.setInt(5, duration);

            cs.execute();
            return true; // if execute() completes without exception, consider it success
        } catch (SQLException e) {
            System.out.println("Error executing stored procedure: " + e.getMessage());
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
                        rs.getDate("offer_start_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sponsors;
    }

    public static List<SponsorshipOffer> getActiveSponsorshipOffersForTeam(int teamId) {
        String sql = "SELECT * FROM sponsorship_offers " +
                "WHERE team_id = ? " +
                "AND offer_expire_date >= CURDATE() " +
                "AND status = 'accepted'";  // filter accepted offers only

        List<SponsorshipOffer> offers = new ArrayList<>();
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    offers.add(new SponsorshipOffer(
                            rs.getInt("offer_id"),
                            rs.getInt("team_id"),
                            rs.getString("sponsor_name"),
                            rs.getString("industry"),
                            rs.getLong("amount"),
                            rs.getInt("contract_duration_months"),
                            rs.getString("status"),
                            rs.getDate("offer_start_date")
                            // add other fields here if needed
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offers;
    }

    public static List<SponsorshipOffer> getActiveAndPendingOffersForTeam(int teamId) {
        String sql = "SELECT * FROM sponsorship_offers " +
                "WHERE team_id = ? " +
                "AND (status = 'accepted' OR status = 'pending') " +
                "AND offer_expire_date >= CURDATE()";

        List<SponsorshipOffer> offers = new ArrayList<>();
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    offers.add(new SponsorshipOffer(
                            rs.getInt("offer_id"),
                            rs.getInt("team_id"),
                            rs.getString("sponsor_name"),
                            rs.getString("industry"),
                            rs.getLong("amount"),
                            rs.getInt("contract_duration_months"),
                            rs.getString("status"),
                            rs.getDate("offer_start_date")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offers;
    }



}
