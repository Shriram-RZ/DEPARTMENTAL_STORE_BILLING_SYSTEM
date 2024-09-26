import javax.swing.*;

public class StoreManagerDashboard extends JFrame {
    public StoreManagerDashboard() {
        setTitle("Store Manager Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton manageInventoryBtn = new JButton("Manage Inventory");
        manageInventoryBtn.addActionListener(e -> new InventoryManagement());

        JPanel panel = new JPanel();
        panel.add(manageInventoryBtn);

        add(panel);
        setVisible(true);
    }
}
