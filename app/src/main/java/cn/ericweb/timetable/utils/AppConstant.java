package cn.ericweb.timetable.utils;

/**
 * 关于客户端请求设置的一些常量
 *
 * @author eric
 */
public class AppConstant {
    // sharedPreferance的key
    public static final String SHARED_PREF_CLASSTABLE = "cn.ericweb.CLASSTABLE";
    public static final String SHARED_PREF_WIDGET = "cn.ericweb.WIDGET";
    public static final String SHARED_PREF_PHONE = "cn.ericweb.PHONE";

    // 课程表的数据的KEY
    public static final String CLASSTABLE_KEY_MAIN = "cn.ericweb.CLASSTABLE_MAIN";
    public static final String CLASSTABLE_KEY_ADDITIONAL_INFO = "cn.ericweb.CLASSTABLE_ADDITIONAL_INFO";
    public static final String CLASSTABLE_KEY_CACHE = "cn.ericweb.CLASSTABLE_CACHE";

    // widget的KEY
    public static final String WIDGET_KEY_WIDTH = "cn.ericweb.WIDGET_WIDTH";
    public static final String WIDGET_KEY_HEIGHT = "cn.ericweb.WIDGET_HEIGHT";

    // 查询课程表的表单
    public static final String CLASSTABLE_ID = "id";
    public static final String CLASSTABLE_PASSWORD = "pwd";
    public static final String CLASSTABLE_CHECKCODE = "checkcode";
    public static final String CLASSTABLE_YEAR = "startYear";
    public static final String CLASSTABLE_SESSION = "indexSemester";

    // 机器的环境
    public static final String PHONE_DENSITY_DPI = "cn.ericweb.PHONE_DENSITY_DPI";
}
