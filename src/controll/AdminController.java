package controll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import service.IAdminService;
import util.ConnectionSingletonHelper;
import util.LoginManager;

public class AdminController implements IAdminService {
    private BufferedReader br;
    private ResultSet rs;
    private StringBuilder sb;
    private PreparedStatement pstmtTotalMovie, pstmtSearchMovie, pstmtInsertScreenInfo, pstmtSearchMovieByNo,pstmtInsertMovie,
    pstmtSerchAllBookedTicket, pstmtSearchIDTicketInfo, pstmtCancelAdmin, pstmtSearchTicketInfoByNo, pstmtSearchIDByNo, pstmtCancelValidAdmin;
    private final String sqlTotalMovie = "SELECT * FROM MOVIE",
            sqlSearchMovie = "SELECT COUNT(*) FROM MOVIE WHERE MOVIE_NO = ?",
            sqlInsertScreenInfo = "INSERT INTO SCREENING_INFO VALUES (?||?||SCREENING_INFO_SEQ.NEXTVAL, ?, ?, TO_DATE(?||' '||?, 'YYYY-MM-DD HH24:MI'), TO_DATE(?||' '||?, 'YYYY-MM-DD HH24:MI'), ?)",
            sqlSearchMovieByNo = "SELECT * FROM MOVIE WHERE MOVIE_NO = ?",
            sqlInsertMovie = "insert into movie values(movie_seq.nextval,?,?,?,?,?,?)",
            sqlAllBookedTicket = "SELECT TI.TICKET_NO, TI.SCREENINFO_NO, TI.MEMBER_ID, TI.PEOPLE, TO_CHAR(PRICE, '999,999,999')||'원', TO_CHAR(TI.TICKET_DATE, 'YYYY-MM-DD'), TO_CHAR(TI.CANCLE_DATE, 'YYYY-MM-DD'), M.MOVIE_TITLE, TI.VALID FROM (TICKETING TI INNER JOIN SCREENING_INFO SI ON TI.SCREENINFO_NO = SI.SCREENINFO_NO) INNER JOIN MOVIE M ON SI.MOVIE_NO = M.MOVIE_NO ORDER BY TI.MEMBER_ID, TI.TICKET_DATE",
            sqlSearchIDTicketInfo = "SELECT TI.TICKET_NO, TI.SCREENINFO_NO, TI.MEMBER_ID, TI.PEOPLE, TO_CHAR(PRICE, '999,999,999')||'원', TO_CHAR(TI.TICKET_DATE, 'YYYY-MM-DD'), TO_CHAR(TI.CANCLE_DATE, 'YYYY-MM-DD'), M.MOVIE_TITLE, TI.VALID, TO_CHAR(SI.MOVIE_START, 'YYYY-MM-DD HH24:MI') FROM (TICKETING TI INNER JOIN SCREENING_INFO SI ON TI.SCREENINFO_NO = SI.SCREENINFO_NO) INNER JOIN MOVIE M ON SI.MOVIE_NO = M.MOVIE_NO AND TI.MEMBER_ID = ?",
            sqlCancelAdmin = "UPDATE TICKETING SET CANCLE_DATE = SYSDATE, VALID = 0 WHERE TICKET_NO = ?",
            sqlCancelValidAdmin = "SELECT TI.TICKET_NO, TI.SCREENINFO_NO, TI.MEMBER_ID, TI.PEOPLE, TO_CHAR(PRICE, '999,999,999')||'원', TO_CHAR(TI.TICKET_DATE, 'YYYY-MM-DD'), TO_CHAR(TI.CANCLE_DATE, 'YYYY-MM-DD'), M.MOVIE_TITLE, TI.VALID FROM (TICKETING TI INNER JOIN SCREENING_INFO SI ON TI.SCREENINFO_NO = SI.SCREENINFO_NO) INNER JOIN MOVIE M ON SI.MOVIE_NO = M.MOVIE_NO AND TI.TICKET_NO = ?",
            sqlSearchTicketInfoByNo = "SELECT COUNT(*) FROM TICKETING WHERE TICKET_NO = ?",
            sqlSearchIDByNo = "SELECT COUNT(*) FROM MEMBER WHERE MEMBER_ID = ?";


    public AdminController() {
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            Connection conn = ConnectionSingletonHelper.getConnection();
            sb = new StringBuilder();
            pstmtTotalMovie = conn.prepareStatement(sqlTotalMovie);
            pstmtSearchMovie = conn.prepareStatement(sqlSearchMovie);
            pstmtInsertScreenInfo = conn.prepareStatement(sqlInsertScreenInfo);
            pstmtSearchMovieByNo = conn.prepareStatement(sqlSearchMovieByNo);
            pstmtInsertMovie = conn.prepareStatement(sqlInsertMovie);
            pstmtSerchAllBookedTicket = conn.prepareStatement(sqlAllBookedTicket);
            pstmtSearchIDTicketInfo = conn.prepareStatement(sqlSearchIDTicketInfo);
            pstmtCancelAdmin = conn.prepareStatement(sqlCancelAdmin);
            pstmtSearchTicketInfoByNo = conn.prepareStatement(sqlSearchTicketInfoByNo);
            pstmtSearchIDByNo = conn.prepareStatement(sqlSearchIDByNo);
            pstmtCancelValidAdmin = conn.prepareStatement(sqlCancelValidAdmin);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addMovie() {

        String str,title, author, genre, start;
        int length, rank;
        try {
            showMovies();
            System.out.println("메뉴로 돌아가기 : q");
            System.out.print("타이틀: "); title = br.readLine().trim(); 
            if (title.equalsIgnoreCase("q")) return;
            pstmtInsertMovie.setString(1,title); // 타이틀

            while(true) {
                try {
                    System.out.print("상영시간: "); str = br.readLine().trim(); // 
                    if (str.equalsIgnoreCase("q")) return;
                    length = Integer.parseInt(str);
                    pstmtInsertMovie.setInt(2,length);// 상영시간
                    break;
                } catch (Exception e) {
                    System.out.println("잘못된 입력입니다.");
                }
            }

            System.out.println("이름은 최대 16자까지 입력이 가능합니다.");
            while(true) {
                try {
                    System.out.print("감독: "); author = br.readLine().trim();
                    if (author.equalsIgnoreCase("q")) return;
                    if(!Pattern.matches("^[a-zA-Zㄱ-힣][a-zA-Zㄱ-힣 ]{0,16}$", author)) {
                        System.out.println("잘못된 입력입니다.");
                        continue;
                    }
                    pstmtInsertMovie.setString(3,author); // 감독
                    break;
                } catch (Exception e) {
                    System.out.println("잘못된 입력입니다.");
                }
            }

            System.out.println("장르는 한글로 입력해주세요.");
            while(true) {
                try {
                    System.out.print("장르: "); genre = br.readLine().trim(); 
                    if (genre.equalsIgnoreCase("q")) return;
                    pstmtInsertMovie.setString(4,genre); // 장르
                    if(!Pattern.matches("^[가-힣]*$", genre)) {
                        System.out.println("잘못된 입력입니다.");
                        continue;
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("잘못된 입력입니다.");
                }
            }

            System.out.println("전연령)0  12세)1  15세)2  18세)3");
            while(true) {
                try {
                    System.out.print("상영등급: "); str = br.readLine().trim(); //
                    if (str.equalsIgnoreCase("q")) return;
                    rank = Integer.parseInt(str);
                    pstmtInsertMovie.setInt(5,rank); // 상영등급
                    break;
                } catch (Exception e) {
                    System.out.println("잘못된 입력입니다.");
                }
            }

            System.out.print("년/월/일 순으로 날짜를 입력");
            while (true) {
                try {
                    System.out.println("상영일");
                    start = br.readLine();
                    if (start.equalsIgnoreCase("q")) return;
                    if (!Pattern.matches("[0-9]{4}/[0-9]{2}/[0-9]{2}", start)) {
                        System.out.println("잘못된 입력입니다");
                        continue;
                    }
                    pstmtInsertMovie.setString(6,start); // 개봉일
                    break;
                }
                catch (Exception e) {
                    System.out.println("잘못된 입력입니다.");
                }
            }

            pstmtInsertMovie.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("잘못된 입력입니다 다시 확인해주세요.");
        }

    }   

    private String getRankString(int i) {
        if (i == 0) return "전체";
        else if (i == 1) return "12세";
        else if (i == 2) return "15세";
        else return "18세";
    }

    private void showMovies() {
        try {
            rs = pstmtTotalMovie.executeQuery();
            System.out.println("────────┬────────────┬────────────┬───────────────────────────────────────────────┐");
            System.out.println(String.format("%-6s│%-10s│%-10s│%-45s│",
                    "번호", "장르", "연령", "제목"));
            System.out.println("────────┼────────────┼────────────┼───────────────────────────────────────────────┤");
            Pattern pattern = Pattern.compile("[ㄱ-힣]");
            while ( rs.next() ) {
                Matcher matcher = pattern.matcher(rs.getString(2));
                int cnt = 0;
                while (matcher.find()) cnt++;

                int len1 = 12 - rs.getString(5).length(),
                        len2 = 16 - getRankString(rs.getInt(6)).getBytes().length,
                        len3 = 47 - cnt;

                System.out.println(String.format("%-8s│%-" + len1 + "s│%-" + len2 + "s│%-" + len3 + "s│",
                        rs.getString(1), rs.getString(5), getRankString(rs.getInt(6)), rs.getString(2)));
            }
            System.out.println("────────┴────────────┴────────────┴───────────────────────────────────────────────┘");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String timeCalc(String start) {
        StringBuilder ret = new StringBuilder();
        try {
            String[] s = start.split(":");
            int h = Integer.parseInt(s[0]),
                    m = Integer.parseInt(s[1]);

            int movieLength = rs.getInt(3);
            h += movieLength / 60;
            m += movieLength % 60;
            h += m / 60;
            m %= 60;
            h %= 24;

            return ret.append(String.format("%02d:%02d", h, m)).toString();
        }
        catch (Exception e) {
            return "";
        }
    }
    @Override
    public void addScreeningInfo() {
        showMovies();

        try {
            String str, date, start, end;
            int movieNum = -1;
            System.out.println("메뉴로 돌아가기 : q");
            while (true) {
                try {
                    System.out.print("영화의 번호를 입력 : ");
                    str = br.readLine();
                    if (str.equalsIgnoreCase("q")) return;
                    movieNum = Integer.parseInt(str);

                    pstmtSearchMovie.setInt(1, movieNum);
                    rs = pstmtSearchMovie.executeQuery();
                    rs.next();
                    if (rs.getInt(1) < 1) {
                        System.out.println("존재하지 않는 영화입니다, 다시 확인해주세요\n");
                        continue;
                    }
                    pstmtInsertScreenInfo.setInt(1, movieNum);
                    pstmtInsertScreenInfo.setInt(3, movieNum);

                    pstmtSearchMovieByNo.setInt(1, movieNum);
                    rs = pstmtSearchMovieByNo.executeQuery();
                    rs.next();
                    break;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("잘못된 입력입니다\n");
                }
            }

            int theaterNum = -1;
            while (true) {
                try {
                    System.out.print("상영관 번호를 입력: ");
                    str = br.readLine();
                    if (str.equalsIgnoreCase("q")) return;
                    theaterNum = Integer.parseInt(str);
                    if (theaterNum >= 5) {
                        System.out.println("없는 상영관입니다");
                        continue;
                    }
                    pstmtInsertScreenInfo.setInt(4, theaterNum);
                    break;
                }
                catch (Exception e) {
                    System.out.println("잘못된 입력입니다");
                }
            }

            while (true) {
                try {
                    System.out.print("년/월/일 순으로 날짜를 입력: ");
                    date = br.readLine();
                    if (str.equalsIgnoreCase("q")) return;
                    if (!Pattern.matches("[0-9]{4}/[0-9]{2}/[0-9]{2}", date)) {
                        System.out.println("잘못된 입력입니다");
                        continue;
                    }
                    pstmtInsertScreenInfo.setString(5, date);
                    pstmtInsertScreenInfo.setString(7, date);
                    pstmtInsertScreenInfo.setString(9, date);
                    break;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            while (true) {
                try {
                    System.out.print("(시작시간 종료시간) 순으로 입력 ex) 10:00 : ");
                    start = br.readLine();
                    if (start.equalsIgnoreCase("q")) return;
                    if (!Pattern.matches("[0-9]{1,2}:[0-9]{2}", start)) {
                        System.out.println("잘못된 입력입니다");
                        continue;
                    }
                    end = timeCalc(start);
                    pstmtInsertScreenInfo.setString(6, start);
                    pstmtInsertScreenInfo.setString(8, end);
                    pstmtInsertScreenInfo.setString(2, start.replace(":", ""));
                    break;
                }
                catch (Exception e) {
                    System.out.println("잘못된 입력입니다");
                }
            }

            try {
                System.out.println("───────────────────────────────────────────────┬────────────┬────────────┬────────────┐");
                System.out.println(String.format("%-45s│%-10s│%-10s│%-10s│", "제목", "날짜", "시작", "종료"));
                System.out.println("───────────────────────────────────────────────┼────────────┼────────────┼────────────┤");

                Pattern pattern = Pattern.compile("[ㄱ-힣]");
                Matcher matcher = pattern.matcher(rs.getString(2));
                int cnt = 0;
                while (matcher.find()) cnt++;
                int len = 47 - cnt;
                System.out.println(String.format("%-" + len + "s│%-12s│%-12s│%-12s│", rs.getString(2), date, start, end));
                System.out.println("───────────────────────────────────────────────┴────────────┴────────────┴────────────┘");
                System.out.print("추가하시겠습니까? y/n: ");
                if (br.readLine().equalsIgnoreCase("y")) {
                    pstmtInsertScreenInfo.executeUpdate();
                    System.out.println("추가에 성공했습니다");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("추가에 실패");
        }
    }


    @Override
    public void sales() {

    }

    /*
	//		1. 전체 예매 내역 조회
	try {
		sb = new StringBuilder();
		rs = pstmtSerchAllBookedTicket.executeQuery();

		sb.append("──────────┬──────────┬────────────────────────┬───────────────────────────────────────────────┬────────┬─────────────┬──────────────────┬──────────────────┐").append("\n");
		sb.append(String.format("%-6s│%-6s│%-20s│%-45s│%-6s│%-10s│%-14s│%-14s│\n","예매번호", "상영번호", "아이디", "제목", "인원", "가격","예매 날짜", "취소 날짜")).append("\n");
		sb.append("──────────┼──────────┼────────────────────────┼───────────────────────────────────────────────┼────────┼─────────────┼──────────────────┼──────────────────┤").append("\n");
		Pattern pattern = Pattern.compile("[ㄱ-힣]");
		while ( rs.next() ) {
			Matcher matcher = pattern.matcher(rs.getString(8));
			int cnt = 0;
			while ( matcher.find() ) cnt++;

			int len = 47 - cnt;
			sb.append(String.format("%-10s│%-10s│%-23s│%-" + len + "s│%-8s│%-12s│%-18s│%-18s\n",rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(8), rs.getString(4), rs.getString(5).trim(), rs.getString(6), rs.getString(7))).append("\n");
		};
		sb.append("──────────┴──────────┴────────────────────────┴───────────────────────────────────────────────┴────────┴─────────────┴──────────────────┘");
		System.out.println(sb);
	} catch (SQLException e) {
		e.printStackTrace();
	}

     */

    @Override
    public void cancel() {
        //		2. 해당 아이디의 예매 내역 조회
        String UserID = null;
        try {

            System.out.println("메뉴로 돌아가기 : q");
            br = new BufferedReader(new InputStreamReader(System.in));
            while ( true ) {
                sb = new StringBuilder();
                System.out.print("조회할 아이디를 입력해 주세요 : ");
                UserID = br.readLine();

                if ( UserID.equals("q") ) return;

                try {
                    pstmtSearchIDByNo.setString(1, UserID);
                    rs = pstmtSearchIDByNo.executeQuery();
                    rs.next();

                    if ( rs.getInt(1) == 0 ) {
                        System.out.println("조회할 수 없습니다.");
                        continue;
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                try {
                    pstmtSearchIDTicketInfo.setString(1, UserID);
                    rs = pstmtSearchIDTicketInfo.executeQuery();

                    sb.append("──────────┬──────────┬────────────────────────┬───────────────────────────────────────────────┬────────┬─────────────┬──────────────────┬──────────────────┬──────────────────┐").append("\n");
                    sb.append(String.format("%-6s│%-6s│%-21s│%-45s│%-6s│%-11s│%-14s│%-14s│%-14s│\n","예매번호", "상영번호", "아이디", "제목", "인원", "가격","상영 날짜", "예매 날짜", "취소 날짜"));
                    sb.append("──────────┼──────────┼────────────────────────┼───────────────────────────────────────────────┼────────┼─────────────┼──────────────────┼──────────────────┼──────────────────┤").append("\n");
                    Pattern pattern = Pattern.compile("[ㄱ-힣]");
                    while( rs.next() ) {
                        Matcher matcher = pattern.matcher(rs.getString(8));
                        int cnt = 0;
                        while ( matcher.find() ) cnt++;

                        int len = 47 - cnt;
                        switch ( rs.getInt(9) ) {
                        case 0:
                            sb.append(String.format("%-10s│%-10s│%-24s│%-" + len + "s│%-8s│%-12s│%-18s│%-18s│%-18s│\n",rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(8), rs.getString(4), rs.getString(5).trim(), rs.getString(10), rs.getString(6), rs.getString(7)));
                            break;
                        case 1:
                            sb.append(String.format("%-10s│%-10s│%-24s│%-" + len + "s│%-8s│%-12s│%-18s│%-18s│                  │\n",rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(8), rs.getString(4), rs.getString(5).trim(), rs.getString(10), rs.getString(6), rs.getString(7)));
                            break;
                        }
                    }
                    sb.append("──────────┴──────────┴────────────────────────┴───────────────────────────────────────────────┴────────┴─────────────┴──────────────────┴──────────────────┴──────────────────┘");
                    System.out.println(sb);

                    System.out.println("메뉴로 돌아가기 : q");
                    System.out.print("계속 조회하시려면 0, 예매 취소 하시려면 1을 입력해주세요. : ");
                    String num = br.readLine();
                    if ( num.equals("0") ) {
                        continue;
                    } else if ( num.equals("1") ) {
                        break;
                    } else if ( num.equalsIgnoreCase("q") ) {
                        return;
                    } else throw new Exception();

                } catch (Exception e) {
                    System.out.println("잘못된 입력입니다.");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //		3. 예매 취소

        String TicketNo = null;
        try {
            System.out.println("메뉴로 돌아가기 : q");
            while ( true ) {
                try {
                    System.out.print("취소할 예매 번호를 입력해주세요 : ");
                    TicketNo = br.readLine();

                    if ( TicketNo.equals("q") ) return;

                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                pstmtSearchTicketInfoByNo.setString(1, TicketNo);
                rs = pstmtSearchTicketInfoByNo.executeQuery();
                rs.next();

                if ( rs.getInt(1) == 0 ) {
                    System.out.println("입력하신 예매번호를 조회할수 없습니다. 다시 한번 확인해주시길 바랍니다.");
                    continue;
                }

                pstmtCancelValidAdmin.setString(1, TicketNo);
                rs = pstmtCancelValidAdmin.executeQuery();
                rs.next();

                if ( rs.getInt(9) == 0 ) {
                    System.out.println("취소된 표입니다.");
                    continue;
                }

                pstmtCancelAdmin.setString(1, TicketNo);
                pstmtCancelAdmin.executeUpdate();
                rs.next();
                break;

            } 
            System.out.println("예약번호 " + TicketNo + " 표가 취소 되었습니다.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void menu(LoginManager lm) {
        String str;
        while(true) {
            
            try {
                System.out.println("───────────────────────────────────────────────");
                System.out.printf("                관리자 메뉴        %s님\n", lm.getLoginUser().getMember_name());
                System.out.println("───────────────────────────────────────────────");
                if ((lm.getFlag() & 1) > 0)
                    System.out.println("1. 매출");
                if ((lm.getFlag() & 1 << 1) > 0)
                    System.out.println("2. 영화추가");
                if ((lm.getFlag() & 1 << 2) > 0)
                    System.out.println("3. 상영정보추가");
                if ((lm.getFlag() & 1 << 3) > 0)
                    System.out.println("4. 예매 취소");
                System.out.println("───────────────────────────────────────────────");
                System.out.println("메뉴로 돌아가기 : q");
                System.out.print("입력: ");
                str = br.readLine();
                if(str.equalsIgnoreCase("q")) return;
                int select = Integer.parseInt(str);
                switch (select) {
                case 1:
                    if ((lm.getFlag() & 1) == 0) throw new Exception();
                    addMovie();
                    break;
                case 2:
                    if ((lm.getFlag() & 1 << 1) == 0) throw new Exception();
                    addScreeningInfo();
                    break;
                case 3:
                    if ((lm.getFlag() & 1 << 2) == 0) throw new Exception();
                    sales();
                    break;
                case 4:
                    if ((lm.getFlag() & 1 << 3) == 0) throw new Exception();
                    cancel();
                    break;
                default:
                    break;
                }
            } catch (Exception e) {
                System.out.println("잘못된입력입니다.");
            }
        }
    }
}
