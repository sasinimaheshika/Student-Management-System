package Models;

public class Enrollment {
    private int studentId;
    private int courseId;
    private double marks;

    public Enrollment(int studentId, int courseId, double marks) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.marks = marks;
    }

    // Getters for Encapsulation
    public int getStudentId() { return studentId; }
    public int getCourseId() { return courseId; }
    public double getMarks() { return marks; }

    // Setters
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setMarks(double marks) { this.marks = marks; }
}