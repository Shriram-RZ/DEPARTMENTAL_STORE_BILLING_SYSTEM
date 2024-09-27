import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventoryManagement extends JFrame {
    private JTextField itemCodeField, itemNameField, quantityField, priceField;

    public InventoryManagement() {
        setTitle("Inventory Management");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with BoxLayout for vertical stacking
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Create text fields with labels
        itemCodeField = createTextField(panel, "Item Code:");
        itemNameField = createTextField(panel, "Item Name:");
        quantityField = createTextField(panel, "Quantity:");
        priceField = createTextField(panel, "Price:");

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Create and style buttons
        JButton addProductBtn = createButton("Add Product", e -> addProduct());
        JButton editProductBtn = createButton("Edit Product", e -> editProduct());
        JButton deleteProductBtn = createButton("Delete Product", e -> deleteProduct());

        buttonPanel.add(addProductBtn);
        buttonPanel.add(editProductBtn);
        buttonPanel.add(deleteProductBtn);

        panel.add(buttonPanel);
        add(panel);
        setVisible(true);
    }

    private JTextField createTextField(JPanel panel, String labelText) {
        JPanel textFieldPanel = new JPanel();
        textFieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 25)); // Set label width

        JTextField textField = new JTextField(15);
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        textField.setPreferredSize(new Dimension(150, 25)); // Set text field width
        textFieldPanel.add(label);
        textFieldPanel.add(textField);
        
        panel.add(textFieldPanel); // Add the text field panel to the main panel
        return textField; // Return the JTextField for later use
    }

    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(new Color(30, 144, 255)); // Dodger Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Padding
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor on hover
        button.addActionListener(action);
        button.setPreferredSize(new Dimension(150, 30)); // Set button size
        return button;
    }

    private void addProduct() {
        String itemCode = itemCodeField.getText();
        String itemName = itemNameField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        double price = Double.parseDouble(priceField.getText());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO inventory (item_code, item_name, quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemCode);
            stmt.setString(2, itemName);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product added successfully!");
            clearFields(); // Clear fields after successful addition
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editProduct() {
        String itemCode = itemCodeField.getText();
        String itemName = itemNameField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        double price = Double.parseDouble(priceField.getText());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE inventory SET item_name = ?, quantity = ?, price = ? WHERE item_code = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemName);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, price);
            stmt.setString(4, itemCode);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product updated successfully!");
            clearFields(); // Clear fields after successful update
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteProduct() {
        String itemCode = itemCodeField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM inventory WHERE item_code = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemCode);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product deleted successfully!");
            clearFields(); // Clear fields after successful deletion
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        itemCodeField.setText("");
        itemNameField.setText("");
        quantityField.setText("");
        priceField.setText("");
    }

    public static void main(String[] args) {
        new InventoryManagement();
    }
}
