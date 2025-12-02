import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {

    private MainFrame frame;

    private JLabel finalWpmLabel;
    private JLabel finalAccuracyLabel;
    private JLabel finalTimeSurviveLabel;
    private JLabel finalScore;
    private JButton btnPlayAgain;
    private JButton btnBackToMenu;

    private Image backgroundImage;
    private ImageIcon buttonIcon;

    public ScorePanel(MainFrame frame) {
        this.frame = frame;

        // ==== LOAD BACKGROUND ====
        backgroundImage = new ImageIcon("assets/background.png").getImage();

        // ==== LOAD & RESIZE BUTTON IMAGE ====
        ImageIcon rawButton = new ImageIcon("assets/button.png");
        Image resized = rawButton.getImage().getScaledInstance(220, 60, Image.SCALE_SMOOTH);
        buttonIcon = new ImageIcon(resized);

        // ==== LAYOUT ====
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // ==== TITLE ====
        JLabel title = new JLabel("Game Over!");
        title.setFont(new Font("Monospaced", Font.BOLD, 38));
        title.setForeground(Color.WHITE);
        gbc.gridy = 0;
        this.add(title, gbc);

        // ==== LABEL WPM ====
        finalWpmLabel = createLabel("WPM: 0", 24);
        gbc.gridy = 1;
        this.add(finalWpmLabel, gbc);

        // ==== ACCURACY ====
        finalAccuracyLabel = createLabel("Accuracy: 0.00%", 24);
        gbc.gridy = 2;
        this.add(finalAccuracyLabel, gbc);

        // ==== TIME SURVIVE ====
        finalTimeSurviveLabel = createLabel("Time Survive: 0.00s", 24);
        gbc.gridy = 3;
        this.add(finalTimeSurviveLabel, gbc);

        // ==== FINAL SCORE ====
        finalScore = createLabel("Final Score: 0", 30);
        gbc.gridy = 4;
        this.add(finalScore, gbc);

        // ==== BUTTON PLAY AGAIN ====
        btnPlayAgain = createImageButton("Play Again");
        gbc.gridy = 5;
        this.add(btnPlayAgain, gbc);

        // ==== BUTTON BACK ====
        btnBackToMenu = createImageButton("Back to Menu");
        gbc.gridy = 6;
        this.add(btnBackToMenu, gbc);

        addListeners();
    }

    private JLabel createLabel(String text, int fontSize) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Serif", Font.BOLD, fontSize));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private JButton createImageButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Serif", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setHorizontalTextPosition(JButton.CENTER);
        btn.setIcon(buttonIcon);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    public void setScore(int value) {
        finalScore.setText("Final Score: " + value);
    }

    public void setWpm(double wpm) {
        finalWpmLabel.setText(String.format("WPM: %.2f", wpm));
    }

    public void setAccuracy(double accuracyPercent) {
        finalAccuracyLabel.setText(String.format("Accuracy: %.2f%%", accuracyPercent));
    }

    public void setTimeSurvive(double seconds) {
        finalTimeSurviveLabel.setText(String.format("Time Survive: %.2fs", seconds));
    }

    private void addListeners() {
        btnPlayAgain.addActionListener(e -> frame.startGame());
        btnBackToMenu.addActionListener(e -> frame.showPanel("menu"));
    }
}
