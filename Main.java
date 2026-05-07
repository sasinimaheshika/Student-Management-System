import java.util.Scanner;
import java.sql.*;
import Models.*; 

public class Main {
    // Using a delimiter to ensure nextLine() works correctly with nextInt()
    private static Scanner scanner = new Scanner(System.in).useDelimiter("\\n");
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("=== STUDENT INFORMATION SYSTEM ===");
        while (true) {
            if (currentUser == null) {
                login();
            } else {
                currentUser.displayMenu();
                System.out.print("Choose an option: ");
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    handleMenuChoice(choice);
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); 
                }
            }
        }
    }

    private static void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.next();
        System.out.print("Password: ");
        String password = scanner.next();

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
                if(currentUser != null) System.out.println("Login Successful! Welcome, " + username);
            } else {
                System.out.println("Invalid Credentials.");
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
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
                System.out.println("Error: Student record not found in the students table.");
            }
        } catch (SQLException e) {
            System.out.println("Fetch Error: " + e.getMessage());
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
        System.out.println("Logged out successfully.");
    }

    // --- FULL CRUD IMPLEMENTATIONS ---

    private static void addStudent() {
        System.out.print("Full Name: "); String name = scanner.next();
        System.out.print("Email: "); String email = scanner.next();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO students (name, email) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            System.out.println("Student Added Successfully.");
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void viewAllStudents() {
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM students");
            System.out.println("\n--- Student List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("student_id") + " | Name: " + rs.getString("name"));
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    private static void updateMarks() {
        System.out.print("Student ID: "); int sId = scanner.nextInt();
        System.out.print("New Marks: "); double marks = scanner.nextDouble();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE enrollments SET marks = ? WHERE student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, marks);
            pstmt.setInt(2, sId);
            int rows = pstmt.executeUpdate();
            if(rows > 0) System.out.println("Marks Updated.");
            else System.out.println("No enrollment found for this Student ID.");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    private static void deleteStudent() {
        System.out.print("Enter ID to Delete: ");
        int id = scanner.nextInt();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM students WHERE student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Deleted Successfully.");
            else System.out.println("Student ID not found.");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    private static void addCourse() {
        System.out.print("Course Name: "); String cName = scanner.next();
        System.out.print("Course Code: "); String cCode = scanner.next();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO courses (course_name, course_code) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cName);
            pstmt.setString(2, cCode);
            pstmt.executeUpdate();
            System.out.println("Course Added.");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    private static void viewMyResults() {
        try (Connection conn = DBConnection.getConnection()) {
            // Requirement: Join tables to show Course Names instead of just IDs[cite: 1]
            String sql = "SELECT c.course_name, e.marks FROM enrollments e " +
                         "JOIN courses c ON e.course_id = c.course_id WHERE e.student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentUser.getUserId());
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- My Results ---");
            while (rs.next()) {
                System.out.println("Course: " + rs.getString("course_name") + " | Marks: " + rs.getDouble("marks"));
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }
}