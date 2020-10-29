package com.softeng306.managers;

import com.softeng306.domain.course.component.MainComponent;
import com.softeng306.domain.course.component.SubComponent;
import com.softeng306.domain.mark.MainComponentMark;
import com.softeng306.domain.mark.Mark;
import com.softeng306.domain.mark.MarkCalculator;
import com.softeng306.domain.mark.SubComponentMark;
import com.softeng306.domain.student.Student;

import com.softeng306.fileprocessing.IFileProcessor;
import com.softeng306.fileprocessing.StudentFileProcessor;

import com.softeng306.enums.Department;
import com.softeng306.enums.Gender;

import com.softeng306.io.StudentMgrIO;
import com.softeng306.validation.StudentValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Manages the student related operations.
 * Contains addStudent, generateStudentId
 */
public class StudentMgr {
    private Scanner scanner = new Scanner(System.in);

    /**
     * A list of all the students in this school.
     */
    private List<Student> students;

    private static StudentMgr singleInstance = null;

    private final MarkCalculator markCalculator;

    private final IFileProcessor<Student> studentFileProcessor;

    /**
     * Override default constructor to implement singleton pattern
     */
    private StudentMgr() {
        studentFileProcessor = new StudentFileProcessor();
        markCalculator = new MarkCalculator();
        students = studentFileProcessor.loadFile();
    }

    /**
     * Return the StudentMgr singleton, if not initialised already, create an instance.
     *
     * @return StudentMgr the singleton instance
     */
    public static StudentMgr getInstance() {
        if (singleInstance == null) {
            singleInstance = new StudentMgr();
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

        currentStudent.setStudentSchool(Department.valueOf(StudentMgrIO.getSchoolName()));  //Set school
        currentStudent.setGender(Gender.valueOf(StudentMgrIO.getStudentGender()));      //gender
        currentStudent.setStudentYear(StudentMgrIO.getStudentYear());   //student year

        studentFileProcessor.writeNewEntryToFile(currentStudent);
        students.add(currentStudent);

        StudentMgrIO.printStudentData(currentStudent.getStudentName(), currentStudent.getStudentID());
    }

    /**
     * Prints transcript (Results of course taken) for a particular student
     */
    public void printStudentTranscript() {
        String studentID = readStudentFromUser().getStudentID();

        double studentGPA = 0d;
        int thisStudentAU = 0;

        List<Mark> thisStudentMark = new ArrayList<>();
        for (Mark mark : MarkMgr.getInstance().getMarks()) {
            if (mark.getStudent().getStudentID().equals(studentID)) {
                thisStudentMark.add(mark);
                thisStudentAU += mark.getCourse().getAU();
            }
        }

        if (thisStudentMark.isEmpty()) {
            System.out.println("------ No transcript ready for this student yet ------");
            return;
        }
        System.out.println("----------------- Official Transcript ------------------");
        System.out.print("Student Name: " + thisStudentMark.get(0).getStudent().getStudentName());
        System.out.println("\tStudent ID: " + thisStudentMark.get(0).getStudent().getStudentID());
        System.out.println("AU for this semester: " + thisStudentAU);
        System.out.println();


        for (Mark mark : thisStudentMark) {
            System.out.print("Course ID: " + mark.getCourse().getCourseID());
            System.out.println("\tCourse Name: " + mark.getCourse().getCourseName());

            for (MainComponentMark mainComponentMark : mark.getCourseWorkMarks()) {
                MainComponent mainComponent = mainComponentMark.getMainComponent();
                Double result = mainComponentMark.getMark();

                System.out.println("Main Assessment: " + mainComponent.getComponentName() + " ----- (" + mainComponent.getComponentWeight() + "%)");
                int mainAssessmentWeight = mainComponent.getComponentWeight();

                for (SubComponentMark subComponentMark : mainComponentMark.getSubComponentMarks()) {
                    SubComponent subComponent = subComponentMark.getSubComponent();
                    System.out.print("Sub Assessment: " + subComponent.getComponentName() + " -- (" + subComponent.getComponentWeight() + "% * " + mainAssessmentWeight + "%) --- ");
                    String subAssessmentName = subComponent.getComponentName();
                    System.out.println("Mark: " + String.valueOf(subComponentMark.getMark()));
                }

                System.out.println("Main Assessment Total: " + result);
                System.out.println();
            }

            System.out.println("Course Total: " + mark.getTotalMark());
            studentGPA += markCalculator.gpaCalculator(mark) * mark.getCourse().getAU();
            System.out.println();
        }
        studentGPA /= thisStudentAU;
        System.out.println("GPA for this semester: " + studentGPA);
        if (studentGPA >= 4.50) {
            System.out.println("On track of First Class Honor!");
        } else if (studentGPA >= 4.0) {
            System.out.println("On track of Second Upper Class Honor!");
        } else if (studentGPA >= 3.5) {
            System.out.println("On track of Second Lower Class Honor!");
        } else if (studentGPA >= 3) {
            System.out.println("On track of Third Class Honor!");
        } else {
            System.out.println("Advice: Study hard");
        }
        System.out.println("------------------ End of Transcript -------------------");

    }

    /**
     * Return the list of all students in the system.
     *
     * @return An list of all students.
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * Displays a list of IDs of all the students.
     */
    public void printAllStudentIds() {
        for (Student s : students) {
            System.out.println(s.getStudentID());
        }
    }

    /**
     * Generates the ID of a new student.
     *
     * @return the generated student ID.
     */
    private String generateStudentID() {
        int smallestAvailableIDNumber = findLargestStudentID();

        // randomly generate the last character from A-Z.
        int rand = new Random().nextInt();
        char randomEndNumber = (char) ((rand * (76-65) + 1) + 65);

        return "U" + (smallestAvailableIDNumber+1) + randomEndNumber;
    }

    /**
     * Find the largest student ID by the number value in all the students.
     * If there is no student in DB, this is default 1800000 (2018 into Uni)
     */
    private int findLargestStudentID() {
        int recentStudentID = 0;
        for (Student student : students) {
            recentStudentID = Math.max(recentStudentID, Integer.parseInt(student.getStudentID().substring(1, 8)));
        }

        return recentStudentID > 0 ? recentStudentID : 1800000;
    }

    /**
     * Prompts the user to input an existing student.
     *
     * @return the inputted student.
     */
    public Student readStudentFromUser() {
        String studentID;
        Student currentStudent = null;
        while (true) {
            System.out.println("Enter Student ID (-h to print all the student ID):");
            studentID = scanner.nextLine();
            while ("-h".equals(studentID)) {
                StudentMgr.getInstance().printAllStudentIds();
                studentID = scanner.nextLine();
            }

            currentStudent = StudentValidator.checkStudentExists(studentID);
            if (currentStudent == null) {
                System.out.println("Invalid Student ID. Please re-enter.");
            } else {
                break;
            }

        }
        return currentStudent;
    }
}
