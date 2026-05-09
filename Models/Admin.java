package Models;

public class Admin extends User {
    private String employee_id;

    public Admin(int userId, String username, String password, String employeeId) {
        super(userId, username, password, "ADMIN");
        this.employee_id = employeeId;
    }

    @Override
    public void displayMenu() {
        System.out.println("\n --------------------");
        System.out.println("|     Admin Menu     |");
        System.out.println(" --------------------");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Add New Course");
        System.out.println("4. Update Marks");
        System.out.println("5. Delete Student Record");
        System.out.println("6. Logout");
    }
}