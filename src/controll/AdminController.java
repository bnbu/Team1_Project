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

public class AdminController implements IAdminService {
	private static Connection conn;
	private ResultSet rs;
	private BufferedReader br;
	private StringBuilder sb;
	private PreparedStatement pstmtSerchAllBookedTicket, pstmtSearchIDTicketInfo, pstmtCancelAdmin, pstmtSearchTicketInfoByNo, pstmtSearchIDByNo, pstmtCancelValidAdmin;

	private final String sqlAllBookedTicket = "SELECT TI.TICKET_NO, TI.SCREENINFO_NO, TI.MEMBER_ID, TI.PEOPLE, TO_CHAR(PRICE, '999,999,999')||'원', TO_CHAR(TI.TICKET_DATE, 'YYYY-MM-DD'), TO_CHAR(TI.CANCLE_DATE, 'YYYY-MM-DD'), M.MOVIE_TITLE, TI.VALID FROM (TICKETING TI INNER JOIN SCREENING_INFO SI ON TI.SCREENINFO_NO = SI.SCREENINFO_NO) INNER JOIN MOVIE M ON SI.MOVIE_NO = M.MOVIE_NO ORDER BY TI.MEMBER_ID, TI.TICKET_DATE",
			sqlSearchIDTicketInfo = "SELECT TI.TICKET_NO, TI.SCREENINFO_NO, TI.MEMBER_ID, TI.PEOPLE, TO_CHAR(PRICE, '999,999,999')||'원', TO_CHAR(TI.TICKET_DATE, 'YYYY-MM-DD'), TO_CHAR(TI.CANCLE_DATE, 'YYYY-MM-DD'), M.MOVIE_TITLE, TI.VALID, TO_CHAR(SI.MOVIE_START, 'YYYY-MM-DD HH24:MI') FROM (TICKETING TI INNER JOIN SCREENING_INFO SI ON TI.SCREENINFO_NO = SI.SCREENINFO_NO) INNER JOIN MOVIE M ON SI.MOVIE_NO = M.MOVIE_NO AND TI.MEMBER_ID = ?",
			sqlCancelAdmin = "UPDATE TICKETING SET CANCLE_DATE = SYSDATE, VALID = 0 WHERE TICKET_NO = ?",
			sqlCancelValidAdmin = "SELECT TI.TICKET_NO, TI.SCREENINFO_NO, TI.MEMBER_ID, TI.PEOPLE, TO_CHAR(PRICE, '999,999,999')||'원', TO_CHAR(TI.TICKET_DATE, 'YYYY-MM-DD'), TO_CHAR(TI.CANCLE_DATE, 'YYYY-MM-DD'), M.MOVIE_TITLE, TI.VALID FROM (TICKETING TI INNER JOIN SCREENING_INFO SI ON TI.SCREENINFO_NO = SI.SCREENINFO_NO) INNER JOIN MOVIE M ON SI.MOVIE_NO = M.MOVIE_NO AND TI.TICKET_NO = ?",
			sqlSearchTicketInfoByNo = "SELECT COUNT(*) FROM TICKETING WHERE TICKET_NO = ?",
			sqlSearchIDByNo = "SELECT COUNT(*) FROM MEMBER WHERE MEMBER_ID = ?";


	public AdminController() throws Exception {
		conn = ConnectionSingletonHelper.getConnection();
		try {
			pstmtSerchAllBookedTicket = conn.prepareStatement(sqlAllBookedTicket);
			pstmtSearchIDTicketInfo = conn.prepareStatement(sqlSearchIDTicketInfo);
			pstmtCancelAdmin = conn.prepareStatement(sqlCancelAdmin);
			pstmtSearchTicketInfoByNo = conn.prepareStatement(sqlSearchTicketInfoByNo);
			pstmtSearchIDByNo = conn.prepareStatement(sqlSearchIDByNo);
			pstmtCancelValidAdmin = conn.prepareStatement(sqlCancelValidAdmin);


		} catch (Exception e) {e.printStackTrace();
		}
	}

	@Override
	public void addMovie() {

	}

	@Override
	public void addScreeningInfo() {

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
	public void menu() {
		cancel();
	}

}
