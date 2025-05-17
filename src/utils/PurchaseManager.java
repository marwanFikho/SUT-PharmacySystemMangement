package utils;

import models.Purchase;
import models.Medicine;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseManager {
    // Use relative paths so the app runs on any machine or OS
    private static final String PURCHASE_FILE = "purchases.txt";
    private static final String MEDICINE_FILE = "medicines.txt";

    /**
     * Record a new purchase
     * @param medicineName Name of medicine purchased
     * @param quantity Quantity purchased
     * @param price Price per unit
     * @return true if purchase was recorded successfully
     */
    public static boolean recordPurchase(String medicineName, int quantity, double price) {
        // Update medicine quantity in file
        boolean success = updateMedicineQuantity(medicineName, quantity);
        if (!success) {
            return false;
        }

        // Record the purchase
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PURCHASE_FILE, true))) {
            Purchase purchase = new Purchase(medicineName, quantity, price, LocalDateTime.now());
            writer.write(purchase.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error recording purchase: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update medicine quantity after a purchase
     * @param medicineName Name of medicine
     * @param quantityPurchased Quantity sold
     * @return true if successful
     */
    private static boolean updateMedicineQuantity(String medicineName, int quantityPurchased) {
        List<Medicine> medicines = FileManager.loadMedicines();
        boolean found = false;

        for (Medicine medicine : medicines) {
            if (medicine.getName().equals(medicineName)) {
                int newQuantity = medicine.getQuantity() - quantityPurchased;
                if (newQuantity < 0) {
                    return false; // Not enough stock
                }
                medicine.setQuantity(newQuantity);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        // Update the medicines file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEDICINE_FILE))) {
            for (Medicine medicine : medicines) {
                writer.write(medicine.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error updating medicine quantity: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load all purchases from file
     * @return List of purchases
     */
    public static List<Purchase> loadAllPurchases() {
        List<Purchase> purchases = new ArrayList<>();
        File file = new File(PURCHASE_FILE);

        if (!file.exists()) {
            return purchases;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(PURCHASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                purchases.add(Purchase.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading purchases: " + e.getMessage());
        }

        return purchases;
    }

    /**
     * Calculate total revenue from all purchases
     * @return Total revenue
     */
    public static double getTotalRevenue() {
        List<Purchase> purchases = loadAllPurchases();
        return purchases.stream()
                .mapToDouble(Purchase::getTotal)
                .sum();
    }

    /**
     * Format purchases as a readable string for display
     * @return Formatted string of purchases
     */
    public static String getPurchasesAsString() {
        List<Purchase> purchases = loadAllPurchases();
        StringBuilder sb = new StringBuilder();

        for (Purchase p : purchases) {
            sb.append("Medicine: ").append(p.getMedicineName())
              .append(" | Qty: ").append(p.getQuantity())
              .append(" | Price: $").append(String.format("%.2f", p.getPrice()))
              .append(" | Total: $").append(String.format("%.2f", p.getTotal()))
              .append(" | Time: ").append(p.getFormattedTime())
              .append("\n");
        }

        sb.append("\n=========================\n");
        sb.append("TOTAL REVENUE: $").append(String.format("%.2f", getTotalRevenue()));

        return sb.toString();
    }

    /**
     * Reset the purchase history by clearing the purchases file
     * @return true if successful, false otherwise
     */
    public static boolean resetPurchaseHistory() {
        try {
            // Create empty file (overwrite existing)
            new PrintWriter(new FileWriter(PURCHASE_FILE, false)).close();
            return true;
        } catch (IOException e) {
            System.out.println("Error resetting purchase history: " + e.getMessage());
            return false;
        }
    }
}
