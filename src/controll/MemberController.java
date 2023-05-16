package controll;

import java.sql.PreparedStatement;

import service.IMemberService;

public class MemberController implements IMemberService {
    private PreparedStatement pstmtInsertMember, pstmtSelectMemberValid, pstmtUpdateMemberLoginInfo, pstmtSelectMember, pstmtUpdateMember, pstmtDeleteMember;
    private final String sqlInsertMember = "INSERT INTO MEMBER VALUES(?,?,?,?,?)", 
            sqlSelectMemberValid = "SELECT MEMBER_ID,MEMBER_PWD,MEMBER_VALID FROM MEMBER WHERE MEMBER_ID=? AND MEMBER_PWD=?",
            sqlUpdateMemberLoginInfo = "UPDATE MEMBER SET MEMBER_VALID=?", // 로그인 됨 1 안됨 0 삭제된회원 2
            sqlSelectMember = "SELECT *FROM MEMBER WHERE MEMBER_ID",
            sqlUpdateMember = "UPDATE MEMBER SET MEMBER_NAME, MEMBER_PWD,MEMBER_PHONE,MEMBER_BIRTHDAY WHERE MEMBER_ID=?",
            sqlDeleteMember = "update member set member_valid=? where member_id=?";

    @Override
    public void register() {

    }

    @Override
    public void login() {

    }

    @Override
    public void logout() {

    }

    @Override
    public void myProfile() {
        
    }

    @Override
    public void editProfile() {
        
    }

    @Override
    public void removeMember() {
        
    }

}
