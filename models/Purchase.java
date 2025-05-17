package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Purchase {
    private String medicineName;
    private int quantity;
    private double price;
    private double total;
    private LocalDateTime purchaseTime;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Purchase(String medicineName, int quantity, double price, LocalDateTime purchaseTime) {
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.price = price;
        this.total = price * quantity;
        this.purchaseTime = purchaseTime;
    }

    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return total; }
    public LocalDateTime getPurchaseTime() { return purchaseTime; }
    
    public String getFormattedTime() {
        return purchaseTime.format(formatter);
    }

    @Override
    public String toString() {
        return medicineName + "," + quantity + "," + price + "," + total + "," + getFormattedTime();
    }
    
    public static Purchase fromString(String line) {
        String[] parts = line.split(",");
        String name = parts[0];
        int qty = Integer.parseInt(parts[1]);
        double price = Double.parseDouble(parts[2]);
        LocalDateTime time = LocalDateTime.parse(parts[4], formatter);
        
        return new Purchase(name, qty, price, time);
    }
}
