package utils;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Handles operations for managing medicines (CRUD)
 * Stores data in "medicines.txt" located in the root project directory
 */
public class MedicineManager {

    // File name only - saved in project root folder
    private static final String MEDICINE_FILE = "medicines.txt";

    /**
     * Load all medicines from file
     * @return List of String arrays, each representing a medicine [name, quantity, price, expiryDate]
     */
    public static List<String[]> loadAll() {
        List<String[]> medicines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                medicines.add(line.split(","));
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error loading medicines: " + e.getMessage());
        }
        return medicines;
    }

    /**
     * Add a new medicine to the file
     */
    public static void addMedicine(String name, int quantity, double price, LocalDate expiryDate) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEDICINE_FILE, true))) {
            bw.write(name + "," + quantity + "," + price + "," + expiryDate);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("⚠️ Error adding medicine: " + e.getMessage());
        }
    }

    /**
     * Update an existing medicine at a given index
     */
    public static void updateMedicine(int index, String name, int quantity, double price, LocalDate expiryDate) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error reading for update: " + e.getMessage());
        }

        if (index >= 0 && index < lines.size()) {
            lines.set(index, name + "," + quantity + "," + price + "," + expiryDate);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEDICINE_FILE))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error writing updated medicine: " + e.getMessage());
        }
    }

    /**
     * Delete a medicine at the given index
     */
    public static void deleteMedicine(int index) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error reading for delete: " + e.getMessage());
        }

        if (index >= 0 && index < lines.size()) {
            lines.remove(index);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEDICINE_FILE))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error writing after delete: " + e.getMessage());
        }
    }

    /**
     * Check if a medicine with the given name already exists
     * @param name Medicine name to check
     * @return true if exists, false otherwise
     */
    public static boolean medicineExists(String name) {
        List<String[]> medicines = loadAll();
        for (String[] med : medicines) {
            if (med[0].equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
