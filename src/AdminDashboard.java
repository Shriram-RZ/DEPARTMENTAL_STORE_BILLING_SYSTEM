import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private User user;

    public AdminDashboard(User user) {
        this.user = user;
        setTitle("Admin Dashboard - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(800, 600); // Set a fixed size for the window

        // Main panel layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome Admin", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Notification button
        JButton notificationBtn = new JButton("Notifications");
        notificationBtn.setBackground(Color.YELLOW);
        notificationBtn.addActionListener(e -> showNotification());

        // Notification panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(notificationBtn);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center align buttons

        // Create regular buttons
        JButton salesReportsBtn = new JButton("Sales Reports");
        salesReportsBtn.addActionListener(e -> fetchSalesReports());

        JButton databaseDetailsBtn = new JButton("Database Details");
        databaseDetailsBtn.addActionListener(e -> fetchDatabaseDetails());
        
        // New button for inventory details
        JButton inventoryDetailsBtn = new JButton("Inventory Details");
        inventoryDetailsBtn.addActionListener(e -> fetchInventoryDetails());

        JButton userDetailsBtn = new JButton("User Details and Roles");
        userDetailsBtn.addActionListener(e -> fetchUserDetails());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        // Add buttons to the button panel
        buttonPanel.add(salesReportsBtn);
        buttonPanel.add(databaseDetailsBtn);
        buttonPanel.add(inventoryDetailsBtn);
        buttonPanel.add(userDetailsBtn);
        buttonPanel.add(logoutBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add main panel to the frame
        add(mainPanel);
        setVisible(true);
    }

    private void showNotification() {
        String notifications = fetchNotifications();
        JOptionPane.showMessageDialog(this, notifications.isEmpty() ? "No new notifications." : notifications, "Notifications", JOptionPane.INFORMATION_MESSAGE);
        markNotificationsAsSeen(); // Mark notifications as seen after displaying
    }

    private String fetchNotifications() {
        StringBuilder notificationList = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM notifications WHERE seen = false"; // Example notification query
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String message = rs.getString("message");
                notificationList.append(message).append("\n");
            }
        } catch (SQLException e) {
            showError("Failed to fetch notifications: " + e.getMessage());
        }
        return notificationList.toString();
    }

    private void markNotificationsAsSeen() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE notifications SET seen = true WHERE seen = false"; // Mark all unseen notifications as seen
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showError("Failed to update notifications: " + e.getMessage());
        }
    }

    private void fetchSalesReports() {
        String[][] salesData = getSalesReports();
        String[] columnNames = { "Sale ID", "Item Code", "Item Name", "Quantity", "Total", "Sale Date", "Cashier" };

        // Create a JTable to display the sales reports
        JTable salesTable = new JTable(salesData, columnNames);
        salesTable.setFillsViewportHeight(true);
        salesTable.setDefaultEditor(Object.class, null); // Make the table read-only

        // Add a scroll pane to the table
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));

        // Display the table in a dialog
        JOptionPane.showMessageDialog(this, scrollPane, "Sales Reports", JOptionPane.INFORMATION_MESSAGE);
    }

    private String[][] getSalesReports() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM sales ORDER BY sale_date DESC";
            PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery();

            // Move the cursor to the last row to get the row count
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst(); // Move back to the start

            // Create data array
            String[][] salesData = new String[rowCount][7];
            int index = 0;

            while (rs.next()) {
                salesData[index][0] = String.valueOf(rs.getInt("id"));
                salesData[index][1] = rs.getString("item_code");
                salesData[index][2] = rs.getString("item_name");
                salesData[index][3] = String.valueOf(rs.getInt("quantity"));
                salesData[index][4] = String.format("$%.2f", rs.getDouble("total"));
                salesData[index][5] = rs.getTimestamp("sale_date").toString();
                salesData[index][6] = rs.getString("cashier_username");
                index++;
            }
            return salesData;
        } catch (SQLException e) {
            showError("Failed to fetch sales reports: " + e.getMessage());
            return new String[0][0]; // Return an empty array in case of error
        }
    }

    private void fetchDatabaseDetails() {
        String details = getDatabaseDetails();
        JOptionPane.showMessageDialog(this, details, "Database Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // New method to fetch and display inventory details
    private void fetchInventoryDetails() {
        String[][] inventoryData = getInventoryDetails();
        String[] columnNames = { "ID", "Item Code", "Item Name", "Price", "Quantity" };

        // Create a JTable to display the inventory
        JTable inventoryTable = new JTable(inventoryData, columnNames);
        inventoryTable.setFillsViewportHeight(true);
        inventoryTable.setDefaultEditor(Object.class, null); // Make the table read-only

        // Add a scroll pane to the table
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));

        // Display the table in a dialog
        JOptionPane.showMessageDialog(this, scrollPane, "Inventory Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private String[][] getInventoryDetails() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM inventory"; // Query to fetch inventory details
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Create a list to hold inventory data
            java.util.List<String[]> inventoryList = new java.util.ArrayList<>();

            while (rs.next()) {
                String[] row = new String[5];
                row[0] = String.valueOf(rs.getInt("id"));
                row[1] = rs.getString("item_code");
                row[2] = rs.getString("item_name");
                row[3] = String.format("$%.2f", rs.getDouble("price"));
                row[4] = String.valueOf(rs.getInt("quantity"));
                inventoryList.add(row);
            }

            // Convert the list to a 2D array
            String[][] inventoryData = new String[inventoryList.size()][5];
            return inventoryList.toArray(inventoryData);
        } catch (SQLException e) {
            showError("Failed to fetch inventory details: " + e.getMessage());
            return new String[0][0]; // Return an empty array in case of error
        }
    }

    private void fetchUserDetails() {
        String[][] userDetails = getUserDetails();
        String[] columnNames = { "User ID", "Username", "Role" };

        // Create a JTable to display user details
        JTable userTable = new JTable(userDetails, columnNames);
        userTable.setFillsViewportHeight(true);
        userTable.setDefaultEditor(Object.class, null); // Make the table read-only

        // Add a scroll pane to the table
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));

        // Display the table in a dialog
        JOptionPane.showMessageDialog(this, scrollPane, "User Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private String[][] getUserDetails() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users"; // Query to fetch user details
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Create a list to hold user details
            java.util.List<String[]> userList = new java.util.ArrayList<>();

            while (rs.next()) {
                String[] row = new String[3];
                row[0] = String.valueOf(rs.getInt("id"));
                row[1] = rs.getString("username");
                row[2] = rs.getString("role");
                userList.add(row);
            }

            // Convert the list to a 2D array
            String[][] userDetails = new String[userList.size()][3];
            return userList.toArray(userDetails);
        } catch (SQLException e) {
            showError("Failed to fetch user details: " + e.getMessage());
            return new String[0][0]; // Return an empty array in case of error
        }
    }

    private String getDatabaseDetails() {
        // Return some details about the database, can be customized
        return "Database Name: YourDatabase\nVersion: 1.0\nTotal Users: " + getTotalUserCount();
    }

    private int getTotalUserCount() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM users";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            showError("Failed to fetch user count: " + e.getMessage());
        }
        return 0;
    }

    private void logout() {
        // Implement logout functionality
        this.dispose(); // Assuming there's a LoginFrame class
    }
3
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        // Example User class instance
        User user = new User("adminUser", "admin");
        new AdminDashboard(user);
    }
}
