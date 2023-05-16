package controll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import service.IMemberService;
import util.ConnectionSingletonHelper;

public class MemberController implements IMemberService {
    private static Connection conn;
    private static BufferedReader br;
    private PreparedStatement pstmtInsertMember, pstmtSelectMemberValid, pstmtUpdateMemberLoginInfo, pstmtSelectMember, pstmtUpdateMember, pstmtDeleteMember;
    private final String sqlInsertMember = "INSERT INTO MEMBER VALUES(?,?,?,?,?)", 
            sqlSelectMemberValid = "SELECT MEMBER_ID,MEMBER_PWD,MEMBER_VALID FROM MEMBER WHERE MEMBER_ID=? AND MEMBER_PWD=?",
            sqlUpdateMemberLoginInfo = "UPDATE MEMBER SET MEMBER_VALID=?", // 로그인 됨 1 안됨 0 삭제된회원 2
            sqlSelectMember = "SELECT member_id, member_name,member_phone,member_birthday FROM MEMBER WHERE MEMBER_ID=?",
            sqlUpdateMember = "UPDATE MEMBER SET MEMBER_NAME=?, MEMBER_PWD=?,MEMBER_PHONE=?,MEMBER_BIRTHDAY=? WHERE MEMBER_ID=?",
            sqlDeleteMember = "UPDATE MEMBER SET MEMBER_VALID=3 WHERE MEMBER_ID=?";
    
    public MemberController() throws Exception {
        conn = ConnectionSingletonHelper.getConnection();
        br = new BufferedReader(new InputStreamReader(System.in));
    }
    
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
    public void myProfile() throws IOException {
        try {
            System.out.println("아이디를 입력해주세요.");
            pstmtSelectMember = conn.prepareStatement(sqlSelectMember);
            pstmtSelectMember.setString(1, br.readLine());
            
            ResultSet rs = pstmtSelectMember.executeQuery();
            System.out.printf("%s %s %s %s\n","아이디","이름","전화번호","생일");
            while(rs.next()) {
                System.out.print(rs.getString(1)+" ");
                System.out.print(rs.getString(2)+" ");
                System.out.print(rs.getString(3)+" ");
                System.out.print(rs.getString(4)+" ");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }
    
    @Override
    public void editProfile() throws IOException {
        try {
            pstmtUpdateMember = conn.prepareStatement(sqlUpdateMember);
            System.out.println("아이디 입력");
            pstmtUpdateMember.setString(5, br.readLine());
            System.out.println("이름");
            pstmtUpdateMember.setString(1, br.readLine());
            System.out.println("비번");
            pstmtUpdateMember.setString(2, br.readLine());
            System.out.println("전화번호");
            pstmtUpdateMember.setString(3, br.readLine());
            System.out.println("생일");
            pstmtUpdateMember.setString(4, br.readLine());
            
            int result = pstmtUpdateMember.executeUpdate();
            System.out.println(result+"개 업데이트 성공");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeMember() throws IOException {
        try {
            pstmtDeleteMember = conn.prepareStatement(sqlDeleteMember);
            pstmtDeleteMember.setString(1, br.readLine());
            
            int result = pstmtDeleteMember.executeUpdate();
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
