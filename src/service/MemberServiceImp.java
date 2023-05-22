package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import model.MemberVO;
import util.ConnectionSingletonHelper;
import util.LoginManager;
import util.SHA256;

public class MemberServiceImp implements IMemberService {
    private static Connection conn;
    private static BufferedReader br;
    private StringBuilder sb;
    private SimpleDateFormat sdf;
    private MemberVO vo;
    private ResultSet rs;
    private PreparedStatement pstmtInsertMember, pstmtSelectMemberValid, pstmtUpdateMemberLoginInfo, 
    		pstmtSelectMember, pstmtUpdateMember, pstmtDeleteMember,pstmtSelectLikeByid,
    		pstmtSearchAdmin;
    private final String sqlInsertMember = "INSERT INTO MEMBER (member_id, member_name, member_pwd, member_phone, member_birthday) VALUES(?,?,?,?,?)", 
            sqlSelectMemberValid = "SELECT MEMBER_ID,MEMBER_NAME,MEMBER_VALID FROM MEMBER WHERE MEMBER_ID=? AND MEMBER_PWD=? AND MEMBER_VALID=?",
            sqlUpdateMemberLoginInfo = "UPDATE MEMBER SET MEMBER_VALID= ? WHERE MEMBER_ID = ?",
            sqlSelectMember = "SELECT member_id, member_name,member_phone,member_birthday FROM MEMBER WHERE MEMBER_ID=?",
            sqlSelectLikeByid = "select member_id from member where member_id like '%'||?||'%'",
            sqlUpdateMember = "UPDATE MEMBER SET MEMBER_NAME=?, MEMBER_PWD=?,MEMBER_PHONE=?,MEMBER_BIRTHDAY=? WHERE MEMBER_ID=?",
            sqlDeleteMember = "UPDATE MEMBER SET MEMBER_VALID=3 WHERE MEMBER_ID=?",
            sqlSearchAdmin = "SELECT \"flag\" FROM ADMIN WHERE MEMBER_ID = ?";

    public MemberServiceImp() throws Exception {
        conn = ConnectionSingletonHelper.getConnection();
        br = new BufferedReader(new InputStreamReader(System.in));
        sb = new StringBuilder();
    }

    @Override
    public void register() {
        SHA256 sha256 = new SHA256();
        try {
            String memberId = null,
                    memberPwd = null,//최소 8 자, 하나 이상의 문자와 하나의 숫자,
                    memberName = null,
                    memberPhone = null, 
                    memberBirthday = null;
            System.out.println("회원가입을 선택하셨습니다.");
            System.out.println("영문자와 숫자를 조합하여 6자이상 20자 이하로 입력하십시오");
            System.out.println("초기메뉴로 돌아가기 : q");
            while (true) {
                System.out.print("ID: ");
                memberId = br.readLine().trim().trim();
                if(memberId.equalsIgnoreCase("q")) return;
                if (!isValid("^[a-z]+[a-z0-9]{5,19}$",memberId)) {
                    System.out.println("잘못된 입력양식 입니다. 다시 입력하십시오");
                    continue;
                }
                pstmtSelectLikeByid = conn.prepareStatement(sqlSelectLikeByid);
                pstmtSelectLikeByid.setString(1, memberId);
                rs = pstmtSelectLikeByid.executeQuery();
                if(rs.isBeforeFirst()) {
                    System.out.println("이미 존재하는 아이디입니다.");
                    continue;
                }
              	pstmtInsertMember = conn.prepareStatement(sqlInsertMember);
                pstmtInsertMember.setString(1, memberId);
                break;
            }

            System.out.println("최소 8 자, 하나 이상의 문자와 하나의 숫자,특수문자 !,@,$,!,%,*,#,^,?,& 를 이용하여  입력하십시오");
            while(true) {
                System.out.print("PassWord: ");
                memberPwd = br.readLine().trim();
                if(memberPwd.equalsIgnoreCase("q")) return;
                if (!isValid("^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[$`~!@$!%*#^?&\\(\\)-_=+]).{8,16}$", memberPwd)) {
                    System.out.println("잘못된 입력양식 입니다. 다시 입력하십시오");
                    continue;
                }
                break;
            }

            System.out.println("비밀번호를 한번 더 입력해 주십시오.");
            while(true) {
                System.out.print("PassWord Confirm: ");
                if (!br.readLine().trim().equals(memberPwd)) {
                    System.out.println("비밀번호가 다릅니다.");
                    continue;
                }
                pstmtInsertMember.setString(3, sha256.encrypt(memberPwd));
                break;
            }

            System.out.println("이름은 최대 16자까지 입력이 가능합니다.");
            while(true) {
                System.out.print("Name: ");
                memberName  = br.readLine().trim();
                if(memberName.equalsIgnoreCase("q")) return;
                if (!isValid("(^[a-zA-Zㄱ-힣][a-zA-Zㄱ-힣 ]*${1,16})", memberName)) {
                    System.out.println("잘못된 이름형식 입니다.");
                    continue;
                }
                pstmtInsertMember.setString(2, memberName);
                break;
            }

            System.out.println("형식 000-0000-0000 으로 입력해 주십시오");
            while(true) {
                System.out.print("Phone: ");
                memberPhone = br.readLine().trim();
                if(memberPhone.equalsIgnoreCase("q")) return;
                if (!isValid("^\\d{3}-\\d{4}-\\d{4}$", memberPhone)) {
                    System.out.println("잘못된 입력양식 입니다. 다시 입력하십시오");
                    continue;
                }
                pstmtInsertMember.setString(4, memberPhone);
                break;
            }

            System.out.println("주민등록번호 앞자리 6글자만 입력하십시오.");
            while(true) {
                try {
                    System.out.print("Birthday: ");
                    memberBirthday = br.readLine().trim();
                    if(memberBirthday.equalsIgnoreCase("q")) return;
                    if (!isValid("([0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[1,2][0-9]|3[0,1]))", memberBirthday)) {
                        System.out.println("잘못된 입력양식 입니다. 다시 입력하십시오");
                        continue;
                    }

                    pstmtInsertMember.setString(5, memberBirthday);
                    break;
                }
                catch (IOException e) {
                    System.out.println("잘못된 날짜 입력입니다");
                }
            }
            pstmtInsertMember.executeUpdate();
        } catch (SQLException e) {
            System.out.println("다시 시도해주세요");
            return;
        } catch (IOException e) {
		} catch (NoSuchAlgorithmException e) {
		} finally {
			ConnectionSingletonHelper.close(pstmtSelectLikeByid);
			ConnectionSingletonHelper.close(pstmtInsertMember);
		}
    }

    private boolean isValid(String pattern, String member_Id) {
        return Pattern.matches(pattern, member_Id);
    }

    @Override
    public MemberVO login(){
        SHA256 sha256 = new SHA256();
        vo = new MemberVO();

        try {
        	pstmtSelectMemberValid = conn.prepareStatement(sqlSelectMemberValid);
            System.out.print("ID: ");
            String memberId = br.readLine().trim();
            System.out.print("PassWord: ");
            String memberPwd = br.readLine().trim();

            pstmtSelectMemberValid.setString(1, memberId);
            pstmtSelectMemberValid.setString(2, sha256.encrypt(memberPwd));
            pstmtSelectMemberValid.setInt(3, 0);
            rs = pstmtSelectMemberValid.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("다시 시도해주세요.");
                return null;
            }

            while (rs.next()) {
                vo.setMember_id(rs.getString(1));
                vo.setMember_name(rs.getString(2));
                vo.setMember_valid(rs.getInt(3));
            }
            
            pstmtUpdateMemberLoginInfo = conn.prepareStatement(sqlUpdateMemberLoginInfo);
            pstmtUpdateMemberLoginInfo.setInt(1, 1);
            pstmtUpdateMemberLoginInfo.setString(2, vo.getMember_id());
            pstmtUpdateMemberLoginInfo.executeUpdate();
            System.out.println("로그인이 되었습니다.");
        } catch (Exception e) {
        } finally {
        	ConnectionSingletonHelper.close(pstmtUpdateMemberLoginInfo);
			ConnectionSingletonHelper.close(pstmtSelectMemberValid);
		}
        return vo;
    }

    @Override
    public MemberVO logout(LoginManager lm) {
        try {
            pstmtUpdateMemberLoginInfo = conn.prepareStatement(sqlUpdateMemberLoginInfo);
            pstmtUpdateMemberLoginInfo.setInt(1, 0);
            pstmtUpdateMemberLoginInfo.setString(2, vo.getMember_id());
            pstmtUpdateMemberLoginInfo.executeUpdate();
            lm.setIsAdmin(false);
            lm.setFlag(0);
            vo = null;
            System.out.println("로그아웃 되었습니다.");
            return vo;
        } catch (Exception e) {
        }finally {
        	ConnectionSingletonHelper.close(pstmtUpdateMemberLoginInfo);
		}
        return vo;
    }

    @Override
    public void myProfile(){
        sb.setLength(0);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            System.out.println("───────────────────회원정보 보기───────────────────");
            pstmtSelectMember = conn.prepareStatement(sqlSelectMember);
            pstmtSelectMember.setString(1, vo.getMember_id()); // 사용자 직접입력 X

            rs = pstmtSelectMember.executeQuery();
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
        } finally {
			ConnectionSingletonHelper.close(pstmtSelectMember);
		}
    }

    @Override
    public void editProfile(){
        SHA256 sha256 = new SHA256();
        String memberName,memberPwd,memberPhone,memberBirthday;
        try {
            System.out.println("───────────────────회원정보 수정───────────────────");
            System.out.println("회원 확인을 위해 비밀번호를 입력해주세요 ");
            System.out.print("비밀번호: "); String pwd = sha256.encrypt(br.readLine().trim());
            pstmtSelectMemberValid = conn.prepareStatement(sqlSelectMemberValid);
            pstmtSelectMemberValid.setString(1, vo.getMember_id());
            pstmtSelectMemberValid.setString(2, pwd);
            pstmtSelectMemberValid.setInt(3, 1);
            rs = pstmtSelectMemberValid.executeQuery();
            if(!rs.isBeforeFirst()) {
                System.out.println("비밀번호가 틀렸습니다.");
                return;
            }
            pstmtUpdateMember = conn.prepareStatement(sqlUpdateMember);
            pstmtUpdateMember.setString(5, vo.getMember_id()); 

            System.out.println("메뉴로 돌아가기 : q");
            System.out.println("이름은 최대 16자까지 입력이 가능합니다.");
            while(true) {
                System.out.print("이름: ");
                memberName  = br.readLine().trim();
                if(memberName.equalsIgnoreCase("q")) return;
                if (!isValid("(^[a-zA-Zㄱ-힣][a-zA-Zㄱ-힣 ]*${1,16})", memberName)) {
                    System.out.println("잘못된 이름형식 입니다.");
                    continue;
                }
                pstmtUpdateMember.setString(1, memberName);
                break;
            }
            System.out.println("최소 8 자, 하나 이상의 문자와 하나의 숫자,특수문자 !,@,$,!,%,*,#,^,?,& 를 이용하여  입력하십시오");
            while(true) {
                System.out.print("PassWord: ");
                memberPwd = br.readLine().trim();
                if(memberPwd.equalsIgnoreCase("q")) return;
                if (!isValid("^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[$`~!@$!%*#^?&\\(\\)-_=+]).{8,16}$", memberPwd)) {
                    System.out.println("잘못된 입력양식 입니다. 다시 입력하십시오");
                    continue;
                }
                pstmtUpdateMember.setString(2, sha256.encrypt(memberPwd)); 
                break;
            }

            System.out.println("형식 000-0000-0000 으로 입력해 주십시오");
            while(true) {
                System.out.print("Phone: ");
                memberPhone = br.readLine().trim();
                if(memberPhone.equalsIgnoreCase("q")) return;
                if (!isValid("^\\d{3}-\\d{4}-\\d{4}$", memberPhone)) {
                    System.out.println("잘못된 입력양식 입니다. 다시 입력하십시오");
                    continue;
                }
                pstmtUpdateMember.setString(3, memberPhone); //System.out.print("전화번호: "); 
                break;
            }

            System.out.println("주민등록번호 앞자리 6글자만 입력하십시오.");
            while(true) {
                try {
                    System.out.print("Birthday: ");
                    memberBirthday = br.readLine().trim();
                    if(memberBirthday.equalsIgnoreCase("q")) return;
                    if (!isValid("([0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[1,2][0-9]|3[0,1]))", memberBirthday)) {
                        System.out.println("잘못된 입력양식 입니다. 다시 입력하십시오");
                        continue;
                    }
                    pstmtUpdateMember.setString(4, memberBirthday); //System.out.print("생일: ");
                    break;
                }
                catch (IOException e) {
                    System.out.println("잘못된 날짜 입력입니다");
                }
            }

            int result = pstmtUpdateMember.executeUpdate();
            System.out.println(result + "개 업데이트 성공");

        } catch (SQLException e) {
        } catch (IOException e) {
        } catch (NoSuchAlgorithmException e) {
		} finally {
			ConnectionSingletonHelper.close(pstmtSelectMemberValid);
			ConnectionSingletonHelper.close(pstmtUpdateMember);
		}
    }

    @Override
    public void removeMember(LoginManager lm){
        SHA256 sha256 = new SHA256();
        try {
            System.out.println("───────────────────회원 탈퇴───────────────────");
            System.out.println("회원 확인을 위해 비밀번호를 입력해주세요 ");
            System.out.print("비밀번호: "); String pwd = sha256.encrypt(br.readLine().trim());
            pstmtSelectMemberValid = conn.prepareStatement(sqlSelectMemberValid);
            pstmtSelectMemberValid.setString(1, vo.getMember_id());
            pstmtSelectMemberValid.setString(2, pwd);
            pstmtSelectMemberValid.setInt(3, 1);
            rs = pstmtSelectMemberValid.executeQuery();
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
        } catch (IOException e) {
        } catch (NoSuchAlgorithmException e) {
		} finally {
			ConnectionSingletonHelper.close(pstmtDeleteMember);
			ConnectionSingletonHelper.close(pstmtSelectMemberValid);
		}
    }
    
    private void menu() {
        sb.setLength(0);
        sb.append("───────────────────────────────────────────────").append("\n");
        sb.append(String.format("                  회원관리        %s님",vo.getMember_name() )).append("\n");
        sb.append("───────────────────────────────────────────────").append("\n");
        sb.append("1. 회원정보 조회").append("\n");
        sb.append("2. 회원정보 수정").append("\n");
        sb.append("3. 회원탈퇴").append("\n");
        sb.append("───────────────────────────────────────────────").append("\n\n");
        sb.append("메뉴로 돌아가기 : q").append("\n");
        sb.append("입력: ");
        System.out.print(sb);
    }

    private void menu2() {
        sb.setLength(0);
        sb.append("───────────────────────────────────────────────").append("\n");
        sb.append("             영화 예매 시스템").append("\n");
        sb.append("───────────────────────────────────────────────").append("\n");
        sb.append("1. 회원가입").append("\n");
        sb.append("2. 로그인").append("\n");
        sb.append("3. 시스템 종료").append("\n");
        sb.append("───────────────────────────────────────────────").append("\n\n");
        sb.append("입력: ");
        System.out.print(sb);
    }

    public void loginMenu(LoginManager lm, IMemberService ms, ITicketService ts){
        while (true) {
            menu2();
            try {
                switch (Integer.parseInt(br.readLine().trim())) {
                case 1:
                    register();
                    break; // 회원가입
                case 2:
                    lm.loginUser(login());
                    if(lm.getLoginUser()!=null) {
                    	pstmtSearchAdmin = conn.prepareStatement(sqlSearchAdmin);
                    	pstmtSearchAdmin.setString(1, lm.getLoginUser().getMember_id());
                    	rs = pstmtSearchAdmin.executeQuery();
                    	if(rs.next()) {
                    	    lm.setIsAdmin(true);
                    	    lm.setFlag(rs.getInt(1));
                    	}
                    	return;
                    }
                    break; // 로그인
                case 3: System.out.println("시스템을 종료합니다.");
                ms.AllClose(); ts.AllClose();
                ConnectionSingletonHelper.close();
                System.exit(0);
                return;
                default: System.out.println("입력을 확인해주세요.");
                break;
                } // switch end
            } catch (Exception e) {
                System.out.println("잘못된 입력입니다.");
            } finally {
				ConnectionSingletonHelper.close(pstmtSearchAdmin);
			}
        } // while end
    }



    @Override
    public void memberMenu(LoginManager lm) {
        while (true) {
            menu();
            try {
                String str = br.readLine().trim();
                if(str.equalsIgnoreCase("q")) return;
                int select = Integer.parseInt(str);
            	switch (select) {
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
            }
            catch (Exception e) {
            	System.out.println("잘못된 입력입니다\n");
            }
        } // while end
    }

	@Override
	public void AllClose() {
		// TODO Auto-generated method stub
		
	}
}
