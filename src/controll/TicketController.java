package controll;

import java.io.*;
import java.sql.*;
import java.util.*;
import service.ITicketService;

public class TicketController implements ITicketService {
	private BufferedReader br;
	private PreparedStatement pstmtTotalMovie, pstmtTotalScreeningInfo, pstmtInsertTicket, pstmtCancelTicket,
							  pstmtSearchTicketInfo, pstmtSearchSeatInfo, pstmtSearchScreeningInfo, pstmtSearchTheaterInfo,
                pstmtSearchValidTicketInfo;
	private final String sqlTotalMovie = "SELECT * FROM MOVIE",
						 sqlTotalScreeningInfo = "SELECT * FROM SCREENING_INFO",
						 sqlInsertTicket = "INSERT INTO TICKETING VALUES (?, ?, ?, ?, ?, ?, ?, 1)",
						 sqlCancelTicket = "UPDATE TICKETING SET VALID = 0 WHERE TICKETN_NO = ?",
						 sqlSearchTicketInfo = "SELECT * FROM TICKETING WHERE MEMBER_BNO = ?",
						 sqlSearchSeatInfo = "SELECT * FROM  SEAT_INFO SI JOIN TICKETING T ON SI.TICKET_NO = T.TICKET_NO WHERE T.VALID = 1 AND SCREENINFO_NO = ?",
						 sqlSearchScreeningInfo = "SELECT * FROM SCREENING_INFO WHERE MOVIE_NO = ?",
						 sqlSearchTheaterInfo = "SELECT SEAT_ROW, SEAT_COL FROM THEATER WHERE THEATER_NO = ?",
             sqlSearchValidTicketInfo = "SELECT * FROM TICKETING WHERE MEMBER_BNO = ? AND VALID = 1";
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
			ResultSetMetaData rsmd = rs.getMetaData();
			
			
			System.out.println(String.format("%-6s|%-12s|%-10s|%-30s",
					"번호", "장르", "등급", "제목"));
			System.out.println("-------+----------------------------------------------------------");
			while (rs.next()) {
				int len = 12 - (rs.getString(5).length() - 1) / 3;
				System.out.println(String.format("%-7s|%-" + len + "s|%-10s|%-30s",
						rs.getString(1), rs.getString(5), getRankString(rs.getInt(6)), rs.getString(2)));
			}
			System.out.println("-------+----------------------------------------------------------");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void showSeat(ResultSet rs) {
		ResultSet seats = rs;
		ResultSet theater = null;
		int rows = -1, cols = -1;
		try {
			theater = pstmtSearchTheaterInfo.executeQuery();
			rows = theater.getInt(1);
			cols = theater.getInt(2);
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		boolean[][] chk = new boolean[rows][cols];
		try {
			while (seats.next()) {
				String str = seats.getString(1).toUpperCase();
				chk[str.charAt(0) - 'A'][Integer.parseInt(str.substring(1))] = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("----------------------------------------------------------");
		// 열 번호도 출력하기
		for (int i = 0; i < rows; i++) {
			System.out.print((char)('A' + i) + ' ');
			for (int j = 0; j < cols; j++) {
				System.out.print(chk[i][j] ? "■" : "□");
				if ((j + 1) % 5 == 0) System.out.print(" ");
			}
			System.out.println('\n');
		}
		System.out.println("----------------------------------------------------------");
	}

	public void ticketing() {
		showMovies();
		
		// 0. 영화를 선택
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
				System.out.println("잘못된 입력입니다");
			}
		}
		
		// 1.선택한 영화로 상영정보를 가져온다
		try {
			System.out.println(String.format("%-6s|%-6s|%-20s|%-10s|%-10s", 
					"번호", "상영관", "날짜", "시작", "종료"));
			System.out.println("----------------------------------------------------------");
			while (rs.next()) {
				System.out.println(String.format("%-6s|%-6s|%-20s|%-10s|%-10s",
						rs.getString(1), rs.getString(3), rs.getString(6), rs.getString(4), rs.getShort(5)));
			}
			System.out.println("----------------------------------------------------------");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 2. 상영정보를 선택
		while (true) {
			try {
				System.out.print("상영 정보의 번호를 입력 : ");
				idx = Integer.parseInt(br.readLine());
				
				pstmtSearchSeatInfo.setInt(1, idx);
				rs = pstmtSearchScreeningInfo.executeQuery();
				break;
			}
			catch (Exception e) {
				System.out.println("잘못된 입력입니다");
			}
		}
		
		// 3. 인원수 (청소년, 일반 등등)을 결정
		// 0 청소년 1 일반 2 우대
		int[] age = new int[3];
		int total = 0;
		while (true) {
			try {
				System.out.println("(청소년, 일반, 우대) 순으로 입력해주세요");
				StringTokenizer st = new StringTokenizer(br.readLine());
				for (int i = 0; i < 3; i++) {
					age[i] = Integer.parseInt(st.nextToken());
					total += age[i];
				}
				break;
			}
			catch (Exception e) {
				System.out.println("잘못된 입력입니다");
			}
		}
		
		// 4. 상영정보번호로 존재하는 좌석을 가져와서, 좌석의 여부를 알려줌
		showSeat(rs);
		// 		=> 좌석정보의 에매 번호로부터 join을 통해 vaild로 좌석 계산에 포함할지 말지 계산
		
		
		// 5. 좌석을 인원수만큼 선택
		String[] selected = new String[total];
		while (true) {
			try {
				StringTokenizer st = new StringTokenizer(br.readLine());
				for (int i = 0; i < total; i++) selected[i] = st.nextToken();
				// 패턴매칭으로 유효한 좌석형식인지 검사하고 후에 좌석이 앉을 수 있는 좌석인지도 체크하기
				break;
			}
			catch (Exception e) {
				System.out.println("잘못된 입력입니다");
			}
		}
		
		// 6. 예매 완료
		System.out.println("예매 완료");
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

			while( rs.next() ) {
					int valid = rs.getInt(8);
					switch ( valid  ) {
					case 0: // 취소
						System.out.print(rsmd.getColumnName(1) + " : " + rs.getString(1) + " ");
						System.out.print(rsmd.getColumnName(3) + " : " + rs.getString(3) + " ");
						System.out.print(rsmd.getColumnName(4) + " : " + rs.getInt(4) + " ");
						System.out.print(rsmd.getColumnName(5) + " : " + rs.getInt(5) + " ");
						System.out.print(rsmd.getColumnName(6) + " : " + rs.getDate(6) + " ");
						System.out.print(rsmd.getColumnName(7) + " : " + rs.getDate(7) + " ");
						break;
					case 1: // 
						System.out.print(rsmd.getColumnName(1) + " : " + rs.getString(1) + " ");
						System.out.print(rsmd.getColumnName(3) + " : " + rs.getString(3) + " ");
						System.out.print(rsmd.getColumnName(4) + " : " + rs.getInt(4) + " ");
						System.out.print(rsmd.getColumnName(5) + " : " + rs.getInt(5) + " ");
						System.out.print(rsmd.getColumnName(6) + " : " + rs.getDate(6) + " ");
						break;
					} // end switch
			} // end while
		} catch (Exception e) { e.printStackTrace(); }
	} // end ticketHistory

	public void ticketingCancel(String ID) throws IOException {
		// 1. 유저 ID로 예매 정보 불러옴
		try {
			pstmtSearchValidTicketInfo.setString(1, ID);
			rs = pstmtSearchValidTicketInfo.executeQuery();
			rsmd = rs.getMetaData(); // 해당 테이블에 대한 정보
			
			while( rs.next() ) {
				System.out.print(rsmd.getColumnName(1) + " : " + rs.getString(1) + " ");
				System.out.print(rsmd.getColumnName(3) + " : " + rs.getString(3) + " ");
				System.out.print(rsmd.getColumnName(4) + " : " + rs.getInt(4) + " ");
				System.out.print(rsmd.getColumnName(5) + " : " + rs.getInt(5) + " ");
				System.out.print(rsmd.getColumnName(6) + " : " + rs.getDate(6) + " ");
			};
			
		} catch (SQLException e) { e.printStackTrace(); }
		// 2. 취소하고자 하는 예매 정보를 선택
		// 3. 해당 예매 정보의 valid를 제거
		try {
			String TicketNo = br.readLine();
			pstmtCancelTicket.setString(1, TicketNo);
			rs = pstmtCancelTicket.executeQuery();
			rsmd = rs.getMetaData();
			
			while( rs.next() ) {
				System.out.print(rsmd.getColumnName(1) + " : " + rs.getString(1) + " ");
				System.out.print(rsmd.getColumnName(3) + " : " + rs.getString(3) + " ");
				System.out.print(rsmd.getColumnName(4) + " : " + rs.getInt(4) + " ");
				System.out.print(rsmd.getColumnName(5) + " : " + rs.getInt(5) + " ");
				System.out.print(rsmd.getColumnName(6) + " : " + rs.getDate(6) + " ");
				System.out.print(rsmd.getColumnName(7) + " : " + rs.getDate(7) + " ");
			};
			
		} catch (SQLException e) { e.printStackTrace(); }
	}

	public void showScreens() {
		// 1. 상영정보를 불러옴
	}
	
}
