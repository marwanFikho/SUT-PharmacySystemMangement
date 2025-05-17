package utils;

import models.Supplier;
import java.io.*;
import java.util.ArrayList;

public class SupplierManager {
    private static final String FILE_NAME = "suppliers.txt";

    public static ArrayList<Supplier> loadSuppliers() {
        ArrayList<Supplier> suppliers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Supplier supplier = Supplier.fromString(line);
                if (supplier != null) {
                    suppliers.add(supplier);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading suppliers: " + e.getMessage());
        }
        return suppliers;
    }

    public static void saveSuppliers(ArrayList<Supplier> suppliers) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Supplier s : suppliers) {
                bw.write(s.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving suppliers: " + e.getMessage());
        }
    }
}
