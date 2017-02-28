package cn.ericweb.timetable.domain;

/**
 * Created by eric on 17-2-25.
 */
public class QueryInfo {
    private String id;
    private String pwd;
    private String checkcode;
    private String startYear;
    private String indexSemester;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCheckcode() {
        return checkcode;
    }

    public void setCheckcode(String checkcode) {
        this.checkcode = checkcode;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getIndexSemester() {
        return indexSemester;
    }

    public void setIndexSemester(String indexSemester) {
        this.indexSemester = indexSemester;
    }
}
