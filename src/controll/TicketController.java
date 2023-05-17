package controll;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.regex.Pattern;

import service.ITicketService;

public class TicketController implements ITicketService {
	private BufferedReader br;
	private PreparedStatement pstmtTotalMovie, pstmtTotalScreeningInfo, pstmtInsertTicket, pstmtCancelTicket,
	pstmtSearchTicketInfo, pstmtSearchSeatInfo, pstmtSearchScreeningInfo, pstmtSearchTheaterInfo, pstmtSearchTicketInfoByNo,
	pstmtSearchValidTicketInfo, pstmtSearchScreeiningByNo, pstmtInsertSeat, pstmtSearchTicketDesc;
	private final String sqlTotalMovie = "SELECT * FROM MOVIE",
			sqlTotalScreeningInfo = "SELECT SI.SCREENINFO_NO, M.MOVIE_TITLE, TO_CHAR(SI.SCREEN_DATE, 'YYYY-MM-DD'), SI.THEATER_NO, TO_CHAR(SI.MOVIE_START, 'HH24:MI'), TO_CHAR(SI.MOVIE_END, 'HH24:MI') FROM SCREENING_INFO SI, MOVIE M WHERE SI.MOVIE_NO = M.MOVIE_NO ORDER BY M.MOVIE_TITLE, SI.SCREEN_DATE, SI.MOVIE_START",
			sqlCancelTicket = "UPDATE TICKETING SET CANCLE_DATE = SYSDATE, VALID = 0 WHERE TICKET_NO = ?",
			sqlSearchTicketInfo = "SELECT TI.TICKET_NO, TI.SCREENINFO_NO, TI.PEOPLE, TO_CHAR(PRICE, '999,999,999')||'원', TO_CHAR(TI.TICKET_DATE, 'YYYY-MM-DD'), TO_CHAR(TI.CANCLE_DATE, 'YYYY-MM-DD'), M.MOVIE_TITLE, TI.VALID FROM (TICKETING TI INNER JOIN SCREENING_INFO SI ON TI.SCREENINFO_NO = SI.SCREENINFO_NO) INNER JOIN MOVIE M ON SI.MOVIE_NO = M.MOVIE_NO AND TI.MEMBER_ID = ?",
			sqlInsertTicket = "INSERT INTO TICKETING VALUES (TO_CHAR(SYSDATE, 'YYMMDD')||?||TICKETING_SEQ.NEXTVAL, ?, ?, ?, ?, ?, NULL, 1)",
			sqlSearchSeatInfo = "SELECT * FROM SEAT_INFO SI JOIN TICKETING T ON SI.TICKET_NO = T.TICKET_NO WHERE T.VALID = 1 AND SI.SCREENINFO_NO = ?",
			sqlSearchScreeningInfo = "SELECT SCREENINFO_NO, MOVIE_NO, THEATER_NO, TO_CHAR(MOVIE_START, 'HH24:MI'), TO_CHAR(MOVIE_END, 'HH24:MI'), TO_CHAR(SCREEN_DATE, 'YYYY-MM-DD') FROM SCREENING_INFO WHERE MOVIE_NO = ?",
			sqlSearchTheaterInfo = "SELECT SEAT_ROW, SEAT_COL FROM THEATER WHERE THEATER_NO = ?",
			sqlSearchValidTicketInfo = "SELECT TI.TICKET_NO, TI.SCREENINFO_NO, TI.PEOPLE, TO_CHAR(PRICE,'999,999,999')||'원', TO_CHAR(TI.TICKET_DATE, 'YYYY-MM-DD'), M.MOVIE_TITLE FROM (TICKETING TI INNER JOIN SCREENING_INFO SI ON TI.SCREENINFO_NO = SI.SCREENINFO_NO) INNER JOIN MOVIE M ON SI.MOVIE_NO = M.MOVIE_NO AND TI.MEMBER_ID = ? AND VALID = 1",
			sqlSearchTicketInfoByNo = "SELECT COUNT(*) FROM TICKETING WHERE TICKET_NO = ?",
			sqlSearchSreeningByNo = "SELECT * FROM SCREENING_INFO WHERE SCREENINFO_NO = ?",
			sqlInsertSeat = "INSERT INTO SEAT_INFO VALUES (?, ?, ?)",
			sqlSearchTicketDesc = "SELECT * FROM TICKETING WHERE MEMBER_ID = ? ORDER BY TICKET_DATE DESC";
			
	private ResultSet rs;
	private ResultSetMetaData rsmd ;
	public TicketController(Connection conn, BufferedReader br) {
		this.br = br;

		try {
			pstmtTotalMovie = conn.prepareStatement(sqlTotalMovie);
			pstmtTotalScreeningInfo = conn.prepareStatement(sqlTotalScreeningInfo);
			pstmtInsertTicket = conn.prepareStatement(sqlInsertTicket);
			pstmtCancelTicket = conn.prepareStatement(sqlCancelTicket);
			pstmtSearchTicketInfo = conn.prepareStatement(sqlSearchTicketInfo);
			pstmtSearchSeatInfo = conn.prepareStatement(sqlSearchSeatInfo);
			pstmtSearchScreeningInfo = conn.prepareStatement(sqlSearchScreeningInfo);
			pstmtSearchTheaterInfo = conn.prepareStatement(sqlSearchTheaterInfo);
			pstmtSearchValidTicketInfo = conn.prepareStatement(sqlSearchValidTicketInfo);
			pstmtSearchTicketInfoByNo = conn.prepareStatement(sqlSearchTicketInfoByNo);
			pstmtSearchScreeiningByNo = conn.prepareStatement(sqlSearchSreeningByNo);
			pstmtInsertSeat = conn.prepareStatement(sqlInsertSeat);
			pstmtSearchTicketDesc = conn.prepareStatement(sqlSearchTicketDesc);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getRankString(int i) {
		if (i == 0) return "전체";
		else if (i == 1) return "12세";
		else if (i == 2) return "15세";
		else return "18세";
	}
	public void showMovies() {
		try {
			rs = pstmtTotalMovie.executeQuery();

			System.out.println(String.format("%-6s│%-10s│%-10s│%-30s",
					"번호", "장르", "연령", "제목"));
			System.out.println("────────┼────────────┼────────────┼────────────────────────────────────────────────");
			while (rs.next()) {
				int len1 = 12 - rs.getString(5).length();
				int len2 = 16 - getRankString(rs.getInt(6)).getBytes().length;
				System.out.println(String.format("%-8s│%-" + len1 + "s│%-" + len2 + "s│%-30s",
						rs.getString(1), rs.getString(5), getRankString(rs.getInt(6)), rs.getString(2)));
			}
			System.out.println("────────┴────────────┴────────────┴────────────────────────────────────────────────");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private boolean[][] showSeat(int num) {
		ResultSet theater = null;
		int rows = 0, cols = 0;
		try {
			pstmtSearchTheaterInfo.setInt(1, num);
			theater = pstmtSearchTheaterInfo.executeQuery();
			theater.next();
			rows = theater.getInt(1);
			cols = theater.getInt(2);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		boolean[][] chk = new boolean[rows][cols];
//		for (int i = 0; i < 10; i++) chk[(int)(Math.random() * rows)][(int)(Math.random() * cols)] = true;
		try {
			while (rs.next()) {
				String str = rs.getString(1).toUpperCase();
				chk[str.charAt(0) - 'A'][Integer.parseInt(str.substring(1)) - 1] = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		System.out.print("───");
		for (int i = 0; i < cols / 5; i++) {
			System.out.print("┬────────────────────");
		}
		System.out.println("────┐");
		System.out.print("     ");
		for (int i = 1; i <= cols; i++) {
			System.out.printf("%-4d", i);
			if (i % 5 == 0) System.out.print("  ");
		}
		System.out.println("│");
		// 열 번호도 출력하기
		for (int i = 0; i < rows; i++) {
			System.out.print((char)('A' + i) + "    ");
			for (int j = 0; j < cols; j++) {
				System.out.printf("%-4s", (chk[i][j] ? "■" : "□"));
				if ((j + 1) % 5 == 0) System.out.print("  ");
			}
			System.out.println("│");
		}
		System.out.print("───");
		for (int i = 0; i < cols / 5; i++)
			System.out.print("┴────────────────────");
		System.out.println("────┘");
		
		return chk;
	}

	public void ticketing(String id) {
		showMovies();

		// 0. 영화를 선택
		String str;
		int idx = -1;

		while (true) {
			try {
				System.out.print("영화의 번호를 입력 : ");
				idx = Integer.parseInt(br.readLine());

				pstmtSearchScreeningInfo.setInt(1, idx);
				rs = pstmtSearchScreeningInfo.executeQuery();
				break;
			}
			catch (Exception e) {
				System.out.println("잘못된 입력입니다\n");
			}
		}
		System.out.println();
		
		// 1.선택한 영화로 상영정보를 가져온다
		try {
			System.out.println(String.format("%-8s│%-4s│%-20s│%-10s│%-10s", 
					"번호", "상영관", "날짜", "시작", "종료"));
			System.out.println("──────────┼───────┼──────────────────────┼────────────┼────────────────────────────");
			while (rs.next()) {
				System.out.println(String.format("%-10s│%-7s│%-22s│%-12s│%-12s",
						rs.getString(1), rs.getString(3), rs.getString(6), rs.getString(4), rs.getString(5)));
			}
			System.out.println("──────────┴───────┴──────────────────────┴────────────┴────────────────────────────");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 2. 상영정보를 선택
		while (true) {
			try {
				System.out.print("상영 정보의 번호를 입력 : ");
				str = br.readLine();
				pstmtSearchScreeiningByNo.setString(1, str);
				rs = pstmtSearchScreeiningByNo.executeQuery();
				
				if (!rs.next()) {
					System.out.println("없는 상영 정보입니다, 다시 확인해주세요\n");
					continue; 
				}
				
				
				pstmtSearchSeatInfo.setString(1, str);
				rs = pstmtSearchScreeningInfo.executeQuery();
				break;
			}
			catch (Exception e) {
				System.out.println("잘못된 입력입니다\n");
			}
		}
		System.out.println();

		// 3. 인원수 (청소년, 일반 등등)을 결정
		// 0 청소년 1 일반 2 우대
		int[] age = new int[3];
		int total = 0;
		while (true) {
			try {
				total = 0;
				System.out.print("(청소년, 일반, 우대) 순으로 입력해주세요 : ");
				StringTokenizer st = new StringTokenizer(br.readLine());
				for (int i = 0; i < 3; i++) {
					age[i] = Integer.parseInt(st.nextToken());
					total += age[i];
				}
				if (total > 0) break;
			}
			catch (Exception e) {
				System.out.println("잘못된 입력입니다\n");
			}
		}
		
		// 4. 상영정보번호로 존재하는 좌석을 가져와서, 좌석의 여부를 알려줌
		int theaterNum = -1;
		try {
			pstmtSearchScreeiningByNo.setString(1, str);
			rs = pstmtSearchScreeiningByNo.executeQuery();
			rs.next();
			theaterNum = rs.getInt(3); 
			pstmtSearchSeatInfo.setString(1, str);
			rs = pstmtSearchSeatInfo.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// 		=> 좌석정보의 에매 번호로부터 join을 통해 vaild로 좌석 계산에 포함할지 말지 계산
		boolean[][] seat = showSeat(theaterNum);


		// 5. 좌석을 인원수만큼 선택
		String[] selected = new String[total];
		while (true) {
			try {
				System.out.println("좌석은 (열행)순으로 입력해주세요 ex) A1 D15");
				System.out.printf("%d개의 좌석을 선택해주세요 : ", total);
				
				// 좌석 입력 후 올바른 좌석형식인지 검사
				StringTokenizer st = new StringTokenizer(br.readLine());
				boolean isClear = true;
				
				for (int i = 0; i < total; i++) {
					selected[i] = st.nextToken().toUpperCase();
					isClear &= Pattern.matches("[a-zA-Z][0-9]+", selected[i]);
				}
				if (!isClear) {
					System.out.println("올바르지 않은 형식입니다, 다시 선택해주세요\n");
					continue;
				}
				
				for (String s : selected) 
					isClear &= !seat[s.charAt(0) - 'A'][Integer.parseInt(s.substring(1)) - 1];
				if (!isClear) {
					System.out.println("이미 선택된 좌석이 있습니다, 다시 선택해주세요\n");
					continue;
				}
				break;
			}
			catch (Exception e) {
				System.out.println("잘못된 입력입니다\n");
			}
		}
		// 6. 예매 완료
		try {
			pstmtInsertTicket.setInt(1, theaterNum);
			pstmtInsertTicket.setString(2, id);
			pstmtInsertTicket.setString(3, str);
			pstmtInsertTicket.setInt(4, total);
			pstmtInsertTicket.setInt(5, age[0]*12000 + age[1]*14000 + age[2]*5000);
			pstmtInsertTicket.setDate(6, (java.sql.Date) new Date(System.currentTimeMillis()));
			pstmtInsertTicket.executeQuery();
			
			pstmtSearchTicketDesc.setString(1, id);
			rs = pstmtSearchTicketDesc.executeQuery();
			rs.next();
			String ticket = rs.getString(1);
			
			for (String s : selected) {
				pstmtInsertSeat.setString(1, s);
				pstmtInsertSeat.setString(2, str);
				pstmtInsertSeat.setString(3, ticket);
				pstmtInsertSeat.executeUpdate();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("예매 완료\n");
	}

	/*
	1 string 예매번호
	2 string 아이디
	3 string 상여정보번호
	4 number 예매인원
	5 number 예매 총 가격
	6 date 예매 날짜
	7 date 예매 취소 날짜
	8 number 유효
	 */

	public void ticketHistory( String ID ) {
		// 1. 유저 ID로 예매 정보를 불러옴

		try {
			pstmtSearchTicketInfo.setString(1, ID);
			rs = pstmtSearchTicketInfo.executeQuery();
			rsmd = rs.getMetaData(); // 해당 테이블에 대한 정보
			
			System.out.println("──────────┬──────────┬────────┬─────────────┬────────────┬────────────┬────────────────────────────────────────────────");
			System.out.printf("%-6s│%-6s│%-6s│%-11s│%-8s│%-8s│%-60s\n","예매번호", "상영번호", "인원", "가격","예매 날짜", "취소 날짜", "제목");
			System.out.println("──────────┼──────────┼────────┼─────────────┼────────────┼────────────┼────────────────────────────────────────────────");

			while( rs.next() ) {
				int valid = rs.getInt(8);
				switch ( valid  ) {
				case 0: // 취소
					System.out.printf("%-10s│%-10s│%-8s│%-12s│%-12s│%-12s│%-60s\n",rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4).trim(), rs.getString(5), rs.getString(6), rs.getString(7));
					break;
				case 1: //
					System.out.printf("%-10s│%-10s│%-8s│%-12s│%-12s│            │%-60s\n",rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4).trim(), rs.getString(5), rs.getString(7));
					break;
				} // end switch
			} System.out.println("──────────┴──────────┴────────┴─────────────┴────────────┴────────────┴────────────────────────────────────────────────");
			// end while
		} catch (Exception e) { e.printStackTrace(); }
	} // end ticketHistory

	public void ticketingCancel(String ID) throws IOException {
		// 1. 유저 ID로 예매 정보 불러옴
		try {
			pstmtSearchValidTicketInfo.setString(1, ID);
			rs = pstmtSearchValidTicketInfo.executeQuery();
			rsmd = rs.getMetaData(); // 해당 테이블에 대한 정보
			
			System.out.println("──────────┬──────────┬────────┬─────────────┬────────────┬────────────────────────────────────────────────");
			System.out.printf("%-6s│%-6s│%-6s│%-11s│%-8s│%-60s\n","예매번호", "상영번호", "인원", "가격", "예매 날짜", "제목");
			System.out.println("──────────┼──────────┼────────┼─────────────┼────────────┼────────────────────────────────────────────────");

			while( rs.next() ) {
				
				System.out.printf("%-10s│%-10s│%-8s│%-12s│%-12s│%-60s\n",rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4).trim(), rs.getString(5), rs.getString(6));
			
			};
			System.out.println("──────────┴──────────┴────────┴─────────────┴────────────┴────────────────────────────────────────────────");

		} catch (SQLException e) { e.printStackTrace(); }
		// 2. 취소하고자 하는 예매 정보를 선택
		// 3. 해당 예매 정보의 valid를 제거
		String TicketNo = null;
		try {
			do {
				try {
					System.out.print("취소할 예매번호를 입력해주세요 : ");
					TicketNo = br.readLine();			

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
			

				pstmtCancelTicket.setString(1, TicketNo);
				pstmtCancelTicket.executeUpdate();
				break;

			} while( true );
			System.out.println("취소되었습니다.");
		} catch (SQLException e) { e.printStackTrace(); }
	}

	public void showScreens() {
		// 1. 상영정보를 불러옴
		try {
			rs = pstmtTotalScreeningInfo.executeQuery();
			rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();

			System.out.println("──────────┬──────────┬───────────┬──────────┬──────────┬────────────────────────────────────────────────");
			System.out.printf("%-8s│%-6s│%-6s│%-8s│%-8s│%-60s\n","번호", "상영날짜", "상영관번호", "시작", "종료", "제목");
			System.out.print("──────────┼──────────┼───────────┼──────────┼──────────┼────────────────────────────────────────────────");

			System.out.println();
			while ( rs.next() ) {

				System.out.printf("%-10s│%-10s│%-11s│%-10s│%-10s│%-60s\n",rs.getString(1), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(2));
			}
			System.out.println("──────────┴──────────┴───────────┴──────────┴──────────┴────────────────────────────────────────────────");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

