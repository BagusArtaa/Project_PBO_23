import javax.swing.*;
import java.awt.*;

import dao.UserDAO;

public class LoginPanel extends JPanel {

    private MainFrame frame;

    private JTextField userField;
    private JPasswordField passField;
    private JButton btnLogin;
    private JButton btnRegister;

    private UserDAO dao;

    // Background
    private Image backgroundImage;

    // Button image
    private ImageIcon buttonIcon;

    public LoginPanel(MainFrame frame) {
        this.frame = frame;
        this.dao  = new UserDAO();

        // Load background
        backgroundImage = new ImageIcon("assets/background.png").getImage();

        // --- Load dan resize button image ---
        ImageIcon rawIcon = new ImageIcon("assets/button.png");
        Image scaled = rawIcon.getImage().getScaledInstance(200, 60, Image.SCALE_SMOOTH);
        buttonIcon = new ImageIcon(scaled);

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to Keystrike Apocalypse");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 30, 10);
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        userLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(userLabel, gbc);

        userField = new JTextField(20);
        userField.setFont(new Font("Serif", Font.PLAIN, 14));
        userField.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(passLabel, gbc);

        passField = new JPasswordField(20);
        passField.setFont(new Font("Serif", Font.PLAIN, 14));
        passField.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(passField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 10));

        // --- BUTTON LOGIN ---
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Serif", Font.BOLD, 18));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setHorizontalTextPosition(JButton.CENTER);
        btnLogin.setIcon(buttonIcon);
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setFocusPainted(false);

        // --- BUTTON REGISTER ---
        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Serif", Font.BOLD, 18));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setHorizontalTextPosition(JButton.CENTER);
        btnRegister.setIcon(buttonIcon);
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setFocusPainted(false);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 10, 5);
        add(buttonPanel, gbc);

        addListeners();
    }

    // Background draw
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void addListeners(){

        btnLogin.addActionListener(e -> {

            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Username dan password wajib diisi.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int userId = dao.loginUser(username, password);

            if (userId != -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Login berhasil!.",
                        "Login Sukses",
                        JOptionPane.INFORMATION_MESSAGE);

                frame.showPanel("menu");

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Username atau password salah.",
                        "Login gagal",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRegister.addActionListener(e -> {

            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Username dan password wajib diisi.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean registered = dao.registerUser(username, password);

            if (registered) {
                userField.setText("");
                passField.setText("");
                JOptionPane.showMessageDialog(this,
                        "Registrasi berhasil! Silahkan login.",
                        "Registrasi Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registrasi gagal. Username mungkin sudah digunakan.",
                        "Registrasi Gagal",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
