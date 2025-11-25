package dao;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {

    public int loginUser(String username, String password) {
        String sql = "SELECT id, password_hash FROM users WHERE username = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return -1;
                }

                String storedHash = rs.getString("password_hash");

                if (BCrypt.checkpw(password, storedHash)) {
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
}
