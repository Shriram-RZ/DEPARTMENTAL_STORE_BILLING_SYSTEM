import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StoreManagerDashboard extends JFrame {
    private User user;

    public StoreManagerDashboard(User user) {
        this.user = user;
        setTitle("Store Manager Dashboard - " + user.getUsername());
        setSize(900, 600); // Adjusted size for better visibility
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel with padding for a neat layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        
        // Header section
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Store Manager Dashboard", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(30, 144, 255)); // Dodger Blue for modern style
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        
        JLabel subHeaderLabel = new JLabel("Welcome, " + user.getUsername(), JLabel.CENTER);
        subHeaderLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        headerPanel.add(subHeaderLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Table to display inventory with custom design
        String[] columnNames = {"Item Code", "Item Name", "Quantity", "Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable inventoryTable = new JTable(tableModel);
        inventoryTable.setRowHeight(25);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryTable.setGridColor(new Color(200, 200, 200)); // Light gray grid
        
        // Alternate row colors for the table
        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240)); // Alternate row colors
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150))); // Light border around table
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Load inventory data into the table
        loadInventoryData(tableModel);

        // Button panel with styled buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        JButton manageInventoryBtn = new JButton("Manage Inventory");
        styleButton(manageInventoryBtn);
        manageInventoryBtn.addActionListener(e -> new InventoryManagement());
        
        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn);
        logoutBtn.addActionListener(e -> logout());
        
        buttonPanel.add(manageInventoryBtn);
        buttonPanel.add(logoutBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
    }

    private void loadInventoryData(DefaultTableModel tableModel) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT item_code, item_name, quantity, price FROM inventory";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String itemCode = rs.getString("item_code");
                String itemName = rs.getString("item_name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                tableModel.addRow(new Object[]{itemCode, itemName, quantity, price});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(30, 144, 255)); // Dodger Blue
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Hover effect for buttons
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 123, 255)); // Lighter blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(30, 144, 255)); // Reset to default color
            }
        });
    }

    private void logout() {
        // Reset button colors to avoid issues when logging out
        SwingUtilities.invokeLater(() -> {
            // Open the login page
            new LoginAsPage(); // Assuming LoginAsPage is a valid class
            // Close the current dashboard
            dispose();
        });
    }

    
}
