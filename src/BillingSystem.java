import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BillingSystem extends JFrame {
    private JTextField itemCodeField, quantityField;
    private JTextArea billArea;
    private JLabel totalCostLabel;
    private double totalCost = 0.0;
    private User user;

    public BillingSystem(User user) {
        this.user = user;
        setTitle("Billing System - " + user.getUsername());
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("Item Code:"));
        itemCodeField = new JTextField();
        panel.add(itemCodeField);

        panel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        JButton addItemBtn = new JButton("Add Item");
        addItemBtn.addActionListener(e -> addItemToBill());
        panel.add(addItemBtn);

        totalCostLabel = new JLabel("Total Cost: $0.00");
        panel.add(totalCostLabel);

        billArea = new JTextArea();
        billArea.setEditable(false);

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(billArea), BorderLayout.CENTER);

        setVisible(true);
    }

    private void addItemToBill() {
        String itemCode = itemCodeField.getText();
        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM inventory WHERE item_code = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String itemName = rs.getString("item_name");
                double price = rs.getDouble("price");
                int availableQuantity = rs.getInt("quantity");

                if (quantity > availableQuantity) {
                    JOptionPane.showMessageDialog(this, "Insufficient quantity!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    double itemTotal = quantity * price;
                    totalCost += itemTotal;

                    billArea.append(itemName + " x " + quantity + " = $" + String.format("%.2f", itemTotal) + "\n");
                    totalCostLabel.setText("Total Cost: $" + String.format("%.2f", totalCost));

                    // Update inventory
                    String updateQuery = "UPDATE inventory SET quantity = quantity - ? WHERE item_code = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, quantity);
                    updateStmt.setString(2, itemCode);
                    updateStmt.executeUpdate();

                    // Insert sale record
                    insertSaleRecord(itemCode, itemName, quantity, itemTotal);

                    // Show notification
                    JOptionPane.showMessageDialog(this, "Item added to bill!", "Notification", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Item not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertSaleRecord(String itemCode, String itemName, int quantity, double itemTotal) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertQuery = "INSERT INTO sales (item_code, item_name, quantity, total, cashier_username) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, itemCode);
            insertStmt.setString(2, itemName);
            insertStmt.setInt(3, quantity);
            insertStmt.setDouble(4, itemTotal);
            insertStmt.setString(5, user.getUsername()); // Insert cashier's username
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
