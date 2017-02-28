package cn.ericweb.timetable.utils;

/**
 * Created by eric on 17-2-21.
 */
public class StringUtils {
    static public boolean isStringEmpty(String target) {
        return target == null || target.length() <= 0;
    }
}
