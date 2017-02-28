package cn.ericweb.timetable.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by eric on 16-9-14.
 */
public class EricDate {
    public EricDate() {
        date = new Date();
        calendar = Calendar.getInstance();
    }

    public Date getMondayDateOfNowWeek() {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) == 1 ? 7 : calendar.get(Calendar.DAY_OF_WEEK) - 1;
        Date result = new Date();
        result.setTime(date.getTime() - (dayOfWeek - 1) * 1000 * 24 * 60 * 60);
        return result;
    }

    private Date date;
    private Calendar calendar;
}
