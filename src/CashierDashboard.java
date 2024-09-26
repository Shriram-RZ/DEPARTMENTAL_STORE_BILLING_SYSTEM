import javax.swing.*;

public class CashierDashboard extends JFrame {
    public CashierDashboard() {
        setTitle("Cashier Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton billingBtn = new JButton("Billing");
        billingBtn.addActionListener(e -> new BillingSystem());

        JPanel panel = new JPanel();
        panel.add(billingBtn);

        add(panel);
        setVisible(true);
    }
}
