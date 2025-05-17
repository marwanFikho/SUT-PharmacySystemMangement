package models;

public class Medicine {
    private String name;
    private int quantity;
    private double price;
    private String expiryDate;

    public Medicine(String name, int quantity, double price, String expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getExpiryDate() { return expiryDate; }

    public void setQuantity(int quantity) { this.quantity = quantity; }


    @Override
    public String toString() {
        return name + "," + quantity + "," + price + "," + expiryDate;
    }
}