package cn.ericweb.timetable.domain;

import java.io.Serializable;

public class User implements Serializable {

  private static final long serialVersionUID = 20160816L;

  private String userId;
  private String pwd;
  private String email;
  
  public void setUserId(String _userId) {
    userId = _userId;
  }
  public String getUserId() {
    return userId;
  }
  public void setPwd(String _pwd) {
    pwd = _pwd;
  }
  public String getPwd() {
    return pwd;
  }
  public void setEmail(String _email) {
    email = _email;
  }
  public String getEmail() {
    return email;
  }
}
