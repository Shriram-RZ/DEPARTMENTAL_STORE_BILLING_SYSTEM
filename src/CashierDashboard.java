import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CashierDashboard extends JFrame {
    private User user;

    public CashierDashboard(User user) {
        this.user = user;
        setTitle("Cashier Dashboard - " + user.getUsername());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with BoxLayout for vertical stacking
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Create buttons with styled appearance
        JButton billingBtn = createStyledButton("Billing System", e -> new BillingSystem(user));
        JButton logoutBtn = createStyledButton("Logout", e -> logout());

        panel.add(billingBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between buttons
        panel.add(logoutBtn);

        add(panel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(new Color(30, 144, 255)); // Dodger Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor on hover
        button.addActionListener(action);
        button.setPreferredSize(new Dimension(150, 40)); // Set button size
        return button;
    }

    private void logout() {
        new LoginPage();
        dispose();
    }
}
