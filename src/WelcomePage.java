import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePage extends JFrame {
    public WelcomePage() {
        setTitle("Welcome");
        // Set to full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a panel with a background color
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY); // Change to your preferred color

        // Welcome label with customized font and color
        JLabel welcomeLabel = new JLabel("Welcome to the Departmental Store Billing System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36)); // Increase font size
        welcomeLabel.setForeground(Color.DARK_GRAY); // Change text color
        panel.add(welcomeLabel, BorderLayout.CENTER);

        // Create a button with customized size and font
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 24)); // Increase button font size
        loginButton.setPreferredSize(new Dimension(200, 50)); // Set preferred button size
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the current WelcomePage
                dispose();
                // Open the LoginPage
                SwingUtilities.invokeLater(() -> new LoginAsPage());
            }
        });

        // Panel for button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY); // Match panel background
        buttonPanel.add(loginButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        // Start the application
        SwingUtilities.invokeLater(WelcomePage::new);
    }
}
