package cn.ericweb.timetable.domain;

import cn.ericweb.timetable.utils.StringUtils;

/**
 * Created by eric on 17-2-21.
 */
public class Class extends Activity {
    public Class(Subject subject) {
        this.setSubject(subject);
    }

    public String subject2Title() {
        if (this.getSubject() != null) {
            StringBuilder titleBuilder = new StringBuilder();
            titleBuilder.append(this.getSubject().getShortTitle());
            if (!StringUtils.isStringEmpty(this.getLocation())) {
                titleBuilder.append("@");
                titleBuilder.append(this.getLocation());
            }
            return titleBuilder.toString();
        }
        return "";
    }
}
