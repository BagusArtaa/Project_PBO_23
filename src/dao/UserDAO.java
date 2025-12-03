package dao;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

import model.User;

public class UserDAO {

    public int loginUser(String username, String password) {
        String sql = "SELECT id, password_hash FROM users WHERE username = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) return -1;

                String hash = rs.getString("password_hash");
                if (BCrypt.checkpw(password, hash)) {
                    return rs.getInt("id");
                }
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean registerUser(String username, String password) {

        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));

            stmt.setString(1, username);
            stmt.setString(2, hashed);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public User getUserById(int id) {
        String sql = "SELECT id, username, created_at FROM users WHERE id = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("created_at")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
