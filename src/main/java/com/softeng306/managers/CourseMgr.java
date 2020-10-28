package com.softeng306.managers;

import com.softeng306.domain.course.group.Group;
import com.softeng306.domain.mark.MarkCalculator;
import com.softeng306.enums.Department;
import com.softeng306.domain.course.Course;
import com.softeng306.domain.course.component.MainComponent;
import com.softeng306.domain.course.component.SubComponent;
import com.softeng306.domain.mark.Mark;
import com.softeng306.domain.course.ICourseBuilder;
import com.softeng306.enums.GroupType;
import com.softeng306.io.CourseMgrIO;
import com.softeng306.io.FILEMgr;
import com.softeng306.io.MainMenuIO;

import java.util.*;


public class CourseMgr {
    /**
     * A list of all the courses in this school.
     */
    private List<Course> courses;

    private static CourseMgr singleInstance = null;

    private CourseMgrIO courseMgrIO = new CourseMgrIO();

    private MarkCalculator markCalculator = new MarkCalculator();

    /**
     * Override default constructor to implement singleton pattern
     */
    private CourseMgr(List<Course> courses) {
        this.courses = courses;
    }

    /**
     * Return the CourseMgr singleton, if not initialised already, create an instance.
     *
     * @return CourseMgr the singleton instance
     */
    public static CourseMgr getInstance() {
        if (singleInstance == null) {
            singleInstance = new CourseMgr(FILEMgr.loadCourses());
        }

        return singleInstance;
    }

    /**
     * Creates a new course and stores it in the file.
     */
    public void addCourse(ICourseBuilder completeBuilder) {

        Course course = completeBuilder.build();
        int addCourseComponentChoice;

        // Update Course in files
        FILEMgr.writeCourseIntoFile(course);
        courses.add(course);

        addCourseComponentChoice = courseMgrIO.readCreateCourseComponentChoice();

        // Don't add course components option selected
        if (addCourseComponentChoice == 2) {
            courseMgrIO.printComponentsNotInitialized(course.getCourseID());
        } else {
            enterCourseWorkComponentWeightage(course);
            courseMgrIO.printCourseAdded(course.getCourseID());
        }
        courseMgrIO.printCourses(courses);
    }

    /**
     * Checks whether a course (with all of its groups) have available slots and displays the result.
     */
    public void checkAvailableSlots() {
        //printout the result directly
        MainMenuIO.printMethodCall("checkAvailableSlots");

        while (true) {
            Course currentCourse = readCourseFromUser();
            if (currentCourse != null) {
                courseMgrIO.printCourseInfoString(this.generateCourseInformation(currentCourse));
                courseMgrIO.printVacanciesForGroups(this.generateGroupInformation(currentCourse.getLectureGroups()),GroupType.LAB_GROUP);

                if (currentCourse.getTutorialGroups() != null) {
                    System.out.println();
                    courseMgrIO.printVacanciesForGroups(this.generateGroupInformation(currentCourse.getTutorialGroups()), GroupType.TUTORIAL_GROUP);
                }

                if (currentCourse.getLabGroups() != null) {
                    System.out.println();
                    courseMgrIO.printVacanciesForGroups(this.generateGroupInformation(currentCourse.getLabGroups()), GroupType.LAB_GROUP);
                }
                break;
            } else {
                courseMgrIO.printCourseNotExist();
            }
        }
    }

    /**
     * Sets the course work component weightage of a course.
     *
     * @param currentCourse The course which course work component is to be set.
     */
    public void enterCourseWorkComponentWeightage(Course currentCourse) {
        // Assume when course is created, no components are added yet
        // Assume once components are created and set, cannot be changed.

        MainMenuIO.printMethodCall("enterCourseWorkComponentWeightage");
        if (currentCourse == null) {
            currentCourse = readCourseFromUser();
        }
        HashSet<String> mainComponentNames = new HashSet<>();
        List<MainComponent> mainComponents = new ArrayList<>(0);
        // Check if mainComponent is empty
        if (currentCourse.getMainComponents().isEmpty()) {
            // empty course
            courseMgrIO.printEmptyCourseComponents(currentCourse);

            int hasFinalExamChoice = 0;
            int examWeight = 0;
            while (hasFinalExamChoice < 1 || hasFinalExamChoice > 2) {
                hasFinalExamChoice = courseMgrIO.readHasFinalExamChoice();
                if (hasFinalExamChoice == 1) {
                    examWeight = courseMgrIO.readExamWeight();
                    MainComponent exam = new MainComponent("Exam", examWeight, new ArrayList<>(0));
                    mainComponents.add(exam);
                } else if (hasFinalExamChoice == 2) {
                    courseMgrIO.printEnterContinuousAssessments();
                }
            }

            int numberOfMain = courseMgrIO.readNoOfMainComponents();

            while (true) {
                int totalWeightage = 100 - examWeight;
                for (int i = 0; i < numberOfMain; i++) {
                    HashMap<String, Double> subComponentsMap = new HashMap<>();
                    String mainComponentName = courseMgrIO.readMainComponentName(totalWeightage, i, mainComponentNames);
                    mainComponentNames.add(mainComponentName);

                    int weight = courseMgrIO.readMainComponentWeightage(i, totalWeightage);
                    totalWeightage -= weight;

                    int noOfSub = courseMgrIO.readNoOfSubComponents(i);

                    subComponentsMap = courseMgrIO.readSubComponents(noOfSub);

                    List<SubComponent> subComponentsList = new ArrayList<SubComponent>();
                    for (String key : subComponentsMap.keySet()){
                        SubComponent subComponent = new SubComponent(key, subComponentsMap.get(key).intValue());
                        subComponentsList.add(subComponent);
                    }

                    MainComponent main = new MainComponent(mainComponentName, weight, subComponentsList);
                    mainComponents.add(main);
                }


                if (totalWeightage != 0) {
                    // weightage assign is not tallied
                    courseMgrIO.printWeightageError();
                    mainComponents.clear();
                    mainComponentNames.clear();
                } else {
                    break;
                }
            }
            //set maincomponent to course
            currentCourse.setMainComponents(mainComponents);

        } else {
            courseMgrIO.printCourseworkWeightageEnteredError();
        }

        courseMgrIO.printComponentsForCourse(currentCourse);

        // Update course into course.csv
    }


    /**
     * Displays a list of IDs of all the courses.
     */
    public void printAllCourseIds() {
        courseMgrIO.printAllCourseIds(courses);
    }

    public List<String> getCourseIdsInDepartment(Department department) {
        List<Course> validCourses = new ArrayList<>();
        courses.forEach(course -> {
            if (department.toString().equals(course.getCourseDepartment().toString())) {
                validCourses.add(course);
            }
        });

        List<String> courseIdsForDepartment = new ArrayList<>();
        validCourses.forEach(course -> {
            String courseID = course.getCourseID();
            courseIdsForDepartment.add(courseID);
        });
        return courseIdsForDepartment;
    }

    /**
     * Prints the course statics including enrollment rate, average result for every assessment component and the average overall performance of this course.
     */
    public void printCourseStatistics() {
        MainMenuIO.printMethodCall("printCourseStatistics");

        Course currentCourse = readCourseFromUser();
        String courseID = currentCourse.getCourseID();

        List<Mark> courseMarks = new ArrayList<>(0);
        for (Mark mark : MarkMgr.getInstance().getMarks()) {
            if (mark.getCourse().getCourseID().equals(courseID)) {
                courseMarks.add(mark);
            }
        }

        courseMgrIO.printCourseStatisticsHeader(currentCourse);

        MainComponent exam = null;

        // Find marks for every assessment components
        for (MainComponent mainComponent : currentCourse.getMainComponents()) {
            String componentName = mainComponent.getComponentName();

            if (componentName.equals("Exam")) {
//                Leave the exam report to the last
                exam = mainComponent;
            } else {
                courseMgrIO.printMainComponent(mainComponent.getComponentName(),mainComponent.getComponentWeight(), markCalculator.computeComponentMark(courseMarks, mainComponent.getComponentName()));
                List<SubComponent> subComponents = mainComponent.getSubComponents();
                if (!subComponents.isEmpty()) {
                    HashMap<String, Integer> subComponentInformation = this.generateSubComponentInformation(subComponents);
                    HashMap<String, Double> subComponentMarks = this.generateComponentMarkInformation(subComponents,courseMarks);
                    courseMgrIO.printSubcomponents(subComponentInformation, subComponentMarks);
                }
            }
        }

        if (exam != null) {
            courseMgrIO.printExamStatistics(exam.getComponentWeight(), markCalculator.computeExamMark(courseMarks));
        } else {
            courseMgrIO.printNoExamMessage();
        }
        courseMgrIO.printOverallPerformance(markCalculator.computerOverallMark(courseMarks));
    }


    /**
     * Prompts the user to input an existing course.
     *
     * @return the inputted course.
     */
    public Course readCourseFromUser() {
        return courseMgrIO.readCourseFromUser();
    }

    /**
     * Prompts the user to input an existing department.
     *
     * @return the inputted department.
     */
    public String readDepartmentFromUser() {
        return courseMgrIO.readDepartmentWithMoreThanOneCourseFromUser();
    }

    /**
     * Return the list of all courses in the system.

     * @return An list of all courses.
     */
    public List<Course> getCourses() {
        return courses;
    }

    public String generateCourseInformation(Course course){
        String infoString = course.getCourseID() + " " + course.getCourseName() + " (Available/Total): " + course.getVacancies() + "/" + course.getTotalSeats();
        return infoString;
    }

    public String[][] generateGroupInformation(List<Group> groups){
        String[][] groupInfo = new String[groups.size()][3];
        for(int i = 0; i<groups.size(); i++){
            groupInfo[i][0] = groups.get(i).getGroupName();
            groupInfo[i][1] = String.valueOf(groups.get(i).getAvailableVacancies());
            groupInfo[i][2] = String.valueOf(groups.get(i).getTotalSeats());
        }
        return groupInfo;
    }

    public HashMap<String, Integer> generateSubComponentInformation(List<SubComponent> subComponents){
        HashMap<String, Integer> map = new HashMap<>();
        for(SubComponent subComponent : subComponents){
            map.put(subComponent.getComponentName(), subComponent.getComponentWeight());
        }
        return map;
    }

    public HashMap<String, Double> generateComponentMarkInformation(List<SubComponent> subComponents, List<Mark> marks){
        HashMap<String, Double> map = new HashMap<>();
        for(SubComponent subComponent : subComponents){
            double mark = markCalculator.computeComponentMark(marks, subComponent.getComponentName());
            map.put(subComponent.getComponentName(), mark);
        }
        return map;
    }


}