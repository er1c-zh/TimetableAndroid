package cn.ericweb.timetable.domain;

import cn.ericweb.timetable.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by eric on 17-2-25.
 */
public class QueryLoginResult {
    static final public int SUCCESS = -1;
    static final public int ERROR_UNKNOWN = 0;
    static final public int ERROR_ID_PWD = 1;
    static final public int ERROR_CHECKCODE = 2;
    static final public int ERROR_SCHOOL_SERVICE = 3;
    static final public int ERROR_MIN_ID = 0;
    static final public int ERROR_MAX_ID = 3;
    static final public ArrayList<String> ERROR_INFO = new ArrayList<String>(Arrays.asList(
            "未知错误", "用户名或密码错误", "验证码错误", "无法连接到校园服务器"
    ));
    static final public String SUCCESS_INFO = "登陆成功";

    private int status;
    private String info;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        if (!StringUtils.isStringEmpty(this.info)) {
            if (this.status == QueryLoginResult.SUCCESS) {
                this.info = QueryLoginResult.SUCCESS_INFO;
            } else {
                if (this.status > QueryLoginResult.ERROR_MAX_ID || this.status < QueryLoginResult.ERROR_MIN_ID) {
                    this.status = QueryLoginResult.ERROR_UNKNOWN;
                }
                this.info = QueryLoginResult.ERROR_INFO.get(this.status);
            }
        }
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
