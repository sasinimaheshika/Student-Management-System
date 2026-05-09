import java.util.Scanner;
import java.sql.*;
import Models.*; 

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("\n ------------------------------------");
        System.out.println("|     STUDENT INFORMATION SYSTEM     |");
        System.out.println(" ------------------------------------");
        while (true) {
            if (currentUser == null) {
                login();
            } else {
                currentUser.displayMenu();
                System.out.print("> Choose an option:- ");
                try {
                    if (scanner.hasNextInt()) {
                        int choice = scanner.nextInt();
                        scanner.nextLine(); // Consume the newline
                        handleMenuChoice(choice);
                    } else {
                    System.out.println("Sorry! Invalid input. Please enter a number.");
                        scanner.nextLine(); // Clear the invalid input
                    }
                } catch (Exception e) {
                    System.out.println("Sorry! Error reading input.");
                    scanner.nextLine();
                }
            }
        }
    }

    private static void login() {
        System.out.println("\n ---------------");
        System.out.println("|     Login     |");
        System.out.println(" ---------------");
        System.out.print("> Enter the username:- ");
        String username = scanner.nextLine().trim();
        System.out.print("> Enter the password:- ");
        String password = scanner.nextLine().trim();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int id = rs.getInt("user_id");

                if (role.equalsIgnoreCase("ADMIN")) {
                    currentUser = new Admin(id, username, password, "EMP" + id);
                } else {
                    // FIXED: Fetching actual Student details from the 'students' table
                    fetchStudentDetails(id, username, password);
                }
                if(currentUser != null) System.out.println("\nCongratulation Login Successful!\n\n\t<-   WELCOME TO THE SYSTEM " + username.toUpperCase()+"   ->");
            } else {
                System.out.println("\tSorry! Invalid Credentials.");
            }
        } catch (SQLException e) {
            System.out.println("\n Sorry! Database Connection Failed.");
            System.out.println("Error: " + e.getMessage());
            System.out.println("\n\t<  Please check if the database server is running.  >\n");
        }
    }

    private static void fetchStudentDetails(int userId, String user, String pass) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM students WHERE student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentUser = new Student(userId, user, pass, rs.getString("name"), rs.getString("email"), "N/A");
            } else {
                System.out.println("Error: Student record not found in the database.");
            }
        } catch (SQLException e) {
            System.out.println("\n Sorry! Database Connection Failed.");
            System.out.println("Error: " + e.getMessage());
            System.out.println("Please check if the database server is running.\n");
        }
    }

    private static void handleMenuChoice(int choice) {
        if (currentUser instanceof Admin) {
            switch (choice) {
                case 1: addStudent(); break;
                case 2: viewAllStudents(); break;
                case 3: addCourse(); break;
                case 4: updateMarks(); break;
                case 5: deleteStudent(); break; 
                case 6: logout(); break;
            }
        } else {
            switch (choice) {
                case 1: viewMyResults(); break;
                case 2: System.out.println("User: " + currentUser.getUsername()); break;
                case 3: logout(); break;
            }
        }
    }

    private static void logout() {
        currentUser = null;
        System.out.println("\n\t<-   Logged out successfully!   ->");
    }

    // --- FULL CRUD IMPLEMENTATIONS ---

    private static void addStudent() {
        System.out.print("> Enter full name:- "); String name = scanner.nextLine().trim();
        System.out.print("> Enter email:- "); String email = scanner.nextLine().trim();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO students (name, email) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            System.out.println("\n\t<-   Student added successfully!   ->");
        } catch (SQLException e) { 
            System.out.println(" Database Error: " + e.getMessage());
        }
    }

    private static void viewAllStudents() {
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM students");
            System.out.println("\n ----------------------");
        System.out.println("|     Student List     |");
        System.out.println(" ----------------------");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("student_id") + " | Name: " + rs.getString("name"));
            }
        } catch (SQLException e) { 
            System.out.println("Sorry! Database Connection Failed");
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateMarks() {
        System.out.print("> Enter student ID:- "); int sId = scanner.nextInt(); scanner.nextLine();
        System.out.print("> Enter the new marks:- "); double marks = scanner.nextDouble(); scanner.nextLine();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE enrollments SET marks = ? WHERE student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, marks);
            pstmt.setInt(2, sId);
            int rows = pstmt.executeUpdate();
            if(rows > 0) System.out.println("\n\t<-   Marks Updated!   ->");
            else System.out.println("\nSorry! no enrollment found for this Student ID.\n");
        } catch (SQLException e) { 
            System.out.println(" Database Error: " + e.getMessage());
        }
    }

    private static void deleteStudent() {
        System.out.print("> Enter ID to delete:- ");
        int id = scanner.nextInt();
        scanner.nextLine();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM students WHERE student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("\n\t<-   Deleted successfully!   ->");
            else System.out.println("Student ID not found.");
        } catch (SQLException e) { 
            System.out.println(" Database Error: " + e.getMessage());
        }
    }

    private static void addCourse() {
        System.out.print("> Enter new course name:-"); String cName = scanner.nextLine().trim();
        System.out.print("> Enter new course code:-"); String cCode = scanner.nextLine().trim();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO courses (course_name, course_code) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cName);
            pstmt.setString(2, cCode);
            pstmt.executeUpdate();
            System.out.println("\n\t<-   Course added successfully!   ->");
        } catch (SQLException e) { 
            System.out.println(" Database Error: " + e.getMessage());
        }
    }

    private static void viewMyResults() {
        try (Connection conn = DBConnection.getConnection()) {
            // Requirement: Join tables to show Course Names instead of just IDs[cite: 1]
            String sql = "SELECT c.course_name, e.marks FROM enrollments e " +
                         "JOIN courses c ON e.course_id = c.course_id WHERE e.student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentUser.getUserId());
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n ------------------");
            System.out.println("|    My Results    |");
            System.out.println(" ------------------ ");
            while (rs.next()) {
                System.out.println("Course: " + rs.getString("course_name") + " | Marks: " + rs.getDouble("marks"));
            }
        } catch (SQLException e) { 
            System.out.println(" Database Connection Failed!");
            System.out.println("Error: " + e.getMessage());
        }
    }
}