import javax.swing.*;
import java.awt.*;

import dao.UserDAO;

public class LoginPanel extends JPanel {

    private JTextField userField;
    private JPasswordField passField;
    private JButton btnLogin;
    private JButton btnRegister;

    private UserDAO dao;

    public LoginPanel(MainFrame frame) {
        this.dao  = new UserDAO();
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to Keystrike Apocalypse");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 30, 10);
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(userLabel, gbc);

        userField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(passLabel, gbc);

        passField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(passField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 10));

        btnLogin = new JButton("Login");
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(200, 40));

        btnRegister = new JButton("Register");
        btnRegister.setFocusPainted(false);
        btnRegister.setPreferredSize(new Dimension(200, 40));


        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 10, 5);
        add(buttonPanel, gbc);

        addListeners(frame);
    }

    private void addListeners(MainFrame frame){
        // --- Action Listener Login ---
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

        // --- Action Listener Register ---
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
