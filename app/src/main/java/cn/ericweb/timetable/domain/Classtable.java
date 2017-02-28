package cn.ericweb.timetable.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by eric on 17-2-21.
 */
public class Classtable implements Serializable {
    private LinkedList<Subject> subjects;

    private LinkedList<Activity> activities;

    private Date sessionStartDate;

    private int numberOfClassPerDay;

    private Time classStartTime;
    private int minutesPerClass;
    private ArrayList<Integer> intervals;

    public Classtable(int _numberOfClassPerDay, int _startHour, int _startMin, int _minsPerClass, ArrayList<Integer> _intervals) {
        this.subjects = new LinkedList<Subject>();
        this.activities = new LinkedList<Activity>();

        this.sessionStartDate = Calendar.getInstance().getTime();
        this.numberOfClassPerDay = _numberOfClassPerDay;

        this.classStartTime = new Time(_startHour, _startMin);
        this.minutesPerClass = _minsPerClass;
        this.intervals = _intervals;
    }
    public void pushActivities(Activity newActivity) {
        this.activities.add(newActivity);
    }

    public LinkedList<Subject> getSubjects() {
        return subjects;
    }

    public void pushSubject(Subject _subject) {
        if(this.subjects.indexOf(_subject) == -1) {
            this.subjects.add(_subject);
        }
    }
    public void setSubjects(LinkedList<Subject> subjects) {
        this.subjects = subjects;
    }

    public LinkedList<Activity> getActivities() {
        return activities;
    }

    public void setActivities(LinkedList<Activity> activities) {
        this.activities = activities;
    }

    public Date getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(Date sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }

    public int getNumberOfClassPerDay() {
        return numberOfClassPerDay;
    }

    public void setNumberOfClassPerDay(int numberOfClassPerDay) {
        this.numberOfClassPerDay = numberOfClassPerDay;
    }

    public Time getClassStartTime() {
        return classStartTime;
    }

    public void setClassStartTime(Time classStartTime) {
        this.classStartTime = classStartTime;
    }

    public int getMinutesPerClass() {
        return minutesPerClass;
    }

    public void setMinutesPerClass(int minutesPerClass) {
        this.minutesPerClass = minutesPerClass;
    }

    public ArrayList<Integer> getIntervals() {
        return intervals;
    }

    public void setIntervals(ArrayList<Integer> intervals) {
        this.intervals = intervals;
    }

    /**
     * 用于合并连在一起的相同的课程/activities
     */
    public void fixClasses() {
        for(Subject subject : this.subjects) {
            LinkedList<Activity> tmpActLL = new LinkedList<Activity>();
            for(Activity activity : this.activities) {
                if(activity.isClass() && activity.getSubject().equals(subject)) {
                    tmpActLL.add(activity);
                }
            }

            for(Activity activity : tmpActLL) {
                this.activities.remove(activity);
            }


            for(int i = 0; i < tmpActLL.size(); i++) {
               int weekday = tmpActLL.get(i).getWhichWeekday();
               int start = tmpActLL.get(i).getStartClassIndex();
               int end = tmpActLL.get(i).getEndClassIndex();

               for(int j = i + 1; j < tmpActLL.size(); j++) {
                   int _weekday = tmpActLL.get(j).getWhichWeekday();
                   int _start = tmpActLL.get(j).getStartClassIndex();
                   int _end = tmpActLL.get(j).getEndClassIndex();
                   if(_weekday == weekday && (Math.abs(_start - end) == 1 || Math.abs(_end - start) == 1)) {
                       int min = Math.min(_start, start);
                       int max = Math.max(_end, end);
                       start = min;
                       end = max;
                       tmpActLL.get(i).setStartClassIndex(min);
                       tmpActLL.get(i).setEndClassIndex(max);
                       tmpActLL.remove(j);
                       j = j - 1;
                   }
               }
            }

            this.activities.addAll(tmpActLL);
        }
    }
}
