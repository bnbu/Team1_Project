package controll;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import service.AdminServiceImp;
import service.IAdminService;
import service.IMemberService;
import service.ITicketService;
import service.MemberServiceImp;
import service.TicketServiceImp;
import util.LoginManager;

public class MainController { 
    private IMemberService ms;
    private ITicketService ts;
    private IAdminService as;
    private BufferedReader br;
    private LoginManager lm;

    public MainController() {
        br = new BufferedReader(new InputStreamReader(System.in));
        try {
            ms = new MemberServiceImp();
            ts = new TicketServiceImp();
            as = new AdminServiceImp();
            lm = null;
        } catch (Exception e) {
            
        }
    }

    public void menu() throws Exception {
        // 로그인
        lm = new LoginManager();
        while(true) {
        	String str = null;
        	ms.loginMenu(lm, ms, ts);
        	while(lm.getLoginUser()!=null) {
        		mainMenu(lm.getLoginUser().getMember_name());
        		try {
        			str = br.readLine();
        			int select = Integer.parseInt(str);
        			switch (select) {
        			case 1: ts.showScreens();
        			break; // 상영정보조회
        			case 2: ts.ticketing(lm.getLoginUser().getMember_id());
        			break; // 예매
        			case 3: ts.ticketHistory(lm.getLoginUser().getMember_id());
        			break; // 예매 내역
        			case 4: ts.ticketingCancel(lm.getLoginUser().getMember_id());
        			break; // 예매 취소
        			case 5: ms.memberMenu(lm);
        			break; // 회원정보 관리
        			case 6: lm.loginUser(ms.logout(lm));
        			break; // 로그아웃
        			default: System.out.println("입력을 확인해주세요");
        			break;
        			}
        		}
        		catch (Exception e) {
        			if (lm.getIsAdmin() && str.equals("#")) as.menu(lm);
        			else System.out.println("잘못된 입력입니다");
        		}
            }
        }
    }

    private void mainMenu(String name) {
        System.out.println("───────────────────────────────────────────────");
        System.out.printf("                  메인 메뉴        %s님\n",name);
        System.out.println("───────────────────────────────────────────────");
        System.out.println("1. 상영 시간표");
        System.out.println("2. 예매");
        System.out.println("3. 예매 내역");
        System.out.println("4. 예매 취소");
        System.out.println("5. 회원정보 관리");
        System.out.println("6. 로그아웃");
        if (lm.getIsAdmin()) System.out.println("#. 관리자 메뉴");
        System.out.println("───────────────────────────────────────────────");
        System.out.println();
        System.out.print("입력: ");
    }
}