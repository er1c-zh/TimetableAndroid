package cn.ericweb.timetable.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author eric
 */
public class DayInClassTable implements Serializable {

    private ArrayList<ClassInClassTable> dayTable;

    public DayInClassTable(int courseNumberPerDay) {
        dayTable = new ArrayList<ClassInClassTable>();

        for (int i = 0; i < courseNumberPerDay; i++) {
            dayTable.add(i, new ClassInClassTable());
        }
    }

    public CourseInClassTable getCourse(int classIndex) {
        try {
            CourseInClassTable result = dayTable.get(classIndex).getCourseInAClass().get(0);
            return result;
        } catch (Exception e) {
            return new CourseInClassTable();
        }
    }

    public void addNewClass(int indexOfClass, CourseInClassTable target) {
        dayTable.get(indexOfClass).addCourse(target);
    }

    public void removeClass(int indexOfClass, CourseInClassTable target) {
        if (null != dayTable.get(indexOfClass)) {
            dayTable.get(indexOfClass).remove(target);
        }
    }

    public void setDayTable(ArrayList<ClassInClassTable> _dayTable) {
        dayTable = _dayTable;
    }

    public ArrayList<ClassInClassTable> getDayTable() {
        return dayTable;
    }
}
