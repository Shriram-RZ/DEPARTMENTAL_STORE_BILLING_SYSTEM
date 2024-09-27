import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Notification extends JFrame {
    public Notification() {
        setTitle("Notifications");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea notificationArea = new JTextArea();
        notificationArea.setEditable(false);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM inventory WHERE quantity < 10";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String itemName = rs.getString("item_name");
                int quantity = rs.getInt("quantity");
                notificationArea.append(itemName + " is low on stock (" + quantity + " left)\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        add(new JScrollPane(notificationArea), BorderLayout.CENTER);
        setVisible(true);
    }
}
