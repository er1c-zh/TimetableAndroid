package cn.ericweb.timetable.domain;

/**
 * 保存科目的信息 不包含上课时间与位置的信息
 *
 * @author eric
 *         Created by eric on 17-2-21.
 */
public class Subject {
    private String id;
    private String title;
    private String shortTitle;
    private Teacher teacher;
    private ExamInfo examInfo;

    @Override
    public String toString() {
        return "Subject{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", shortTitle='" + shortTitle + '\'' +
                ", teacher=" + teacher +
                ", examInfo=" + examInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subject subject = (Subject) o;

        return id != null ? id.equals(subject.id) : subject.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public ExamInfo getExamInfo() {
        return examInfo;
    }

    public void setExamInfo(ExamInfo examInfo) {
        this.examInfo = examInfo;
    }
}
