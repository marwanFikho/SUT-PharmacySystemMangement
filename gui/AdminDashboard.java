// AdminDashboard.java
package gui;

import models.Supplier;
import utils.MedicineManager;
import utils.SupplierManager;
import utils.UserManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {

    private void loadUsersTable(DefaultTableModel model) {
        model.setRowCount(0);
        String[][] users = UserManager.getAllUsers();
        for (String[] user : users) {
            model.addRow(user);
        }
    }

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(logoutButton);

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        // ===== Inventory Tab =====
        JPanel inventoryPanel = new JPanel(new BorderLayout());

        String[] medColumns = {"Name", "Quantity", "Price", "Expiry"};
        DefaultTableModel medModel = new DefaultTableModel(medColumns, 0);
        JTable medTable = new JTable(medModel);
        JScrollPane medScroll = new JScrollPane(medTable);

        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField expiryField = new JTextField();

        formPanel.add(new JLabel("Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Quantity:")); formPanel.add(quantityField);
        formPanel.add(new JLabel("Price:")); formPanel.add(priceField);
        formPanel.add(new JLabel("Expiry Date (yyyy-mm-dd):")); formPanel.add(expiryField);

        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear Form");
        JPanel medicineButtonPanel = new JPanel();
        medicineButtonPanel.add(addBtn); medicineButtonPanel.add(editBtn); medicineButtonPanel.add(delBtn); medicineButtonPanel.add(clearBtn);

        List<String[]> allMeds = MedicineManager.loadAll();
        for (String[] med : allMeds) medModel.addRow(med);

        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int qty = Integer.parseInt(quantityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                LocalDate exp = LocalDate.parse(expiryField.getText().trim());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "❌ Medicine name cannot be empty.");
                    return;
                }

                if (MedicineManager.medicineExists(name)) {
                    JOptionPane.showMessageDialog(this,
                            "❌ A medicine named '" + name + "' already exists.\nPlease use a unique name.",
                            "Duplicate Medicine",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                MedicineManager.addMedicine(name, qty, price, exp);
                medModel.addRow(new String[]{name, String.valueOf(qty), String.valueOf(price), exp.toString()});

                JOptionPane.showMessageDialog(this, "✅ Medicine added successfully.");
                nameField.setText("");
                quantityField.setText("");
                priceField.setText("");
                expiryField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid quantity or price. Please enter numbers only.");
            } catch (DateTimeException ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid date format. Use yyyy-mm-dd format.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });

        editBtn.addActionListener(e -> {
            int row = medTable.getSelectedRow();
            if (row == -1) return;

            try {
                String name = nameField.getText().trim();
                int qty = Integer.parseInt(quantityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                LocalDate exp = LocalDate.parse(expiryField.getText().trim());

                MedicineManager.updateMedicine(row, name, qty, price, exp);
                medModel.setValueAt(name, row, 0);
                medModel.setValueAt(String.valueOf(qty), row, 1);
                medModel.setValueAt(String.valueOf(price), row, 2);
                medModel.setValueAt(exp.toString(), row, 3);
                JOptionPane.showMessageDialog(this, "✅ Updated.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid input.");
            }
        });

        delBtn.addActionListener(e -> {
            int row = medTable.getSelectedRow();
            if (row == -1) return;
            MedicineManager.deleteMedicine(row);
            medModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "🗑️ Deleted.");
        });

        clearBtn.addActionListener(e -> {
            nameField.setText("");
            quantityField.setText("");
            priceField.setText("");
            expiryField.setText("");
            medTable.clearSelection();
        });

        medTable.getSelectionModel().addListSelectionListener(e -> {
            int row = medTable.getSelectedRow();
            if (row != -1) {
                nameField.setText(medModel.getValueAt(row, 0).toString());
                quantityField.setText(medModel.getValueAt(row, 1).toString());
                priceField.setText(medModel.getValueAt(row, 2).toString());
                expiryField.setText(medModel.getValueAt(row, 3).toString());
            }
        });
        
        inventoryPanel.add(medScroll, BorderLayout.CENTER);
        inventoryPanel.add(formPanel, BorderLayout.NORTH);
        inventoryPanel.add(medicineButtonPanel, BorderLayout.SOUTH);
        tabs.addTab("Inventory", inventoryPanel);

        // ===== Users Tab =====
        JPanel userPanel = new JPanel(new BorderLayout());
        JPanel userFormPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        userFormPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Pharmacist", "Customer"});

        userFormPanel.add(new JLabel("Username:")); userFormPanel.add(usernameField);
        userFormPanel.add(new JLabel("Password:")); userFormPanel.add(passwordField);
        userFormPanel.add(new JLabel("Role:"));     userFormPanel.add(roleBox);

        String[] userColumns = {"Username", "Password", "Role"};
        DefaultTableModel userModel = new DefaultTableModel(userColumns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable userTable = new JTable(userModel);
        JScrollPane userScroll = new JScrollPane(userTable);
        loadUsersTable(userModel);

        JPanel userButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addUserBtn = new JButton("Add User");
        JButton editUserBtn = new JButton("Update User");
        JButton deleteUserBtn = new JButton("Delete User");
        JButton clearFormBtn = new JButton("Clear Form");
        JLabel userFeedback = new JLabel("", JLabel.CENTER);

        userButtonPanel.add(addUserBtn);
        userButtonPanel.add(editUserBtn);
        userButtonPanel.add(deleteUserBtn);
        userButtonPanel.add(clearFormBtn);
        userButtonPanel.add(userFeedback);

        userTable.getSelectionModel().addListSelectionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                usernameField.setText(userModel.getValueAt(row, 0).toString());
                passwordField.setText(userModel.getValueAt(row, 1).toString());
                String role = userModel.getValueAt(row, 2).toString();
                roleBox.setSelectedItem(role.equals("Admin") ? "Admin" : role);
                roleBox.setEnabled(!role.equals("Admin"));
            }
        });

        clearFormBtn.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            roleBox.setSelectedIndex(0);
            roleBox.setEnabled(true);
            userTable.clearSelection();
            userFeedback.setText("");
        });

        addUserBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pw = new String(passwordField.getPassword());
            String role = (String) roleBox.getSelectedItem();
            if (user.isBlank() || pw.isBlank()) {
                userFeedback.setText("❌ Username and password required.");
                return;
            }

            boolean success = UserManager.createUser(user, pw, role);
            if (success) {
                userFeedback.setText("✅ User created.");
                loadUsersTable(userModel);
                clearFormBtn.doClick();
            } else {
                userFeedback.setText("❌ Username already exists.");
            }
        });

        editUserBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row == -1) {
                userFeedback.setText("❌ Select a user to edit.");
                return;
            }

            String user = usernameField.getText().trim();
            String pw = new String(passwordField.getPassword());
            String role = (String) roleBox.getSelectedItem();

            if (user.isBlank() || pw.isBlank()) {
                userFeedback.setText("❌ Username and password required.");
                return;
            }

            boolean success = UserManager.updateUser(row, user, pw, role);
            if (success) {
                userFeedback.setText("✅ User updated.");
                loadUsersTable(userModel);
                clearFormBtn.doClick();
            } else {
                userFeedback.setText("❌ Update failed.");
            }
        });

        deleteUserBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row == -1) {
                userFeedback.setText("❌ Select a user to delete.");
                return;
            }

            String username = userModel.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete user '" + username + "'?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = UserManager.deleteUser(row);
                if (success) {
                    userFeedback.setText("✅ User deleted.");
                    loadUsersTable(userModel);
                    clearFormBtn.doClick();
                } else {
                    userFeedback.setText("❌ Cannot delete Admin user.");
                }
            }
        });

        userPanel.add(userFormPanel, BorderLayout.NORTH);
        userPanel.add(userScroll, BorderLayout.CENTER);
        userPanel.add(userButtonPanel, BorderLayout.SOUTH);
        tabs.addTab("Manage Users", userPanel);
        
        
        // ===== Supplier Management Tab =====
        JPanel supplierPanel = new JPanel(new BorderLayout());
        
        // Table setup
        DefaultTableModel supplierTableModel = new DefaultTableModel(new String[]{"Name", "Phone", "Address", "Medicines"}, 0);
        JTable supplierTable = new JTable(supplierTableModel);
        JScrollPane supplierScrollPane = new JScrollPane(supplierTable);
        supplierScrollPane.setPreferredSize(new Dimension(700, 200));
        supplierPanel.add(supplierScrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel supplierFormPanel = new JPanel(new GridLayout(5, 2, 10, 5));
        supplierFormPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField supplierNameField = new JTextField();
        JTextField supplierPhoneField = new JTextField();
        JTextField supplierAddressField = new JTextField();
        JTextField supplierMedicinesField = new JTextField();
        
        supplierFormPanel.add(new JLabel("Name:"));
        supplierFormPanel.add(supplierNameField);
        supplierFormPanel.add(new JLabel("Phone:"));
        supplierFormPanel.add(supplierPhoneField);
        supplierFormPanel.add(new JLabel("Address:"));
        supplierFormPanel.add(supplierAddressField);
        supplierFormPanel.add(new JLabel("Medicines:"));
        supplierFormPanel.add(supplierMedicinesField);
        
        // Button panel
        JPanel supplierButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JButton addButton = new JButton("Add Supplier");
        JButton deleteButton = new JButton("Delete Selected");
        JButton saveButton = new JButton("Save All");
        
        // Load supplier data
        ArrayList<Supplier> suppliers = SupplierManager.loadSuppliers();
        
        // Refresh table helper method (as local function)
        Runnable refreshSupplierTable = () -> {
            supplierTableModel.setRowCount(0);
            for (Supplier s : suppliers) {
                supplierTableModel.addRow(new Object[]{
                    s.getName(), s.getPhone(), s.getAddress(), s.getSuppliedMedicines()
                });
            }
        };
        
        // Initial table population
        refreshSupplierTable.run();
        
        // Clear fields helper method
        Runnable clearFields = () -> {
            supplierNameField.setText("");
            supplierPhoneField.setText("");
            supplierAddressField.setText("");
            supplierMedicinesField.setText("");
        };
        
        // Add button action
        addButton.addActionListener(e -> {
            String name = supplierNameField.getText().trim();
            String phone = supplierPhoneField.getText().trim();
            String address = supplierAddressField.getText().trim();
            String meds = supplierMedicinesField.getText().trim();
            
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
            refreshSupplierTable.run();
            clearFields.run();
            JOptionPane.showMessageDialog(this, "✅ Supplier added successfully.");
        });
        
        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedRow = supplierTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this supplier?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    suppliers.remove(selectedRow);
                    refreshSupplierTable.run();
                    JOptionPane.showMessageDialog(this, "🗑️ Supplier deleted.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "❌ Select a supplier to delete.");
            }
        });
        
        // Save button action
        saveButton.addActionListener(e -> {
            SupplierManager.saveSuppliers(suppliers);
            JOptionPane.showMessageDialog(this, "✅ Suppliers saved successfully.");
        });
        
        supplierButtonPanel.add(addButton);
        supplierButtonPanel.add(deleteButton);
        supplierButtonPanel.add(saveButton);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(supplierFormPanel, BorderLayout.CENTER);
        bottomPanel.add(supplierButtonPanel, BorderLayout.SOUTH);
        
        supplierPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add the supplier panel to the tabs
        tabs.addTab("Supplier Management", supplierPanel);

        // ===== Sales History Tab =====
        JPanel salesPanel = new JPanel(new BorderLayout());
        JTextArea salesTextArea = new JTextArea();
        salesTextArea.setEditable(false);
        salesTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane salesScrollPane = new JScrollPane(salesTextArea);

        JButton refreshButton = new JButton("Refresh Sales Data");
        JButton resetButton = new JButton("Reset Sales History");
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(resetButton);

        JLabel confirmationLabel = new JLabel("");
        confirmationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        confirmationLabel.setForeground(new Color(0, 128, 0));
        JPanel confirmPanel = new JPanel(new BorderLayout());
        confirmPanel.add(confirmationLabel, BorderLayout.CENTER);
        confirmPanel.add(buttonsPanel, BorderLayout.EAST);

        Runnable loadSalesData = () -> {
            String salesData = utils.PurchaseManager.getPurchasesAsString();
            salesTextArea.setText(salesData.trim().isEmpty() ? "No sales recorded yet." : "SALES HISTORY\n\n" + salesData);
        };
        loadSalesData.run();

        refreshButton.addActionListener(e -> {
            loadSalesData.run();
            confirmationLabel.setText("");
        });

        resetButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to reset all sales history?\nThis action cannot be undone!",
                    "Confirm Reset",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = utils.PurchaseManager.resetPurchaseHistory();
                if (success) {
                    confirmationLabel.setText("✅ Sales history has been reset successfully");
                    salesTextArea.setText("No sales recorded yet.");
                } else {
                    confirmationLabel.setText("❌ Failed to reset sales history");
                }
            }
        });

        salesPanel.add(salesScrollPane, BorderLayout.CENTER);
        salesPanel.add(confirmPanel, BorderLayout.SOUTH);
        tabs.addTab("Sales History", salesPanel);

        add(tabs, BorderLayout.CENTER);
    }
}