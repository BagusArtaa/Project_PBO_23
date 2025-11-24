import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class LeaderboardPanel extends JPanel {

    private Main mainApp;
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;

    public LeaderboardPanel(Main mainApp) {
        this.mainApp = mainApp;
        
        setBackground(Theme.DARK_GRAY);
        setLayout(new BorderLayout(10, 10));
        
        JLabel titleLabel = new JLabel("Top 10 Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.YELLOW);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Username", "WPM", "Akurasi (%)", "Waktu"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        leaderboardTable = new JTable(tableModel);
        
        styleTable();
        
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.getViewport().setBackground(Theme.DARK_GRAY);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("« Kembali ke Menu");
        backButton.setBackground(Theme.YELLOW);
        backButton.setForeground(Color.BLACK);
        backButton.setFont(Theme.BOLD_FONT);
        backButton.setFocusPainted(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Theme.DARK_GRAY);
        buttonPanel.add(backButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        backButton.addActionListener(e -> mainApp.showPanel("MENU"));
    }
    
    private void styleTable() {
        leaderboardTable.setBackground(Theme.DARK_GRAY);
        leaderboardTable.setForeground(Theme.LIGHT_GRAY);
        leaderboardTable.setFont(Theme.MAIN_FONT);
        leaderboardTable.setRowHeight(30);
        leaderboardTable.setFillsViewportHeight(true);
        
        JTableHeader header = leaderboardTable.getTableHeader();
        header.setBackground(Theme.YELLOW);
        header.setForeground(Color.BLACK);
        header.setFont(Theme.BOLD_FONT);
        header.setPreferredSize(new Dimension(100, 40));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setBackground(Theme.DARKER_GRAY);
        centerRenderer.setForeground(Theme.LIGHT_GRAY);
        
        for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public void onPanelShown() {
        tableModel.setRowCount(0);
        
        List<Object[]> data = mainApp.getKoneksi().getLeaderboard();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (Object[] row : data) {
            String username = (String) row[0];
            double wpm = (Double) row[1];
            double accuracy = (Double) row[2];
            Timestamp timestamp = (Timestamp) row[3];
            
            tableModel.addRow(new Object[]{
                username,
                String.format("%.0f", wpm),
                String.format("%.2f%%", accuracy),
                dateFormat.format(timestamp)
            });
        }
    }
}