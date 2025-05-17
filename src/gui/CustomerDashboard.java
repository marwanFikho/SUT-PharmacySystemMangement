package gui;

import javax.swing.*;
import java.awt.*;
import models.Medicine;
import utils.FileManager;
import utils.PurchaseManager;
import java.util.List;

public class CustomerDashboard extends JFrame {
    public CustomerDashboard() {
        setTitle("Customer Dashboard");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout(10, 10));

        // Top panel: welcome and logout
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome Customer!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(logoutButton);

        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Center: medicine list
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        refreshMedicineList(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Bottom: buy medicine button
        JButton buyButton = new JButton("Buy Medicine");
        buyButton.addActionListener(e -> showBuyDialog(textArea));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(buyButton);

        // Add all to frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Refresh the medicine list in the text area
    private void refreshMedicineList(JTextArea textArea) {
        textArea.setText("🛒 Available Medicines to Buy:\n\n" + FileManager.getMedicineListAsString());
    }

    // Show the buy dialog
    private void showBuyDialog(JTextArea textArea) {
        List<Medicine> medicines = FileManager.loadMedicines();

        if (medicines.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No medicines available.");
            return;
        }

        String[] medicineNames = medicines.stream()
            .map(Medicine::getName)
            .toArray(String[]::new);

        JComboBox<String> medicineBox = new JComboBox<>(medicineNames);
        JTextField quantityField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Select Medicine:"));
        panel.add(medicineBox);
        panel.add(new JLabel("Enter Quantity:"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Buy Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedMedicine = (String) medicineBox.getSelectedItem();
            int quantity;

            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid quantity.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Medicine selected = medicines.stream()
                .filter(m -> m.getName().equals(selectedMedicine))
                .findFirst()
                .orElse(null);

            if (selected != null && quantity <= selected.getQuantity()) {
                boolean success = PurchaseManager.recordPurchase(selected.getName(), quantity, selected.getPrice());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Purchase successful!");
                    refreshMedicineList(textArea);
                } else {
                    JOptionPane.showMessageDialog(this, "Error processing purchase.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Not enough stock available.", "Out of Stock", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
