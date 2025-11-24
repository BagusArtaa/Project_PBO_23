import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;


public class KoneksiDatabase {
    
    private final String dbUrl = "jdbc:mysql://localhost:3306/keystrike?serverTimezone=UTC";
    private final String dbUser = "root";
    private final String dbPass = ""; 

    private Connection connection;

    public KoneksiDatabase() {
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            System.out.println("Koneksi DB berhasil!");

            populateSoalFromTxt("assets/contoh_soal.txt");

        } catch (SQLException e) {
            System.err.println("Koneksi DB Gagal: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Gagal terhubung ke database. Pastikan MySQL berjalan.", 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateSoalFromTxt(String filePath) {
        String checkQuery = "SELECT COUNT(*) FROM soal";
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery(checkQuery)) {
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Tabel 'soal' sudah berisi data. Skip populasi dari TXT.");
                return; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        System.out.println("Tabel 'soal' kosong. Membaca dari " + filePath + "...");
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File soal tidak ditemukan di: " + file.getAbsolutePath());
            return;
        }
    
        String insertQuery = "INSERT INTO soal (data) VALUES (?)";
        try (Scanner scanner = new Scanner(file);
             PreparedStatement ps = connection.prepareStatement(insertQuery)) {
    
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    ps.setString(1, line);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            System.out.println("Berhasil populasi data soal dari " + filePath);
    
        } catch (FileNotFoundException e) {
            System.err.println("File soal tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() {
        return connection;
    }

    public String[] findUser(String username, String passwordPlain) {
        String query = "SELECT id, username, password_hash FROM users WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String inputHash = hashPassword(passwordPlain);

                    if (storedHash.equals(inputHash)) {
                        return new String[] {
                                String.valueOf(rs.getInt("id")),
                                rs.getString("username")
                        };
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean registerUser(String username, String passwordPlain) {
        String query = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            String hashedPassword = hashPassword(passwordPlain);

            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Register Gagal: Username sudah ada.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getRandomSoal() {
        String query = "SELECT data FROM soal ORDER BY RAND() LIMIT 1";
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("data");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Ini adalah soal fallback jika database gagal mengambil soal. Coba lagi nanti.";
    }

    public boolean saveScore(int userId, double wpm, double accuracy) {
        String query = "INSERT INTO scores (user_id, wpm, accuracy, timestamp) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setDouble(2, wpm);
            ps.setDouble(3, accuracy);
            
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> getLeaderboard() {
        List<Object[]> leaderboardData = new ArrayList<>();
        String query = "SELECT u.username, s.wpm, s.accuracy, s.timestamp " +
                       "FROM scores s " +
                       "JOIN users u ON s.user_id = u.user_id " +
                       "ORDER BY s.wpm DESC " +
                       "LIMIT 10";
        
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery(query)) {
            
            while (rs.next()) {
                leaderboardData.add(new Object[] {
                    rs.getString("username"),
                    rs.getDouble("wpm"),
                    rs.getDouble("accuracy"),
                    rs.getTimestamp("timestamp")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderboardData;
    }
}