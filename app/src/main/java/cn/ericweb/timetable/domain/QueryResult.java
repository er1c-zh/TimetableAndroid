package cn.ericweb.timetable.domain;

/**
 * 用于客户端查询返回结果
 * @author eric
 */
public class QueryResult {

    public static final boolean QUERY_GOOD = true;
    public static final boolean QUERY_BAD = false;
    private boolean status;
    private String info;
    private String resultJson;

    public QueryResult() {
        status = QUERY_BAD;
        info = "Init";
        resultJson = "";
    }
    
    public void setStatus(boolean _status) {
        status = _status;
    }
    public boolean getStatus() {
        return status;
    }
    public void setInfo(String _info) {
        info = _info;
    }
    public String getInfo() {
        return info;
    }
    public void setResultJson(String _resultJson) {
        resultJson = _resultJson;
    }
    public String getResultJson() {
        return resultJson;
    }
}
