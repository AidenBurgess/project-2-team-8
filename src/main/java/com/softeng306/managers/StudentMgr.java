package com.softeng306.managers;


import com.softeng306.domain.student.Student;
import com.softeng306.io.FILEMgr;
import com.softeng306.io.StudentMgrIO;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the student related operations.
 * Contains addStudent, generateStudentId
 */
public class StudentMgr {
    /**
     * A list of all the students in this school.
     */
    public static List<Student> students = new ArrayList<>(0);

    private static StudentMgr singleInstance = null;


    /**
     * Uses idNumber to generate student ID.
     */
    private static int idNumber = 1800000;

    /**
     * Override default constructor to implement singleton pattern
     */
    private StudentMgr(List<Student> students) {
        this.students = students;
    }

    /**
     * Return the StudentMgr singleton, if not initialised already, create an instance.
     *
     * @return StudentMgr the singleton instance
     */
    public static StudentMgr getInstance() {
        if (singleInstance == null) {
            singleInstance = new StudentMgr(FILEMgr.loadStudents());
        }

        return singleInstance;
    }


    /**
     * Adds a student and put the student into file
     */
    public void addStudent() {
        String studentID = null;
        StudentMgrIO.printMenu();

        boolean systemGeneratedID = StudentMgrIO.systemGenerateID();
        if (!systemGeneratedID) {
            studentID = StudentMgrIO.getStudentID();
        } else {
            studentID = generateStudentID();
        }

        String studentName = StudentMgrIO.getStudentName();
        Student currentStudent = new Student(studentID, studentName);

        currentStudent.setStudentSchool(StudentMgrIO.getSchoolName());  //Set school
        currentStudent.setGender(StudentMgrIO.getStudentGender());      //gender
        currentStudent.setStudentYear(StudentMgrIO.getStudentYear());   //student year

        FILEMgr.writeStudentsIntoFile(currentStudent);
        StudentMgr.students.add(currentStudent);

        StudentMgrIO.printStudentID(currentStudent.getStudentName(), currentStudent.getStudentID());
    }

    /**
     * Displays a list of IDs of all the students.
     */
    public void printAllStudentIds() {
        for (Student s : StudentMgr.students) {
            System.out.println(s.getStudentID());
        }
    }

    /**
     * Sets the idNumber variable of this student manager class.
     *
     * @param idNumber static variable idNumber of this class.
     */
    public static void setIdNumber(int idNumber) {
        StudentMgr.idNumber = idNumber;
    }

    /**
     * Generates the ID of a new student.
     *
     * @return the generated student ID.
     */
    private String generateStudentID() {
        String generateStudentID;
        boolean studentIDUsed;
        do {
            int rand = (int) (Math.random() * ((76 - 65) + 1)) + 65;
            String lastPlace = Character.toString((char) rand);
            idNumber += 1;
            generateStudentID = "U" + String.valueOf(idNumber) + lastPlace;
            studentIDUsed = false;
            for (Student student : this.students) {
                if (generateStudentID.equals(student.getStudentID())) {
                    studentIDUsed = true;
                    break;
                }
            }
            if (!studentIDUsed) {
                break;
            }
        } while (true);
        return generateStudentID;
    }
}
