import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;
import java.awt.Font;

public class BillingSystem extends JFrame {
    private JTextField productField, quantityField, customerNameField, customerPhoneField;
    private JTextArea billArea;
    private JLabel totalCostLabel, subTotalLabel;
    private double totalCost = 0.0;
    private double subTotal = 0.0;
    private StringBuilder billContent;
    private User user;

    public BillingSystem(User user) {
        this.user = user;
        this.billContent = new StringBuilder();
        setTitle("Billing System - " + user.getUsername());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));

        // Customer details
        inputPanel.add(createLabel("Customer Name:"));
        customerNameField = createTextField();
        inputPanel.add(customerNameField);

        inputPanel.add(createLabel("Customer Phone:"));
        customerPhoneField = createTextField();
        inputPanel.add(customerPhoneField);

        // Product fields
        inputPanel.add(createLabel("Product:"));
        productField = createTextField();
        inputPanel.add(productField);

        inputPanel.add(createLabel("Quantity:"));
        quantityField = createTextField();
        inputPanel.add(quantityField);

        JButton addItemBtn = createButton("Add Item");
        addItemBtn.addActionListener(e -> addItemToBill());
        inputPanel.add(addItemBtn);

        subTotalLabel = new JLabel("Subtotal: $0.00");
        inputPanel.add(subTotalLabel);

        totalCostLabel = new JLabel("Total Cost: $0.00");
        inputPanel.add(totalCostLabel);

        // Discount buttons
        inputPanel.add(createLabel("Apply Discount:"));
        JPanel discountPanel = new JPanel(new FlowLayout());
        JButton discountYesBtn = createButton("Yes");
        JButton discountNoBtn = createButton("No");

        discountYesBtn.addActionListener(e -> applyDiscountLogic(true));
        discountNoBtn.addActionListener(e -> applyDiscountLogic(false));

        discountPanel.add(discountYesBtn);
        discountPanel.add(discountNoBtn);
        inputPanel.add(discountPanel);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Bill area
        billArea = new JTextArea();
        billArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(billArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printBillBtn = createButton("Print Bill");
        printBillBtn.addActionListener(e -> printBill());
        buttonPanel.add(printBillBtn);

        JButton finalizeBillBtn = createButton("Finalize Bill");
        finalizeBillBtn.addActionListener(e -> finalizeBill());
        buttonPanel.add(finalizeBillBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        return textField;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }

    private void addItemToBill() {
        String productName = productField.getText();
        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM inventory WHERE item_name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String itemCode = rs.getString("item_code");
                double price = rs.getDouble("price");
                int availableQuantity = rs.getInt("quantity");

                if (quantity > availableQuantity) {
                    JOptionPane.showMessageDialog(this, "Insufficient quantity!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    double itemTotal = quantity * price;
                    subTotal += itemTotal;

                    String itemLine = String.format("%-20s x %3d = $%6.2f\n", productName, quantity, itemTotal);
                    billArea.append(itemLine);
                    billContent.append(itemLine);

                    // Update subtotal
                    subTotalLabel.setText("Subtotal: $" + String.format("%.2f", subTotal));
                    JOptionPane.showMessageDialog(this, "Item added successfully!");

                    // Update inventory
                    updateInventory(conn, itemCode, quantity);

                    // Clear input fields
                    productField.setText("");
                    quantityField.setText("");
                    productField.requestFocus();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Product not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyDiscountLogic(boolean applyDiscount) {
        if (applyDiscount) {
            String discountPercentStr = JOptionPane.showInputDialog(this, "Enter Discount Percentage:");
            try {
                double discountPercent = Double.parseDouble(discountPercentStr);
                double discountAmount = (discountPercent / 100) * subTotal;
                totalCost = subTotal - discountAmount;
                totalCostLabel.setText("Total Cost: $" + String.format("%.2f", totalCost));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid discount percentage!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            totalCost = subTotal;
            totalCostLabel.setText("Total Cost: $" + String.format("%.2f", totalCost));
        }
    }

    private void updateInventory(Connection conn, String itemCode, int quantity) throws SQLException {
        String updateQuery = "UPDATE inventory SET quantity = quantity - ? WHERE item_code = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
        updateStmt.setInt(1, quantity);
        updateStmt.setString(2, itemCode);
        updateStmt.executeUpdate();
    }

    private void finalizeBill() {
        if (customerNameField.getText().isEmpty() || customerPhoneField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in customer details!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Prepare bill content
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String customerName = customerNameField.getText();
        String customerPhone = customerPhoneField.getText();
        
        // Generate invoice number (you can change the logic as per your requirement)
        String invoiceNumber = "INV-" + System.currentTimeMillis(); // Simple unique identifier
    
        billContent.insert(0, "Date: " + date + "\nCustomer: " + customerName + " (" + customerPhone + ")\n\n");
        billArea.setText(billContent.toString());
    
        // Save bill to PDF
        saveBillToPDF(date, customerName, customerPhone);
    
        // Save sale to database
        saveSaleToDatabase(invoiceNumber, customerName, customerPhone);
    
        // Reset fields
        resetFields();
    }
    
    private void saveSaleToDatabase(String invoiceNumber, String customerName, String customerPhone) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertQuery = "INSERT INTO sales (item_code, item_name, quantity, total, invoice_number, cashier_username, customer_name, customer_phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            
            // You might need to process each item in the bill here.
            for (String line : billContent.toString().split("\n")) {
                String[] parts = line.split(" x "); // Assuming the format is "ProductName x Quantity"
                if (parts.length < 2) continue; // Skip if the format is unexpected
    
                String productName = parts[0].trim();
                int quantity = Integer.parseInt(parts[1].split(" = ")[0].trim()); // Extract quantity
                double total = Double.parseDouble(parts[1].split(" = \\$")[1].trim()); // Extract total
    
                // Retrieve item_code from inventory
                String codeQuery = "SELECT item_code FROM inventory WHERE item_name = ?";
                PreparedStatement codeStmt = conn.prepareStatement(codeQuery);
                codeStmt.setString(1, productName);
                ResultSet rs = codeStmt.executeQuery();
    
                if (rs.next()) {
                    String itemCode = rs.getString("item_code");
    
                    // Set parameters for the sales insertion
                    stmt.setString(1, itemCode);
                    stmt.setString(2, productName);
                    stmt.setInt(3, quantity);
                    stmt.setDouble(4, total);
                    stmt.setString(5, invoiceNumber);
                    stmt.setString(6, user.getUsername()); // Assuming you have a method to get the cashier's username
                    stmt.setString(7, customerName);
                    stmt.setString(8, customerPhone);
                    
                    stmt.addBatch(); // Add to batch for execution
                }
            }
    
            // Execute batch insert
            stmt.executeBatch();
            JOptionPane.showMessageDialog(this, "Sale saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred while saving the sale!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void saveBillToPDF(String date, String customerName, String customerPhone) {
    Document document = new Document(PageSize.A4);
    try {
        String fileName = "Bill_" + System.currentTimeMillis() + ".pdf";
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // Add Store Name and Header
        Paragraph storeName = new Paragraph("Store Billing System", FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD));
        storeName.setAlignment(Element.ALIGN_CENTER);
        document.add(storeName);

        document.add(Chunk.NEWLINE);

        // Add Customer Information
        Paragraph customerInfo = new Paragraph(
            String.format("Date: %s\nCustomer: %s\nPhone: %s", date, customerName, customerPhone),
            FontFactory.getFont(FontFactory.HELVETICA, 12, Font.PLAIN)
        );
        customerInfo.setAlignment(Element.ALIGN_LEFT);
        document.add(customerInfo);

        document.add(Chunk.NEWLINE);

        // Add Table for Bill Items
        PdfPTable table = new PdfPTable(4); // 4 columns: Product, Quantity, Price, Total
        table.setWidthPercentage(100); // Full width
        table.setSpacingBefore(10f); // Space before the table
        table.setSpacingAfter(10f);  // Space after the table

        // Add Table Header
        Stream.of("Product", "Quantity", "Price", "Total").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setPhrase(new Phrase(columnTitle));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(BaseColor.LIGHT_GRAY); // Add background color to header cells
            table.addCell(header);
        });

        // Add Bill Items to the Table
        String[] lines = billContent.toString().split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("Date:") || line.startsWith("Customer:")) {
                continue; // Skip empty lines and customer info
            }
            String[] parts = line.split("x|=");
            if (parts.length == 3) {
                table.addCell(parts[0].trim()); // Product Name
                table.addCell(parts[1].trim()); // Quantity
                String[] priceParts = parts[2].split("\\$");
                if (priceParts.length == 2) {
                    double price = Double.parseDouble(priceParts[1].trim()) / Integer.parseInt(parts[1].trim());
                    table.addCell(String.format("$%.2f", price)); // Unit Price
                    table.addCell(priceParts[1].trim()); // Total Price
                }
            }
        }

        document.add(table);

        // Add Total Amount Section
        Paragraph totalAmount = new Paragraph(
            String.format("Subtotal: $%.2f\nTotal: $%.2f", subTotal, totalCost),
            FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)
        );
        totalAmount.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalAmount);

        document.add(Chunk.NEWLINE);

        // Add Footer (Optional)
        Paragraph footer = new Paragraph("Thank you for shopping with us!", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        JOptionPane.showMessageDialog(this, "Bill saved as " + fileName, "Success", JOptionPane.INFORMATION_MESSAGE);
    } catch (DocumentException | IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error saving bill as PDF!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void printBill() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(new Printable() {
            public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pf.getImageableX(), pf.getImageableY());
                g2d.drawString(billArea.getText(), 100, 100); // You may need to adjust the coordinates
                return PAGE_EXISTS;
            }
        });

        boolean doPrint = printerJob.printDialog();
        if (doPrint) {
            try {
                printerJob.print();
            } catch (PrinterException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to print!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetFields() {
        productField.setText("");
        quantityField.setText("");
        customerNameField.setText("");
        customerPhoneField.setText("");
        billArea.setText("");
        subTotal = 0.0;
        totalCost = 0.0;
        subTotalLabel.setText("Subtotal: $0.00");
        totalCostLabel.setText("Total Cost: $0.00");
        billContent.setLength(0); // Clear bill content
    }
}
