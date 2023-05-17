package controll;

import java.io.*;
import java.sql.*;
import java.util.*;
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
        lm.loginUser(ms.login());
        while(lm.getLoginUser()==null) {
            lm.loginUser(ms.login());
        }
        
        while(true) {
            mainMenu();
            int select = Integer.parseInt(br.readLine());
            switch (select) {
            case 1: ts.ticketing();
                break; // 예매
            case 2: ts.ticketHistory();
                break; // 예매 내역
            case 3: ms.memberMenu();
                break; // 회원정보 관리
            case 4: ms.logout();
                break; // 로그아웃
            case 0: 
                /* close(); */System.out.println("시스템을 종료합니다."); 
                System.exit(0);; // 시스템 종료
            }
        }
    }

    private void mainMenu() {
        System.out.println("=====================메인 메뉴========================");
        System.out.println("1. 예매");
        System.out.println("2. 예매 내역");
        System.out.println("3. 회원정보 관리");
        System.out.println("4. 로그아웃");
        System.out.println("0. 시스템 종료");
        System.out.println("======================================================");
        System.out.println();
    }
}
