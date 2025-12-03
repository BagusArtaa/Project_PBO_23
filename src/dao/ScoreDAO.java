package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import model.Score;

public class ScoreDAO {


    public boolean saveScore(Score s) {
        String sql = "INSERT INTO leaderboard (user_id, score, wpm, accuracy, time_survive) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, s.getUserId());
            stmt.setInt(2, s.getScore());
            stmt.setDouble(3, s.getWpm());
            stmt.setDouble(4, s.getAccuracy());
            stmt.setDouble(5, s.getTimeSurvive());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Score> getTopScores(int limit) {

        List<Score> list = new ArrayList<>();

        String sql = 
                "SELECT u.username, l.score, l.wpm, l.accuracy, l.time_survive " +
                "FROM leaderboard l " +
                "JOIN users u ON l.user_id = u.id " +
                "ORDER BY l.score DESC " +
                "LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Score sc = new Score(
                            rs.getString("username"),
                            rs.getInt("score"),
                            rs.getDouble("wpm"),
                            rs.getDouble("accuracy"),
                            rs.getDouble("time_survive")
                    );
                    list.add(sc);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
