import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;

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

        // Button for adding users
        JButton addUserBtn = new JButton("Add User");
        addUserBtn.addActionListener(e -> addUser());

        // Button for deleting users
        JButton deleteUserBtn = new JButton("Delete User");
        deleteUserBtn.addActionListener(e -> deleteUser());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        // Add buttons to the button panel
        buttonPanel.add(salesReportsBtn);
        buttonPanel.add(databaseDetailsBtn);
        buttonPanel.add(inventoryDetailsBtn);
        buttonPanel.add(userDetailsBtn);
        buttonPanel.add(addUserBtn);
        buttonPanel.add(deleteUserBtn);
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
            // Check if any inventory item's quantity is below 10
            String checkInventoryQuery = "SELECT item_name FROM inventory WHERE quantity < 10";
            PreparedStatement checkStmt = conn.prepareStatement(checkInventoryQuery);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next()) {
                notificationList.append("Warning: Some items are low in stock!\n");
                do {
                    String itemName = checkRs.getString("item_name");
                    notificationList.append("- ").append(itemName).append(" is below 10\n");
                } while (checkRs.next());
            }

            // Now fetch unseen notifications
            String query = "SELECT * FROM notifications WHERE seen = false";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String message = rs.getString("message");
                notificationList.append(message).append("\n");
            }
        } catch (SQLException e) {
            showError("Click OK to get notifications");
        }
        return notificationList.toString();
    }

    private void markNotificationsAsSeen() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE notifications SET seen = true WHERE seen = false"; // Mark all unseen notifications as seen
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showError("Click OK to get notifications");
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
        String query = "SELECT * FROM sales ORDER BY sale_datetime DESC";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        // Collecting data
        ArrayList<String[]> salesDataList = new ArrayList<>();
        
        while (rs.next()) {
            String[] row = new String[7];
            row[0] = String.valueOf(rs.getInt("id"));
            row[1] = rs.getString("item_code");
            row[2] = rs.getString("item_name");
            row[3] = String.valueOf(rs.getInt("quantity"));
            row[4] = String.format("$%.2f", rs.getDouble("total"));
            row[5] = rs.getTimestamp("sale_datetime").toString();
            row[6] = rs.getString("cashier_username");
            salesDataList.add(row);
        }
        
        // Convert ArrayList to String[][]
        return salesDataList.toArray(new String[salesDataList.size()][]);
    } catch (SQLException e) {
        showError("Failed to fetch sales reports: " + e.getMessage());
        return new String[0][0]; // Return an empty array in case of error
    }
}

    private void fetchDatabaseDetails() {
        String details = getDatabaseDetails();
        JOptionPane.showMessageDialog(this, details, "Database Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void fetchInventoryDetails() {
        String[][] inventoryData = getInventoryDetails();
        String[] columnNames = { "Item Code", "Item Name", "Quantity", "Price" };

        // Create a JTable to display the inventory details
        JTable inventoryTable = new JTable(inventoryData, columnNames);
        inventoryTable.setFillsViewportHeight(true);
        inventoryTable.setDefaultEditor(Object.class, null); // Make the table read-only

        // Add a scroll pane to the table
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        // Display the table in a dialog
        JOptionPane.showMessageDialog(this, scrollPane, "Inventory Details", JOptionPane.INFORMATION_MESSAGE);
    }

   
private String[][] getInventoryDetails() {
    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "SELECT item_code, item_name, quantity, price FROM inventory"; // Query to fetch inventory details
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        // Collecting data
        ArrayList<String[]> inventoryDataList = new ArrayList<>();
        
        while (rs.next()) {
            String[] row = new String[4];
            row[0] = rs.getString("item_code");
            row[1] = rs.getString("item_name");
            row[2] = String.valueOf(rs.getInt("quantity"));
            row[3] = String.format("$%.2f", rs.getDouble("price"));
            inventoryDataList.add(row);
        }
        
        // Convert ArrayList to String[][]
        return inventoryDataList.toArray(new String[inventoryDataList.size()][]);
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
        scrollPane.setPreferredSize(new Dimension(600, 300));

        // Display the table in a dialog
        JOptionPane.showMessageDialog(this, scrollPane, "User Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private String[][] getUserDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) { // Modified to allow scrolling
             
            String query = "SELECT * FROM users"; // Query to fetch user details
            ResultSet rs = stmt.executeQuery(query);
    
            // Get row count
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst(); // Move cursor back to before the first row
    
            // Create data array
            String[][] userDetails = new String[rowCount][3];
            int index = 0;
    
            while (rs.next()) {
                userDetails[index][0] = String.valueOf(rs.getInt("id"));
                userDetails[index][1] = rs.getString("username");
                userDetails[index][2] = rs.getString("role");
                index++;
            }
            return userDetails;
        } catch (SQLException e) {
            showError("Failed to fetch user details: " + e.getMessage());
            return new String[0][0]; // Return an empty array in case of error
        }
    }
    

    private void addUser() {
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        String[] roles = {"Admin", "Store Manager", "Cashier"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(Box.createHorizontalStrut(15)); // a spacer
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(Box.createHorizontalStrut(15)); // a spacer
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();
            if (addUserToDatabase(username, password, role)) {
                JOptionPane.showMessageDialog(this, "User added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("Failed to add user.");
            }
        }
    }

    private boolean addUserToDatabase(String username, String password, String role) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // Use hashing for real applications
            stmt.setString(3, role);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            showError("Failed to add user: " + e.getMessage());
            return false;
        }
    }

    private void deleteUser() {
        String username = JOptionPane.showInputDialog(this, "Enter username to delete:");
        if (username != null && !username.trim().isEmpty()) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user: " + username + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                if (deleteUserFromDatabase(username)) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showError("Failed to delete user.");
                }
            }
        }
    }

    private boolean deleteUserFromDatabase(String username) {
        String query = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if a row was deleted
        } catch (SQLException e) {
            showError("Failed to delete user: " + e.getMessage());
            return false;
        }
    }

    private String getDatabaseDetails() {
        // This method can return the database version or other relevant information
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String dbDetails = "Database Name: " + metaData.getDatabaseProductName() + "\n"
                    + "Database Version: " + metaData.getDatabaseProductVersion();
            return dbDetails;
        } catch (SQLException e) {
            return "Failed to fetch database details: " + e.getMessage();
        }
    }

    private void logout() {
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            dispose(); // Close the current window
            new LoginAsPage(); // Open the login page (assumes LoginPage class exists)
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Alert", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        // Example User for testing purposes
        User testUser = new User("admin", "Admin");
        new AdminDashboard(testUser);
    }
}
