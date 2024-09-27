import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class GenerateReports extends JFrame {
    public GenerateReports() {
        setTitle("Generate Reports");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton dailyReportBtn = new JButton("Generate Daily Report");
        dailyReportBtn.addActionListener(e -> generateReport("daily"));

        JButton weeklyReportBtn = new JButton("Generate Weekly Report");
        weeklyReportBtn.addActionListener(e -> generateReport("weekly"));

        JButton monthlyReportBtn = new JButton("Generate Monthly Report");
        monthlyReportBtn.addActionListener(e -> generateReport("monthly"));

        panel.add(dailyReportBtn);
        panel.add(weeklyReportBtn);
        panel.add(monthlyReportBtn);

        add(panel);
        setVisible(true);
    }

    private void generateReport(String reportType) {
        // Logic to generate the specific report (daily, weekly, monthly)
        // This would typically involve querying sales records from the database.
    }
}
