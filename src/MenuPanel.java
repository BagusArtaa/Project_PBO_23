import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private MainFrame frame;

    private JLabel welcomeLabel;
    private JButton btnEasy;
    private JButton btnNormal;
    private JButton btnExpert;
    private JButton btnLogout;

    private Image backgroundImage;
    private ImageIcon buttonIcon;

    public MenuPanel(MainFrame frame) {
        this.frame = frame;

        // === Load background ===
        backgroundImage = new ImageIcon("assets/background.png").getImage();

        // === Load & resize button icon ===
        ImageIcon rawIcon = new ImageIcon("assets/button.png");
        Image scaled = rawIcon.getImage().getScaledInstance(250, 60, Image.SCALE_SMOOTH);
        buttonIcon = new ImageIcon(scaled);

        // === Layout ===
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10); 


        // === Welcome Label ===
        welcomeLabel = new JLabel("Selamat Datang, User!");
        welcomeLabel.setFont(new Font("Monospaced", Font.BOLD, 34)); // Ubah font
        welcomeLabel.setForeground(Color.WHITE); // Ubah warna
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 20, 10); 
        add(welcomeLabel, gbc);

        // === Tombol EASY ===
        btnEasy = createMenuButton("Easy");
        gbc.gridy = 1;
        add(btnEasy, gbc);

        // === Tombol NORMAL ===
        btnNormal = createMenuButton("Normal");
        gbc.gridy = 2;
        add(btnNormal, gbc);

        // === Tombol EXPERT ===
        btnExpert = createMenuButton("Expert");
        gbc.gridy = 3;
        add(btnExpert, gbc);

        btnLogout = createSmallButton("Logout");
        gbc.gridy = 5;
        add(btnLogout, gbc);

        addListeners();
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Serif", Font.BOLD, 22)); // Ubah font
        btn.setForeground(Color.WHITE);                 // Warna teks
        btn.setHorizontalTextPosition(JButton.CENTER);
        btn.setPreferredSize(new Dimension(250, 60));   // Ukuran tombol
        btn.setIcon(buttonIcon);                        // Gunakan tombol gambar
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Serif", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);

        // Resize icon sesuai ukuran logout button kecil
        ImageIcon rawIcon = new ImageIcon("assets/button.png");
        Image smallImg = rawIcon.getImage().getScaledInstance(150, 45, Image.SCALE_SMOOTH);
        btn.setIcon(new ImageIcon(smallImg));

        btn.setPreferredSize(new Dimension(150, 45));
        btn.setHorizontalTextPosition(JButton.CENTER);
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

    private void addListeners() {
        btnEasy.addActionListener(e -> frame.startGame(1));
        btnNormal.addActionListener(e -> frame.startGame(2));
        btnExpert.addActionListener(e -> frame.startGame(3));

        btnLogout.addActionListener(e -> frame.showPanel("login"));
    }
}
