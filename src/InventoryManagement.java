import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventoryManagement extends JFrame {
    JTextField itemCodeField, itemNameField, priceField, quantityField;

    public InventoryManagement() {
        setTitle("Inventory Management");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2));

        panel.add(new JLabel("Item Code:"));
        itemCodeField = new JTextField();
        panel.add(itemCodeField);

        panel.add(new JLabel("Item Name:"));
        itemNameField = new JTextField();
        panel.add(itemNameField);

        panel.add(new JLabel("Price:"));
        priceField = new JTextField();
        panel.add(priceField);

        panel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        JButton addItemBtn = new JButton("Add Item");
        addItemBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        });
        panel.add(addItemBtn);

        add(panel);
        setVisible(true);
    }

    public void addItem() {
        String itemCode = itemCodeField.getText();
        String itemName = itemNameField.getText();
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO inventory (item_code, item_name, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemCode);
            stmt.setString(2, itemName);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
