package cn.ericweb.timetable.domain;

import java.io.Serializable;

/**
 * 放入课程表每个格子中的课程信息，可以用来扩展一些与Course相关性较差的数据。
 *
 * @author eric
 */
public class CourseInClassTable implements Serializable {

    private Course course;
    private String timeArrangementForCourse;

    public CourseInClassTable () {
        course = new Course();
        timeArrangementForCourse = "";
    }
    public boolean isCourseExistInWeek(int _week) {
        try {
            if ("1".charAt(0) == timeArrangementForCourse.charAt(_week)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }
        if (obj.getClass().equals(this.getClass())) {
            CourseInClassTable other = (CourseInClassTable) obj;
            return course.equals(other.getCourse()) && timeArrangementForCourse.equals(other.getTimeArrangementForCourse());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.course != null ? this.course.hashCode() : 0);
        hash = 89 * hash + (this.timeArrangementForCourse != null ? this.timeArrangementForCourse.hashCode() : 0);
        return hash;
    }
    @Override
    public String toString() {
        return course.toString();
    }

    public void setCourse(Course _course) {
        course = _course;
    }

    public Course getCourse() {
        return course;
    }

    public void setTimeArrangementForCourse(String _timeArrangementForCourse) {
        timeArrangementForCourse = _timeArrangementForCourse;
    }

    public String getTimeArrangementForCourse() {
        return timeArrangementForCourse;
    }
}
