import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageUsers extends JFrame {
    JTextField usernameField, passwordField;
    JComboBox<String> roleBox;

    public ManageUsers() {
        setTitle("Manage Users");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JTextField();
        panel.add(passwordField);

        panel.add(new JLabel("Role:"));
        String[] roles = { "Store Manager", "Cashier" };
        roleBox = new JComboBox<>(roles);
        panel.add(roleBox);

        JButton addUserBtn = new JButton("Add User");
        addUserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });
        panel.add(addUserBtn);

        add(panel);
        setVisible(true);
    }

    public void addUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = (String) roleBox.getSelectedItem();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
