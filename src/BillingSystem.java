import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BillingSystem extends JFrame {
    JTextField itemCodeField, quantityField;
    JTextArea billArea;

    public BillingSystem() {
        setTitle("Billing System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2));

        panel.add(new JLabel("Item Code:"));
        itemCodeField = new JTextField();
        panel.add(itemCodeField);

        panel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        JButton addItemBtn = new JButton("Add Item");
        addItemBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItemToBill();
            }
        });
        panel.add(addItemBtn);

        billArea = new JTextArea();
        billArea.setEditable(false);
        add(new JScrollPane(billArea), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void addItemToBill() {
        String itemCode = itemCodeField.getText();
        int quantity = Integer.parseInt(quantityField.getText());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM inventory WHERE item_code = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, itemCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String itemName = rs.getString("item_name");
                double price = rs.getDouble("price");
                double total = price * quantity;

                billArea.append(itemName + "\t" + quantity + "\t" + total + "\n");
            } else {
                JOptionPane.showMessageDialog(this, "Item not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
