package Models;

public class Course {
    private int courseId;
    private String courseName;
    private String courseCode;

    public Course(int courseId, String courseName, String courseCode) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
    }
}