package cn.ericweb.timetable.domain;

import cn.ericweb.timetable.utils.StringUtils;

/**
 * Created by eric on 17-2-21.
 */
public class Class extends Activity {
    public Class(Subject subject) {
        this.setSubject(subject);
        this.setClass(true);
    }

}
