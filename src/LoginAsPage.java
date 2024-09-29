import javax.swing.*;
import java.awt.*;

public class LoginAsPage extends JFrame {

    public LoginAsPage() {
        setTitle("DEPARTMENTAL STORES - Login As");
        setSize(300, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel headingLabel = new JLabel("LOGIN AS", SwingConstants.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(headingLabel);

        String[] roles = {"ADMIN", "STORE MANAGER", "CASHIER"};
        for (String role : roles) {
            JButton button = new JButton(role);
            button.addActionListener(e -> openLoginPage(role));
            panel.add(button);
        }

        add(panel, BorderLayout.CENTER);
    }

    private void openLoginPage(String role) {
        new LoginPage(role);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginAsPage::new);
    }
}
