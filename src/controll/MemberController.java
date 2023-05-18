package controll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import model.MemberVO;
import service.IMemberService;
import util.ConnectionSingletonHelper;
import util.LoginManager;

public class MemberController implements IMemberService {
    private static Connection conn;
    private static BufferedReader br;
    private StringBuilder sb;
    private SimpleDateFormat sdf;
    private MemberVO vo;
    private PreparedStatement pstmtInsertMember, pstmtSelectMemberValid, pstmtUpdateMemberLoginInfo, pstmtSelectMember, pstmtUpdateMember, pstmtDeleteMember;
    private final String sqlInsertMember = "INSERT INTO MEMBER (member_id, member_name, member_pwd, member_phone, member_birthday) VALUES(?,?,?,?,?)", 
            sqlSelectMemberValid = "SELECT MEMBER_ID,MEMBER_PWD,MEMBER_VALID FROM MEMBER WHERE MEMBER_ID=? AND MEMBER_PWD=? AND MEMBER_VALID=?",
            sqlUpdateMemberLoginInfo = "UPDATE MEMBER SET MEMBER_VALID= ? WHERE MEMBER_ID = ?",
            sqlSelectMember = "SELECT member_id, member_name,member_phone,member_birthday FROM MEMBER WHERE MEMBER_ID=?",
            sqlUpdateMember = "UPDATE MEMBER SET MEMBER_NAME=?, MEMBER_PWD=?,MEMBER_PHONE=?,MEMBER_BIRTHDAY=? WHERE MEMBER_ID=?",
            sqlDeleteMember = "UPDATE MEMBER SET MEMBER_VALID=3 WHERE MEMBER_ID=?";
    
    public MemberController() throws Exception {
        conn = ConnectionSingletonHelper.getConnection();
        br = new BufferedReader(new InputStreamReader(System.in));
        sb = new StringBuilder();
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
        pstmtInsertMember.setString(3, sha256.encrypt(member_Pwd));
        pstmtInsertMember.setString(4, member_Phone);
        pstmtInsertMember.setString(5, member_Birthday);

        pstmtInsertMember.executeUpdate();// 값 저장
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public MemberVO login() throws IOException {
    	 SHA256 sha256 = new SHA256();
    	 vo = new MemberVO();

      try {
        System.out.print("ID: ");
        String member_Id = br.readLine();
        System.out.print("PassWord: ");
        String member_Pwd = br.readLine();
        pstmtSelectMemberValid = conn.prepareStatement(sqlSelectMemberValid);
        pstmtSelectMemberValid.setString(1, member_Id);
        pstmtSelectMemberValid.setString(2, sha256.encrypt(member_Pwd));
        pstmtSelectMemberValid.setInt(3, 0);
        ResultSet rs = pstmtSelectMemberValid.executeQuery();

        if (!rs.isBeforeFirst()) {
          System.out.println("로그인이 되지않았습니다.");
          return null;
        }

        while (rs.next()) {
          vo.setMember_id(rs.getString(1));
          vo.setMember_pwd(rs.getString(2));
          vo.setMember_valid(rs.getInt(3));
        }
        pstmtUpdateMemberLoginInfo = conn.prepareStatement(sqlUpdateMemberLoginInfo );
        pstmtUpdateMemberLoginInfo.setInt(1, 1);
        pstmtUpdateMemberLoginInfo.setString(2, vo.getMember_id());
        pstmtUpdateMemberLoginInfo.executeUpdate();
        System.out.println("로그인이 되었습니다.");
      } catch (Exception e) {
        e.printStackTrace();
      } 
    return vo;
    }

    @Override
    public MemberVO logout() {
      try {
        pstmtUpdateMemberLoginInfo = conn.prepareStatement(sqlUpdateMemberLoginInfo );
        pstmtUpdateMemberLoginInfo.setInt(1, 0);
        pstmtUpdateMemberLoginInfo.setString(2, vo.getMember_id());
        pstmtUpdateMemberLoginInfo.executeUpdate();
        vo = null;
        System.out.println("로그아웃 되었습니다.");
        return vo;
      } catch (Exception e) {
        e.printStackTrace();
      }
      return vo;
    }

    @Override
    public void myProfile() throws IOException {
        sb.setLength(0);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            System.out.println("───────────────────회원정보 보기───────────────────");
            pstmtSelectMember = conn.prepareStatement(sqlSelectMember);
            pstmtSelectMember.setString(1, vo.getMember_id()); // 사용자 직접입력 X

            ResultSet rs = pstmtSelectMember.executeQuery();
            rs.next();
            sb.append("──────────┬─────────────────────────────────────").append("\n");
            sb.append(String.format("%-7s│%s", "아이디", rs.getString(1))).append("\n");
            sb.append("──────────┼─────────────────────────────────────").append("\n");
            sb.append(String.format("%-8s│%s", "이름", rs.getString(2))).append("\n");
            sb.append("──────────┼─────────────────────────────────────").append("\n");
            sb.append(String.format("%-6s│%s", "전화번호", rs.getString(3))).append("\n");
            sb.append("──────────┼─────────────────────────────────────").append("\n");
            sb.append(String.format("%-8s│%s", "생일", sdf.format(rs.getDate(4)))).append("\n");
            sb.append("──────────┴─────────────────────────────────────").append("\n");
            System.out.println(sb);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editProfile() throws IOException {
        SHA256 sha256 = new SHA256();
        try {
            System.out.println("───────────────────회원정보 수정───────────────────");
            System.out.println("회원 확인을 위해 비밀번호를 입력해주세요 ");
            System.out.print("비밀번호: "); String pwd = sha256.encrypt(br.readLine());
            
            pstmtSelectMemberValid = conn.prepareStatement(sqlSelectMemberValid);
            pstmtSelectMemberValid.setString(1, vo.getMember_id());
            pstmtSelectMemberValid.setString(2, pwd);
            pstmtSelectMemberValid.setInt(3, 1);
            ResultSet rs = pstmtSelectMemberValid.executeQuery();
            if(!rs.isBeforeFirst()) {
                System.out.println("비밀번호가 틀렸습니다.");
                return;
            }
            
            pstmtUpdateMember = conn.prepareStatement(sqlUpdateMember);
            pstmtUpdateMember.setString(5, vo.getMember_id());
            System.out.print("이름: ");
            pstmtUpdateMember.setString(1, br.readLine());
            System.out.print("비밀번호: ");
            pstmtUpdateMember.setString(2, sha256.encrypt(br.readLine()));
            System.out.print("전화번호: ");
            pstmtUpdateMember.setString(3, br.readLine());
            System.out.print("생일: ");
            pstmtUpdateMember.setString(4, br.readLine());

            int result = pstmtUpdateMember.executeUpdate();
            System.out.println(result + "개 업데이트 성공");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeMember(LoginManager lm) throws IOException {
        SHA256 sha256 = new SHA256();
        try {
            System.out.println("───────────────────회원 탈퇴───────────────────");
            System.out.println("회원 확인을 위해 비밀번호를 입력해주세요 ");
            System.out.print("비밀번호: "); String pwd = sha256.encrypt(br.readLine());
            System.out.println(pwd);
            
            pstmtSelectMemberValid = conn.prepareStatement(sqlSelectMemberValid);
            pstmtSelectMemberValid.setString(1, vo.getMember_id());
            pstmtSelectMemberValid.setString(2, pwd);
            pstmtSelectMemberValid.setInt(3, 1);
            ResultSet rs = pstmtSelectMemberValid.executeQuery();
            if(!rs.isBeforeFirst()) {
                System.out.println("비밀번호가 틀렸습니다.");
                return;
            }
            
            pstmtDeleteMember = conn.prepareStatement(sqlDeleteMember);
            pstmtDeleteMember.setString(1, vo.getMember_id());
            
            int result = pstmtDeleteMember.executeUpdate();
            System.out.println(result<1?"다시 시도해주세요":"탈퇴 처리되었습니다");
            vo = null;
            lm.loginUser(vo);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
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
	private void menu() {
	    sb.setLength(0);
	    sb.append("───────────────────회원 관리───────────────────").append("\n");
	    sb.append("1. 회원정보 조회").append("\n");
	    sb.append("2. 회원정보 수정").append("\n");
	    sb.append("3. 회원탈퇴").append("\n");
	    sb.append("4. 돌아가기").append("\n");
	    sb.append("───────────────────────────────────────────────").append("\n\n");
        sb.append("입력: ");
        System.out.print(sb);
    }

	private void menu2() {
	    sb.setLength(0);
        sb.append("───────────────────────────────────────────────").append("\n");
        sb.append("             영화 예매 시스템 시작").append("\n");
        sb.append("───────────────────────────────────────────────").append("\n");
        sb.append("1. 회원가입").append("\n");
        sb.append("2. 로그인").append("\n");
        sb.append("3. 시스템 종료").append("\n");
        sb.append("───────────────────────────────────────────────").append("\n\n");
        sb.append("입력: ");
        System.out.print(sb);
    }
	
	public void loginMenu(LoginManager lm) throws NumberFormatException, IOException {
        while (true) {
            menu2();
           
            switch (Integer.parseInt(br.readLine())) {
            case 1:
                try {
                    register();
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break; // 회원가입
            case 2:
                lm.loginUser(login());
                if(lm.getLoginUser()!=null)return;
                break; // 로그인
            case 3: System.out.println("시스템을 종료합니다.");
                System.exit(0);
                return;
            } // switch end
        } // while end
    }

    

    @Override
    public void memberMenu(LoginManager lm) throws NumberFormatException, IOException {
        while (true) {
            menu();
            
            switch (Integer.parseInt(br.readLine())) {
            case 1:
                myProfile();
                break; // 회원정보
            case 2:
                editProfile();
                break; // 회원 수정
            case 3:
                removeMember(lm);
                return; // 회원 삭제
            case 4: System.out.println("메인메뉴로 돌아갑니다.");
                return;
            } // switch end
        } // while end
    }
}