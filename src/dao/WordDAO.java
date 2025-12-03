package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.WordEntry;

public class WordDAO {

    public List<WordEntry> getWordsByMaxDifficulty(int maxDifficulty) {
        List<WordEntry> list = new ArrayList<>();

        String sql = "SELECT id, word, difficulty FROM words WHERE difficulty <= ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maxDifficulty);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new WordEntry(
                            rs.getInt("id"),
                            rs.getString("word"),
                            rs.getInt("difficulty")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}
