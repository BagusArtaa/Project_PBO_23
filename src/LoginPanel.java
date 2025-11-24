import java.awt.*;
import javax.swing.*;

public class LoginPanel extends JPanel {

    private Main mainApp;
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginPanel(Main mainApp) {
        this.mainApp = mainApp;

        setBackground(Theme.DARK_GRAY);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to Keystrike Apocalypse");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.YELLOW);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 30, 10);
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(Theme.BOLD_FONT);
        userLabel.setForeground(Theme.LIGHT_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(userLabel, gbc);

        userField = new JTextField(20);
        userField.setFont(Theme.MAIN_FONT);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(Theme.BOLD_FONT);
        passLabel.setForeground(Theme.LIGHT_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(passLabel, gbc);

        passField = new JPasswordField(20);
        passField.setFont(Theme.MAIN_FONT);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(passField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 10));
        buttonPanel.setBackground(Theme.DARK_GRAY);

        loginButton = createStyledButton("Login");
        registerButton = createStyledButton("Register");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 10, 5);
        add(buttonPanel, gbc);

        addListeners();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Theme.YELLOW);
        button.setForeground(Color.BLACK);
        button.setFont(Theme.BOLD_FONT);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }

    private void addListeners() {
        loginButton.addActionListener(e -> processLogin());
        registerButton.addActionListener(e -> processRegister());
        passField.addActionListener(e -> processLogin());
    }

    private void processLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password tidak boleh kosong!");
            return;
        }

        String[] userData = mainApp.getKoneksi().findUser(username, password);
        if (userData != null) {
            int userId = Integer.parseInt(userData[0]);
            mainApp.onLoginSuccess(userId, userData[1]);
        } else {
            JOptionPane.showMessageDialog(this, "Username atau password salah.");
        }
    }

    private void processRegister() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password tidak boleh kosong!");
            return;
        }

        boolean success = mainApp.getKoneksi().registerUser(username, password);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registrasi berhasil! Silakan login.");
            resetField();
        } else {
            JOptionPane.showMessageDialog(this, "Registrasi gagal. Username mungkin sudah ada.");
        }
    }

    public void resetField() {
        userField.setText("");
        passField.setText("");
    }
}
