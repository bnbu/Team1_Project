package controll;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import service.IMemberService;
import service.ITicketService;
import util.LoginManager;

public class MainController { 
    private IMemberService ms;
    private ITicketService ts;
    private BufferedReader br;
    private StringBuilder sb;
    
 
    public MainController() {
        sb = new StringBuilder();
        br = new BufferedReader(new InputStreamReader(System.in));
        try {
            ms = new MemberController();
            ts = new TicketController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void menu() throws Exception {
        // 로그인
        LoginManager lm = new LoginManager();
        while(true) {
            ms.loginMenu(lm);
            
            while(lm.getLoginUser()!=null) {
                mainMenu();
                int select = Integer.parseInt(br.readLine());
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
                case 6: lm.loginUser(ms.logout());
                    break; // 로그아웃
                }
            }
        }
    }

    private void mainMenu() {
        System.out.println("──────────────────────메인 메뉴──────────────────────");
        System.out.println("1. 상영 시간표");
        System.out.println("2. 예매");
        System.out.println("3. 예매 내역");
        System.out.println("4. 예매 취소");
        System.out.println("5. 회원정보 관리");
        System.out.println("6. 로그아웃");
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println();
        System.out.print("입력: ");
    }
}