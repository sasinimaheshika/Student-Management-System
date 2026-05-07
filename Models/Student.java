package Models;

public class Student extends User {
    private String name;
    private String email;
    private String phone;

    public Student(int userId, String username, String password, String name, String email, String phone) {
        super(userId, username, password, "STUDENT");
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public void displayMenu() {
        System.out.println("\n--- Student Menu ---");
        System.out.println("1. View My Results (READ)");
        System.out.println("2. View My Profile");
        System.out.println("3. Logout");
    }
}