import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePage extends JFrame {
    public WelcomePage() {
        setTitle("Welcome");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to the Departmental Store Billing System", SwingConstants.CENTER);
        panel.add(welcomeLabel, BorderLayout.CENTER);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the current WelcomePage
                dispose();
                // Open the LoginPage
                SwingUtilities.invokeLater(() -> new LoginAsPage());
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}
