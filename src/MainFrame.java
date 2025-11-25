import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel; 
    private JPanel loginPanel; 
    private JPanel menuPanel; 


    public MainFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(800, 600);

        // CardLayout sebagai pengendali panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Inisialisasi panel-panel
        loginPanel = new LoginPanel(this);
        menuPanel = new MenuPanel(this);

        // Daftarkan panel ke dalam CardLayout
        mainPanel.add(loginPanel, "login");
        mainPanel.add(menuPanel, "menu");

        // Panel pertama yang ditampilkan
        cardLayout.show(mainPanel, "login");


        this.setContentPane(mainPanel);
        this.setTitle("Keystrike Apocalypse");
        this.setVisible(true);
    }

    // Method helper untuk mengganti panel
    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }
    
}
