package cn.ericweb.timetable.domain;

import java.io.Serializable;

public class Course implements Serializable {

    public Course() {
        courseName = "";
        teacher = new Teacher();
        address = "";
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
            Course other = (Course) obj;
            return courseName.equals(other.getCourseName()) && teacher.equals(other.getTeacher()) && address.equals(other.getAddress());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.courseName != null ? this.courseName.hashCode() : 0);
        hash = 17 * hash + (this.teacher != null ? this.teacher.hashCode() : 0);
        hash = 17 * hash + (this.address != null ? this.address.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        if (address.equals("")) {
            return courseName;
        } else {
            return courseName + "@" + address;
        }
    }

    public void setCourseName(String _courseName) {
        courseName = _courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setTeacher(Teacher _teacher) {
        teacher = _teacher;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setAddress(String _address) {
        address = _address;
    }

    public String getAddress() {
        return address;
    }

    private String courseName;
    private Teacher teacher;
    private String address;
}
