import javax.swing.*;

public class GenerateReports extends JFrame {
    public GenerateReports() {
        setTitle("Generate Reports");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();

        JButton dailyReportBtn = new JButton("Daily Report");
        JButton weeklyReportBtn = new JButton("Weekly Report");
        JButton monthlyReportBtn = new JButton("Monthly Report");

        panel.add(dailyReportBtn);
        panel.add(weeklyReportBtn);
        panel.add(monthlyReportBtn);

        add(panel);
        setVisible(true);
    }
}
