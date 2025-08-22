package dao;

import model.User;
import java.sql.*;

public class UserDAO {

    public static boolean isValidAdminAccessKey(String accessKey) {
        String sql = "SELECT COUNT(*) FROM users WHERE access_key = ? AND role = 'admin'";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accessKey);
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

    public static boolean usernameExists(String username) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static boolean emailExists(String email) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static String generateNewAdminAccessKey() {
        String prefix = "ADM-";
        String sql = "SELECT MAX(access_key) FROM users WHERE role = 'admin' AND access_key LIKE ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String accessKey = rs.getString(1);
                    if (accessKey != null) {
                        String numberPart = accessKey.replace(prefix, "");
                        int num = Integer.parseInt(numberPart);
                        return prefix + String.format("%03d", num + 1);
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        // fallback if no existing or parse error
        return prefix + "001";
    }

    public static boolean registerAdmin(User user, String AccessKey) {
        // Generate new unique referral number for this admin
        String newAccessKey = generateNewAdminAccessKey();

        String sqlUser = "INSERT INTO users (first_name, last_name, username, email, password, role, access_key) VALUES (?, ?, ?, ?, ?, 'admin', ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psUser = conn.prepareStatement(sqlUser)) {

            psUser.setString(1, user.firstName);
            psUser.setString(2, user.lastName);
            psUser.setString(3, user.username);
            psUser.setString(4, user.email);
            psUser.setString(5, user.password);
            psUser.setString(6, newAccessKey);

            int rowsInserted = psUser.executeUpdate();
            if (rowsInserted == 1) {
                System.out.println("Admin account created with referral number: " + newAccessKey);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean registerUser(User user, String teamName, String teamOrigin) {
        int teamId = TeamDAO.addTeam(teamName, teamOrigin);
        int userId = -1;
        String query = "INSERT INTO users (first_name, last_name, username, email, password, role) VALUES (?, ?, ?, ?, ?, 'user')";
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.firstName);
            ps.setString(2, user.lastName);
            ps.setString(3, user.username);
            ps.setString(4, user.email);
            ps.setString(5, user.password);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            UserDAO.assignTeamToUser(userId, teamId);
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static boolean assignTeamToUser(int userId, int teamId) {
        String query = "UPDATE users SET team_id = ? WHERE user_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static User authenticateUser(String username, String password) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ? AND role = 'user' ");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("team_id"));
                user.userId = rs.getInt("user_id");
                return user;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static User getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.userId = rs.getInt("user_id");
                u.username = rs.getString("username");
                u.teamId = rs.getInt("team_id");
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRoleFromDatabase(String username) {
        String sql = "SELECT role FROM Users WHERE username = ? LIMIT 1";
        try {
            Connection conn = DBManager.getConnection();
            if (conn == null) return null;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
            return null;
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
            return null;
        }
    }

    public static boolean authenticateAdmin(String username, String password) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ? AND role = 'admin'");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
