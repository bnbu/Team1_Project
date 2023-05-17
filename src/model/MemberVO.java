package model;

import java.util.Date;

public class MemberVO {
    private String member_id;
    private String member_name;
    private String member_pwd;
    private String member_phone;
    private Date member_birthday;
    private int member_valid;
    
    public String getMember_id() {
        return member_id;
    }
    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }
    public String getMember_name() {
        return member_name;
    }
    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }
    public String getMember_pwd() {
        return member_pwd;
    }
    public void setMember_pwd(String member_pwd) {
        this.member_pwd = member_pwd;
    }
    public String getMember_phone() {
        return member_phone;
    }
    public void setMember_phone(String member_phone) {
        this.member_phone = member_phone;
    }
    public Date getMember_birthday() {
        return member_birthday;
    }
    public void setMember_birthday(Date member_birthday) {
        this.member_birthday = member_birthday;
    }
    public int getMember_valid() {
        return member_valid;
    }
    public void setMember_valid(int member_valid) {
        this.member_valid = member_valid;
    }
}
