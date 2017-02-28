package cn.ericweb.timetable.domain;

/**
 * 展现于时间表上的所有物体的基类
 * Created by eric on 17-2-21.
 */
public class Activity {
    private boolean isClass;
    private Subject subject;

    private String title;
    private String location;

    private int whichWeekday;
    private int startClassIndex;
    private int endClassIndex;

    private String existedWeek;

    private Color colorFont;
    private Color colorBg;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity = (Activity) o;

        if (isClass != activity.isClass) return false;
        if (whichWeekday != activity.whichWeekday) return false;
        if (startClassIndex != activity.startClassIndex) return false;
        if (endClassIndex != activity.endClassIndex) return false;
        if (subject != null ? !subject.equals(activity.subject) : activity.subject != null)
            return false;
        if (title != null ? !title.equals(activity.title) : activity.title != null) return false;
        if (location != null ? !location.equals(activity.location) : activity.location != null)
            return false;
        return existedWeek != null ? existedWeek.equals(activity.existedWeek) : activity.existedWeek == null;
    }

    @Override
    public int hashCode() {
        int result = (isClass ? 1 : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + whichWeekday;
        result = 31 * result + startClassIndex;
        result = 31 * result + endClassIndex;
        result = 31 * result + (existedWeek != null ? existedWeek.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "isClass=" + isClass +
                ", subject=" + subject +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", whichWeekday=" + whichWeekday +
                ", startClassIndex=" + startClassIndex +
                ", endClassIndex=" + endClassIndex +
                ", existedWeek='" + existedWeek + '\'' +
                ", colorFont=" + colorFont +
                ", colorBg=" + colorBg +
                '}';
    }

    public boolean isClass() {
        return isClass;
    }

    public void setClass(boolean aClass) {
        isClass = aClass;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getWhichWeekday() {
        return whichWeekday;
    }

    public void setWhichWeekday(int whichWeekday) {
        this.whichWeekday = whichWeekday;
    }

    public int getStartClassIndex() {
        return startClassIndex;
    }

    public void setStartClassIndex(int startClassIndex) {
        this.startClassIndex = startClassIndex;
    }

    public int getEndClassIndex() {
        return endClassIndex;
    }

    public void setEndClassIndex(int endClassIndex) {
        this.endClassIndex = endClassIndex;
    }

    public String getExistedWeek() {
        return existedWeek;
    }

    public void setExistedWeek(String existedWeek) {
        this.existedWeek = existedWeek;
    }

    public Color getColorFont() {
        return colorFont;
    }

    public void setColorFont(Color colorFont) {
        this.colorFont = colorFont;
    }

    public Color getColorBg() {
        return colorBg;
    }

    public void setColorBg(Color colorBg) {
        this.colorBg = colorBg;
    }
}
