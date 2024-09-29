import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventoryManagement extends JFrame {
    private JTextField itemCodeField, itemNameField, quantityField, priceField;
    private JComboBox<String> itemSelector; // Combo box for selecting items

    public InventoryManagement() {
        setTitle("Inventory Management");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with GridBagLayout for better organization
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Create text fields with labels and store the JTextField references
        itemCodeField = createTextField(panel, "Item Code:", 0);
        itemNameField = createTextField(panel, "Item Name:", 1);
        quantityField = createTextField(panel, "Quantity:", 2);
        priceField = createTextField(panel, "Price:", 3);

        // Button panel with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);

        // Create and style buttons
        JButton addProductBtn = createButton("Add Product", e -> addProduct());
        JButton editProductBtn = createButton("Edit Product", e -> showItemListForEdit());
        JButton deleteProductBtn = createButton("Delete Product", e -> showDeleteConfirmation());

        buttonPanel.add(addProductBtn);
        buttonPanel.add(editProductBtn);
        buttonPanel.add(deleteProductBtn);

        // Add button panel to the main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 4; // Position the button panel at the bottom
        panel.add(buttonPanel, gbc);

        add(panel);
        setVisible(true);
    }

    private JTextField createTextField(JPanel panel, String labelText, int gridY) {
        JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        textFieldPanel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 25)); // Set label width
        label.setFont(new Font("Arial", Font.PLAIN, 14)); // Change font style

        JTextField textField = new JTextField(15);
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        textField.setPreferredSize(new Dimension(150, 25)); // Set text field width
        textField.setFont(new Font("Arial", Font.PLAIN, 14)); // Change font style

        textFieldPanel.add(label);
        textFieldPanel.add(textField);

        // Add the textFieldPanel to the main panel at the specified gridY position
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = gridY; // Use the gridY parameter to position correctly
        gbc.insets = new Insets(5, 5, 5, 5); // Add spacing
        panel.add(textFieldPanel, gbc); // Add the text field panel to the main panel

        return textField; // Return the JTextField for later use
    }

    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(new Color(30, 144, 255)); // Dodger Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor on hover
        button.addActionListener(action);
        button.setPreferredSize(new Dimension(150, 40)); // Set button size

        // Add mouse listeners for hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255)); // Lighter blue on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 144, 255)); // Reset color
            }
        });

        return button;
    }

    private void loadItemsIntoComboBox() {
        itemSelector = new JComboBox<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT item_name FROM inventory";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itemSelector.addItem(rs.getString("item_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showItemListForEdit() {
        loadItemsIntoComboBox();

        JOptionPane.showMessageDialog(this, itemSelector, "Select Item to Edit", JOptionPane.QUESTION_MESSAGE);

        String selectedItem = (String) itemSelector.getSelectedItem();
        if (selectedItem != null) {
            loadItemDetailsForEdit(selectedItem);
        }
    }

    private void loadItemDetailsForEdit(String itemName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT item_code, quantity, price FROM inventory WHERE item_name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String itemCode = rs.getString("item_code");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");

                showEditDialog(itemCode, itemName, quantity, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showEditDialog(String itemCode, String itemName, int quantity, double price) {
        JDialog editDialog = new JDialog(this, "Edit Product", true);
        editDialog.setSize(300, 200);
        editDialog.setLayout(new GridLayout(5, 2));
        editDialog.setLocationRelativeTo(this); // Center the dialog

        JTextField newItemCodeField = new JTextField(itemCode);
        JTextField newItemNameField = new JTextField(itemName);
        JTextField newQuantityField = new JTextField(String.valueOf(quantity));
        JTextField newPriceField = new JTextField(String.valueOf(price));

        editDialog.add(new JLabel("Item Code:"));
        editDialog.add(newItemCodeField);
        editDialog.add(new JLabel("Item Name:"));
        editDialog.add(newItemNameField);
        editDialog.add(new JLabel("Quantity:"));
        editDialog.add(newQuantityField);
        editDialog.add(new JLabel("Price:"));
        editDialog.add(newPriceField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String updatedItemCode = newItemCodeField.getText();
            String updatedItemName = newItemNameField.getText();
            int updatedQuantity = Integer.parseInt(newQuantityField.getText());
            double updatedPrice = Double.parseDouble(newPriceField.getText());

            editProduct(updatedItemCode, updatedItemName, updatedQuantity, updatedPrice);
            editDialog.dispose();
        });

        editDialog.add(saveButton);
        editDialog.setVisible(true);
    }

    private void editProduct(String itemCode, String itemName, int quantity, double price) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE inventory SET item_code = ?, item_name = ?, quantity = ?, price = ? WHERE item_code = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemCode);
            stmt.setString(2, itemName);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            stmt.setString(5, itemCode);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showDeleteConfirmation() {
        String itemName = JOptionPane.showInputDialog(this, "Enter item name to delete:");

        if (itemName != null && !itemName.isEmpty()) {
            deleteProduct(itemName);
        }
    }

    private void deleteProduct(String itemName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM inventory WHERE item_name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemName);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product deleted successfully!");
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
        SwingUtilities.invokeLater(InventoryManagement::new);
    }
}

// DatabaseConnection class for managing database connection
class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        // Replace the details below with your actual database configuration
        String url = "jdbc:mysql://localhost:3306/your_database";
        String username = "root";
        String password = "password";
        return DriverManager.getConnection(url, username, password);
    }
}
