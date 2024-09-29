import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private String role;

    public LoginPage(String role) {
        this.role = role;
        setTitle("DEPARTMENTAL STORES - " + role + " Login");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> handleLogin());
        panel.add(loginBtn);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> goBack());
        panel.add(backBtn);

        add(panel, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (authenticateUser(username, password, role)) {
            User user = new User(username, role);
            switch (role) {
                case "ADMIN":
                    new AdminDashboard(user);
                    break;
                case "STORE MANAGER":
                    new StoreManagerDashboard(user);
                    break;
                case "CASHIER":
                    new CashierDashboard(user);
                    break;
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean authenticateUser(String username, String password, String role) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password); // Consider hashing the password
            stmt.setString(3, role);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void goBack() {
        new LoginAsPage();
        dispose();
    }
}
