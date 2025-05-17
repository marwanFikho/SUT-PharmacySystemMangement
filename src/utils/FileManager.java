package utils;

import models.Medicine;
import java.io.*;
import java.util.*;

public class FileManager {
    private static final String FILE_NAME = "medicines.txt";

    public static List<Medicine> loadMedicines() {
        List<Medicine> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                list.add(new Medicine(data[0], Integer.parseInt(data[1]),
                        Double.parseDouble(data[2]), data[3]));
            }
        } catch (Exception e) {
            System.out.println("Error reading medicines: " + e.getMessage());
        }
        return list;
    }

    public static void saveMedicines(Medicine medicine) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("medicines.txt", true))) {
            String line = String.join(",",
                    medicine.getName(),
                    String.valueOf(medicine.getQuantity()),
                    String.valueOf(medicine.getPrice()),
                    medicine.getExpiryDate()
            );
            writer.write(line);
            writer.newLine(); // important to separate entries
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getMedicineListAsString() {
        List<Medicine> list = loadMedicines();
        StringBuilder sb = new StringBuilder();
        for (Medicine m : list) {
            sb.append("Name: ").append(m.getName())
                    .append(" | Qty: ").append(m.getQuantity())
                    .append(" | Price: $").append(m.getPrice())
                    .append(" | Expiry: ").append(m.getExpiryDate()).append("\n");
        }
        return sb.toString();
    }
}