package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import models.Medicine;
import utils.FileManager;
import utils.PurchaseManager;

public class PharmacistDashboard extends JFrame {
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JTextField quantityField;
    private JLabel statusLabel;
    
    public PharmacistDashboard() {
        setTitle("Pharmacist Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main layout
        setLayout(new BorderLayout(10, 10));
        
        // Top panel with welcome message and logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome Pharmacist!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose(); // Close this dashboard
            new LoginScreen().setVisible(true); // Show login screen
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(logoutButton);
        
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Medicine table
        String[] columns = {"Name", "Quantity", "Price ($)", "Expiry Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        medicineTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(medicineTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Medicines"));
        
        // Load medicines into the table
        loadMedicineTable();
        
        // Sale panel
        JPanel salePanel = new JPanel(new BorderLayout(10, 10));
        salePanel.setBorder(BorderFactory.createTitledBorder("Record Sale"));
        
        JPanel formPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        JLabel qtyLabel = new JLabel("Quantity to Sell:", SwingConstants.CENTER);
        quantityField = new JTextField("1");
        JButton sellButton = new JButton("Record Sale");
        statusLabel = new JLabel("", SwingConstants.CENTER);
        
        formPanel.add(qtyLabel);
        formPanel.add(quantityField);
        formPanel.add(sellButton);
        
        sellButton.addActionListener(this::sellMedicine);
        
        salePanel.add(formPanel, BorderLayout.NORTH);
        salePanel.add(statusLabel, BorderLayout.CENTER);
        
        // Instructions panel
        JTextArea instructionsArea = new JTextArea();
        instructionsArea.setEditable(false);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setText("Instructions:\n" +
                "1. Select a medicine from the table above\n" +
                "2. Enter the quantity to sell\n" +
                "3. Click 'Record Sale' to process the transaction\n\n" +
                "Note: The system will verify if enough stock is available.");
        instructionsArea.setBackground(new Color(240, 240, 240));
        instructionsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("How to Use"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(salePanel, BorderLayout.CENTER);
        bottomPanel.add(instructionsArea, BorderLayout.SOUTH);
        
        // Add all components to frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Load medicines from file into the table
     */
    private void loadMedicineTable() {
        tableModel.setRowCount(0); // Clear existing data
        
        List<Medicine> medicines = FileManager.loadMedicines();
        for (Medicine med : medicines) {
            Object[] row = {
                med.getName(),
                med.getQuantity(),
                med.getPrice(),
                med.getExpiryDate()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Process a medicine sale
     */
    private void sellMedicine(ActionEvent e) {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            statusLabel.setText("❌ Please select a medicine first");
            return;
        }
        
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                statusLabel.setText("❌ Quantity must be greater than zero");
                return;
            }
            
            String medicineName = tableModel.getValueAt(selectedRow, 0).toString();
            int available = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString());
            double price = Double.parseDouble(tableModel.getValueAt(selectedRow, 2).toString());
            
            if (quantity > available) {
                statusLabel.setText("❌ Not enough stock available");
                return;
            }
            
            boolean success = PurchaseManager.recordPurchase(medicineName, quantity, price);
            if (success) {
                statusLabel.setText("✅ Sale recorded successfully! Total: $" + 
                    String.format("%.2f", price * quantity));
                loadMedicineTable(); // Refresh the table
                quantityField.setText("1"); // Reset quantity field
            } else {
                statusLabel.setText("❌ Error recording sale. Please try again.");
            }
            
        } catch (NumberFormatException ex) {
            statusLabel.setText("❌ Please enter a valid quantity");
        }
    }
}