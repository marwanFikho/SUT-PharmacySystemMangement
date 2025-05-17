package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Prescription {
    private String id;
    private String patientName;
    private String doctorName;
    private String medicineName;
    private int quantity;
    private String dosage;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private boolean processed;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Prescription(String patientName, String doctorName, String medicineName, 
                      int quantity, String dosage, LocalDate issueDate, LocalDate expiryDate) {
        this.id = UUID.randomUUID().toString().substring(0, 8); // Generate a unique ID
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.dosage = dosage;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.processed = false;
    }
    
    // Constructor for loading from file
    public Prescription(String id, String patientName, String doctorName, String medicineName, 
                      int quantity, String dosage, LocalDate issueDate, LocalDate expiryDate, boolean processed) {
        this.id = id;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.dosage = dosage;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.processed = processed;
    }
    
    // Getters
    public String getId() { return id; }
    public String getPatientName() { return patientName; }
    public String getDoctorName() { return doctorName; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public String getDosage() { return dosage; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public boolean isProcessed() { return processed; }
    
    // Setters
    public void setProcessed(boolean processed) { this.processed = processed; }
    
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
    
    public boolean isValid() {
        return !isExpired() && !processed;
    }
    
    @Override
    public String toString() {
        return String.join(",", 
            id,
            patientName, 
            doctorName, 
            medicineName, 
            String.valueOf(quantity), 
            dosage,
            issueDate.format(DATE_FORMATTER),
            expiryDate.format(DATE_FORMATTER),
            String.valueOf(processed)
        );
    }
    
    public static Prescription fromString(String data) {
        String[] parts = data.split(",");
        return new Prescription(
            parts[0], // id
            parts[1], // patientName
            parts[2], // doctorName
            parts[3], // medicineName
            Integer.parseInt(parts[4]), // quantity
            parts[5], // dosage
            LocalDate.parse(parts[6], DATE_FORMATTER), // issueDate
            LocalDate.parse(parts[7], DATE_FORMATTER), // expiryDate
            Boolean.parseBoolean(parts[8]) // processed
        );
    }
}
