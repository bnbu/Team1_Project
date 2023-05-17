package controll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;
import java.sql.SQLException;
import model.MemberVO;
import service.IMemberService;
import util.ConnectionSingletonHelper;

public class MemberController implements IMemberService {

    private static Connection conn;
    private static BufferedReader br;
    private static MemberVO vo = new MemberVO();
    private PreparedStatement pstmtInsertMember, pstmtSelectMemberValid, pstmtUpdateMemberLoginInfo, pstmtSelectMember, pstmtUpdateMember, pstmtDeleteMember;
    private final String sqlInsertMember = "INSERT INTO MEMBER (member_id, member_name, member_pwd, member_phone, member_birthday) VALUES(?,?,?,?,?)", 
            sqlSelectMemberValid = "SELECT MEMBER_ID,MEMBER_PWD,MEMBER_VALID FROM MEMBER WHERE MEMBER_ID=? AND MEMBER_PWD=?",
            sqlUpdateMemberLoginInfo = "UPDATE MEMBER SET MEMBER_VALID= ? WHERE MEMBER_ID = ?",
            sqlSelectMember = "SELECT member_id, member_name,member_phone,member_birthday FROM MEMBER WHERE MEMBER_ID=?",
            sqlUpdateMember = "UPDATE MEMBER SET MEMBER_NAME=?, MEMBER_PWD=?,MEMBER_PHONE=?,MEMBER_BIRTHDAY=? WHERE MEMBER_ID=?",
            sqlDeleteMember = "UPDATE MEMBER SET MEMBER_VALID=3 WHERE MEMBER_ID=?";
    
    public MemberController() throws Exception {
        conn = ConnectionSingletonHelper.getConnection();
        br = new BufferedReader(new InputStreamReader(System.in));
    }
  
    @Override
    public void register() throws IOException, NoSuchAlgorithmException {
      SHA256 sha256 = new SHA256();
      try {
        System.out.print("ID: ");
        String member_Id = br.readLine();
        System.out.print("PassWord: ");
        String member_Pwd = br.readLine();
        System.out.print("Member Name: ");
        String member_Name = br.readLine();
        System.out.print("Member Phone: ");
        String member_Phone = br.readLine();
        System.out.print("Member Birthday: ");
        String member_Birthday = br.readLine();

        pstmtInsertMember = conn.prepareStatement(sqlInsertMember);
        pstmtInsertMember.setString(1, member_Id);
        pstmtInsertMember.setString(2, member_Name);
        pstmtInsertMember.setString(4, member_Phone);
        pstmtInsertMember.setString(5, member_Birthday);

        String password = member_Pwd;
        String cryptongram = sha256.encrypt(password);
        System.out.println(cryptongram);
        System.out.println("비밀 번호가 일치하는가 " + cryptongram.equals(sha256.encrypt(member_Pwd)));// 일치여부
        pstmtInsertMember.setString(3, cryptongram);
        int result = pstmtInsertMember.executeUpdate();// 값 저장
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void login() throws IOException {
      try {
        System.out.print("ID: ");
        String member_Id = br.readLine();
        System.out.print("PassWord: ");
        String member_Pwd = br.readLine();
        pstmtSelectMemberValid = conn.prepareStatement(sqlSelectMemberValid);
        pstmtSelectMemberValid.setString(1, member_Id); // 1계정
        pstmtSelectMemberValid.setString(2, member_Pwd);// 2비밀번호
        ResultSet rs = pstmtSelectMemberValid.executeQuery();

        if (!rs.isBeforeFirst()) {
          System.out.println("로그인이 되지않았습니다.");
          return;
        }

        while (rs.next()) {

          vo.setMember_id(rs.getString(1));
          vo.setMember_pwd(rs.getString(2));
          vo.setMember_valid(rs.getInt(3));
        }
        pstmtUpdateMemberLoginValid = conn.prepareStatement(sqlUpdateMemberLoginVaild);
        pstmtUpdateMemberLoginValid.setInt(1, 1);
        pstmtUpdateMemberLoginValid.setString(2, vo.getMember_id());
        int result = pstmtUpdateMemberLoginValid.executeUpdate(); // valid 를 바꿔주기 위함
        System.out.println(result);// 성공한 값만큼 리턴한다.
      } catch (Exception e) {
        e.printStackTrace();
      } finally {

      }
      System.out.println("로그인이 되었습니다.");
    }

    @Override
    public void logout() {
      try {
        pstmtSelectMemberValid = conn.prepareStatement(sqlSelectMemberValid);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {

      }
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

	public class SHA256 {

		public String encrypt(String text) throws NoSuchAlgorithmException {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(text.getBytes());

			return bytesToHex(md.digest());
		}

		private String bytesToHex(byte[] bytes) {
			StringBuilder builder = new StringBuilder();
			for (byte b : bytes) {
				builder.append(String.format("%02x", b));
			}
			return builder.toString();
		}

	}

	public class TelTest {
		public static String telNumber(String number) {
			// 전화번호 정규표현식으로 제한
			String regEx = "(\\d{2,3})(\\d{3,4})(\\d{4})";

			if (!Pattern.matches(regEx, number)) {
				System.out.println("에러 1 : 형식 오류 ====> " + number.toString());
				return null;
			}

			// 지역번호가 02이면서 9자리 수일 때 == not error
			if (number.substring(0, 2).contains("02") && number.length() == 9) {
				return number.replaceAll(regEx, "$1-$2-$3"); // 출력 xxx-xxx-xxxx
			}

			// 지역번호 02를 제외한 번호 (070,031,064 ...) 가 9자리 일 때 == > 에러
			else if (number.length() == 9) {
				System.out.println("에러 2 : 자릿수 입력 오류 ====> " + number.toString());
				return null;
			}
			return number.replaceAll(regEx, "$1-$2-$3"); // 출력 xxx-xxxx-xxxx
		}
	}
}