import java.util.Scanner;
import java.sql.*;
import Models.*; 

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("\n\t\t\t " + drawLine("-", 40));
        System.out.println("\t\t\t|       STUDENT INFORMATION SYSTEM       |");
        System.out.println("\t\t\t " + drawLine("-", 40));
        
        while (true) {
            if (currentUser == null) {
                login();
            } else {
                currentUser.displayMenu();
                System.out.print("\n  > Choose an option:- ");
                try {
                    if (scanner.hasNextInt()) {
                        int choice = scanner.nextInt();
                        scanner.nextLine(); 
                        handleMenuChoice(choice);
                    } else {
                        System.out.println("Invalid input. Please enter a number.");
                        scanner.nextLine(); 
                    }
                } catch (Exception e) {
                    System.out.println("Error reading input.");
                    scanner.nextLine();
                }
            }
        }
    }

    private static void login() {
        System.out.println("\n " + drawLine("-", 15));
        System.out.println("|     Login     |");
        System.out.println(" " + drawLine("-", 15));
        System.out.print("> Enter your username: ");
        String username = scanner.nextLine().trim();
        System.out.print("> Enter your password: ");
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
                    fetchStudentDetails(id, username, password);
                }
                
                if(currentUser != null) {
                    System.out.println("\n\t<-   WELCOME TO THE SYSTEM " + username.toUpperCase() + "   ->");
                }
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
                currentUser = new Student(userId, user, pass, 
                    rs.getString("name"), 
                    rs.getString("email"), 
                    rs.getString("phone"));
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void handleMenuChoice(int choice) {
        if (currentUser instanceof Admin) {
            switch (choice) {
                case 1: addStudent(); break;
                case 2: deleteStudent(); break;
                case 3: updateStudent(); break; 
                case 4: viewAllStudents(); break;
                case 5: addCourse(); break;
                case 6: viewAllCourses(); break;
                case 7: addMarks(); break; 
                case 8: viewAllMarks(); break;
                case 9: logout(); break;
                default: System.out.println("Invalid choice.");
            }
        } else {
            switch (choice) {
                case 1: viewMyResults(); break;
                case 2: displayProfile(); break;
                case 3: logout(); break;
            }
        }
    }

    private static void displayProfile() {
        if (currentUser instanceof Student) {
            Student s = (Student) currentUser;
            System.out.println("\n " + drawLine("-", 20));
        System.out.println("|     My Profile     |");
        System.out.println(" " + drawLine("-", 20));
            System.out.println("> Student Name: " + s.getName());
            System.out.println("> Student Email: " + s.getEmail());
            System.out.println("> Student Phone: " + s.getPhone());
        }
    }

    private static void logout() {
        currentUser = null;
        System.out.println("\n <  Logged out Successfully.  >");
    }

    private static void addStudent() {
        System.out.println("\n --------------------------");
        System.out.println("|      Add New Student     |");
        System.out.println(" --------------------------");
        System.out.print("> Enter Full Name: "); String name = scanner.nextLine().trim();
        System.out.print("> Enter Email: "); String email = scanner.nextLine().trim();
        System.out.print("> Enter Phone: "); String phone = scanner.nextLine().trim();
        System.out.print("> Enter Username for student: "); String uname = scanner.nextLine().trim();
        System.out.print("> Enter Password for student: "); String pword = scanner.nextLine().trim();

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'STUDENT')";
            PreparedStatement p1 = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            p1.setString(1, uname);
            p1.setString(2, pword);
            p1.executeUpdate();

            ResultSet rs = p1.getGeneratedKeys();
            if (rs.next()) {
                int newId = rs.getInt(1);
                String studentSql = "INSERT INTO students (student_id, name, email, phone) VALUES (?, ?, ?, ?)";
                PreparedStatement p2 = conn.prepareStatement(studentSql);
                p2.setInt(1, newId);
                p2.setString(2, name);
                p2.setString(3, email);
                p2.setString(4, phone);
                p2.executeUpdate();
            }

            conn.commit();
            System.out.println("\n <  Student & Logins Created Successfully.  >");
        } catch (SQLException e) {
            try { if(conn != null) conn.rollback(); } catch(SQLException ex) {}
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    private static void deleteStudent() {
        System.out.println("\n -------------------------");
        System.out.println("|      Delete Student     |");
        System.out.println(" -------------------------");
        System.out.print("> Enter Student ID to delete: ");
        int id = scanner.nextInt();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            if(pstmt.executeUpdate() > 0) System.out.println("\n <  Student records removed.  >");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    private static void updateStudent() {
        System.out.println("\n -------------------------");
        System.out.println("|      Update Student     |");
        System.out.println(" -------------------------");
        System.out.print("> Enter Student ID to update: ");
        int id = scanner.nextInt(); scanner.nextLine(); 
        System.out.print("> Enter New Full Name: "); String name = scanner.nextLine().trim();
        System.out.print("> Enter New Email: "); String email = scanner.nextLine().trim();
        System.out.print("> Enter New Phone: "); String phone = scanner.nextLine().trim();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE students SET name = ?, email = ?, phone = ? WHERE student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setInt(4, id);

            if (pstmt.executeUpdate() > 0) {
                System.out.println("\n\t<  Student Profile Updated Successfully!  >");
            } else {
                System.out.println("Student ID not found.");
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void viewAllStudents() {
        System.out.println("\n ----------------------------");
        System.out.println("|      View All Students     |");
        System.out.println(" ----------------------------");
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM students");
            System.out.println(" " + drawLine("-", 73));
            System.out.printf("| %-5s | %-20s | %-25s | %-12s |%n", "ID", "Name", "Email", "Phone");
            System.out.println(" " + drawLine("-", 73));
            while (rs.next()) {
                System.out.printf("| %-5d | %-20s | %-25s | %-12s |%n",
                    rs.getInt("student_id"), rs.getString("name"), rs.getString("email"), rs.getString("phone"));
            }
            System.out.println(" " + drawLine("-", 73));
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    private static void addCourse() {
        System.out.println("\n -------------------------");
        System.out.println("|      Add New Course     |");
        System.out.println(" -------------------------");
        System.out.print("> Enter Course Name: "); String name = scanner.nextLine();
        System.out.print("> Enter Course Code: "); String code = scanner.nextLine();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO courses (course_name, course_code) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, code);
            pstmt.executeUpdate();
            System.out.println("\n <  Course Added Successfully.  >");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    private static void viewAllCourses() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM courses";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            
            System.out.println("\n ----------------------");
            System.out.println("|      Course List     |");
            System.out.println(" ----------------------");
            System.out.println(" "+drawLine("-", 58));
            System.out.printf("| %-5s | %-30s | %-15s |%n", "ID", "Course Name", "Course Code");
            System.out.println(" "+drawLine("-", 58));
            
            while (rs.next()) {
                System.out.printf("| %-5d | %-30s | %-15s |%n",
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getString("course_code"));
            }
            
            System.out.println(" "+drawLine("-", 58));
            
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    private static void addMarks() {
        System.out.println("\n -----------------------");
        System.out.println("|       Add Marks       |");
        System.out.println(" -----------------------");
        System.out.print("> Enter Student ID: "); int sId = scanner.nextInt();
        System.out.print("> Enter Course ID: "); int cId = scanner.nextInt();
        System.out.print("> Enter Marks: "); double m = scanner.nextDouble();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO enrollments (student_id, course_id, marks) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sId);
            pstmt.setInt(2, cId);
            pstmt.setDouble(3, m);
            if(pstmt.executeUpdate() > 0) System.out.println("\n <  Marks Added Successfully.  >");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    private static void viewAllMarks() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT s.name, c.course_name, e.marks " +
                         "FROM enrollments e " +
                         "JOIN students s ON e.student_id = s.student_id " +
                         "JOIN courses c ON e.course_id = c.course_id";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            System.out.println("\n " + drawLine("-", 66));
            System.out.printf("| %-20s | %-30s | %-8s |%n", "Student Name", "Course Name", "Marks");
            System.out.println(" " + drawLine("-", 66));

            while (rs.next()) {
                System.out.printf("| %-20s | %-30s | %-8.2f |%n",
                    rs.getString("name"), rs.getString("course_name"), rs.getDouble("marks"));
            }
            System.out.println(" " + drawLine("-", 66));
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void viewMyResults() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT c.course_name, e.marks FROM enrollments e " +
                         "JOIN courses c ON e.course_id = c.course_id WHERE e.student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentUser.getUserId());
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n " + drawLine("-", 20));
            System.out.println("|     My Results     |");
            System.out.println(" " + drawLine("-", 20));
            while (rs.next()) {
                System.out.println(rs.getString("course_name") + ": " + rs.getDouble("marks"));
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    private static String drawLine(String character, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(character);
        return sb.toString();
    }
}