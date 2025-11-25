import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private JLabel welcomeLabel;
    private JButton btnStart;
    private JButton bntLeaderboard;
    private JButton btnLogout;

    public MenuPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        welcomeLabel = new JLabel("Selamat Datang, User!");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 30, 10);
        add(welcomeLabel, gbc);
        
        gbc.insets = new Insets(10, 10, 10, 10);
        
        btnStart = new JButton("Mulai Main");
        btnStart.setFocusPainted(false);
        btnStart.setPreferredSize(new Dimension(250, 50));
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(btnStart, gbc);
        
        bntLeaderboard = new JButton("Lihat Leaderboard");
        bntLeaderboard.setFocusPainted(false);
        bntLeaderboard.setPreferredSize(new Dimension(250, 50));
        gbc.gridy = 2;
        add(bntLeaderboard, gbc);
        
        btnLogout = new JButton("Logout");
        btnLogout.setFocusPainted(false);
        btnLogout.setPreferredSize(new Dimension(100, 30));

        gbc.gridy = 3;
        add(btnLogout, gbc);
        
        addListeners(frame);
    }
 
    
    private void addListeners(MainFrame frame) {
        btnStart.addActionListener(e -> frame.showPanel("game"));
        bntLeaderboard.addActionListener(e -> frame.showPanel("leaderboard"));
        btnLogout.addActionListener(e -> frame.showPanel("login"));
    }
}