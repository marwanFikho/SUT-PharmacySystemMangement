package gui;

import models.Supplier;
import utils.SupplierManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class SupplierManagement extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField, phoneField, addressField, medicinesField;
    private ArrayList<Supplier> suppliers;

    public SupplierManagement() {
        setTitle("Supplier Management");
        setSize(750, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        suppliers = SupplierManager.loadSuppliers();

        tableModel = new DefaultTableModel(new String[]{"Name", "Phone", "Address", "Medicines"}, 0);
        table = new JTable(tableModel);
        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 200));
        add(scrollPane, BorderLayout.CENTER);

        // ==== Form Panel ====
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        medicinesField = new JTextField();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Medicines:"));
        formPanel.add(medicinesField);

        // ==== Button Panel ====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JButton addButton = new JButton("Add Supplier");
        JButton deleteButton = new JButton("Delete Selected");
        JButton saveButton = new JButton("Save All");

        addButton.addActionListener(e -> addSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());
        saveButton.addActionListener(e -> saveSuppliers());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{
                s.getName(), s.getPhone(), s.getAddress(), s.getSuppliedMedicines()
            });
        }
    }

    private void addSupplier() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String meds = medicinesField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prevent duplicate names
        for (Supplier s : suppliers) {
            if (s.getName().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this, "❌ Supplier with this name already exists.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Supplier supplier = new Supplier(name, phone, address, meds);
        suppliers.add(supplier);
        refreshTable();
        clearFields();
        JOptionPane.showMessageDialog(this, "✅ Supplier added successfully.");
    }

    private void deleteSupplier() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this supplier?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                suppliers.remove(selectedRow);
                refreshTable();
                JOptionPane.showMessageDialog(this, "🗑️ Supplier deleted.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "❌ Select a supplier to delete.");
        }
    }

    private void saveSuppliers() {
        SupplierManager.saveSuppliers(suppliers);
        JOptionPane.showMessageDialog(this, "✅ Suppliers saved successfully.");
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        addressField.setText("");
        medicinesField.setText("");
    }
}
