package Models;

public class Admin extends User {
    private String employee_id;

    public Admin(int user_id, String username, String password, String employee_id) {
        super(user_id, username, password, "ADMIN");
        this.employee_id = employee_id;
    }

    @Override
    public void displayMenu() {
        System.out.println("\n--- Admin Menu ---");
        System.out.println("1. Add Student (CREATE)");
        System.out.println("2. View All Students (READ)");
        System.out.println("3. Add New Course");
        System.out.println("4. Update Marks (UPDATE)");
        System.out.println("5. Delete Student Record (DELETE)");
        System.out.println("6. Logout");
    }
}