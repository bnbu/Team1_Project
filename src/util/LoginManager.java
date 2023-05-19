package util;

import model.MemberVO;

public class LoginManager {
    private MemberVO member;
    private boolean isAdmin;

    public void loginUser(MemberVO member) {
        this.member = member;
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