package Models;

public class Admin extends User {
    private String employeeId;

    public Admin(int userId, String username, String password, String employeeId) {
        super(userId, username, password, "ADMIN");
        this.employeeId = employeeId;
    }

    @Override
    public void displayMenu() {
        System.out.println("\n--- Admin Menu ---");
        System.out.println("1. Add Student (CREATE)");
        System.out.println("2. View All Students (READ)");
        System.out.println("3. Add New Course");
        System.out.println("4. Update Marks (UPDATE)");
        System.out.println("5. Delete Student Record (DELETE)"); // Added for CRUD
        System.out.println("6. Logout");
    }
}