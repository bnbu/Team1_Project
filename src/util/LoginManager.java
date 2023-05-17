package util;

import model.MemberVO;

public class LoginManager {
    private MemberVO member;

    public void loginUser(MemberVO member) {
        this.member = member;
    }

    public MemberVO getLoginUser() {
        return member;
    }
}