package Models;

public abstract class User {
    private int user_id;
    private String username;
    private String password;
    private String role;

    public User(int user_id, String username, String password, String role) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters for Encapsulation[cite: 1, 8]
    public int getUserId() { return user_id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // Abstract method for Polymorphism[cite: 1, 8]
    public abstract void displayMenu();
}