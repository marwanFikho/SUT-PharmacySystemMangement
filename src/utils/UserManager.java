package utils;

import java.io.*;
import java.util.*;

/**
 * Very small user-store:
 * ─ one line per user   →  username,password,role
 * ─ only ONE line may have role = Admin
 * File is created with default  admin,admin,Admin  if it doesn’t exist.
 */
public class UserManager {
    private static final String FILE = "users.txt";

    /* ─────────────────────────  PUBLIC API  ───────────────────────── */

    /** Ensure file exists with the default admin if missing. */
    public static void bootstrap() {
        File f = new File(FILE);
        if (!f.exists()) {
            saveAll(List.of("admin,admin,Admin"));   // default record
        }
    }
    
    /** Get all users as a 2D array (username, password, role) */
    public static String[][] getAllUsers() {
        List<String> userLines = loadAll();
        String[][] users = new String[userLines.size()][3];
        
        for (int i = 0; i < userLines.size(); i++) {
            users[i] = userLines.get(i).split(",");
        }
        
        return users;
    }

    /** true ⇢ credentials match the single admin account */
    public static boolean checkAdmin(String username, String password) {
        return loadAll().stream()
                .filter(l -> l.endsWith(",Admin"))
                .anyMatch(l -> {
                    String[] p = l.split(",");
                    return p[0].equals(username) && p[1].equals(password);
                });
    }

    /** Update admin UN or PW (pass null for the part you keep). */
    public static void updateAdminCreds(String newUser, String newPass) {
        List<String> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).endsWith(",Admin")) {
                String[] p = all.get(i).split(",");
                all.set(i, String.join(",",                     //
                        newUser == null ? p[0] : newUser,      //
                        newPass == null ? p[1] : newPass,      //
                        "Admin"));
                break;
            }
        }
        saveAll(all);
    }

    /* ───────────────────────  internal helpers  ───────────────────── */

    private static List<String> loadAll() {
        List<String> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) out.add(line.trim());
        } catch (IOException ignored) {}
        return out;
    }

    private static void saveAll(List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (String s : lines) pw.println(s);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static String checkCredentials(String username, String password) {
        for (String line : loadAll()) {
            String[] parts = line.split(",");
            if (parts.length == 3 && parts[0].equals(username) && parts[1].equals(password)) {
                return parts[2];
            }
        }
        return null; // invalid
    }

    public static boolean createUser(String username, String password, String role) {
        if (usernameExists(username)) return false;
        List<String> users = loadAll();
        users.add(username + "," + password + "," + role);
        saveAll(users);
        return true;
    }

    private static boolean usernameExists(String username) {
        return loadAll().stream()
                .anyMatch(l -> l.startsWith(username + ","));
    }
    
    /**
     * Delete a user by index
     * @param index The index of the user to delete
     * @return true if successful, false if it was the admin or index invalid
     */
    public static boolean deleteUser(int index) {
        List<String> users = loadAll();
        
        // Don't allow deleting the admin user
        if (index >= 0 && index < users.size() && !users.get(index).endsWith(",Admin")) {
            users.remove(index);
            saveAll(users);
            return true;
        }
        return false;
    }
    
    /**
     * Update a user's information
     * @param index Index of user to update
     * @param username New username
     * @param password New password
     * @param role New role
     * @return true if successful, false if username exists or can't update admin role
     */
    public static boolean updateUser(int index, String username, String password, String role) {
        List<String> users = loadAll();
        
        if (index < 0 || index >= users.size()) {
            return false;
        }
        
        String[] parts = users.get(index).split(",");
        boolean isAdmin = parts[2].equals("Admin");
        
        // If changing username, check it doesn't exist already
        if (!parts[0].equals(username) && usernameExists(username)) {
            return false;
        }
        
        // Don't allow changing Admin role
        if (isAdmin && !role.equals("Admin")) {
            return false;
        }
        
        // Don't allow changing to Admin role
        if (!isAdmin && role.equals("Admin")) {
            return false;
        }
        
        users.set(index, username + "," + password + "," + role);
        saveAll(users);
        return true;
    }
}