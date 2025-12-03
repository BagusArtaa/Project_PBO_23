import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

import dao.ScoreDAO;
import model.Score;

public class LeaderboardPanel extends JPanel {

    private MainFrame frame;

    private DefaultTableModel tableModel;
    private JTable table;
    private ScoreDAO scoreDAO;

    private JLabel heading;
    private JButton btnBackToMenu;

    private Image backgroundImage;
    private ImageIcon buttonIcon;

    public LeaderboardPanel(MainFrame frame) {
        this.frame = frame;

        // === Load background ===
        backgroundImage = new ImageIcon("assets/background.png").getImage();

        // === Load & resize button icon ===
        ImageIcon rawIcon = new ImageIcon("assets/button.png");
        Image scaled = rawIcon.getImage().getScaledInstance(250, 60, Image.SCALE_SMOOTH);
        buttonIcon = new ImageIcon(scaled);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);

        // === Heading Label ===
        heading = new JLabel("Top 10 Leaderboard");
        heading.setFont(new Font("Monospaced", Font.BOLD, 34));
        heading.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 20, 10);
        add(heading, gbc);

        scoreDAO = new ScoreDAO();

        // === Table Model ===
        tableModel = new DefaultTableModel(new String[]{
                "User", "Score", "WPM", "Accuracy (%)", "Time (sec)"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable();   // <<< STYLE BARU DI SINI

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(scroll, gbc);

        // ==== BUTTON BACK ====
        btnBackToMenu = createImageButton("Back to Menu");
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        add(btnBackToMenu, gbc);

        addListeners();
    }

    private void styleTable() {

        // === Background warna gelap untuk isi tabel ===
        Color rowColor = new Color(90, 90, 90);        // warna background row
        Color textColor = Color.WHITE;                 // warna text
    
        table.setFont(new Font("SansSerif", Font.PLAIN, 18));
        table.setRowHeight(32);
        table.setForeground(textColor);
        table.setBackground(rowColor);                 // ← background solid
    
        // === Center all cells ===
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        center.setBackground(rowColor);                
        center.setForeground(textColor);
        center.setOpaque(true);                        
    
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    
        // === Header style ===
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBackground(new Color(51, 51, 51));  
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(100, 40));
    
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(false);
        table.setGridColor(new Color(255, 255, 255));  
        table.setIntercellSpacing(new Dimension(1, 1));

        header.setReorderingAllowed(false);   // Mencegah user memindah kolom
    }
    

    private JButton createImageButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Serif", Font.BOLD, 20));
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

    public void refreshLeaderboard() {
        List<Score> list = scoreDAO.getTopScores(10);

        tableModel.setRowCount(0);

        for (Score s : list) {
            tableModel.addRow(new Object[]{
                    s.getUsername(),
                    s.getScore(),
                    s.getWpm(),
                    String.format("%.2f", s.getAccuracy()),
                    s.getTimeSurvive()
            });
        }
    }

    private void addListeners() {
        btnBackToMenu.addActionListener(e -> frame.showPanel("menu"));
    }
}
