package cn.ericweb.timetable.domain;

import cn.ericweb.timetable.R;

/**
 * 用来盛放只与应用端有关的课程信息
 * Created by eric on 16-9-15.
 */
public class CourseAppAdditionalInfo {
    public CourseAppAdditionalInfo(Course _course) {
        course = _course;
        shortOfCourseName = this.getShortOfCourseName(course.getCourseName());
        colorId = R.color.background_class_default;
    }

    /**
     * 检查是否包含了目标course
     *
     * @param _course target course to check
     * @return
     */
    public boolean isInfoAboutThisCourse(Course _course) {
        return this.course.equals(_course);
    }

    /**
     * 通过显示的字符串来判断是不是包含的course
     *
     * @param _string
     * @return
     */
    public boolean isInfoAboutThisCourse(String _string) {
        return null != _string && _string.equals(course.toString());
    }

    /**
     * 获得显示在课程表中的字符串
     *
     * @return
     */
    public String getString2Show() {
        String result = shortOfCourseName;
        String address = course.getAddress();
        result += (address.equals("") ? "" : "@" + address);
        return result;
    }

    public void setCourse(Course _course) {
        course = _course;
        shortOfCourseName = this.getShortOfCourseName(_course.getCourseName());
    }

    public Course getCourse() {
        return course;
    }

    public void setShortOfCourseName(String _shortOfCourseName) {
        shortOfCourseName = _shortOfCourseName;
    }

    public String getShortOfCourseName() {
        return shortOfCourseName;
    }

    public void setColorId(int _colorId) {
        colorId = _colorId;
    }

    public int getColorId() {
        return colorId;
    }

    private String getShortOfCourseName(String _string) {
        if(_string.length() < LENGTH_SHORT_OF_NAME) {
            return _string;
        } else {
            return _string.substring(0, LENGTH_SHORT_OF_NAME);
        }
    }

    private static int LENGTH_SHORT_OF_NAME = 4;
    private Course course;
    private String shortOfCourseName;
    private int colorId;
}
