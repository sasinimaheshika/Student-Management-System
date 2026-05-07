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
}