import java.awt.*;
import javax.swing.*;

public class MainMenuPanel extends JPanel {

    private Main mainApp;
    private JLabel welcomeLabel;
    private JButton startButton;
    private JButton leaderboardButton;
    private JButton logoutButton;

    public MainMenuPanel(Main mainApp) {
        this.mainApp = mainApp;
        
        setBackground(Theme.DARK_GRAY);
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        welcomeLabel = new JLabel("Selamat Datang, User!");
        welcomeLabel.setFont(Theme.TITLE_FONT);
        welcomeLabel.setForeground(Theme.YELLOW);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 30, 10);
        add(welcomeLabel, gbc);
        
        gbc.insets = new Insets(10, 10, 10, 10);
        
        startButton = createStyledButton("Mulai Main");
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(startButton, gbc);
        
        leaderboardButton = createStyledButton("Lihat Leaderboard");
        gbc.gridy = 2;
        add(leaderboardButton, gbc);
        
        logoutButton = LogoutButton("Logout");
        gbc.gridy = 3;
        add(logoutButton, gbc);
        
        addListeners();
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Theme.YELLOW);
        button.setForeground(Color.BLACK);
        button.setFont(Theme.BOLD_FONT);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 50));
        return button;
    }

    private JButton LogoutButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Theme.DARKER_GRAY);
        button.setForeground(Color.WHITE);
        button.setFont(Theme.BOLD_FONT);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 30));
        return button;
    }
    
    
    public void setWelcomeMessage(String username) {
        welcomeLabel.setText("Selamat Datang, " + username + "!");
    }
    
    private void addListeners() {
        startButton.addActionListener(e -> mainApp.showPanel("GAME"));
        leaderboardButton.addActionListener(e -> mainApp.showPanel("LEADERBOARD"));
        logoutButton.addActionListener(e -> mainApp.showPanel("LOGIN"));
    }
}