package com.softeng306.domain.course.courseregistration;

import com.softeng306.domain.course.Course;
import com.softeng306.domain.student.Student;

import java.util.Comparator;


public class CourseRegistration {
    private Student student;
    private Course course;
    private String lectureGroup;
    private String tutorialGroup;
    private String labGroup;

    /**
     * Default constructor. Required for Jackson serialization.
     */
    public CourseRegistration() {

    }

    public CourseRegistration(Student student, Course course, String lectureGroup, String tutorialGroup, String labGroup) {
        this.student = student;
        this.course = course;
        this.lectureGroup = lectureGroup;
        this.tutorialGroup = tutorialGroup;
        this.labGroup = labGroup;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public String getLectureGroup() {
        return lectureGroup;
    }

    public String getTutorialGroup() {
        return tutorialGroup;
    }

    public String getLabGroup() {
        return labGroup;
    }
}
