package cn.ericweb.timetable.util;

/**
 * 关于客户端请求设置的一些常量
 * @author eric
 */
public class AppConstant {
    // 查询课程表的表单
    public static final String CLASSTABLE_ID = "schoolId";
    public static final String CLASSTABLE_PASSWORD = "pwd";
    public static final String CLASSTABLE_YEAR = "year";
    public static final String CLASSTABLE_SESSION = "session";

    // 设置对应的key
    public static final String CONFIG_SHARED_PREF = "cn.ericweb.CONFIG";
    public static final String NOW_WEEK = "cn.ericweb.settings.classtable.NOW_WEEK";
    public static final String FIRST_WEEK_START_MONDAY_DATE = "cn.ericweb.settings.classtable.NOW_WEEK_START_MONDAY_DATE";
    public static final String IF_WEEKENDS = "cn.ericweb.settings.classtable.IF_WEEKENDS";
}
