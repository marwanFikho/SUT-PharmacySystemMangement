package gui;

import utils.UserManager;
import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    public LoginScreen() {
        setTitle("Pharmacy Login");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pw = new String(passField.getPassword());

            String role = UserManager.checkCredentials(user, pw);
            if (role == null) {
                JOptionPane.showMessageDialog(this, "❌ Invalid credentials.");
                return;
            }

            switch (role) {
                case "Admin" -> new AdminDashboard().setVisible(true);
                case "Pharmacist" -> new PharmacistDashboard().setVisible(true);
                case "Customer" -> new CustomerDashboard().setVisible(true);
            }
            dispose();
        });

        add(new JLabel("Username:")); add(userField);
        add(new JLabel("Password:")); add(passField);
        add(loginBtn);
    }
}