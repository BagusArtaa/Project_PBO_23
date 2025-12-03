import javax.swing.*;
import java.awt.*;

import model.User;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private ScorePanel scorePanel;
    private LeaderboardPanel leaderboardPanel;

    private User loggedUser;

    private int lastDifficulty = -1;

    public MainFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // CardLayout sebagai pengendali panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Inisialisasi panel-panel
        loginPanel = new LoginPanel(this);
        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);
        scorePanel = new ScorePanel(this);
        leaderboardPanel = new LeaderboardPanel(this);

        // Daftarkan panel ke dalam CardLayout
        mainPanel.add(loginPanel, "login");
        mainPanel.add(menuPanel, "menu");
        mainPanel.add(gamePanel, "game");
        mainPanel.add(scorePanel, "score");
        mainPanel.add(leaderboardPanel, "leaderboard");

        // Panel pertama yang ditampilkan
        cardLayout.show(mainPanel, "login");

        this.setContentPane(mainPanel);

        this.setTitle("Keystrike Apocalypse");
        this.setSize(914, 637);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    // Method helper untuk mengganti panel
    public void showPanel(String name) {
        if (name.equals("leaderboard")) {
            leaderboardPanel.refreshLeaderboard();
        }
        cardLayout.show(mainPanel, name);
    }

    public void showMenu() {
        if (loggedUser != null) {
            menuPanel.updateWelcome(loggedUser.getUsername());
        }
        showPanel("menu");
    }

    public void startGame(int maxDifficulty) {
        this.lastDifficulty = maxDifficulty;
        gamePanel.setMaxDifficulty(maxDifficulty);

        showPanel("game");

        gamePanel.resetGame();
        gamePanel.requestFocusInWindow();
        gamePanel.startCountdown();
    }

    public void startGame() {
        if (lastDifficulty == -1) {
            showPanel("menu");
            return;
        }

        startGame(lastDifficulty);
    }

    public ScorePanel getScorePanel() {
        return scorePanel;
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

}
