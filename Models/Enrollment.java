package Models;

public class Enrollment {
    private int student_id;
    private int courseId;
    private double marks;

    public Enrollment(int student_id, int course_id, double marks) {
        this.student_id = student_id;
        this.courseId = course_id;
        this.marks = marks;
    }
}