package cn.ericweb.timetable.domain;

/**
 * Created by eric on 17-2-28.
 */
public class Time {
    public int hour;
    public int min;

    public Time(int _h, int _m) {
        this.hour = _h;
        this.min = _m;
    }

    @Override
    public String toString() {
        return "Time{" +
                "hour=" + hour +
                ", min=" + min +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Time time = (Time) o;

        if (hour != time.hour) return false;
        return min == time.min;
    }

    @Override
    public int hashCode() {
        int result = hour;
        result = 31 * result + min;
        return result;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
