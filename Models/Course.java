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

    // Getters for Encapsulation
    public int getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getCourseCode() { return courseCode; }

    // Setters
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
}