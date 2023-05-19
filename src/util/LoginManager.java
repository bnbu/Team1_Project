package util;

import model.MemberVO;

public class LoginManager {
    private MemberVO member;
    private boolean isAdmin;
    private int flag;

    public void loginUser(MemberVO member) {
        this.member = member;
    }
    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }
    public void setIsAdmin(boolean b) {
    	isAdmin = b;
    }
    public boolean getIsAdmin() {
    	return isAdmin;
    }
    public MemberVO getLoginUser() {
        return member;
    }
}