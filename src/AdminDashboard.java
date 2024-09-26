import javax.swing.*;
import java.awt.event.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton manageUsersBtn = new JButton("Manage Users");
        manageUsersBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageUsers();
            }
        });

        JButton generateReportsBtn = new JButton("Generate Reports");
        generateReportsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GenerateReports();
            }
        });

        JButton manageInventoryBtn = new JButton("Manage Inventory");
        manageInventoryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InventoryManagement();
            }
        });

        JPanel panel = new JPanel();
        panel.add(manageUsersBtn);
        panel.add(generateReportsBtn);
        panel.add(manageInventoryBtn);

        add(panel);
        setVisible(true);
    }
}
