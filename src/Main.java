import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {

    private CardLayout cardLayout;
    private JPanel containerPanel;
    private LoginPanel loginPanel;
    private MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private LeaderboardPanel leaderboardPanel;
    private KoneksiDatabase koneksi;
    private int currentUserId = -1;
    private String currentUsername = "";

    public Main() {
        koneksi = new KoneksiDatabase();
        
        setTitle("Keystrike Apocalypse");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);
        
        loginPanel = new LoginPanel(this);
        mainMenuPanel = new MainMenuPanel(this);
        gamePanel = new GamePanel(this);
        leaderboardPanel = new LeaderboardPanel(this);
        
        containerPanel.add(loginPanel, "LOGIN");
        containerPanel.add(mainMenuPanel, "MENU");
        containerPanel.add(gamePanel, "GAME");
        containerPanel.add(leaderboardPanel, "LEADERBOARD");
        
        add(containerPanel);
        setVisible(true);
    }

    public void showPanel(String panelName) {
        cardLayout.show(containerPanel, panelName);
        
        if (panelName.equals("LEADERBOARD")) {
            leaderboardPanel.onPanelShown();
        } else if (panelName.equals("GAME")) {
            gamePanel.startCountdown();
        } else if (panelName.equals("LOGIN")) {
            loginPanel.resetField();
            this.currentUserId = -1;
            this.currentUsername = "";
        } else if (panelName.equals("MENU")) {
            gamePanel.stopGame(GameEndType.USER_QUIT, false); 
        }
    }

    public void onLoginSuccess(int userId, String username) {
        this.currentUserId = userId;
        this.currentUsername = username;
        
        mainMenuPanel.setWelcomeMessage(username);
        gamePanel.setCurrentUsername(username);
        
        showPanel("MENU");
    }

    public KoneksiDatabase getKoneksi() {
        return koneksi;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}