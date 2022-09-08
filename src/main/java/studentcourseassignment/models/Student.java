package studentcourseassignment.models;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Student {
    Integer id;
    String name;
    Integer credit;
    String course;
    String grade;

    @JsonIgnore
    Map<Course, Grade> courseGrade;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Map<Course, Grade> getCourseGrade() {
        return courseGrade;
    }

    public void setCourseGrade(Map<Course, Grade> courseGrade) {
        this.courseGrade = courseGrade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }


}
