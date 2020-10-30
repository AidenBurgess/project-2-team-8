package com.softeng306.domain.course;

import com.softeng306.domain.course.group.IGroup;
import com.softeng306.domain.course.group.Group;
import com.softeng306.domain.exceptions.ProfessorNotFoundException;
import com.softeng306.domain.professor.Professor;
import com.softeng306.enums.CourseType;
import com.softeng306.enums.Department;
import com.softeng306.enums.GroupType;
import com.softeng306.managers.ProfessorMgr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseBuilder implements ICourseBuilder {
    private ICourse course;

    public CourseBuilder() {
        course = new Course();
    }

    @Override
    public void setCourseID(String id) {
        course.setID(id);
    }

    @Override
    public void setCourseName(String name) {
        course.setName(name);
    }

    @Override
    public void setProfInCharge(String profID) throws ProfessorNotFoundException {
        ProfessorMgr profMgr = ProfessorMgr.getInstance();
        //Guaranteed at this point that the ID is valid
        Professor profInCharge = profMgr.getProfessorFromID(profID);
        course.setProfInCharge(profInCharge);
    }

    @Override
    public void setTotalSeats(int totalSeats) {
        course.setTotalSeat(totalSeats);
        course.setVacancies(totalSeats);
    }

    @Override
    public void setLectureGroups(Map<String, Double> lectureGroups) {
        List<IGroup> newLectureGroups = convertMapToGroups(lectureGroups, GroupType.LECTURE_GROUP);

        course.setLectureGroups(newLectureGroups);
    }

    @Override
    public void setTutorialGroups(Map<String, Double> tutorialGroups) {
        List<IGroup> newTutorialGroups = convertMapToGroups(tutorialGroups, GroupType.TUTORIAL_GROUP);

        course.setTutorialGroups(newTutorialGroups);
    }

    @Override
    public void setLabGroups(Map<String, Double> labGroups) {
        List<IGroup> newLabGroups = convertMapToGroups(labGroups, GroupType.LAB_GROUP);

        course.setLabGroups(newLabGroups);
    }

    private List<IGroup> convertMapToGroups(Map<String, Double> groups, GroupType type) {
        List<IGroup> newGroups = new ArrayList<>();

        for(String groupName : groups.keySet()){
            IGroup group = new Group(groupName, groups.get(groupName).intValue(), groups.get(groupName).intValue(), type);
            newGroups.add(group);
        }

        return newGroups;
    }

    @Override
    public void setAcademicUnits(int academicUnits) {
        course.setAcademicUnits(academicUnits);
    }

    @Override
    public void setCourseDepartment(String department) {
        course.setCourseDepartment(Department.valueOf(department));
    }

    @Override
    public void setCourseType(String type) {
        course.setType(CourseType.valueOf(type));
    }

    @Override
    public void setLecWeeklyHour(int lecWeeklyHour) {
        course.setLecWeeklyHour(lecWeeklyHour);
    }

    @Override
    public void setTutWeeklyHour(int tutWeeklyHour) {
        course.setTutWeeklyHour(tutWeeklyHour);
    }

    @Override
    public void setLabWeeklyHour(int labWeeklyHour) {
        course.setLabWeeklyHour(labWeeklyHour);
    }

    @Override
    public ICourse build() {
        return course;
    }
}
