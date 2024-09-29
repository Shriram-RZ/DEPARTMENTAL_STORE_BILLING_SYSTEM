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
        setLookAndFeel();

        // Main panel with BorderLayout for header and main content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 144, 255)); // Dodger Blue
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(welcomeLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Main content panel with BoxLayout for vertical stacking
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Create buttons with styled appearance
        JButton billingBtn = createStyledButton("Billing System", e -> new BillingSystem(user));
        JButton logoutBtn = createStyledButton("Logout", e -> logout());

        buttonPanel.add(billingBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between buttons
        buttonPanel.add(logoutBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(new Color(30, 144, 255)); // Dodger Blue
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor on hover
        button.addActionListener(action);
        button.setPreferredSize(new Dimension(150, 40)); // Set button size
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Font style for buttons
        return button;
    }

    private void logout() {
        new LoginAsPage(); // Ensure this class exists
        dispose();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
}
