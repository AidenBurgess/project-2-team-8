package com.softeng306.io;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.softeng306.domain.course.Course;
import com.softeng306.domain.course.courseregistration.CourseRegistration;
import com.softeng306.domain.mark.Mark;
import com.softeng306.domain.professor.Professor;
import com.softeng306.domain.student.Student;
import com.softeng306.managers.StudentMgr;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;


public class FILEMgr {

    private static final String studentFileName = "data/studentFile.json";
    private static final String courseFileName = "data/courseFile.json";
    private static final String professorFileName = "data/professorFile.json";
    private static final String courseRegistrationFileName = "data/courseRegistrationFile.json";
    private static final String markFileName = "data/markFile.json";

    /**
     * Write a new student information into the file.
     *
     * @param student a student to be added into the file
     */
    public static void writeStudentsIntoFile(Student student) {
        try {
            List<Student> students = loadStudents();
            students.add(student);

            writeToFile(studentFileName, students);
        } catch (IOException e) {
            System.out.println("Error in adding a student to the file.");
            e.printStackTrace();
        }
    }


    /**
     * Load all the students' information from file into the system.
     *
     * @return a list of all the students.
     */
    public static List<Student> loadStudents() {
        ObjectMapper objectMapper = new ObjectMapper();
        File studentFile = Paths.get(studentFileName).toFile();
        ArrayList<Student> allStudents = new ArrayList<>();

        try {
            allStudents = new ArrayList<>(Arrays.asList(objectMapper.readValue(studentFile, Student[].class)));
            updateStudentIDs(allStudents);
        } catch (IOException e) {
            System.out.println("Error occurs when loading students.");
            e.printStackTrace();
        }

        return allStudents;
    }

    /**
     * Set the recent student ID, let the newly added student have the ID onwards.
     * If there is no student in DB, set recentStudentID to 1800000 (2018 into Uni)
     *
     * @param students The students to update.
     */
    private static void updateStudentIDs(List<Student> students) {
        int recentStudentID = 0;
        for (Student student : students) {
            recentStudentID = Math.max(recentStudentID, Integer.parseInt(student.getStudentID().substring(1, 8)));
        }
        StudentMgr.setIdNumber(recentStudentID > 0 ? recentStudentID : 1800000);
    }

    /**
     * Write a new course information into the file.
     *
     * @param course a course to be added into file
     */
    public static void writeCourseIntoFile(Course course) {
        try {
            List<Course> courses = loadCourses();
            courses.add(course);

            writeToFile(courseFileName, courses);
        } catch (IOException e) {
            System.out.println("Error in adding a course to the file.");
            e.printStackTrace();
        }
    }

    /**
     * Load all the courses' information from file into the system.
     *
     * @return a list of all the courses.
     */
    public static List<Course> loadCourses() {
        ObjectMapper objectMapper = new ObjectMapper();
        File courseFile = Paths.get(courseFileName).toFile();
        ArrayList<Course> allCourses = new ArrayList<>();

        try {
            allCourses = new ArrayList<>(Arrays.asList(objectMapper.readValue(courseFile, Course[].class)));
        } catch (IOException e) {
            System.out.println("Error happens when loading courses.");
            e.printStackTrace();
        }

        return allCourses;
    }

    /**
     * Backs up all the changes of courses made into the file.
     * NOTE THAT BACKUPS MAY NOT WORK, NOT TESTED. WE shouldn't need them in the future.
     *
     * @param courses courses to be backed up
     */
    public static void backUpCourse(List<Course> courses) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(Paths.get(courseFileName).toFile(), courses);
        } catch (IOException e) {
            System.out.println("Error in backing up courses.");
            e.printStackTrace();
        }
    }

    /**
     * Load all the professors' information from file into the system.
     *
     * @return a list of all the professors.
     */
    public static List<Professor> loadProfessors() {
        ObjectMapper objectMapper = new ObjectMapper();
        File professorFile = Paths.get(professorFileName).toFile();
        ArrayList<Professor> allProfessors = new ArrayList<>();

        try {
            allProfessors = new ArrayList<>(Arrays.asList(objectMapper.readValue(professorFile, Professor[].class)));
        } catch (IOException e) {
            System.out.println("Error occurs when loading professors.");
            e.printStackTrace();
        }

        return allProfessors;
    }

    /**
     * Writes a new course registration record into the file.
     *
     * @param courseRegistration courseRegistration to be added into file
     */
    public static void writeCourseRegistrationIntoFile(CourseRegistration courseRegistration) {
        try {
            List<CourseRegistration> courseRegistrations = loadCourseRegistration();
            courseRegistrations.add(courseRegistration);

            writeToFile(courseRegistrationFileName, courseRegistrations);
        } catch (IOException e) {
            System.out.println("Error in adding a course registration to the file.");
            e.printStackTrace();
        }
    }

    /**
     * Load all the course registration records from file into the system.
     *
     * @return a list of all the course registration records.
     */
    public static List<CourseRegistration> loadCourseRegistration() {
        ObjectMapper objectMapper = new ObjectMapper();
        File courseRegistrationFile = Paths.get(courseRegistrationFileName).toFile();
        ArrayList<CourseRegistration> allCourseRegistrations = new ArrayList<>();

        try {
            allCourseRegistrations = new ArrayList<>(Arrays.asList(objectMapper.readValue(courseRegistrationFile, CourseRegistration[].class)));
        } catch (IOException e) {
            System.out.println("Error happens when loading courses.");
            e.printStackTrace();
        }

        return allCourseRegistrations;
    }


    /**
     * Writes a new student mark record into the file.
     *
     * @param mark mark to be updated into the file
     */
    public static void updateStudentMarks(Mark mark) {
        try {
            List<Mark> marks = loadStudentMarks();
            marks.add(mark);

            writeToFile(markFileName, marks);
        } catch (IOException e) {
            System.out.println("Error in adding a mark to the file.");
            e.printStackTrace();
        }
    }

    /**
     * Load all the student mark records from file into the system.
     *
     * @return a list of all the student mark records.
     */
    public static List<Mark> loadStudentMarks() {
        ObjectMapper objectMapper = new ObjectMapper();
        File markFile = Paths.get(markFileName).toFile();
        ArrayList<Mark> allStudentMarks = new ArrayList<>();

        try {
            allStudentMarks = new ArrayList<>(Arrays.asList(objectMapper.readValue(markFile, Mark[].class)));
        } catch (IOException e) {
            System.out.println("Error occurs when loading student marks.");
            e.printStackTrace();
        }

        return allStudentMarks;
    }

    /**
     * Backs up all the changes of student mark records made into the file.
     *
     * @param marks marks to be backed up into file
     */
    public static void backUpMarks(List<Mark> marks) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(Paths.get(markFileName).toFile(), marks);
        } catch (IOException e) {
            System.out.println("Error in adding a mark to the file.");
            e.printStackTrace();
        }
    }

    /**
     * Clears the file contents
     *
     * @param filename file to clear
     */
    private static void clearFileContents(String filename) {
        try {
            new PrintWriter(filename);
        } catch (FileNotFoundException e) {
            // don't do anything
        }
    }

    /**
     * Write information into a file
     *
     * @param filename          the file to write into
     * @param collectionToWrite the information to write
     * @throws IOException
     */
    private static void writeToFile(String filename, List<?> collectionToWrite) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        File file = Paths.get(filename).toFile();
        FileWriter fileWriter = new FileWriter(file, true);

        clearFileContents(filename);
        objectMapper.writeValue(fileWriter, collectionToWrite);
    }
}
