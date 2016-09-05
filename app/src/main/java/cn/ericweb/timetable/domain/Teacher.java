package cn.ericweb.timetable.domain;

import java.io.Serializable;

public class Teacher implements Serializable {

    public Teacher() {
        name = "";
        email = "";
        phoneNumber = "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(this.getClass())) {
            Teacher other = (Teacher) obj;
            return other.getEmail().equals(email) && other.getName().equals(name) && other.getPhoneNumber().equals(phoneNumber);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.email != null ? this.email.hashCode() : 0);
        hash = 53 * hash + (this.phoneNumber != null ? this.phoneNumber.hashCode() : 0);
        return hash;
    }

    public void setName(String _name) {
        name = _name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String _email) {
        email = _email;
    }

    public String getEmail() {
        return email;
    }

    public void setPhoneNumber(String _phoneNumber) {
        phoneNumber = _phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    private String name;
    private String email;
    private String phoneNumber;
}
