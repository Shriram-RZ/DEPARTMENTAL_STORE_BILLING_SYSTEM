import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StoreManagerDashboard extends JFrame {
    private User user;

    public StoreManagerDashboard(User user) {
        this.user = user;
        setTitle("Store Manager Dashboard - " + user.getUsername());
        setSize(800, 600); // Adjusted size for better visibility
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Table to display inventory
        String[] columnNames = {"Item Code", "Item Name", "Quantity", "Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable inventoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Load inventory data into the table
        loadInventoryData(tableModel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton manageInventoryBtn = new JButton("Manage Inventory");
        manageInventoryBtn.addActionListener(e -> new InventoryManagement());
        
        JButton logoutBtn = new JButton("Logout");
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

    private void logout() {
        new LoginPage(); // Assuming LoginPage is a valid class
        dispose();
    }

    public static void main(String[] args) {
        User managerUser = new User("store_manager"); // Example user
        new StoreManagerDashboard(managerUser);
    }
}
