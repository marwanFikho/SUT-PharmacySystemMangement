package models;

public class Supplier {
    private String name;
    private String phone;
    private String address;
    private String suppliedMedicines;

    public Supplier(String name, String phone, String address, String suppliedMedicines) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.suppliedMedicines = suppliedMedicines;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getSuppliedMedicines() {
        return suppliedMedicines;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSuppliedMedicines(String suppliedMedicines) {
        this.suppliedMedicines = suppliedMedicines;
    }

    // Safer string conversion using "||" as delimiter
    @Override
    public String toString() {
        return name + "||" + phone + "||" + address + "||" + suppliedMedicines;
    }

    public static Supplier fromString(String line) {
        String[] parts = line.split("\\|\\|");
        if (parts.length == 4) {
            return new Supplier(parts[0], parts[1], parts[2], parts[3]);
        }
        return null;
    }
}
