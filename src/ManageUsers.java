import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageUsers extends JFrame {
    private JTextField usernameField, roleField, passwordField;

    public ManageUsers() {
        setTitle("Manage Users");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Role:"));
        roleField = new JTextField();
        panel.add(roleField);

        panel.add(new JLabel("Password:"));
        passwordField = new JTextField();
        panel.add(passwordField);

        JButton addUserBtn = new JButton("Add User");
        addUserBtn.addActionListener(e -> addUser());

        JButton deleteUserBtn = new JButton("Delete User");
        deleteUserBtn.addActionListener(e -> deleteUser());

        panel.add(addUserBtn);
        panel.add(deleteUserBtn);

        add(panel);
        setVisible(true);
    }

    private void addUser() {
        String username = usernameField.getText();
        String role = roleField.getText();
        String password = passwordField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, role, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, role);
            stmt.setString(3, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteUser() {
        String username = usernameField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
