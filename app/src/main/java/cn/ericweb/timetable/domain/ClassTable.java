package cn.ericweb.timetable.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author eric
 */
public class ClassTable implements Serializable {

    private static int DAY_PER_WEEK = 7;
    private ArrayList<Course> courseInfo;
    private ArrayList<DayInClassTable> dayInClassTable;
    private int courseNumberPerDay;

    private int startHour;
    private int startMinute;
    private ArrayList<Integer> intervalPerCourse;

    /**
     * 构造函数
     *
     * @param _courseNumberPerDay 每天有几节课程
     */
    public ClassTable(int _courseNumberPerDay) {
        courseNumberPerDay = _courseNumberPerDay;
        dayInClassTable = new ArrayList<DayInClassTable>(DAY_PER_WEEK);
        for (int i = 0; i < DAY_PER_WEEK; i++) {
            dayInClassTable.add(new DayInClassTable(courseNumberPerDay));
        }
    }

    /**
     * 获得CourseInClassTable
     *
     * @param weekday 周几
     * @param classIndex 第几节课
     * @return 如果不存在，返回空对象
     */
    public CourseInClassTable getCourseInClassTable(int weekday, int classIndex) {
        try {
            CourseInClassTable result = dayInClassTable.get(weekday).getCourse(classIndex);
            return result;
        } catch (Exception e) {
            return new CourseInClassTable();
        }
    }

    /**
     * 添加一个CourseInClassTable
     *
     * @param weekday 周几
     * @param classIndex 第几节课
     * @param target 目标CourseInClassTable
     */
    public void addCourseInClassTable(int weekday, int classIndex, CourseInClassTable target) {
        dayInClassTable.get(weekday).addNewClass(classIndex, target);
    }

    public void setCourseInfo(ArrayList<Course> _courseInfo) {
        courseInfo = _courseInfo;
    }

    public ArrayList<Course> getCourseInfo() {
        return courseInfo;
    }

    public void setDayInClassTable(ArrayList<DayInClassTable> _classTable) {
        dayInClassTable = _classTable;
    }

    public ArrayList<DayInClassTable> getDayInClassTable() {
        return dayInClassTable;
    }

    public void setCourseNumberPerDay(int _courseNumberPerDay) {
        courseNumberPerDay = _courseNumberPerDay;
    }

    public int getCourseNumberPerDay() {
        return courseNumberPerDay;
    }

    public void setStartHour(int _startHour) {
        startHour = _startHour;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartMinute(int _startMinute) {
        startMinute = _startMinute;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setIntervalPerCourse(ArrayList<Integer> _intervalPerCourse) {
        intervalPerCourse = _intervalPerCourse;
    }

    public ArrayList<Integer> getIntervalPerCourse() {
        return intervalPerCourse;
    }
}
